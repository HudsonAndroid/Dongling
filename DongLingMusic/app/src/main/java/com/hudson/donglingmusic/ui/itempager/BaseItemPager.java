package com.hudson.donglingmusic.ui.itempager;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.view.LetterSelectorView;
import com.hudson.donglingmusic.utils.ToastUtils;

/**
 * Created by Hudson on 2017/3/18.
 * 这是每个标签详情页
 */

public abstract class BaseItemPager {

    public Activity mActivity;
    public View mRootView;
    public SwipeRefreshLayout mSwipeRefreshLayout;//容器，我们的刷新控件
    public RecyclerView mRecyclerView;
    public boolean hasLoadData = false;
    public LetterSelectorView mLetterSelectorView;//侧边a-z控件
    public View mLoadingView;
    public View mEmptyView;
    public View mMoreToolsView;
    public View mLocationView,mSearchView;

    public BaseItemPager(Activity activity){
        mActivity = activity;
        initView();
    }

    public void initView(){
        mRootView = View.inflate(mActivity, R.layout.pager_item_layout,null);
        mLoadingView = mRootView.findViewById(R.id.ll_loading_data);
        mEmptyView = mRootView.findViewById(R.id.ll_empty);
        mMoreToolsView = mRootView.findViewById(R.id.ll_more_tools);
        mLocationView = mRootView.findViewById(R.id.iv_location);
        mSearchView = mRootView.findViewById(R.id.iv_search);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.srl_root);
        mLetterSelectorView = (LetterSelectorView) mRootView.findViewById(R.id.letterView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reLoadData();
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkDataReloadSuccessfullyOrNot();
                    }
                },5000);
            }
        });
        mSwipeRefreshLayout.setProgressViewEndTarget(false,120);//修改下拉多大距离开始刷新（第一个参数true表示下拉过程自动缩放）
        mRecyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.rv_container);
    }

    /**
     * 加载数据，子类必须实现
     */
    public abstract void initData();

    /**
     * 刷新数据（重新加载）
     */
    public abstract void reLoadData();

    /**
     * 检查数据重新加载是否成功
     */
    public void checkDataReloadSuccessfullyOrNot(){
        if(mSwipeRefreshLayout.isRefreshing()){//数据加载失败，5s钟还在加载
            mSwipeRefreshLayout.setRefreshing(false);
            ToastUtils.showToast("数据加载异常！");
        }else{//数据已经加载成功了或者加载失败但是已经被处理了

        }
    }

    public void onDestroy(){

    }

    public boolean isHasLoadData(){
        return  hasLoadData;
    }
}
