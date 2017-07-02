package com.hudson.donglingmusic.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hudson.donglingmusic.utils.UIUtils;

import java.util.ArrayList;


public class SystemMusicOpenHelper {

	public static ArrayList<MusicInfo> queryMusics() {
	    ArrayList<MusicInfo> musiclistResult = new ArrayList<MusicInfo>();
	    ContentResolver cr = UIUtils.getContext().getContentResolver();
	    Cursor musics = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	            new String[] {
	                    MediaStore.Audio.Media._ID, // int
	                    MediaStore.Audio.Media.TITLE,
	                    MediaStore.Audio.Media.ARTIST,
	                    MediaStore.Audio.Media.ALBUM,
	                    MediaStore.Audio.Media.DURATION,
	                    MediaStore.Audio.Media.DATA, // String
	                    MediaStore.Audio.Media.DISPLAY_NAME, // String
	                    MediaStore.Audio.Media.MIME_TYPE, // String
	                    MediaStore.Audio.Media.ALBUM_ID // int
	            },
	            MediaStore.Audio.Media.IS_MUSIC + " = 1 AND "
	                    + MediaStore.Audio.Media.DURATION + " > 10000",
	            null,
	            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);	 
	    musics.moveToFirst();
	    while (!musics.isAfterLast()) {
	    	MusicInfo temp = new MusicInfo();
			temp.setSongId(musics.getInt(musics.getColumnIndex(MediaStore.Audio.Media._ID)));
			temp.setAlbum(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
	        temp.setArtist(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
	        temp.setData(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.DATA)));
	        temp.setDuration(musics.getLong(musics.getColumnIndex(MediaStore.Audio.Media.DURATION)));
	        temp.setTitle(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.TITLE)));
	        temp.setAlbumId(musics.getInt(musics.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
	        musiclistResult.add(temp);
	        musics.moveToNext();
	    }
	    musics.close();
	    return musiclistResult;
	}
}
