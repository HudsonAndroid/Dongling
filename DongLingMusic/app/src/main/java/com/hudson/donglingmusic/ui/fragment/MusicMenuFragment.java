package com.hudson.donglingmusic.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MusicMenuItemBean;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.bean.NetMusicDownloadInfoBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.net.download.DownloadHelper;
import com.hudson.donglingmusic.net.download.MusicDownloadManager;
import com.hudson.donglingmusic.ui.activity.MoreInfoActivity;
import com.hudson.donglingmusic.ui.itempager.DownloadCompletedItemPager;
import com.hudson.donglingmusic.ui.itempager.HistoryPlayItemPager;
import com.hudson.donglingmusic.ui.itempager.LocalItemPager;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MusicMenuRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.MusicUtils;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hudson.donglingmusic.ui.activity.ModifyGedanInfoActivity.MY_GEDAN_DATABASE_CHANGE;
import static com.hudson.donglingmusic.ui.activity.PlayPageActivity.DOWNLOAD_TASK_START;
import static com.hudson.donglingmusic.ui.itempager.MyGedanItemPager.MY_GEDAN_DATABASE;
import static com.hudson.donglingmusic.ui.itempager.MyGedanItemPager.getMyGedanDatabaseName;

/**
 * Created by Hudson on 2017/4/4.
 * 歌曲更多的页面（信息详情页）
 */

public class MusicMenuFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private TextView mTitle;
    private String mSelectedListInfo;//所属列表的信息
    private MoreInfoActivity mInfoActivity;
    private MusicInfo mSelectedMusic;
    public static int REQUEST_CODE_WRITE_SETTINGS = 0;
    private MySharePreferences mInstance;
    public static final String NO_CHANGE_PLAY_LIST = "com.huson.donglingmusic.nochangeplaylist";

    @Override
    public View initView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.fragment_musicmenu_layout,null);
        mInstance = MySharePreferences.getInstance();
        mTitle = (TextView) v.findViewById(R.id.tv_title);//标题
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_musicmenu);
        mInfoActivity = (MoreInfoActivity)mActivity;
        mSelectedMusic = mInfoActivity.mSelectedMusic;
        mSelectedListInfo = mInfoActivity.mSelectedMusicListInfo;
        mTitle.setText(mSelectedMusic.getTitle());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mInfoActivity, LinearLayoutManager.VERTICAL, false));
        ((MoreInfoActivity)(mActivity)).setListener(new MoreInfoActivity.PermissionGrantedOrNotListener() {
            @Override
            public void onPermissionAllowOrNot() {//权限允许
                DialogUtils.showSetRingDialog(mActivity,mSelectedMusic);
            }
        });
        return v;
    }

    @Override
    public void initData() {
        ArrayList<MusicMenuItemBean> data = new ArrayList<>();
        MusicMenuItemBean bean = new MusicMenuItemBean(R.drawable.music_menu_nextplay,getString(R.string.music_menu_as_next_play));
        data.add(bean);
        bean = new MusicMenuItemBean(R.drawable.music_menu_add,getString(R.string.music_menu_add));
        data.add(bean);
        final String musicPath = mSelectedMusic.getData();
        final String musicTitle = mSelectedMusic.getTitle();
        if(musicPath.startsWith("http")){
            bean = new MusicMenuItemBean(R.drawable.music_menu_download,getString(R.string.download));
            data.add(bean);
        }else{
            bean = new MusicMenuItemBean(R.drawable.music_menu_delete,getString(R.string.music_menu_delete));
            data.add(bean);
        }
        bean = new MusicMenuItemBean(R.drawable.music_menu_info,getString(R.string.music_menu_scan_info));
        data.add(bean);
        if(!musicPath.startsWith("http")){
            bean = new MusicMenuItemBean(R.drawable.music_menu_setring,getString(R.string.music_menu_set_ring));
            data.add(bean);
        }
        MusicMenuRecyclerViewAdapter adapter = new MusicMenuRecyclerViewAdapter(mActivity,data);
        adapter.setOnItemClickListener(new MusicMenuRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position){
                    case 0://下一曲
                        //如果是那些容易被销毁的列表中的，那么我们需要将列表保存
                        if(mSelectedListInfo.equals(LocalItemPager.LOCAL_LIST_INFO)|
                                mSelectedListInfo.equals(DownloadCompletedItemPager.DOWNLOAD_LIST)|
                                mSelectedListInfo.equals(HistoryPlayItemPager.HISTORY_LIST_INFO)){
                            mInstance.saveNextPlayChangeList(true);
                        }else{
                            mInstance.saveNextPlayChangeList(false);
                            Intent intent = new Intent(NO_CHANGE_PLAY_LIST);
                            intent.putExtra("nextMusic",mSelectedMusic);
                            mActivity.sendBroadcast(intent);
                        }
                        mInstance.saveNextPlayIndex(mInfoActivity.mSelectedIndex);
                        mInstance.saveNextPlayListInfo(mSelectedListInfo);
                        ToastUtils.showToast("已经作为下一曲准备播放！");
                        mInfoActivity.startTranOutAnimator();
                        break;
                    case 1://添加到歌单
                        DialogUtils.showGedanChooseDialog(mInfoActivity, mSelectedMusic, new Runnable() {
                            @Override
                            public void run() {
                                //添加到新建歌单
                                DialogUtils.showEditInfoDialog(mActivity,DialogUtils.TYPE_MODIFY_GEDAN_TITLE, UIUtils.getString(R.string.gedan_title_should_not_empty),"新建歌单",null,"输入歌单名", new Runnable() {
                                    @Override
                                    public void run() {
                                        new AsyncTask<Void,Void,Boolean>(){

                                            @Override
                                            protected Boolean doInBackground(Void... params) {
                                                List<MyGedanBean> userGedans = new ArrayList<>();
                                                LitePalDB litePalDB = new LitePalDB(MY_GEDAN_DATABASE, 1);
                                                litePalDB.addClassName(MyGedanBean.class.getName());
                                                LitePal.use(litePalDB);
                                                List<MyGedanBean> myAllGedanBeen = DataSupport.findAll(MyGedanBean.class);
                                                if(myAllGedanBeen!=null&&myAllGedanBeen.size()!=0){
                                                    for (MyGedanBean myGedanBean : myAllGedanBeen) {
                                                        if(myGedanBean.getNetGedanId() == null){
                                                            userGedans.add(myGedanBean);
                                                        }
                                                    }
                                                }
                                                String newGedanTitle = mInstance.getNewGedanTitle();
                                                MyGedanBean gedanBean;
                                                for (int i = 0; i < userGedans.size(); i++) {
                                                    gedanBean = userGedans.get(i);
                                                    if(gedanBean.getTitle().equals(newGedanTitle)){//歌单已经存在
                                                        return true;
                                                    }
                                                }
                                                MyGedanBean mNewGedan = new MyGedanBean();
                                                mNewGedan.setTitle(newGedanTitle);
                                                mNewGedan.setDatabaseName(newGedanTitle);
                                                mNewGedan.save();
                                                //创建新创建歌单的数据库
                                                litePalDB = new LitePalDB(getMyGedanDatabaseName(newGedanTitle), 1);
                                                litePalDB.addClassName(MusicInfo.class.getName());
                                                LitePal.use(litePalDB);
                                                return false;
                                            }

                                            @Override
                                            protected void onPostExecute(Boolean aVoid) {
                                                super.onPostExecute(aVoid);
                                                if(aVoid){//说明歌单存在
                                                    ToastUtils.showToast("创建失败，歌单已经存在！");
                                                }else{
                                                    if(mSelectedMusic.save()){//前面已经切换到这个歌单的数据库了
                                                        ToastUtils.showToast("歌单创建成功，且成功添加歌曲！");
                                                    }else{
                                                        ToastUtils.showToast("歌单创建成功，但是歌曲添加失败,请稍后重新添加！");
                                                    }
                                                    mActivity.sendBroadcast(new Intent(MY_GEDAN_DATABASE_CHANGE));
                                                }
                                            }
                                        }.execute();

                                    }
                                });
                            }
                        },"添加成功！");
                        break;
                    case 2://下载或者删除歌曲
                        if(musicPath.startsWith("http")){
                            if(new File(DownloadHelper.getLocalPathByMusicInfoTitle(musicTitle)).exists()){
                                DialogUtils.showInformationDialog(mActivity, "文件已存在", musicTitle + " 已经下载过了，是否重新下载？", "重新下载", "取消下载", null, new Runnable() {
                                    @Override
                                    public void run() {
                                        MusicDownloadManager.getInstance().startDownload(mSelectedMusic);
                                        mActivity.sendBroadcast(new Intent(DOWNLOAD_TASK_START));
                                        ToastUtils.showToast("已加入到下载队列！");
                                    }
                                });
                            }else{
                                processSongImageLyricsDownloadPathAndDownload(mSelectedMusic, BMA.Song.songInfo(String.valueOf(mSelectedMusic.getSongId())),mSelectedMusic.getTitle());
                            }
                        }else{
                            DialogUtils.showDeleteSongDialog(mActivity, "删除提示", "确定从列表中删除 " + musicTitle + " 歌曲？", "删除", "取消",mSelectedMusic,mSelectedListInfo);
                        }
                        break;
                    case 3://查看歌曲信息
                        DialogUtils.showMusicInformationDialog(mActivity, musicTitle,
                                MusicUtils.cutName(mSelectedMusic),mSelectedMusic.getArtist(),
                                mSelectedMusic.getAlbum(), musicPath);
                        break;
                    case 4://设置为铃声
                        if(Build.VERSION.SDK_INT>=23){
                            if(Settings.System.canWrite(mActivity)){
                                DialogUtils.showSetRingDialog(mActivity,mSelectedMusic);
                            }else{
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS );
                            }
                        }else{
                            DialogUtils.showSetRingDialog(mActivity,mSelectedMusic);
                        }
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }



    /**
     * 网络音乐
     * 解析歌曲的下载地址、歌曲背景下载链接、歌曲的歌词下载链接，并下载
     * @param url
     */
    private void processSongImageLyricsDownloadPathAndDownload(final MusicInfo netMusic, final String url, final String title) {
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        processBillBoardData(netMusic,responseInfo.result,title);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ToastUtils.showToast("歌曲解析失败，请检查网络是否可用！");
                    }
                });
    }


    /**
     * 解析榜单数据
     */
    private void processBillBoardData(MusicInfo netMusic,String jsonResult,String title) {
        Gson gson = new Gson();
        try{
            NetMusicDownloadInfoBean bean = gson.fromJson(jsonResult,NetMusicDownloadInfoBean.class);
            if(bean!=null){
                NetMusicDownloadInfoBean.SonginfoBean songinfo = bean.getSonginfo();
                netMusic.setAlbum(songinfo.getAlbum_title());
                netMusic.setAlbumId(Integer.valueOf(songinfo.getAlbum_id()));
                HttpUtils httpUtils = XUtilsManager.getHttpUtilsInstance();
                String imagePath = new StringBuilder(StorageUtils.
                        getAppMusicPicAbsolutePath()).append(title).append(".jpg").toString();
                if(!new File(imagePath).exists()){
                    //500*500图片大小
                    String imageUrl = songinfo.getPic_premium();
                    if(TextUtils.isEmpty(imageUrl)){//如果这类图片没有
                        imageUrl = songinfo.getPic_big();
                    }
                    httpUtils.download(imageUrl, imagePath, false, false, new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            System.out.println("歌曲图片下载成功");
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("歌曲图片下载失败");
                        }
                    });
                }
                //歌词下载路径
                String lrcPath = new StringBuilder(StorageUtils.
                        getAppLyricsAbsolutePath()).append(title).append(".lrc").toString();
                if(!new File(lrcPath).exists())
                    httpUtils.download(songinfo.getLrclink(), lrcPath, false, false, new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            System.out.println("歌曲歌词下载成功");
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("歌曲歌词下载失败");
                        }
                    });
                //歌曲下载路径
                List<NetMusicDownloadInfoBean.SongurlBean.UrlBean> urlBeanList = bean.getSongurl().getUrl();
                if(urlBeanList!=null&&urlBeanList.size()!=0){
                    for(NetMusicDownloadInfoBean.SongurlBean.UrlBean urlBean:urlBeanList){
                        if(urlBean.getFile_bitrate() == mInstance.getDownloadMusicType()){//标准
                            String file_link = urlBean.getFile_link();
                            if(!TextUtils.isEmpty(file_link)){
                                netMusic.setData(file_link);
                                MusicDownloadManager.getInstance().startDownload(netMusic);
                                mActivity.sendBroadcast(new Intent(DOWNLOAD_TASK_START));
                                ToastUtils.showToast("已加入到下载队列！");
                            }
                        }
                    }
                    if(netMusic.getData().equals("http??")){//能走到这里，说明没有播放，随便选一个播放
                        for(NetMusicDownloadInfoBean.SongurlBean.UrlBean urlBean:urlBeanList){
                            String file_link = urlBean.getFile_link();
                            if(!TextUtils.isEmpty(file_link)){
                                netMusic.setData(file_link);
                                MusicDownloadManager.getInstance().startDownload(netMusic);
                                mActivity.sendBroadcast(new Intent(DOWNLOAD_TASK_START));
                                ToastUtils.showToast("已加入到下载队列！");
                            }
                        }
                    }
                }
            }else{
                ToastUtils.showToast("加载失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
        }
    }

}
