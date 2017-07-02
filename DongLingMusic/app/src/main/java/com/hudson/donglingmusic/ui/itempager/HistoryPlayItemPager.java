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
import com.hudson.donglingmusic.db.HistoryMusicDao;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.activity.MoreInfoActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.LocalItemRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hudson.donglingmusic.ui.fragment.PlayListFragment.PLAY_LIST_ITEM_DELETE_BROADCAST;
import static com.hudson.donglingmusic.utils.DialogUtils.DELETE_SONG_BROADCAST;

/**
 * Created by Hudson on 2017/3/21.
 * 历史记录播放
 */

public class HistoryPlayItemPager extends BaseItemPager implements LocalItemRecyclerViewAdapter.OnItemClickListener, LocalItemRecyclerViewAdapter.OnItemMenuClickListener {
    private List<MusicInfo> mHistoryList;
    private LocalItemRecyclerViewAdapter mAdapter;
    private IDonglingMusicAidlInterface mInterface;
    private NewMusicReceiver mReceiver;
    private MySharePreferences mInstance;
    private boolean mHasSetPlayList = false;
    public static final String HISTORY_LIST_INFO = "history_list";

    public HistoryPlayItemPager(Activity activity) {
        super(activity);
        //记得设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mReceiver = new NewMusicReceiver();
        IntentFilter filter = new IntentFilter(MusicService.UPDATE_HISTORY_DATABASE_COMPLETE);
        filter.addAction(MusicService.ACTION_STOP);
        filter.addAction(MusicService.PLAY_LIST_CHANGE);
        filter.addAction(MusicService.NEXT_PLAY_LIST_SET_BROADCAST);
        filter.addAction(DELETE_SONG_BROADCAST);
        filter.addAction(PLAY_LIST_ITEM_DELETE_BROADCAST);
        mActivity.registerReceiver(mReceiver, filter);
        mInstance = MySharePreferences.getInstance();
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try{
                    ArrayList<MusicInfo> historyList = HistoryMusicDao.getInstance(mActivity).queryAll();
                    mHistoryList = new ArrayList<>();
                    //倒序
                    int size = historyList.size();
                    for (int i = size - 1; i >= 0; i--) {
                        mHistoryList.add(historyList.get(i));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mLoadingView.setVisibility(View.INVISIBLE);
                if(mHistoryList==null|(mHistoryList!=null&&mHistoryList.size() == 0)){
                    mEmptyView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyView.setVisibility(View.INVISIBLE);
                    if(mAdapter!=null){
                        mAdapter.setMusicInfos(mHistoryList);
                    }else{
                        mAdapter = new LocalItemRecyclerViewAdapter(mActivity, mHistoryList);
                        mAdapter.setOnItemClickListener(HistoryPlayItemPager.this);
                        mAdapter.setOnItemMenuClickListener(HistoryPlayItemPager.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
                if(mInterface == null){
                    mInterface = ((HomeActivity)mActivity).getInterface();
                }
                try {
                    if(mInterface.getPlayState()!=MusicService.STATE_IDLE){
                        checkContainsCurPlaySong(mInterface.getCurMusicInfo());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }

        }.execute();
    }

    private void checkContainsCurPlaySong(MusicInfo curMusicInfo){
        if(curMusicInfo == null|mHistoryList == null|mHistoryList.size() == 0){
            return ;
        }
        MusicInfo info;
        for (int i = 0; i < mHistoryList.size(); i++) {
            info = mHistoryList.get(i);
            if(info.equals(curMusicInfo)){
                mAdapter.setPlayingId(0);//一定是第一个
                return ;
            }
        }
    }

    @Override
    public void reLoadData() {
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mReceiver);
        mInterface = null;
        if(mHistoryList !=null){
            mHistoryList.removeAll(mHistoryList);
            mHistoryList = null;
        }
    }


    //一个歌曲项点击事件
    @Override
    public void onItemClick(View v, int position) {
        try {
            if(!mHasSetPlayList){//第一次点击该列表中的歌曲，我们需要设置该列表为播放列表
                mInterface.setPlayList(mHistoryList);
                mHasSetPlayList = true;
                mInstance.savePlayerPlaylistInfo(HISTORY_LIST_INFO);
                mInterface.play(position);
            }else{
                mInterface.play(getRightPositionFromLocalToPlaylist(position));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private int getRightPositionFromLocalToPlaylist(int position){
        int srcPosition = position;
        ArrayList<Integer> playListDeleteItem = mInstance.getPlayListDeleteItem();
        if(playListDeleteItem!=null){
            for (int i = 0; i < playListDeleteItem.size(); i++) {
                Integer deleteItem = playListDeleteItem.get(i);
                if(srcPosition == deleteItem){//如果就是这个列表，那么由于该歌曲被删了，所以重新设置播放列表
                    try {
                        mInterface.setPlayList(mHistoryList);
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

    //一个歌曲项的menu点击事件
    @Override
    public void onItemMenuClick(View v, int position) {
        Intent intent = new Intent(mActivity, MoreInfoActivity.class);
        intent.putExtra("music_info",mHistoryList.get(position));
        intent.putExtra("selected_index",position);
        intent.putExtra("list_info",HISTORY_LIST_INFO);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(0,0);
    }

    class NewMusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MusicService.UPDATE_HISTORY_DATABASE_COMPLETE)){
                initData();
            }else if(action.equals(MusicService.PLAY_LIST_CHANGE)){//接收播放列表变化通知
                if(!mInstance.getPlayerPlaylistInfo().equals(HISTORY_LIST_INFO)){
                    mHasSetPlayList = false;
                }
            }else if(action.equals(MusicService.NEXT_PLAY_LIST_SET_BROADCAST)){
                if(intent.getStringExtra("listInfo").equals(HISTORY_LIST_INFO)){
                    int nextPlayIndex = intent.getIntExtra("nextPlayIndex",-1);
                    if(nextPlayIndex!=-1){
                        try {
                            mInterface.setPlayList(mHistoryList);
                            mHasSetPlayList = true;
                            mInstance.savePlayerPlaylistInfo(HISTORY_LIST_INFO);
                            mInterface.play(nextPlayIndex);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(action.equals(DELETE_SONG_BROADCAST)){
                String listInfo = intent.getStringExtra("listInfo");
                if(listInfo!=null&&listInfo.equals(HISTORY_LIST_INFO)){
                    boolean deleteFileOrNot = intent.getBooleanExtra("deleteFileOrNot", false);
                    MusicInfo musicInfo = intent.getParcelableExtra("musicInfo");
                    if(deleteFileOrNot){
                        try{
                            String data = musicInfo.getData();
                            if(!data.startsWith("http")){
                                if(!new File(data).delete()){
                                    ToastUtils.showToast("删除文件失败！");
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            ToastUtils.showToast("抱歉，删除文件时出现异常！");
                        }
                    }
                    mHistoryList.remove(musicInfo);
                    mAdapter.notifyDataSetChanged();
                    HistoryMusicDao.getInstance(mActivity).deleteItem(musicInfo);
                }
            }else if(action.equals(PLAY_LIST_ITEM_DELETE_BROADCAST)&&mHasSetPlayList){
                MusicInfo deleteItem = intent.getParcelableExtra("itemMusic");
                MusicInfo info;
                for (int i = 0; i < mHistoryList.size(); i++) {
                    info = mHistoryList.get(i);
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
            }else{//stop
                if(mInterface!=null)
                    mAdapter.setPlayingId(-1);
            }
        }
    }

}
