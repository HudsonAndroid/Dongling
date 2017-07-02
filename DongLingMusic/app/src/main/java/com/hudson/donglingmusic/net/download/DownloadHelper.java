package com.hudson.donglingmusic.net.download;

import com.hudson.donglingmusic.bean.DownloadFailOrPauseInfo;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.utils.StorageUtils;

/**
 * Created by Hudson on 2017/4/23.
 * 下载的实际操作，全部交给本类
 */

public class DownloadHelper {

    public int mDownloadId;//下载id，用于区分多个下载状态下不同下载
    public String mDownloadUrl;//下载路径
    public String mDownloadTitle;
    public long mTotalSize;//总大小
    public long mCurDownloadSize;//下载的大小
    public int mCurDownloadState;
    public String mLocalPath;//本地路径

    //歌曲其他信息，用于加入本地音乐时更好添加数据库
    private String mAuthor;
    private String mAlbum;
    private int mAlbumId;

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(int albumId) {
        mAlbumId = albumId;
    }

    private DownloadHelper(int downloadId, String downloadName, String downloadUrl, String author,
                           String album, int albumId) {
        mDownloadId = downloadId;
        mDownloadTitle = downloadName;
        mDownloadUrl = downloadUrl;
        mLocalPath = new StringBuilder(StorageUtils.getAppDownloadAbsolutePath()).
                append(downloadName).append(".mp3").toString();
        mAuthor = author;
        mAlbum = album;
        mAlbumId = albumId;
    }

    /**
     * 通过歌曲的title来获取本地的路径
     * @param title
     * @return
     */
    public static String getLocalPathByMusicInfoTitle(String title){
        if(title == null){
            return null;
        }
        return new StringBuilder(StorageUtils.getAppDownloadAbsolutePath()).
                append(title).append(".mp3").toString();
    }

    /**
     * 创建新的下载对象，但是没有开始下载
     * musicinfo的data必须是http开头
     * @param musicInfo 网络歌曲的实例对象
     * @return
     */
    public static DownloadHelper getDownloadHelper(MusicInfo musicInfo){
        DownloadHelper helper = new DownloadHelper(musicInfo.getSongId(),musicInfo.getTitle(),
                musicInfo.getData(),musicInfo.getArtist(),musicInfo.getAlbum(),musicInfo.getAlbumId());
        helper.mCurDownloadSize = 0;
        helper.mCurDownloadState = MusicDownloadManager.STATE_NONE;
        return helper;
    }

    /**
     * 创建新的下载对象，但是没有开始下载
     * @param bean 上一次下载失败的保存的对象
     * @return
     */
    public static DownloadHelper getDownloadHelper(DownloadFailOrPauseInfo bean){
        DownloadHelper helper = new DownloadHelper(bean.getDownloadId(),bean.getDownloadTitle(),
                bean.getDownloadUrl(),bean.getAuthor(),bean.getAlbum(),bean.getAlbumId());
        helper.mCurDownloadSize = 0;
        helper.mCurDownloadState = MusicDownloadManager.STATE_NONE;
        return helper;
    }

}
