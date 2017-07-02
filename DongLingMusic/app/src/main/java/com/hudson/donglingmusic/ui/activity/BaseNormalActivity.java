package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;

/**
 * 一般界面的基类activity
 */
public abstract class BaseNormalActivity extends Activity {
    public ScrollView mScrollView;
    private TextView mTitle;
    private SlideRelativeLayout mSlideRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_normal);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseNormalActivity.this.finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.tv_activity_title);
        mScrollView = (ScrollView) findViewById(R.id.sv_container);
        mSlideRelativeLayout = (SlideRelativeLayout) findViewById(R.id.srl_base_root);
        BitmapMusicUtils.setViewDefaultBackground(this,mSlideRelativeLayout);
        mSlideRelativeLayout.setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                BaseNormalActivity.this.finish();
            }
        });
        mScrollView.addView(initView());
    }

    /**
     * 子activity加载自身特有布局的方法
     * @return 用于装入scrollView中的控件
     */
    public abstract View initView();

    public void setActivityTitle(String title){
        mTitle.setText(title);
    }

}
