package com.hudson.donglingmusic.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

//处理数据
public class HistoryMusicDao {

	private static HistoryMusicDao mHistoryMusicDao = null;
	/**
	 * 单例
	 * @return httpUtils单例
	 */
	public static HistoryMusicDao getInstance(Context context){
		if(mHistoryMusicDao==null){
			synchronized (HistoryMusicDao.class){
				if(mHistoryMusicDao==null)
					mHistoryMusicDao = new HistoryMusicDao(context);
			}
		}
		return mHistoryMusicDao;
	}

	private HistoryMusicOpenHelper musicOpenHelper;// 数据库帮助类对象

	private HistoryMusicDao(Context context) {
		musicOpenHelper = new HistoryMusicOpenHelper(context);
	}

	public void insert(MusicInfo musicInfo) {
		SQLiteDatabase db = musicOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(
					"insert into history(mMusicId,song_id,album_id, duration, title,artist, album, filedata) values(?,?,?,?,?,?,?,?);",
					new Object[] {musicInfo.getMusicId(),musicInfo.getSongId(), musicInfo.getAlbumId(),
							musicInfo.getDuration() +"", musicInfo.getTitle(),
							musicInfo.getArtist(),musicInfo.getAlbum(),musicInfo.getData()});
			db.close();
		}
	}

	/**
	 * 清空数据库
	 */
	public void deleteDataBase(){
		SQLiteDatabase db = musicOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from history;");
			db.close();
		}
	}

	public void deleteItem(MusicInfo info){
		SQLiteDatabase db = musicOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from history where song_id = ?;",
					new Integer[]{info.getSongId()});
			db.close();
		}
	}

	public void deleteItemByMusicId(int musicId){
		SQLiteDatabase db = musicOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from history where mMusicId = ?;",
					new Integer[]{musicId});
			db.close();
		}
	}


	//修改歌曲信息，并不会同步到系统数据库
	public void UpdateMusicInfo(String title,String artist,String album,int music_id) {
		SQLiteDatabase db = musicOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("update history set title = ? ,artist = ? ,album = ? where mMusicId = ?;",new Object[]{title,artist,album,music_id});
			db.close();
		}
	}

	/**
	 * 
	 * @param fromIndex 起始id
	 * repeatTimes 循环次数
	 */
	public void ChangeMusicId(int fromIndex,int repeatTimes){
		SQLiteDatabase db = musicOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			for(int i= 0;i<repeatTimes;i++){
				db.execSQL("update history set mMusicId = ? where mMusicId = ?;",new Object[]{fromIndex,fromIndex+1});
				fromIndex+=1;
			}
			db.close();
		}
	}
	
	public ArrayList<MusicInfo> queryAll() {
		SQLiteDatabase db = musicOpenHelper.getReadableDatabase();	// 获得一个只读的数据库对象
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery("select mMusicId,song_id,album_id, duration, title,artist, album, filedata from history;", null);
			if(cursor != null && cursor.getCount() > 0) {
				ArrayList<MusicInfo> personList = new ArrayList<MusicInfo>();
				int music_id,song_id,album_id;
				String duration,title,artist,album,filedata;
				while(cursor.moveToNext()) {
					music_id = cursor.getInt(0);
					song_id = cursor.getInt(1);	//注意这里的id对应上面rawQuery的，而不是表格
					album_id = cursor.getInt(2);	
					duration = cursor.getString(3);
					title = cursor.getString(4);
					artist = cursor.getString(5);
					album = cursor.getString(6);
					filedata = cursor.getString(7);
					personList.add(new MusicInfo(music_id,song_id,album_id, Long.parseLong(duration), title,artist, album, filedata));
				}
				db.close();
				return personList;
			}
			db.close();
		}
		return null;
	}
	
	/**
	 * 根据id查询人
	 * @param son_id
	 * @return
	 */
	public MusicInfo queryItem(int son_id) {
		SQLiteDatabase db = musicOpenHelper.getReadableDatabase();	// 获得一个只读的数据库对象
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery("select mMusicId,song_id,album_id, duration, title,artist, album, filedata from history where song_id = ?;", new String[]{son_id + ""});
			if(cursor != null && cursor.moveToFirst()) {
				int music_id = cursor.getInt(0);
				int song_id = cursor.getInt(1);	
				int album_id = cursor.getInt(2);	
				String duration = cursor.getString(3);
				String title = cursor.getString(4);
				String artist = cursor.getString(5);
				String album = cursor.getString(6);
				String filedata = cursor.getString(7);
				db.close();
				return new MusicInfo(music_id,song_id,album_id, Long.parseLong(duration), title,artist, album, filedata);
			}
			db.close();
		}
		return null;
	}
}
