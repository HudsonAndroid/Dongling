package com.hudson.donglingmusic.utils;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Hudson on 2017/3/20.
 * 文件管理工具类
 */

public class StorageUtils {

    private static String SD_ABSOLUTE_PATH = null;
    private static final String APP_ROOT_PATH = "donglingMusic/";//app的默认根路径,不含sd卡绝对路径
    private static final String APP_LYRICS_PATH = APP_ROOT_PATH + "Lyrics/";//app歌词路径
    private static final String APP_DOWNLOAD_PATH = APP_ROOT_PATH + "download/";//app下载路径
    private static final String APP_MUSIC_PIC_PATH = APP_ROOT_PATH + "albumbg/";//app音乐图片路径
    private static final String APP_LYRICS_FONTS_PATH = APP_ROOT_PATH + "fonts/";//app歌词字体路径

    public static String getAppRootPath() {
        return APP_ROOT_PATH;
    }

    public static String getAppLyricsPath() {
        return APP_LYRICS_PATH;
    }

    public static String getAppDownloadPath() {
        return APP_DOWNLOAD_PATH;
    }

    public static String getAppMusicPicPath() {
        return APP_MUSIC_PIC_PATH;
    }

    public static String getAppLyricsFontsPath() {
        return APP_LYRICS_FONTS_PATH;
    }

    /**
     * 获取SD的的绝对路径，包含/符号，如/storage/sdcard/
     *
     * @return
     */
    public static String getSdAbsolutePath() {
        if (SD_ABSOLUTE_PATH == null) {
            SD_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }
        return SD_ABSOLUTE_PATH;
    }

    /**
     * 获取app的绝对路径，包含/
     *
     * @return
     */
    public static String getAppAbsolutePath() {
        return getSdAbsolutePath() + APP_ROOT_PATH;
    }

    /**
     * 获取歌词的绝对路径,包含/
     *
     * @return
     */
    public static String getAppLyricsAbsolutePath() {
        return getSdAbsolutePath() + APP_LYRICS_PATH;
    }

    /**
     * 获取下载地址的绝对路径,包含/
     *
     * @return
     */
    public static String getAppDownloadAbsolutePath() {
        return getSdAbsolutePath() + APP_DOWNLOAD_PATH;
    }

    /**
     * 获取音乐图片的绝对路径,包含/
     *
     * @return
     */
    public static String getAppMusicPicAbsolutePath() {
        return getSdAbsolutePath() + APP_MUSIC_PIC_PATH;
    }

    /**
     * 获取歌词字体的绝对路径,包含/
     *
     * @return
     */
    public static String getAppLyricsFontAbsolutePath() {
        return getSdAbsolutePath() + APP_LYRICS_FONTS_PATH;
    }


    /**
     * 请求权限
     *
     * @param activity
     */
    private static void requestSdCardPermission(Activity activity) {
        PermissionUtils.requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0, null);
        PermissionUtils.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 0, null);
    }


    /**
     * 检测sd卡是否可用
     *
     * @return
     */
    public static boolean isSdCardAvailable() {
        //检测SD卡是否可用,注意是equals，因为这个是字符串
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 写入sd卡
     *
     * @param pathNotContainSdDir 不包含/storage/sdcard0/
     * @param data                写入的数据
     * @return 是否成功, true成功
     */
    public static boolean writeSd(String pathNotContainSdDir, String data) {
        if (isSdCardAvailable()) {
            try {
                FileOutputStream fos = new FileOutputStream(
                        new File(getSdAbsolutePath() + pathNotContainSdDir));
                fos.write(data.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                throw new Exception("there is no SD card!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * 读取sd卡
     *
     * @param pathNotContainSdDir
     * @return 读取的数据
     */
    public static String readSD(String pathNotContainSdDir) {
        if (isSdCardAvailable()) {
            try {
                throw new Exception("there is no SD card!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        String data = "";
        try {
            FileInputStream fis = new FileInputStream(
                    new File(getSdAbsolutePath() + pathNotContainSdDir));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String lineString;
            while ((lineString = br.readLine()) != null) {
                data += (lineString + "\n");
            }
            br.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    /**
     * 保存歌词
     * @param lyrics 歌词的数组
     * @param songData 歌曲的全路径
     * @return 保存是否成功
     */
    public static boolean saveLyricsFile(String[] lyrics, String songData) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new StringBuilder(getAppLyricsAbsolutePath())
                    .append(MusicUtils.cutName(songData)).append(".lrc").toString()));
            int length = lyrics.length;
            for (int i = 0; i < length; i++) {
                bw.write(lyrics[i]);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
        }
        return true;
    }

    /**
     * 保存歌词
     * @param lyrics 歌词的数组
     * @param lyricsPath 歌曲的全路径
     * @return 保存是否成功
     */
    public static boolean saveLyricsFileByLyricsPath(String[] lyrics, String lyricsPath) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(lyricsPath));
            int length = lyrics.length;
            for (int i = 0; i < length; i++) {
                bw.write(lyrics[i]);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
        }
        return true;
    }


    public static String getGedanBgFilePath(String gedanTitle){
        return getAppMusicPicAbsolutePath()+"gedan"+gedanTitle+".jpg";
    }


}
