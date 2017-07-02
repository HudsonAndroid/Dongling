package com.hudson.donglingmusic.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Hudson on 2017/4/30.
 * 下载暂停或者失败的信息单位
 * 用于保存到本地数据库
 */

public class DownloadFailOrPauseInfo extends DataSupport{
    private int mDownloadId;//下载id，用于区分多个下载状态下不同下载
    private String mDownloadUrl;//下载路径
    private String mDownloadTitle;
    private long mTotalSize;//总大小
    private long mCurDownloadSize;//下载的大小
    private int mCurDownloadState;//下载状态

    //歌曲其他信息，用于加入本地音乐时更好添加数据库
    private String mAuthor;
    private String mAlbum;
    private int mAlbumId;

    public DownloadFailOrPauseInfo(int downloadId, String downloadUrl, String downloadTitle,
                                   long totalSize, long curDownloadSize, int curDownloadState,
                                   String author, String album, int albumId) {
        mDownloadId = downloadId;
        mDownloadUrl = downloadUrl;
        mDownloadTitle = downloadTitle;
        mTotalSize = totalSize;
        mCurDownloadSize = curDownloadSize;
        mCurDownloadState = curDownloadState;
        mAuthor = author;
        mAlbum = album;
        mAlbumId = albumId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(int albumId) {
        mAlbumId = albumId;
    }

    public int getDownloadId() {
        return mDownloadId;
    }

    public void setDownloadId(int downloadId) {
        mDownloadId = downloadId;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
    }

    public String getDownloadTitle() {
        return mDownloadTitle;
    }

    public void setDownloadTitle(String downloadTitle) {
        mDownloadTitle = downloadTitle;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(long totalSize) {
        mTotalSize = totalSize;
    }

    public long getCurDownloadSize() {
        return mCurDownloadSize;
    }

    public void setCurDownloadSize(long curDownloadSize) {
        mCurDownloadSize = curDownloadSize;
    }

    public int getCurDownloadState() {
        return mCurDownloadState;
    }

    public void setCurDownloadState(int curDownloadState) {
        mCurDownloadState = curDownloadState;
    }
}
