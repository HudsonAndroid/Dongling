package com.hudson.donglingmusic.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/6/6.
 * 分类歌曲所有类别
 * 这个类有点问题，因为服务器的数据是一个列表（也不能说是一个List）中包含了多个list。
 * 所以使用gsonFormat的时候会出现问题。因此这里只能使用原始的json解析
 */

public class CategoryAllSongBean {

    public List<String> mTags;

    public ArrayList<CategoryBean> mZhuti,mXinqing,mFengge,mNiandai,mYueqi,mYuyan,mChangjing
            ,mLiupai,mYingshi,mXiqu,mRensheng;

    public int mErrorCode;


    public class CategoryBean{
        public String categoryType;
        public int hot;
        public String link;
        public int tagFrom;
        public String title;
    }
}
