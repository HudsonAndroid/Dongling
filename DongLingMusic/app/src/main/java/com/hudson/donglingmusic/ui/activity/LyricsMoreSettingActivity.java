package com.hudson.donglingmusic.ui.activity;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.view.SelectProgressBar;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/4/10.
 * 歌词的更多设置页面
 *
 * 注意：高亢歌词字体大小必须大于默认歌词字体大小
 */

public class LyricsMoreSettingActivity extends BaseNormalActivity {
    private SelectProgressBar mNormalTextSize;
    private SelectProgressBar mFocusTextSize;
    private SelectProgressBar mLyricsShowCount;
    private SelectProgressBar mLyricsAdjustTimeOffset;
    private LinearLayout mRootView;
    private View mNormalColorView,mFocusColorView;
    private ArrayList<TextView> mTextViews;
    private int mShowCount;
    private int mNormalTextSizeValue,mFocusTextSizeValue;
    private int mNormalTextColorValue,mFocusTextColorValue;
    private LinearLayout mPreView;
    private MySharePreferences mMySharePreferences;

    @Override
    public View initView() {
        setActivityTitle(getString(R.string.lyrics_other_setting));
        mTextViews = new ArrayList<>();
        mMySharePreferences = MySharePreferences.getInstance();
        View v  = View.inflate(this, R.layout.activity_lyrics_more_setting,null);
        mRootView = (LinearLayout) v.findViewById(R.id.ll_lyrics_more_setting_root);
        mNormalTextSize = (SelectProgressBar) v.findViewById(R.id.spb_lyrics_normal_text_size);
        mFocusTextSize = (SelectProgressBar) v.findViewById(R.id.spb_lyrics_focus_text_size);
        mNormalColorView = v.findViewById(R.id.v_normal_color);
        mNormalTextColorValue = mMySharePreferences.getNormalLyricsColor();
        mNormalColorView.setBackgroundColor(mNormalTextColorValue);
        mNormalColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showColorPickerDialog(LyricsMoreSettingActivity.this,
                        getString(R.string.lyrics_normal_text_color),
                        mMySharePreferences.getNormalLyricsColor(),
                        DialogUtils.TYPE_NORMAL_LYRICS_COLOR,
                        new Runnable() {
                            @Override
                            public void run() {
                                mNormalTextColorValue = mMySharePreferences.getNormalLyricsColor();
                                mNormalColorView.setBackgroundColor(
                                        mNormalTextColorValue);
                                changeTextViewColor();
                            }
                        },true);
            }
        });
        mFocusColorView = v.findViewById(R.id.v_focus_color);
        mFocusTextColorValue = mMySharePreferences.getFocusLyricsColor();
        mFocusColorView.setBackgroundColor(mFocusTextColorValue);
        mFocusColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showColorPickerDialog(LyricsMoreSettingActivity.this,
                        getString(R.string.lyrics_focus_text_color),
                        mMySharePreferences.getFocusLyricsColor(),
                        DialogUtils.TYPE_FOCUS_LYRICS_COLOR,
                        new Runnable() {
                            @Override
                            public void run() {
                                mFocusTextColorValue = mMySharePreferences.getFocusLyricsColor();
                                mFocusColorView.setBackgroundColor(
                                        mFocusTextColorValue);
                                changeTextViewColor();
                            }
                        },true);
            }
        });
        mNormalTextSizeValue = mMySharePreferences.getNormalLyricsTextSize();
        mFocusTextSizeValue = mMySharePreferences.getFocusLyricsTextSize();
        mNormalTextSize.setSelectedValue(mNormalTextSizeValue);
        mFocusTextSize.setSelectedValue(mFocusTextSizeValue);
        mFocusTextSize.setMinValue(mNormalTextSizeValue+1);//高亢歌词最小也得比高亢歌词大
        mNormalTextSize.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mNormalTextSizeValue = selectedValue;
                mMySharePreferences.saveNormalLyricsTextSize(selectedValue);
                //判断focusTextSize是否小于正常歌词字体大小
                checkLyricsTextSizeValid();
                changeTextViewSize();
            }
        });
        mFocusTextSize.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mFocusTextSizeValue = selectedValue;
                changeTextViewSize();
                mMySharePreferences.saveFocusLyricsTextSize(selectedValue);
            }
        });
        mLyricsShowCount = (SelectProgressBar) v.findViewById(R.id.spb_lyrics_show_count);
        mShowCount = mMySharePreferences.getLyricsShowCount();
        mLyricsShowCount.setSelectedValue(mShowCount);
        mLyricsShowCount.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mShowCount = selectedValue;
                initPreView();
                mMySharePreferences.saveLyricsShowCount(selectedValue);
            }
        });
        //调整歌词
        mLyricsAdjustTimeOffset = (SelectProgressBar) v.findViewById(R.id.spb_lyrics_speed_slow_time);
        mLyricsAdjustTimeOffset.setSelectedValue(mMySharePreferences.getAdjustOffsetTime());
        mLyricsAdjustTimeOffset.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mLyricsAdjustTimeOffset.setSelectedValue(selectedValue);
                mMySharePreferences.saveAdjustOffsetTime(selectedValue);
            }
        });
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initPreView();
            }
        });
        return v;
    }

    /**
     * 检查字体大小的合法性(focus>normal)
     */
    private void checkLyricsTextSizeValid(){
        if(mFocusTextSizeValue<=mNormalTextSizeValue){
            ToastUtils.showToast("您设置的非播放歌词比播放歌词大，已自动调整播放歌词大小！");
            mFocusTextSizeValue = mNormalTextSizeValue + 1;
            mFocusTextSize.setSelectedValue(mFocusTextSizeValue);
            mMySharePreferences.saveFocusLyricsTextSize(mFocusTextSizeValue);
        }
        mFocusTextSize.setMinValue(mNormalTextSizeValue+1);
    }

    private void initPreView(){
        if(mTextViews!=null){
            mTextViews.removeAll(mTextViews);
            mRootView.removeView(mPreView);
        }
        mPreView = new LinearLayout(this);
        mPreView.setOrientation(LinearLayout.VERTICAL);
        mPreView.setBackgroundResource(R.drawable.preview_bg);
        mPreView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mShowCount*150+500));
        TextView textView;
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 150);
        for (int i = 0; i < mShowCount; i++) {
            textView = new TextView(this);
            textView.setText("动铃音乐");
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(mNormalTextColorValue);
            textView.setTextSize(mNormalTextSizeValue);
            textView.setLayoutParams(textParams);
            if(i == mShowCount/2){
                textView.setTextColor(mFocusTextColorValue);
                textView.setTextSize(mFocusTextSizeValue);
            }
            mTextViews.add(textView);
            mPreView.addView(textView);
        }
        mRootView.addView(mPreView);
        mScrollView.invalidate();
    }

    private void changeTextViewSize(){
        TextView textView;
        int count = mTextViews.size();
        int halfIndex = count/2;
        for (int i = 0; i < count; i++) {
            textView = mTextViews.get(i);
            if(halfIndex != i){
                textView.setTextSize(mNormalTextSizeValue);
            }else{
                textView.setTextSize(mFocusTextSizeValue);
            }
        }
    }

    private void changeTextViewColor(){
        TextView textView;
        int count = mTextViews.size();
        int halfIndex = count/2;
        for (int i = 0; i < count; i++) {
            textView = mTextViews.get(i);
            if(halfIndex != i){
                textView.setTextColor(mNormalTextColorValue);
            }else{
                textView.setTextColor(mFocusTextColorValue);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if(mTextViews!=null){
            mTextViews.removeAll(mTextViews);
        }
        super.onDestroy();
    }
}
