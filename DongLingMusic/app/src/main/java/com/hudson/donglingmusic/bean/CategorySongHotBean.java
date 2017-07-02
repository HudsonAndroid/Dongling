package com.hudson.donglingmusic.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hudson on 2017/6/6.
 * 歌曲分类，热门歌曲标签
 */

public class CategorySongHotBean {


    /**
     * taglist : [{"title":"情歌","class":"red","source":"1"},{"title":"红歌","class":"","source":"1"},{"title":"劲爆","class":"red","source":"1"},{"title":"天籁","class":"blue","source":"1"},{"title":"经典老歌","class":"bold","source":"1"},{"title":"欧美","class":"blue","source":"1"},{"title":"网络歌曲","class":"bold","source":"1"},{"title":"民歌","class":"red","source":"1"}]
     * error_code : 22000
     */

    private int error_code;
    private List<TaglistBean> taglist;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<TaglistBean> getTaglist() {
        return taglist;
    }

    public void setTaglist(List<TaglistBean> taglist) {
        this.taglist = taglist;
    }

    public static class TaglistBean {
        /**
         * title : 情歌
         * class : red
         * source : 1
         */

        private String title;
        @SerializedName("class")
        private String classX;
        private String source;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getClassX() {
            return classX;
        }

        public void setClassX(String classX) {
            this.classX = classX;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
