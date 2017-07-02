package com.hudson.donglingmusic.bean;

/**
 * Created by Hudson on 2017/4/4.
 */

public class MusicMenuItemBean {

    private int mImageId;
    private String mDesc;

    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int imageId) {
        mImageId = imageId;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public MusicMenuItemBean(int imageId, String desc) {
        mImageId = imageId;
        mDesc = desc;
    }
}
