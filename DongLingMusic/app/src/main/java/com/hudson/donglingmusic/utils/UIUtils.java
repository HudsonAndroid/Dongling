package com.hudson.donglingmusic.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;

import com.hudson.donglingmusic.global.MyApplication;

public class UIUtils {

	public static Context getContext() {
		return MyApplication.getContext();
	}

	public static Handler getHandler() {
		return MyApplication.getmHandler();
	}

	public static int getMainThreadId() {
		return MyApplication.getMainThreadId();
	}

	/**
	 * 获取一个资源文件中的字符串
	 * 
	 * @param id
	 * @return
	 */
	public static String getString(int id) {
		return getContext().getResources().getString(id);
	}

	/**
	 * 获取一个资源文件中的字符串数组
	 * 
	 * @param id
	 * @return
	 */
	public static String[] getStringArray(int id) {
		return getContext().getResources().getStringArray(id);
	}

	/**
	 * 获取一个资源文件中的图片
	 * 
	 * @param id
	 * @return
	 */
	public static Drawable getDrawable(int id) {
		return getContext().getResources().getDrawable(id);
	}

	/**
	 * 获取一个资源文件中的颜色
	 * 
	 * @param id
	 * @return
	 */
	public static int getColor(int id) {
		return getContext().getResources().getColor(id);
	}

	/**
	 * 获取一个资源文件中的尺寸值
	 * 
	 * @param id
	 * @return 像素值
	 */
	public static float getDimension(int id) {
		return getContext().getResources().getDimensionPixelSize(id);
	}

	/**
	 * 将dp值转成px值
	 * @param dp
	 * @return 像素 int
	 */
	public static int dp2px(int dp) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dp, getContext().getResources().getDisplayMetrics()) + 0.5f);
	}
	/**
	 * 将dp转成px
	 * @param dp
	 * @return 像素 float
	 */
	public static float dp2px(float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}
	/**
	 * 将sp转成px
	 * @param sp
	 * @return 像素 int 
	 */
	public static int sp2px(int sp) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				getContext().getResources().getDisplayMetrics()) + 0.5f);
	}
	/**
	 * 将sp转成px
	 * @param sp
	 * @return 像素float
	 */
	public static float sp2px(float sp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				getContext().getResources().getDisplayMetrics());
	}
	
	//===========布局加载============
	/**
	 * 加载一个布局文件
	 * @param resId
	 * @return View控件
	 */
	public static View inflate(int resId){
		return View.inflate(getContext(), resId, null);
	}
	/**
	 * 判断当前线程是否在UI线程
	 * @return
	 */
	public static boolean isRunOnUIThread(){
		//获取当前的线程id是否与主线程id一致
		if(android.os.Process.myTid()==getMainThreadId()){
			return true;
		}
		return false;
	}
	/**
	 * 在UI线程中执行该内容
	 * @param r
	 */
	public static void runOnUIThread(Runnable r){
		if(isRunOnUIThread()){//如果是主线程
			r.run();
		}else {//如果不是主线程，使用handler使它在主线程中运行
			getHandler().post(r);
		}
	}
	
}
