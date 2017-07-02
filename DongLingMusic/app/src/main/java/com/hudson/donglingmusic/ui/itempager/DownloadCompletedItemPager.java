package com.hudson.donglingmusic.ui.itempager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.activity.MoreInfoActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.DownloadCompleteItemRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.ToastUtils;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hudson.donglingmusic.ui.fragment.PlayListFragment.PLAY_LIST_ITEM_DELETE_BROADCAST;
import static com.hudson.donglingmusic.utils.DialogUtils.DELETE_SONG_BROADCAST;


/**
 * Created by Hudson on 2017/4/30.
 */

public class DownloadCompletedItemPager extends BaseItemPager {
    private List<MusicInfo> mDownloadCompleteInfos;
    private DownloadCompleteItemRecyclerViewAdapter mCompleteItemRecyclerViewAdapter;
    private IDonglingMusicAidlInterface mInterface;
    public static final String DOWNLOAD_LIST = "downloadlist";
    private MusicReceiver mReceiver;
    private MySharePreferences mInstance;
    private boolean mHasSetPlayList = false;

    public DownloadCompletedItemPager(Activity activity) {
        super(activity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mInterface = ((HomeActivity)mActivity).getInterface();
        mLoadingView.setVisibility(View.VISIBLE);
        mInstance = MySharePreferences.getInstance();
        mCompleteItemRecyclerViewAdapter = new DownloadCompleteItemRecyclerViewAdapter(mActivity);
        mRecyclerView.setAdapter(mCompleteItemRecyclerViewAdapter);
        mCompleteItemRecyclerViewAdapter.setOnItemClickListener(new DownloadCompleteItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(mInterface!=null){
                    try {
                        if(!mHasSetPlayList){
                            mInstance.savePlayerPlaylistInfo(DOWNLOAD_LIST);
                            mInterface.setPlayList(mDownloadCompleteInfos);
                            mHasSetPlayList = true;
                            mInterface.play(position);
                        }else{
                            mInterface.play(getRightPositionFromLocalToPlaylist(position));
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else{
                    ToastUtils.showToast("数据正在初始化，请稍后重试！");
                }
            }
        });
        mCompleteItemRecyclerViewAdapter.setOnItemMenuClickListener(new DownloadCompleteItemRecyclerViewAdapter.OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View v, int position) {
                Intent intent = new Intent(mActivity, MoreInfoActivity.class);
                intent.putExtra("music_info",mDownloadCompleteInfos.get(position));
                intent.putExtra("selected_index",position);
                intent.putExtra("list_info",DOWNLOAD_LIST);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(0,0);
            }
        });
        mReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter(MusicService.PLAY_LIST_CHANGE);
        filter.addAction(MusicService.NEXT_PLAY_LIST_SET_BROADCAST);
        filter.addAction(DELETE_SONG_BROADCAST);
        filter.addAction(PLAY_LIST_ITEM_DELETE_BROADCAST);
        mActivity.registerReceiver(mReceiver,filter);
    }

    private int getRightPositionFromLocalToPlaylist(int position){
        int srcPosition = position;
        ArrayList<Integer> playListDeleteItem = mInstance.getPlayListDeleteItem();
        if(playListDeleteItem!=null){
            for (int i = 0; i < playListDeleteItem.size(); i++) {
                Integer deleteItem = playListDeleteItem.get(i);
                if(srcPosition == deleteItem){//如果就是这个列表，那么由于该歌曲被删了，所以重新设置播放列表
                    try {
                        mInterface.setPlayList(mDownloadCompleteInfos);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return srcPosition;
                }
                if(position> deleteItem){
                    position --;
                }
            }
        }
        return position;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mReceiver);
    }

    @Override
    public void initData() {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                //查询下载完成数据库
                LitePal.useDefault();
                List<MusicInfo> downloadCompleteInfos = DataSupport.findAll(MusicInfo.class);
                int size = downloadCompleteInfos.size();
                mDownloadCompleteInfos = new ArrayList<>();
                for (int i = size-1; i >=0; i--) {
                    mDownloadCompleteInfos.add(downloadCompleteInfos.get(i));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(mDownloadCompleteInfos.size() == 0){
                    mEmptyView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyView.setVisibility(View.INVISIBLE);
                }
                mCompleteItemRecyclerViewAdapter.setMusicInfos(mDownloadCompleteInfos);
                mLoadingView.setVisibility(View.INVISIBLE);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void reLoadData() {
        initData();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MusicService.NEXT_PLAY_LIST_SET_BROADCAST)){
                if(intent.getStringExtra("listInfo").equals(DOWNLOAD_LIST)){
                    int nextPlayIndex = intent.getIntExtra("nextPlayIndex",-1);
                    if(nextPlayIndex!=-1){
                        try {
                            mInterface.setPlayList(mDownloadCompleteInfos);
                            mHasSetPlayList = true;
                            mInstance.savePlayerPlaylistInfo(DOWNLOAD_LIST);
                            mInterface.play(nextPlayIndex);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(action.equals(DELETE_SONG_BROADCAST)){
                String listInfo = intent.getStringExtra("listInfo");
                if(listInfo!=null&&listInfo.equals(DOWNLOAD_LIST)){
                    boolean deleteFileOrNot = intent.getBooleanExtra("deleteFileOrNot", false);
                    MusicInfo musicInfo = intent.getParcelableExtra("musicInfo");
                    if(deleteFileOrNot){
                        try{
                            if(!new File(musicInfo.getData()).delete()){
                                ToastUtils.showToast("删除文件失败！");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            ToastUtils.showToast("抱歉，删除文件时出现异常！");
                        }
                    }
                    mDownloadCompleteInfos.remove(musicInfo);
                    mCompleteItemRecyclerViewAdapter.notifyDataSetChanged();
                    LitePal.useDefault();
                    DataSupport.deleteAll(MusicInfo.class, "msongId = ? ", musicInfo.getSongId()+"");
                }
            }else if(action.equals(PLAY_LIST_ITEM_DELETE_BROADCAST)&&mHasSetPlayList){
                MusicInfo deleteItem = intent.getParcelableExtra("itemMusic");
                MusicInfo info;
                for (int i = 0; i < mDownloadCompleteInfos.size(); i++) {
                    info = mDownloadCompleteInfos.get(i);
                    if(info.equals(deleteItem)){
                        ArrayList<Integer> playListDeleteItem = mInstance.getPlayListDeleteItem();
                        if(playListDeleteItem == null){
                            playListDeleteItem = new ArrayList<>();
                        }
                        playListDeleteItem.add(i);
                        mInstance.savePlayListDeleteItem(playListDeleteItem);
                        break;
                    }
                }
            }else{
                if(!mInstance.getPlayerPlaylistInfo().equals(DOWNLOAD_LIST)){
                    mHasSetPlayList = false;
                }
            }
        }
    }
}
