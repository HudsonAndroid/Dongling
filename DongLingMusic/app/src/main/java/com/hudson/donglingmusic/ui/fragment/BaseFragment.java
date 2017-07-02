package com.hudson.donglingmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hudson on 2017/3/15.
 * 基类fragment
 */

public abstract class BaseFragment extends Fragment {

    public FragmentActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取他的activity对象
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater);
    }

    //fragment依赖的activity的onCreate方法执行完成
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据
        initData();
    }

    //初始化布局，必须由子类实现
    /**
     * 初始化布局
     * @param inflater
     * @return
     */
    public abstract View initView(LayoutInflater inflater);
    //初始化数据，必须由子类实现
    /**
     * 初始化数据
     */
    public abstract void initData();

}
