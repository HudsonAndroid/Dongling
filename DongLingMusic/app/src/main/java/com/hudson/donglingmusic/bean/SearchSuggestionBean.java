package com.hudson.donglingmusic.bean;

import java.util.List;

/**
 * Created by Hudson on 2017/6/1.
 */

public class SearchSuggestionBean {

    /**
     * song : [{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"400120","songname":"告白气球","songid":"266322598","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"2507fdfc2a60958414cceL"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"61690","songname":"青花瓷","songid":"354387","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"6605568530958414d10L"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"41720","songname":"夜曲","songid":"1191265","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"9706122d610958414d0cL"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"37840","songname":"晴天","songid":"816477","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"59081D5A2BDA0858F045DB"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"35140","songname":"简单爱","songid":"10736444","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"9206a3d33c095848ca73L"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"33990","songname":"烟花易冷","songid":"228393","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"000537c290958414d0fL"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"30000","songname":"发如雪","songid":"1147070","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"56061180be0958414d02L"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"29250","songname":"千里之外","songid":"205792","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦,费玉清","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"7005323e00958414d0cL"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"28340","songname":"菊花台","songid":"252832","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"电影《满城尽带黄金甲》片尾曲","resource_provider":"1","control":"0000000000","encrypted_songid":"34081D65663C0858F091DD"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"27880","songname":"龙卷风","songid":"7316935","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"40081D6BA0B308591BD246"}]
     * album : [{"albumname":"周杰伦的床边故事","weight":"463010","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/98c6dc2454b0b891951652bb29c16cf2/541522187/541522187.jpg@s_0,w_40","albumid":"266322553"},{"albumname":"我很忙","weight":"157650","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/88389085/88389085.jpg@s_0,w_40","albumid":"68674"},{"albumname":"十一月的萧邦","weight":"137020","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/88653998/88653998.jpg@s_0,w_40","albumid":"697896"},{"albumname":"依然范特西","weight":"87290","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/cded2b0dd94f8ffad1d19e4cbf098f80/273953159/273953159.jpg@s_0,w_40","albumid":"64306"},{"albumname":"魔杰座","weight":"79920","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/a3b7ae3ab51d164aaf3c472d178d31b5/274049161/274049161.jpg@s_0,w_40","albumid":"116177"},{"albumname":"叶惠美","weight":"63850","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/67b5d358bcc58bc627da744b91a77238/541060889/541060889.jpg@s_0,w_40","albumid":"72257"},{"albumname":"范特西","weight":"52320","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/88730647/88730647.jpg@s_0,w_40","albumid":"2023093"},{"albumname":"跨时代","weight":"45980","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/c238284663904babc2ebad8b1d18b462/274045371/274045371.jpg@s_0,w_40","albumid":"67834"},{"albumname":"十二新作","weight":"40810","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/89325459/89325459.jpg@s_0,w_40","albumid":"31496572"},{"albumname":"八度空间","weight":"37910","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/88389240/88389240.jpg@s_0,w_40","albumid":"68717"}]
     * order : artist,song,album
     * error_code : 22000
     * artist : [{"yyr_artist":"0","artistname":"周杰伦","artistid":"7994","artistpic":"http://qukufile2.qianqian.com/data2/pic/046d17bfa056e736d873ec4f891e338f/540336142/540336142.jpg@s_0,w_48","weight":"570010"}]
     */

    private String order;
    private int error_code;
    private List<SongBean> song;
    private List<AlbumBean> album;
    private List<ArtistBean> artist;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<SongBean> getSong() {
        return song;
    }

    public void setSong(List<SongBean> song) {
        this.song = song;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    public List<ArtistBean> getArtist() {
        return artist;
    }

    public void setArtist(List<ArtistBean> artist) {
        this.artist = artist;
    }

    public static class SongBean {
        /**
         * bitrate_fee : {"0":"129|-1","1":"-1|-1"}
         * weight : 400120
         * songname : 告白气球
         * songid : 266322598
         * has_mv : 0
         * yyr_artist : 0
         * resource_type_ext : 0
         * artistname : 周杰伦
         * info :
         * resource_provider : 1
         * control : 0000000000
         * encrypted_songid : 2507fdfc2a60958414cceL
         */

        private String bitrate_fee;
        private String weight;
        private String songname;
        private String songid;
        private String has_mv;
        private String yyr_artist;
        private String resource_type_ext;
        private String artistname;
        private String info;
        private String resource_provider;
        private String control;
        private String encrypted_songid;

        public String getBitrate_fee() {
            return bitrate_fee;
        }

        public void setBitrate_fee(String bitrate_fee) {
            this.bitrate_fee = bitrate_fee;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }

        public String getSongid() {
            return songid;
        }

        public void setSongid(String songid) {
            this.songid = songid;
        }

        public String getHas_mv() {
            return has_mv;
        }

        public void setHas_mv(String has_mv) {
            this.has_mv = has_mv;
        }

        public String getYyr_artist() {
            return yyr_artist;
        }

        public void setYyr_artist(String yyr_artist) {
            this.yyr_artist = yyr_artist;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getResource_provider() {
            return resource_provider;
        }

        public void setResource_provider(String resource_provider) {
            this.resource_provider = resource_provider;
        }

        public String getControl() {
            return control;
        }

        public void setControl(String control) {
            this.control = control;
        }

        public String getEncrypted_songid() {
            return encrypted_songid;
        }

        public void setEncrypted_songid(String encrypted_songid) {
            this.encrypted_songid = encrypted_songid;
        }
    }

    public static class AlbumBean {
        /**
         * albumname : 周杰伦的床边故事
         * weight : 463010
         * artistname : 周杰伦
         * resource_type_ext : 0
         * artistpic : http://qukufile2.qianqian.com/data2/pic/98c6dc2454b0b891951652bb29c16cf2/541522187/541522187.jpg@s_0,w_40
         * albumid : 266322553
         */

        private String albumname;
        private String weight;
        private String artistname;
        private String resource_type_ext;
        private String artistpic;
        private String albumid;

        public String getAlbumname() {
            return albumname;
        }

        public void setAlbumname(String albumname) {
            this.albumname = albumname;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistpic() {
            return artistpic;
        }

        public void setArtistpic(String artistpic) {
            this.artistpic = artistpic;
        }

        public String getAlbumid() {
            return albumid;
        }

        public void setAlbumid(String albumid) {
            this.albumid = albumid;
        }
    }

    public static class ArtistBean {
        /**
         * yyr_artist : 0
         * artistname : 周杰伦
         * artistid : 7994
         * artistpic : http://qukufile2.qianqian.com/data2/pic/046d17bfa056e736d873ec4f891e338f/540336142/540336142.jpg@s_0,w_48
         * weight : 570010
         */

        private String yyr_artist;
        private String artistname;
        private String artistid;
        private String artistpic;
        private String weight;

        public String getTing_uid() {
            return ting_uid;
        }

        public void setTing_uid(String ting_uid) {
            this.ting_uid = ting_uid;
        }

        //这个新增来自于综合搜索
        private String ting_uid;





        public String getYyr_artist() {
            return yyr_artist;
        }

        public void setYyr_artist(String yyr_artist) {
            this.yyr_artist = yyr_artist;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getArtistid() {
            return artistid;
        }

        public void setArtistid(String artistid) {
            this.artistid = artistid;
        }

        public String getArtistpic() {
            return artistpic;
        }

        public void setArtistpic(String artistpic) {
            this.artistpic = artistpic;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }
}
