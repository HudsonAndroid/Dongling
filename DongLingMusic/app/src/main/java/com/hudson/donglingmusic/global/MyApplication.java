package com.hudson.donglingmusic.global;

import android.content.Context;
import android.os.Handler;

import com.hudson.donglingmusic.db.MusicInfo;

import org.litepal.LitePalApplication;

import java.util.ArrayList;

/**
 * 全局初始化,将常用的放到这里
 * @author Hudson
 *
 */
public class MyApplication extends LitePalApplication {
	private static Context context;
	private static Handler mHandler;
	private static int mainThreadId;
	private static MyApplication mApplication = null;

	public static MyApplication getMyApplication() {
		return mApplication;
	}

	public ArrayList<MusicInfo> getAllList() {
		return mAllList;
	}

	public void setAllList(ArrayList<MusicInfo> allList) {
		mAllList = allList;
	}

	private ArrayList<MusicInfo> mAllList;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		context = getApplicationContext();
		mHandler = new Handler();
		//获取线程id，由于当前是application所以是主线程id
		mainThreadId = android.os.Process.myTid();
	}

	public static Context getContext() {
		return context;
	}

	public static Handler getmHandler() {
		return mHandler;
	}

	public static int getMainThreadId() {
		return mainThreadId;
	}

}
