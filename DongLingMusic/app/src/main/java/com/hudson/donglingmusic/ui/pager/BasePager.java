package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.pagerdetail.BasePagerDetail;

/**
 * Created by Hudson on 2017/3/15.
 * 这里是页面的基类
 *
 */

public class BasePager {
    public Activity mActivity;
    public View mRootView;//主布局
    public FrameLayout mContainer;//这个是需要子类往里面添加内容
    public BasePagerDetail mBasePagerDetail;

    public BasePager(Activity activity){
        mActivity = activity;
        mRootView = initView();
    }

    //初始化布局
    public View initView(){
        View view = View.inflate(mActivity, R.layout.pager_base_layout, null);
        mContainer = (FrameLayout) view.findViewById(R.id.fl_container);
        return view;
    }

    //初始化数据
    /**
     * 初始化数据（最好在点击该内容的时候加载，以节省资源）
     */
    public void initData(){

    }

    public void onDestroy(){

    }

    //获取内部的ViewPager
    public int getInnerViewPagerCurPosition(){
        return -1;
    }


}
