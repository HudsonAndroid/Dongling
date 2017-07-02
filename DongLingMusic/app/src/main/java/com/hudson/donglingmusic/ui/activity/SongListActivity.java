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
import com.hudson.donglingmusic.bean.BMABillBoardBean;
import com.hudson.donglingmusic.bean.SongListHeaderBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MusicListRecyclerViewAdapter;
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

/**
 * Created by Hudson on 2017/4/21.
 * 这里发现一个sharepreference的bug，对于key较长的字符串，例如url，如果只有
 * 一两个字符不同，而且字符靠后，sharepreference都会认为是同一个。因此这里不使用缓存
 *
 *
 * 这里总结一下：一个List在被多个类之间赋值的时候，只要其中一个类改变了这个list，那么
 * 另一个类中访问的list也被修改，即本质上两个是共用的
 */

public class SongListActivity extends Activity{
    private RecyclerView mRecyclerView;
    private ArrayList<MusicInfo> mNetListData;
    private MusicListRecyclerViewAdapter mMusicListRecyclerViewAdapter;
    private View mLoadingView;
    private RecyclerView.OnScrollListener mScrollListener;
    private LinearLayoutManager mLayoutManager;
    private int mLoadStartIndex = 0;
    private int mUrlIndex;
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private SongListServiceConnection mConnection;
    private boolean mHasSetPlayList = false;
    private boolean mIsLoadingMore = false;//是否正在加载更多
    private MySharePreferences mInstance;
    private MusicReceiver mReceiver;
    public static final String SONG_LIST_INFO = "songlist";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        bind();
        Intent intent = getIntent();
        mInstance = MySharePreferences.getInstance();
        if(intent!=null){
            initView();
            mUrlIndex = intent.getIntExtra("urlIndex",0);
            //每次加载20条数据
            //这里设置了每次获取三个,参数一：歌单index,参数二：加载起始值，参数三：加载数目
            initData(BMA.Billboard.billSongList(BMA.Billboard.billBoards[mUrlIndex],mLoadStartIndex,20));
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
        mRecyclerView.removeOnScrollListener(mScrollListener);
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongListActivity.this.finish();
            }
        });
        ((SlideRelativeLayout)findViewById(R.id.srl_song_list_root)).setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                SongListActivity.this.finish();
            }
        });
        mLoadingView = findViewById(R.id.ll_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_song_list_container);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMusicListRecyclerViewAdapter = new MusicListRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mMusicListRecyclerViewAdapter);
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mNetListData!=null){
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        //注意：getChildCount是获取屏幕上可以看到的控件个数，getItemCount获取的是所有个数
                        if(!mIsLoadingMore&&mLayoutManager.findLastVisibleItemPosition() == mLayoutManager.getItemCount()-1){
                            //已经滑到底部了
                            mMusicListRecyclerViewAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_LOADING);
                            mLoadStartIndex =mNetListData.size();
                            mIsLoadingMore = true;
                            startGetNetBillBoardData(BMA.Billboard.billSongList(BMA.Billboard.billBoards[mUrlIndex],mLoadStartIndex,20),true);
                        }
                    }
                }
            }
        };
        mMusicListRecyclerViewAdapter.setOnItemClickListener(new MusicListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {//每一项的点击事件
                if(mInterface!=null){
                    try {
                        if(!mHasSetPlayList){
                            mInstance.savePlayerPlaylistInfo(SONG_LIST_INFO);
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
        mMusicListRecyclerViewAdapter.setOnItemMenuClickListener(new MusicListRecyclerViewAdapter.OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View v, int position) {//每一项更多的点击事件
                Intent intent = new Intent(SongListActivity.this, MoreInfoActivity.class);
                intent.putExtra("music_info",mNetListData.get(position));
                intent.putExtra("selected_index",position);
                intent.putExtra("list_info",SONG_LIST_INFO);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        mRecyclerView.addOnScrollListener(mScrollListener);
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
                        String result = responseInfo.result;//返回的结果
                        if(!loadMore){
                            processBillBoardData(result);
                        }else{
                            System.out.println("进入解析更多");
                            processBillBoardMoreData(result);
                        }
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
        SongListHeaderBean headerBean = null;
        mNetListData = new ArrayList<>();
        Gson gson = new Gson();
        try{
            BMABillBoardBean bean = gson.fromJson(jsonResult,BMABillBoardBean.class);
            if(bean!=null){
                BMABillBoardBean.BillboardBean billboard = bean.getBillboard();
                headerBean = new SongListHeaderBean(billboard.getPic_s260(),billboard.getName(),
                        billboard.getBillboard_songnum(),billboard.getUpdate_date());
                ArrayList<BMABillBoardBean.SongListBean> list = bean.getSong_list();
                BMABillBoardBean.SongListBean item;
                MusicInfo netMusicInfo;
                for (int j = 0; j < list.size(); j++) {
                    item = list.get(j);
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
        mMusicListRecyclerViewAdapter.setData(headerBean,mNetListData);//设置加载完成的数据
        mLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 解析榜单数据，更多数据
     */
    private void processBillBoardMoreData(String jsonResult) {
//        System.out.println("解析更多数据");
        ArrayList<MusicInfo> netMusics = new ArrayList<>();
        Gson gson = new Gson();
        try{
            BMABillBoardBean bean = gson.fromJson(jsonResult,BMABillBoardBean.class);
            if(bean!=null){
                ArrayList<BMABillBoardBean.SongListBean> list = bean.getSong_list();
                BMABillBoardBean.SongListBean item;
                MusicInfo netMusicInfo;
                for (int j = 0; j < list.size(); j++) {
                    item = list.get(j);
                    netMusicInfo = new MusicInfo();
                    netMusicInfo.setDuration(0);
                    netMusicInfo.setArtist(item.getAuthor());
                    netMusicInfo.setAlbum(item.getAlbum_title());
                    netMusicInfo.setTitle(item.getTitle());
                    netMusicInfo.setAlbumId(Integer.valueOf(item.getAlbum_id()));
                    netMusicInfo.setMusicId(-1);//尚不是本地歌曲，所以没有id
                    netMusicInfo.setSongId(Integer.valueOf(item.getSong_id()));
                    netMusicInfo.setData("http??");//歌曲地址未知,但是确定是网络音乐
                    netMusics.add(netMusicInfo);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            ToastUtils.showToast("没有更多数据了！");
            mMusicListRecyclerViewAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_NO_MORE);
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showToast("加载异常，请稍后重试！");
            //这里的错误一般由需要网络验证的网络导致
            System.out.println("json格式不正确！");
        }
        mMusicListRecyclerViewAdapter.updateMusicList(netMusics);//刷新，更多数据
        mMusicListRecyclerViewAdapter.setLoadMoreStatus(SongListBottomViewHolder.STATUS_NONE);
        mHasSetPlayList = false;//列表增加了
//        mNetListData.addAll(netMusics); 在adapter中已经增加过了，这里也会跟着改变
//        System.out.println("输出列表大小"+mNetListData.size());
        mIsLoadingMore = false;
    }

    class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!mInstance.getPlayerPlaylistInfo().equals(SONG_LIST_INFO)){
                mHasSetPlayList = false;
            }
        }
    }

}
