package com.hudson.donglingmusic.ui.pagerdetail;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.ui.itempager.DownloadCompletedItemPager;
import com.hudson.donglingmusic.ui.itempager.DownloadingItemPager;
import com.hudson.donglingmusic.ui.itempager.HistoryPlayItemPager;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/17.
 */

public class HistoryPagerDetail extends BasePagerDetail {
    private ArrayList<View> mPagers;
    private HistoryPagerAdapter mAdapter;
    private DownloadingItemPager mDownloadingItemPager;
    private DownloadCompletedItemPager mDownloadCompletedItemPager;
    private HistoryPlayItemPager mHistoryPlayItemPager;

    public HistoryPagerDetail(Activity activity, ArrayList<String> data) {
        super(activity, data);
        mPagers = new ArrayList<>();
    }

    //初始化viewPager的数据内容
    @Override
    public void initData() {
        if(mAdapter == null){//说明数据没有添加过
            for (int i = 0; i < mTitles.size(); i++) {
                if(i==1){
                    mDownloadingItemPager = new DownloadingItemPager(mActivity);
                    mPagers.add(mDownloadingItemPager.mRootView);
                }else if(i == 0){
                    mDownloadCompletedItemPager = new DownloadCompletedItemPager(mActivity);
                    mPagers.add(mDownloadCompletedItemPager.mRootView);
                    mDownloadCompletedItemPager.initData();//第一个页面切入就刷新
                }else{
                    mHistoryPlayItemPager = new HistoryPlayItemPager(mActivity);
                    mPagers.add(mHistoryPlayItemPager.mRootView);
                }
            }
            mAdapter = new HistoryPagerAdapter();
            mViewPager.setAdapter(mAdapter);
            //将ViewPager与指示器绑定在一起（注意必须在ViewPager设置完数据才能绑定）
            mIndicator.setViewPager(mViewPager);
            configMusicViewPager();
        }else{
            switch (mViewPager.getCurrentItem()){
                case 0:
                    mDownloadCompletedItemPager.initData();
                    break;
                case 1:
                    mDownloadingItemPager.initData();
                    break;
                case 2:
                    mHistoryPlayItemPager.initData();
                    break;
            }
        }
    }

    class HistoryPagerAdapter extends PagerAdapter {

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
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //刷新数据
                if(position == 1){
                    mDownloadingItemPager.initData();
                }
                if(position == 0){
                    mDownloadCompletedItemPager.initData();
                }
                if(position == 2){
                    mHistoryPlayItemPager.initData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
