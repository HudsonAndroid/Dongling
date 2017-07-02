package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/2/27.
 * 主要目的：熟悉canvas的变化
 * 实现思路：超级简单，仅一个api,即canvas.rotate。
 *
 * 宽高说明：本控件会取宽高中的小值作为参考值，所以一般都宽高都设置为match_parent
 */

public class CircleVisualizerFFTView extends View {
    private static final int DEFAULT_CAKE_DEGREE = 5;//默认的频谱块旋转角度。canvas就是通过它旋转的
    private static final int DEFAULT_CAKE_COLOR = Color.WHITE;//默认的频谱块颜色
    private static final int DEFAULT_PADDING_OFFSET = 50;//dp。默认的控件外围预留区域
    private int[] mHeights;//频谱块高度数组
    private int mCakeCount;//频谱块个数
    private float mCakeDegree;
    private int mCakeColor;
    private int mCircleRadius;
    private int mPaddingOffset;
    private Paint mPaint;
    private int mCenterX;
    private int mCenterY;
    private int mDrawStartY;
    private int mStrokeWidth = 10;
    private int[] mColors;
    private boolean mRotateColor = false;//颜色是否开启旋转动画

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        mPaint.setStrokeWidth(mStrokeWidth);
        invalidate();
    }

    /**
     * 获取是否开启颜色旋转动画
     * @return
     */
    public boolean isRotateColor() {
        return mRotateColor;
    }

    /**
     * 设置开启颜色旋转动画
     * @param rotateColor
     */
    public void setRotateColor(boolean rotateColor) {
        mRotateColor = rotateColor;
        mOffset = 0;
    }

    /**
     * 设置数据，仅用于预览
     * @param heights
     */
    public void setHeights(int[] heights) {
        mHeights = heights;
    }

    public int getCakeCount() {
        return mCakeCount;
    }

    public void setCakeCount(int cakeCount) {
        mCakeCount = cakeCount;
        mCakeDegree = 360*1.0f / mCakeCount;
        invalidate();
    }

    public int[] getColors() {
        return mColors;
    }

    public void setColors(int[] colors) {
        mColors = new int[colors.length+1];
        int length = mColors.length;
        for (int i = 0; i < length; i++) {
            if(i == length-1){
                mColors[i] = colors[0];
            }else{
                mColors[i] = colors[i];
            }
        }
        invalidate();
    }

    public CircleVisualizerFFTView(Context context) {
        this(context,null);
    }

    public CircleVisualizerFFTView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleVisualizerFFTView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        mCakeDegree = DEFAULT_CAKE_DEGREE;
        mCakeColor = DEFAULT_CAKE_COLOR;
        mPaddingOffset = DEFAULT_PADDING_OFFSET;
        if(attrs!=null){//避免由于第一个构造方法造成异常
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.CircleVisualizerFFTView);
            mCakeDegree = ta.getFloat(R.styleable.CircleVisualizerFFTView_cake_degree,mCakeDegree);
            mCakeColor = ta.getColor(R.styleable.CircleVisualizerFFTView_cake_color,mCakeColor);
            mPaddingOffset = (int)ta.getDimension(R.styleable.CircleVisualizerFFTView_padding_offset,dp2px(mPaddingOffset,context));
            ta.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCakeColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mCakeCount = (int)(360 / mCakeDegree);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(MeasureSize(width, widthMode,1),
                MeasureSize(height, heightMode,2));//或者使用super.onMeasure()
    }

    /**
     * 测量控件大小
     * @param size
     * @param sizeMode
     * @param code 1表示width，2表示height
     * @return
     */
    private int MeasureSize(int size, int sizeMode,int code) {
        if (sizeMode == MeasureSpec.EXACTLY) {// 如果指定了明确的大小
            return size;
        } else {// 根据我们的情况设置大小
            int requireSize = 0;
            if(code == 1){//表示width,200是默认半径值,像素
                requireSize = getPaddingLeft()+getPaddingRight()+(200+mPaddingOffset)*2;
            }else if(code == 2){//表示height
                requireSize = getPaddingBottom()+getPaddingTop()+(200+mPaddingOffset)*2;
            }
            if (sizeMode == MeasureSpec.AT_MOST) {
                requireSize = Math.min(size, requireSize);
            }
            return requireSize;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(w<h){
            mCircleRadius = (w - getPaddingLeft() -getPaddingRight() - mPaddingOffset*2)/2;
        }else{
            mCircleRadius = (h - getPaddingBottom() - getPaddingTop() - mPaddingOffset*2)/2;
        }
        mCenterX = w/2;
        mCenterY = h/2;
        mDrawStartY = mCenterY - mCircleRadius;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    //初始化频谱块高度
    public void updateVisualizer(byte[] fft) {
        int[] model = new int[fft.length / 2 + 1];
        model[0] =  Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mCakeCount;) {
            model[j] = (int) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
        mHeights = model;
        invalidate();
    }

    private int mOffset=0;
    @Override
    protected void onDraw(Canvas canvas) {
        if (mHeights == null) {
            return;
        }
        drawLine(canvas, mHeights[0]);
        for (int i = 0; i < mCakeCount; i++) {
            mPaint.setColor(evaluate((i+mOffset)%mCakeCount*1.0f/mCakeCount, mColors));
            canvas.rotate(mCakeDegree,mCenterX,mCenterY);
            drawLine(canvas, mHeights[i+1]);
        }
        if(mRotateColor){
            mOffset ++;
            if(mOffset>=mCakeCount){
                mOffset = 0;
            }
        }
    }

    public void drawLine(Canvas canvas,int height){
        mPaint.setAlpha(255);
        if(height>mDrawStartY){
            canvas.drawLine(mCenterX,mDrawStartY,mCenterX,50,mPaint);
        }else{
            canvas.drawLine(mCenterX,mDrawStartY,mCenterX,mDrawStartY-height,mPaint);
        }
        mPaint.setAlpha(150);
        canvas.drawLine(mCenterX,mDrawStartY+10,mCenterX,mDrawStartY+10+height*0.4f,mPaint);
    }

    public static float dp2px(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 根据占比，来获取渐变色对应的颜色
     * 思路来源：系统渐变色动画
     * @param fraction 占比0.0-1.0之间
     * @param startColor 起始颜色
     * @param endColor 终止颜色
     * @return
     */
    public int evaluate(float fraction, int startColor, int endColor) {
        int startInt = (Integer) startColor;
        int startA = (startInt >> 24);
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endColor;
        int endA = (endInt >> 24);
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }

    /**
     * start-middle 0-0.5  ---->fraction*2 --->0-1
     * middle-end  0.5-1.0  ---> (fraction-0.5)*2 --->0-1
     * 前半部分占0.5，后半部分占0.5
     * @param fraction
     * @param startColor
     * @param middleColor
     * @param endColor
     * @return
     */
    public int evaluate(float fraction,int startColor,int middleColor,int endColor){
        if(fraction<=0.5){
            return evaluate(fraction*2,startColor,middleColor);
        }else{
            return evaluate((fraction-0.5f)*2,middleColor,endColor);
        }
    }

    /**
     * 有n个中间颜色
     * @param fraction
     * @param colors
     * @return
     */
    public int evaluate(float fraction,int[] colors){
        int length = colors.length;
        if(length ==0){
            throw new IllegalStateException("颜色为空，the mColors is null!");
        }
        if(length == 1){
            return colors[0];//返回原色
        }
        if(length == 2){
            return evaluate(fraction,colors[0],colors[1]);
        }
        //计算出每两个颜色占用的比值
        float eachFraction = 1.0f / (length -1);
        int index = (int)(fraction / eachFraction);//注意不需要四舍五入
        if(index == length-1){
            index = length-2;
        }
        return evaluate((fraction - index*eachFraction)*(length-1),colors[index],colors[index+1]);
    }
}
