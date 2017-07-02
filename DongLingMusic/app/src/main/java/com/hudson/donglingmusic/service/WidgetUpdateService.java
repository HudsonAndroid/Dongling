package com.hudson.donglingmusic.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.widget.DeskTopWidget;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import static com.hudson.donglingmusic.service.MusicService.REMOTE_NEXT;
import static com.hudson.donglingmusic.service.MusicService.REMOTE_PLAY_PAUSE;
import static com.hudson.donglingmusic.service.MusicService.REMOTE_PRE;

/**
 * Created by Hudson on 2017/5/2.
 */

public class WidgetUpdateService extends Service {

    private AppWidgetManager mWidgetManager;//用于更新桌面控件
    private ComponentName mProvider;
    private PendingIntent mPlayModeChangePIntent;
    private PendingIntent mPlayPausePIntent;
    private PendingIntent mNextPIntent;
    private PendingIntent mPrePIntent;
    private PendingIntent mPendingIntent;
    private RemoteViews mRemoteViews;
    private MySharePreferences mInstance;
    private int mMusicBgDimension;//px
    private MusicStateChangeReceiver mReceiver;
    public static final String ACTION_REMOTE_PLAY_MODE_CHANGE = "com.hudson.donglingmusic.remote_mode";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 初始化操作
    @Override
    public void onCreate() {
        System.out.println("服务创建了");
        mReceiver = new MusicStateChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_PAUSE);
        filter.addAction(MusicService.ACTION_STOP);
        filter.addAction(MusicService.ACTION_PLAY);
        filter.addAction(MusicService.ACTION_NEW_MUSIC);
        filter.addAction(MusicService.ACTION_PLAY_MODE_CHANGE);
        registerReceiver(mReceiver,filter);
        mWidgetManager = AppWidgetManager.getInstance(WidgetUpdateService.this);
        mMusicBgDimension = (int)MusicService.dp2px(UIUtils.getDimension(R.dimen.widget_music_bg_dimension),this);
        mInstance = MySharePreferences.getInstance();
        Intent intent = new Intent(UIUtils.getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 如果只是指定他就不行，他会把原来的消灭，新建一个HomeActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 指定为单例模式，就是原来有就用原来的。
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);// 第二次操作会把第一次的覆盖掉

        Intent shuffleModeIntent = new Intent();// 修改播放模式
        shuffleModeIntent.setAction(ACTION_REMOTE_PLAY_MODE_CHANGE);
        mPlayModeChangePIntent = PendingIntent.getBroadcast(this, 0,
                shuffleModeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent();// 暂停 播放
        pauseIntent.setAction(REMOTE_PLAY_PAUSE);
        mPlayPausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent();// 下一曲
        nextIntent.setAction(REMOTE_NEXT);
        mNextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent preIntent = new Intent();// 上一曲
        preIntent.setAction(REMOTE_PRE);
        mPrePIntent = PendingIntent.getBroadcast(this, 0, preIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 设置更新的组件
        mProvider = new ComponentName(WidgetUpdateService.this, DeskTopWidget.class);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.widget_desktop_layout);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_pre, mPrePIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_next, mNextPIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_play_pause, mPlayPausePIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_play_mode, mPlayModeChangePIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.rl_widget, mPendingIntent);
        //一被创建就刷新
        updatePlayModeImage();
        updatePlayPauseImage();
        updateMusicBgImage();
        commit();
        super.onCreate();
    }


    /**
     * 刷新桌面控件上的播放模式图片
     * 需要调用commit
     */
    private void updatePlayModeImage(){
        switch (mInstance.getPlayMode()){
            case MusicService.MODE_LIST_LOOP:
                mRemoteViews.setImageViewResource(R.id.iv_play_mode,R.drawable.widget_list_loop);
                break;
            case MusicService.MODE_LIST_ORDER:
                mRemoteViews.setImageViewResource(R.id.iv_play_mode,R.drawable.widget_list_order);
                break;
            case MusicService.MODE_LIST_SHUFFLE:
                mRemoteViews.setImageViewResource(R.id.iv_play_mode,R.drawable.widget_list_shuffle);
                break;
            case MusicService.MODE_ONE_LOOP:
                mRemoteViews.setImageViewResource(R.id.iv_play_mode,R.drawable.widget_one_list);
                break;
        }
    }

    /**
     * 刷新播放图片
     * 需要调用commit
     */
    private void updatePlayPauseImage(){
        if(mInstance.getPlayerIsPlaying()){
            mRemoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.widget_pause);
        }else{
            mRemoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.widget_play);
        }
    }

    /**
     * 刷新音乐图片
     * 需要调用commit
     */
    private void updateMusicBgImage(){
        Bitmap artwork = BitmapMusicUtils.getArtwork(mInstance.getCurMusicTitle(), mInstance.getCurMusicAlbumId(), mMusicBgDimension, mMusicBgDimension);
        if(artwork != null){
            mRemoteViews.setImageViewBitmap(R.id.iv_music_bg,
                    artwork);
        }else{
            mRemoteViews.setImageViewResource(R.id.iv_music_bg,R.drawable.icon_player);
        }
    }

    /**
     * 提交刷新的内容
     */
    private void commit(){
        mWidgetManager.updateAppWidget(mProvider,mRemoteViews);// 一旦收到了广播我就更新
    }


    // 销毁操作
    @Override
    public void onDestroy() {
        System.out.println("修改widget的播放图标");
        mRemoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.widget_play);
        commit();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    class MusicStateChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MusicService.ACTION_PAUSE)|action.equals(MusicService.ACTION_PLAY)){
                updatePlayPauseImage();
                commit();
            }else if(action.equals(MusicService.ACTION_NEW_MUSIC)){
                updatePlayPauseImage();
                updateMusicBgImage();
                commit();
            }else if(action.equals(MusicService.ACTION_STOP)){
                updatePlayPauseImage();
                updateMusicBgImage();
                commit();
            }else if(action.equals(MusicService.ACTION_PLAY_MODE_CHANGE)){
                updatePlayModeImage();
                commit();
            }
        }
    }


}
