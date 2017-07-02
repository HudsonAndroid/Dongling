package com.hudson.donglingmusic.ui.pagerdetail;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.itempager.CategoryItemPager;
import com.hudson.donglingmusic.ui.itempager.HotItemPager;
import com.hudson.donglingmusic.ui.itempager.LocalItemPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/16.
 * 主界面的详情页
 *      内部的标签：热门、本地、分类
 */

public class MusicPagerDetail extends BasePagerDetail {

    private ArrayList<View> mPagers;
    private HotItemPager mHotItemPager;
    private LocalItemPager mLocalItemPager;
    private CategoryItemPager mCategoryItemPager;
    private MusicDetailPagerAdapter mAdapter;


    public MusicPagerDetail(Activity activity, ArrayList<String> data) {
        super(activity, data);
        mPagers = new ArrayList<>();
    }


    @Override
    public void initData() {
        if(mAdapter == null){
            //添加热门标签
            mHotItemPager = new HotItemPager(mActivity);
            mPagers.add(mHotItemPager.mRootView);
            //添加本地标签
            mLocalItemPager = new LocalItemPager(mActivity);
            mPagers.add(mLocalItemPager.mRootView);
            //添加分类标签
            mCategoryItemPager = new CategoryItemPager(mActivity);
            mPagers.add(mCategoryItemPager.mRootView);
            mAdapter = new MusicDetailPagerAdapter();
            mViewPager.setAdapter(mAdapter);
            //将ViewPager与指示器绑定在一起（注意必须在ViewPager设置完数据才能绑定）
            mIndicator.setViewPager(mViewPager);
            configMusicViewPager();
        }
        mHotItemPager.initData();
    }

    class MusicDetailPagerAdapter extends PagerAdapter{

        //指示器的标题是通过ViewPager的这个方法获取的，所以我们需要重写
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mPagers.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    private void configMusicViewPager(){
        final SlidingMenu slidingMenu = ((HomeActivity) mActivity).getSlidingMenu();
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //刷新数据
                if(position == 1){
                    mLocalItemPager.initData();
                }
                if(position == 2){
                    mCategoryItemPager.initData();
                }
                if(position == 0){
                    slidingMenu.setSlidingEnabled(true);
                }else{
                    slidingMenu.setSlidingEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHotItemPager.onDestroy();
        mLocalItemPager.onDestroy();
    }
}
