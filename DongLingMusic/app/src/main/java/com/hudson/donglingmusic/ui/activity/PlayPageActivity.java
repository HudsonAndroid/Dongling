package com.hudson.donglingmusic.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bitmapblurjni.StackBlur;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.net.download.DownloadHelper;
import com.hudson.donglingmusic.net.download.MusicDownloadManager;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.pager.BasePlayPager;
import com.hudson.donglingmusic.ui.pager.LyricsPager;
import com.hudson.donglingmusic.ui.pager.VisibleMusicPager;
import com.hudson.donglingmusic.ui.view.ArcMenu;
import com.hudson.donglingmusic.ui.view.PlayPageViewPager;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.DimensionUtils;
import com.hudson.donglingmusic.utils.PermissionUtils;
import com.hudson.donglingmusic.utils.TimeUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.hudson.donglingmusic.net.download.MusicDownloadManager.NEW_DOWNLOAD_MUSIC;
import static com.hudson.donglingmusic.ui.itempager.MyGedanItemPager.getMyGedanDatabaseName;

/**
 *
 *
 * 动态权限说明：
 *      如果第一次运行，那么将会提示要求用户确认，在华为手机上，第一次确认会进入activity的回调方法中；
 *      如果是后面运行的，权限拥有与否已经确定了，那么不会进入activity的会调中，而是在我们的permissionUtils的
 *      runnable中执行操作,我们只需要判断权限是否拥有即可，如果不拥有就不添加可视化音乐的界面，如果拥有，就展示
 *      可视化音乐。
 *
 * 本界面的背景使用了高斯模糊处理的算法，使用jni技术完成，并没有使用renderScript，原因是renderScript需要在API17
 * 之后方可支持（4.0）
 */
public class PlayPageActivity extends Activity {
    private ImageView mFavorite;
    private IDonglingMusicAidlInterface mInterface;
    private PlayPageServiceConnection mConnection;
    private LyricsPager mLyricsPager;
    private VisibleMusicPager mVisibleMusicPager;
    private PlayPageViewPager mViewPager;
    private PlayPageReceiver mReceiver;
    //下载按钮可用与否
    private ImageView mDownloadImageView;
    //用于seekbar
    private Timer mTimer;
    private TimerTask mTimerTask;
    private SeekBar mSeekBar;
    private TextView mLeftTime,mRightTime;
    //标题
    private TextView mMusicTitle,mMusicInfo;

    //pager页
    private ArrayList<BasePlayPager> mPagerList;
    private PlayPagerAdapter mAdapter;

    //录音权限是否拥有
    private volatile boolean mRecordAudioPermissionGranted = false;
    private SlideRelativeLayout mRootView;


    //圆形菜单
    private ArcMenu mLeftMenu,mRightMenu;
    private ImageView mRightMenuPlayPause;

    private int[] mScreenDimensions;

    public static final String DOWNLOAD_TASK_START = "com.hudson.music.download_task_start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_page);
        bind();
        init();
    }

    @Override
    protected void onStart() {
        mLyricsPager.moveFocusLyricsToCenter();
        super.onStart();
    }

    private void init() {
        mScreenDimensions = new int[2];
        DimensionUtils.getScreenDimension(this,mScreenDimensions);
        findViewById(R.id.iv_play_page_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFavorite = (ImageView) this.findViewById(R.id.playpage_favorite);
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myFavoriteTitle = UIUtils.getString(R.string.my_favorite_gedan);
                LitePalDB litePalDB = new LitePalDB(getMyGedanDatabaseName(myFavoriteTitle), 1);
                litePalDB.addClassName(MusicInfo.class.getName());
                LitePal.use(litePalDB);
                if(mInterface!=null){
                    try {
                        MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                        if(curMusicInfo!=null){
                            if(!mIsFavoriteSong){
                                curMusicInfo.save();
                                ToastUtils.showToast("已添加到我的最爱歌单！");
                                mFavorite.setImageResource(R.drawable.play_page_favorite);
                            }else{
                                DataSupport.deleteAll(MusicInfo.class,"msongId = ? and malbumId = ? and mtitle = ? and martist = ? and malbum = ?",
                                        curMusicInfo.getSongId()+"",curMusicInfo.getAlbumId()+"",curMusicInfo.getTitle(),
                                        curMusicInfo.getArtist(),curMusicInfo.getAlbum());
                                ToastUtils.showToast("取消喜欢!");
                                mFavorite.setImageResource(R.drawable.play_page_favorite_empty);
                            }
                        }else{
                            ToastUtils.showToast("当前没有播放的歌曲！");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mRootView = (SlideRelativeLayout) findViewById(R.id.sll_root);
        mRootView.setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                finish();//会触发onDestroy方法
            }
        });
        mDownloadImageView = (ImageView) findViewById(R.id.playpage_download);
        mDownloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterface!=null){
                    try {
                        final MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                        if(curMusicInfo!=null){
                            String data = curMusicInfo.getData();
                            if(data.startsWith("http:")||data.startsWith("https:")){
                                String title = curMusicInfo.getTitle();
                                if(new File(DownloadHelper.getLocalPathByMusicInfoTitle(title)).exists()){
                                    DialogUtils.showInformationDialog(PlayPageActivity.this, "文件已存在", title + " 已经下载过了，是否重新下载？", "重新下载", "取消下载", null, new Runnable() {
                                        @Override
                                        public void run() {
                                            MusicDownloadManager.getInstance().startDownload(curMusicInfo);
                                            sendBroadcast(new Intent(DOWNLOAD_TASK_START));
                                        }
                                    });
                                }else{
                                    MusicDownloadManager.getInstance().startDownload(curMusicInfo);
                                    sendBroadcast(new Intent(DOWNLOAD_TASK_START));
                                    ToastUtils.showToast("已添加到下载队列！");
                                }
                            }else{
                                ToastUtils.showToast("当前歌曲已经是本地音乐或者已经下载过了！");
                            }
                        }else{
                            ToastUtils.showToast("没有正在播放的歌曲！");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else{
                    ToastUtils.showToast("正在初始化数据，请稍后!");
                }
            }
        });
        mLeftMenu = (ArcMenu) findViewById(R.id.left_menu);
        mLeftMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Intent intent;
                switch (position){
                    case 1://搜索歌词
                        ToastUtils.showToast("开发者较懒，本功能不想再处理，谢谢您的支持！");
                        break;
                    case 2://设置
                        intent = new Intent(PlayPageActivity.this,PlayPageSettingActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 3://制作歌词
                        try {
                            if(mInterface!=null&&mInterface.getPlayState()!= MusicService.STATE_IDLE){
                                intent = new Intent(PlayPageActivity.this,LyricsMakerActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                ToastUtils.showToast("当前没有播放的歌曲，无法制作歌词！");
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4://分享
                        ToastUtils.showToast("开发者较懒，本功能不想再处理，谢谢您的支持！");
                        break;
                }
            }
        });
        mRightMenu = (ArcMenu) findViewById(R.id.right_menu);
        mLeftMenu.setOnClickMenuToggleListener(new ArcMenu.onClickMenuToggleListener() {
            @Override
            public void onClickMenuToggle(int status) {
                if(status == ArcMenu.OPEN){
                    if(mRightMenu.mStatus == ArcMenu.OPEN){
                        mRightMenu.closeMenu(300);
                    }
                }
            }
        });
        mRightMenu.setOnClickMenuToggleListener(new ArcMenu.onClickMenuToggleListener() {
            @Override
            public void onClickMenuToggle(int status) {
                if(status == ArcMenu.OPEN){
                    if(mLeftMenu.mStatus == ArcMenu.OPEN){
                        mLeftMenu.closeMenu(300);
                    }
                }
            }
        });
        mRightMenuPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
        mRightMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                switch (position){
                    case 1://下一曲,这里有一个问题使用了子线程也一样，会阻塞动画的执行
                        if(mInterface!=null){
                            try {
                                mInterface.next();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 2://播放暂停
                        if(mInterface!=null){
                            try {
                                int playState = mInterface.getPlayState();
                                if(playState !=MusicService.STATE_IDLE){
                                    if(playState == MusicService.STATE_PLAYING){
                                        mRightMenuPlayPause.setImageResource(R.drawable.page_menu_play);
                                    }else{
                                        mRightMenuPlayPause.setImageResource(R.drawable.page_menu_pause);
                                    }
                                    mInterface.play(mInterface.getCurMusicInfoIndex());
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 3://上一曲,这里有一个问题使用了子线程也一样，会阻塞动画的执行
                        if(mInterface!=null){
                            try {
                                mInterface.pre();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 4://播放列表
                        Intent intent = new Intent(PlayPageActivity.this, MoreInfoActivity.class);
                        intent.putExtra("playListPageOrNot",true);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                }
            }
        });
        mPagerList = new ArrayList<>();
        mLyricsPager = new LyricsPager(this);
        mPagerList.add(mLyricsPager);
        mVisibleMusicPager = new VisibleMusicPager(this);
        mAdapter = new PlayPagerAdapter();
        mViewPager = (PlayPageViewPager) findViewById(R.id.vp_play);
        mViewPager.setAdapter(mAdapter);
        //申请录音权限
        PermissionUtils.requestPermission(this, Manifest.permission.RECORD_AUDIO, 100, new Runnable() {
            @Override
            public void run() {
                //权限允许
                mRecordAudioPermissionGranted = true;
            }
        });
        mMusicTitle = (TextView) findViewById(R.id.tv_music_title);
        mMusicInfo = (TextView) findViewById(R.id.tv_music_info);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeftTime.setText(TimeUtils.toTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    mInterface.seekTo(seekBar.getProgress());
                    mLyricsPager.playFromPosition();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mLeftTime = (TextView) findViewById(R.id.time_left);
        mRightTime = (TextView) findViewById(R.id.time_right);
        mReceiver = new PlayPageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_NEW_MUSIC);
        filter.addAction(MusicService.ACTION_PLAY);
        filter.addAction(MusicService.ACTION_PAUSE);
        filter.addAction(MusicService.ACTION_STOP);
        filter.addAction(NEW_DOWNLOAD_MUSIC);
        registerReceiver(mReceiver,filter);
    }

    private boolean mIsFavoriteSong = false;//是否是我的最爱歌单里的歌曲
    /**
     * 检测歌曲是不是我的最爱歌曲
     */
    private void detectFavoriteSongOrNot(){
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                if(mInterface!=null){
                    try {
                        MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                        if(curMusicInfo!=null){
                            String myFavoriteTitle = UIUtils.getString(R.string.my_favorite_gedan);
                            LitePalDB litePalDB = new LitePalDB(getMyGedanDatabaseName(myFavoriteTitle), 1);
                            litePalDB.addClassName(MusicInfo.class.getName());
                            LitePal.use(litePalDB);
                            List<MusicInfo> musics = DataSupport.findAll(MusicInfo.class);
                            for (MusicInfo music : musics) {
                                if(music.equals(curMusicInfo)){
                                    return true;//属于我的最爱歌单
                                }
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                mIsFavoriteSong = aVoid;
                if(mIsFavoriteSong){
                    mFavorite.setImageResource(R.drawable.play_page_favorite);
                }else{
                    mFavorite.setImageResource(R.drawable.play_page_favorite_empty);
                }
            }
        }.execute();
    }

    private void resetSeekBar(){
        try{
            int max = mInterface.getDuration();
            mRightTime.setText(TimeUtils.toTime(max));
            mSeekBar.setMax(max);
            if (mTimer != null) {
                mTimer.cancel();
                mTimerTask.cancel();
                mTimer = null;
                mTimerTask = null;
            }
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if(mInterface.isPlaying()||mInterface.getPlayState() == MusicService.STATE_PAUSE)//只有在播放或暂停状态我们才去更新！
                            mSeekBar.setProgress(mInterface.getCurPosition());// 更新小圆球
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            mTimer.schedule(mTimerTask, 0, 500);// 每隔500毫秒执行一次
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 更新模糊背景
     */
    public void updateGaosiBackground(){
        final Bitmap[] blurBitmap = new Bitmap[1];
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Bitmap bitmap = null;
                    if(mInterface.getPlayState()!=MusicService.STATE_IDLE) {
                        MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                        bitmap = BitmapMusicUtils.getArtwork(curMusicInfo.getTitle(),curMusicInfo.getAlbumId(),mScreenDimensions[0],mScreenDimensions[1]);
                    }
                    if(bitmap!=null){
                        blurBitmap[0] = StackBlur.blurNatively(bitmap, MySharePreferences.getInstance().getGaosiRadius(),true);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e){
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast("音乐图片不支持模糊处理，自动切换回默认背景！");
                        }
                    });
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(blurBitmap[0]!=null)
                    mRootView.setBackgroundDrawable(new BitmapDrawable(blurBitmap[0]));
                else{
                    if(!BitmapMusicUtils.setViewDefaultBackground(PlayPageActivity.this,mRootView)){
                        mRootView.setBackgroundResource(R.drawable.default_bg);
                    }
                }
                super.onPostExecute(aVoid);
            }

        }.execute();
    }


    private void bind(){
        mConnection = new PlayPageServiceConnection();
        bindService(new Intent(PlayPageActivity.this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }

    class PlayPageServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
            mLyricsPager.setIDonglingMusicInterface(mInterface);
            try {
                if(mInterface.getPlayState() == MusicService.STATE_PLAYING){
                    mRightMenuPlayPause.setImageResource(R.drawable.page_menu_pause);
                }else{
                    mRightMenuPlayPause.setImageResource(R.drawable.page_menu_play);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mLyricsPager.initDataAfterMeasured();
            if(mRecordAudioPermissionGranted){//如果拥有权限，就加入
                mVisibleMusicPager.setIDonglingMusicInterface(mInterface);
                mVisibleMusicPager.initData();
                mPagerList.add(mVisibleMusicPager);
                mAdapter.notifyDataSetChanged();
            }
            resetSeekBar();
            updateGaosiBackground();
            try {
                MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                if(curMusicInfo==null){
                    mMusicTitle.setText(R.string.app_name);
                    mMusicInfo.setText(R.string.author);
                }else{
                    mMusicTitle.setText(curMusicInfo.getTitle());
                    mMusicInfo.setText(curMusicInfo.getMusicAlbumInfo());
                }
                if(curMusicInfo!=null&&curMusicInfo.getData().startsWith("http")){
                    mDownloadImageView.setImageResource(R.drawable.play_page_download);
                }else{
                    mDownloadImageView.setImageResource(R.drawable.play_page_download_invalid);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            detectFavoriteSongOrNot();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        unBind();
        unregisterReceiver(mReceiver);
        mLyricsPager.clear();
        mVisibleMusicPager.clear();
        if(mTimer!=null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimerTask = null;
            mTimer = null;
        }
        super.onDestroy();
    }

    class PlayPageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MusicService.ACTION_NEW_MUSIC)){//新的歌曲
                if(mRecordAudioPermissionGranted&&mVisibleMusicPager.mInterface!=null){
                    mVisibleMusicPager.initData();
                }
                mLyricsPager.initData();
                resetSeekBar();
                updateGaosiBackground();
                try {
                    MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                    mMusicTitle.setText(curMusicInfo.getTitle());
                    mMusicInfo.setText(curMusicInfo.getMusicAlbumInfo());
                    if(curMusicInfo.getData().startsWith("http")){
                        mDownloadImageView.setImageResource(R.drawable.play_page_download);
                    }else{
                        mDownloadImageView.setImageResource(R.drawable.play_page_download_invalid);
                    }
                    if(mInterface.isPlaying()){
                        mRightMenuPlayPause.setImageResource(R.drawable.page_menu_pause);
                    }else{
                        mRightMenuPlayPause.setImageResource(R.drawable.page_menu_play);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                detectFavoriteSongOrNot();
            }else if(action.equals(MusicService.ACTION_PLAY)){
                mLyricsPager.play();
                if(mRecordAudioPermissionGranted){
                    mVisibleMusicPager.reStartRotateDisk();
                }
                mRightMenuPlayPause.setImageResource(R.drawable.page_menu_pause);
            }else if(action.equals(MusicService.ACTION_PAUSE)){
                mLyricsPager.pause();
                if(mRecordAudioPermissionGranted){
                    mVisibleMusicPager.pauseRotateDisk();
                }
                mRightMenuPlayPause.setImageResource(R.drawable.page_menu_play);
            }else if(action.equals(MusicService.ACTION_STOP)){
                mLyricsPager.clear();
                if(mRecordAudioPermissionGranted){
                    mVisibleMusicPager.stopRotateDisk();
                }
                mRightMenuPlayPause.setImageResource(R.drawable.page_menu_play);
                mMusicTitle.setText(getString(R.string.app_name));
                mMusicInfo.setText(getString(R.string.author));
                resetSeekBar();
                detectFavoriteSongOrNot();
            }else if(action.equals(NEW_DOWNLOAD_MUSIC)){
//                intent.putExtra("artist", artist);
//                String album = mHelper.getAlbum();
//                intent.putExtra("album", album);
//                intent.putExtra("title",mHelper.mDownloadTitle);
//                int albumId = mHelper.getAlbumId();
//                intent.putExtra("albumId", albumId);
//                intent.putExtra("songId", downloadId);
                String artist = intent.getStringExtra("artist");
                String album = intent.getStringExtra("album");
                String title = intent.getStringExtra("title");
                int albumId = intent.getIntExtra("albumId",-1);
                int songId = intent.getIntExtra("songId",-1);
                if(mInterface!=null){
                    MusicInfo curMusicInfo = null;
                    try {
                        curMusicInfo = mInterface.getCurMusicInfo();
                        if(curMusicInfo!=null){
                            if(curMusicInfo.getTitle().equals(title)&&curMusicInfo.getArtist().equals(artist)
                                    &&curMusicInfo.getAlbum().equals(album)&&curMusicInfo.getAlbumId() == albumId
                                    &&curMusicInfo.getSongId() == songId){
                                mDownloadImageView.setImageResource(R.drawable.play_page_download_invalid);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode    , permissions, grantResults);
        if (requestCode == 100) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //权限被允许,后续操作
                if(mInterface!=null){
                    mVisibleMusicPager.setIDonglingMusicInterface(mInterface);
                    mVisibleMusicPager.initData();
                    mPagerList.add(mVisibleMusicPager);
                    mAdapter.notifyDataSetChanged();
                }
            }else{
                //权限被拒绝
                ToastUtils.showToast("抱歉，权限被拒绝，可视化音乐无法开启！");
            }
        }
    }

    class PlayPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mPagerList.get(position).mRootView;
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
}
