package com.hudson.donglingmusic.ui.activity;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckedTextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.view.BigBackgroundView;
import com.hudson.donglingmusic.ui.view.SelectProgressBar;
import com.hudson.donglingmusic.utils.DialogUtils;

/**
 * Created by Hudson on 2017/4/15.
 * 动态背景设置页面
 */

public class VisibleBgSettingActivity extends BaseNormalActivity {
    private CheckedTextView mEnable;
    private View mEnableView;
    private SelectProgressBar mVisibleBgRadius;
    private View mVisibleBgColor;
    private BigBackgroundView mPreView;
    private MySharePreferences mInstance;
    private int mRadius;
    private int mColor;


    @Override
    public View initView() {
        setActivityTitle(getString(R.string.visible_music_bg));
        mInstance = MySharePreferences.getInstance();
        View v = View.inflate(this, R.layout.activity_visiblebg_setting,null);
        mEnableView =  v.findViewById(R.id.ll_enable_root);
        mEnable = (CheckedTextView) v.findViewById(R.id.ctv_enable_visible_bg);
        boolean enable = mInstance.getVisibleBgEnable();
        if(!enable){
            mEnableView.setVisibility(View.INVISIBLE);
        }
        mEnable.setChecked(enable);
        mEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = !mEnable.isChecked();
                mEnable.setChecked(checked);
                mInstance.saveVisibleBgEnable(checked);
                if(checked){
                    mEnableView.setVisibility(View.VISIBLE);
                }else{
                    mEnableView.setVisibility(View.INVISIBLE);
                }
            }
        });
        mVisibleBgRadius = (SelectProgressBar) v.findViewById(R.id.spb_visible_bg_radius);
        mRadius = mInstance.getVisibleBgRadius();
        mVisibleBgRadius.setSelectedValue(mRadius);
        mVisibleBgRadius.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mRadius = selectedValue;
                mInstance.saveVisibleBgRadius(selectedValue);
                updatePreview();
            }
        });
        mVisibleBgColor = v.findViewById(R.id.v_visible_bg_color);
        mColor = mInstance.getVisibleBgColor();
        mVisibleBgColor.setBackgroundColor(mColor);
        mVisibleBgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showColorPickerDialog(VisibleBgSettingActivity.this,
                        getString(R.string.visible_bg_music_color), mInstance.getVisibleBgColor(),
                        DialogUtils.TYPE_VISIBLE_BG_COLOR, new Runnable() {
                            @Override
                            public void run() {
                                mColor = mInstance.getVisibleBgColor();
                                mVisibleBgColor.setBackgroundColor(mColor);
                                updatePreview();
                            }
                        },true);
            }
        });
        mPreView = (BigBackgroundView) v.findViewById(R.id.bbv_visible_bg_preview);
        mPreView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPreView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                updatePreview();
            }
        });
        return v;
    }


    private void updatePreview(){
        mPreView.setColor(mColor);
        mPreView.setRadius(mRadius);
    }

}
