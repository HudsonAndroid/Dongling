package com.hudson.donglingmusic.bean;

import java.util.List;

/**
 * Created by Hudson on 2017/6/7.
 * 分类歌单的歌曲列表对应的json实例
 */

public class CategorySongListBean {


    /**
     * taginfo : {"songlist":[{"song_id":"975644","title":"流年","artist_id":"15","author":"王菲","album_id":"194430","album_title":"王菲2001","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"1","all_rate":"64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"931772","title":"鬼迷心窍","artist_id":"123","author":"周华健","album_id":"187446","album_title":"NOW现在周华健","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"8867095","title":"离开","artist_id":"844","author":"郭静","album_id":"7589353","album_title":"陪着我的时候想着她","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless,vip"},{"song_id":"856060","title":"烫心","artist_id":"786","author":"羽泉","album_id":"182772","album_title":"热爱","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"8523344","title":"You Belong To Me","artist_id":"2198911","author":"Jason Wade","album_id":"8523297","album_title":"Shrek","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":""},{"song_id":"847609","title":"二缺一","artist_id":"70","author":"蔡卓妍","album_id":"405773","album_title":"As A Sa","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"128,320","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"vip"},{"song_id":"8341319","title":"I Have You","artist_id":"1869","author":"Carpenters","album_id":"8341309","album_title":"A Kind Of Hush","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"809073","title":"我会想念你","artist_id":"224","author":"张震岳","album_id":"115522","album_title":"阿岳正传","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"8038873","title":"My Everything","artist_id":"7380640","author":"98º","album_id":"8038866","album_title":"Revelation","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":""},{"song_id":"790142","title":"后来","artist_id":"74","author":"刘若英","album_id":"190892","album_title":"我等你","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"751126","title":"我是一只鱼","artist_id":"119","author":"任贤齐","album_id":"173971","album_title":"情义","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7348702","title":"True Love","artist_id":"295","author":"蔡健雅","album_id":"7327870","album_title":"记念","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"7328681","title":"七月七日晴","artist_id":"840","author":"许慧欣","album_id":"7313039","album_title":"美丽的爱情","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7319921","title":"等你爱我","artist_id":"87","author":"陈奕迅","album_id":"7311560","album_title":"Stranger Under My Skin","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7288562","title":"不是真的爱我","artist_id":"7","author":"孙燕姿","album_id":"7287761","album_title":"风筝","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"1","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7280185","title":"凤凰花开的路口","artist_id":"313","author":"林志炫","album_id":"7274801","album_title":"绝对收藏","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"24,64,128,192,256,319,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless,vip"},{"song_id":"7280177","title":"离人","artist_id":"313","author":"林志炫","album_id":"7274801","album_title":"绝对收藏","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless,vip"},{"song_id":"672411","title":"温哥华悲伤一号","artist_id":"393","author":"万芳","album_id":"141488","album_title":"ONE芳新歌,精选","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"639512","title":"多想","artist_id":"166","author":"张信哲","album_id":"191785","album_title":"挚爱","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"flac,320,256,192,128,64,24","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"621650","title":"棋子","artist_id":"15","author":"王菲","album_id":"7312306","album_title":"天空","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"}],"count":1000,"havemore":1}
     * error_code : 22000
     */

    private TaginfoBean taginfo;
    private int error_code;

    public TaginfoBean getTaginfo() {
        return taginfo;
    }

    public void setTaginfo(TaginfoBean taginfo) {
        this.taginfo = taginfo;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class TaginfoBean {
        /**
         * songlist : [{"song_id":"975644","title":"流年","artist_id":"15","author":"王菲","album_id":"194430","album_title":"王菲2001","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"1","all_rate":"64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"931772","title":"鬼迷心窍","artist_id":"123","author":"周华健","album_id":"187446","album_title":"NOW现在周华健","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"8867095","title":"离开","artist_id":"844","author":"郭静","album_id":"7589353","album_title":"陪着我的时候想着她","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless,vip"},{"song_id":"856060","title":"烫心","artist_id":"786","author":"羽泉","album_id":"182772","album_title":"热爱","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"8523344","title":"You Belong To Me","artist_id":"2198911","author":"Jason Wade","album_id":"8523297","album_title":"Shrek","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":""},{"song_id":"847609","title":"二缺一","artist_id":"70","author":"蔡卓妍","album_id":"405773","album_title":"As A Sa","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"128,320","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"vip"},{"song_id":"8341319","title":"I Have You","artist_id":"1869","author":"Carpenters","album_id":"8341309","album_title":"A Kind Of Hush","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"809073","title":"我会想念你","artist_id":"224","author":"张震岳","album_id":"115522","album_title":"阿岳正传","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"8038873","title":"My Everything","artist_id":"7380640","author":"98º","album_id":"8038866","album_title":"Revelation","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":""},{"song_id":"790142","title":"后来","artist_id":"74","author":"刘若英","album_id":"190892","album_title":"我等你","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"751126","title":"我是一只鱼","artist_id":"119","author":"任贤齐","album_id":"173971","album_title":"情义","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7348702","title":"True Love","artist_id":"295","author":"蔡健雅","album_id":"7327870","album_title":"记念","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"7328681","title":"七月七日晴","artist_id":"840","author":"许慧欣","album_id":"7313039","album_title":"美丽的爱情","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7319921","title":"等你爱我","artist_id":"87","author":"陈奕迅","album_id":"7311560","album_title":"Stranger Under My Skin","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7288562","title":"不是真的爱我","artist_id":"7","author":"孙燕姿","album_id":"7287761","album_title":"风筝","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"1","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"7280185","title":"凤凰花开的路口","artist_id":"313","author":"林志炫","album_id":"7274801","album_title":"绝对收藏","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"24,64,128,192,256,319,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless,vip"},{"song_id":"7280177","title":"离人","artist_id":"313","author":"林志炫","album_id":"7274801","album_title":"绝对收藏","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless,vip"},{"song_id":"672411","title":"温哥华悲伤一号","artist_id":"393","author":"万芳","album_id":"141488","album_title":"ONE芳新歌,精选","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"64,128,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"},{"song_id":"639512","title":"多想","artist_id":"166","author":"张信哲","album_id":"191785","album_title":"挚爱","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"flac,320,256,192,128,64,24","havehigh":2,"has_mv":0,"has_mv_mobile":0,"charge":0,"biaoshi":"lossless"},{"song_id":"621650","title":"棋子","artist_id":"15","author":"王菲","album_id":"7312306","album_title":"天空","del_status":"0","copy_type":"0","resouce_type":"0","resource_type":"0","resource_type_ext":"0","versions":"","bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","relate_status":"0","all_rate":"24,64,128,192,256,320,flac","havehigh":2,"has_mv":1,"has_mv_mobile":1,"charge":0,"biaoshi":"lossless"}]
         * count : 1000
         * havemore : 1
         */

        private int count;
        private int havemore;
        private List<SonglistBean> songlist;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getHavemore() {
            return havemore;
        }

        public void setHavemore(int havemore) {
            this.havemore = havemore;
        }

        public List<SonglistBean> getSonglist() {
            return songlist;
        }

        public void setSonglist(List<SonglistBean> songlist) {
            this.songlist = songlist;
        }

        public static class SonglistBean {
            /**
             * song_id : 975644
             * title : 流年
             * artist_id : 15
             * author : 王菲
             * album_id : 194430
             * album_title : 王菲2001
             * del_status : 0
             * copy_type : 0
             * resouce_type : 0
             * resource_type : 0
             * resource_type_ext : 0
             * versions :
             * bitrate_fee : {"0":"0|0","1":"0|0"}
             * relate_status : 1
             * all_rate : 64,128,192,256,320,flac
             * havehigh : 2
             * has_mv : 1
             * has_mv_mobile : 1
             * charge : 0
             * biaoshi : lossless
             */

            private String song_id;
            private String title;
            private String artist_id;
            private String author;
            private String album_id;
            private String album_title;
            private String del_status;
            private String copy_type;
            private String resouce_type;
            private String resource_type;
            private String resource_type_ext;
            private String versions;
            private String bitrate_fee;
            private String relate_status;
            private String all_rate;
            private int havehigh;
            private int has_mv;
            private int has_mv_mobile;
            private int charge;
            private String biaoshi;

            public String getSong_id() {
                return song_id;
            }

            public void setSong_id(String song_id) {
                this.song_id = song_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getArtist_id() {
                return artist_id;
            }

            public void setArtist_id(String artist_id) {
                this.artist_id = artist_id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getAlbum_id() {
                return album_id;
            }

            public void setAlbum_id(String album_id) {
                this.album_id = album_id;
            }

            public String getAlbum_title() {
                return album_title;
            }

            public void setAlbum_title(String album_title) {
                this.album_title = album_title;
            }

            public String getDel_status() {
                return del_status;
            }

            public void setDel_status(String del_status) {
                this.del_status = del_status;
            }

            public String getCopy_type() {
                return copy_type;
            }

            public void setCopy_type(String copy_type) {
                this.copy_type = copy_type;
            }

            public String getResouce_type() {
                return resouce_type;
            }

            public void setResouce_type(String resouce_type) {
                this.resouce_type = resouce_type;
            }

            public String getResource_type() {
                return resource_type;
            }

            public void setResource_type(String resource_type) {
                this.resource_type = resource_type;
            }

            public String getResource_type_ext() {
                return resource_type_ext;
            }

            public void setResource_type_ext(String resource_type_ext) {
                this.resource_type_ext = resource_type_ext;
            }

            public String getVersions() {
                return versions;
            }

            public void setVersions(String versions) {
                this.versions = versions;
            }

            public String getBitrate_fee() {
                return bitrate_fee;
            }

            public void setBitrate_fee(String bitrate_fee) {
                this.bitrate_fee = bitrate_fee;
            }

            public String getRelate_status() {
                return relate_status;
            }

            public void setRelate_status(String relate_status) {
                this.relate_status = relate_status;
            }

            public String getAll_rate() {
                return all_rate;
            }

            public void setAll_rate(String all_rate) {
                this.all_rate = all_rate;
            }

            public int getHavehigh() {
                return havehigh;
            }

            public void setHavehigh(int havehigh) {
                this.havehigh = havehigh;
            }

            public int getHas_mv() {
                return has_mv;
            }

            public void setHas_mv(int has_mv) {
                this.has_mv = has_mv;
            }

            public int getHas_mv_mobile() {
                return has_mv_mobile;
            }

            public void setHas_mv_mobile(int has_mv_mobile) {
                this.has_mv_mobile = has_mv_mobile;
            }

            public int getCharge() {
                return charge;
            }

            public void setCharge(int charge) {
                this.charge = charge;
            }

            public String getBiaoshi() {
                return biaoshi;
            }

            public void setBiaoshi(String biaoshi) {
                this.biaoshi = biaoshi;
            }
        }
    }
}
