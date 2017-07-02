package com.hudson.donglingmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.NetMusicDownloadInfoBean;
import com.hudson.donglingmusic.db.HistoryMusicDao;
import com.hudson.donglingmusic.db.MusicDao;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.db.SystemMusicOpenHelper;
import com.hudson.donglingmusic.global.MyApplication;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.net.download.DownloadHelper;
import com.hudson.donglingmusic.net.download.MusicDownloadManager;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.fragment.MusicMenuFragment;
import com.hudson.donglingmusic.ui.itempager.HistoryPlayItemPager;
import com.hudson.donglingmusic.ui.itempager.LocalItemPager;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.MusicUtils;
import com.hudson.donglingmusic.utils.PinYinUtil;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mMediaPlayer;
    //不使用enum,原因在于aidl不支持该类型
    public static final int MODE_LIST_LOOP = 1;//列表循环
    public static final int MODE_LIST_ORDER = 2;//列表顺序
    public static final int MODE_ONE_LOOP = 3;//单曲循环
    public static final int MODE_LIST_SHUFFLE = 4;//列表随机

    public static final int STATE_PLAYING = 5;//正在播放
    public static final int STATE_PAUSE = 6;//暂停
    public static final int STATE_IDLE = 7;//空闲
    public static final int STATE_PREPARED = 8;//准备状态

    public static final String ACTION_PLAY = "com.hudson.donglingmusic.play";
    public static final String ACTION_PAUSE = "com.hudson.donglingmusic.pause";
    public static final String ACTION_NEW_MUSIC = "com.hudson.donglingmusic.newmusic";
    public static final String ACTION_STOP = "com.hudson.donglingmusic.stop";
    public static final String ACTION_PLAY_MODE_CHANGE = "com.hudson.donglingmusic.mode_change";

    private int mPlayMode = MODE_LIST_ORDER;
    private int mPlayState = STATE_IDLE;

    private ArrayList<MusicInfo> mPlayList;//当前播放列表
    private int mCurPlayIndex = -1;

    //通知栏
    private Notification mNotification;
    private RemoteViews mRemoteViews;
    private static final int NOTIFICATION_ID = 0x01;
    private NotificationManager mNotificationManager;

    //广播接受者，接收内容有：外部的控件，RemoteViews远程控制播放，例如通知栏与桌面控件
    public static final String REMOTE_PLAY_PAUSE = "com.hudson.donglingmusic.remote_play_pause";
    public static final String REMOTE_PRE = "com.hudson.donglingmusic.remote_pre";
    public static final String REMOTE_NEXT = "com.hudson.donglingmusic.remote_next";
    public static final String LOCAL_PLAYING_INDEX_CHANGED = "com.hudson.donglingmusic.local_playing_index_change";
    public static final String PLAY_BY_OPENING_LOCAL_PATH = "com.hudson.donglingmusic.play_by_opening_local_path";
    public static final String PLAY_LIST_CHANGE = "com.hudson.donglingmusic.playlist_change";
    public static final String UPDATE_HISTORY_DATABASE_COMPLETE = "update_history_database_complete";
    public static final String NEXT_PLAY_LIST_SET_BROADCAST = "com.hudson.donglingmusic.next_music_list_set";
    private RemoteControlReceiver mReceiver;

    private boolean[] mRandomFlags;//注意！！！！！！！这个大小需要随着mplaylist的大小变化而变化
    private MySharePreferences mInstance;
    private int mExitSongCount = -1;

    public MusicService() {
        mMediaPlayer = new MediaPlayer();
        mPlayList = new ArrayList<>();
    }


    @Override
    public IBinder onBind(Intent intent) {
        mReceiver = new RemoteControlReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(REMOTE_NEXT);
        filter.addAction(REMOTE_PRE);
        filter.addAction(REMOTE_PLAY_PAUSE);
        filter.addAction(DialogUtils.EXIT_SONG_COUNT_BROADCAST);
        filter.addAction(MusicDownloadManager.NEW_DOWNLOAD_MUSIC);
        filter.addAction(WidgetUpdateService.ACTION_REMOTE_PLAY_MODE_CHANGE);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);//耳机拔下来，手机变得noisy
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);//来电事件监听
        filter.addAction(MusicMenuFragment.NO_CHANGE_PLAY_LIST);
        registerReceiver(mReceiver,filter);
        mInstance = MySharePreferences.getInstance();
        mInstance.saveNextPlayIndex(-1);//重置,不能开启app还播放上次使用保存的下一曲
        checkLastExitSong();
        return new ServiceInnerControl();
    }

    /**
     * 检查上次退出时的歌曲
     */
    private void checkLastExitSong() {
        if(mInstance.getExitSongInfoOrNot()){
            MusicInfo musicInfo = mInstance.getExitSong();
            if(musicInfo!=null){
                if(musicInfo.getData().startsWith("http")){//播放历史记录
                    mInstance.savePlayerPlaylistInfo(HistoryPlayItemPager.HISTORY_LIST_INFO);
                    ArrayList<MusicInfo> musicInfos = HistoryMusicDao.getInstance(this).queryAll();
                    if(musicInfos==null|(musicInfo!=null&&musicInfos.size()==0)){
                        return ;
                    }else{
                        setPlayList(musicInfos);
                    }
                }else{//播放本地列表
                    mInstance.savePlayerPlaylistInfo(LocalItemPager.LOCAL_LIST_INFO);
                    //因为播放的是本地音乐，切换回本地列表
                    setPlayList(MyApplication.getMyApplication().getAllList());
                    //通知本地列表，当前播放的是他自己
                    sendBroadcast(new Intent(PLAY_BY_OPENING_LOCAL_PATH));
                }
                preparedButNotPlay(musicInfo);
            }
        }
    }

    /**
     * 检查是否需要保存歌曲信息
     */
    private void checkSaveSongInfoOrNot(){
        if(mInstance.getExitSongInfoOrNot()){
            mInstance.saveExitSong(getCurMusicInfo());
            if(mInstance.getExitSongProgressOrNot()){
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if(currentPosition == mMediaPlayer.getDuration()){
                    currentPosition = 0;
                }
                mInstance.saveLastExitSongProgress(currentPosition);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mReceiver);
        return super.onUnbind(intent);
    }


    /**
     * 音乐播放器准备
     * @param position playList的index
     * @return
     */
    public void preparedAndPlay(final int position) {
        if(mPlayList == null|(mPlayList!=null&&mPlayList.size() == 0)){
            ToastUtils.showToast("当前没有播放列表被选中！");
            return ;
        }
        int nextPlayIndex = mInstance.getNextPlayIndex();
        if(nextPlayIndex != -1){
            mInstance.saveNextPlayIndex(-1);//重置
            if(mInstance.getNextPlayChangeList()){
                //如果歌单是本地、下载完成、历史记录这三个，下一曲将会切换播放列表，其他只会往原有列表末尾添加歌曲
                String nextPlayListInfo = mInstance.getNextPlayListInfo();
                if(nextPlayListInfo.equals(mInstance.getPlayerPlaylistInfo())){
                    //如果当前的歌单就是下一曲需要播放的歌单
                    preparedAndPlay(nextPlayIndex);
                }else{//下一曲的歌单并不是当前的歌单，所以我们需要修改playList
                    Intent intent = new Intent(NEXT_PLAY_LIST_SET_BROADCAST);
                    intent.putExtra("listInfo",nextPlayListInfo);
                    intent.putExtra("nextPlayIndex",nextPlayIndex);
                    sendBroadcast(intent);//发送广播，等待列表控制播放器播放
                }
            }else{
                preparedAndPlay(mPlayList.size()-1);//我们会在末尾添加上我们的下一曲
            }
        }else{
            mMediaPlayer.reset();
            String path = mPlayList.get(position).getData();
            try {
                if(path.startsWith("http")){
                    //获取歌曲的地址
                    MusicInfo netMusic = mPlayList.get(position);
                    //检测本地是否存在，如果存在不再访问网络
                    String localPath = detectLocalExistThisFileOrNot(netMusic.getTitle());
                    if(localPath == null){
                        initMusicDownloadPicsAndLyrics(netMusic,
                                BMA.Song.songInfo(String.valueOf(netMusic.getSongId())),netMusic.getTitle());
                    }else{
                        mMediaPlayer.setDataSource(localPath);
                        mMediaPlayer.prepare();
                    }
                }else{
                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.prepare();
                }
                mPlayState = STATE_PREPARED;
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mCurPlayIndex = position;
                        mMediaPlayer.start();
                        sendBroadcast(new Intent(ACTION_NEW_MUSIC));
                        mPlayState = STATE_PLAYING;
                        updateNotification();
                        //用于桌面控件获取信息
                        mInstance.savePlayerIsPlaying(true);
                        MusicInfo musicInfo = mPlayList.get(mCurPlayIndex);
                        updateHistoryPlayListDatabase(musicInfo);
                        mInstance.saveCurMusicTitle(musicInfo.getTitle());
                        mInstance.saveCurMusicAlbumId(musicInfo.getAlbumId());
                    }
                });
                mMediaPlayer.setOnCompletionListener(this);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast("播放出错了！");
                stop();
            }
        }
    }

    private boolean isFirstStartPlayLastSong = false;
    /**
     * 准备但不播放，仅用于记录上次退出的歌曲
     * @param musicInfo
     */
    private void preparedButNotPlay(MusicInfo musicInfo){
        mMediaPlayer.reset();
        String path = musicInfo.getData();
        final int position = MusicUtils.getMusicInfoIndexByData(path,mPlayList);
        try {
            if(path.startsWith("http")){
                //获取歌曲的地址
                MusicInfo netMusic = mPlayList.get(position);
                //检测本地是否存在，如果存在不再访问网络
                String localPath = detectLocalExistThisFileOrNot(netMusic.getTitle());
                if(localPath == null){
                    initMusicDownloadPicsAndLyrics(netMusic,
                            BMA.Song.songInfo(String.valueOf(netMusic.getSongId())),netMusic.getTitle());
                }else{
                    mMediaPlayer.setDataSource(localPath);
                    mMediaPlayer.prepare();
                }
            }else{
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
            }
            mPlayState = STATE_PREPARED;
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mCurPlayIndex = position;
                    mPlayState = STATE_PAUSE;
                    if(mInstance.getExitSongProgressOrNot()){
                        mMediaPlayer.seekTo(mInstance.getLastExitSongProgress());
                    }
                    isFirstStartPlayLastSong = true;
                    sendBroadcast(new Intent(ACTION_NEW_MUSIC));
                    //用于桌面控件获取信息
                    MusicInfo musicInfo = mPlayList.get(mCurPlayIndex);
                    mInstance.saveCurMusicTitle(musicInfo.getTitle());
                    mInstance.saveCurMusicAlbumId(musicInfo.getAlbumId());
                }
            });
            mMediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToast("播放出错了！");
            stop();
        }
    }

    /**
     * 检测本地是否存在该歌曲
     * @param title
     * @return 本地该歌曲的路径
     */
    private String detectLocalExistThisFileOrNot(String title){
        String localPathByMusicInfoTitle = DownloadHelper.getLocalPathByMusicInfoTitle(title);
        if(new File(localPathByMusicInfoTitle).exists()){
            return localPathByMusicInfoTitle;
        }
        return null;
    }

    /**
     * 更新最近播放记录
     * 最多仅保存100条记录
     * 本方法需要完成的工作：如果播放列表就是历史记录，我们需要同时修改播放列表数据
     *
     */
    private void updateHistoryPlayListDatabase(final MusicInfo musicInfo){
        new Thread(){
            @Override
            public void run(){
                HistoryMusicDao historyMusicDao = HistoryMusicDao.getInstance(MusicService.this);
                ArrayList<MusicInfo> historyList = historyMusicDao.queryAll();
                if(historyList == null){
                    historyList = new ArrayList<>();
                }
                int size = historyList.size();
                if(size >=99){
                    //超出大小限制，删除第一个
                    historyMusicDao.deleteItem(historyList.get(0));
                }
                historyMusicDao.deleteItem(musicInfo);//删除所有与musicInfo一致的歌曲
                historyMusicDao.insert(musicInfo);//重新添加歌曲到尾部（读取时反过来）
                MusicService.this.sendBroadcast(new Intent(UPDATE_HISTORY_DATABASE_COMPLETE));

                //检测一下当前播放的是否是历史记录的歌单，如果是，我们需要将播放列表更新
                if(mInstance.getPlayerPlaylistInfo().equals(HistoryPlayItemPager.HISTORY_LIST_INFO)){
                    MusicInfo music;
                    for (int i = 0; i < historyList.size(); i++) {
                        music = historyList.get(i);
                        if(music.equals(musicInfo)){
                            historyList.remove(i);
                            break;
                        }
                    }
                    historyList.add(musicInfo);
                    ArrayList<MusicInfo> contraryList = new ArrayList<>();
                    for (int i = historyList.size()-1; i >=0 ; i--) {
                        contraryList.add(historyList.get(i));
                    }
                    try {
// TODO: 2017/6/18 注意：这里存在安全隐患，原因在于如果其他处理不及时，那么很容易出问题，有解决思路是
// TODO：我们可以在切换新歌曲的时候，我们在把playList改变。（如果遇到问题，可以试试）
                        Thread.sleep(300);//等其他事件处理完成
                        mPlayList = contraryList;
                        mCurPlayIndex = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 网络音乐
     * 解析歌曲的下载地址、歌曲背景下载链接、歌曲的歌词下载链接，并下载
     * @param url
     */
    private void initMusicDownloadPicsAndLyrics(final MusicInfo netMusic, final String url, final String title) {
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        processBillBoardData(netMusic,responseInfo.result,title);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ToastUtils.showToast("数据加载失败，请检查网络是否可用！");
                    }
                });
    }

    /**
     * 解析榜单数据
     */
    private void processBillBoardData(MusicInfo netMusic,String jsonResult,String title) {
        Gson gson = new Gson();
        try{
            NetMusicDownloadInfoBean bean = gson.fromJson(jsonResult,NetMusicDownloadInfoBean.class);
            if(bean!=null){
                NetMusicDownloadInfoBean.SonginfoBean songinfo = bean.getSonginfo();
                netMusic.setAlbum(songinfo.getAlbum_title());
                netMusic.setAlbumId(Integer.valueOf(songinfo.getAlbum_id()));
                HttpUtils httpUtils = XUtilsManager.getHttpUtilsInstance();
                String imagePath = new StringBuilder(StorageUtils.
                        getAppMusicPicAbsolutePath()).append(title).append(".jpg").toString();
                if(!new File(imagePath).exists()){
                    //500*500图片大小
                    String imageUrl = songinfo.getPic_premium();
                    if(TextUtils.isEmpty(imageUrl)){//如果这类图片没有
                        imageUrl = songinfo.getPic_big();
                    }
                    httpUtils.download(imageUrl, imagePath, false, false, new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            System.out.println("歌曲图片下载成功");
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("歌曲图片下载失败");
                        }
                    });
                }
                //歌词下载路径
                String lrcPath = new StringBuilder(StorageUtils.
                        getAppLyricsAbsolutePath()).append(title).append(".lrc").toString();
                if(!new File(lrcPath).exists())
                httpUtils.download(songinfo.getLrclink(), lrcPath, false, false, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        System.out.println("歌曲歌词下载成功");
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        System.out.println("歌曲歌词下载失败");
                    }
                });
                //歌曲下载路径
                List<NetMusicDownloadInfoBean.SongurlBean.UrlBean> urlBeanList = bean.getSongurl().getUrl();
                if(urlBeanList!=null&&urlBeanList.size()!=0){
                    for(NetMusicDownloadInfoBean.SongurlBean.UrlBean urlBean:urlBeanList){
                        if(urlBean.getFile_bitrate() == mInstance.getDownloadMusicType()){//标准
                            String file_link = urlBean.getFile_link();
                            if(!TextUtils.isEmpty(file_link)){
                                mMediaPlayer.setDataSource(file_link);//开始播放该网络歌曲
                                netMusic.setData(file_link);
                                mMediaPlayer.prepareAsync();
                            }
                        }
                    }
                    if(netMusic.getData().equals("http??")){//能走到这里，说明没有播放，随便选一个播放
                        for(NetMusicDownloadInfoBean.SongurlBean.UrlBean urlBean:urlBeanList){
                            String file_link = urlBean.getFile_link();
                            if(!TextUtils.isEmpty(file_link)){
                                mMediaPlayer.setDataSource(file_link);//开始播放该网络歌曲
                                netMusic.setData(file_link);
                                mMediaPlayer.prepareAsync();
                            }
                        }
                    }
                }
            }else{
                ToastUtils.showToast("加载失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int nextIndex = mCurPlayIndex;
        switch (mPlayMode){
            case MODE_LIST_LOOP:
                nextIndex = mCurPlayIndex +1;
                break;
            case MODE_LIST_ORDER:
                if(mCurPlayIndex == mPlayList.size()-1){
                    stop();
                    return ;
                }
                nextIndex = mCurPlayIndex +1;
                break;
            case MODE_LIST_SHUFFLE:
                nextIndex = getShuffleSongId();
                break;
//            case MODE_ONE_LOOP:
//                nextIndex = mCurPlayIndex;
//                break;
        }
        if(mExitSongCount!=-1){
            mExitSongCount --;
            mInstance.saveExitAtTimeSongCount(mExitSongCount);
            if(mExitSongCount == 0){
                sendBroadcast(new Intent(DialogUtils.EXIT_BROADCAST));
                return ;//不要继续播放了,直接终止
            }
        }
        preparedAndPlay(getValidMusicIndex(nextIndex));
    }

    private void stop() {
        sendBroadcast(new Intent(ACTION_STOP));
        mMediaPlayer.stop();
        mPlayState = STATE_IDLE;
        mCurPlayIndex = -1;
        updateNotificationPlayPause();
        mInstance.savePlayerIsPlaying(false);
    }

    private int getValidMusicIndex(int position){
        if(position<0){
            return 0;
        }else if(position>mPlayList.size()-1){
            if(mPlayMode == MODE_LIST_LOOP){
                return 0;
            }
            return mPlayList.size()-1;
        }else{
            return position;
        }
    }

    //通知栏(一旦播放，我们只显示一个通知（在init中new通知），如果需要每首歌曲都显示，可以在update中new通知)
    private void initNotification(){
        mNotification = new Notification();
        mNotification.icon = R.drawable.icon_player;
        mNotification.tickerText = getString(R.string.notification_create_tip);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 指定通知栏转到的activity是HomeActivity
        Intent intent = new Intent(UIUtils.getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mNotification.contentIntent = PendingIntent.getActivity(UIUtils.getContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);// 第二次操作会把第一次的覆盖掉
        //解析自定义的通知栏布局
        mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
        mNotification.contentView = mRemoteViews;
        // 此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值，这点非常重要！！！！
        Intent pauseIntent = new Intent();
        pauseIntent.setAction(REMOTE_PLAY_PAUSE);
        //点击就会发送广播
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notification_play_pause,pausePIntent);
        Intent nextIntent = new Intent();
        nextIntent.setAction(REMOTE_NEXT);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notification_next,nextPIntent);
    }

    /**
     * 更新通知栏
     */
    private void updateNotification(){
        if(mNotification==null){
            initNotification();
        }
        MusicInfo curMusicInfo = mPlayList.get(mCurPlayIndex);
        mRemoteViews.setTextViewText(R.id.tv_notification_title, curMusicInfo.getTitle());
        mRemoteViews.setTextViewText(R.id.tv_notification_info, curMusicInfo.getMusicSingerInfo());
        //这里注意，我们的通知栏高度固定是60dp的
        Bitmap singerBg = BitmapMusicUtils.getArtwork(curMusicInfo.getTitle(),curMusicInfo.getAlbumId(),
                dp2px(60, this), dp2px(60, this));
        if(singerBg!=null){
            mRemoteViews.setImageViewBitmap(R.id.iv_notification_icon,
                    singerBg);
        }else{
            mRemoteViews.setImageViewResource(R.id.iv_notification_icon,R.drawable.icon_player);
        }
        updateNotificationPlayPause();
    }

    /**
     * 修改通知栏的播放暂停图片
     */
    private void updateNotificationPlayPause(){
        if(mNotification!=null){
            if(mPlayState == STATE_PLAYING){
                mRemoteViews.setImageViewResource(R.id.iv_notification_play_pause,
                        R.drawable.notification_pause);
            }else{
                mRemoteViews.setImageViewResource(R.id.iv_notification_play_pause,
                        R.drawable.notification_play);
            }
            //必须执行这个后才会更新到通知栏
            startForeground(NOTIFICATION_ID, mNotification);
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        mNotificationManager.cancel(NOTIFICATION_ID);
        stopSelf();
        super.onDestroy();
    }

    private void pause(){
        if(mPlayState == STATE_PLAYING){
            mMediaPlayer.pause();
            mPlayState = STATE_PAUSE;
            sendBroadcast(new Intent(ACTION_PAUSE));
            updateNotificationPlayPause();
            mInstance.savePlayerIsPlaying(false);
        }
    }

    private void play(int position){
        if(position != mCurPlayIndex){
            preparedAndPlay(getValidMusicIndex(position));
        }else if(mPlayState == STATE_PAUSE){
            mMediaPlayer.start();
            mPlayState = STATE_PLAYING;
            sendBroadcast(new Intent(ACTION_PLAY));
            updateNotificationPlayPause();
            mInstance.savePlayerIsPlaying(true);
            if(isFirstStartPlayLastSong){
                updateNotification();
                if(mPlayMode == MODE_LIST_SHUFFLE){
                    if(mCurPlayIndex!=-1){
                        mRandomFlags[mCurPlayIndex] = true;
                    }
                }
                isFirstStartPlayLastSong = false;
            }
        }else if(mPlayState == STATE_PLAYING&&position == mCurPlayIndex){
            pause();
        }
    }

    private int getShuffleSongId(){
        if(mPlayList != null && mPlayList.size() != 0){
            Random random = new Random();
            int result = random.nextInt(mPlayList.size());
            if(mRandomFlags[result]){//这个已经产生过了
                checkShuffleListValid();
                return getShuffleSongId();
            }else{
                mRandomFlags[result] = true;
                return result;
            }
        }
        return 0;
    }

    /**
     * 查看随机数是否产生了一整遍
     */
    private void checkShuffleListValid(){
        for (int i = 0; i < mRandomFlags.length; i++) {
            if(mRandomFlags[i]){
                continue;
            }else{//说明还有未产生的随机数
                return ;
            }
        }
        //重置
        for (int i = 0; i < mRandomFlags.length; i++) {
            mRandomFlags[i] = false;
        }
    }

    private void setPlayMode(int mode){
        mPlayMode = mode;
        if(mode == MODE_LIST_SHUFFLE){
            if(mCurPlayIndex!=-1){
                mRandomFlags[mCurPlayIndex] = true;
            }
        }
        mInstance.savePlayMode(mode);//保存当前的播放模式信息
        sendBroadcast(new Intent(ACTION_PLAY_MODE_CHANGE));//播放模式变化了
    }

    class ServiceInnerControl extends IDonglingMusicAidlInterface.Stub{

        @Override
        public void pause() throws RemoteException {
            MusicService.this.pause();
        }

        @Override
        public void play(int position) throws RemoteException {
            MusicService.this.play(position);
        }

        @Override
        public void playByPath(String path) throws RemoteException {
            if(path.startsWith("http")){//如果是网络资源

            }else{
                if(!mInstance.getPlayerPlaylistInfo().equals(LocalItemPager.LOCAL_LIST_INFO)){
                    mInstance.savePlayerPlaylistInfo(LocalItemPager.LOCAL_LIST_INFO);
                    //因为播放的是本地音乐，切换回本地列表
                    setPlayList(MyApplication.getMyApplication().getAllList());
                    //通知本地列表，当前播放的是他自己
                    sendBroadcast(new Intent(PLAY_BY_OPENING_LOCAL_PATH));
                }
                int destIndex = MusicUtils.getMusicInfoIndexByData(path, mPlayList);
                if(destIndex ==-1){
                    updateLocalDataBaseMusic(path);
                    destIndex = MusicUtils.getMusicInfoIndexByData(path, mPlayList);
                }
                play(destIndex);
            }
        }

        @Override
        public void stop() throws RemoteException {
            MusicService.this.stop();
        }

        @Override
        public void pre() throws RemoteException {
            if(mPlayMode == MODE_LIST_SHUFFLE){
                preparedAndPlay(getShuffleSongId());
            }else{
                preparedAndPlay(getValidMusicIndex(mCurPlayIndex-1));
            }
        }

        @Override
        public void next() throws RemoteException {
            if(mPlayMode == MODE_LIST_SHUFFLE){
                preparedAndPlay(getShuffleSongId());
            }else{
                preparedAndPlay(getValidMusicIndex(mCurPlayIndex+1));
            }
        }

        @Override
        public void removeSongFromPlayList(int position) throws RemoteException {
            mPlayList.remove(position);
            if(position == mCurPlayIndex){
                int size = mPlayList.size();
                if(size == 0){
                    stop();
                }else{
                    mCurPlayIndex = (position>size-1)?size-1:position;
                    preparedAndPlay(getValidMusicIndex(mCurPlayIndex));
                }
            }else{
                if(position<mCurPlayIndex){
                    mCurPlayIndex --;
                }
            }
        }

        @Override
        public void removeSongFromPlayListByPath(String path) throws RemoteException {

        }

        @Override
        public int getDuration() throws RemoteException {
            if(mMediaPlayer!=null&&mPlayState!=STATE_IDLE){
                return mMediaPlayer.getDuration();
            }
            return -1;
        }

        @Override
        public int getCurPosition() throws RemoteException {
            if(mMediaPlayer!=null){
                return mMediaPlayer.getCurrentPosition();
            }
            return -1;
        }

        @Override
        public int getCurMusicInfoIndex() throws RemoteException {
            return mCurPlayIndex;
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            if(mPlayState!=STATE_IDLE)
            mMediaPlayer.seekTo(position);
        }

        @Override
        public MusicInfo getCurMusicInfo() throws RemoteException {
            return MusicService.this.getCurMusicInfo();
        }

        @Override
        public List<MusicInfo> getPlayList() throws RemoteException {
            return mPlayList;
        }

        @Override
        public void setPlayList(List<MusicInfo> playList) throws RemoteException {
            MusicService.this.setPlayList(playList);
        }

        @Override
        public int getPlayState() throws RemoteException {
            return mPlayState;
        }

        @Override
        public boolean isPlaying() throws RemoteException {
//            return mMediaPlayer.isPlaying();
            return mPlayState==STATE_PLAYING;
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return mPlayMode;
        }

        @Override
        public void setPlayState(int state) throws RemoteException {
            mPlayState = state;
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            MusicService.this.setPlayMode(mode);
        }

        @Override
        public void exit() throws RemoteException {
            checkSaveSongInfoOrNot();
            if(mPlayState!=STATE_IDLE){
                MusicService.this.stop();
            }
            if(mMediaPlayer!=null){
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if(mPlayList!=null){
                mPlayList.removeAll(mPlayList);
                mPlayList = null;
            }
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            if(mMediaPlayer!=null){
                return mMediaPlayer.getAudioSessionId();
            }
            return -1;
        }
    }

    /**
     * 很棘手的问题，如果我们取消list的关联性（如本地列表与播放列表），那么
     * 在本地页面点击了item，会调用play(position)这样position与播放列表
     * 不对应，播放结果肯定不对。但是如果存在list关联性，那么我们对播放列表
     * 进行歌曲删除操作，那么假设是本地列表，本地列表也会跟着变，这又不好。
     * 后续解决：利用sp保存删除的item项（必须使用广播发送至播放的原始列表
     * 因为我们对playList删除会改变playlist，所以position也可能变）。
     * @param playList
     */
    private void setPlayList(List<MusicInfo> playList) {
        if(playList!=null&&playList.size()!=0){
            mPlayList = new ArrayList<>();
            //为了取消list关联性,所以利用for循环一个一个添加，如果使用下面注释的方式，只是一个引用，
            //如果playlist变化了，那么对应的列表也会跟着变化，这不是我们想看到的
            for (int i = 0; i < playList.size(); i++) {
                mPlayList.add(playList.get(i));
            }
//            mPlayList = (ArrayList<MusicInfo>) playList;
            mRandomFlags = new boolean[mPlayList.size()];
            mCurPlayIndex = -1;
            MusicService.this.sendBroadcast(new Intent(PLAY_LIST_CHANGE));
            mInstance.savePlayListDeleteItem(null);
        }
    }

    @Nullable
    private MusicInfo getCurMusicInfo() {
        if(mCurPlayIndex==-1)
            return null;
        return mPlayList.get(mCurPlayIndex);
    }

    /**
     * 只有播放本地音乐的时候才会调用本方法
     * 本app的数据库中没有该歌曲，我们需要加入到我们的数据库中
     *  1.首先判断系统数据库中是否存在
     *  2.如果系统数据库不存在，我们直接添加
     * @param path 歌曲的全路径
     */
    private void updateLocalDataBaseMusic(String path){
        MusicInfo musicInfo = MusicUtils.getMusicInfoByData(path,
                SystemMusicOpenHelper.queryMusics());
        if(musicInfo==null){//如果系统数据库不存在，我们需要自己处理音乐信息
            musicInfo = new MusicInfo();
            musicInfo.setSongId(mPlayList.size());
            String totalName = MusicUtils.cutName(path);
            String musicTitle = MusicUtils.getTitleNameByTotalName(totalName);
            musicInfo.setAlbum(musicTitle);
            musicInfo.setTitle(musicTitle);
            musicInfo.setArtist(MusicUtils.getSingerNameByTotalName(totalName));
            musicInfo.setData(path);
            musicInfo.setAlbumId(-1);
            musicInfo.setDuration(-1);
        }
        //这时mPlayList已经是本地音乐了
        musicInfo.setMusicId(mPlayList.size());
        mPlayList.add(musicInfo);
        //重新排序a-z
        PinYinUtil.sortMusics(mPlayList);
        updateLocalDataBase(mPlayList);
    }

    private void updateLocalDataBase(final ArrayList<MusicInfo> localList){
        //更新到本地数据库
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                MusicDao musicDao = new MusicDao(UIUtils.getContext());
                musicDao.deleteDataBase();
                MusicInfo musicInfo;
                for (int i = 0; i < localList.size(); i++) {
                    musicInfo = localList.get(i);
                    musicInfo.setMusicId(i);
                    musicDao.insert(musicInfo);
                }
                return null;
            }
        }.execute();
    }

    /**
     * 接收来自远程的控制，例如通知栏与桌面控件
     */
    class RemoteControlReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(REMOTE_PRE)){
                if(mPlayMode == MODE_LIST_SHUFFLE){
                    preparedAndPlay(getShuffleSongId());
                }else{
                    preparedAndPlay(getValidMusicIndex(mCurPlayIndex-1));
                }
            }else if(action.equals(REMOTE_NEXT)){
                if(mPlayMode == MODE_LIST_SHUFFLE){
                    preparedAndPlay(getShuffleSongId());
                }else{
                    preparedAndPlay(getValidMusicIndex(mCurPlayIndex+1));
                }
            }else if(action.equals(REMOTE_PLAY_PAUSE)){
                play(mCurPlayIndex);
            }else if(action.equals(DialogUtils.EXIT_SONG_COUNT_BROADCAST)){
                mExitSongCount = mInstance.getExitAtTimeSongCount();
            }else if(action.equals(MusicDownloadManager.NEW_DOWNLOAD_MUSIC)){
                //1.更新歌曲到本地歌曲
                ArrayList<MusicInfo> allMusics = MyApplication.getMyApplication().getAllList();
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setData(intent.getStringExtra("data"));
                musicInfo.setMusicId(allMusics.size());
                musicInfo.setSongId(intent.getIntExtra("songId",-1));
                musicInfo.setAlbumId(intent.getIntExtra("albumId",-1));
                musicInfo.setAlbum(intent.getStringExtra("album"));
                musicInfo.setTitle(intent.getStringExtra("title"));
                musicInfo.setArtist(intent.getStringExtra("artist"));
                allMusics.add(musicInfo);
                PinYinUtil.sortMusics(allMusics);
                updateLocalDataBase(allMusics);
                //2.判断播放队列是不是播放的本地音乐,如果是，我们需要修改播放队列，同时播放id也得改
                if(mInstance.getPlayerPlaylistInfo().equals(LocalItemPager.LOCAL_LIST_INFO)){
                    String curPath = mPlayList.get(mCurPlayIndex).getData();
                    mPlayList = allMusics;
                    mRandomFlags = new boolean[mPlayList.size()];
                    mCurPlayIndex = MusicUtils.getMusicInfoIndexByData(curPath, mPlayList);
                    //通知本地列表当前的播放id变了，修改一下高亢id
                    sendBroadcast(new Intent(LOCAL_PLAYING_INDEX_CHANGED));
                }
            }else if(action.equals(WidgetUpdateService.ACTION_REMOTE_PLAY_MODE_CHANGE)){
                switch (mPlayMode){
                    case MODE_LIST_ORDER:
                        setPlayMode(MODE_LIST_LOOP);
                        break;
                    case MODE_LIST_LOOP:
                        setPlayMode(MODE_LIST_SHUFFLE);
                        break;
                    case MODE_LIST_SHUFFLE:
                        setPlayMode(MODE_ONE_LOOP);
                        break;
                    case MODE_ONE_LOOP:
                        setPlayMode(MODE_LIST_ORDER);
                        break;
                }
            }else if(action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)&&mInstance.getIsHeadsetPlugPause()){
                pause();
            }else if(action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
                manageTelephonyPlayState();
            }else if(action.equals(MusicMenuFragment.NO_CHANGE_PLAY_LIST)){
                MusicInfo nextMusic = intent.getParcelableExtra("nextMusic");
                mPlayList.add(nextMusic);
                boolean[] newRandomFlags = new boolean[mPlayList.size()];
                for (int i = 0; i < mRandomFlags.length; i++) {
                    newRandomFlags[i] = mRandomFlags[i];
                }
                newRandomFlags[mPlayList.size()-1] = true;
                mRandomFlags = newRandomFlags;
            }
        }
    }

    private boolean mIsTelephonyLastStatePlay = false;
    /**
     * 管理来电时音乐状态
     */
    private void manageTelephonyPlayState() {
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING: // 来电铃声响起
                if(mPlayState == STATE_PLAYING){
                    mIsTelephonyLastStatePlay = true;
                    pause();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:// 接起电话
                break;
            case TelephonyManager.CALL_STATE_IDLE: //挂断电话
                if(mIsTelephonyLastStatePlay&&mInstance.getIsTelephonyCompleteContinue()){// 如果原来是播放状态就继续播放
                    play(mCurPlayIndex);
                    mIsTelephonyLastStatePlay = false;
                }
                break;
        }
    }


    // dp值转px 参数整型值
    public static int dp2px(int dp, Context context) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics())+0.5f);
    }

    // dp值转PX 参数浮点数
    public static float dp2px(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

}
