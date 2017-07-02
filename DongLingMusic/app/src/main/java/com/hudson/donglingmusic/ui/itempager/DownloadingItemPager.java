package com.hudson.donglingmusic.ui.itempager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.DownloadFailOrPauseInfo;
import com.hudson.donglingmusic.net.download.DownloadHelper;
import com.hudson.donglingmusic.net.download.MusicDownloadManager;
import com.hudson.donglingmusic.ui.activity.PlayPageActivity;
import com.hudson.donglingmusic.ui.view.DownloadingItemView;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.hudson.donglingmusic.net.download.MusicDownloadManager.DOWNLOAD_FAIL_OR_PAUSE_DATABASE;

/**
 * Created by Hudson on 2017/4/23.
 * 正在下载
 */

public class DownloadingItemPager implements MusicDownloadManager.DownloadObserver {

    private final MusicDownloadManager mDownloadManager;
    public Activity mActivity;
    public View mRootView;
    private LinearLayout mContainer;
    private ArrayList<DownloadingItemView> mDownloadingItemViews;
    private View mLoadingView;
    private View mEmptyTipView;
    private DownloadReceiver mReceiver;


    public DownloadingItemPager(Activity activity){
        mActivity = activity;
        mDownloadManager = MusicDownloadManager.getInstance();
        mDownloadManager.registerObserver(this);
        mDownloadingItemViews = new ArrayList<>();
        mReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter(PlayPageActivity.DOWNLOAD_TASK_START);
        mActivity.registerReceiver(mReceiver,filter);
        initView();
    }

    public void initView(){
        mRootView = View.inflate(mActivity, R.layout.pager_item_downloading,null);
        mContainer = (LinearLayout) mRootView.findViewById(R.id.ll_downloading_container);
        mLoadingView = mRootView.findViewById(R.id.ll_loading_data);
        mEmptyTipView = mRootView.findViewById(R.id.ll_empty);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public void initData(){
        mContainer.removeAllViews();
        new AsyncTask<Void,Void,Void>(){
            List<DownloadFailOrPauseInfo> downloadFailOrPauseInfos;
            @Override
            protected Void doInBackground(Void... params) {
                //从数据库中读取，看是否存在下载失败或者暂停的项目
                LitePalDB litePalDB = new LitePalDB(DOWNLOAD_FAIL_OR_PAUSE_DATABASE, 1);
                litePalDB.addClassName(DownloadFailOrPauseInfo.class.getName());
                LitePal.use(litePalDB);//切换数据库
                try{
                    downloadFailOrPauseInfos = DataSupport.findAll(DownloadFailOrPauseInfo.class);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mEmptyTipView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.INVISIBLE);
                processData(downloadFailOrPauseInfos);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void processData(List<DownloadFailOrPauseInfo> downloadFailOrPauseInfos){
        DownloadingItemView downloadingItemView;
        if(downloadFailOrPauseInfos!=null&&downloadFailOrPauseInfos.size()!=0){
            mEmptyTipView.setVisibility(View.INVISIBLE);
            for (int i = 0; i < downloadFailOrPauseInfos.size(); i++) {
                final DownloadFailOrPauseInfo downloadFailOrPauseInfo = downloadFailOrPauseInfos.get(i);
                downloadingItemView = new DownloadingItemView(mActivity);
                downloadingItemView.setCurState(downloadFailOrPauseInfo.getCurDownloadState());
                downloadingItemView.setDownloadingTitle(downloadFailOrPauseInfo.getDownloadTitle());
                downloadingItemView.setMax((int)downloadFailOrPauseInfo.getTotalSize());
                final int downloadId = downloadFailOrPauseInfo.getDownloadId();
                downloadingItemView.setDownloadIdTag(downloadId);//绑定该控件与该下载内容
                downloadingItemView.setDownloadControlListener(new DownloadingItemView.DownloadControlListener() {
                    @Override
                    public void onPause() {
                        mDownloadManager.pause(downloadId);
                    }

                    @Override
                    public void onContinue() {
                        mDownloadManager.startDownload(downloadFailOrPauseInfo);
                    }

                    @Override
                    public void onRestart() {
                        mDownloadManager.restartDownload(downloadId);
                    }

                    @Override
                    public void onCancelDownloadClick() {
                        mDownloadManager.cancelDownload(downloadId);
                    }
                });
                mContainer.addView(downloadingItemView,new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mDownloadingItemViews.add(downloadingItemView);
            }
        }
        //读取当前正在下载的项目,下载失败或者暂停的这里不处理
        ConcurrentHashMap<Integer, DownloadHelper> downloadHelperMap = mDownloadManager.getDownloadHelperMap();
        ArrayList<Integer> mDownloadId = mDownloadManager.getDownloadId();
        if(downloadHelperMap!=null&&downloadHelperMap.size()!=0){
            mEmptyTipView.setVisibility(View.INVISIBLE);
            DownloadHelper helper;
            for (int i = 0; i < downloadHelperMap.size(); i++) {
                final int downloadId = mDownloadId.get(i);
                helper = downloadHelperMap.get(downloadId);
                int state = helper.mCurDownloadState;
                //只有是正在下载的才加入
                if(state == MusicDownloadManager.STATE_DOWNLOADING){
                    downloadingItemView = new DownloadingItemView(mActivity);
                    downloadingItemView.setCurState(state);
                    downloadingItemView.setDownloadingTitle(helper.mDownloadTitle);
                    downloadingItemView.setMax((int)helper.mTotalSize);
                    downloadingItemView.setDownloadIdTag(helper.mDownloadId);//绑定该控件与该下载内容
                    downloadingItemView.setDownloadControlListener(new DownloadingItemView.DownloadControlListener() {
                        @Override
                        public void onPause() {
                            mDownloadManager.pause(downloadId);
                        }

                        @Override
                        public void onContinue() {
                            mDownloadManager.continueDownload(downloadId);
                        }

                        @Override
                        public void onRestart() {
                            mDownloadManager.restartDownload(downloadId);
                        }

                        @Override
                        public void onCancelDownloadClick() {
                            mDownloadManager.cancelDownload(downloadId);
                        }
                    });
                    mContainer.addView(downloadingItemView,new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    mDownloadingItemViews.add(downloadingItemView);
                }
            }
        }
    }

    public void onDestroy(){
        mActivity.unregisterReceiver(mReceiver);
    }

    //================下载变化监听==========
    @Override
    public void onDownloadStateChanged(DownloadHelper helper) {
        DownloadingItemView downloadingItemView;
        for (int i = 0; i < mDownloadingItemViews.size(); i++) {
            downloadingItemView = mDownloadingItemViews.get(i);
            if(downloadingItemView.getDownloadIdTag() == helper.mDownloadId){
                downloadingItemView.setCurState(helper.mCurDownloadState);
            }
        }
    }

    @Override
    public void onDownloadProgressUpdate(DownloadHelper helper) {
        DownloadingItemView downloadingItemView;
        for (int i = 0; i < mDownloadingItemViews.size(); i++) {
            downloadingItemView = mDownloadingItemViews.get(i);
            if(downloadingItemView.getDownloadIdTag() == helper.mDownloadId){
                downloadingItemView.setCurProgress((int)helper.mCurDownloadSize,(int)helper.mTotalSize);
            }
        }
    }

    class DownloadReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
}
