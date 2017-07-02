package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.itempager.MyGedanItemPager;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MyLocalGedanListRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.utils.ToastUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.util.List;

import static com.hudson.donglingmusic.ui.activity.GedanListActivity.GEDAN_LIST_INFO;
import static com.hudson.donglingmusic.ui.activity.ModifyGedanInfoActivity.MY_GEDAN_DATABASE_CHANGE;

/**
 * Created by Hudson on 2017/6/3
 * 用户创建的专辑歌单
 * 不同于音乐榜单，专辑歌单歌曲数目较小，所以不存在加载更多
 */

public class MyGedanListActivity extends Activity{
    private RecyclerView mRecyclerView;
    private List<MusicInfo> mGeDanListData;
    private MyLocalGedanListRecyclerViewAdapter mAdapter;
    private View mLoadingView;
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private SongListServiceConnection mConnection;
    private boolean mHasSetPlayList = false;
    private MySharePreferences mInstance;
    private MusicReceiver mReceiver;
    public static final String MY_GEDAN_LIST_INFO = "mygeDanList";
    private MyGedanBean mGedanBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        bind();
        Intent intent = getIntent();
        mInstance = MySharePreferences.getInstance();
        if(intent!=null){
            initView();
            mGedanBean = new MyGedanBean();
            mGedanBean.setDesc(intent.getStringExtra("desc"));
            mGedanBean.setTitle(intent.getStringExtra("title"));
            mGedanBean.setTag(intent.getStringExtra("tag"));
            mGedanBean.setImagePath(intent.getStringExtra("imagePath"));
            mLoadingView.setVisibility(View.VISIBLE);
            //加载数据
            initData(intent.getStringExtra("databaseName"));
        }else{//没有数据，直接销毁
            finish();
        }
        mReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter(MusicService.PLAY_LIST_CHANGE);
        filter.addAction(MY_GEDAN_DATABASE_CHANGE);
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
                MyGedanListActivity.this.finish();
            }
        });
        ((SlideRelativeLayout)findViewById(R.id.srl_song_list_root)).setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                MyGedanListActivity.this.finish();
            }
        });
        mLoadingView = findViewById(R.id.ll_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_song_list_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MyLocalGedanListRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyLocalGedanListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {//每一项的点击事件
                if(mInterface!=null){
                    try {
                        if(!mHasSetPlayList){
                            mInstance.savePlayerPlaylistInfo(MY_GEDAN_LIST_INFO);
                            mInterface.setPlayList(mGeDanListData);
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
        mAdapter.setOnItemMenuClickListener(new MyLocalGedanListRecyclerViewAdapter.OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View v, int position) {//每一项更多的点击事件
                Intent intent = new Intent(MyGedanListActivity.this, MoreInfoActivity.class);
                intent.putExtra("music_info",mGeDanListData.get(position));
                intent.putExtra("selected_index",position);
                intent.putExtra("list_info",MY_GEDAN_LIST_INFO);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        mAdapter.setOnManageGedanClickListener(new MyLocalGedanListRecyclerViewAdapter.OnManageGedanClickListener() {
            @Override
            public void onManageGedanClick() {
                if(mGedanBean!=null){
                    Intent modify = new Intent(MyGedanListActivity.this,ModifyGedanInfoActivity.class);
                    modify.putExtra("desc",mGedanBean.getDesc());
                    modify.putExtra("title",mGedanBean.getTitle());
                    modify.putExtra("tag",mGedanBean.getTag());
                    modify.putExtra("imagePath",mGedanBean.getImagePath());
                    startActivity(modify);
                }
            }
        });
    }

    /**
     * 从数据库读取列表
     * @param title
     */
    private void initData(final String title) {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                LitePalDB litePalDB = new LitePalDB(MyGedanItemPager.getMyGedanDatabaseName(title), 1);
                litePalDB.addClassName(MusicInfo.class.getName());
                LitePal.use(litePalDB);
                mGeDanListData = DataSupport.findAll(MusicInfo.class);
                mGedanBean.setSongCount(mGeDanListData.size());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(mGeDanListData!=null){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter.setData(mGedanBean,mGeDanListData);
                }
                mLoadingView.setVisibility(View.GONE);
            }
        }.execute();
    }


    class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MY_GEDAN_DATABASE_CHANGE)){
                mGedanBean.setTitle(intent.getStringExtra("title"));
                mGedanBean.setTag(intent.getStringExtra("tag"));
                mGedanBean.setDesc(intent.getStringExtra("desc"));
                mGedanBean.setImagePath(intent.getStringExtra("imagePath"));
                mAdapter.setHeaderBean(mGedanBean);
            }else{
                if(!mInstance.getPlayerPlaylistInfo().equals(GEDAN_LIST_INFO)){
                    mHasSetPlayList = false;
                }
            }
        }
    }

}
