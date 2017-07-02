package com.hudson.donglingmusic.bean;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/17.
 * 榜单实例
 */

public class QQNetMusicListBean {

    public int showapi_res_code;
    public  String showapi_res_error;
    public MusicListInfo showapi_res_body;

    public class MusicListInfo{
        public  PageBean pagebean;
        public int ret_code;
    }

    public class PageBean{
        public int ret_code;
        public int total_song_num;
        public ArrayList<QQMusicInfo> songlist;
    }

    public class QQMusicInfo {
        public int albumid;
        public String albummid;
        public String albumpic_big;
        public String albumpic_small;
        public String downUrl;
        public int seconds;
        public int singerid;
        public String singername;
        public int songid;
        public String songname;
        public String url;

        @Override
        public String toString() {
            return "QQMusicInfo{" +
                    "albumid=" + albumid +
                    ", albummid='" + albummid + '\'' +
                    ", albumpic_big='" + albumpic_big + '\'' +
                    ", albumpic_small='" + albumpic_small + '\'' +
                    ", downUrl='" + downUrl + '\'' +
                    ", seconds=" + seconds +
                    ", singerid=" + singerid +
                    ", singername='" + singername + '\'' +
                    ", songid=" + songid +
                    ", songname='" + songname + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
