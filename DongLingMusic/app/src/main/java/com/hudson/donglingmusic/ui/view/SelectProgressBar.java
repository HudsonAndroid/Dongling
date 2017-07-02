package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/4/10.
 * choose value progress bar
 * this view is used to selected a value from min number to max number
 * Different from normal progressbar,this view is not used to show the
 * percentage but show the specific value in the range.
 *
 * summary:I found a big question that i have kept doing the wrong thing for a
 * long time.That's custom attribute dimension transition we should not transform
 * the value after execute the method "getDimension",because the method returns a
 * pixel value not other value.Oh my god,there are so many custom view need modifying!
 */

public class SelectProgressBar extends View {
    private static final int DEFAULT_LINE_LENGTH = 300;//dp
    private static final int DEFAULT_CIRCLE_RADIUS = 10;//dp
    private static final int DEFAULT_LINE_STROKE_WIDTH = 6;//dp
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_LINE_COLOR = Color.GRAY;
    private static final int DEFAULT_CIRCLE_COLOR = Color.GREEN;
    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private int mLineColor;
    private int mCircleColor;
    private float mCenterX;
    private float mCenterY;
    enum Style{
        VERTICAL,HORIZONTAL
    }
    private Style mStyle = Style.VERTICAL;
    private Paint mPaint;
    private Paint mCirclePaint;
    private Paint mDraggingBgPaint;
    private int mTextSize;
    private int mRadius;
    private int mLineLength;
    private int mLineStrokeWidth;
    private int mMinValue;
    private int mMaxValue;
    private int mSelectedValue;
    private int mTextHeight;
    private int mTextBaseLine;
    private boolean mIsDragging = false;//是否正在被拖拽,check the view is been dragging or not

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
        invalidate();
    }

    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(int circleColor) {
        mCircleColor = circleColor;
        invalidate();
    }

    public Style getStyle() {
        return mStyle;
    }

    public void setStyle(Style style) {
        mStyle = style;
        invalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        invalidate();
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        mRadius = radius;
        invalidate();
    }

    public int getLineLength() {
        return mLineLength;
    }

    public void setLineLength(int lineLength) {
        mLineLength = lineLength;
        invalidate();
    }

    public int getLineStrokeWidth() {
        return mLineStrokeWidth;
    }

    public void setLineStrokeWidth(int lineStrokeWidth) {
        mLineStrokeWidth = lineStrokeWidth;
        invalidate();
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        invalidate();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        invalidate();
    }

    public int getSelectedValue() {
        return mSelectedValue;
    }

    public void setSelectedValue(int selectedValue) {
        mSelectedValue = selectedValue;
        invalidate();
    }

    public SelectProgressBar(Context context) {
        this(context,null);
    }

    public SelectProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelectProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLineLength = dp2px(DEFAULT_LINE_LENGTH,context);
        mRadius = dp2px(DEFAULT_CIRCLE_RADIUS,context);
        mLineStrokeWidth = dp2px(DEFAULT_LINE_STROKE_WIDTH,context);
        mMinValue = DEFAULT_MIN_VALUE;
        mMaxValue = DEFAULT_MAX_VALUE;
        mLineColor = DEFAULT_LINE_COLOR;
        mCircleColor = DEFAULT_CIRCLE_COLOR;
        mTextSize = sp2px(DEFAULT_TEXT_SIZE,context);
        if(attrs!=null){
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.SelectProgressBar);
            mLineLength = (int)ta.getDimension(R.styleable.SelectProgressBar_line_length,mLineLength);
            mRadius = (int)ta.getDimension(R.styleable.SelectProgressBar_select_circle_radius,mRadius);
            mLineStrokeWidth = (int) ta.getDimension(R.styleable.SelectProgressBar_line_stroke_width,mLineStrokeWidth);
            mMinValue = ta.getInteger(R.styleable.SelectProgressBar_min_value,DEFAULT_MIN_VALUE);
            mMaxValue = ta.getInteger(R.styleable.SelectProgressBar_max_value,DEFAULT_MAX_VALUE);
            mLineColor = ta.getColor(R.styleable.SelectProgressBar_line_color,DEFAULT_LINE_COLOR);
            mCircleColor = ta.getColor(R.styleable.SelectProgressBar_circle_color,DEFAULT_CIRCLE_COLOR);
            mTextSize = (int) ta.getDimension(R.styleable.SelectProgressBar_select_text_size,mTextSize);
            int style = ta.getInt(R.styleable.SelectProgressBar_style,0);
            mStyle = (style == 0)? Style.HORIZONTAL: Style.VERTICAL;
            ta.recycle();
        }
        mSelectedValue = mMinValue;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(mLineStrokeWidth);
        //note: here the round style doesn't work normally at Samsung mobile phone
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setTextSize(mTextSize);
        mDraggingBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDraggingBgPaint.setColor(0x55ffffff);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(MeasureSize(width, widthMode,1),
                MeasureSize(height, heightMode,2));
    }

    /**
     * 测量控件大小,measure size of the view
     * @param size
     * @param sizeMode
     * @param code 1 token width，2 token height
     * @return
     */
    private int MeasureSize(int size, int sizeMode,int code) {
        if (sizeMode == MeasureSpec.EXACTLY) {// 如果指定了明确的大小
            return size;
        } else {// 根据我们的情况设置大小
            int requireSize = 0;
            if(code == 1){//表示width,100是默认的线条长度
                if(mStyle == Style.HORIZONTAL){
                    requireSize = getPaddingLeft()+getPaddingRight()+mLineLength+(mRadius+10)*2;
                }else{
                    requireSize = getPaddingLeft() + getPaddingRight()+(mRadius+10)*2+getTextWidth()*2;
                }
            }else if(code == 2){//表示height
                if(mStyle == Style.HORIZONTAL){
                    requireSize = getPaddingBottom()+getPaddingTop()+mRadius*2+getTextHeight()*2;
                }else{
                    requireSize = getPaddingBottom() + getPaddingTop() + mLineLength + (mRadius+10)*2;
                }
            }
            if (sizeMode == MeasureSpec.AT_MOST) {
                requireSize = Math.min(size, requireSize);
            }
            return requireSize;
        }
    }

    /**
     * 获取文字的高度,get the text height
     * 在horizontal模式使用,不过我们在vertical模式下也需要获取baseline，所以
     * 在getTextWidth里需要调用一下本方法
     * @return
     */
    private int getTextHeight(){
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        mTextHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        mTextBaseLine = (-fontMetricsInt.ascent - fontMetricsInt.descent)/2;
        return mTextHeight;
    }

    /**
     * 获取文字的宽度,get text width
     * 在vertical模式使用
     * @return
     */
    private int getTextWidth(){
        getTextHeight();//do nothing,just for getting the baseline value
//        System.out.println("文字宽度"+mPaint.measureText(mMaxValue+""));
        return (int)mPaint.measureText(mMaxValue+"");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(mStyle == Style.HORIZONTAL){
            mLineLength = w - getPaddingLeft() - getPaddingRight() - (mRadius+10)*2;
            mCenterY = h/2;
        }else{
            mLineLength = h - getPaddingTop() - getPaddingBottom() - (mRadius+10)*2;
            mCenterX = w/2;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //note: 10 is the offset for dragging status, it's just draw the circle background to show the
    // circle at the dragging status
    @Override
    protected void onDraw(Canvas canvas) {
        //compute the offset to correctly show the circle location
        float offset = (mSelectedValue - mMinValue)*1.0f / (mMaxValue - mMinValue) * mLineLength;
        float selectedTextWidth = mPaint.measureText(mSelectedValue+"");
        float startTextWidth = mPaint.measureText(mMinValue+"");
        float endTextWidth = mPaint.measureText(mMaxValue+"");
        if(mStyle == Style.HORIZONTAL){
            int lineStartX = getPaddingLeft() + mRadius+10;//Horizontal mode,the start reference value of circle
            canvas.drawLine(lineStartX,mCenterY,
                    lineStartX+mLineLength,mCenterY,mPaint);
            mCenterX = lineStartX + offset;
            if(mIsDragging){
                canvas.drawCircle(mCenterX,mCenterY,mRadius+10,mDraggingBgPaint);
            }
            canvas.drawCircle(mCenterX,mCenterY,mRadius,mCirclePaint);
            int realBaseLine = (int)mCenterY + mTextBaseLine + mRadius+mTextHeight/2;
            //draw the min text
            canvas.drawText(mMinValue+"",lineStartX - startTextWidth/2, realBaseLine,mPaint);
            //draw the max text
            canvas.drawText(mMaxValue+"",lineStartX+mLineLength - endTextWidth/2,realBaseLine,mPaint);
        }else{
            int lineStartY = getPaddingTop() + mRadius+10;//Vertical mode,the start reference value of circle
            canvas.drawLine(mCenterX,lineStartY,mCenterX,lineStartY+mLineLength,mPaint);
            mCenterY = lineStartY + offset;
            if(mIsDragging){
                canvas.drawCircle(mCenterX,mCenterY,mRadius+10,mDraggingBgPaint);
            }
            canvas.drawCircle(mCenterX,mCenterY,mRadius,mCirclePaint);
            //draw the min text
            float startX = getPaddingLeft()+10-startTextWidth/2;
            canvas.drawText(mMinValue+"",startX<0?0:startX,lineStartY + mTextBaseLine,mPaint);
            //draw the max text
            startX = getPaddingLeft()+10-endTextWidth/2;
            canvas.drawText(mMaxValue+"",startX<0?0:startX,lineStartY+mLineLength+mTextBaseLine,mPaint);
        }
        //draw the selected text
        canvas.drawText(mSelectedValue+"", mCenterX -selectedTextWidth/2,mCenterY+mTextBaseLine,mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        if(mStyle == Style.HORIZONTAL){
            mCenterX = event.getX();
        }else{
            mCenterY = event.getY();
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mIsDragging = true;
            case MotionEvent.ACTION_MOVE:
                if(mStyle == Style.HORIZONTAL){
                    mSelectedValue = mMinValue + (int)((mCenterX - getPaddingLeft()-mRadius)/
                            mLineLength*(mMaxValue - mMinValue)+0.5f);
                }else{
                    mSelectedValue = mMinValue+(int)((mCenterY - getPaddingTop()-mRadius)/
                            mLineLength*(mMaxValue-mMinValue)+0.5f);
                }
                mSelectedValue = (mSelectedValue<mMinValue)?mMinValue:mSelectedValue;
                mSelectedValue = (mSelectedValue>mMaxValue)?mMaxValue:mSelectedValue;
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsDragging = false;
                if(mOnValueSelectedListener!=null){
                    mOnValueSelectedListener.onValueSelected(mSelectedValue);
                }
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    private OnValueSelectedListener mOnValueSelectedListener;

    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        mOnValueSelectedListener = onValueSelectedListener;
    }

    public interface OnValueSelectedListener{
        void onValueSelected(int selectedValue);
    }


    // dp值转px 参数整型值
    public static int dp2px(int dp, Context context) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics())+0.5f);
    }

    // sp 转px
    public static int sp2px(int sp, Context context) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics())+0.5f);
    }

}
