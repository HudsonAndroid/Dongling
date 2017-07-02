// IDonglingMusicAidlInterface.aidl
package com.hudson.donglingmusic;

// Declare any non-default types here with import statements
import com.hudson.donglingmusic.db.MusicInfo;

interface IDonglingMusicAidlInterface {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    void pause();
    void play(int position);
    void playByPath(String path);
    void stop();
    void pre();
    void next();
    void removeSongFromPlayList(int position);
    void removeSongFromPlayListByPath(String path);
    int getDuration();
    int getCurPosition();
    int getCurMusicInfoIndex();
    void seekTo(int position);
    MusicInfo getCurMusicInfo();
    List<MusicInfo> getPlayList();
    void setPlayList(in List<MusicInfo> playList);
    int getPlayState();
    boolean isPlaying();
    int getPlayMode();
    void setPlayState(int state);
    void setPlayMode(int mode);
    void exit();
    int getAudioSessionId();
}
