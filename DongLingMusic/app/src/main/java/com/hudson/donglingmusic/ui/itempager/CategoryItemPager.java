package com.hudson.donglingmusic.ui.itempager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.hudson.donglingmusic.bean.CategoryAllSongBean;
import com.hudson.donglingmusic.bean.CategorySongHotBean;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.ui.activity.CategorySongListActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.CategorySongRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.CacheUtils;
import com.hudson.donglingmusic.utils.NetManagerUtils;
import com.hudson.donglingmusic.utils.PermissionUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/3/18.
 * 分类标签
 * <p>
 * 缓存数据我们缓存的是json字符串，不是对象
 */

public class CategoryItemPager extends BaseItemPager {
    private boolean mAllowReadCache = false;//是否允许读取缓存
    public static final int EACH_ROW_COUNT = 4;//每一行显示的子标签个数
    public static final int HOT_CATEGORY_COUNT = 8;//热门歌单分类
    private CategorySongRecyclerViewAdapter mAdapter;

    private ArrayList<String> mHotCategoryTag;

    public CategoryItemPager(Activity activity) {
        super(activity);
        //每一行显示4个子标签
        final GridLayoutManager manager = new GridLayoutManager(mActivity, EACH_ROW_COUNT);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if(type == CategorySongRecyclerViewAdapter.TYPE_CONTENT){//实际内容的情况下是4个
                    return 1;
                }else{//其他是两个
                    return EACH_ROW_COUNT;
                }
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new CategorySongRecyclerViewAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);
//        System.out.println("搜索歌单分类"+BMA.Tag.allSongTags());
//        System.out.println("歌曲分类地址"+BMA.Tag.hotSongTags(8));
//        System.out.println("歌单分类地址"+BMA.GeDan.geDanCategory());
//        System.out.println("纯音乐歌单分类"+BMA.GeDan.geDanByTag("纯音乐",0,10));
        mAdapter.setOnItemClickListener(new CategorySongRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //开启歌单详情
                Intent intent = new Intent(mActivity, CategorySongListActivity.class);
                intent.putExtra("tag",text);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        //请求服务器获取数据（可以放到外面，这样可以在网速较慢的情况下能够先显示缓存信息，之后显示网络信息）
        if (!hasLoadData){
            mLoadingView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);
            getDataFromServer();
        }
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
            startGetHotCategorySongHeaderData();//获取热门分类
            startGetAllCategorySongData();
        }else{
            ToastUtils.showToast("当前网络不可用！");
            mAllowReadCache = true;
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 获取热门歌单分类
     */
    private void startGetHotCategorySongHeaderData(){
        final String hotCategory = BMA.Tag.hotSongTags(HOT_CATEGORY_COUNT);
        //先判断有无缓存,如果有加载缓存
        String cache = CacheUtils.getCache(hotCategory);
        if (!TextUtils.isEmpty(cache)&&mAllowReadCache) {
            processHotCategorySongHeaderData(cache);
        }else{
            XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                    hotCategory, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            processHotCategorySongHeaderData(result);
                            CacheUtils.setCache(hotCategory, result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("获取热门歌曲分类失败" + msg);
                        }
                    });
        }
    }

    /**
     * 解析热门歌单分类
     * @param json
     */
    private void processHotCategorySongHeaderData(String json) {
        try{
            Gson gson = new Gson();
            CategorySongHotBean bean = gson.fromJson(json, CategorySongHotBean.class);
            if(bean!=null){
                List<CategorySongHotBean.TaglistBean> taglist = bean.getTaglist();
                if(taglist != null&&taglist.size() != 0){
                    mHotCategoryTag = new ArrayList<>();
                    for (int i = 0; i < taglist.size(); i++) {
                        mHotCategoryTag.add(taglist.get(i).getTitle());
                    }
                }
                mAdapter.setHotCategory(mHotCategoryTag);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("json格式不正确");
        }
    }

    /**
     * 开始获取网络的所有歌曲分类
     */
    private void startGetAllCategorySongData() {
        final String url = BMA.Tag.allSongTags();
        //先判断有无缓存,如果有加载缓存
        String cache = CacheUtils.getCache(url);
        if (!TextUtils.isEmpty(cache)&&mAllowReadCache) {
            processAllCategoryData(cache);
        } else {
            //第三个参数的泛型表示请求返回的数据类型
            XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                    url, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;//返回的结果
                            processAllCategoryData(result);
                            CacheUtils.setCache(url, result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {

                        }
                    });
        }
    }

    public static final String[] CATEGORY_TITLES= new String[]{"主题","乐器","人声","场景",
        "年代","影视节目","心情","戏曲","流派","语言&地域","风格"};
    /**
     * 解析所有分类歌曲标签数据
     * 这里由于json数据中中文问题,使用gsonFormat会出现问题(使用中文作为类名)，所以自己解析
     */
    private void processAllCategoryData(String jsonData) {
//        System.out.println("进入解析...........");
        if(mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            CategoryAllSongBean allSongBean = new CategoryAllSongBean();
            allSongBean.mErrorCode = jsonObject.getInt("error_code");
            JSONObject tagListObject = jsonObject.getJSONObject("taglist");

            for (int j = 0; j < CATEGORY_TITLES.length; j++) {
                ArrayList<CategoryAllSongBean.CategoryBean> list = new ArrayList<>();
                JSONArray jsonArray = tagListObject.getJSONArray(CATEGORY_TITLES[j]);
                CategoryAllSongBean.CategoryBean categoryBean;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    categoryBean = allSongBean.new CategoryBean();
                    categoryBean.hot = json.getInt("hot");
                    categoryBean.title = json.getString("title");
                    categoryBean.tagFrom = json.getInt("tag_from");
                    list.add(categoryBean);
                }
                switch (j){
                    case 0:
                        allSongBean.mZhuti = list;
                        break;
                    case 1:
                        allSongBean.mYueqi = list;
                        break;
                    case 2:
                        allSongBean.mRensheng = list;
                        break;
                    case 3:
                        allSongBean.mChangjing = list;
                        break;
                    case 4:
                        allSongBean.mNiandai = list;
                        break;
                    case 5:
                        allSongBean.mYingshi = list;
                        break;
                    case 6:
                        allSongBean.mXinqing = list;
                        break;
                    case 7:
                        allSongBean.mXiqu = list;
                        break;
                    case 8:
                        allSongBean.mLiupai = list;
                        break;
                    case 9:
                        allSongBean.mYuyan = list;
                        break;
                    case 10:
                        allSongBean.mFengge = list;
                        break;
                }
            }
            mAdapter.setCategoryAllSongBean(allSongBean);
            mLoadingView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);
            mAllowReadCache = true;
            hasLoadData = true;
        }catch (JSONException e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
            mEmptyView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.INVISIBLE);
            //这里的错误一般由需要网络验证的网络导致
            System.out.println("json格式不正确！");
        }
    }

}
