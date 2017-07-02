package com.hudson.donglingmusic.bean;

/**
 * Created by Hudson on 2017/6/9.
 * 歌单详情的头布局实例
 */

public class GedanListHeaderBean {

    private String mListBgUrl;
    private String mTitle;
    private String mTag;
    private String mListenCount;
    private String mStarCount;
    private String mDesc;
    private String mListenId;

    public String getListenId() {
        return mListenId;
    }

    public void setListenId(String listenId) {
        mListenId = listenId;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public String getListBgUrl() {
        return mListBgUrl;
    }

    public void setListBgUrl(String listBgUrl) {
        mListBgUrl = listBgUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getListenCount() {
        return mListenCount;
    }

    public void setListenCount(String listenCount) {
        mListenCount = listenCount;
    }

    public String getStarCount() {
        return mStarCount;
    }

    public void setStarCount(String starCount) {
        mStarCount = starCount;
    }
}
