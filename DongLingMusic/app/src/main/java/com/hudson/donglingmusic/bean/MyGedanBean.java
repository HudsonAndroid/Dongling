package com.hudson.donglingmusic.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Hudson on 2017/6/11.
 */

public class MyGedanBean extends DataSupport{

    private String mTitle;
    private String mTag;
    private String mImagePath;
    private String mDesc;
    private String mNetGedanId = null;//仅用于网络歌单
    private int mSongCount;
    private String mDatabaseName;//数据库名,由于title可能被修改，但是对应的数据库名不变，所以需要一个数据库名属性

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public void setDatabaseName(String databaseName) {
        mDatabaseName = databaseName;
    }

    public int getSongCount() {
        return mSongCount;
    }

    public void setSongCount(int songCount) {
        mSongCount = songCount;
    }

    public String getNetGedanId() {
        return mNetGedanId;
    }

    public void setNetGedanId(String netGedanId) {
        mNetGedanId = netGedanId;
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

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    @Override
    public String toString() {
        return "MyGedanBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mTag='" + mTag + '\'' +
                ", mImagePath='" + mImagePath + '\'' +
                ", mDesc='" + mDesc + '\'' +
                ", mNetGedanId='" + mNetGedanId + '\'' +
                ", mSongCount=" + mSongCount +
                '}';
    }
}
