package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hudson.donglingmusic.R;

/*
 * 1.创建attrs.xml文件
 * 2.在自定义的View中获取attrs中的属性值
 * 		2.1声明我们的属性的成员变量，用于存储
 * 		2.2获取xml中的自定义的属性值
 * 3.onMeasure
 * 4.onDraw()
 * 存在的问题是：我们必须指定一下padding值，否则bar显示可能有问题（在padding为0时）
 * 解决办法：添加自定义属性padding，不接收系统的padding，无论是否设置系统的padding，都无效，生效的是我们自己的padding
 * 					为什么呢？因为padding是关乎View的自身内容的，需要View自己确定，不是ViewGroup，也就是padding值写了不代表
 * 					就生效，需要我们在代码中解析到。说白了，我们没有解析系统的padding，而是解析了我们自定义的padding
 */

public class CircleSeekBar extends View {

	// 默认值
	private static final int DEFAULT_RADIUS = 120;// 默认半径 dp
	private static final float DEFAULT_REACH_STROKE_WIDTH = 6;// 走过部分的画笔宽度，dp
	private static final float DEFAULT_UNREACH_STROKE_WIDTH = 9;//dp
	private static final int DEFAULT_REACH_COLOR = 0xff42aa0f;
	private static final int DEFAULT_UNREACH_COLOR = 0x55000000;
	//成员变量
	private int mRadius;
	private float mReachStrokeWidth;
	private float mUnreachStrokeWidth;
	private int mReachColor;
	private int mUnreachColor;
	private Paint mArcReachPaint;
	private Paint mArcUnReachPaint;
	private Bitmap mBarBitmap, mBarPressedBitmap;
	//用于把圆弧框起来
	private RectF mRectF;
	//圆心位置，注意，所有的坐标都是相对画布canvas而言的.
	private float mCenterX, mCenterY;
	//bar图片的中心坐标
	private float mBarBitmapCenterX, mBarBitmapCenterY;
	/*
	 * 触摸位置距离起始处的角度，起始就是我们生活中的那个坐标系
	 */
	private double mTouchPositionAngle;//注意是角度值，不是弧度值
	//当前位置占据的百分比
	private double mPercent;
	/*
	 * 触摸的位置
	 */
	private boolean isValidTouch;

    private int mDefaultOffset = 20;//px
    private float mPadding;


    public CircleSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleSeekBar(Context context) {
		this(context, null);
	}

	public CircleSeekBar(Context context, AttributeSet attrs,
                         int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context,attrs);
	}

	private void init(Context context,AttributeSet attrs) {
		mRadius = dp2px(DEFAULT_RADIUS, context);
		mReachStrokeWidth = dp2px(DEFAULT_REACH_STROKE_WIDTH,context);
		mUnreachStrokeWidth = dp2px(DEFAULT_UNREACH_STROKE_WIDTH,context);
		mUnreachColor = DEFAULT_UNREACH_COLOR;
		mReachColor = DEFAULT_REACH_COLOR;
		//获取自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleSeekBar);
		mRadius = (int) ta.getDimension(R.styleable.CircleSeekBar_seekbar_radius, mRadius);
		mReachStrokeWidth = ta.getDimension(R.styleable.CircleSeekBar_reach_stroke_width, mReachStrokeWidth);
		mUnreachStrokeWidth = ta.getDimension(R.styleable.CircleSeekBar_unreach_stroke_width, mUnreachStrokeWidth);
		mReachColor = ta.getColor(R.styleable.CircleSeekBar_seekbar_reach_color, mReachColor);
		mUnreachColor = ta.getColor(R.styleable.CircleSeekBar_seekbar_unreach_color, mUnreachColor);
		ta.recycle();
		mArcReachPaint = new Paint();
		mArcReachPaint.setAntiAlias(true);
		mArcReachPaint.setDither(true);
		mArcReachPaint.setColor(mReachColor);
		mArcReachPaint.setStyle(Style.STROKE);
		mArcReachPaint.setStrokeWidth(mReachStrokeWidth);
		mArcUnReachPaint = new Paint();
		mArcUnReachPaint.setAntiAlias(true);
		mArcUnReachPaint.setColor(mUnreachColor);
		mArcUnReachPaint.setStyle(Style.STROKE);
		mArcUnReachPaint.setStrokeWidth(mUnreachStrokeWidth);
		mBarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mp_seekbar_thumb);
		mBarPressedBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.mp_seekbar_thumb_pressed);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int realDimension = Math.min(getDimension(widthSize,widthMode,1),getDimension(heightSize,heightMode,2));
		setMeasuredDimension(realDimension, realDimension);//确定View的宽高
	}

	private int getDimension(int size,int mode,int type){
        int result;
        if(mode == MeasureSpec.EXACTLY){
            return size;
        }else{
            if(type == 1){
                result = (int) (mUnreachStrokeWidth + getPaddingLeft() + getPaddingRight() + mRadius*2+mDefaultOffset*2);
            }else{
                result = (int) (mUnreachStrokeWidth + getPaddingTop() + getPaddingBottom() + mRadius*2+mDefaultOffset*2);
            }
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }
            return result;
        }
    }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w/2;
        mCenterY = h/2;
        mPadding = (w - mRadius*2)/2;
        mBarBitmapCenterX = mRadius;//注意这个是图片的中心坐标，不是绘制的起始坐标
        mBarBitmapCenterY = 0;
		mRectF = new RectF(mPadding, mPadding, mPadding + mRadius*2, mPadding + mRadius*2);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(mRectF, -90, 360, false, mArcUnReachPaint);
		canvas.drawArc(mRectF, -90, (int) mTouchPositionAngle, false, mArcReachPaint);
		if(!isValidTouch){
			canvas.drawBitmap(mBarBitmap, mBarBitmapCenterX - mBarBitmap.getWidth()/2+mPadding, mBarBitmapCenterY +mPadding - mBarBitmap.getHeight()/2, null);
		}else{
			canvas.drawBitmap(mBarPressedBitmap, mBarBitmapCenterX - mBarBitmap.getWidth()/2+mPadding, mBarBitmapCenterY +mPadding - mBarBitmap.getHeight()/2, null);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);//禁止父view拦截事件
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float up_xoffset = event.getX() - mCenterX;
		float up_yoffset = event.getY() - mCenterY;
		float Offset = up_xoffset * up_xoffset + up_yoffset * up_yoffset
				- mRadius * mRadius;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (Math.abs(Offset) < 11000) {//判断点击是否有效
				isValidTouch = true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_MOVE:
            if(!isValidTouch){//如果down位置是在有效范围内，而且move的位置符合要求。（如果down位置不满足，但是move位置满足，我们不能修改位置）
                return true;
			}
			double tmp = 0;
			if (up_xoffset >= 0 && up_yoffset <= 0) {// 在圆形右上角
				tmp = getArcSin(up_xoffset, up_yoffset);
				mTouchPositionAngle = tmp;
				mBarBitmapCenterX = (float) (mRadius * (1 + Math.sin(tmp)));
				mBarBitmapCenterY = (float) (mRadius * (1 - Math.cos(tmp)));
			} else if (up_xoffset > 0 && up_yoffset > 0) {// 在圆形右下角
				tmp = getArcSin(up_yoffset, up_xoffset);
				mTouchPositionAngle = tmp + Math.PI / 2;
				mBarBitmapCenterX = (float)(mRadius *(1+Math.cos(tmp)));
				mBarBitmapCenterY = (float)(mRadius *(1+Math.sin(tmp)));
			} else if (up_xoffset < 0 && up_yoffset > 0) {// 在圆形左下角
				tmp = getArcSin(Math.abs(up_xoffset), up_yoffset);
				mTouchPositionAngle = Math.PI + tmp;
				mBarBitmapCenterX = (float)(mRadius *(1-Math.sin(tmp)));
				mBarBitmapCenterY = (float)(mRadius *(1+Math.cos(tmp)));
			} else if (up_xoffset <= 0 && up_yoffset <= 0) {//在圆形的左上角
				tmp = getArcSin(Math.abs(up_yoffset),Math.abs(up_xoffset));
				mTouchPositionAngle = Math.PI* 3/ 2+ tmp;
				mBarBitmapCenterX = (float)(mRadius *(1-Math.cos(tmp)));
				mBarBitmapCenterY = (float)(mRadius *(1-Math.sin(tmp)));
			}
			mPercent = mTouchPositionAngle / (2 * Math.PI);
			mTouchPositionAngle = mTouchPositionAngle *180/Math.PI;
			if(progressBarChangeListener!=null&&event.getAction() != MotionEvent.ACTION_MOVE){
                //只有进度调整结束了，我们才回调，避免过多回调seekTo
				progressBarChangeListener.onProgressBarChange();
                isValidTouch = false;
			}
            invalidate();
			break;
		}
		return true;
	}

	public int getCurProgress(){
        return (int) (duration*mPercent);
    }
	
	public double getTouchPercent(){
		return mPercent;
	}
	
	private int duration;
	public void setMax(int duration){
		this.duration = duration;
	}
	
	public void updateProgressBar(int curProgress){
        if(isValidTouch){//如果用户正在拖拽，那么我们不更新来自播放器的进度
            return ;
        }
		mTouchPositionAngle = curProgress*1.0f/duration*360;
		double tmp = mTouchPositionAngle *Math.PI/180;
		if(mTouchPositionAngle <=90){
			mBarBitmapCenterX = (float) (mRadius *(Math.sin(tmp)+1));
			mBarBitmapCenterY = (float)(mRadius *(1-Math.cos(tmp)));
		}else if(mTouchPositionAngle <= 180){
			tmp = tmp - Math.PI/2;
			mBarBitmapCenterX = (float) (mRadius *(Math.cos(tmp)+1));
			mBarBitmapCenterY = (float)(mRadius *(1+Math.sin(tmp)));
		}else if(mTouchPositionAngle <= 270){
			tmp = tmp - Math.PI;
			mBarBitmapCenterX = (float) (mRadius *(1-Math.sin(tmp)));
			mBarBitmapCenterY = (float)(mRadius *(1+Math.cos(tmp)));
		}else {
			tmp = tmp - Math.PI*3/2;
			mBarBitmapCenterX = (float) (mRadius *(1-Math.cos(tmp)));
			mBarBitmapCenterY = (float)(mRadius *(1-Math.sin(tmp)));
		}
		invalidate();
	}
	
	private onProgressBarChangeListener progressBarChangeListener;
	public void setOnProgressBarChangeListener(onProgressBarChangeListener listener){
		progressBarChangeListener = listener;
	}
	public interface onProgressBarChangeListener{
        void onProgressBarChange();
	}
	/**
	 * 获取对应的sin反三角
	 * x是角所对应的边
	 */
	private double getArcSin(float x, float y) {
		double longWay = Math.sqrt(x * x + y * y);
		double result = Math.asin(x / longWay);
		return result;
	}

	/*
	 * 单位的转换
	 */
	public  int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	
	public  float dp2px(float dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	public  int sp2px(int sp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				context.getResources().getDisplayMetrics());
	}
}
