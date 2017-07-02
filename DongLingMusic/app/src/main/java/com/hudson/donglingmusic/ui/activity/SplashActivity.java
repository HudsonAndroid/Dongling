package com.hudson.donglingmusic.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.utils.StorageUtils;

import java.io.File;

/**
 * 启动页面
 * 效果：动画效果
 * 使用：属性动画
 */

public class SplashActivity extends Activity {
    private View mSplashLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashLayout = findViewById(R.id.splash_bg_layout);
        preAppDataInit();
        startAnimator();
    }

    /**
     * 对APP的一些初始化
     */
    private void preAppDataInit() {
        initDirectory();
    }

    /**
     * 初始化app的目录结构
     */
    private void initDirectory(){
        if (StorageUtils.isSdCardAvailable()) {// 判断sd卡是否存在
            File file = new File(StorageUtils.getAppLyricsAbsolutePath());
            if (!file.exists()) {// 只有创建了该目录才能继续在该目录下自动创建文件
                file.mkdirs();
            }
            file = new File(StorageUtils.getAppDownloadAbsolutePath());
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(StorageUtils.getAppMusicPicAbsolutePath());
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(StorageUtils.getAppLyricsFontAbsolutePath());
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }


    public void startAnimator(){
        ObjectAnimator alphaAnimator  = ObjectAnimator.ofFloat(mSplashLayout,"alpha",0.0f,1.0f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mSplashLayout,"scaleX",0.0f,1.0f);
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mSplashLayout,"rotation",-90,0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,scaleAnimator,rotateAnimator);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent;
                if(MySharePreferences.getInstance().getFlagFirstUseApp()){//如果是第一次使用app
                    intent = new Intent(SplashActivity.this,GuideActivity.class);
                }else{//跳到主界面
                    intent = new Intent(SplashActivity.this,HomeActivity.class);
                }
                startActivity(intent);
                finish();//结束当前页面
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(1500);
        set.start();
    }
}
