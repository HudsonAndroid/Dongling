package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;

import com.hudson.donglingmusic.ui.pagerdetail.MusicPagerDetail;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/15.
 * 音乐页面
 *
 * 我们需要往mRootView中添加我们的自己数据
 *
 * 访问网络数据
 *      使用开源框架XUtils（该框架也可以用于下载）
 *          注意：由于23之后httpClient被去除，所以需要使用jar包
 * 解析网络数据
 *      利用gson。关键点：gson.fromJson(json,对应的对象);
 */

public class MusicPager extends BasePager {

    public MusicPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        if(mBasePagerDetail == null){
            //给帧布局填充我们拥有自己特性的内容
            ArrayList<String> mTitles = new ArrayList<>();
            mTitles.add("热门");
            mTitles.add("本地");
            mTitles.add("分类");
            mBasePagerDetail = new MusicPagerDetail(mActivity, mTitles);
            mContainer.addView(mBasePagerDetail.mRootView);
        }
        mBasePagerDetail.initData();
//        //先判断有无缓存,如果有加载缓存
//        String cache = CacheUtils.getCache(Constants.NEWS_URL);
//        if(!TextUtils.isEmpty(cache)){
//            processData(cache);
//        }else{
//            //请求服务器获取数据（可以放到外面，这样可以在网速较慢的情况下能够先显示缓存信息，之后显示网络信息）
//            readMyGedanData();
//        }
    }


    /**
     * 获取内部ViewPager的页面id
     * @return
     */
    @Override
    public int getInnerViewPagerCurPosition() {
        if(mBasePagerDetail!=null){
            return mBasePagerDetail.mViewPager.getCurrentItem();
        }
        return super.getInnerViewPagerCurPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBasePagerDetail.onDestroy();
    }
}
