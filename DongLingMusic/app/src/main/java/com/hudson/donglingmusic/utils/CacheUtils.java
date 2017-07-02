package com.hudson.donglingmusic.utils;

import com.hudson.donglingmusic.global.MySharePreferences;

/**
 * Created by Hudson on 2017/3/15.
 * 网络数据的缓冲工具类
 * 思路：缓存利用json的地址作为标识，利用Json的字符串作为内容
 * 注意：这里使用了sharedPreference实现的，也可以使用文件的方法和数据库的方法
 */

public class CacheUtils {

    /**
     * 以url为Key,以json的数据为内容，保存在本地
     * @param key
     * @param value
     */
    public static void setCache(String key,String value){
        MySharePreferences.getInstance().saveCachData(key,value);
    }

    public static String getCache(String key){
        return MySharePreferences.getInstance().getCachData(key);
    }
}
