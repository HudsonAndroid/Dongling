package com.hudson.donglingmusic.bean;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 * 热门标签的ViewPager的网络图片获取实例
 */

public class HeaderPic {

    public int error_code;
    public ArrayList<Picture> pic;

    public class Picture{
        public String ipad_desc;
        public String randpic;//图片的地址
    }
}
