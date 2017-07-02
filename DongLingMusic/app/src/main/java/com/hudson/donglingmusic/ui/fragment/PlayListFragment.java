package com.hudson.donglingmusic.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.activity.MoreInfoActivity;
import com.hudson.donglingmusic.ui.itempager.DownloadCompletedItemPager;
import com.hudson.donglingmusic.ui.itempager.HistoryPlayItemPager;
import com.hudson.donglingmusic.ui.itempager.LocalItemPager;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.PlayListRecyclerViewAdapter;

import java.util.List;

/**
 * Created by Hudson on 2017/4/4.
 * 播放列表页面
 * 注意：通过远程服务获取的list与获取处的List居然是相互关联的，也就是说，任何一处修改都会引起另一处的变化
 */

public class PlayListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private TextView mTitle;
    private MoreInfoActivity mInfoActivity;
    private IDonglingMusicAidlInterface mInterface;
    private List<MusicInfo> mPlayList;
    private PlayListRecyclerViewAdapter mAdapter;
    private MySharePreferences mInstance;
    private MusicChangeReceiver mReceiver;
    public static final String PLAY_LIST_ITEM_DELETE_BROADCAST = "com.hudson.donglingmusic.play_list_delete_item";

    @Override
    public View initView(final LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.fragment_musicmenu_layout,null);
        mInstance = MySharePreferences.getInstance();
        mTitle = (TextView) v.findViewById(R.id.tv_title);//标题
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_musicmenu);
        mInfoActivity = (MoreInfoActivity)mActivity;
        mReceiver = new MusicChangeReceiver();
        IntentFilter filter = new IntentFilter(MusicService.ACTION_NEW_MUSIC);
        mActivity.registerReceiver(mReceiver,filter);
        mInfoActivity.setOnServiceConnectedListener(new MoreInfoActivity.OnServiceConnectedListener() {
            @Override
            public void onServiceConnected(IDonglingMusicAidlInterface face) {
                mInterface = face;
                initData();
            }
        });
        mTitle.setText("播放队列");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mInfoActivity, LinearLayoutManager.VERTICAL, false));
        mAdapter = new PlayListRecyclerViewAdapter(mActivity);
        mAdapter.setOnItemClickListener(new PlayListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                try {
                    mInterface.play(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mAdapter.setOnDeleteItemClickListener(new PlayListRecyclerViewAdapter.OnDeleteItemClickListener() {
            @Override
            public void onDeleteItemClick(int position) {
                try {
                    //删除歌曲时并没有这么简单，如果删除的歌曲是本地、下载完成、历史记录这三块的
                    //我们需要特殊处理。原因在于，我们的playlist与这三个列表是相互存在关系的，
                    //在播放列表是这三个之一（并不是指是引用，非引用情况），我们在这三个列表之一
                    //页面点击item，那么会回调播放器的play(position)方法，但是我们这边又把播放
                    //列表item删除，所以会导致两个列表并不是一一对应的情况，从而导致play(position）
                    //播放的结果与我们在页面点击的item结果不一致
                    /*
                     * 解决办法：我们根据当前播放的列表的性质，判断如果是上述三个之一，我们记录删除的item
                     * 的position，然后在上述三个之一（正在播放的那个）,position做出相应的改变即可
                     */
                    String curPlayListInfo = mInstance.getPlayerPlaylistInfo();
                    if(curPlayListInfo.equals(LocalItemPager.LOCAL_LIST_INFO)|
                            curPlayListInfo.equals(DownloadCompletedItemPager.DOWNLOAD_LIST)|
                            curPlayListInfo.equals(HistoryPlayItemPager.HISTORY_LIST_INFO)){
                        Intent intent = new Intent(PLAY_LIST_ITEM_DELETE_BROADCAST);
                        intent.putExtra("itemMusic",mPlayList.get(position));//删除的歌曲
                        mActivity.sendBroadcast(intent);
                    }
                    mInterface.removeSongFromPlayList(position);
                    mAdapter.setPlayingPosition(mInterface.getCurMusicInfoIndex());
//                    mPlayList.remove(position);//这两个列表是相互关联的
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }


    @Override
    public void initData() {
        mInterface = mInfoActivity.getInterface();
        if(mInterface!=null){
            try {
                mPlayList = mInterface.getPlayList();
                mAdapter.setMusicMenuDatas(mPlayList);
                int curMusicInfoIndex = mInterface.getCurMusicInfoIndex();
                mAdapter.setPlayingPosition(curMusicInfoIndex);
                mRecyclerView.scrollToPosition(curMusicInfoIndex);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mReceiver);
    }

    class MusicChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mAdapter.setPlayingPosition(mInterface.getCurMusicInfoIndex());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
