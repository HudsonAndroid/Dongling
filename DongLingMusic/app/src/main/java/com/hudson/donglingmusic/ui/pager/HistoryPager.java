package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;

import com.hudson.donglingmusic.ui.pagerdetail.HistoryPagerDetail;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/15.
 * 历史页面
 *
 * 我们需要往mRootView中添加我们的自己数据
 */

public class HistoryPager extends BasePager {

    public HistoryPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        if(mBasePagerDetail==null){
            //给帧布局填充我们拥有自己特性的内容
            ArrayList<String> data = new ArrayList<>();
            data.add("最近下载");
            data.add("正在下载");
            data.add("最近播放");
            mBasePagerDetail = new HistoryPagerDetail(mActivity, data);
            mContainer.addView(mBasePagerDetail.mRootView);
        }
        mBasePagerDetail.initData();
    }


}
