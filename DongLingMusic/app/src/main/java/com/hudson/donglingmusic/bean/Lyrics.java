package com.hudson.donglingmusic.bean;

/**
 * Created by Hudson on 2017/1/1.
 */

public class Lyrics implements Comparable<Lyrics> {
    private String lrcStr; // 歌词内容
    private long lrcTime; // 歌词当前时间
    private long delayTime;//该歌词显示前需要延迟的时长

    public String getLrcStr() { // 获取歌词内容
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public long getLrcTime() { // 获取歌词时间
        return lrcTime;
    }

    public void setLrcTime(long lrcTime) {
        this.lrcTime = lrcTime;
    }

    public long getDelayTime() { // 获取延迟时间
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public int compareTo(Lyrics another) {
        if (lrcTime != another.lrcTime) {
            return (int) (lrcTime - another.lrcTime);
        }
        return 1;//如果相同返回大于.注意我们不能让它相同
    }
}
