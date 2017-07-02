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
import android.widget.TextView;

import com.google.gson.Gson;
import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.ArtistListBean;
import com.hudson.donglingmusic.bean.ArtistListHeaderBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.ArtistListRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SongListBottomViewHolder;
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
 * Created by Hudson on 2017/4/21.
 * 歌手列表歌单
 *
 */

public class ArtistListActivity extends Activity{
    private RecyclerView mRecyclerView;
    private ArrayList<MusicInfo> mArtistSongList;
    private ArtistListRecyclerViewAdapter mArtistListRecyclerViewAdapter;
    private View mLoadingView;
    private RecyclerView.OnScrollListener mScrollListener;
    private LinearLayoutManager mLayoutManager;
    private int mLoadStartIndex = 0;
    private String mArtistId,mTingUid;
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private ArtistListMusicConnect mConnection;
    private boolean mHasSetPlayList = false;
    private boolean mIsLoadingMore = false;//是否正在加载更多
    private MySharePreferences mInstance;
    private MusicReceiver mReceiver;
    public static final String ARTIST_LIST = "artistlist";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        bind();
        Intent intent = getIntent();
        mInstance = MySharePreferences.getInstance();
        ((TextView)findViewById(R.id.tv_activity_info)).setText("歌手详情");
        if(intent!=null){
            initView();
            mArtistId = intent.getStringExtra("artistId");
            mTingUid = intent.getStringExtra("tingUid");
            //每次加载20条数据
            //这里设置了每次获取三个,参数一：歌单index,参数二：加载起始值，参数三：加载数目
            initData(BMA.Artist.artistInfo(mTingUid, mArtistId),BMA.Artist.artistSongList(mTingUid,mArtistId,0,20));
        }else{//没有数据，直接销毁
            finish();
        }
        mReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter(MusicService.PLAY_LIST_CHANGE);
        registerReceiver(mReceiver,filter);
    }

    private void bind(){
        mConnection = new ArtistListMusicConnect();
        bindService(new Intent(this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }

    class ArtistListMusicConnect implements ServiceConnection {

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
        mRecyclerView.removeOnScrollListener(mScrollListener);
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistListActivity.this.finish();
            }
        });
        ((SlideRelativeLayout)findViewById(R.id.srl_song_list_root)).setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                ArtistListActivity.this.finish();
            }
        });
        mLoadingView = findViewById(R.id.ll_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_song_list_container);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mArtistListRecyclerViewAdapter = new ArtistListRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mArtistListRecyclerViewAdapter);
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mArtistSongList !=null){
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        //注意：getChildCount是获取屏幕上可以看到的控件个数，getItemCount获取的是所有个数
                        if(mHasMore&&!mIsLoadingMore&&mLayoutManager.findLastVisibleItemPosition() == mLayoutManager.getItemCount()-1){
                            //已经滑到底部了
                            mArtistListRecyclerViewAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_LOADING);
                            mLoadStartIndex = mArtistSongList.size();
                            mIsLoadingMore = true;
                            startGetArtistSongListData(BMA.Artist.artistSongList(mTingUid,mArtistId,mLoadStartIndex,20),true);
                        }
                    }
                }
            }
        };
        mArtistListRecyclerViewAdapter.setOnItemClickListener(new ArtistListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {//每一项的点击事件
                if(mInterface!=null){
                    try {
                        if(!mHasSetPlayList){
                            mInstance.savePlayerPlaylistInfo(ARTIST_LIST);
                            mInterface.setPlayList(mArtistSongList);
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
        mArtistListRecyclerViewAdapter.setOnItemMenuClickListener(new ArtistListRecyclerViewAdapter.OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View v, int position) {//每一项更多的点击事件
                Intent intent = new Intent(ArtistListActivity.this, MoreInfoActivity.class);
                intent.putExtra("music_info",mArtistSongList.get(position));
                intent.putExtra("selected_index",position);
                intent.putExtra("list_info",ARTIST_LIST);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    private void initData(String artistInfoUrl,String artistSongListUrl) {
        if(NetManagerUtils.isNetworkAvailable(this)){
            PermissionUtils.requestPermission(this, Manifest.permission.INTERNET,2,null);
            PermissionUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,2,null);
            startGetArtistInfoData(artistInfoUrl);
            startGetArtistSongListData(artistSongListUrl,false);
        }else{
            ToastUtils.showToast("当前网络不可用！");
        }
    }

    /**
     * 获取歌手信息
     * @param url
     */
    private void startGetArtistInfoData(String url){
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        processArtistInfoData(responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ToastUtils.showToast("数据加载失败，请检查网络是否可用！");
                    }
                });
    }

    /**
     * 解析歌手信息
     * @param result
     */
    private void processArtistInfoData(String result){
        Gson gson = new Gson();
        try{
            ArtistListHeaderBean headerBean = gson.fromJson(result,ArtistListHeaderBean.class);
            if(headerBean!=null){
                mArtistListRecyclerViewAdapter.setHeaderBean(headerBean);
                mLoadingView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
            //这里的错误一般由需要网络验证的网络导致
            System.out.println("json格式不正确！");
        }
    }

    /**
     * 从网络上获取数据
     * @param url
     */
    private void startGetArtistSongListData(final String url, final boolean loadMore) {
        //第三个参数的泛型表示请求返回的数据类型
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        processArtistSongListData(responseInfo.result,loadMore);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ToastUtils.showToast("数据加载失败，请检查网络是否可用！");
                    }
        });
    }

    private boolean mHasMore = false;//是否还有更多数据
    /**
     * 解析歌手的歌单数据
     * @param jsonResult
     * @param isLoadMore 是否是加载更多
     */
    private void processArtistSongListData(String jsonResult,boolean isLoadMore) {
        if(!isLoadMore)//如果是第一次，那么需要新建
            mArtistSongList = new ArrayList<>();
        Gson gson = new Gson();
        try{
            ArtistListBean bean = gson.fromJson(jsonResult,ArtistListBean.class);
            if(bean!=null){
                List<ArtistListBean.SonglistBean> songlist = bean.getSonglist();
                if(bean.getHavemore() == 1){
                    mHasMore = true;
                }else{
                    mHasMore = false;
                }
                ArtistListBean.SonglistBean item;
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
                    mArtistSongList.add(netMusicInfo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
            //这里的错误一般由需要网络验证的网络导致
            System.out.println("json格式不正确！");
        }
        if(!isLoadMore){//是第一次加载
            mArtistListRecyclerViewAdapter.setDatas(mArtistSongList);//设置加载完成的数据
            mLoadingView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }else{//是加载更多完毕
            mArtistListRecyclerViewAdapter.notifyDataSetChanged();//刷新数据
            mHasSetPlayList = false;//列表增加了
            mIsLoadingMore = false;
        }
        if(!mHasMore){
            mArtistListRecyclerViewAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_NO_MORE);
        }else{
            mArtistListRecyclerViewAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_NONE);
        }
    }


    class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!mInstance.getPlayerPlaylistInfo().equals(ARTIST_LIST)){
                mHasSetPlayList = false;
            }
        }
    }

}
