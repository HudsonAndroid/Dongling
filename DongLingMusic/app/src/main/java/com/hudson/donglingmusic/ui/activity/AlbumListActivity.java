package com.hudson.donglingmusic.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.AlbumBean;
import com.hudson.donglingmusic.bean.AlbumListHeaderBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.AlbumListRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.utils.NetManagerUtils;
import com.hudson.donglingmusic.utils.PermissionUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/6/3
 * 专辑歌单
 * 不同于音乐榜单，专辑歌单歌曲数目较小，所以不存在加载更多
 */

public class AlbumListActivity extends Activity{
    private RecyclerView mRecyclerView;
    private ArrayList<MusicInfo> mNetListData;
    private AlbumListRecyclerViewAdapter mAlbumListRecyclerViewAdapter;
    private View mLoadingView;
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private SongListServiceConnection mConnection;
    private boolean mHasSetPlayList = false;
    private MySharePreferences mInstance;
    private MusicReceiver mReceiver;
    public static final String ALBUM_LIST_INFO = "albumlist";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        bind();
        Intent intent = getIntent();
        mInstance = MySharePreferences.getInstance();
        if(intent!=null){
            initView();
            String albumId = intent.getStringExtra("albumId");
            //加载数据
            initData(BMA.Album.albumInfo(albumId));
        }else{//没有数据，直接销毁
            finish();
        }
        mReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter(MusicService.PLAY_LIST_CHANGE);
        registerReceiver(mReceiver,filter);
    }

    private void bind(){
        mConnection = new SongListServiceConnection();
        bindService(new Intent(this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }

    class SongListServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind();
        unregisterReceiver(mReceiver);
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumListActivity.this.finish();
            }
        });
        ((SlideRelativeLayout)findViewById(R.id.srl_song_list_root)).setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                AlbumListActivity.this.finish();
            }
        });
        mLoadingView = findViewById(R.id.ll_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_song_list_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAlbumListRecyclerViewAdapter = new AlbumListRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAlbumListRecyclerViewAdapter);
        mAlbumListRecyclerViewAdapter.setOnItemClickListener(new AlbumListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {//每一项的点击事件
                if(mInterface!=null){
                    try {
                        if(!mHasSetPlayList){
                            mInstance.savePlayerPlaylistInfo(ALBUM_LIST_INFO);
                            mInterface.setPlayList(mNetListData);
                            mHasSetPlayList = true;
                        }
                        mInterface.play(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else{
                    ToastUtils.showToast("数据正在初始化，请稍后重试！");
                }
            }
        });
        mAlbumListRecyclerViewAdapter.setOnItemMenuClickListener(new AlbumListRecyclerViewAdapter.OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View v, int position) {//每一项更多的点击事件
                Intent intent = new Intent(AlbumListActivity.this, MoreInfoActivity.class);
                intent.putExtra("music_info",mNetListData.get(position));
                intent.putExtra("selected_index",position);
                intent.putExtra("list_info",ALBUM_LIST_INFO);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    private void initData(String url) {
        if(NetManagerUtils.isNetworkAvailable(this)){
            PermissionUtils.requestPermission(this, Manifest.permission.INTERNET,2,null);
            PermissionUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,2,null);
            startGetNetBillBoardData(url,false);
        }else{
            ToastUtils.showToast("当前网络不可用！");
        }
//        mRecyclerView.setAdapter(new MusicListRecyclerViewAdapter());
    }

    /**
     * 从网络上获取数据
     * @param url
     */
    private void startGetNetBillBoardData(final String url, final boolean loadMore) {
        System.out.println("开始加载数据"+url);
        //第三个参数的泛型表示请求返回的数据类型
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        processBillBoardData(responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ToastUtils.showToast("数据加载失败，请检查网络是否可用！");
                    }
        });
    }

    /**
     * 解析榜单数据
     */
    private void processBillBoardData(String jsonResult) {
        AlbumListHeaderBean headerBean = null;
        mNetListData = new ArrayList<>();
        Gson gson = new Gson();
        try{
            AlbumBean bean = gson.fromJson(jsonResult,AlbumBean.class);
            if(bean!=null){
                AlbumBean.AlbumInfoBean albumInfo = bean.getAlbumInfo();
                headerBean = new AlbumListHeaderBean(albumInfo.getAuthor(),albumInfo.getCollect_num()+"",
                        albumInfo.getComment_num()+"",albumInfo.getCountry(),albumInfo.getInfo(),albumInfo.getLanguage(),
                        albumInfo.getListen_num()+"",albumInfo.getPic_radio(),albumInfo.getPublishcompany(),
                        albumInfo.getPublishtime(),albumInfo.getShare_num()+"",albumInfo.getSongs_total(),
                        albumInfo.getStyles(),albumInfo.getTitle());
                List<AlbumBean.SonglistBean> songlist = bean.getSonglist();
                AlbumBean.SonglistBean item;
                MusicInfo netMusicInfo;
                for (int j = 0; j < songlist.size(); j++) {
                    item = songlist.get(j);
                    netMusicInfo = new MusicInfo();
                    netMusicInfo.setDuration(0);
                    netMusicInfo.setArtist(item.getAuthor());
                    netMusicInfo.setAlbum(item.getAlbum_title());
                    netMusicInfo.setTitle(item.getTitle());
                    netMusicInfo.setAlbumId(Integer.valueOf(item.getAlbum_id()));
                    netMusicInfo.setMusicId(-1);//尚不是本地歌曲，所以没有id
                    netMusicInfo.setSongId(Integer.valueOf(item.getSong_id()));
                    netMusicInfo.setData("http??");//歌曲地址未知,但是确定是网络音乐
                    mNetListData.add(netMusicInfo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
            //这里的错误一般由需要网络验证的网络导致
            System.out.println("json格式不正确！");
        }
        mAlbumListRecyclerViewAdapter.setData(headerBean,mNetListData);//设置加载完成的数据
        mLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!mInstance.getPlayerPlaylistInfo().equals(ALBUM_LIST_INFO)){
                mHasSetPlayList = false;
            }
        }
    }

}
