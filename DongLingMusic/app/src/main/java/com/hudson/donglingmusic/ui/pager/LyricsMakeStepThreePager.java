package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.TimeUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hudson on 2017/4/1.
 * 本pager是歌词正式制作界面
 *
 * 存在一个问题就是：这个地方修改了进度，但是如果原来就有歌词，那么歌词将会出错
 * 但是由于我们是正式制作歌词，所以没有予以考虑
 * 解决办法也很简单，在playPageActivity的onstart方法中改成playFromPosition即可.但是随即而来有另外一个问题..
 */

public class LyricsMakeStepThreePager extends BaseLyricsMakePager implements View.OnClickListener {
    private boolean mIsStarted = false;//标识是否开始
    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private ArrayList<TextView> mTextViews;
    private int mCurPlayIndex;
    private TextView mCurTextView = null;
    private String[] mLyricsStrs;
    private static final int mLyricsShowCount = 5;//歌词显示的个数
    private int mUnreachedLyricsColor = 0xff358120;
    private int mReachedLyricsColor = 0xff00C853;
    private int mUnreachedTextSize = 17;
    private int mReachededTextSize = 21;
    private IDonglingMusicAidlInterface mInterface;
    private int mSpeedOrSlowTime = 3000;//一次快进或者快退的时长3s
    private ImageView mPlayPause;
    private int mItemHeight;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private SeekBar mSeekBar;


    public void setInterface(IDonglingMusicAidlInterface anInterface) {
        mInterface = anInterface;
        resetSeekBar();
    }

    private void resetSeekBar(){
        try{
            int max = mInterface.getDuration();
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

    public LyricsMakeStepThreePager(Activity context) {
        super(context);
    }

    @Override
    public View initView(Context context) {
        View view = View.inflate(context, R.layout.pager_lyricsmaker_three, null);
        mSeekBar = (SeekBar) view.findViewById(R.id.sb_make_lyrics);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    mInterface.seekTo(seekBar.getProgress());
                    //存在一个问题就是：这个地方修改了进度，但是如果原来就有歌词，那么歌词将会出错
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mScrollView = (ScrollView) view.findViewById(R.id.sl_make_lyrics);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_lyric_container);
        mTextViews = new ArrayList<>();
        view.findViewById(R.id.iv_seek_low).setOnClickListener(this);
        view.findViewById(R.id.iv_last_lyric).setOnClickListener(this);
        view.findViewById(R.id.iv_next_lyric).setOnClickListener(this);
        view.findViewById(R.id.iv_seek_high).setOnClickListener(this);
        view.findViewById(R.id.iv_restart).setOnClickListener(this);
        mPlayPause = ((ImageView) view.findViewById(R.id.iv_play));
        mPlayPause.setOnClickListener(this);
        view.findViewById(R.id.iv_complete).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_seek_low:
                try {
                    if(mInterface.getPlayState()!= MusicService.STATE_IDLE){
                        int computeTime = 0;
                        computeTime = mInterface.getCurPosition() - mSpeedOrSlowTime;
                        int destPosition = computeTime<0?0:computeTime;
                        mInterface.seekTo(destPosition);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_last_lyric://上一句歌词
                try {
                    if (mInterface.isPlaying()&&mIsStarted) {
                        if(mCurTextView!=null){//说明存在上句
                            mCurPlayIndex = mCurPlayIndex - 1;
                            mCurTextView.setTextColor(mUnreachedLyricsColor);
                            mCurTextView.setTextSize(mUnreachedTextSize);
                            TextPaint tp = mCurTextView.getPaint();
                            tp.setFakeBoldText(false);
                        }
                        mTextViews.get(mCurPlayIndex).setTextColor(mUnreachedLyricsColor);
                        int index = mLyricsStrs[mCurPlayIndex].indexOf("]");
                        mLyricsStrs[mCurPlayIndex] = mLyricsStrs[mCurPlayIndex].substring(index + 1);// 重新改为原来的歌词，未加时间标志的
                        mScrollView.smoothScrollTo(0,
                                (mCurPlayIndex-1)*mItemHeight);
                    }else {
                        ToastUtils.showToast("请按下开始键再进行其他操作！");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_next_lyric:
                try {
                    if (mInterface.isPlaying()&&mIsStarted) {
                        if (mCurPlayIndex < mTextViews.size()) {
                            mCurTextView = mTextViews.get(mCurPlayIndex);
                            mCurTextView.setTextColor(mReachedLyricsColor);
                            mCurTextView.setTextSize(mReachededTextSize);
                            TextPaint tp = mCurTextView.getPaint();
                            tp.setFakeBoldText(true);
                            // 由于人具有反应时间，所以减掉260（加上机器反应时间）
                            mLyricsStrs[mCurPlayIndex] = TimeUtils.getTime(mInterface.getCurPosition()
                                    - 260) + mLyricsStrs[mCurPlayIndex];
                            mScrollView.smoothScrollTo(0,mCurPlayIndex*mItemHeight);
                            mCurPlayIndex++;
                        } else {
                            saveLyrics();// 保存歌词文件
                        }
                    }else {
                        ToastUtils.showToast("请按下开始键再进行其他操作！");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_seek_high:
                try {
                    if(mInterface.getPlayState()!= MusicService.STATE_IDLE){
                        int computeTime = 0;
                        computeTime = mInterface.getCurPosition() + mSpeedOrSlowTime;
                        int destPosition = computeTime>mInterface.getDuration()?
                                mInterface.getDuration():computeTime;
                        mInterface.seekTo(destPosition);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_restart:
                restart();
                break;
            case R.id.iv_play:
                if(!mIsStarted){//开始制作歌词
                    restart();
                }else{//播放或者暂停
                    try {
                        if(mInterface.isPlaying()){
                            mPlayPause.setImageResource(R.drawable.make_play);
                            mInterface.pause();
                        }else{
                            mPlayPause.setImageResource(R.drawable.make_pause);
                            mInterface.play(mInterface.getCurMusicInfoIndex());
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.iv_complete://保存歌词
                if (mIsStarted&&mLyricsStrs!=null) {
                    saveLyrics();
                }else {
                    ToastUtils.showToast("您还没有开始制作歌词呢，歌词无法保存的哦！");
                }
                break;
        }
    }

    private onLoadLyricsStringListener mOnLoadLyricsStringListener;
    public void setOnLoadLyricsStringListener(onLoadLyricsStringListener onLoadLyricsStringListener) {
        mOnLoadLyricsStringListener = onLoadLyricsStringListener;
    }

    public interface onLoadLyricsStringListener{
        String onLoadLyricsString();
    }

    private void restart(){
        try {
            if(mOnLoadLyricsStringListener!=null){
                String srcLyrics = mOnLoadLyricsStringListener.onLoadLyricsString();
                if(!TextUtils.isEmpty(srcLyrics)){
                    mInterface.seekTo(0);
                    if(!mInterface.isPlaying()){
                        mInterface.play(mInterface.getCurMusicInfoIndex());
                    }
                    initLrcMaker(srcLyrics);
                    mPlayPause.setImageResource(R.drawable.make_pause);
                    mIsStarted = true;
                }else{
                    ToastUtils.showToast(UIUtils.getString(R.string.lyrics_has_not_modify));
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void initLrcMaker(String lyricsString) {
        mScrollView.removeAllViews();
        mLinearLayout.removeAllViews();
        mTextViews.removeAll(mTextViews);
        mCurPlayIndex = 0;
        mCurTextView = null;
        mItemHeight = mScrollView.getHeight() / mLyricsShowCount;
        // 第二个参数用于设置歌词的起始位置
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mScrollView.getHeight()/2 - mItemHeight/2);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        View startBlankView = new View(mLinearLayout.getContext());
        startBlankView.setLayoutParams(spaceParams);
        mLinearLayout.addView(startBlankView);
        mLyricsStrs = lyricsString.split("\n");
        int length = mLyricsStrs.length;
        if (length != 0) {
            TextView textView;
            for (int i = 0; i < length; i++) {
                textView = new TextView(mLinearLayout.getContext());
                textView.setText(mLyricsStrs[i]);
                textView.setTextSize(mUnreachedTextSize);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(mUnreachedLyricsColor);
                textView.setLayoutParams(textParams);
                mTextViews.add(textView);
                mLinearLayout.addView(textView);
            }
        }
        View endBlankView = new View(mLinearLayout.getContext());
        endBlankView.setLayoutParams(spaceParams);
        mLinearLayout.addView(endBlankView);
        mScrollView.addView(mLinearLayout);
        mScrollView.smoothScrollTo(0, 0);
    }

    /**
     * 清理工作
     */
    public void onDestroy(){
        mScrollView.removeAllViews();
        mLinearLayout.removeAllViews();
        mTextViews.removeAll(mTextViews);
        mTextViews = null;
        if(mTimer!=null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimerTask = null;
            mTimer = null;
        }
    }

    private void saveLyrics() {
        mPlayPause.setImageResource(R.drawable.make_play);
        try {
            if(StorageUtils.saveLyricsFile(mLyricsStrs,mInterface.getCurMusicInfo().getData())){
                DialogUtils.showInformationDialog(mActivity,
                        UIUtils.getString(R.string.lyrics_make_complete_title),
                        UIUtils.getString(R.string.lyrics_make_complete_content),
                        UIUtils.getString(R.string.ok_tip),UIUtils.getString(R.string.cancel_tip),
                        new Runnable(){
                            @Override
                            public void run() {
                                mActivity.finish();
                            }
                        },new Runnable(){
                            @Override
                            public void run() {
                                mActivity.finish();
                            }
                        });
            }else{
                DialogUtils.showInformationDialog(mActivity,
                        UIUtils.getString(R.string.lyrics_make_save_failed_title),
                        UIUtils.getString(R.string.lyrics_make_save_failed_content),
                        UIUtils.getString(R.string.lyrics_make_save_cancel),UIUtils.getString(R.string.lyrics_make_save_again),
                        new Runnable(){
                            @Override
                            public void run() {
                                mActivity.finish();
                            }
                        },new Runnable(){//再次保存
                            @Override
                            public void run() {
                                saveLyrics();
                            }
                        });
            }
            mIsStarted = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
