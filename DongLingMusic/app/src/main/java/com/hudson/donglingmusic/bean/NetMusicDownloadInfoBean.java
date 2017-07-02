package com.hudson.donglingmusic.bean;

import java.util.List;

/**
 * Created by Hudson on 2017/4/22.
 * 网络歌曲详细信息，包括下载地址，图片背景下载地址，歌词下载地址
 *
 * 注意：使用gsonFormat时虽然块，但是有缺点就是有时候如果是一个数组，它只会对数组的第一个
 * 对象作为全部类型，然而可能第一个类型是Int，而后面的是double类型，所以导致出问题
 */

public class NetMusicDownloadInfoBean {

    /**
     * error_code : 22000
     * songinfo : {"album_1000_1000":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_1000","album_500_500":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_500","album_id":"540190712","album_no":"1","album_title":"脑洞超级大","aliasname":"","all_artist_id":"315779031","all_rate":"64,128,256,320,flac","area":"0","artist_1000_1000":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189789/540189789.jpg@s_0,w_1000","artist_480_800":"","artist_500_500":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189789/540189789.jpg@s_0,w_500","artist_640_1136":"","artist_id":"315779031","author":"十二星宿风之少年","bitrate":"64,128,256,320,1133","charge":0,"collect_num":117,"comment_num":5,"compose":"小三真子","compress_status":"0","copy_type":"1","country":"内地","del_status":"0","distribution":"0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000","expire":36000,"file_duration":"266","has_mv":0,"has_mv_mobile":0,"havehigh":2,"high_rate":"320","hot":"81833","is_charge":"","is_first_publish":0,"korean_bb_song":"0","language":"国语","learn":0,"lrclink":"http://musicdata.baidu.com/data2/lrc/b16cafbe0bab6293a69660c326387dcb/540190608/540190608.lrc","multiterminal_copytype":"","original":0,"original_rate":"","piao_id":"0","pic_big":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_150","pic_huge":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_1000","pic_premium":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_500","pic_radio":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_300","pic_singer":"","pic_small":"http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_90","play_type":0,"publishtime":"2017-04-11","relate_status":"0","resource_type":"0","resource_type_ext":"0","share_num":1,"share_url":"http://music.baidu.com/song/540190714","song_id":"540190714","song_source":"web","songwriting":"刘佳","sound_effect":"","ting_uid":"239948764","title":"脑洞超级大","toneid":"0","versions":""}
     * songurl : {"url":[{"can_load":true,"can_see":1,"down_type":0,"file_bitrate":64,"file_duration":266,"file_extension":"mp3","file_link":"http://yinyueshiting.baidu.com/data2/music/88f8b84e50225ceb7b4fdca3e89367e8/540377637/540377637.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","file_size":2127453,"free":1,"hash":"20395dc784dcaff8e9b1b970e75c6907d23ba293","is_udition_url":1,"original":0,"preload":40,"replay_gain":"0.000000","show_link":"http://zhangmenshiting.baidu.com/data2/music/88f8b84e50225ceb7b4fdca3e89367e8/540377637/540377637.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","song_file_id":540377637},{"can_load":true,"can_see":1,"down_type":1,"file_bitrate":128,"file_duration":266,"file_extension":"mp3","file_link":"http://yinyueshiting.baidu.com/data2/music/5649322c50d3ffbbb7b9eca9de1d0342/540377610/540377610.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","file_size":4253611,"free":1,"hash":"9f7b618ecaa4a33459267fbdff6cad2a4b33cfa0","is_udition_url":0,"original":0,"preload":80,"replay_gain":"0.000000","show_link":"http://zhangmenshiting.baidu.com/data2/music/5649322c50d3ffbbb7b9eca9de1d0342/540377610/540377610.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","song_file_id":540377610},{"can_load":true,"can_see":1,"down_type":0,"file_bitrate":256,"file_duration":266,"file_extension":"mp3","file_link":"http://yinyueshiting.baidu.com/data2/music/ba087324ce065089fe92a40c337f0f41/540377598/540377598.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","file_size":8505928,"free":1,"hash":"662528be26c7c0bbf3279ff7686219885748d333","is_udition_url":0,"original":0,"preload":160,"replay_gain":"0.000000","show_link":"http://zhangmenshiting.baidu.com/data2/music/ba087324ce065089fe92a40c337f0f41/540377598/540377598.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","song_file_id":540377598},{"can_load":true,"can_see":1,"down_type":2,"file_bitrate":320,"file_duration":266,"file_extension":"mp3","file_link":"http://yinyueshiting.baidu.com/data2/music/38c619d9ffdea1a8c82dd501b7b366be/540377516/540377516.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","file_size":10632086,"free":1,"hash":"0206c615e3c5e11f4fc8d2ce070294920f42dd5e","is_udition_url":0,"original":0,"preload":200,"replay_gain":"0.000000","show_link":"http://zhangmenshiting.baidu.com/data2/music/38c619d9ffdea1a8c82dd501b7b366be/540377516/540377516.mp3?xcode=6f625248278deaf8afe7b73b41f866fe","song_file_id":540377516},{"can_load":true,"can_see":1,"down_type":0,"file_bitrate":1133,"file_duration":266,"file_extension":"flac","file_link":"","file_size":37690743,"free":1,"hash":"4ebb0ae19f50e6fd10dda196ee0817f38c78f150","is_udition_url":0,"original":0,"preload":708.125,"replay_gain":"0.000000","show_link":"","song_file_id":540377482}]}
     */

    private int error_code;
    private SonginfoBean songinfo;
    private SongurlBean songurl;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public SonginfoBean getSonginfo() {
        return songinfo;
    }

    public void setSonginfo(SonginfoBean songinfo) {
        this.songinfo = songinfo;
    }

    public SongurlBean getSongurl() {
        return songurl;
    }

    public void setSongurl(SongurlBean songurl) {
        this.songurl = songurl;
    }

    public static class SonginfoBean {
        /**
         * album_1000_1000 : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_1000
         * album_500_500 : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_500
         * album_id : 540190712
         * album_no : 1
         * album_title : 脑洞超级大
         * aliasname :
         * all_artist_id : 315779031
         * all_rate : 64,128,256,320,flac
         * area : 0
         * artist_1000_1000 : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189789/540189789.jpg@s_0,w_1000
         * artist_480_800 :
         * artist_500_500 : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189789/540189789.jpg@s_0,w_500
         * artist_640_1136 :
         * artist_id : 315779031
         * author : 十二星宿风之少年
         * bitrate : 64,128,256,320,1133
         * charge : 0
         * collect_num : 117
         * comment_num : 5
         * compose : 小三真子
         * compress_status : 0
         * copy_type : 1
         * country : 内地
         * del_status : 0
         * distribution : 0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000
         * expire : 36000
         * file_duration : 266
         * has_mv : 0
         * has_mv_mobile : 0
         * havehigh : 2
         * high_rate : 320
         * hot : 81833
         * is_charge :
         * is_first_publish : 0
         * korean_bb_song : 0
         * language : 国语
         * learn : 0
         * lrclink : http://musicdata.baidu.com/data2/lrc/b16cafbe0bab6293a69660c326387dcb/540190608/540190608.lrc
         * multiterminal_copytype :
         * original : 0
         * original_rate :
         * piao_id : 0
         * pic_big : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_150
         * pic_huge : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_1000
         * pic_premium : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_500
         * pic_radio : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_300
         * pic_singer :
         * pic_small : http://musicdata.baidu.com/data2/pic/8bda1a0af34cd406d6f738e48e97aa65/540189803/540189803.jpg@s_0,w_90
         * play_type : 0
         * publishtime : 2017-04-11
         * relate_status : 0
         * resource_type : 0
         * resource_type_ext : 0
         * share_num : 1
         * share_url : http://music.baidu.com/song/540190714
         * song_id : 540190714
         * song_source : web
         * songwriting : 刘佳
         * sound_effect :
         * ting_uid : 239948764
         * title : 脑洞超级大
         * toneid : 0
         * versions :
         */

        private String album_1000_1000;
        private String album_500_500;
        private String album_id;
        private String album_no;
        private String album_title;
        private String aliasname;
        private String all_artist_id;
        private String all_rate;
        private String area;
        private String artist_1000_1000;
        private String artist_480_800;
        private String artist_500_500;
        private String artist_640_1136;
        private String artist_id;
        private String author;
        private String bitrate;
        private int charge;
        private int collect_num;
        private int comment_num;
        private String compose;
        private String compress_status;
        private String copy_type;
        private String country;
        private String del_status;
        private String distribution;
        private int expire;
        private String file_duration;
        private int has_mv;
        private int has_mv_mobile;
        private int havehigh;
        private String high_rate;
        private String hot;
        private String is_charge;
        private int is_first_publish;
        private String korean_bb_song;
        private String language;
        private int learn;
        private String lrclink;
        private String multiterminal_copytype;
        private int original;
        private String original_rate;
        private String piao_id;
        private String pic_big;
        private String pic_huge;
        private String pic_premium;
        private String pic_radio;
        private String pic_singer;
        private String pic_small;
        private int play_type;
        private String publishtime;
        private String relate_status;
        private String resource_type;
        private String resource_type_ext;
        private int share_num;
        private String share_url;
        private String song_id;
        private String song_source;
        private String songwriting;
        private String sound_effect;
        private String ting_uid;
        private String title;
        private String toneid;
        private String versions;

        public String getAlbum_1000_1000() {
            return album_1000_1000;
        }

        public void setAlbum_1000_1000(String album_1000_1000) {
            this.album_1000_1000 = album_1000_1000;
        }

        public String getAlbum_500_500() {
            return album_500_500;
        }

        public void setAlbum_500_500(String album_500_500) {
            this.album_500_500 = album_500_500;
        }

        public String getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(String album_id) {
            this.album_id = album_id;
        }

        public String getAlbum_no() {
            return album_no;
        }

        public void setAlbum_no(String album_no) {
            this.album_no = album_no;
        }

        public String getAlbum_title() {
            return album_title;
        }

        public void setAlbum_title(String album_title) {
            this.album_title = album_title;
        }

        public String getAliasname() {
            return aliasname;
        }

        public void setAliasname(String aliasname) {
            this.aliasname = aliasname;
        }

        public String getAll_artist_id() {
            return all_artist_id;
        }

        public void setAll_artist_id(String all_artist_id) {
            this.all_artist_id = all_artist_id;
        }

        public String getAll_rate() {
            return all_rate;
        }

        public void setAll_rate(String all_rate) {
            this.all_rate = all_rate;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getArtist_1000_1000() {
            return artist_1000_1000;
        }

        public void setArtist_1000_1000(String artist_1000_1000) {
            this.artist_1000_1000 = artist_1000_1000;
        }

        public String getArtist_480_800() {
            return artist_480_800;
        }

        public void setArtist_480_800(String artist_480_800) {
            this.artist_480_800 = artist_480_800;
        }

        public String getArtist_500_500() {
            return artist_500_500;
        }

        public void setArtist_500_500(String artist_500_500) {
            this.artist_500_500 = artist_500_500;
        }

        public String getArtist_640_1136() {
            return artist_640_1136;
        }

        public void setArtist_640_1136(String artist_640_1136) {
            this.artist_640_1136 = artist_640_1136;
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

        public String getBitrate() {
            return bitrate;
        }

        public void setBitrate(String bitrate) {
            this.bitrate = bitrate;
        }

        public int getCharge() {
            return charge;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        public int getCollect_num() {
            return collect_num;
        }

        public void setCollect_num(int collect_num) {
            this.collect_num = collect_num;
        }

        public int getComment_num() {
            return comment_num;
        }

        public void setComment_num(int comment_num) {
            this.comment_num = comment_num;
        }

        public String getCompose() {
            return compose;
        }

        public void setCompose(String compose) {
            this.compose = compose;
        }

        public String getCompress_status() {
            return compress_status;
        }

        public void setCompress_status(String compress_status) {
            this.compress_status = compress_status;
        }

        public String getCopy_type() {
            return copy_type;
        }

        public void setCopy_type(String copy_type) {
            this.copy_type = copy_type;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getDel_status() {
            return del_status;
        }

        public void setDel_status(String del_status) {
            this.del_status = del_status;
        }

        public String getDistribution() {
            return distribution;
        }

        public void setDistribution(String distribution) {
            this.distribution = distribution;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public String getFile_duration() {
            return file_duration;
        }

        public void setFile_duration(String file_duration) {
            this.file_duration = file_duration;
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

        public int getHavehigh() {
            return havehigh;
        }

        public void setHavehigh(int havehigh) {
            this.havehigh = havehigh;
        }

        public String getHigh_rate() {
            return high_rate;
        }

        public void setHigh_rate(String high_rate) {
            this.high_rate = high_rate;
        }

        public String getHot() {
            return hot;
        }

        public void setHot(String hot) {
            this.hot = hot;
        }

        public String getIs_charge() {
            return is_charge;
        }

        public void setIs_charge(String is_charge) {
            this.is_charge = is_charge;
        }

        public int getIs_first_publish() {
            return is_first_publish;
        }

        public void setIs_first_publish(int is_first_publish) {
            this.is_first_publish = is_first_publish;
        }

        public String getKorean_bb_song() {
            return korean_bb_song;
        }

        public void setKorean_bb_song(String korean_bb_song) {
            this.korean_bb_song = korean_bb_song;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public int getLearn() {
            return learn;
        }

        public void setLearn(int learn) {
            this.learn = learn;
        }

        public String getLrclink() {
            return lrclink;
        }

        public void setLrclink(String lrclink) {
            this.lrclink = lrclink;
        }

        public String getMultiterminal_copytype() {
            return multiterminal_copytype;
        }

        public void setMultiterminal_copytype(String multiterminal_copytype) {
            this.multiterminal_copytype = multiterminal_copytype;
        }

        public int getOriginal() {
            return original;
        }

        public void setOriginal(int original) {
            this.original = original;
        }

        public String getOriginal_rate() {
            return original_rate;
        }

        public void setOriginal_rate(String original_rate) {
            this.original_rate = original_rate;
        }

        public String getPiao_id() {
            return piao_id;
        }

        public void setPiao_id(String piao_id) {
            this.piao_id = piao_id;
        }

        public String getPic_big() {
            return pic_big;
        }

        public void setPic_big(String pic_big) {
            this.pic_big = pic_big;
        }

        public String getPic_huge() {
            return pic_huge;
        }

        public void setPic_huge(String pic_huge) {
            this.pic_huge = pic_huge;
        }

        public String getPic_premium() {
            return pic_premium;
        }

        public void setPic_premium(String pic_premium) {
            this.pic_premium = pic_premium;
        }

        public String getPic_radio() {
            return pic_radio;
        }

        public void setPic_radio(String pic_radio) {
            this.pic_radio = pic_radio;
        }

        public String getPic_singer() {
            return pic_singer;
        }

        public void setPic_singer(String pic_singer) {
            this.pic_singer = pic_singer;
        }

        public String getPic_small() {
            return pic_small;
        }

        public void setPic_small(String pic_small) {
            this.pic_small = pic_small;
        }

        public int getPlay_type() {
            return play_type;
        }

        public void setPlay_type(int play_type) {
            this.play_type = play_type;
        }

        public String getPublishtime() {
            return publishtime;
        }

        public void setPublishtime(String publishtime) {
            this.publishtime = publishtime;
        }

        public String getRelate_status() {
            return relate_status;
        }

        public void setRelate_status(String relate_status) {
            this.relate_status = relate_status;
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

        public int getShare_num() {
            return share_num;
        }

        public void setShare_num(int share_num) {
            this.share_num = share_num;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public String getSong_id() {
            return song_id;
        }

        public void setSong_id(String song_id) {
            this.song_id = song_id;
        }

        public String getSong_source() {
            return song_source;
        }

        public void setSong_source(String song_source) {
            this.song_source = song_source;
        }

        public String getSongwriting() {
            return songwriting;
        }

        public void setSongwriting(String songwriting) {
            this.songwriting = songwriting;
        }

        public String getSound_effect() {
            return sound_effect;
        }

        public void setSound_effect(String sound_effect) {
            this.sound_effect = sound_effect;
        }

        public String getTing_uid() {
            return ting_uid;
        }

        public void setTing_uid(String ting_uid) {
            this.ting_uid = ting_uid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getToneid() {
            return toneid;
        }

        public void setToneid(String toneid) {
            this.toneid = toneid;
        }

        public String getVersions() {
            return versions;
        }

        public void setVersions(String versions) {
            this.versions = versions;
        }
    }

    public static class SongurlBean {
        private List<UrlBean> url;

        public List<UrlBean> getUrl() {
            return url;
        }

        public void setUrl(List<UrlBean> url) {
            this.url = url;
        }

        public static class UrlBean {
            /**
             * can_load : true
             * can_see : 1
             * down_type : 0
             * file_bitrate : 64
             * file_duration : 266
             * file_extension : mp3
             * file_link : http://yinyueshiting.baidu.com/data2/music/88f8b84e50225ceb7b4fdca3e89367e8/540377637/540377637.mp3?xcode=6f625248278deaf8afe7b73b41f866fe
             * file_size : 2127453
             * free : 1
             * hash : 20395dc784dcaff8e9b1b970e75c6907d23ba293
             * is_udition_url : 1
             * original : 0
             * preload : 40
             * replay_gain : 0.000000
             * show_link : http://zhangmenshiting.baidu.com/data2/music/88f8b84e50225ceb7b4fdca3e89367e8/540377637/540377637.mp3?xcode=6f625248278deaf8afe7b73b41f866fe
             * song_file_id : 540377637
             */

            private boolean can_load;
            private int can_see;
            private int down_type;
            private int file_bitrate;
            private int file_duration;
            private String file_extension;
            private String file_link;
            private int file_size;
            private int free;
            private String hash;
            private int is_udition_url;
            private int original;
            private double preload;
            private String replay_gain;
            private String show_link;
            private int song_file_id;

            public boolean isCan_load() {
                return can_load;
            }

            public void setCan_load(boolean can_load) {
                this.can_load = can_load;
            }

            public int getCan_see() {
                return can_see;
            }

            public void setCan_see(int can_see) {
                this.can_see = can_see;
            }

            public int getDown_type() {
                return down_type;
            }

            public void setDown_type(int down_type) {
                this.down_type = down_type;
            }

            public int getFile_bitrate() {
                return file_bitrate;
            }

            public void setFile_bitrate(int file_bitrate) {
                this.file_bitrate = file_bitrate;
            }

            public int getFile_duration() {
                return file_duration;
            }

            public void setFile_duration(int file_duration) {
                this.file_duration = file_duration;
            }

            public String getFile_extension() {
                return file_extension;
            }

            public void setFile_extension(String file_extension) {
                this.file_extension = file_extension;
            }

            public String getFile_link() {
                return file_link;
            }

            public void setFile_link(String file_link) {
                this.file_link = file_link;
            }

            public int getFile_size() {
                return file_size;
            }

            public void setFile_size(int file_size) {
                this.file_size = file_size;
            }

            public int getFree() {
                return free;
            }

            public void setFree(int free) {
                this.free = free;
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }

            public int getIs_udition_url() {
                return is_udition_url;
            }

            public void setIs_udition_url(int is_udition_url) {
                this.is_udition_url = is_udition_url;
            }

            public int getOriginal() {
                return original;
            }

            public void setOriginal(int original) {
                this.original = original;
            }

            public double getPreload() {
                return preload;
            }

            public void setPreload(double preload) {
                this.preload = preload;
            }

            public String getReplay_gain() {
                return replay_gain;
            }

            public void setReplay_gain(String replay_gain) {
                this.replay_gain = replay_gain;
            }

            public String getShow_link() {
                return show_link;
            }

            public void setShow_link(String show_link) {
                this.show_link = show_link;
            }

            public int getSong_file_id() {
                return song_file_id;
            }

            public void setSong_file_id(int song_file_id) {
                this.song_file_id = song_file_id;
            }
        }
    }
}
