package com.hudson.donglingmusic.bean;

/**
 * Created by Hudson on 2017/4/21.
 * 专辑信息
 */

public class AlbumListHeaderBean {

    private String mAuthor;//专辑的作者
    private String mCollectCount;//被收藏次数
    private String mCommentCount;//评论次数
    private String mArea;//地区，如港台
    private String mInfo;//专辑信息(概述)
    private String mLanguage;//语言
    private String mListenCount;//被听次数
    private String mListBgUrl;//背景图片地址
    private String mPublishCompany;//发行公司
    private String mPublishTime;//发布时间
    private String mShareNum;//被分享次数
    private String mSongCount;//歌曲个数
    private String mStyle;//歌曲风格
    private String mAlbumName;//专辑名称

    public AlbumListHeaderBean(String author, String collectCount, String commentCount, String area, String info, String language, String listenCount, String listBgUrl, String publishCompany, String publishTime, String shareNum, String songCount, String style, String albumName) {
        mAuthor = author;
        mCollectCount = collectCount;
        mCommentCount = commentCount;
        mArea = area;
        mInfo = info;
        mLanguage = language;
        mListenCount = listenCount;
        mListBgUrl = listBgUrl;
        mPublishCompany = publishCompany;
        mPublishTime = publishTime;
        mShareNum = shareNum;
        mSongCount = songCount;
        mStyle = style;
        mAlbumName = albumName;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getCollectCount() {
        return mCollectCount;
    }

    public void setCollectCount(String collectCount) {
        mCollectCount = collectCount;
    }

    public String getCommentCount() {
        return mCommentCount;
    }

    public void setCommentCount(String commentCount) {
        mCommentCount = commentCount;
    }

    public String getArea() {
        return mArea;
    }

    public void setArea(String area) {
        mArea = area;
    }

    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String info) {
        mInfo = info;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getListenCount() {
        return mListenCount;
    }

    public void setListenCount(String listenCount) {
        mListenCount = listenCount;
    }

    public String getListBgUrl() {
        return mListBgUrl;
    }

    public void setListBgUrl(String listBgUrl) {
        mListBgUrl = listBgUrl;
    }

    public String getPublishCompany() {
        return mPublishCompany;
    }

    public void setPublishCompany(String publishCompany) {
        mPublishCompany = publishCompany;
    }

    public String getPublishTime() {
        return mPublishTime;
    }

    public void setPublishTime(String publishTime) {
        mPublishTime = publishTime;
    }

    public String getShareNum() {
        return mShareNum;
    }

    public void setShareNum(String shareNum) {
        mShareNum = shareNum;
    }

    public String getSongCount() {
        return mSongCount;
    }

    public void setSongCount(String songCount) {
        mSongCount = songCount;
    }

    public String getStyle() {
        return mStyle;
    }

    public void setStyle(String style) {
        mStyle = style;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }
}
