package com.hudson.donglingmusic.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/14.
 */

public class MySharePreferences {
    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;
    private static MySharePreferences instance;

    public MySharePreferences(Context context) {
        mSp = context.getSharedPreferences("donglingmusic",
                Context.MODE_PRIVATE);
        mEditor = mSp.edit();
    }

    /**
     * 单例模式
     * @return
     */
    public static MySharePreferences getInstance(){
        synchronized (MySharePreferences.class) {
            if(instance==null){
                instance = new MySharePreferences(UIUtils.getContext());
            }
        }
        return instance;
    }

    /**
     * 保存是否是第一次进入应用
     */
    public void saveFlagFirstUseApp(boolean firstUsae) {
        mEditor.putBoolean("firstUse", firstUsae);
        mEditor.commit();
    }

    /**
     * 获取是否是第一次使用app
     * @return
     */
    public boolean getFlagFirstUseApp() {
        return mSp.getBoolean("firstUse", true);
    }

    /**
     * 设置缓存
     * @param key 缓存标识
     * @param data json数据
     */
    public void saveCachData(String key,String data){
        mEditor.putString(key, data);
        mEditor.commit();
    }

    /**
     * 获取缓存
     * @param key 缓存标识
     * @return json格式缓存
     */
    public String getCachData(String key){
        return mSp.getString(key,null);
    }



    public void saveFontFilePath(String path){
        mEditor.putString("com.hudson.donglingmusic.fontpath", path);
        mEditor.commit();
    }

    public String getFontFilePath(){
        return mSp.getString("com.hudson.donglingmusic.fontpath",null);
    }

    public void saveDefaultBgPath(String path){
        mEditor.putString("com.hudson.donglingmusic.defaultbgpath", path);
        mEditor.commit();
    }

    public String getDefaultBgPath(){
        return mSp.getString("com.hudson.donglingmusic.defaultbgpath",null);
    }

    public void saveLyricsShowCount(int count){
        mEditor.putInt("com.hudson.donglingmusic.lyricscount",count);
        mEditor.commit();
    }

    public int getLyricsShowCount(){
        return mSp.getInt("com.hudson.donglingmusic.lyricscount",3);
    }

    public void saveNormalLyricsTextSize(int size){
        mEditor.putInt("com.hudson.donglingmusic.normalsize",size);
        mEditor.commit();
    }

    public int getNormalLyricsTextSize(){
        return mSp.getInt("com.hudson.donglingmusic.normalsize",22);
    }

    public void saveFocusLyricsTextSize(int size){
        mEditor.putInt("com.hudson.donglingmusic.focussize",size);
        mEditor.commit();
    }

    public int getFocusLyricsTextSize(){
        return mSp.getInt("com.hudson.donglingmusic.focussize",24);
    }

    public void saveAdjustOffsetTime(int time){
        mEditor.putInt("com.hudson.donglingmusic.adjustoffsettime",time);
        mEditor.commit();
    }

    public int getAdjustOffsetTime(){
        return mSp.getInt("com.hudson.donglingmusic.adjustoffsettime",500);
    }

    public void saveGaosiRadius(int radius){
        mEditor.putInt("com.hudson.donglingmusic.gaosiradius",radius);
        mEditor.commit();
    }

    public int getGaosiRadius(){
        return mSp.getInt("com.hudson.donglingmusic.gaosiradius",100);
    }

    public void saveNormalLyricsColor(int color){
        mEditor.putInt("com.hudson.donglingmusic.normal_lyrics_color",color);
        mEditor.commit();
    }

    public int getNormalLyricsColor(){
        return mSp.getInt("com.hudson.donglingmusic.normal_lyrics_color", Color.GRAY);
    }

    public void saveFocusLyricsColor(int color){
        mEditor.putInt("com.hudson.donglingmusic.focus_lyrics_color",color);
        mEditor.commit();
    }

    public int getFocusLyricsColor(){
        return mSp.getInt("com.hudson.donglingmusic.focus_lyrics_color", Color.YELLOW);
    }

    public void saveVisibleCircleAddColor(int color){
        mEditor.putInt("com.hudson.donglingmusic.visible_circle_add",color);
        mEditor.commit();
    }

    public int getVisibleCircleAddColor(){
        return mSp.getInt("com.hudson.donglingmusic.visible_circle_add",-1);
    }

    public void saveVisibleCircleColumnCount(int count){
        mEditor.putInt("com.hudson.donglingmusic.visible_circle_column_count",count);
        mEditor.commit();
    }

    public int getVisibleCircleColumnCount(){
        return mSp.getInt("com.hudson.donglingmusic.visible_circle_column_count",120);
    }

    public void saveVisibleCircleColumnWidth(int width){
        mEditor.putInt("com.hudson.donglingmusic.visible_circle_column_width",width);
        mEditor.commit();
    }

    public int getVisibleCircleColumnWidth(){
        return mSp.getInt("com.hudson.donglingmusic.visible_circle_column_width",6);
    }

    public void saveVisibleCircleModifyColor(int color){
        mEditor.putInt("com.hudson.donglingmusic.visible_circle_modify",color);
        mEditor.commit();
    }

    public int getVisibleCircleModifyColor(){
        return mSp.getInt("com.hudson.donglingmusic.visible_circle_modify",-1);
    }


    public void saveColorsArray(int[] colors){
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < colors.length; i++) {
            if(i!=colors.length-1)
                stringBuilder.append(colors[i]).append("#");
            else
                stringBuilder.append(colors[i]);
        }
        mEditor.putString("com.hudson.donglingmusic.circle_visible_colors",stringBuilder.toString());
        mEditor.commit();
    }

    public int[] getColorsArray(){
        String colorsString = mSp.getString("com.hudson.donglingmusic.circle_visible_colors",null);
        if(colorsString==null){
            return new int[]{0xffffffff};
        }
        String[] strs = colorsString.split("#");
        int length = strs.length;
        int[] colors = new int[length];
        for (int i = 0; i < length; i++) {
            colors[i] = Integer.valueOf(strs[i]);
        }
        return colors;
    }

    /**
     * 保存扫描本地音乐完成
     * @param scanComplete
     */
    public void saveScanMusicsComplete(boolean scanComplete){
        mEditor.putBoolean("com.hudson.donglingmusic.scan_local_music",scanComplete);
        mEditor.commit();
    }

    /**
     * 获取是否扫描完成
     * @return
     */
    public boolean getScanMusicsComplete(){
        return mSp.getBoolean("com.hudson.donglingmusic.scan_local_music",false);
    }

    public void saveRotateCircleVisibleMusicColor(boolean rotate){
        mEditor.putBoolean("com.hudson.donglingmusic.rotate_circle_color",rotate);
        mEditor.commit();
    }

    public boolean getRotateCircleVisibleMusicColor(){
        return mSp.getBoolean("com.hudson.donglingmusic.rotate_circle_color",false);
    }

    public void saveVisibleBgEnable(boolean enable){
        mEditor.putBoolean("com.hudson.donglingmusic.visible_bg_enable",enable);
        mEditor.commit();
    }

    public boolean getVisibleBgEnable(){
        return mSp.getBoolean("com.hudson.donglingmusic.visible_bg_enable",false);
    }

    public void saveVisibleBgRadius(int radius){
        mEditor.putInt("com.hudson.donglingmusic.visible_bg_radius",radius);
        mEditor.commit();
    }

    public int getVisibleBgRadius(){
        return mSp.getInt("com.hudson.donglingmusic.visible_bg_radius",500);
    }

    public void saveVisibleBgColor(int color){
        mEditor.putInt("com.hudson.donglingmusic.visible_bg_color",color);
        mEditor.commit();
    }

    public int getVisibleBgColor(){
        return mSp.getInt("com.hudson.donglingmusic.visible_bg_color",0xaa1CC234);
    }

    public void savePlayMode(int mode){
        mEditor.putInt("com.hudson.donglingmusic.play_mode",mode);
        mEditor.commit();
    }

    public int getPlayMode(){
        return mSp.getInt("com.hudson.donglingmusic.play_mode", MusicService.MODE_LIST_ORDER);
    }

    public void saveNextPlayIndex(int nextIndex){
        mEditor.putInt("com.hudson.donglingmusic.next_play_index",nextIndex);
        mEditor.commit();
    }

    public int getNextPlayIndex(){
        return mSp.getInt("com.hudson.donglingmusic.next_play_index", -1);
    }

    public void saveNextPlayChangeList(boolean changeOrNot){
        mEditor.putBoolean("com.huson.donglingmusic.next_play_change_list",changeOrNot);
        mEditor.commit();
    }

    public boolean getNextPlayChangeList(){
        return mSp.getBoolean("com.huson.donglingmusic.next_play_change_list",false);
    }

    public void saveNextPlayListInfo(String nextPlayListInfo){
        mEditor.putString("com.hudson.donglingmusic.next_play_list_info",nextPlayListInfo);
        mEditor.commit();
    }

    public String getNextPlayListInfo(){
        return mSp.getString("com.hudson.donglingmusic.next_play_list_info",null);
    }

    public void saveExitAtTimeType(int type){
        mEditor.putInt("com.hudson.donglingmusic.exit_time_type",type);
        mEditor.commit();
    }

    public int getExitAtTimeType(){
        return mSp.getInt("com.hudson.donglingmusic.exit_time_type", DialogUtils.TYPE_EXIT_NONE);
    }

    /**
     * 保存定时时间段退出的开始时间，用于计算剩余时间
     */
    public void saveExitTimeOffsetExitTime(long startTime){
        mEditor.putLong("com.hudson.donglingmusic.exit_offset_start_time",startTime);
        mEditor.commit();
    }

    public long getExitTimeOffsetExitTime(){
        return mSp.getLong("com.hudson.donglingmusic.exit_offset_start_time",-1);
    }


    public void saveExitAtTimeSongCount(int count){
        mEditor.putInt("com.hudson.donglingmusic.exit_time_song_count",count);
        mEditor.commit();
    }

    public int getExitAtTimeSongCount(){
        return mSp.getInt("com.hudson.donglingmusic.exit_time_song_count",-1);
    }

    public void saveExitTimePoint(String timePoint){
        mEditor.putString("com.hudson.donglingmusic.exit_time_point",timePoint);
        mEditor.commit();
    }

    public String getExitTimePoint(){
        return mSp.getString("com.hudson.donglingmusic.exit_time_point",null);
    }

    public void saveDownloadMusicFileTotalSize(String key,long value){
        mEditor.putLong("total"+key,value);
        mEditor.commit();
    }

    public long getDownloadMusicFileTotalSize(String key){
        return mSp.getLong("total"+key,0);
    }

    public void saveDownloadMusicFileCurSize(String key,long value){
        mEditor.putLong("cur"+key,value);
        mEditor.commit();
    }

    public long getDownloadMusicFileCurSize(String key){
        return mSp.getLong("cur"+key,0);
    }

    public void removeDownloadMusicSave(String key){
        mEditor.remove("total"+key);
        mEditor.remove("cur"+key);
        mEditor.commit();
    }

    public void saveDownloadMusicType(int type){
        mEditor.putInt("com.hudson.donglingmusic.download_music_type",type);
        mEditor.commit();
    }

    public int getDownloadMusicType(){
        return mSp.getInt("com.hudson.donglingmusic.download_music_type",128);
    }

    public void savePlayerPlaylistInfo(String playListInfo){
        mEditor.putString("playlistInfo",playListInfo);
        mEditor.commit();
    }

    public String getPlayerPlaylistInfo(){
        return mSp.getString("playlistInfo","null");
    }

    /**
     * 在使用widget时由于无法访问，所以使用sp保存
     * @param isPlaying
     */
    public void savePlayerIsPlaying(boolean isPlaying){
        mEditor.putBoolean("isPlaying",isPlaying);
        mEditor.commit();
    }

    public boolean getPlayerIsPlaying(){
        return mSp.getBoolean("isPlaying",false);
    }

    public void saveCurMusicTitle(String title){
        mEditor.putString("curMusicTitle",title);
        mEditor.commit();
    }

    public String getCurMusicTitle(){
        return mSp.getString("curMusicTitle",UIUtils.getString(R.string.app_name));
    }

    public void saveCurMusicAlbumId(int albumId){
        mEditor.putInt("curMusicAlbumId",albumId);
        mEditor.commit();
    }

    public int getCurMusicAlbumId(){
        return mSp.getInt("curMusicAlbumId",-1);
    }

    public void saveShowDeskTopLyrics(boolean showDesktopLyrics){
        mEditor.putBoolean("showDesktopLyrics",showDesktopLyrics);
        mEditor.commit();
    }

    public boolean getShowDeskTopLyrics(){
        return mSp.getBoolean("showDesktopLyrics",false);
    }

    public void saveExitSongInfoOrNot(boolean exitSongInfo){
        mEditor.putBoolean("exitSongInfo",exitSongInfo);
        mEditor.commit();
    }

    public boolean getExitSongInfoOrNot(){
        return mSp.getBoolean("exitSongInfo",true);
    }

    public void saveExitSong(MusicInfo musicInfo){
        if(musicInfo!=null){
            mEditor.putString("lastPlaySong",musicInfo.getStringFromInfo());
            mEditor.commit();
        }
    }

    public MusicInfo getExitSong(){
        return MusicInfo.getMusicInfoFromString(mSp.getString("lastPlaySong",null));
    }

    public void saveExitSongProgressOrNot(boolean exitSongProgress){
        mEditor.putBoolean("exitSongProgress",exitSongProgress);
        mEditor.commit();
    }

    public boolean getExitSongProgressOrNot(){
        return mSp.getBoolean("exitSongProgress",true);
    }

    public void saveLastExitSongProgress(int progress){
        mEditor.putInt("lastExitSongProgress",progress);
        mEditor.commit();
    }

    public int getLastExitSongProgress(){
        return mSp.getInt("lastExitSongProgress",-1);
    }

    public void saveDesktopLyricsLocationX(int x){
        mEditor.putInt("desktopLyricsLocationX",x);
        mEditor.commit();
    }

    public int getDesktopLyricsLocationX(){
        return mSp.getInt("desktopLyricsLocationX",-1);
    }

    public void saveDesktopLyricsLocationY(int y){
        mEditor.putInt("desktopLyricsLocationY",y);
        mEditor.commit();
    }

    public int getDesktopLyricsLocationY(){
        return mSp.getInt("desktopLyricsLocationY",-1);
    }

    public void saveIsHeadsetPlugPause(boolean pause){
        mEditor.putBoolean("isHeadsetPlugPause",pause);
        mEditor.commit();
    }

    public boolean getIsHeadsetPlugPause(){
        return mSp.getBoolean("isHeadsetPlugPause",true);
    }

    public void saveIsTelephonyCompleteContinue(boolean continuePlay){
        mEditor.putBoolean("isTelephonyCompleteContinue",continuePlay);
        mEditor.commit();
    }

    public boolean getIsTelephonyCompleteContinue(){
        return mSp.getBoolean("isTelephonyCompleteContinue",false);
    }

    public void saveIsWidgetCreated(boolean isWidgetCreated){
        mEditor.putBoolean("isWidgetCreated",isWidgetCreated);
        mEditor.commit();
    }

    public boolean getIsWidgetCreated(){
        return mSp.getBoolean("isWidgetCreated",false);
    }

//    /**
//     * 保存当前歌词的offset，避免由于界面销毁导致offset无效
//     */
//    public void saveCurLyricsOffset(int offset){
//        mEditor.putInt("curlyricsOffset",offset);
//        mEditor.commit();
//    }
//
//    public int getCurLyricsOffset(){
//        return mSp.getInt("curlyricsOffset",0);
//    }

    public void saveModifyLyrics(String modifyLrc){
        mEditor.putString("modifyLrc",modifyLrc);
        mEditor.commit();
    }

    public String getModifyLyrics(){
        return mSp.getString("modifyLrc","");
    }

    public void saveNewGedanTitle(String title){
        mEditor.putString("gedanTitle",title);
        mEditor.commit();
    }

    public String getNewGedanTitle(){
        return mSp.getString("gedanTitle","");
    }



    public void saveNewGedanTag(String tag){
        mEditor.putString("gedanTag",tag);
        mEditor.commit();
    }

    public String getNewGedanTag(){
        return mSp.getString("gedanTag","");
    }

    /**
     * 保存历史搜索记录
     * @param historySearch
     */
    public void saveHistorySearch(ArrayList<String> historySearch){
        if(historySearch == null|(historySearch != null && historySearch.size() == 0)){
            mEditor.putString("historySearch",null);
            mEditor.commit();
        }else{
            StringBuilder sb = new StringBuilder(historySearch.get(0)).append("\n");
            for (int i = 1; i < historySearch.size(); i++) {
                sb = sb.append(historySearch.get(i)).append("\n");
            }
            mEditor.putString("historySearch",sb.toString());
            mEditor.commit();
        }
    }

    /**
     * 获取历史搜索记录
     * @return
     */
    public ArrayList<String> getHistorySearch(){
        String srcStrs = mSp.getString("historySearch",null);
        if(!TextUtils.isEmpty(srcStrs)){
            String[] strArray = srcStrs.split("\n");
            int length = strArray.length;
            if(length >0){
                ArrayList<String> results = new ArrayList<>();
                for (int i = 0; i <length; i++) {
                    results.add(strArray[i]);
                }
                return results;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * 保存播放列表删除过的Item
     * @param deleteItems
     */
    public void savePlayListDeleteItem(ArrayList<Integer> deleteItems){
        if(deleteItems == null|(deleteItems!=null&&deleteItems.size() == 0)){
            mEditor.putString("deleteItem",null);
            mEditor.commit();
        }else{
            String s = String.valueOf(deleteItems.get(0)) + "\n";
            for (int i = 1; i < deleteItems.size(); i++) {
                s += String.valueOf(deleteItems.get(i)) + "\n";
            }
            mEditor.putString("deleteItem",s);
            mEditor.commit();
        }
    }

    /**
     * 获取播放列表删除过的item
     * @return
     */
    public ArrayList<Integer> getPlayListDeleteItem(){
        String srcStrs = mSp.getString("deleteItem",null);
        if(!TextUtils.isEmpty(srcStrs)){
            String[] strArray = srcStrs.split("\n");
            int length = strArray.length;
            if(length >0){
                ArrayList<Integer> deleteItems = new ArrayList<>();
                for (int i = 0; i <length; i++) {
                    deleteItems.add(Integer.valueOf(strArray[i]));
                }
                return deleteItems;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }



    public int getHeadsetOnePressed(){
        return mSp.getInt("headsetOnePressedSelect",0);
    }

    public void saveHeadsetOnePressed(int select){
        mEditor.putInt("headsetOnePressedSelect",select);
        mEditor.commit();
    }

    public int getHeadsetTwoPressed(){
        return mSp.getInt("headsetTwoPressedSelect",1);
    }

    public void saveHeadsetTwoPressed(int select){
        mEditor.putInt("headsetTwoPressedSelect",select);
        mEditor.commit();
    }

    public int getHeadsetLongClick(){
        return mSp.getInt("headsetLongClickSelect",2);
    }

    public void saveHeadsetLongClick(int select){
        mEditor.putInt("headsetLongClickSelect",select);
        mEditor.commit();
    }

}
