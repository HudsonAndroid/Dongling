package com.hudson.donglingmusic.bean;

/**
 * Created by Hudson on 2017/4/21.
 */

public class SongListHeaderBean {

    private String mListBgUrl;
    private String mListName;
    private String mListSongNumber;
    private String mListUpdateTime;

    public SongListHeaderBean(String listBgUrl, String listName, String listSongNumber, String listUpdateTime) {
        mListBgUrl = listBgUrl;
        mListName = listName;
        mListSongNumber = listSongNumber;
        mListUpdateTime = listUpdateTime;
    }

    public String getListBgUrl() {
        return mListBgUrl;
    }

    public void setListBgUrl(String listBgUrl) {
        mListBgUrl = listBgUrl;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String listName) {
        mListName = listName;
    }

    public String getListSongNumber() {
        return mListSongNumber;
    }

    public void setListSongNumber(String listSongNumber) {
        mListSongNumber = listSongNumber;
    }

    public String getListUpdateTime() {
        return mListUpdateTime;
    }

    public void setListUpdateTime(String listUpdateTime) {
        mListUpdateTime = listUpdateTime;
    }
}
