package com.hudson.donglingmusic.ui.pagerdetail;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.ui.itempager.GedanItemPager;
import com.hudson.donglingmusic.ui.itempager.MyGedanItemPager;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/17.
 *
 * 我的最爱页面详情
 *      内部标签:最爱、歌单
 */

public class FavoritePagerDetail extends BasePagerDetail{
    public ArrayList<View> mPagers;
    private FavoritePagerAdapter mAdapter;
    private GedanItemPager mGedanItemPager;
    private MyGedanItemPager mMyGedanItemPager;

    public FavoritePagerDetail(Activity activity, ArrayList<String> data) {
        super(activity, data);
        mPagers = new ArrayList<>();
    }

    @Override
    public void initData() {
        if(mAdapter == null){
            mMyGedanItemPager = new MyGedanItemPager(mActivity);
            mMyGedanItemPager.initData();
            mPagers.add(mMyGedanItemPager.mRootView);
            mGedanItemPager = new GedanItemPager(mActivity);
            mPagers.add(mGedanItemPager.mRootView);
            mAdapter = new FavoritePagerAdapter();
            mViewPager.setAdapter(mAdapter);
            //将ViewPager与指示器绑定在一起（注意必须在ViewPager设置完数据才能绑定）
            mIndicator.setViewPager(mViewPager);
            configMusicViewPager();
        }
    }

    private void configMusicViewPager(){
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //刷新数据
                if(position == 1){
                    mGedanItemPager.initData();
                }
                if(position == 0){
                    mMyGedanItemPager.initData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class FavoritePagerAdapter extends PagerAdapter{

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
}
