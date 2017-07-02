package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hudson.donglingmusic.R;

import java.util.ArrayList;

/*
 * 步骤进度条
 * by Hudson
 * 2016-11-29
 */
public class StepProgressView extends View {

	private static final int DEFAULT_CIRCLE_RADIUS = 8;// dp 默认圆球半径
	private static final int DEFAULT_LINE_LENGTH = 80;// dp 默认的进度线长度
	private static final int DEFAULT_TEXT_SIZE = 14;// sp 默认的字体大小
	private static final int DEFAULT_OFFSET = 3;// dp 默认背景与视图的偏差
	private static final int DEFAULT_STEP_COUNTS = 3;// 默认的步骤数
	private static final int DEFAULT_PADDING = 2;// dp 默认的padding值
	private static final int DEFAULT_REACHED_COLOR = 0x5533CC33;// 已经通过的步骤的颜色
	private static final int DEFAULT_CUR_COLOR = 0xff33CC33;// 当前的步骤
	private static final int DEFAULT_BG_COLOR = 0xffB9B9B9;// 背景颜色
	private static final int DEFAULT_NUMBER_COLOR = Color.WHITE;// 圆圈中的数字的字体颜色
	private static final int DEFAULT_STROKE_WIDTH = 8;// dp,进度线条的宽度

	private int circleRadius;// 圆形半径
	private int lineLength;// 进度线的长度
	private int strokeWidth;// 进度线的宽度
	private int textSize;// 文字大小
	private int offset;// 灰色背景与高亢前景之间的偏差
	private int stepCounts;// 步骤的个数
	private int paddingLeft, paddingRight, paddingTop, paddingBottom;// 内容的padding值
	private Paint textPaint;// 文字的画笔
	private Paint showPaint;// 高亢部分的画笔
	private Paint bgPaint;// 灰色背景的画笔
	private int reachedColor, curColor, bgColor;// 已过步骤的颜色、当前步骤的颜色、灰色背景的颜色
	private int curStep;// 当前的步骤
	private String[] stepDesc;// 描述步骤的字符串数组
//	// 加粗字体
//	private Typeface boldTypeface = Typeface.create(Typeface.SANS_SERIF,
//			Typeface.BOLD);
//	// 普通字体
//	private Typeface normalTypeface = Typeface.create(Typeface.SANS_SERIF,
//			Typeface.NORMAL);
	// 圆形的圆心集合
	private ArrayList<Point> centerPoints;
	// 是否自动根据宽度适配（如果为true，那么lineLength将会被修改，而不是设定的值，在onSizeChanged方法中）
	private boolean autoAdapte = false;
	/*
	 * 描述文字距离上方的圆形的margin
	 */
	private int textMargin = 10;

	private class Point {
		public float x, y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * 获取步骤的总个数
	 * 
	 * @return
	 */
	public int getStepCounts() {
		return stepCounts;
	}

	/**
	 * 获取当前的步骤的id
	 * 
	 * @return
	 */
	public int getCurStepIndex() {
		return curStep;
	}

	/**
	 * 设置当前的步骤的id
	 * 
	 * @param stepId
	 */
	public void setCurStepIndex(int stepId) {
		if (stepId >= stepCounts | stepId < 0) {
			try {
				throw new Exception("设置的id必须大于0且小于" + stepCounts);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		curStep = stepId;
		invalidate();
	}

	/**
	 * 设置步骤的描述内容
	 * 
	 * @param strs
	 */
	public void setStepDesc(String[] strs) {
		if (strs.length < stepCounts) {
			try {
				throw new Exception("错误，步骤描述过少");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		} else {
			for (int i = 0; i < stepCounts; i++) {
				stepDesc[i] = strs[i];
			}
		}
	}

	public StepProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public StepProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StepProgressView(Context context) {
		this(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		// 固定值
		lineLength = dp2px(DEFAULT_LINE_LENGTH, context);
		offset = dp2px(DEFAULT_OFFSET, context);
		// 可以通过属性设置的值
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.StepProgressView);
		textSize = (int) sp2px(ta.getDimension(
				R.styleable.StepProgressView_text_size, DEFAULT_TEXT_SIZE),
				context);
		circleRadius = (int) dp2px(ta.getDimension(
				R.styleable.StepProgressView_circle_radius,
				DEFAULT_CIRCLE_RADIUS), context);
		strokeWidth = (int) dp2px(
				ta.getDimension(R.styleable.StepProgressView_stroke_width,
						DEFAULT_STROKE_WIDTH), context);
		stepCounts = ta.getInteger(R.styleable.StepProgressView_step_counts,
				DEFAULT_STEP_COUNTS);
		paddingLeft = (int) dp2px(ta.getDimension(
				R.styleable.StepProgressView_padding_left, DEFAULT_PADDING),
				context);
		paddingRight = (int) dp2px(ta.getDimension(
				R.styleable.StepProgressView_padding_right, DEFAULT_PADDING),
				context);
		paddingTop = (int) dp2px(ta.getDimension(
				R.styleable.StepProgressView_padding_top, DEFAULT_PADDING),
				context);
		paddingBottom = (int) dp2px(ta.getDimension(
				R.styleable.StepProgressView_padding_bottom, DEFAULT_PADDING),
				context);
		reachedColor = ta.getColor(R.styleable.StepProgressView_reached_color,
				DEFAULT_REACHED_COLOR);
		curColor = ta.getColor(R.styleable.StepProgressView_cur_color,
				DEFAULT_CUR_COLOR);
		bgColor = ta.getColor(R.styleable.StepProgressView_bg_color,
				DEFAULT_BG_COLOR);
		autoAdapte = ta.getBoolean(R.styleable.StepProgressView_auto_adapte,
				false);
		ta.recycle();
		stepDesc = new String[stepCounts];
		for (int i = 0; i < stepCounts; i++) {
			stepDesc[i] = "描述步骤";
		}
		centerPoints = new ArrayList<Point>();
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setDither(true);
		textPaint.setTextSize(textSize);
		showPaint = new Paint();
		showPaint.setAntiAlias(true);
		showPaint.setDither(true);
		showPaint.setStrokeWidth(strokeWidth);
		showPaint.setStyle(Style.FILL);
		showPaint.setColor(reachedColor);
		bgPaint = new Paint();
		bgPaint.setAntiAlias(true);
		bgPaint.setDither(true);
		bgPaint.setStrokeWidth(strokeWidth + offset * 2);
		bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(bgColor);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {// onMeasure方法一开始测的都是0，一旦执行本方法之后就说明width获取成功了
	// System.out.println("大小变化了"+getWidth());
		if (autoAdapte) {
			lineLength = (getWidth() - paddingLeft - paddingRight - (circleRadius + offset)
					* 2 * stepCounts)
					/ (stepCounts - 1);
			if (lineLength < 0) {
				lineLength = 0;
				try {
					throw new Exception("你设置的步骤数过多导致进度线空间不够，请设置更小圆形的半径");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

			}
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	// 根据用户给定的长宽设定自身的view的大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(MeasureWidth(width, widthMode),
				MeasureHeight(height, heightMode));
	}

	private int MeasureWidth(int width, int widthMode) {
		if (widthMode == MeasureSpec.EXACTLY) {// 如果指定了明确的大小
			return width;
		} else {// 根据我们的情况设置大小
			int requireWidth = paddingLeft + (circleRadius + offset) * 2
					* stepCounts + lineLength * (stepCounts - 1) + paddingRight;
			if (widthMode == MeasureSpec.AT_MOST) {
				requireWidth = Math.min(width, requireWidth);
			}
			return requireWidth;
		}
	}

	private int MeasureHeight(int height, int heightMode) {
		if (heightMode == MeasureSpec.EXACTLY) {// 如果指定了明确的大小
			return height;
		} else {// 根据我们的情况设置大小
			int requireHeight = (int) (textPaint.descent() - textPaint.ascent()
					+ paddingBottom + paddingTop + textMargin)
					+ (circleRadius + offset) * 2;
			if (heightMode == MeasureSpec.AT_MOST) {
				requireHeight = Math.min(height, requireHeight);
			}
			return requireHeight;
		}
	}

	private boolean hasInit = false;

	@Override
	protected void onDraw(Canvas canvas) {
		if (!hasInit) {
			float cx = paddingLeft + offset + circleRadius;
			float cy = paddingTop + offset + circleRadius;
			for (int i = 0; i < stepCounts; i++) {
				centerPoints.add(new Point(cx + i
						* (lineLength + (offset + circleRadius) * 2), cy));
			}
		}
		drawCircles(canvas);
		drawLines(canvas);
		drawText(canvas);
	}

	private void drawText(Canvas canvas) {
		for (int i = 0; i < stepCounts; i++) {
			if (i == curStep) {
				textPaint.setColor(curColor);
				textPaint.setFakeBoldText(true);
			} else if (i < curStep) {
				textPaint.setFakeBoldText(false);
				textPaint.setColor(reachedColor);
			} else {
				textPaint.setFakeBoldText(false);
				textPaint.setColor(bgColor);
			}
			// 绘制描述内容
			float textWidth = textPaint.measureText(stepDesc[i]);
			float x = centerPoints.get(i).x - textWidth / 2;
			if (centerPoints.get(i).x + textWidth / 2 > getWidth()) {
				x = getWidth() - textWidth;
			}
			canvas.drawText(stepDesc[i], (x < 0 ? 0 : x), centerPoints.get(i).y
					+ circleRadius + offset + textMargin + (textPaint.descent() - textPaint.ascent()) / 2,
					textPaint);
			// 绘制步骤号码
			textPaint.setColor(DEFAULT_NUMBER_COLOR);
			String stepNum = (i + 1) + "";
			x = centerPoints.get(i).x - textPaint.measureText(stepNum) / 2;
			canvas.drawText(stepNum, (x < 0 ? 0 : x), centerPoints.get(i).y
					+ (Math.abs(textPaint.ascent())-textPaint.descent())/2, textPaint);
		}
	}

	// 画线
	private void drawLines(Canvas canvas) {
		float startX = paddingLeft + (offset + circleRadius) * 2;
		float startY = paddingTop + offset + circleRadius;
		// 画背景
		for (int i = 0; i < stepCounts - 1; i++) {
			canvas.drawLine(startX + (lineLength + (circleRadius + offset) * 2)
					* i - offset, startY, startX
					+ (lineLength + (circleRadius + offset) * 2) * i
					+ lineLength + offset, startY, bgPaint);
		}
		// 画显示部分
		for (int i = 0; i < curStep; i++) {
			canvas.drawLine(startX + (lineLength + (circleRadius + offset) * 2)
					* i - offset, startY, startX
					+ (lineLength + (circleRadius + offset) * 2) * i
					+ lineLength + offset, startY, showPaint);
		}
	}

	// 画圆
	private void drawCircles(Canvas canvas) {
		// 画背景
		for (int i = 0; i < stepCounts; i++) {
			canvas.drawCircle(centerPoints.get(i).x, centerPoints.get(i).y,
					circleRadius + offset, bgPaint);
		}
		// 画显示部分，必须在画背景之后，否则会被覆盖
		for (int i = 0; i <= curStep; i++) {
			if (i == curStep) {
				showPaint.setColor(curColor);
			}
			canvas.drawCircle(centerPoints.get(i).x, centerPoints.get(i).y,
					circleRadius, showPaint);
		}
		showPaint.setColor(reachedColor);// 将颜色还原（后面的drawLines还要使用）
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			checkClickIsInCircle(event);
			break;
		}
		return true;
	}

	private void checkClickIsInCircle(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		Point point;
		for (int i = 0; i < stepCounts; i++) {
			point = centerPoints.get(i);
			if (eventX > point.x - circleRadius
					&& eventY > point.y - circleRadius
					&& eventX < point.x + circleRadius
					&& eventY < point.y + circleRadius) {
				setCurStepIndex(i);
				if (mClickListener != null) {
					mClickListener.onItemClick(i);
				}
			}
		}
	}

	public interface ItemClickListener {
		public void onItemClick(int i);
	}

	private ItemClickListener mClickListener;

	public void setOnItemClickListener(ItemClickListener mClickListener) {
		this.mClickListener = mClickListener;
	}

	// dp值转px 参数整型值
	public int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	// dp值转PX 参数浮点数
	public float dp2px(float dp, Context context) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	// sp 转px
	public int sp2px(int sp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				context.getResources().getDisplayMetrics());
	}

	// sp 转px
	public float sp2px(float sp, Context context) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				context.getResources().getDisplayMetrics());
	}

}
