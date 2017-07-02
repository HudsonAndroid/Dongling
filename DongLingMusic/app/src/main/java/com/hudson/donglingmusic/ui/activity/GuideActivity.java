package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;

import java.util.ArrayList;

/**
 * 引导界面
 * 思路：使用ViewPager实现
 * 注意：1.布局上最后一个页面有“开始体验”是通过visibility设置的
 *      2.布局上的指示点也是脱离ViewPager的
 *      3.指示点是通过shape实现的
 * 总结：
 *      1.ViewPager使用前需要初始化数据
 *      2.ViewPager的适配器有四个重要方法getCount、isViewFromObject、instantiateItem、destroyItem
 *      3.在activity中通过getViewTreeObserver().addOnGlobalLayoutListener获取尺寸
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private RelativeLayout mRelativeLayout;
    private ViewPager mViewPager;
    //引导图片id数组
    private int[] mImageIds = new int[]{R.drawable.guide1,R.drawable.guide2,R.drawable.guide3};
    private ArrayList<ImageView> mImageViews;

    //指示点的容器
    private LinearLayout mLinearLayout;
    //可移动的小红点
    private ImageView mRedImageView;
    private int mPointMargin = 20;//px,指示点之间的Margin值
    private int mPointDistance;
    private View mStartUseApp;

    //开始体验按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mViewPager = (ViewPager) this.findViewById(R.id.vp_guide);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_guide);
        mStartUseApp = findViewById(R.id.btn_start);
        mStartUseApp.setOnClickListener(this);
        initData();
        mViewPager.setAdapter(new GuidePagerAdapter());
        mViewPager.setOnPageChangeListener(this);
    }

    //初始化布局与数据
    public void initData(){
        mImageViews = new ArrayList<>();
        ImageView imageView,point;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(mPointMargin/2,0,mPointMargin/2,0);
        for (int i = 0; i < mImageIds.length; i++) {
            //初始化ViewPager数据
            imageView = new ImageView(this);
            imageView.setBackgroundResource(mImageIds[i]);
            mImageViews.add(imageView);
            //初始化指示点
            point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);
            point.setLayoutParams(layoutParams);
            mLinearLayout.addView(point);
        }
        //添加小红点
        RelativeLayout.LayoutParams redPointParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRedImageView = new ImageView(this);
        mRedImageView.setImageResource(R.drawable.shape_point_red);
        redPointParams.addRule(RelativeLayout.ALIGN_LEFT,R.id.ll_container);
        redPointParams.addRule(RelativeLayout.ALIGN_TOP,R.id.ll_container);
        redPointParams.setMargins(mPointMargin/2,0,0,0);
        mRedImageView.setLayoutParams(redPointParams);
        mRelativeLayout.addView(mRedImageView);
        //初始化小红点移动总距离（由于在onCreate方法无法获取，所以需要监听layout完成）
        mRedImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//onLayout执行结束的回调方法
                //只需要一次（这里过时是由于google方法写错了，然后后面发现了，所以改了，然后这个就过时了）
                mRedImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mPointDistance = mRedImageView.getWidth() + mPointMargin;
            }
        });
    }


    //ViewPager的适配器
    private class GuidePagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {//返回个数
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化item布局(这里是初始化imageView)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViews.get(position);
            container.addView(view);
            return view;
        }

        //销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    //===========小红点的移动============
    //小红点的移动距离是两个指示点之间的距离*ViewPager移动百分比
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//页面滑动
        //间距 getWidth+margin的计算放在layout布局完成后里面处理，提高性能
        mRedImageView.setTranslationX(mPointDistance*(positionOffset+position));
    }

    @Override
    public void onPageSelected(int position) {//页面选中
        if(position == mImageViews.size()-1){
            mStartUseApp.setVisibility(View.VISIBLE);
        }else{
            mStartUseApp.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {//页面状态变化

    }

    //开始体验app（按钮点击事件）
    @Override
    public void onClick(View v) {
        MySharePreferences.getInstance().saveFlagFirstUseApp(false);//引导过了
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
}
