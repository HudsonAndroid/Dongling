package com.hudson.donglingmusic.bean;

import java.util.List;

/**
 * Created by Hudson on 2017/5/31.
 * 本类是热门搜索词语
 */

public class HotSearchWord {


    /**
     * error_code : 22000
     * result : [{"strong":1,"word":"薛之谦","linktype":0,"linkurl":""},{"strong":0,"word":"林忆莲《我不能忘记你》","linktype":0,"linkurl":""},{"strong":0,"word":"梁静茹","linktype":0,"linkurl":""},{"strong":0,"word":"莫文蔚","linktype":0,"linkurl":""},{"strong":0,"word":"李宗盛","linktype":0,"linkurl":""},{"strong":0,"word":"五月天","linktype":0,"linkurl":""},{"strong":0,"word":"陈梓童","linktype":0,"linkurl":""},{"strong":0,"word":"孙盛希《Between》","linktype":0,"linkurl":""},{"strong":0,"word":"刚好遇见你","linktype":0,"linkurl":""},{"strong":0,"word":"张杰","linktype":0,"linkurl":""},{"strong":0,"word":"周杰伦","linktype":0,"linkurl":""},{"strong":0,"word":"凉凉","linktype":0,"linkurl":""},{"strong":0,"word":"薛之谦《高尚》","linktype":0,"linkurl":""},{"strong":0,"word":"凉凉-《三生三世十里桃花》片尾曲-月狸li","linktype":0,"linkurl":""},{"strong":0,"word":"林俊杰","linktype":0,"linkurl":""}]
     */

    private int error_code;
    private List<ResultBean> result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * strong : 1
         * word : 薛之谦
         * linktype : 0
         * linkurl :
         */

        private int strong;
        private String word;
        private int linktype;
        private String linkurl;

        public int getStrong() {
            return strong;
        }

        public void setStrong(int strong) {
            this.strong = strong;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getLinktype() {
            return linktype;
        }

        public void setLinktype(int linktype) {
            this.linktype = linktype;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }
    }
}
