package com.hudson.donglingmusic.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicDao;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.db.MusicOpenHelper;
import com.hudson.donglingmusic.db.SystemMusicOpenHelper;
import com.hudson.donglingmusic.global.MyApplication;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.receiver.HeadsetButtonReceiver;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.service.WidgetUpdateService;
import com.hudson.donglingmusic.ui.fragment.HomeContentFragment;
import com.hudson.donglingmusic.ui.fragment.LeftMenuFragment;
import com.hudson.donglingmusic.ui.view.MarqueeTextView;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;
import com.hudson.donglingmusic.utils.DeskTopLyricsUtils;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.MusicUtils;
import com.hudson.donglingmusic.utils.PermissionUtils;
import com.hudson.donglingmusic.utils.PinYinUtil;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.io.File;
import java.util.ArrayList;

import static com.hudson.donglingmusic.receiver.HeadsetButtonReceiver.MEDIA_BUTTON_CLICK_TIMES_BROADCAST;
import static com.hudson.donglingmusic.service.MusicService.REMOTE_PLAY_PAUSE;
import static com.hudson.donglingmusic.utils.DialogUtils.EXIT_BROADCAST;

//import com.hudson.donglingmusic.IDonglingMusicAidlInterface;

/**
 * 主界面
 *
 * 1.使用slidingMenu:
 *      1.引入lib
 *      2.将activity继承自slidingActivity（如果用到fragment,就改成SlidingFragmentActivity）
 *      3.将activity的onCreate方法改成public
 *      4.常用API:
 *          4.1)获取slidingMenu对象   getSlidingMenu()
 *          4.2)设置右侧侧边栏         slidingMenu.setSecondaryMenu(id);   slidingMenu.setMode(Left_Right);
 *
 * 2.使用fragment替换原有布局
 */
public class HomeActivity extends SlidingFragmentActivity {

    private SlidingMenu mSlidingMenu;//侧边栏
    private static final String FRAGMENT_TAG_CONTENT ="tag_content";//fragment标记
    private static final String FRAGMENT_TAG_LEFT_MENU ="tag_left_menu";
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;//权限请求
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private HomeServiceConnection mConnection;
    private MusicChangeReceiver mReceiver;

    private ImageView mBottomPic;
    private MarqueeTextView mBottomTitle;
    private TextView mBottomInfo;
    private ImageView mBottomPlayPause;
    private LeftMenuFragment mLeftMenuFragment;
    private MySharePreferences mInstance;
    private DeskTopLyricsUtils mDeskTopLyricsUtils;

    // 耳机广播事件相关变量
    private AudioManager mAudioManager;
    private ComponentName mComponentName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        //设置侧边栏
        setBehindContentView(R.layout.menu_left);
        mSlidingMenu = getSlidingMenu();
        //设置全屏触摸可以滑动
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置侧边栏给屏幕预留宽度(即减去slidingMenu的宽度)
        mSlidingMenu.setBehindOffset(200);
        initFragment();
        initPlayerDataBase();
        initBroadCast();
        initBottomBar();
        checkWidgetExist();//探测widget是否被创建过
        bind();
        checkHeadsetState();
    }

    private void checkHeadsetState() {
        // 耳机广播事件
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // AudioManager注册一个MediaButton对象
        mComponentName = new ComponentName(getPackageName(),
                HeadsetButtonReceiver.class.getName());
        boolean wiredHeadsetOn = mAudioManager.isWiredHeadsetOn();
        if(wiredHeadsetOn){
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        }
    }

    /**
     * 检测是否有widget创建过,如果创建过，那么重新开启widget更新服务
     */
    private void checkWidgetExist() {
        if(mInstance.getIsWidgetCreated()){
            Intent intent  = new Intent(this,WidgetUpdateService.class);
            startService(intent);
        }
    }

    private void initBroadCast() {
        mReceiver = new MusicChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_NEW_MUSIC);
        filter.addAction(MusicService.ACTION_PAUSE);
        filter.addAction(MusicService.ACTION_PLAY);
        filter.addAction(MusicService.ACTION_STOP);
        filter.addAction(Intent.ACTION_SCREEN_ON);//锁屏
        filter.addAction(EXIT_BROADCAST);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);//用于耳机线控制，注意拔出耳机暂停不是这个
        filter.addAction(MEDIA_BUTTON_CLICK_TIMES_BROADCAST);
        registerReceiver(mReceiver,filter);
    }

    private void initBottomBar() {
        mInstance = MySharePreferences.getInstance();
        mBottomPic = (ImageView) findViewById(R.id.iv_music_pic);
        findViewById(R.id.bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,PlayPageActivity.class));
            }
        });
        mBottomInfo = (TextView) findViewById(R.id.tv_bottom_music_info);
        mBottomTitle = (MarqueeTextView) findViewById(R.id.tv_bottom_music_title);
        findViewById(R.id.iv_bottom_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterface!=null){
                    try {
                        mInterface.pre();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mBottomPlayPause = (ImageView) findViewById(R.id.iv_bottom_play_pause);
        mBottomPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterface!=null){
                    try {
                        mInterface.play(mInterface.getCurMusicInfoIndex());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findViewById(R.id.iv_bottom_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterface!=null){
                    try {
                        mInterface.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findViewById(R.id.iv_bottom_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MoreInfoActivity.class);
                intent.putExtra("playListPageOrNot",true);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    private void updateBottomBar(MusicInfo info){
        Bitmap bitmap = BitmapMusicUtils.getArtwork(info.getTitle(),info.getAlbumId(), mBottomPic.getWidth(), mBottomPic.getHeight());
        if(bitmap == null){
            mBottomPic.setImageResource(R.drawable.icon_player);
        }else{
            mBottomPic.setImageBitmap(bitmap);
        }
        mBottomInfo.setText(info.getMusicAlbumInfo());
        mBottomTitle.setText(info.getTitle());
        try {
            if(mInterface.isPlaying()){
                mBottomPlayPause.setImageResource(R.drawable.bottom_bar_pause);
            }else{
                mBottomPlayPause.setImageResource(R.drawable.bottom_bar_play);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void resetBottomBar(){
        mBottomInfo.setText(getString(R.string.app_name));
        mBottomTitle.setText(getString(R.string.author));
        mBottomPic.setImageResource(R.drawable.icon_player);
        mBottomPlayPause.setImageResource(R.drawable.bottom_bar_play);
    }

    private void initPlayerDataBase() {
        if (!new File(
                "data/data/"+getPackageName()+"/databases/"+ MusicOpenHelper.mDataBaseName)
                .exists()) {
            //访问数据库需要READ_EXTERNAL_STORAGE权限
            PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_CAMERA_CODE, new Runnable() {
                @Override
                public void run() {
                    new MyInitDataBaseAsyncTask().execute();
                }
            });
//            //开启一个异步线程来操作，在权限被允许后执行
//            new MyInitDataBaseAsyncTask().execute();
        }else{
//            System.out.println("数据存在");
            MyApplication.getMyApplication().setAllList(new MusicDao(UIUtils.getContext()).queryAll());
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();//开启事务
        transaction.add(R.id.fl_home,new HomeContentFragment(), FRAGMENT_TAG_CONTENT);
        mLeftMenuFragment = new LeftMenuFragment();
        transaction.add(R.id.fl_left_menu, mLeftMenuFragment, FRAGMENT_TAG_LEFT_MENU);
        transaction.commit();
    }

    //申请权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //权限被允许,后续操作
                //开启一个异步线程来操作
//                System.out.println("回调的权限允许");
                new MyInitDataBaseAsyncTask().execute();
            }else{
                //权限被拒绝
                ToastUtils.showToast("权限被拒绝，您无法正常使用本应用，应用即将退出！");
                // TODO: 2017/3/23 推出应用操作
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(mOnActivityReturnResultListener!=null){
                mOnActivityReturnResultListener.onResultReturn(data.getIntExtra("position",-1));
            }
        }
    }
    public void setOnActivityReturnResultListener(OnActivityReturnResultListener onActivityReturnResultListener) {
        mOnActivityReturnResultListener = onActivityReturnResultListener;
    }
    private OnActivityReturnResultListener mOnActivityReturnResultListener;
    public interface OnActivityReturnResultListener{
        void onResultReturn(int position);
    }

    /**
     * 初始化数据的异步线程
     */
    class MyInitDataBaseAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
//            System.out.println("初始化数据库");
            // 初始化歌曲数据库
            ArrayList<MusicInfo> musics = SystemMusicOpenHelper
                    .queryMusics();
            if (musics.size() == 0) {
                //sd卡没有歌曲
            } else {
                PinYinUtil.sortMusics(musics);
                int size = musics.size();
                MusicDao musicDao = new MusicDao(UIUtils.getContext());
                MusicInfo musicInfo;
                for (int i = 0; i < size; i++) {
                    musicInfo = musics.get(i);
                    musicInfo.setMusicId(i);
                    musicDao.insert(musicInfo);
                }
                MyApplication.getMyApplication().setAllList(musics);
            }
            mInstance.saveScanMusicsComplete(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {//数据库初始化完了
            super.onPostExecute(aVoid);
        }
    }


    public IDonglingMusicAidlInterface getInterface(){
        return mInterface;
    }

    private void bind(){
        mConnection = new HomeServiceConnection();
        //注意：不能使用intent("xxxx.service");的方式，5.0之后规定服务不能这样启动
        bindService(new Intent(HomeActivity.this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }

    /**
     * 检测是否是文件打开意图
     */
    private void detectOpenMusicFile(){
        Intent openFileIntent = getIntent();
        if(openFileIntent!=null){
            Uri uri = openFileIntent.getData();
            if(uri!=null){
                try {
                    mInterface.playByPath(uri.getPath());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 由于设置了singleTask模式，所以已经存在情况下默认是getIntent是第一次启动的intent
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        detectOpenMusicFile();
    }

    class HomeServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
            try {
                mInterface.setPlayMode(mInstance.getPlayMode());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mLeftMenuFragment.onMusicServiceConnected();
            detectOpenMusicFile();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    @Override
    protected void onStop() {
        isAppShowForground = false;
        startDesktopLyricsPlay();
        super.onStop();
    }

    private boolean isAppShowForground = false;//app是否正在前台运行，用于桌面歌词

    private void startDesktopLyricsPlay() {
        try {
            if(mInterface!=null){
                final int playState = mInterface.getPlayState();
                if(!isAppShowForground&&mInstance.getShowDeskTopLyrics()&&(playState==
                        MusicService.STATE_PLAYING|playState==MusicService.STATE_PAUSE)){
                    mDeskTopLyricsUtils = DeskTopLyricsUtils.getInstance();
                    mDeskTopLyricsUtils.setmOnLoadCompleteListener(new DeskTopLyricsUtils.onLyricsLoadCompleteListener() {
                        @Override
                        public void onLyricsLoadComplete() {
                            try {
                                mDeskTopLyricsUtils.calibrationLyricsTime(mInterface.getCurPosition(),playState == MusicService.STATE_PAUSE);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mDeskTopLyricsUtils.showDeskTopLyrics(getApplicationContext(),
                            new StringBuilder(StorageUtils.getAppLyricsAbsolutePath())
                                    .append(MusicUtils.cutName(mInterface.getCurMusicInfo())).append(".lrc").toString());
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        isAppShowForground = true;
        if(mInstance.getShowDeskTopLyrics()){
            DeskTopLyricsUtils.getInstance().hide();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(mAudioManager.isWiredHeadsetOn()){
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        }
        mAudioManager = null;
        mComponentName = null;
        if(mInstance.getShowDeskTopLyrics()){
            if(mDeskTopLyricsUtils!=null)
                mDeskTopLyricsUtils.hide();
        }
        try {
            mInterface.exit();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unBind();
        DialogUtils.cancelExitAtTimeOffset(this);
        DialogUtils.cancelExitSongCount(this);
        unregisterReceiver(mReceiver);
        mInstance.savePlayerPlaylistInfo("null");
//        //关闭桌面widget更新的服务
//        Intent intent  = new Intent(this,WidgetUpdateService.class);
//        stopService(intent);
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public class MusicChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MusicService.ACTION_NEW_MUSIC)){
                try {
                    updateBottomBar(mInterface.getCurMusicInfo());
                    startDesktopLyricsPlay();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
//                mInstance.saveCurLyricsOffset(0);//在playPage的歌词调整时有用
            }else if(action.equals(MusicService.ACTION_PAUSE)){
                mBottomPlayPause.setImageResource(R.drawable.bottom_bar_play);
                if(!isAppShowForground&&mInstance.getShowDeskTopLyrics()){
                    mDeskTopLyricsUtils.pauseDesktopLyrics();
                }
            }else if(action.equals(MusicService.ACTION_PLAY)){
                mBottomPlayPause.setImageResource(R.drawable.bottom_bar_pause);
                if(!isAppShowForground&&mInstance.getShowDeskTopLyrics()){
                    try {
                        mDeskTopLyricsUtils.restartDesktopLyricsPlay(mInterface.getCurPosition());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(action.equals(MusicService.ACTION_STOP)){
                resetBottomBar();
                if(mInstance.getShowDeskTopLyrics()){
                    if(mDeskTopLyricsUtils!=null){
                        mDeskTopLyricsUtils.stopDesktopLyrics();
                    }
                }
            }else if(action.equals(EXIT_BROADCAST)){
                finish();
            }else if(action.equals(intent.ACTION_SCREEN_ON)){
                int playState = -1;
                try {
                    playState = mInterface.getPlayState();
                    if(playState == MusicService.STATE_PLAYING|playState == MusicService.STATE_PAUSE){
                        startActivity(new Intent(HomeActivity.this, LockScreenActivity.class));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else if(action.equals(Intent.ACTION_HEADSET_PLUG)){
                if (intent.getIntExtra("state", 0) == 1) {
                    mAudioManager
                            .registerMediaButtonEventReceiver(mComponentName);
                } else { // 耳机拔出，取消接受广播
                    mAudioManager
                            .unregisterMediaButtonEventReceiver(mComponentName);
                }
            }else if(action.equals(MEDIA_BUTTON_CLICK_TIMES_BROADCAST)){
                if (intent.getBooleanExtra("isLongClick", false)) {
                    chooseWay(mInstance.getHeadsetLongClick());
                }else if(!isChecking){
                    isChecking = true;
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            isChecking = false;
                            switch (HeadsetButtonReceiver.mClickTimes) {
                                case 1:// 暂停、播放
                                    chooseWay(mInstance.getHeadsetOnePressed());
                                    break;
                                case 2:// 下一曲
                                    chooseWay(mInstance.getHeadsetTwoPressed());
                                    break;
                                default:
                                    break;
                            }
                            HeadsetButtonReceiver.mClickTimes = 0;
                        }
                    }, 550);
                }
            }
        }
    }

    private Handler mHandler = new Handler();
    private boolean isChecking = false;

    /**
     * 耳机按键事件响应
     * @param index
     */
    private void chooseWay(int index) {
        switch (index) {
            case 0:
                sendBroadcast(new Intent(REMOTE_PLAY_PAUSE));
                break;
            case 1:
                if(mInterface!=null){
                    try {
                        mInterface.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if(mInterface!=null){
                    try {
                        mInterface.pre();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:// 取消该功能

                break;
            default:
                break;
        }
    }
}
