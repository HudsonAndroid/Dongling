package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bitmapblurjni.StackBlur;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.view.CircleRoundImageView;
import com.hudson.donglingmusic.ui.view.CircleSeekBar;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;
import com.hudson.donglingmusic.utils.DimensionUtils;
import com.hudson.donglingmusic.utils.TimeUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LockScreenActivity extends Activity {
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private LockServiceConnection mConnection;
    private MusicChangeReceiver mReceiver;
    private CircleSeekBar mCircleSeekBar;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private ImageView mLockPlayPause;
    private TextView mTimeView,mMusicTitle,mMusicInfo;
    private Calendar mCalendar;
    private SlideRelativeLayout mRootView;
    private int[] mScreenDimensions;
    private CircleRoundImageView mCenterImage;
    private boolean isCenterImageMeasureComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                        // bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_lock_screen);
        mCircleSeekBar = (CircleSeekBar) findViewById(R.id.csb_circle_seekbar);
        mCenterImage = (CircleRoundImageView) findViewById(R.id.criv_lock_image);
        mCenterImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//onLayout执行结束的回调方法
                mCenterImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //测量完成
                isCenterImageMeasureComplete = true;
                updateCenterImage();
            }
        });
        mCircleSeekBar.setOnProgressBarChangeListener(new CircleSeekBar.onProgressBarChangeListener() {
            @Override
            public void onProgressBarChange() {
                try {
                    mInterface.seekTo(mCircleSeekBar.getCurProgress());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mRootView = (SlideRelativeLayout) findViewById(R.id.srl_lock_root);
        mRootView.setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                LockScreenActivity.this.finish();
                overridePendingTransition(0,0);//取消默认动画
            }
        });
        mLockPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
        mLockPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterface!=null){
                    try {
                        mInterface.play(mInterface.getCurMusicInfoIndex());
                        updatePlayPause();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mTimeView = (TextView) findViewById(R.id.tv_time);
        mMusicTitle = (TextView) findViewById(R.id.tv_music_title);
        mMusicInfo = (TextView) findViewById(R.id.tv_music_info);
        findViewById(R.id.iv_pre).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
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
        mReceiver = new MusicChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_NEW_MUSIC);
        filter.addAction(MusicService.ACTION_PLAY);
        filter.addAction(MusicService.ACTION_PAUSE);
        filter.addAction(MusicService.ACTION_STOP);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver,filter);
        mScreenDimensions = new int[2];
        DimensionUtils.getScreenDimension(this,mScreenDimensions);
        bind();
    }

    class LockServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
            resetSeekBar();
            try {
                updatePlayPause();
                updateMusicText();
                updateSystemTime();
                updateGaosiBackground();
                if(isCenterImageMeasureComplete){
                    updateCenterImage();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void updatePlayPause() throws RemoteException {
        if(mInterface.isPlaying()){
            mLockPlayPause.setImageResource(R.drawable.lock_pause);
        }else{
            mLockPlayPause.setImageResource(R.drawable.lock_play);
        }
    }

    //为了节约电量，我们每30s刷新一次系统时间，即updateSystemTimeFlag到达60才更新一次
    private int updateSystemTimeFlag = 0;
    private void updateSystemTime(){
        System.out.println("刷新系统时间");
        mCalendar = Calendar.getInstance();
        mTimeView.setText(TimeUtils.toHourAndMinute(mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE)));
    }

    private void updateMusicText() throws RemoteException {
        MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
        mMusicInfo.setText(curMusicInfo.getMusicSingerInfo());
        mMusicTitle.setText(curMusicInfo.getTitle());
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
                    ToastUtils.showToast("音乐图片不支持模糊处理，自动切换回默认背景！");//好像在异常附近无法土司？？
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(blurBitmap[0]!=null){
                    mRootView.setBackgroundDrawable(new BitmapDrawable(blurBitmap[0]));
                }else{
                    mRootView.setBackgroundResource(R.drawable.default_bg);
                }
                super.onPostExecute(aVoid);
            }

        }.execute();
    }

    private void updateCenterImage(){
        if(mInterface == null){
            return ;
        }
        MusicInfo curMusicInfo = null;
        try {
            curMusicInfo = mInterface.getCurMusicInfo();
            Bitmap artwork = BitmapMusicUtils.getArtwork(curMusicInfo.getTitle(),
                    curMusicInfo.getAlbumId(), mCenterImage.getWidth(), mCenterImage.getHeight());
            if(artwork != null){
                mCenterImage.setImageBitmap(artwork);
            }else{
                mCenterImage.setImageResource(R.drawable.bg_activity_splash);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void resetSeekBar(){
        try{
            int max = mInterface.getDuration();
            mCircleSeekBar.setMax(max);
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
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mCircleSeekBar.updateProgressBar(mInterface.getCurPosition());
                                        updateSystemTimeFlag ++;
                                        if(updateSystemTimeFlag>=60){
                                            updateSystemTime();
                                            updateSystemTimeFlag = 0;
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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

    private void bind(){
        mConnection = new LockServiceConnection();
        //注意：不能使用intent("xxxx.service");的方式，5.0之后规定服务不能这样启动
        bindService(new Intent(LockScreenActivity.this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        unBind();
        unregisterReceiver(mReceiver);
        if(mTimer!=null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimerTask = null;
            mTimer = null;
        }
        super.onDestroy();
    }

    class MusicChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MusicService.ACTION_NEW_MUSIC)){
                if(mInterface!=null){
                    resetSeekBar();
                    try {
                        updateMusicText();
                        updatePlayPause();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    updateGaosiBackground();
                    updateCenterImage();
                }
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                finish();
            }else if(action.equals(MusicService.ACTION_STOP)){
                try {
                    updatePlayPause();
                    updateGaosiBackground();
                    updateCenterImage();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else if(action.equals(MusicService.ACTION_PAUSE)){
                try {
                    updatePlayPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else if(action.equals(MusicService.ACTION_PLAY)){
                try {
                    updatePlayPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
