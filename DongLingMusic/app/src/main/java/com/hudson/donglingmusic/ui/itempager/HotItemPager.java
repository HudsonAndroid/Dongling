package com.hudson.donglingmusic.ui.itempager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.hudson.donglingmusic.bean.BMABillBoardBean;
import com.hudson.donglingmusic.bean.HeaderPic;
import com.hudson.donglingmusic.bean.HotRecyclerViewItemMusic;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.ui.activity.SongListActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.HotItemRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.CacheUtils;
import com.hudson.donglingmusic.utils.NetManagerUtils;
import com.hudson.donglingmusic.utils.PermissionUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 * 热门标签
 * <p>
 * 缓存数据我们缓存的是json字符串，不是对象
 */

public class HotItemPager extends BaseItemPager {
    private ArrayList<HotRecyclerViewItemMusic> mHotRecyclerViewItemMusics;
    private HotItemRecyclerViewAdapter mAdapter;
    public static final int HEADER_VIEWPAGER_COUNT = 7;//加载轮播条的图片个数
    private ArrayList<String> results;
    private int billBoardCount;
    private boolean mAllowReadCache = false;//是否允许读取缓存

    public HotItemPager(Activity activity) {
        super(activity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mAdapter = new HotItemRecyclerViewAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new HotItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //开启歌单详情
                Intent intent = new Intent(mActivity, SongListActivity.class);
                intent.putExtra("urlIndex",position);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        //请求服务器获取数据（可以放到外面，这样可以在网速较慢的情况下能够先显示缓存信息，之后显示网络信息）
        if (!hasLoadData)
            getDataFromServer();
    }

    @Override
    public void reLoadData() {
        mAllowReadCache = false;
        getDataFromServer();
    }

    public void getDataFromServer() {
        if(NetManagerUtils.isNetworkAvailable(mActivity)){
            PermissionUtils.requestPermission(mActivity, Manifest.permission.INTERNET,2,null);
            PermissionUtils.requestPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE,2,null);

            billBoardCount = BMA.Billboard.billBoards.length;//需要获取的个数
            results = new ArrayList<>();
            for (int i = 0; i < billBoardCount; i++) {
                results.add(null);
                //这里设置了每次获取三个,参数一：歌单index,参数二：加载起始值，参数三：加载数目
                //这里百度好像发现有人用了他的服务器，把第一个数据的offset弄了一下，原来是size=6,现在只能获取3个数据，所以改为6
//                System.out.println("地址"+BMA.Billboard.billSongList(BMA.Billboard.billBoards[i],0,6));
                startGetNetBillBoardData(i,BMA.Billboard.billSongList(BMA.Billboard.billBoards[i],0,6));
            }
            startGetNetHeadPicData();
        }else{
            ToastUtils.showToast("当前网络不可用！");
            mAllowReadCache = true;
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 开始获取网络的榜单数据
     * @param url
     */
    private void startGetNetBillBoardData(final int index,final String url) {
//        System.out.println("歌单的地址"+url);
        //先判断有无缓存,如果有加载缓存
        String cache = CacheUtils.getCache(url);
//        System.out.println("为空？"+TextUtils.isEmpty(cache)+"允许读取"+mAllowReadCache);
        if (!TextUtils.isEmpty(cache)&&mAllowReadCache) {
//            results.add(cache);
            results.set(index,cache);
            if(checkComplete()){
                processBillBoardData();
            }
        } else {
            //第三个参数的泛型表示请求返回的数据类型
            XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                    url, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
//                            System.out.println("数据获取成功");
                            String result = responseInfo.result;//返回的结果
//                            results.add(index,result);
                            results.set(index,result);
                            CacheUtils.setCache(url, result);
                            if(checkComplete()){//加载完成了，可以设置数据了
                                processBillBoardData();
//                                System.out.println("数据加载成功了");
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            //加载失败,那就不加载这部分数据了
//                            System.out.println("数据获取失败");
                            billBoardCount--;
                        }
                    });
        }

    }

    /**
     * 检查网络榜单数据是否获取完毕
     * @return
     */
    private boolean checkComplete() {
        int contentCount = 0;//拥有数据的个数
        for(String s:results){
            if(s!=null){
                contentCount ++;
            }
        }
        if(contentCount == billBoardCount){
            return true;
        }
        return false;
    }

    /**
     * 解析榜单数据
     */
    private void processBillBoardData() {
        if(mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        mHotRecyclerViewItemMusics = new ArrayList<>();
        if(results.size()!=0){
            Gson gson = new Gson();
            for (int i = 0; i < results.size(); i++) {
                try{
                    BMABillBoardBean bean = gson.fromJson(results.get(i),BMABillBoardBean.class);
                    if(bean!=null){
                        //把解析出来的数据传给mHotItemMusics
                        HotRecyclerViewItemMusic hotRecyclerViewItemMusic = new HotRecyclerViewItemMusic(bean.getBillboard().getName());
                        ArrayList<HotRecyclerViewItemMusic.SongInfo> infos = new ArrayList<>();
                        HotRecyclerViewItemMusic.SongInfo info;
                        ArrayList<BMABillBoardBean.SongListBean> list = bean.getSong_list();
                        BMABillBoardBean.SongListBean item;
                        for (int j = 0; j < list.size(); j++) {
                            item = list.get(j);
                            info = hotRecyclerViewItemMusic.new SongInfo(item.getPic_small(), item.getAuthor(), item.getTitle());
                            infos.add(info);
                        }
                        hotRecyclerViewItemMusic.mSongInfos = infos;
                        mHotRecyclerViewItemMusics.add(hotRecyclerViewItemMusic);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.showToast("加载异常，请稍后重试！");
                    //这里的错误一般由需要网络验证的网络导致
                    System.out.println("json格式不正确！");
                }
            }
        }else{
            ToastUtils.showToast("数据加载失败，请检查网络配置！");
        }
        mAdapter.setDatas(mHotRecyclerViewItemMusics);
        mAllowReadCache = true;
        hasLoadData = true;
    }

    private void startGetNetHeadPicData(){
        final String picUrl = BMA.focusPic(HEADER_VIEWPAGER_COUNT);
        System.out.println("头布局地址"+picUrl);
        //先判断有无缓存,如果有加载缓存
        String cache = CacheUtils.getCache(picUrl);
        if (!TextUtils.isEmpty(cache)&&mAllowReadCache) {
            processHeaderData(cache);
        }else{
            XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                    picUrl, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            processHeaderData(result);
                            CacheUtils.setCache(picUrl, result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("获取图片失败" + msg);
                        }
                    });
        }
    }

    private void processHeaderData(String json) {
        try{
            Gson gson = new Gson();
            HeaderPic pic = gson.fromJson(json, HeaderPic.class);
            if(pic!=null&&pic.pic!=null){
                ArrayList<String> urls = new ArrayList<>();
                for (int i = 0; i < pic.pic.size(); i++) {
                    urls.add(pic.pic.get(i).randpic);
                }
                mAdapter.setViewPagerPicUrls(urls);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("json格式不正确");
        }
    }

}
