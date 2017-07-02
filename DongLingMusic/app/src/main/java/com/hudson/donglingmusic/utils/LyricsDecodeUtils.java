package com.hudson.donglingmusic.utils;

import android.util.Log;

import com.hudson.donglingmusic.bean.Lyrics;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hudson on 2017/1/1.
 * 歌词解析类
 */

public class LyricsDecodeUtils {
    //歌词集合，每一项中第一个是时间，第二个是文本
    private ArrayList<Lyrics> lyricsList = new ArrayList<>();

    public static String getFileIncode(File file) {
        if (!file.exists()) {
            System.err.println("getFileIncode: file not exists!");
            return null;
        }
        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            detector.reset();
            fis.close();
            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取歌词
     * @param path
     * @return
     */
    public ArrayList<Lyrics> readLRC(String path) {
        if(lyricsList!=null&&lyricsList.size()!=0){
            lyricsList.clear();
        }
        final InputStreamReader isr;
        FileInputStream fis= null;
        if (new File(path).exists()) {
            File file = new File(path);
            try {
                //创建一个文件输入流对象
                fis = new FileInputStream(file);
                String fileCode = getFileIncode(file);
                isr = new InputStreamReader(fis,(fileCode==null)?"utf-8":fileCode);
                readLrcFromInputStream(isr);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally{
                if(fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                setLyricsDelayTimes();
                return lyricsList;
            }
        }else{
            System.err.println("the lyric file doesn't exist!");
            return null;
        }
    }

    /**
     * 设置每句歌词播放前需要的延迟时间
     */
    private void setLyricsDelayTimes(){
        if(lyricsList!=null){
            Lyrics tmp;
            for(int i=0;i<lyricsList.size();i++){
                tmp = lyricsList.get(i);
                if(i==0){
                    tmp.setDelayTime(tmp.getLrcTime()-0);
                }else {
                    tmp.setDelayTime(tmp.getLrcTime()-lyricsList.get(i-1).getLrcTime());
                }
            }
        }
    }


    //从流中读取歌词
    private void readLrcFromInputStream(InputStreamReader isr){
        try{
            BufferedReader br = new BufferedReader(isr);
            String s = "";
            Lyrics lyrics ;
            int len;
            String splitLrcData[];
            while((s = br.readLine()) != null) {
                //替换字符
                s = s.replace("[", "");
                //分离“@”字符
                splitLrcData = s.split("]");
                len = splitLrcData.length;
                //判断是否以]结尾，存在[xx][xx]情况
                if(s.endsWith("]")){//不加入该歌词
                    continue;
                }
                if(len > 1) {
                    for(int i = 0;i<len-1;i++){
                        lyrics = new Lyrics();
                        lyrics.setLrcStr(splitLrcData[len-1]);//添加内容
//                        if(!s.endsWith("]")){//如果以"]"结尾，如："[00:08.78][04:35.99]"没有歌词内容
//
//                        }else {
//                            lyrics.setLrcStr("music...");//添加空白内容,linux系统以\n结尾，所以不可能跑进这个方法中
//                        }
                        long time = StringTime2IntTime(splitLrcData[i]);
                        if(time!=-1){
                            lyrics.setLrcTime(time);
                            lyricsList.add(lyrics);
                        }else{

                        }
                    }
                }
            }
            Collections.sort(lyricsList);//对歌词对象进行排序，2015.10.9发现有些歌词为了简单，出现了一句歌词有多个时间的情况，所以需要排序
        }catch(Exception e){
            Log.e("readLyric","read lyric occur error!");
            e.printStackTrace();
        }
    }

    /**
     * 保存歌词
     * @param savePath  保存路径
     * @param lyricsStringList  歌词文本（包括时间和内容）
     */
    public static void SaveLrc(String savePath,List<String> lyricsStringList) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(savePath));
            for (int i = 0; i < lyricsStringList.size(); i++) {
                bw.write(lyricsStringList.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 解析歌词时间
     * 1.标准格式歌词内容格式如下：
     * [00:02.32]陈奕迅
     * [00:03.43]好久不见
     * [00:05.22]歌词制作  王涛
     * 2.其他格式：
     * [00:02]陈奕迅
     * [00:03]好久不见
     * [00:05]歌词制作  王涛
     * 3.其他格式  本格式忽略
     * [00:02:32]陈奕迅
     * [00:03:43]好久不见
     * [00:05:22]歌词制作  王涛
     * @param timeStr 时间字符串
     * @return
     */
    public long StringTime2IntTime(String timeStr) {
        long currentTime = 0,second,minute;
        if(timeStr.contains(".")){
            timeStr = timeStr.replace(":", ".");  //用后面的替换前面的
            timeStr = timeStr.replace(".", "@");
            String timeData[] = timeStr.split("@"); //将时间分隔成字符串数组
            //分离出分、秒并转换为整型
            minute = Integer.parseInt(timeData[0]);
            second = Integer.parseInt(timeData[1]);
            long millisecond = Integer.parseInt(timeData[2]);  //我认为是10毫秒为单位，但是百度百科说是毫秒
            //计算上一行与下一行的时间转换为毫秒数
            currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        }else if(timeStr.contains(":")&&timeStr.length()<6){//第三种格式几乎没见过，不予考虑
            String timeData[] = timeStr.split(":"); //将时间分隔成字符串数组
            //分离出分、秒并转换为整型
            minute = Integer.parseInt(timeData[0]);
            second = Integer.parseInt(timeData[1]);
            currentTime = (minute * 60 + second) * 1000;
        }else{//说明字符串不是时间格式或者字符串是第三种类型（没处理）
            return -1;
        }
        return currentTime;
    }

}
