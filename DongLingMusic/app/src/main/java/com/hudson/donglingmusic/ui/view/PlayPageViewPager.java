package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Hudson on 2017/3/16.
 * 在不同情况下禁用与启动父View拦截事件的ViewPager
 * 由于外部的父ViewGroup有相应的事件处理，所以我们这里
 * 分情况来处理
 */

public class PlayPageViewPager extends ViewPager {
    public PlayPageViewPager(Context context) {
        super(context);
    }

    public PlayPageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private int mLastX,mLastY;
    /**
     * 在不同的情况下禁用父View的拦截事件
     * 以下是拦截情况：
     *      1.向右滑动且page是第一个页面（id=0)
     * 其他情况不可以拦截
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//先请求父View拦截
        //后面判断是否拦截
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (ev.getX() - mLastX);
                int dy = (int) (ev.getY() - mLastY);
                int curPosition = getCurrentItem();
                if(dx>0) {//向右滑
                    if (curPosition == 0) {//如果是第一个页面，拦截
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
