package com.hudson.donglingmusic.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.Lyrics;
import com.hudson.donglingmusic.global.MySharePreferences;

import java.util.ArrayList;

import static com.hudson.donglingmusic.service.MusicService.REMOTE_NEXT;
import static com.hudson.donglingmusic.service.MusicService.REMOTE_PLAY_PAUSE;
import static com.hudson.donglingmusic.service.MusicService.REMOTE_PRE;

/**
 * Created by Hudson on 2017/5/2.
 *
 * wm.updateViewLayout(view, params);是用来更新windowmanager上控件的位置的
 */

public class DeskTopLyricsUtils {

    private static DeskTopLyricsUtils instance;
    private MySharePreferences mMySharePreferences;

    /**
     * 单例模式
     * @return
     */
    public static DeskTopLyricsUtils getInstance(){
        synchronized (DeskTopLyricsUtils.class) {
            if(instance==null){
                instance = new DeskTopLyricsUtils();
            }
        }
        return instance;
    }

    /*
	 * 窗体管理者
	 */
    private  WindowManager wm;
    /*
     * 窗体的参数
     */
    private  WindowManager.LayoutParams params = new WindowManager.LayoutParams();

    private  boolean isTouched = false;

    private View view;

    private TextView mPlayLyrics;

    private View mControlView;

    private ImageView mPlayPause;

    private ArrayList<Lyrics> mLyrics;

    public void showDeskTopLyrics(final Context context, String lyricsPath) {
        mMySharePreferences = MySharePreferences.getInstance();
        if(isViewShowed()){//如果当前已经显示了一个，那就移除
            hide();
        }
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型,重要
        params.format=1;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // 不能抢占聚焦点

        params.alpha = 1.0f;
        int x = mMySharePreferences.getDesktopLyricsLocationX();
        int y = mMySharePreferences.getDesktopLyricsLocationY();
        if(x!=-1&&y!=-1){
            params.x = x;
            params.y = y;
        }else{
            // 对齐方式
            params.x = 100;
            params.y = 300;
        }
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        view = View.inflate(context, R.layout.desktop_lyrics_layout,null);
        mControlView = view.findViewById(R.id.ll_desktop_control);
        mControlView.setVisibility(View.GONE);
        view.findViewById(R.id.iv_lyrics_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(new Intent(REMOTE_PRE));
            }
        });
        view.findViewById(R.id.iv_lyrics_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(new Intent(REMOTE_NEXT));
            }
        });
        mPlayPause = (ImageView) view.findViewById(R.id.iv_lyrics_play_pause);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(new Intent(REMOTE_PLAY_PAUSE));
            }
        });
        view.findViewById(R.id.iv_desktop_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMySharePreferences.saveShowDeskTopLyrics(false);
                hide();
            }
        });
        mPlayLyrics = (TextView) view.findViewById(R.id.tv_desktop_lyrics);
        //修改字体类型
        String lyricsFontPath = mMySharePreferences.getFontFilePath();
        if(lyricsFontPath!=null&&!TextUtils.isEmpty(lyricsFontPath)){
            try{
                mPlayLyrics.setTypeface(Typeface.createFromFile(lyricsFontPath));
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showToast("先前设置的字体出现异常！");
                mMySharePreferences.saveFontFilePath(null);
            }
        }
        mPlayLyrics.setTextColor(mMySharePreferences.getFocusLyricsColor());
        mPlayLyrics.setTextSize(mMySharePreferences.getFocusLyricsTextSize());
        wm.addView(view, params);
        view.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isTouched){
                    isTouched = true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指按下
                        showControlView();
                        event.getRawX();
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        showControlView();
                        int dx = (int) (event.getRawX() - startX);
                        int dy = (int) (event.getRawY() - startY);
                        params.x += dx;
                        params.y += dy;
                        // 超出边界修正
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (wm.getDefaultDisplay().getWidth() - view
                                .getWidth())) {
                            params.x = wm.getDefaultDisplay().getWidth()
                                    - view.getWidth();
                        }
                        if (params.y > (wm.getDefaultDisplay().getHeight() - view
                                .getHeight())) {
                            params.y = wm.getDefaultDisplay().getHeight()
                                    - view.getHeight();
                        }
                        wm.updateViewLayout(view, params);
                        // 重复第一步的操作 ，重新初始化手指的开始位置。
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mHandler.sendEmptyMessageDelayed(2,3000);
                        break;
                }
                return true;//如果true那么将无法响应onclick事件
            }
        });
        initLyrics(lyricsPath);
    }

    private void showControlView(){
        mHandler.removeMessages(2);
        mControlView.setVisibility(View.VISIBLE);
    }

    private void updatePlayPause(boolean isPlay){
        if(isPlay){
            mPlayPause.setImageResource(R.drawable.widget_pause);
        }else{
            mPlayPause.setImageResource(R.drawable.widget_play);
        }
    }

    public interface onLyricsLoadCompleteListener{
        void onLyricsLoadComplete();
    }
    private onLyricsLoadCompleteListener mOnLoadCompleteListener;
    public void setmOnLoadCompleteListener(onLyricsLoadCompleteListener mOnLoadCompleteListener) {
        this.mOnLoadCompleteListener = mOnLoadCompleteListener;
    }

    private void initLyrics(String lyricsPath){
        if(mLyrics!=null){
            mLyrics.clear();
            curIndex = -1;
        }
        mLyrics = new LyricsDecodeUtils().readLRC(lyricsPath);
        if(mOnLoadCompleteListener!=null){
            mOnLoadCompleteListener.onLyricsLoadComplete();
        }
    }

    /**
     * 校准歌词时间
     * @param time
     */
    public void calibrationLyricsTime(int time,boolean isPause){
        if(mLyrics!=null&&mLyrics.size()!=0){
            mPlayLyrics.setText("");//已经获取到歌词了
            startPlayLyrics(time,isPause);
        }else{
            mPlayLyrics.setText(UIUtils.getString(R.string.no_lrc_show_after));
        }
        updatePlayPause(!isPause);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if(mLyrics!=null&&mLyrics.size()>0){
                        Lyrics lyrics = mLyrics.get(curIndex);
                        mPlayLyrics.setText(lyrics.getLrcStr());
                        curIndex++;
                        if(curIndex<mLyrics.size()){
                            mHandler.sendEmptyMessageDelayed(0, mLyrics.get(curIndex).getDelayTime());
                        }
                    }
                    break;
                case 2://隐藏控制
                    mControlView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private int curIndex = -1;

    private void startPlayLyrics(int time,boolean isPause){
        mHandler.removeCallbacksAndMessages(null);
        //检查当前位置所处的index，用来设置curIndex
        if(mLyrics.size()==1){//只有一句歌词情况下，curIndex始终为0
            mPlayLyrics.setText(mLyrics.get(0).getLrcStr());
        }
        for(int i=0;i<mLyrics.size();i++){
            if(time<mLyrics.get(0).getLrcTime()){//第一句歌词还没播放
                if(isPause){
                    return;
                }
                curIndex = 0;
                mHandler.sendEmptyMessageDelayed(0, mLyrics.get(curIndex).getLrcTime()-time);
                return ;
            }
            if(time>=mLyrics.get(mLyrics.size()-1).getLrcTime()){//最后一句歌词播放完了
                curIndex = mLyrics.size()-1;
                mPlayLyrics.setText(mLyrics.get(curIndex).getLrcStr());
                return ;
            }
            if(time == mLyrics.get(i).getLrcTime()){//刚好是这个位置，那么需要特殊处理
                curIndex = i;
                mPlayLyrics.setText(mLyrics.get(curIndex).getLrcStr());
                if(!isPause){
                    curIndex++;
                    mHandler.sendEmptyMessageDelayed(0, mLyrics.get(curIndex).getDelayTime());
                }
                return ;
            }
            if(time>mLyrics.get(i).getLrcTime()&&time<mLyrics.get(i+1).getLrcTime()){
                curIndex = i;
                mPlayLyrics.setText(mLyrics.get(curIndex).getLrcStr());
                if(!isPause){
                    curIndex++;
                    mHandler.sendEmptyMessageDelayed(0, mLyrics.get(curIndex).getLrcTime() - time);
                }
                return ;
            }
        }
    }

    public void stopDesktopLyrics(){
        updatePlayPause(false);
        mPlayLyrics.setText(UIUtils.getString(R.string.no_lrc_show_after));
    }

    public void pauseDesktopLyrics(){
        updatePlayPause(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void restartDesktopLyricsPlay(int time){
        updatePlayPause(true);
        if(mLyrics!=null&&mLyrics.size()!=0){
            startPlayLyrics(time,false);
        }
    }

    /**
     * 隐藏自定义吐司 注意：windowManager不是一个容器ViewGroup,所以无法实现动画
     * 具体参考：http://blog.csdn.net/wangjinyu501/article/details/38847611
     * 这里使用了系统的动画
     */
    public void hide() {
        if (wm != null) {
            if (view != null) {
                mMySharePreferences.saveDesktopLyricsLocationX(params.x);
                mMySharePreferences.saveDesktopLyricsLocationY(params.y);
                wm.removeView(view);
                view = null;
            }
            if (view == null) {
                wm = null;
                params.x = 0;
                isTouched = false;
            }
        }
    }

    /**
     * 判断toast是否还有显示
     * @return	true表示还有显示，需要手动hide
     */
    public  boolean isViewShowed(){
        if(view!=null||wm!=null){
            return true;
        }else {
            return false;
        }
    }

}
