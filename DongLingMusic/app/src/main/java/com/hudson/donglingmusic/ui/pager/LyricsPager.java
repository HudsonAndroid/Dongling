package com.hudson.donglingmusic.ui.pager;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.view.LyricsView;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.MusicUtils;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import static com.hudson.donglingmusic.R.id.locateView;

/**
 * Created by Hudson on 2017/3/24.
 * 歌词播放页面
 *
 *  歌词定位的分析：由于scrollView存在惯性滑动，所以我们在点击了从横线位置开始播放的按键后，
 *  我们不能立刻定位，我们需要等待惯性滑动完成，所以需要一个标志位mNeedLocate
 */

public class LyricsPager extends BasePlayPager{
    public LyricsView mLyricsView;
    private View mLocateView;
    private boolean mNeedLocate = false;
    private int mHorizontalLinePosition;
    private View mToolViews;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mLocateView.setVisibility(View.INVISIBLE);
        }
    };
    private MySharePreferences mMySharePreferences;

    public LyricsPager(Context context){
        super(context);
    }

    @Override
    public View initView(Context context) {
        View v = View.inflate(context, R.layout.pager_lyrics,null);
        mLocateView = v.findViewById(locateView);
        mLyricsView = (LyricsView) v.findViewById(R.id.lv_lyrics);
        mMySharePreferences = MySharePreferences.getInstance();
        //修改字体颜色
        mLyricsView.setFocusTextColor(mMySharePreferences.getFocusLyricsColor());
        mLyricsView.setNormalTextColor(mMySharePreferences.getNormalLyricsColor());
        //修改字体大小
        mLyricsView.setFocusTextSize(mMySharePreferences.getFocusLyricsTextSize());
        mLyricsView.setNormalTextSize(mMySharePreferences.getNormalLyricsTextSize());
        //修改显示歌词个数
        mLyricsView.setItemCount(mMySharePreferences.getLyricsShowCount());
        //修改字体类型
        String lyricsFontPath = mMySharePreferences.getFontFilePath();
        if(lyricsFontPath!=null&&!TextUtils.isEmpty(lyricsFontPath)){
            try{
                mLyricsView.setTypeface(Typeface.createFromFile(lyricsFontPath));
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showToast("先前设置的字体出现异常！");
                mMySharePreferences.saveFontFilePath(null);
            }
        }
        v.findViewById(R.id.iv_locate_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mNeedLocate){//防止多次点击
                    if(mLyricsView.isScrolling()){
                        mNeedLocate = true;
                    }else{
                        locateLyrics(mHorizontalLinePosition);
                    }
                }else{
                    ToastUtils.showToast("正在定位歌词，请稍后重试！");
                }
            }
        });
        mLyricsView.setOnScrollEventListener(new LyricsView.OnScrollEventListener() {
            @Override
            public void onScrollMove() {
                if(!mLyricsView.isNoScrollDistance())//如果只有一句歌词或者没有，不显示
                mLocateView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrollUpAfterViewScroll(long screenCenterLrcPosition) {
                mHorizontalLinePosition = (int)screenCenterLrcPosition;
                if(mNeedLocate){
                    locateLyrics(mHorizontalLinePosition);
                }
                mNeedLocate = false;
                mHandler.removeMessages(0);//移除之前所有该消息
                mHandler.sendEmptyMessageDelayed(0,3000);
            }

            @Override
            public void onScrollUp() {
            }
        });
        mToolViews = v.findViewById(R.id.ll_function);
        v.findViewById(R.id.iv_function).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mToolViews.getVisibility() == View.VISIBLE){
                    mToolViews.setVisibility(View.INVISIBLE);
                }else{
                    mToolViews.setVisibility(View.VISIBLE);
                }
            }
        });
        v.findViewById(R.id.iv_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index = mLyricsView.getCurIndex() - 1;
                String positionLyrics = mLyricsView.getPositionLyrics(index);
                if(positionLyrics!=null){
                    DialogUtils.showModifyLyricsDialog(mContext, positionLyrics, new Runnable() {
                        @Override
                        public void run() {
                            mLyricsView.modifyPositionLyrics(index,mMySharePreferences.getModifyLyrics());
                        }
                    });
                }else{
                    ToastUtils.showToast("当前没有处在播放中的歌词！");
                }
            }
        });
        v.findViewById(R.id.iv_slow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adjustOffsetTime = mMySharePreferences.getAdjustOffsetTime();
                mLyricsView.setOffsetTimeAndPlay(-adjustOffsetTime);
                ToastUtils.showToast("歌词快退"+adjustOffsetTime+"毫秒");
            }
        });
        v.findViewById(R.id.iv_accelerate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adjustOffsetTime = mMySharePreferences.getAdjustOffsetTime();
                mLyricsView.setOffsetTimeAndPlay(adjustOffsetTime);
                ToastUtils.showToast("歌词快进"+adjustOffsetTime+"毫秒");
            }
        });
        v.findViewById(R.id.iv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lyricsContent = mLyricsView.getLyricsContent();
                if(TextUtils.isEmpty(lyricsContent)){
                    ToastUtils.showToast("当前歌曲没有歌词哦！");
                }else{
                    ClipboardManager clip = (ClipboardManager) UIUtils.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(lyricsContent);
                    ToastUtils.showToast("歌词文本已复制到剪切板！");
                }
            }
        });
        return v;
    }

    private void locateLyrics(int position){
        try {
            mInterface.seekTo(position);
            mLyricsView.playLyricsFromPosition(position,!mInterface.isPlaying());
            mLocateView.setVisibility(View.INVISIBLE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 由于我们的lyricsView依赖自身宽高，所以必须保证宽高已经测量出来了才加载
     */
    public void initDataAfterMeasured(){
        //监听宽高测量完成
        mLyricsView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//onLayout执行结束的回调方法
                mLyricsView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initData();
            }
        });
    }

    @Override
    public void initData(){
        mLyricsView.clear();
        MusicInfo curMusicInfo = null;
        try {
            curMusicInfo = mInterface.getCurMusicInfo();
            if(curMusicInfo == null)
                return ;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //加载歌词（必须先设置加载歌词监听器，才开始加载歌词)
        mLyricsView.setOnLyricsLoadCompletedListener(new LyricsView.OnLyricsLoadCompletedListener() {
            @Override
            public void onLyricsLoadCompleted() {
                try {
//                    int adjustOffsetTime = mMySharePreferences.getCurLyricsOffset();
//                    mLyricsView.setOffsetTimeNotPlay(adjustOffsetTime);
                    mLyricsView.playLyricsFromPosition(mInterface.getCurPosition(),!mInterface.isPlaying());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mLyricsView.setHelper(new LyricsView.AccurateTimeHelper() {
            @Override
            public long getAccurateTime() {
                try {
                    return mInterface.getCurPosition();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        mLyricsView.loadLyricsAndInitView(new StringBuilder(StorageUtils.getAppLyricsAbsolutePath())
                    .append(MusicUtils.cutName(curMusicInfo)).append(".lrc").toString());
    }

    public void play(){
        try {
            mLyricsView.startPlayLyrics(mInterface.getCurPosition());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        mLyricsView.pauseLyrics();
    }

    public void playFromPosition(){
        try {
            mLyricsView.playLyricsFromPosition(mInterface.getCurPosition(),!mInterface.isPlaying());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        mLyricsView.clear();
    }

    public void moveFocusLyricsToCenter(){
        mLyricsView.moveFocusLyricsToCenter();
    }

}
