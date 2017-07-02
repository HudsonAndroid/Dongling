package com.hudson.donglingmusic.global;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;

/**
 * Created by Hudson on 2017/3/15.
 */

public class XUtilsManager {

    private static HttpUtils mHttpUtils = null;
    private static BitmapUtils mBitmapUtils = null;

    /**
     * 单例
     * @return httpUtils单例
     */
    public static HttpUtils getHttpUtilsInstance(){
        if(mHttpUtils==null){
            synchronized (XUtilsManager.class){
                if(mHttpUtils==null)
                    mHttpUtils = new HttpUtils();
            }
        }
        return mHttpUtils;
    }

    /**
     * 单例
     * @return bitmapUtils单例
     */
    public static BitmapUtils getBitmapUtilsInstance(){
        if(mBitmapUtils==null){
            synchronized (XUtilsManager.class){
                if(mBitmapUtils==null){
                    mBitmapUtils = new BitmapUtils(UIUtils.getContext());
                    //设置网络加载中的默认的图片
                    mBitmapUtils.configDefaultLoadingImage(R.drawable.default_loading);
                }
            }
        }
        return mBitmapUtils;
    }
}
