package com.hudson.donglingmusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicOpenHelper extends SQLiteOpenHelper {
	public static final String mDataBaseName = "donglingMusicplayer.db";

	public MusicOpenHelper(Context context) {
		super(context, mDataBaseName, null, 1);
	}
	//创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		//操作数据库
		String sql = "create table musicinfo(mMusicId integer,song_id integer,album_id integer,duration varchar(10),title varchar(100),artist varchar(100),album varchar(100),filedata varchar(100));";
		db.execSQL(sql);//创建表
	}
	//数据库版本号更新时回调
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
