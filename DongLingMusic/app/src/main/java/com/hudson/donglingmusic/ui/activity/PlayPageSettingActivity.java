package com.hudson.donglingmusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/4/8.
 * playPage的设置界面
 */

public class PlayPageSettingActivity extends BaseNormalActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View initView() {
        setActivityTitle("音乐特效设置");
        View v = View.inflate(this, R.layout.activity_playpage_setting,null);
        v.findViewById(R.id.ll_lyrics_font).setOnClickListener(this);
        v.findViewById(R.id.ll_lyrics_other).setOnClickListener(this);
        v.findViewById(R.id.ll_visible_circle).setOnClickListener(this);
        v.findViewById(R.id.ll_visible_bg).setOnClickListener(this);
        v.findViewById(R.id.ll_gaosi).setOnClickListener(this);
        return v;
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.ll_lyrics_font://更改字体
                intent = new Intent(this,LyricsFontSelectorActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_lyrics_other://歌词其他设置
                intent = new Intent(this,LyricsMoreSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_visible_circle://圆形频谱设置
                intent = new Intent(this,CircleVisibleMusicSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_visible_bg://动态背景设置
                intent = new Intent(this,VisibleBgSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_gaosi://高斯模糊设置
                intent = new Intent(this,GaosiBgBitmapSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
