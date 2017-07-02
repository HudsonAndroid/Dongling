package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Hudson on 2017/3/16.
 * 在不同情况下禁用与启动父View拦截事件的ViewPager
 * 由于存在多层ViewPager嵌套，所以自定义一个ViewPager
 *  头布局轮播条
 */

public class MyInnerViewPager extends ViewPager {
    public MyInnerViewPager(Context context) {
        super(context);
    }

    public MyInnerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private int mLastX,mLastY;
    /**
     * 在不同的情况下禁用父View的拦截事件
     * 以下是拦截情况：
     *      1.上下滑动
     *      2.向右滑动且page是第一个页面（id=0)
     *      3.向左滑动且page是最后一个页面（id=count-1)
     * 其他情况不可以拦截
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//先禁用父View拦截
        //后面判断是否拦截
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (ev.getX() - mLastX);
                int dy = (int) (ev.getY() - mLastY);
                if(Math.abs(dx)>Math.abs(dy)){
                    int curPosition = getCurrentItem();
                    //左右滑动
                    if(dx>0){//向右滑
                        if(curPosition == 0){//如果是第一个页面，允许拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }else{
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }else{//向左滑动
                        //如果是最后一个页面，允许拦截(注意不是getChildCount)
                        if(curPosition == getAdapter().getCount() -1){
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }else{
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }else{
                    //上下滑动，允许拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
