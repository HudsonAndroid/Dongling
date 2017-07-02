package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.hudson.donglingmusic.utils.MusicUtils;

/**
 * 可以让activity实现侧滑的效果的relativeLayout
 * 使用前提是activity必须是透明的
 */
public class SlideRelativeLayout extends RelativeLayout {

	private Scroller mScroller;
	private ViewGroup viewGroup;
	private int viewWidth;
	private boolean isFinish;
	public boolean PlayPageIsNotFirst;//表示playpage不在界面1

	// 构造方法
	public SlideRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mScroller = new Scroller(context);
		detector = new GestureDetector(context, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {

				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (velocityX > 150 && velocityY < -50) {
					isFliding = true;
					MusicUtils.startRotateExitAnimation(viewWidth + disX,
							viewGroup.getHeight(), viewGroup);
					disX = 0;// 取消偏移量
					handler.sendEmptyMessageDelayed(0, 800);
				}
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {

				return false;
			}
		});
	}


	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(mOnFinishedListener !=null)
			mOnFinishedListener.whileFinished();// 由子类去实现
		};
	};

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 获取SildingFinishLayout所在布局的父布局
			viewGroup = (ViewGroup) this.getParent();
			viewWidth = this.getWidth();
		}
	}

	public boolean IsChooseColor = false;
	// 消除其他控件的干扰
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean get = false;
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			firstX = (int) ev.getX();
			firstY = (int)ev.getY();
			detector.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			if(PlayPageIsNotFirst){//传给子view
				get = false;
			}else {
				int distanceY = (int) Math.abs(ev.getY() - firstY);
				int distanceX = (int) (ev.getX() - firstX);
				if(Math.abs(distanceX) >distanceY&&distanceX >10){//如果X方向上移动的更大，那么就使得整体左右移动
					get = true;//消费掉这个事件，执行我这个ViewGroup的OnTouch事件
				}else {//否则移动内部的控件的上下方向
					get =false;//传递给子view
				}
			}
			if(IsChooseColor){//不再消费该事件
				get = false;
			}
			break;
		}
		return get;
	}

	private GestureDetector detector;
	private int firstX;
	private int firstY;
	private boolean isFliding;
	private int disX;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		detector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			firstX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if (!isFliding) {
				disX /* = tempDisX */= (int) (event.getX() - firstX);
				if (Math.abs(disX) > 10) {// 确定是否是滑动了
					if (viewGroup.getScrollX() == 0 && disX < 0) {// 开始的时候如果向左滑动，我就不让他滑动
					} else {
						viewGroup.scrollBy(-disX, 0);//坐标系跟我们想的相反
					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			if (!isFliding) {
				int scrollX = viewGroup.getScrollX();
				if (scrollX < -viewWidth / 4) {// 向右超过一半
					mScroller.startScroll(scrollX, 0,
							-(scrollX + viewWidth) + 1, 0,
							500);// 第三个参数的正负决定了滑动的方向，
					// 负值是向右.这里注意需要加1让他在上一次的滑动中就完成，然后提示完成了滑动过程
					isFinish = true;
				} else {// 其他情况复原
					mScroller.startScroll(scrollX, 0, -scrollX, 0,
							500);
					isFinish = false;
				}
				postInvalidate();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			long newX = mScroller.getCurrX();
			viewGroup.scrollTo((int) newX, 0);
			postInvalidate();
			if (mScroller.isFinished()) {// 注意这里调用的是mScroller的属性.
											// 不清楚为什么mScroller返回的一直是false，所以注释掉了
				if (mOnFinishedListener != null && isFinish) {
					mOnFinishedListener.whileFinished();// 由子类去实现
				}
			}
		}
	}

	public OnFinishedListener getOnFinishedListener() {
		return mOnFinishedListener;
	}

	public void setOnFinishedListener(OnFinishedListener onFinishedListener) {// 设置子类的监听器
		this.mOnFinishedListener = onFinishedListener;
	}

	private OnFinishedListener mOnFinishedListener;

	public interface OnFinishedListener {
		void whileFinished();
	}
}
