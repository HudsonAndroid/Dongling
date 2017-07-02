package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by Hudson on 2017/4/1.
 * 歌词制作页面基类
 */

public abstract class BaseLyricsMakePager {
    public View mRootView;
    public Activity mActivity;

    public BaseLyricsMakePager(Activity activity){
        mRootView = initView(activity);
        mActivity = activity;
    }

    public abstract View initView(Context context);

}
