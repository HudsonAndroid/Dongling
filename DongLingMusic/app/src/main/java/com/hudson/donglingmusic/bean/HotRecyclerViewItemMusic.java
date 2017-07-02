package com.hudson.donglingmusic.bean;

import java.util.ArrayList;

/**R.layout.item_recyclerview_hot
 * Created by Hudson on 2017/3/18.
 * 本实例是Hot标签的一个，展示的信息有
 *      1.歌单信息
 *      2.六首歌曲
 *      3.每首歌曲的title,singer
 *    或者类似上述信息的
 * 绑定的layout布局:
 */

public class HotRecyclerViewItemMusic {

    public String title;//歌单名字
    public ArrayList<SongInfo> mSongInfos;

    public HotRecyclerViewItemMusic(String title){
        this.title = title;
    }

    public HotRecyclerViewItemMusic(String title, ArrayList<SongInfo> infos){
        this.title = title;
        mSongInfos = infos;
    }

    public class SongInfo{

        public SongInfo(String bgUrl, String singerName, String songTitle) {
            this.bgUrl = bgUrl;
            this.singerName = singerName;
            this.songTitle = songTitle;
        }

        public String bgUrl;//图片背景
        public String singerName;//歌手
        public String songTitle;//歌名
    }
}
