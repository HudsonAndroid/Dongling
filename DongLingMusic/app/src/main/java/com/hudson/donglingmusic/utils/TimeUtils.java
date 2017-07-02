package com.hudson.donglingmusic.utils;

/**
 * Created by Hudson on 2017/3/25.
 */

public class TimeUtils {

    /**
     * 时间转化处理
     * 将时间转换为字符串00:00
     */
    public static String toTime(int time) {
        time /= 1000;
        return String.format(" %02d:%02d ", time / 60%60, time % 60);
    }

    /**
     * 时间转化处理
     * 将时间转换为字符串0:00:00
     */
    public static String toLargeTime(int time) {
        time /= 1000;
        return String.format(" %01d:%02d:%02d ", time/3600%60,time/60%60, time % 60);
    }

    /**
     * 给定小时和分钟，给出类似18:01的效果
     * @param hour
     * @param minute
     * @return
     */
    public static String toHourAndMinute(int hour,int minute){
        return String.format(" %02d:%02d ",hour,minute);
    }

//    /**
//     * 时间转化处理
//     * 将时间转换为字符串00:00 小时+分钟
//     * @param time 秒
//     */
//    public static String toHourAndMinuteTime(int time) {
//        time = time/60;
//        return String.format(" %02d:%02d ",time/60%60, time % 60);
//    }

    /**
     * 将时间转换成歌词时间[00:00.00]
     * @param time
     * @return
     */
    public static String getTime(int time) {
        int millisecond = time % 1000/10;// 获取百位和十位组成的两位数，单位：10毫秒
        int second = time / 1000 % 60;// 获取秒数，先转换成以秒为单位的数值，把不超过60的部分留下
        int minute = time / 1000 / 60;// 获取分钟数
        return "[" + minute + ":" + second + "." + millisecond + "]";
    }
}
