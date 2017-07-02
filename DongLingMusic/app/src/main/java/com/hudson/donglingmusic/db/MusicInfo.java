package com.hudson.donglingmusic.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.utils.MusicUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import org.litepal.crud.DataSupport;

public class MusicInfo extends DataSupport implements Parcelable{
	private int mMusicId;
	private int mSongId, mAlbumId;
	private long mDuration;
	private String mTitle, mArtist, mAlbum, mData;

	public int getMusicId() {
		return mMusicId;
	}

	public void setMusicId(int musicId) {
		mMusicId = musicId;
	}

	public int getSongId() {
		return mSongId;
	}

	public void setSongId(int songId) {
		mSongId = songId;
	}

	public int getAlbumId() {
		return mAlbumId;
	}

	public void setAlbumId(int albumId) {
		mAlbumId = albumId;
	}

	public long getDuration() {
		return mDuration;
	}

	public void setDuration(long duration) {
		mDuration = duration;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getArtist() {
		return mArtist;
	}

	public void setArtist(String artist) {
		mArtist = artist;
	}

	public String getAlbum() {
		return mAlbum;
	}

	public void setAlbum(String album) {
		mAlbum = album;
	}

	public String getData() {
		return mData;
	}

	public void setData(String data) {
		mData = data;
	}

	public MusicInfo(){}
	
	public MusicInfo(int musicId, int songId, int albumId, long duration, String title,
					 String artist, String album, String data) {
		super();
		mMusicId = musicId;
		mSongId = songId;
		mAlbumId = albumId;
		mDuration = duration;
		mTitle = title;
		mArtist = artist;
		mAlbum = album;
		mData = data;
	}

	/**
	 * 默认先获取歌手，如果歌手未知，就获取专辑
	 * @return
	 */
	public String getMusicSingerInfo(){
		if (mArtist!=null&&!mArtist.equals("<unknown>")) {
			return this.mArtist;
		}else if(!mData.startsWith("http")&&MusicUtils.cutName(this).contains("-")) {
			String musicTitle = MusicUtils.cutName(this);
			return musicTitle.substring(0, musicTitle.indexOf("-"));
		}else{
			return mAlbum;
		}
	}

	/**
	 * 默认先获取专辑信息，如果空或者未知就获取歌手信息
	 * @return
	 */
	public String getMusicAlbumInfo(){
		if(!TextUtils.isEmpty(mAlbum)&&!mAlbum.equals("<unknown>")){
			return mAlbum;
		}else if(mArtist!=null&&!mArtist.equals("<unknown>")){
			return mArtist;
		}else{
			return UIUtils.getString(R.string.unknown_music_album);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MusicInfo)) return false;

		MusicInfo musicInfo = (MusicInfo) o;

		if (getSongId() != musicInfo.getSongId()) return false;
		if (getAlbumId() != musicInfo.getAlbumId()) return false;
		if (getTitle() != null ? !getTitle().equals(musicInfo.getTitle()) : musicInfo.getTitle() != null)
			return false;
		if (getArtist() != null ? !getArtist().equals(musicInfo.getArtist()) : musicInfo.getArtist() != null)
			return false;
		return getAlbum() != null ? getAlbum().equals(musicInfo.getAlbum()) : musicInfo.getAlbum() == null;

	}

	@Override
	public int hashCode() {
		int result = getSongId();
		result = 31 * result + getAlbumId();
		result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
		result = 31 * result + (getArtist() != null ? getArtist().hashCode() : 0);
		result = 31 * result + (getAlbum() != null ? getAlbum().hashCode() : 0);
		return result;
	}

	public String getStringFromInfo(){
		return mMusicId+"\n"+mSongId+"\n"+mAlbumId+"\n"+
				mDuration+"\n"+mTitle+"\n"+mArtist+"\n"+
				mAlbum+"\n"+mData;
	}

	public static MusicInfo getMusicInfoFromString(String info){
		if(info == null){
			return null;
		}
		String[] strs = info.split("\n");
		if(strs.length!=8){//个数不对
			return null;
		}else{
			MusicInfo musicInfo = new MusicInfo();
			musicInfo.mMusicId = Integer.valueOf(strs[0]);
			musicInfo.mSongId = Integer.valueOf(strs[1]);
			musicInfo.mAlbumId = Integer.valueOf(strs[2]);
			musicInfo.mDuration = Long.valueOf(strs[3]);
			musicInfo.mTitle = strs[4];
			musicInfo.mArtist = strs[5];
			musicInfo.mAlbum = strs[6];
			musicInfo.mData = strs[7];
			return musicInfo;
		}
	}

	/**
	 * 获取内容数据，用于搜索
	 * @return
	 */
	public String getContentString(){
		String result = mTitle;
		if(mArtist.equals("<unknown>")){
			if(!mAlbum.equals("<unknown>")){
				result += ("("+mAlbum+")");
			}
		}else{
			if(mAlbum.equals("<unknown>")){
				result += ("("+mArtist+")");
			}else{
				result += ("("+mArtist+","+mAlbum+")");
			}
		}
		return result;
	}


	@Override
	public String toString() {
		return "MusicInfo{" +
				"mMusicId=" + mMusicId +
				", mSongId=" + mSongId +
				", mAlbumId=" + mAlbumId +
				", mDuration=" + mDuration +
				", mTitle='" + mTitle + '\'' +
				", mArtist='" + mArtist + '\'' +
				", mAlbum='" + mAlbum + '\'' +
				", mData='" + mData + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.mMusicId);
		dest.writeInt(this.mSongId);
		dest.writeInt(this.mAlbumId);
		dest.writeLong(this.mDuration);
		dest.writeString(this.mTitle);
		dest.writeString(this.mArtist);
		dest.writeString(this.mAlbum);
		dest.writeString(this.mData);
	}

	protected MusicInfo(Parcel in) {
		this.mMusicId = in.readInt();
		this.mSongId = in.readInt();
		this.mAlbumId = in.readInt();
		this.mDuration = in.readLong();
		this.mTitle = in.readString();
		this.mArtist = in.readString();
		this.mAlbum = in.readString();
		this.mData = in.readString();
	}

	public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>() {
		@Override
		public MusicInfo createFromParcel(Parcel source) {
			return new MusicInfo(source);
		}

		@Override
		public MusicInfo[] newArray(int size) {
			return new MusicInfo[size];
		}
	};
}
