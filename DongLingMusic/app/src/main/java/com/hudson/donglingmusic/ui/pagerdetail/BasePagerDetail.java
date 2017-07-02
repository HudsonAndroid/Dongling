package com.hudson.donglingmusic.ui.pagerdetail;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/16.
 * 页面详情基类(包含指示器的，每一个都含有ViewPager)
 * 注意：
 *      1.使用指示器需要设置activity的主题（如果觉得库的字体颜色啥的不好看，可以自行修改）
 *      2.需要先使得ViewPager设置数据，即设置adapter，后才让指示器绑定ViewPager
 *      3.指示器标题是通过重写PagerAdapter的getPageTitle来设置
 *      4.使用指示器时如果需要监听onPageChangeListener需要使用指示器的
 *
 * 我们的侧边栏需要在page=0的时候启用，在page!=0时禁用
 */

public class BasePagerDetail {
    public Activity mActivity;
    public ViewPager mViewPager;
    public TabPageIndicator mIndicator;
    public View mRootView;//主布局
    public ArrayList<String> mTitles;//数据

    public BasePagerDetail(Activity activity,ArrayList<String> data){
        mActivity = activity;
        mTitles = data;
        mRootView = initView();
    }

    public View initView(){
        View view = View.inflate(mActivity, R.layout.pager_base_detail_layout,null);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_pager_detail);
        mIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        return view;
    }

    //初始化数据
    public void initData(){

    }

    public void onDestroy(){

    }
}
