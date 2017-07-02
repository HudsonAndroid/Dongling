package com.hudson.donglingmusic.ui.pager;

import android.content.Context;
import android.view.View;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;

/**
 * Created by Hudson on 2017/3/25.
 */

public abstract class BasePlayPager {
    public View mRootView;
    public IDonglingMusicAidlInterface mInterface;
    public Context mContext;

    public void setIDonglingMusicInterface(IDonglingMusicAidlInterface anInterface){
        mInterface = anInterface;
    }

    public BasePlayPager(Context context){
        mRootView = initView(context);
        mContext = context;
    }

    public abstract View initView(Context context);

    public abstract void initData();

}
