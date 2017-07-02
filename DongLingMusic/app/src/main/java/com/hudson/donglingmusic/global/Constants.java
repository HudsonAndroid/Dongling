package com.hudson.donglingmusic.global;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Hudson on 2017/3/15.
 * 用于存储固定字符串
 * qq音乐接口没有用到
 */

public class Constants {

    public static final String NEWS_URL = "http://route.showapi.com/231-1?showapi_appid=28856&num=10&page=1&showapi_sign=90540b0e789f41a99d90ad4b8677e991";//轮播条新闻连接

    //qq音乐基地址
    private static final String QQUrlBase = "http://route.showapi.com/213-4";
    public static final String APP_ID = "28856";
    public static final String TOP_WESTERN = "3";//欧美
    public static final String TOP_MAINLAND = "5";//内地
    public static final String TOP_GANGTAI = "6";//港台
    public static final String TOP_HANGUO = "16";//韩国
    public static final String TOP_JANPAN = "17";//日本
    public static final String TOP_FOLK_SONG = "18";//民谣
    public static final String TOP_ROCK_MUSIC = "19";//摇滚
    public static final String TOP_SALES = "23";//销量
    public static final String TOP_HOT = "26";//热歌
    private static final String APP_SECRET = "90540b0e789f41a99d90ad4b8677e991";



    /**
     * 获取榜单地址
     * @return
     */
    public static String generateUrl(String topId){
        String systemTime = generateSystemTime();
        String src = QQUrlBase + "?showapi_appid="+APP_ID+"&showapi_timestamp="+systemTime
                +"&topid="+topId+"&showapi_sign=";
        ArrayList<String> strs = new ArrayList<>();
        strs.add("showapi_appid"+APP_ID);
        strs.add("showapi_timestamp"+systemTime);
        strs.add("topid"+topId);
        Collections.sort(strs);
        String srcDecode = "";
        for (int i = 0; i < strs.size(); i++) {
            srcDecode += strs.get(i);
        }
        srcDecode += APP_SECRET;
//        String sign = new String(Hex.encodeHex(DigestUtils.md5(srcDecode)));
//        return src+sign;
        return null;
    }

    /**
     * 获取系统时间
     * @return 例如20170319103241表示2017年3月19日10：32 42秒
     */
    public static String generateSystemTime(){
        Calendar now = Calendar.getInstance();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }


}
