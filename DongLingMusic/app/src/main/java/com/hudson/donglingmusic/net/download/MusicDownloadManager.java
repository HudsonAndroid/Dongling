package com.hudson.donglingmusic.net.download;

import android.content.Intent;
import android.net.Uri;

import com.hudson.donglingmusic.bean.DownloadFailOrPauseInfo;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hudson on 2017/4/22.
 * 观察者模式
 * 用于给多个地方提供下载状态的信息
 */

public class MusicDownloadManager {

    private static MusicDownloadManager instance;
    public static final int STATE_NONE = 0;//未下载
    public static final int STATE_WAITING = 1;//等待下载
    public static final int STATE_DOWNLOADING = 2;//正在下载
    public static final int STATE_PAUSE = 3;//暂停
    public static final int STATE_SUCCESSFULLY = 4;//下载成功
    public static final int STATE_FAILED = 5;//下载失败
    private ArrayList<DownloadObserver> mObservers = new ArrayList<>();
    private MySharePreferences mInstance = MySharePreferences.getInstance();
    public static final String NEW_DOWNLOAD_MUSIC = "com.hudson.donglingmusic.new_local_music";
    public static final String DOWNLOAD_FAIL_OR_PAUSE_DATABASE = "download_fail_pause_database";

    /**
     * 单例模式
     * @return
     */
    public static MusicDownloadManager getInstance(){
        synchronized (MusicDownloadManager.class) {
            if(instance==null){
                instance = new MusicDownloadManager();
            }
        }
        return instance;
    }

    /**
     * 注册观察者
     * @param observer
     */
    public void registerObserver(DownloadObserver observer){
        if(observer!=null&&!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    /**
     * 注销观察者
     * @param observer
     */
    public void unRegisterOberver(DownloadObserver observer){
        if(observer!=null&&mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    /**
     * 下载监听器
     */
    public interface DownloadObserver{
        void onDownloadStateChanged(DownloadHelper helper);
        void onDownloadProgressUpdate(DownloadHelper helper);
    }


    //挨个通知观察者，发生变化了
    public void notifyDownloadProgressUpdate(DownloadHelper helper){
        for(DownloadObserver observer:mObservers){
            observer.onDownloadProgressUpdate(helper);
        }
    }

    public void notifyDownloadStateChanged(DownloadHelper helper){
        for(DownloadObserver observer:mObservers){
            observer.onDownloadStateChanged(helper);
        }
    }


    //多个下载,用于区分下载是否存在.ConcurrentHashMap线程安全的hashmap
    private ConcurrentHashMap<Integer,DownloadHelper> mDownloadHelperMap = new ConcurrentHashMap<>();
    private ArrayList<Integer> mDownloadId = new ArrayList<>();

    public ArrayList<Integer> getDownloadId() {
        return mDownloadId;
    }

    //下载任务集合
    private ConcurrentHashMap<Integer,DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Integer, DownloadHelper> getDownloadHelperMap() {
        return mDownloadHelperMap;
    }

    /**
     * 开始下载
     * @param musicInfo
     */
    public synchronized void startDownload(MusicInfo musicInfo){
        //如果是第一次下载，需要new一个下载对象，下载;如果是之前下载过，那么接着原来位置开始下载
        int songId = musicInfo.getSongId();
        DownloadHelper helper = mDownloadHelperMap.get(songId);
        if(helper == null){
            //产生一个下载的对象
            helper = DownloadHelper.getDownloadHelper(musicInfo);
            //记录该下载已经存在了
            mDownloadHelperMap.put(songId,helper);
            mDownloadId.add(songId);
        }
        download(helper);
    }

    /**
     * 开始下载
     * @param bean
     */
    public synchronized void startDownload(DownloadFailOrPauseInfo bean){
        //如果是第一次下载，需要new一个下载对象，下载;如果是之前下载过，那么接着原来位置开始下载
        DownloadHelper helper = mDownloadHelperMap.get(bean.getDownloadId());
        if(helper == null){
            //产生一个下载的对象
            helper = DownloadHelper.getDownloadHelper(bean);
            //记录该下载已经存在了
            mDownloadHelperMap.put(bean.getDownloadId(),helper);
            mDownloadId.add(bean.getDownloadId());
        }
        download(helper);
    }


    public synchronized void download(DownloadHelper helper){
        //状态切换，等待下载
        helper.mCurDownloadState = STATE_WAITING;
        notifyDownloadStateChanged(helper);

        //在线程池中执行
        DownloadTask downloadTask = new DownloadTask(helper);
        ThreadManager.getThreadPool().execute(downloadTask);
        //将下载任务放入集合，用于后续移除
        mDownloadTaskMap.put(helper.mDownloadId,downloadTask);
    }


    class DownloadTask implements Runnable{
        private DownloadHelper mHelper;

        public DownloadTask(DownloadHelper helper) {
            mHelper = helper;
        }

        @Override
        public void run() {
            System.out.println(mHelper.mLocalPath+"开始下载了");
            mHelper.mCurDownloadState = STATE_DOWNLOADING;
            notifyDownloadStateChanged(mHelper);
            //判断文件存不存在
            File file = new File(mHelper.mLocalPath);
            mHelper.mCurDownloadSize = mInstance.getDownloadMusicFileCurSize(mHelper.mLocalPath);
            mHelper.mTotalSize = mInstance.getDownloadMusicFileTotalSize(mHelper.mLocalPath);
            HttpURLConnection connection;
            if(!file.exists()||file.length() != mHelper.mCurDownloadSize||mHelper.mCurDownloadSize == 0){//从头开始下载
                //删除无效文件，不存在也可以删
                file.delete();
                mHelper.mCurDownloadSize = 0;
                System.out.println("从头开始下载");
                connection = getStartDownloadConnection(mHelper);
            }else{//接着原来位置下载
                //从本地文件中读取已经下载的信息，如需要下载的总大小
                System.out.println("从原来位置下载");
                connection = getPauseDownloadConnection(mHelper);
            }
            InputStream is = null;
            FileOutputStream fos = null;
            try{
                if(connection!=null&&connection.getInputStream()!=null){
                    is = connection.getInputStream();
                    fos = new FileOutputStream(file,true);//追加方式
                    int len = 0;
                    byte[] buffer = new byte[1024*4];
                    while((len = is.read(buffer))!=-1&&mHelper.mCurDownloadState == STATE_DOWNLOADING){
                        //只有是下载状态下，才写入
                        fos.write(buffer,0,len);
                        fos.flush();//把数据刷入本地
                        //更新下载进度
                        mHelper.mCurDownloadSize += len;
                        notifyDownloadProgressUpdate(mHelper);
                    }
                }else{
                    onDownloadFailed(file,mHelper);
                }
            }catch (Exception e){
                onDownloadFailed(file,mHelper);
            }finally {
                if(is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(connection!=null){
                    connection.disconnect();
                }
            }
            String localPath = mHelper.mLocalPath;
            if(file.length() == mHelper.mTotalSize){//判断是否下载成功
                mHelper.mCurDownloadState = STATE_SUCCESSFULLY;
                notifyDownloadStateChanged(mHelper);
                //删除sp保存的本地内容
                mInstance.removeDownloadMusicSave(localPath);
                int downloadId = mHelper.mDownloadId;
                mDownloadHelperMap.remove(downloadId);
                mDownloadTaskMap.remove(downloadId);
                //删除的是对象，不是id
                mDownloadId.remove((Integer) downloadId);
                //发送广播，通知往本地数据库添加该歌曲
                Intent intent = new Intent(NEW_DOWNLOAD_MUSIC);
                intent.putExtra("data",localPath);
                String artist = mHelper.getAuthor();
                intent.putExtra("artist", artist);
                String album = mHelper.getAlbum();
                intent.putExtra("album", album);
                intent.putExtra("title",mHelper.mDownloadTitle);
                int albumId = mHelper.getAlbumId();
                intent.putExtra("albumId", albumId);
                intent.putExtra("songId", downloadId);
                UIUtils.getContext().sendBroadcast(intent);
                //将下载记录保存到本地数据库
                MusicInfo musicInfo = new MusicInfo(-1,downloadId,albumId,0,mHelper.mDownloadTitle,
                        artist,album,localPath);
                //切换使用默认数据库（下载成功数据库)
                LitePal.useDefault();
                if(!musicInfo.save()){//如果保存失败，再次保存
                    System.out.println("保存失败!!!!!");
                    musicInfo.save();
                }
                //通知系统有新的音乐下载了
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(new File(localPath)));
                UIUtils.getContext().sendBroadcast(scanIntent);
                //从暂停或者下载失败的数据库中删除
                removeDownloadInfoFromFailOrPauseDatabase(downloadId);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mHelper.mDownloadTitle+" 下载成功!");
                    }
                });
            }else if(mHelper.mCurDownloadState == STATE_PAUSE){
                //保存暂停时的下载信息
                mInstance.saveDownloadMusicFileTotalSize(localPath,mHelper.mTotalSize);
                mInstance.saveDownloadMusicFileCurSize(localPath,mHelper.mCurDownloadSize);
                saveDownloadFailedOrPauseInformation(mHelper);
            }else{//下载失败
                onDownloadFailed(file,mHelper);
            }
        }
    }

    /**
     * 从下载失败和暂停的数据库中删除一项,如果不存在就啥也没干呗
     * @param downloadId 下载的Id
     */
    private void removeDownloadInfoFromFailOrPauseDatabase(int downloadId){
        LitePalDB litePalDB = new LitePalDB(DOWNLOAD_FAIL_OR_PAUSE_DATABASE, 1);
        litePalDB.addClassName(DownloadFailOrPauseInfo.class.getName());
        LitePal.use(litePalDB);//切换数据库
        DataSupport.deleteAll(DownloadFailOrPauseInfo.class,"mDownloadId = ? ",String.valueOf(downloadId));
    }

    /**
     * 下载失败
     * @param f
     * @param helper
     */
    private void onDownloadFailed(File f, final DownloadHelper helper){
        f.delete();//删除无效文件
        helper.mCurDownloadState = STATE_FAILED;
        helper.mCurDownloadSize = 0;
        //删除sp保存的本地内容
        mInstance.removeDownloadMusicSave(helper.mLocalPath);
        notifyDownloadStateChanged(helper);
        //数据库保存下载失败的歌曲
        saveDownloadFailedOrPauseInformation(helper);
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(helper.mDownloadTitle+" 下载失败!");
            }
        });
    }

    /**
     * 保存下载失败或者暂停的信息
     * @param helper
     */
    private void saveDownloadFailedOrPauseInformation(DownloadHelper helper){
        LitePalDB litePalDB = new LitePalDB(DOWNLOAD_FAIL_OR_PAUSE_DATABASE, 1);
        litePalDB.addClassName(DownloadFailOrPauseInfo.class.getName());
        LitePal.use(litePalDB);//切换数据库
        DownloadFailOrPauseInfo downloadFailOrPauseInfo = new DownloadFailOrPauseInfo(helper.mDownloadId,
                helper.mDownloadUrl,helper.mDownloadTitle,helper.mTotalSize,helper.mCurDownloadSize,
                helper.mCurDownloadState,helper.getAuthor(),helper.getAlbum(),helper.getAlbumId());
        if(!downloadFailOrPauseInfo.save()){
            System.out.println("下载失败或暂停信息保存失败");
            downloadFailOrPauseInfo.save();
        }
    }


    private HttpURLConnection getStartDownloadConnection(DownloadHelper helper){
        try{
            URL url = new URL(helper.mDownloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(4000);
            connection.setRequestMethod("GET");
            if(connection.getResponseCode() == 200){//连接成功
                helper.mTotalSize = connection.getContentLength();//获取下载内容的总长度
                return connection;
            }else {
                System.out.println("从头开始返回码错误");
                return null;
            }
        }catch (Exception e){
            System.out.println("从头开始异常");
            return null;
        }
    }

    private HttpURLConnection getPauseDownloadConnection(DownloadHelper helper){
        try{
            URL url = new URL(helper.mDownloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(4000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + helper.mCurDownloadSize+ "-" + helper.mTotalSize);
            if(connection.getResponseCode() == 206){//连接成功
                System.out.println("断点正常");
                return connection;
            }else {
                System.out.println("断点返回码错误");
                return null;
            }
        }catch (Exception e){
            System.out.println("断点异常");
            return null;
        }
    }


    public synchronized void pause(int downloadId){
        DownloadHelper helper = mDownloadHelperMap.get(downloadId);
        if(helper != null){
            //只有在等待下载与正在下载时才能暂停
            if(helper.mCurDownloadState == STATE_WAITING||helper.mCurDownloadState == STATE_DOWNLOADING){
                helper.mCurDownloadState = STATE_PAUSE;
                notifyDownloadStateChanged(helper);
                DownloadTask downloadTask = mDownloadTaskMap.get(downloadId);
                if(downloadTask != null){
                    //这里仅仅只是对未开始下载的任务而言，真正的暂停在while的读文件中
                    ThreadManager.getThreadPool().cancelTask(downloadTask);
                }
            }
        }
    }

    public void continueDownload(int downloadId){
        DownloadHelper helper = mDownloadHelperMap.get(downloadId);
        if(helper != null){
            if(helper.mCurDownloadState == STATE_PAUSE){
                download(helper);
            }
        }
    }

    /**
     * 下载失败情况下，在应用未关闭的情况下
     * @param downloadId
     */
    public void restartDownload(int downloadId){
        DownloadHelper helper = mDownloadHelperMap.get(downloadId);
        if(helper != null){
            mInstance.saveDownloadMusicFileTotalSize(helper.mLocalPath,0);
            mInstance.saveDownloadMusicFileCurSize(helper.mLocalPath,0);
            download(helper);
        }
    }


    public void cancelDownload(int downloadId){
        DownloadHelper helper = mDownloadHelperMap.get(downloadId);
        if(helper != null){
            pause(downloadId);
            new File(helper.mLocalPath).delete();
            mInstance.removeDownloadMusicSave(helper.mLocalPath);
            mDownloadHelperMap.remove(helper.mDownloadId);
            mDownloadTaskMap.remove(helper.mDownloadId);
            mDownloadId.remove((Integer)helper.mDownloadId);
        }
        removeDownloadInfoFromFailOrPauseDatabase(downloadId);
    }

}
