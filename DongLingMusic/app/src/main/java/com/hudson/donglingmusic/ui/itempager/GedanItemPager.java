package com.hudson.donglingmusic.ui.itempager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.hudson.donglingmusic.bean.GedanItemBean;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.ui.activity.GedanListActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.GedanRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SongListBottomViewHolder;
import com.hudson.donglingmusic.utils.NetManagerUtils;
import com.hudson.donglingmusic.utils.PermissionUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/6/8.
 * 歌单
 */

public class GedanItemPager extends BaseItemPager {
    private String mCategoryTitle = "全部分类";
    private List<GedanItemBean.ContentBean> mContentBeans;
    private RecyclerView.OnScrollListener mScrollListener;
    private GedanRecyclerViewAdapter mAdapter;
    private boolean mIsLoadingMore = false;//是否正在加载更多
    private int mPageNo = 0;

    public GedanItemPager(Activity activity) {
        super(activity);
        //每一行显示4个子标签
        final GridLayoutManager manager = new GridLayoutManager(mActivity, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if(type == GedanRecyclerViewAdapter.TYPE_NORMAL){//实际内容的情况下是2个
                    return 1;
                }else{//其他,一个item占据一行
                    return 2;
                }
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GedanRecyclerViewAdapter(mActivity);
        mAdapter.setHasStableIds(true);//为每一个item设置一个独特的标识
        mRecyclerView.setAdapter(mAdapter);
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mContentBeans!=null){
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        //注意：getChildCount是获取屏幕上可以看到的控件个数，getItemCount获取的是所有个数
                        if(mHasMore&&!mIsLoadingMore&&manager.findLastVisibleItemPosition() == manager.getItemCount()-1){
                            //已经滑到底部了
                            mAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_LOADING);
                            mPageNo +=2;
                            mIsLoadingMore = true;
                            startGetGedanData(true);
                        }
                    }
                }
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);
        mAdapter.setOnItemClickListener(new GedanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, String  listId) {
                //打开网络歌单页面
                Intent intent = new Intent(mActivity, GedanListActivity.class);
                intent.putExtra("gedanListId",listId);
                mActivity.startActivity(intent);
            }
        });
        mAdapter.setOnChooseCategoryClickListener(new GedanRecyclerViewAdapter.OnChooseCategoryClickListener() {
            @Override
            public void onChooseCategoryClick() {

            }
        });
    }

    @Override
    public void initData() {
        //请求服务器获取数据（可以放到外面，这样可以在网速较慢的情况下能够先显示缓存信息，之后显示网络信息）
        if (!hasLoadData){
            getDataFromServer();
        }
    }

    @Override
    public void reLoadData() {
        mPageNo = 0;//重新从开头加载数据
        getDataFromServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(mScrollListener);
    }

    public void getDataFromServer() {
        if(NetManagerUtils.isNetworkAvailable(mActivity)){
            PermissionUtils.requestPermission(mActivity, Manifest.permission.INTERNET,2,null);
            PermissionUtils.requestPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE,2,null);
            startGetGedanData(false);
        }else{
            ToastUtils.showToast("当前网络不可用！");
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 开始获取网络的歌单信息
     */
    private void startGetGedanData(final boolean isLoadMore) {
        final String url = BMA.GeDan.geDan(mPageNo,10);
        //第三个参数的泛型表示请求返回的数据类型
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        processGedanData(responseInfo.result,isLoadMore);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        mPageNo -=2;
                        if(mPageNo <0){
                            mPageNo =0;
                        }
                        ToastUtils.showToast("数据加载失败！");
                    }
                });
    }

    private boolean mHasMore = false;//是否还有更多数据
    /**
     * 解析网络歌单数据
     * @param jsonResult
     * @param isLoadMore 是否是加载更多
     */
    private void processGedanData(String jsonResult, boolean isLoadMore) {
        if(mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        if(!isLoadMore)//如果是第一次，那么需要新建
            mContentBeans = new ArrayList<>();
        Gson gson = new Gson();
        try{
            GedanItemBean bean = gson.fromJson(jsonResult,GedanItemBean.class);
            if(bean!=null&&bean.getError_code()==BMA.RESPONSE_CODE){
                if(bean.getHavemore() == 1){
                    mHasMore = true;
                }else{
                    mHasMore = false;
                }
                List<GedanItemBean.ContentBean> contents = bean.getContent();
                mContentBeans.addAll(contents);
                if(!isLoadMore){
                    mAdapter.setDatas(mContentBeans);//设置加载完成的数据
                    mAdapter.setCategoryTitle(mCategoryTitle);
                    mLoadingView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    hasLoadData = true;
                }else{//加载更多
                    mAdapter.notifyDataSetChanged();//刷新数据
                    mAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_NONE);
                    if(!mHasMore){
                        mAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_NO_MORE);
                    }
                    mIsLoadingMore = false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            mPageNo -=2;
            if(mPageNo <0){
                mPageNo =0;
            }
            ToastUtils.showToast("加载异常，请稍后重试！");
            //这里的错误一般由需要网络验证的网络导致
            System.out.println("json格式不正确！");
        }
    }

}
