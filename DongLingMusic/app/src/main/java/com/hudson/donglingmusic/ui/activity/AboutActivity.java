package com.hudson.donglingmusic.ui.activity;

import android.view.View;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/4/20.
 * 关于界面
 */

public class AboutActivity extends BaseNormalActivity {

    @Override
    public View initView() {
        setActivityTitle(getString(R.string.about));
        View v = View.inflate(this, R.layout.activity_about,null);
        return v;
    }
}
