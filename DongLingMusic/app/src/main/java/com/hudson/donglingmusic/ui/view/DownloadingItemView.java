package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.net.download.MusicDownloadManager;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/4/23.
 */

public class DownloadingItemView extends LinearLayout {
    private TextView mDownloadingTitle;
    private View mDownloadingView;
    private TextView mPauseView;
    private TextView mDownloadSize;
    private ProgressBar mDownloadingProgressbar;
    private int mDownloadIdTag = -1;//用于区分
    private int mCurState;

    public int getCurState() {
        return mCurState;
    }

    public void setCurState(final int curState) {
        mCurState = curState;
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                switch (curState){
                    case MusicDownloadManager.STATE_NONE:
                        break;
                    case MusicDownloadManager.STATE_DOWNLOADING:
                        mDownloadingView.setVisibility(VISIBLE);
                        mPauseView.setVisibility(INVISIBLE);
                        break;
                    case MusicDownloadManager.STATE_PAUSE:
                        mDownloadingView.setVisibility(INVISIBLE);
                        mPauseView.setVisibility(VISIBLE);
                        mPauseView.setText(UIUtils.getString(R.string.downloading_pause_tip));
                        break;
                    case MusicDownloadManager.STATE_WAITING:
                        mDownloadingView.setVisibility(INVISIBLE);
                        mPauseView.setVisibility(VISIBLE);
                        mPauseView.setText(UIUtils.getString(R.string.downloading_waiting));
                        break;
                    case MusicDownloadManager.STATE_SUCCESSFULLY:
                        mDownloadingView.setVisibility(INVISIBLE);
                        mPauseView.setVisibility(VISIBLE);
                        mPauseView.setText(UIUtils.getString(R.string.downloading_successfully));
                        break;
                    case MusicDownloadManager.STATE_FAILED:
                        mDownloadingView.setVisibility(INVISIBLE);
                        mPauseView.setVisibility(VISIBLE);
                        mPauseView.setText(UIUtils.getString(R.string.downloading_failed_tip));
                        break;
                }
            }
        });
    }


    public DownloadingItemView(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.item_downloading,null);
        mDownloadingView = view.findViewById(R.id.ll_downloading_progress);
        mDownloadSize = (TextView) view.findViewById(R.id.tv_downloading_size);
        mPauseView = (TextView) view.findViewById(R.id.tv_pause_tip);
        view.findViewById(R.id.ll_downloading_item_root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurState == MusicDownloadManager.STATE_DOWNLOADING){//暂停
                    if(mDownloadControlListener !=null){
                        mDownloadControlListener.onPause();
                    }
                    mDownloadingView.setVisibility(INVISIBLE);
                    mPauseView.setVisibility(VISIBLE);
                    mCurState = MusicDownloadManager.STATE_PAUSE;
                }else if(mCurState == MusicDownloadManager.STATE_PAUSE){
                    if(mDownloadControlListener !=null){
                        mDownloadControlListener.onContinue();
                    }
                    mDownloadingView.setVisibility(VISIBLE);
                    mPauseView.setVisibility(INVISIBLE);
                    mCurState = MusicDownloadManager.STATE_DOWNLOADING;
                }else if(mCurState == MusicDownloadManager.STATE_FAILED){
                    if(mDownloadControlListener !=null){
                        mDownloadControlListener.onRestart();
                    }
                    mDownloadingView.setVisibility(VISIBLE);
                    mPauseView.setVisibility(INVISIBLE);
                    mCurState = MusicDownloadManager.STATE_DOWNLOADING;
                }
            }
        });
        mDownloadingTitle = (TextView) view.findViewById(R.id.tv_downloading_title);
        mDownloadingProgressbar = (ProgressBar) view.findViewById(R.id.pb_download);
        view.findViewById(R.id.iv_delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDownloadControlListener!=null){
                    mDownloadControlListener.onCancelDownloadClick();
                }
            }
        });
        addView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setDownloadingTitle(final String downloadingTitle) {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mDownloadingTitle.setText(downloadingTitle);
            }
        });
    }

    public void setCurProgress(final int progress, final int totalSize){
        mDownloadingProgressbar.setProgress(progress);
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mDownloadSize.setText(new StringBuilder(Formatter.formatFileSize(UIUtils.
                        getContext(),progress)).append("/").
                        append(Formatter.formatFileSize(UIUtils.getContext(),totalSize)));
            }
        });
    }

    public void setMax(int max){
        mDownloadingProgressbar.setMax(max);
    }

    public int getDownloadIdTag() {
        return mDownloadIdTag;
    }

    public void setDownloadIdTag(int downloadIdTag) {
        mDownloadIdTag = downloadIdTag;
    }

    private DownloadControlListener mDownloadControlListener;

    public DownloadControlListener getDownloadControlListener() {
        return mDownloadControlListener;
    }

    public void setDownloadControlListener(DownloadControlListener downloadListener) {
        mDownloadControlListener = downloadListener;
    }

    public interface DownloadControlListener{
        void onPause();
        void onContinue();
        void onRestart();
        void onCancelDownloadClick();
    }
}
