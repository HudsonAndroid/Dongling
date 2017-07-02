package com.hudson.donglingmusic.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.view.CircleVisualizerFFTView;
import com.hudson.donglingmusic.ui.view.SelectProgressBar;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/4/13.
 * 圆形频谱设置界面
 */

public class CircleVisibleMusicSettingActivity extends BaseNormalActivity {
    private CircleVisualizerFFTView mCircleVisualizerFFTView;
    private CheckedTextView mRotateColorView;
    private LinearLayout mColorsContainer;
    private ImageView mSelectedImageView;
    private int mSelectedItemColor;
    private int mNormalItemColor;
    private int mSelectedIndex = -1;
    private ArrayList<Integer> mColors;
    private MySharePreferences mInstance;
    private ImageView mAdd,mModify,mDelete;
    private SelectProgressBar mCakeCountProgressbar,mCakeWidthProgressbar;
    private int mSelectedColumnCount;
    private int mSelectedColumnWidth;

    @Override
    public View initView() {
        View v = View.inflate(this, R.layout.activity_circlevisible_setting,null);
        setActivityTitle(getString(R.string.visible_music_circle));
        mInstance = MySharePreferences.getInstance();
        mRotateColorView = (CheckedTextView) v.findViewById(R.id.ctv_rotate_color);
        mRotateColorView.setChecked(mInstance.getRotateCircleVisibleMusicColor());
        mRotateColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mRotateColorView.isChecked();
                mRotateColorView.setChecked(!checked);
                mInstance.saveRotateCircleVisibleMusicColor(!checked);
            }
        });
        mCircleVisualizerFFTView = (CircleVisualizerFFTView) v.findViewById(R.id.cvf_preview);
        initColumnData();
        mCakeCountProgressbar = (SelectProgressBar) v.findViewById(R.id.spb_circle_column_count);
        mCakeWidthProgressbar = (SelectProgressBar) v.findViewById(R.id.spb_circle_column_stroke_width);
        mSelectedColumnCount = mInstance.getVisibleCircleColumnCount();
        mSelectedColumnWidth = mInstance.getVisibleCircleColumnWidth();
        updateCircleColumnCount();
        updateCircleColumnWidth();
        mCakeCountProgressbar.setSelectedValue(mSelectedColumnCount);
        mCakeCountProgressbar.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mSelectedColumnCount = selectedValue;
                mInstance.saveVisibleCircleColumnCount(selectedValue);
                updateCircleColumnCount();
            }
        });
        mCakeWidthProgressbar.setSelectedValue(mSelectedColumnWidth);
        mCakeWidthProgressbar.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                mSelectedColumnWidth = selectedValue;
                mInstance.saveVisibleCircleColumnWidth(selectedValue);
                updateCircleColumnWidth();
            }
        });
        mAdd = (ImageView) v.findViewById(R.id.iv_color_add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedImageView!=null){//在被选中的颜色后面添加新颜色
                    DialogUtils.showColorPickerDialog(CircleVisibleMusicSettingActivity.this,
                            getString(R.string.visible_circle_add_color),
                            Color.WHITE,DialogUtils.TYPE_VISIBLE_CIRCLE_ADD,new Runnable(){
                                @Override
                                public void run() {
                                    mColors.add(mSelectedIndex+1,mInstance.getVisibleCircleAddColor());
                                    initColorContainer();
                                    mSelectedIndex = -1;
                                    mSelectedImageView = null;
                                }
                            },false);
                }else{
                    ToastUtils.showToast("请先选择一种颜色!");
                }
            }
        });
        mModify = (ImageView) v.findViewById(R.id.iv_color_modify);
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedImageView!=null){//修改本颜色
                    DialogUtils.showColorPickerDialog(CircleVisibleMusicSettingActivity.this,
                            getString(R.string.visible_circle_modify_color),
                            mColors.get(mSelectedIndex),
                            DialogUtils.TYPE_VISIBLE_CIRCLE_MODIFY,new Runnable(){
                                @Override
                                public void run() {
                                    mColors.set(mSelectedIndex,mInstance.getVisibleCircleModifyColor());
                                    initColorContainer();
                                    mSelectedIndex = -1;
                                    mSelectedImageView = null;
                                }
                            },true);
                }else{
                    ToastUtils.showToast("请先选择一种颜色!");
                }
            }
        });
        mDelete = (ImageView) v.findViewById(R.id.iv_color_delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedImageView!=null){//删除本颜色
                    if(mColors.size() == 1){
                        ToastUtils.showToast("这已经是最后一种颜色了!");
                    }else{
                        mColors.remove(mSelectedIndex);
                        mSelectedIndex = -1;
                        mSelectedImageView = null;
                        initColorContainer();
                    }
                }else{
                    ToastUtils.showToast("请先选择一种颜色!");
                }
            }
        });
        mColors = array2List(mInstance.getColorsArray());
        mNormalItemColor = getResources().getColor(R.color.transparent);
        mSelectedItemColor = getResources().getColor(R.color.selected_item_color);
        mColorsContainer = (LinearLayout) v.findViewById(R.id.ll_colors_container);
        mColorsContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//onLayout执行结束的回调方法
                mColorsContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initColorContainer();
            }
        });
        return v;
    }

    /**
     * 初始化颜色容器
     */
    private void initColorContainer(){
        mColorsContainer.removeAllViews();
        int size = mColors.size();
        int itemWidth = mColorsContainer.getWidth()/size;
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < size; i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(imageParams);
            imageView.setPadding(10,10,10,10);
            imageView.setImageDrawable(new ColorDrawable(mColors.get(i)));
            final int index = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectedImageView!=null){
                        mSelectedImageView.setBackgroundColor(mNormalItemColor);
                    }
                    mSelectedImageView = imageView;
                    mSelectedImageView.setBackgroundColor(mSelectedItemColor);
                    mSelectedIndex = index;
                }
            });
            mColorsContainer.addView(imageView);
        }
        mColorsContainer.invalidate();
        updateCirclePreviewColors();
    }

    /**
     * 初始化频谱的数据（随机数）
     */
    private void initColumnData(){
        int[] columnData = new int[201];
        for (int i = 0; i < 201; i++) {
            columnData[i] = 30;
        }
        mCircleVisualizerFFTView.setHeights(columnData);
    }

    /**
     * 刷新频谱颜色
     */
    private void updateCirclePreviewColors(){
        mCircleVisualizerFFTView.setColors(list2Array(mColors));
    }

    private void updateCircleColumnCount(){
        mCircleVisualizerFFTView.setCakeCount(mSelectedColumnCount);
    }

    private void updateCircleColumnWidth(){
        mCircleVisualizerFFTView.setStrokeWidth(mSelectedColumnWidth);
    }

    private ArrayList<Integer> array2List(int[] array){
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            colors.add(array[i]);
        }
        return colors;
    }

    private int[] list2Array(ArrayList<Integer> list){
        if(list == null){
            throw new IllegalStateException("list is null");
        }
        int size = list.size();
        int[] colors = new int[size];
        for (int i = 0; i < size; i++) {
            colors[i] = list.get(i);
        }
        return colors;
    }

    @Override
    protected void onDestroy() {
        mInstance.saveColorsArray(list2Array(mColors));
        mColors.removeAll(mColors);
        mColors = null;
        super.onDestroy();
    }
}
