package com.hudson.donglingmusic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetManagerUtils {
    /**
     * 判断当前网络是否是wifi 网络
     * @param mContext
     * @return
     */
	public  static boolean isWifi(Context mContext) {  
	    ConnectivityManager connectivityManager = (ConnectivityManager) mContext  
	            .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
	    if (activeNetInfo != null  
	            && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
	        return true;  
	    }  
	    return false;  
	}

    /**
     * 检测当的网络（WLAN、3G/2G）状态 是否可用
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {  
        ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity != null) {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (info != null && info.isConnected())   
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)   
                {
                    // 当前所连接的网络可用
                    return true;  
                }  
            }  
        }  
        return false;  
    }
}
