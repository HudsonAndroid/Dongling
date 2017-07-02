package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.hudson.donglingmusic.R;

//卫星菜单
public class ArcMenu extends ViewGroup implements OnClickListener {
	private static final int POS_RGIHT_BOTTOM = 3;
	private static final int POS_LEFT_BOTTOM = 1;
	private static final int POS_LEFT_TOP = 0;
	private static final int POS_RGIHT_TOP = 2;
	public static final int CLOSED = 0;
	public static final int OPEN = 1;
	private Position mposition = Position.RIGHT_BOTTOM;
	private int Radius;
	// 菜单的状态
	public int  mStatus = CLOSED;
	// 菜单的主按钮
	private View mCButton;
	private OnMenuItemClickListener menuItemClickListener;
	private OnCenterMenuClickListener centerMenuClickListener;
	private int childCount;

	public enum Position {// 菜单的位置的枚举类
		LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
	}

	public ArcMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArcMenu(Context context) {
		this(context, null);
	}

	public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		Radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				100, getResources().getDisplayMetrics());
		// 获取自定义属性的值
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.ArcMenu, defStyleAttr, 0);
		int pos = a.getInt(R.styleable.ArcMenu_position, 3);
		switch (pos) {
		case POS_LEFT_TOP:
			mposition = Position.LEFT_TOP;
			break;
		case POS_LEFT_BOTTOM:
			mposition = Position.LEFT_BOTTOM;
			break;
		case POS_RGIHT_TOP:
			mposition = Position.RIGHT_TOP;
			break;
		case POS_RGIHT_BOTTOM:
			mposition = Position.RIGHT_BOTTOM;
			break;

		}

		Radius = (int) a.getDimension(R.styleable.ArcMenu_radius, TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
						getResources().getDisplayMetrics()));
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		childCount = getChildCount();
		// 测量child
		for (int i = 0; i < childCount; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {// 如果发生改变才执行
			layoutCbutton();
			// 定位菜单项，根据圆形的数学知识获得
			View child;
			int cl,ct,cWidth,cHeight;
			for (int i = 0; i < childCount - 1; i++) {
				child = getChildAt(i + 1);
				child.setVisibility(View.GONE);
				cl = (int) (Radius * Math.sin(Math.PI / 2
						/ (getChildCount() - 2) * i));
				ct = (int) (Radius * Math.cos(Math.PI / 2
						/ (getChildCount() - 2) * i));
				cWidth = child.getMeasuredWidth();
				cHeight = child.getMeasuredHeight();

				// 如果菜单在底部
				if (mposition == Position.LEFT_BOTTOM
						|| mposition == Position.RIGHT_BOTTOM) {
					ct = getMeasuredHeight() - cHeight - ct;
				}
				// 右上，右下
				if (mposition == Position.RIGHT_TOP
						|| mposition == Position.RIGHT_BOTTOM) {
					cl = getMeasuredWidth() - cWidth - cl;
				}
				child.layout(cl, ct, cl + cWidth, ct + cHeight);
			}
		}
	}

//	/*
//	 * 按钮点击导致的歌曲状态发生变化，这时候需要修改按钮的图标
//	 */
//	public void OnMusicChange() {
//		int curPlayState = sp.getPlayState();
//		if (curPlayState != lastPlayState) {
//			View view = getChildAt(3);
//			if (view.getTag().toString().equals("play")) {
//				if (curPlayState == PAUSE) {
//					((ImageView) view).setImageResource(R.drawable.move_play);
//				} else if (curPlayState == PLAYING) {
//					((ImageView) view).setImageResource(R.drawable.move_pause);
//				}
//				lastPlayState = curPlayState;
//			}
//		}
//	}
//
//	public void OnPlayModeChange() {
//		View view1 = getChildAt(5);// 顺序播放
//		View view2 = getChildAt(1);// 随机
//		switch (sp.getPlayMode()) {
//		case LIST_LOOPING:
//			((ImageView) view1).setImageResource(R.drawable.move_repeat_list);
//			((ImageView) view2)
//					.setImageResource(R.drawable.move_shuffle_closed);
//			break;
//		case ONE_LOOPING:
//			((ImageView) view1).setImageResource(R.drawable.move_repeat_one);
//			((ImageView) view2)
//					.setImageResource(R.drawable.move_shuffle_closed);
//			break;
//		case ORDER:
//			((ImageView) view1).setImageResource(R.drawable.move_repeat_order);
//			((ImageView) view2)
//					.setImageResource(R.drawable.move_shuffle_closed);
//			break;
//		case SHUFFLE:
//			((ImageView) view2)
//					.setImageResource(R.drawable.move_repeat_shuffle);
//			((ImageView) view1).setImageResource(R.drawable.move_repeat_order);
//			break;
//		}
//	}

	/*
	 * 点击子菜单项的回调接口
	 */
	public interface OnMenuItemClickListener {
		/**
		 * 注意这里的position是从1开始的
		 * @param view
		 * @param position 从1开始的数字
		 */
		void OnClick(View view, int position);
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener menuClickListener) {
		this.menuItemClickListener = menuClickListener;
	}

	// 定位主菜单按钮
	private void layoutCbutton() {
		mCButton = getChildAt(0);
		mCButton.setOnClickListener(this);
		int l = 0;
		int t = 0;
		int width = mCButton.getMeasuredWidth();
		int height = mCButton.getMeasuredHeight();
		switch (mposition) {
		case LEFT_TOP:
			l = 0;
			t = 0;
			break;
		case LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;
		case RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;
		case RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			break;

		}
		mCButton.layout(l, t, l + width, t + width);
	}

	@Override
	public void onClick(View v) {
		// //确定mcbutton
		// mCButton = findViewById(R.id.id_button);
		// if(mCButton == null){
		// mCButton = getChildAt(0);//第二种方式获取
		// }
		if(centerMenuClickListener!=null){
			centerMenuClickListener.OnCenterClick(v);
		}
		
		rotateCButton(mCButton, 0f, 360f, 300);
		toggleMenu(300);// 如果菜单展开则关闭
		if(mposition==Position.RIGHT_BOTTOM){
//			OnMusicChange();// 检查歌曲状态是否发生变化
//			OnPlayModeChange();// 检查模式是否发生变化
		}
	}
	
	/*
	 * 点击子菜单项的回调接口
	 */
	public interface OnCenterMenuClickListener {
		void OnCenterClick(View view);
	}

	public void setOnCenterMenuClickListener(
			OnCenterMenuClickListener CenterMenuClickListener) {
		this.centerMenuClickListener = CenterMenuClickListener;
	}

	
	/*
	 * 切换菜单
	 */
	public void toggleMenu(int duration) {
		// 为MenuItem添加平移动画和旋转动画
		int cl,ct,xFlag,yFlag;
		AnimationSet animationSet;
		Animation tranAnimation;
		RotateAnimation rotateAnimation;
		for (int i = 0; i < childCount - 1; i++) {
			final View childView = getChildAt(i + 1);
			childView.setVisibility(View.VISIBLE);
			// end 0,0
			// start
			cl = (int) (Radius * Math.sin(Math.PI / 2
					/ (getChildCount() - 2) * i));
			ct = (int) (Radius * Math.cos(Math.PI / 2
					/ (getChildCount() - 2) * i));

			xFlag = 1;
			yFlag = 1;

			if (mposition == Position.LEFT_TOP
					|| mposition == Position.LEFT_BOTTOM) {
				xFlag = -1;
			}
			if (mposition == Position.LEFT_TOP
					|| mposition == Position.RIGHT_TOP) {
				yFlag = -1;
			}

			animationSet = new AnimationSet(true);
			tranAnimation = null;
			// to open
			if (mStatus == CLOSED) {
				tranAnimation = new TranslateAnimation(xFlag * cl, 0, yFlag
						* ct, 0);
				childView.setClickable(true);
				childView.setFocusable(true);
			} else {// to close
				tranAnimation = new TranslateAnimation(0, xFlag * cl, 0, yFlag
						* ct);
				childView.setClickable(false);
				childView.setFocusable(false);
			}
			tranAnimation.setFillAfter(true);
			tranAnimation.setDuration(duration);
			tranAnimation.setStartOffset(i * 300 / childCount);// 设置开始动画的时间，这里跟i有关
			tranAnimation.setAnimationListener(new AnimationListener() {// 监听动画结束

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							if (mStatus == CLOSED) {
								childView.setVisibility(View.GONE);
							}
						}
					});
			// 旋转动画
			rotateAnimation = new RotateAnimation(0, 720,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(duration);
			rotateAnimation.setFillAfter(true);
			// 注意动画添加的前后也有影响
			animationSet.addAnimation(rotateAnimation);
			animationSet.addAnimation(tranAnimation);
			childView.startAnimation(animationSet);

			final int position = i + 1;
			// 设置子按钮的点击事件
			childView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (menuItemClickListener != null)
						menuItemClickListener.OnClick(childView, position);
					menItemAnimation(position - 1);// 被点击的动画
					changeStatus();
				}
			});
		}
		changeStatus();
		if(mOnClickMenuToggleListener!=null){
			mOnClickMenuToggleListener.onClickMenuToggle(mStatus);
		}
	}

	private onClickMenuToggleListener mOnClickMenuToggleListener;

	public void setOnClickMenuToggleListener(onClickMenuToggleListener onClickMenuToggleListener) {
		mOnClickMenuToggleListener = onClickMenuToggleListener;
	}

	/**
	 * 点击使得菜单打开和关闭的监听器，不是菜单的打开关闭监听器
	 */
	public interface onClickMenuToggleListener{
		void onClickMenuToggle(int status);
	}

	public void closeMenu(int duration) {//专为activity设定
		// 为MenuItem添加平移动画和旋转动画
		int cl,ct,xFlag,yFlag;
		AnimationSet animationSet;
		Animation tranAnimation;
		RotateAnimation rotateAnimation;
		for (int i = 0; i < childCount - 1; i++) {
			final View childView = getChildAt(i + 1);
			childView.setVisibility(View.VISIBLE);
			// end 0,0
			// start
			cl = (int) (Radius * Math.sin(Math.PI / 2
					/ (getChildCount() - 2) * i));
			ct = (int) (Radius * Math.cos(Math.PI / 2
					/ (getChildCount() - 2) * i));

			xFlag = 1;
			yFlag = 1;

			if (mposition == Position.LEFT_TOP
					|| mposition == Position.LEFT_BOTTOM) {
				xFlag = -1;
			}
			if (mposition == Position.LEFT_TOP
					|| mposition == Position.RIGHT_TOP) {
				yFlag = -1;
			}

			animationSet = new AnimationSet(true);
			tranAnimation = new TranslateAnimation(0, xFlag * cl, 0, yFlag * ct);
			childView.setClickable(false);
			childView.setFocusable(false);
			tranAnimation.setFillAfter(true);
			tranAnimation.setDuration(duration);
			tranAnimation.setStartOffset(i * 100 / childCount);// 设置开始动画的时间，这里跟i有关
			tranAnimation.setAnimationListener(new AnimationListener() {// 监听动画结束

						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							if (mStatus == CLOSED) {
								childView.setVisibility(View.GONE);
							}
						}
					});
			// 旋转动画
			rotateAnimation = new RotateAnimation(0, 720,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(duration);
			rotateAnimation.setFillAfter(true);
			// 注意动画添加的前后也有影响
			animationSet.addAnimation(rotateAnimation);
			animationSet.addAnimation(tranAnimation);
			childView.startAnimation(animationSet);
		}
		changeStatus();
	}

	private Handler handler = new Handler() {
	};

	// 添加menuItem的点击动画
	private void menItemAnimation(int pos) {
		View childView;
		for (int i = 0; i < childCount - 1; i++) {
			childView = getChildAt(i + 1);
			if (i == pos) {// 如果是被点击的内容
				childView.startAnimation(scaleBigAnimation(300));
			} else {
				childView.startAnimation(scaleSmallAnimation(300));
			}
			childView.setClickable(false);
			childView.setFocusable(false);
		}

	}

	/*
	 * 为当前点击的item设置变大动画和透明度降低的动画
	 */
	public  static Animation scaleBigAnimation(int duration) {
		AnimationSet aniSet = new AnimationSet(true);

		ScaleAnimation scaleAni = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 透明度
		AlphaAnimation alphaAni = new AlphaAnimation(1.0f, 0.0f);
		aniSet.addAnimation(scaleAni);
		aniSet.addAnimation(alphaAni);

		aniSet.setDuration(duration);
		aniSet.setFillAfter(true);

		return aniSet;
	}

	/*
	 * 为当前的其他item设置变小动画
	 */
	private Animation scaleSmallAnimation(int duration) {
		AnimationSet aniSet = new AnimationSet(true);

		ScaleAnimation scaleAni = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 透明度
		AlphaAnimation alphaAni = new AlphaAnimation(1.0f, 0.0f);
		aniSet.addAnimation(scaleAni);
		aniSet.addAnimation(alphaAni);

		aniSet.setDuration(duration);
		aniSet.setFillAfter(true);

		return aniSet;
	}

	// 切换菜单状态
	private void changeStatus() {
		mStatus = (mStatus == CLOSED ? OPEN : CLOSED);
	}

	private void rotateCButton(View v, float start, float end, int duration) {
		RotateAnimation animation = new RotateAnimation(start, end,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}

}
