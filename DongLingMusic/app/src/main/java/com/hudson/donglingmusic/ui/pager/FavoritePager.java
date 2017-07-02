package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;

import com.hudson.donglingmusic.ui.pagerdetail.FavoritePagerDetail;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/15.
 * 最爱页面
 *
 * 我们需要往mRootView中添加我们的自己数据
 */

public class FavoritePager extends BasePager {

    public FavoritePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        if(mBasePagerDetail==null){
            //给帧布局填充我们拥有自己特性的内容
            ArrayList<String> data = new ArrayList<>();
            data.add("我的最爱");
            data.add("网络歌单");
            mBasePagerDetail = new FavoritePagerDetail(mActivity,data);
            mContainer.addView(mBasePagerDetail.mRootView);
        }
        mBasePagerDetail.initData();
    }
}
