package com.hudson.donglingmusic.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.RotateAnimation;

import com.hudson.donglingmusic.db.MusicDao;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class MusicUtils {

	/**
	 * 通过music的data来获取对应列表的id
	 * @param data
	 * @param musics
	 * @return -1表示不存在
	 */
	public static int getMusicInfoIndexByData(String data,
										ArrayList<MusicInfo> musics) {
		for (int i = 0; i < musics.size(); i++) {
			if (musics.get(i).getData().equals(data)) {
				return i;
			}
		}
		return -1;
	}

    /**
     * 通过music的data来获取对应列表的id
     * @param data
     * @param musics
     * @return null表示不存在
     */
    public static MusicInfo getMusicInfoByData(String data,
                                              ArrayList<MusicInfo> musics) {
        MusicInfo musicInfo = null;
        for (int i = 0; i < musics.size(); i++) {
            musicInfo = musics.get(i);
            if (musicInfo.getData().equals(data)) {
				return musicInfo;
            }
        }
        return null;//没找到
    }

	/**
	 * 截取歌曲名
	 * @param musicInfo
	 * @return
	 */
	public static String cutName(MusicInfo musicInfo) {
        String resName = musicInfo.getData();
		if(resName.startsWith("http")){//如果是网络歌曲，那么是无法根据路径截取的
			return musicInfo.getTitle();
		}
		return resName.substring(resName.lastIndexOf("/") + 1,
				resName.lastIndexOf("."));
	}

    /**
     * 截取歌曲名
     * @param resName
     * @return
     */
    public static String cutName(String resName) {
		if(resName.startsWith("http")){
			return "";
		}
        return resName.substring(resName.lastIndexOf("/") + 1,
                resName.lastIndexOf("."));
    }

	/**
	 * 格式化时间，将毫秒转换为分:秒格式
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		String min = time / (1000 * 60) + "";
		String sec = time % (1000 * 60) + "";
		if (min.length() < 2) {
			min = "0" + time / (1000 * 60) + "";
		} else {
			min = time / (1000 * 60) + "";
		}
		if (sec.length() == 4) {
			sec = "0" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 3) {
			sec = "00" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 2) {
			sec = "000" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 1) {
			sec = "0000" + (time % (1000 * 60)) + "";
		}
		return min + ":" + sec.trim().substring(0, 2);
	}

	/**
	 * 截取歌曲title
	 * @param totalName
	 * @return 歌曲title
	 */
	public static String getTitleNameByTotalName(String totalName) {
		if (totalName.contains("-")) {
			int index = totalName.indexOf("-");
			if (totalName.charAt(index + 1) == ' ') {
				return totalName.substring(index + 2);
			} else {
				return totalName.substring(index + 1);
			}
		}
		return totalName;
	}

	/**
	 * 截取歌曲title
	 * @param totalName
	 * @return 歌手信息
	 */
	public static String getSingerNameByTotalName(String totalName) {
		if (totalName.contains("-")) {
			totalName = totalName.substring(0,totalName.indexOf("-"));
		}
		return totalName;
	}

//
//	public static ArrayList<String> getMusicNames(Context context) {// 获取歌曲名数组
//		musicInfos = new ArrayList<MusicInfo>();
//		musicInfos = new MusicDao(context).queryAll();
//		ArrayList<String> MusicNames = new ArrayList<String>();
//		for (MusicInfo musicInfo : musicInfos) {
//			MusicNames.add(MusicUtils.cutName(musicInfo.mData));
//		}
//		return MusicNames;
//	}
//
//	public static int getIdByName(Context context, String name) {
//		musicInfos = MyApplication.getMyApplication().getAllList();
//		if(musicInfos.size() ==0){
//			musicInfos = new ArrayList<MusicInfo>();
//			musicInfos = new MusicDao(context).queryAll();
//		}
//		for (int i = 0; i < musicInfos.size(); i++) {
//			if (musicInfos.get(i).mData.contains(name)) {
//				return i;
//			}
//		}
//		return -1;
//	}

	/*
	 * 酷狗歌词解析
	 */
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];
		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);
		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		decompresser.end();
		return output;
	}

	public static boolean isAppOnForeground(Context context) {// 判断本应用是否在前台运行
		ActivityManager mActivityManager = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE));
		String mPackageName = context.getPackageName();
		List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();// 如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
		if (tasksInfo.size() > 0) {
			// L.i("top Activity = "
			// + tasksInfo.get(0).topActivity.getPackageName());
			// 应用程序位于堆栈的顶层
			if (mPackageName.equals(tasksInfo.get(0).topActivity
					.getPackageName()) && isScreenOn) {
				return true;
			}
		}
		return false;
	}

//	public static BitmapDrawable SetViewBackGround(Context context, MySharedStorage sp,
//			View v) {
//		String path = sp.getPath();
//		if (path == null) {
//			path = "bg_view1.jpg";
//		}
//		BitmapDrawable bitmapDrawable = null;
//		if (!sp.getUseFile()) {
//			try {
//				bitmapDrawable = new BitmapDrawable(context.getResources(),
//						context.getAssets().open("bkgs/" + path));
//				v.setBackground(bitmapDrawable);
//			} catch (IOException e) {
//			}
//		} else {
//			if(!new File(path).exists()){
//				Toast.makeText(context, "错误，没有找到本地背景图片，已自动切换回默认背景！", Toast.LENGTH_LONG).show();
//				path = "bg_view1.jpg";
//				try {
//					bitmapDrawable = new BitmapDrawable(context.getResources(),
//							context.getAssets().open("bkgs/" + path));
//					v.setBackground(bitmapDrawable);
//				} catch (IOException e) {
//				}
//				sp.savePath(path);
//				sp.saveUseFile(false);
//			}
//			bitmapDrawable = new BitmapDrawable(path);
//			v.setBackground(bitmapDrawable);
//		}
//		return bitmapDrawable;
//	}

	// 判断某个列表中是否存在某个元素
	public static boolean CheckListIsHave(List<Integer> list, int i) {
		for (int s : list) {
			if (s == i) {// 不能是Integer，否则的话会是一个对象
				return true;// 说明存在
			}
		}
		return false;
	}

	// 历史记录处理判断列表中是否存在该歌曲   【判断列表，存在即删除】
	/**
	 *
	 * @param list  所处理的列表
	 * @param i   要处理的id
	 * @param isFromHistory  是否来自历史记录list
	 * @return 0表示歌单不存在该歌曲 ；1表示歌单中存在该歌曲，而且把这首歌曲删除了  ;2表示从历史纪录来的，删了又添
	 */
	public static int  CheckAndSaveHistoryListIsExists(List<Integer> list, int i,boolean isFromHistory) {
		int type = 0;
		for (int j = 0; j < list.size(); j++) {
			if (list.get(j) == i) {
				list.remove(j);
				type = 1;
			}
		}
		if(isFromHistory){
			type = 2;
			list.add(i);
		}
		return type;
	}

	// 界面退出动画
	public static void startRotateExitAnimation(int cX, int cY, View view) {// 动画旋转出界面
		RotateAnimation animation = new RotateAnimation(0, 90, cX, cY);
		animation.setDuration(800);
		animation.setFillAfter(true);// 动画完成后保持完成的状态
		view.startAnimation(animation);
	}

	// 获取本地应用程序的版本信息
	public static String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();// 用来管理手机的APK
		try {
			// 得到一个指定APK功能清单文件
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static ArrayList<MusicInfo> initMusics(ArrayList<MusicInfo> musics,
			Context context) {
		musics = MyApplication.getMyApplication().getAllList();
		if (musics != null && musics.size() >= 0) {

		} else {// 避免因为系统回收内存导致问题的备用方案
			musics = new MusicDao(context).queryAll();
			MyApplication.getMyApplication().setAllList(musics);
		}
		return musics;
	}

	// 手机振动器
	//方法一：指定震动时长
	public static void Vibrate(final Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
	//方法二：指定震动模式以及重复与否
	/*
	 *
final Activity activity  ：调用该方法的Activity实例
long milliseconds ：震动的时长，单位是毫秒
long[] pattern  ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
	 */
	public static void Vibrate(final Context context, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
	
//	//删除某首歌曲，数据库、歌单、总歌单
//	/**
//	 * 请在子线程中执行
//	 * @param deleteIndex 要删除的歌曲id
//	 * @param sp  MysharedStorage的实例一个
//	 * @param context
//	 * @param totalSize  当前歌曲总数
//	 */
//	public static void RemoveOneMusicFromDataAndList(int deleteIndex,MySharedStorage sp,Context context,int totalSize){
//		//歌单处理，总计有：我的最爱、历史记录、我的歌单
//		List<Integer> musicList;
//		int music_id;
//		//我的最爱歌单处理
//		String  listString = sp.getMyFavoriteListString();
//		if (listString != null && !listString.equals("")) {
//			try {
//			  musicList = ListUtils.String2SceneList(listString);
//				for(int i = 0;i<musicList.size();i++){//绝不能优化该for
//					music_id = musicList.get(i);
//					if(music_id>deleteIndex){//如果大于，说明要减一
//						musicList.set(i, music_id-1);
//					}else if (music_id == deleteIndex) {//如果是等于，说明该删除
//						musicList.remove(i);
//					}
//				}
//				sp.saveMyFavoriteListString(ListUtils.SceneList2String(musicList));
//			}  catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		//历史记录处理
//		listString = sp.getMusicListString();
//		if (listString != null && !listString.equals("")) {
//			try {
//			  musicList = ListUtils.String2SceneList(listString);
//				for(int i = 0;i<musicList.size();i++){
//					music_id = musicList.get(i);
//					if(music_id>deleteIndex){
//						musicList.set(i, music_id-1);
//					}else if (music_id == deleteIndex) {
//						musicList.remove(i);
//					}
//				}
//				sp.saveMusicListString(ListUtils.SceneList2String(musicList));
//			}  catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		//最近添加
//		listString = sp.getRecentAddListString();
//		if(listString!=null&&!listString.equals("")){
//			try {
//				musicList = ListUtils.String2SceneList(listString);
//				for(int i = 0;i<musicList.size();i++){
//					music_id = musicList.get(i);
//					if(music_id>deleteIndex){
//						musicList.set(i, music_id-1);
//					}else if (music_id == deleteIndex) {
//						musicList.remove(i);
//					}
//				}
//				sp.saveRecentAddListString(ListUtils.SceneList2String(musicList));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		listString = sp.getMyMusicListString();
//		if (listString != null && !listString.equals("")) {
//			 try {
//				List<MusicListInfo> musicListInfos = ListUtils.String2List(listString);
//				if(musicListInfos!=null&&musicListInfos.size()!=0){
//					for(MusicListInfo musicListInfo:musicListInfos){
//						musicList = musicListInfo.MusicLists;
//						for(int i = 0;i<musicList.size();i++){
//							music_id = musicList.get(i);
//							if(music_id>deleteIndex){
//								musicList.set(i, music_id-1);
//							}else if (music_id == deleteIndex) {
//								musicList.remove(i);
//							}
//						}
//						sp.saveMyMusicListString(ListUtils.List2String(musicListInfos));
//					}
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		//数据库处理
//		MusicDao musicDao = new MusicDao(
//				context);
//		musicDao.delete(deleteIndex);// 删除数据库相应内容
//		musicDao.ChangeMusicId(deleteIndex, totalSize - 1
//				- deleteIndex);// 更改数据库歌曲id
//	}
//
//	public static boolean InsertMusicToDataBase(Context context,String path,MySharedStorage sp){
//		boolean add_success;
//		musicInfos = MyApplication.getMyApplication().getMusics();
//		MusicDao musicDao = new MusicDao(context);
//		if(musicInfos.size() ==0){
//			musicInfos = new ArrayList<MusicInfo>();
//			musicInfos = new MusicDao(context).queryAll();
//		}
//		MusicInfo music=  MusicUtils.getMusicInfoByData(context, path, SystemMusicOpenHelper.queryMusics(context));
//		int index = musicInfos.size();
//		if(music!=null){//如果系统数据库中存在该歌曲，把这首添加到我的数据库
//			music.setMusicId(index);//添加该歌曲
//			musicInfos.add(music);
//			musicDao.insert(music);
//			add_success = true;
//		}else {//系统数据库中没有该歌曲
//			music = new MusicInfo();
//			music.mSongId = -1;
//			music.mAlbumId = -1;
//			music.setMusicId(index);
//			music.mData = path;
//			music.mAlbum = "未知专辑";
//			String allname = MusicUtils.cutName(path);
//			music.mTitle = CutGetTitleName((allname));
//			music.mArtist = PlayPageActivity.getArtist(allname);
//			if(music.mTitle ==null&&music.mTitle.equals("")){
//				music.mTitle = allname;
//				music.mArtist = "未知歌手";
//			}//到此，就剩下总时间未知
//			MediaPlayer mediaPlayer = new MediaPlayer();
//			mediaPlayer.reset();
//			try {
//				mediaPlayer.setDataSource(path);
//				mediaPlayer.prepare();
//				Thread.sleep(20);
//			} catch (Exception e) {//像某些文件故意变更后缀名的需要排除
//				e.printStackTrace();
//				return false;
//			}
//			music.mDuration = mediaPlayer.getDuration();
//			if(music.mDuration >90000){//如果时长长于1:30就添加
//				mediaPlayer = null;//置为空
//				musicInfos.add(music);
//				musicDao.insert(music);
//				add_success = true;
//			}else {
//				add_success = false;
//			}
//		}
//		if(add_success){
//			String listString = sp.getRecentAddListString();
//			List<Integer> mymusic_list = null;
//			if(listString!=null){
//				try {
//					mymusic_list = ListUtils.String2SceneList(listString);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}else {
//				mymusic_list = new ArrayList<Integer>();
//			}
//			mymusic_list.add(0,index);
//			try {
//				sp.saveRecentAddListString(ListUtils.SceneList2String(mymusic_list));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return add_success;
//	}
//
//
//	//扫描文件夹歌曲
//	public static List<String> ScanMusicFromFileByPath(Context context,String path){
//		List<String> add_music_names = new ArrayList<String>();//所添加音乐名
//		MySharedStorage sp = new MySharedStorage(context);
//		ScanMusic(add_music_names, context, path,sp);
//		return add_music_names;
//	}
//
//	private static void ScanMusic(List<String> names,Context context,String path,MySharedStorage sp){//问题是，传过来的list是否会跟着函数内部变化而变化？
//		File file = new File(path);
//		File fi[] = file.listFiles();
//		String name;
//		if(fi!=null){
//			String mp3_name;
//			for(File f:fi){
//				mp3_name = f.getName();
//				if(!mp3_name.startsWith(".")){//隐藏文件不处理
//					name = path+"/"+mp3_name;
//					if (f.isDirectory()) {// 如果是文件夹
//						ScanMusic(names,context, name,sp);
//					} else if (name.endsWith(".mp3")) {// 如果是mp3文件
//						if(MusicUtils.getMusicInfoIndex(context, name) == -1){//说明没有找到，添加
//							if(InsertMusicToDataBase(context, name,sp)){//添加到数据库，如果失败那么取消往list中添加
//								names.add(mp3_name.replace(".mp3", ""));
//							}
//						}
//					} else {// 其他文件不处理
//
//					}
//				}
//			}
//		}
//	}
}
