package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hudson on 2017/3/30.
 * 用于可视化音乐的背景色控件
 */

public class BigBackgroundView extends View {
    private Paint mPaint;
    private int mColor = 0xaaff0000;
    private int mRadius = 500;
    private boolean mEnable = true;//是否启用

    public int getColor() {
        return mColor;
    }

    /**
     * 设置中心的颜色，必须为ARGB格式
     * @param color
     */
    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    public int getRadius() {
        return mRadius;
    }

    /**
     * 设置颜色扩散半径
     * @param radius 大于800最好
     */
    public void setRadius(int radius) {
        mRadius = radius;
        mPaint.setShader(new RadialGradient(mCenterX, mCenterY, mRadius+mRadiusOffset,
                mColor,0x00000000, Shader.TileMode.MIRROR));
        invalidate();
    }

    public boolean isEnable() {
        return mEnable;
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
        mPaint.setShader(new RadialGradient(mCenterX, mCenterY, mRadius+mRadiusOffset,
                mColor,0x00000000, Shader.TileMode.MIRROR));
        invalidate();
    }

    private int mRadiusOffset = 0;
    private int mCenterX,mCenterY;


    public BigBackgroundView(Context context) {
        this(context,null);
    }

    public BigBackgroundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BigBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w/2;
        mCenterY = h/2;
        updateView(0,0);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void updateView(int data1,int data2){
        if(mEnable){
            mRadiusOffset = (int) Math.hypot(data1,data2);
            mPaint.setShader(new RadialGradient(mCenterX, mCenterY, mRadius+mRadiusOffset,
                    mColor,0x00000000, Shader.TileMode.MIRROR));
            invalidate();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(mEnable){
            canvas.drawCircle(mCenterX,mCenterY,mRadius+mRadiusOffset,mPaint);
        }
    }
}
