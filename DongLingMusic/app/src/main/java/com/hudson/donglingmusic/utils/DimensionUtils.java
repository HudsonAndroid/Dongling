package com.hudson.donglingmusic.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by Hudson on 2017/5/4.
 */

public class DimensionUtils {

    /**
     * 获取屏幕宽高
     * @param activity activity实例
     * @param result 宽高数组
     */
    public static void getScreenDimension(Activity activity, int[] result){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        result[0] = (int)(dm.widthPixels * density + 0.5f);
        result[1] = (int)(dm.heightPixels * density + 0.5f);
    }
}
