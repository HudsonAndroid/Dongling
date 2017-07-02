package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.ui.itempager.HotItemPager;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 *      热门标签的ViewPager页面（列表的一部分）
 *
 *      自动轮播
 *          1.利用handler
 *          2.当用户点击在上面时，我们的自动轮播应该取消，并交给用户处理
 */

public class HotViewPagerViewHolder extends BaseRecyclerViewHolder<ArrayList<String>> implements ViewPager.OnPageChangeListener {
    private ListPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private ArrayList<ImageView> mPagers;
    private Context mContext;
    private CirclePageIndicator mIndicator;//圆形指示器
    private Handler mHandler;//用于自动轮播
    private int mDelayTime = 3000;

    public HotViewPagerViewHolder(View itemView) {
        super(itemView);
        mViewPager = (ViewPager) itemView.findViewById(R.id.vp_header);
        mIndicator = (CirclePageIndicator) itemView.findViewById(R.id.header_indicator);
        mPagers = new ArrayList<>();
        mContext = itemView.getContext();
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //取消自动轮播
                        if(mHandler!=null){
                            mHandler.removeCallbacksAndMessages(null);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL://事件取消或者抬起都需要重新开启
                    case MotionEvent.ACTION_UP:
                        //重新开启自动轮播
                        if(mHandler!=null){
                            mHandler.sendEmptyMessageDelayed(0,mDelayTime);
                        }
                        break;
                }
                return false;
            }
        });
        //初始化轮播条的imageView
        for (int i = 0; i < HotItemPager.HEADER_VIEWPAGER_COUNT; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.default_loading);//先展示默认图片
            mPagers.add(imageView);
        }
        mAdapter = new ListPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mIndicator.setOnPageChangeListener(this);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setSnap(true);//快照方式展示（点不随手势移动而移动）
    }

    @Override
    public void refreshView(ArrayList<String> data) {
        int size = Math.min(data.size(),HotItemPager.HEADER_VIEWPAGER_COUNT);
        for (int i = 0; i < size; i++) {
            XUtilsManager.getBitmapUtilsInstance().display(mPagers.get(i),data.get(i));
        }
        mAdapter.notifyDataSetChanged();
        if(mHandler == null){
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    int nextItem = mViewPager.getCurrentItem()+1;
                    if(nextItem>mPagers.size()-1){
                        nextItem = 0;
                    }
                    mViewPager.setCurrentItem(nextItem);
                    mHandler.sendEmptyMessageDelayed(0,mDelayTime);
                }
            };
        }else{//说明以前有Handler在处理，我们移除所有消息
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler.sendEmptyMessageDelayed(0,mDelayTime);
    }


    class ListPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mPagers.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }


    //轮播的处理
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
