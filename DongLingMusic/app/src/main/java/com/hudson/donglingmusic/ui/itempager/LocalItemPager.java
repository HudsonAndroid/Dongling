package com.hudson.donglingmusic.ui.itempager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.db.MusicDao;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MyApplication;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.activity.ListSearchActivity;
import com.hudson.donglingmusic.ui.activity.MoreInfoActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.LocalItemRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.view.LetterSelectorView;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.PinYinUtil;
import com.hudson.donglingmusic.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;

import static com.hudson.donglingmusic.service.MusicService.ACTION_NEW_MUSIC;
import static com.hudson.donglingmusic.service.MusicService.NEXT_PLAY_LIST_SET_BROADCAST;
import static com.hudson.donglingmusic.service.MusicService.PLAY_BY_OPENING_LOCAL_PATH;
import static com.hudson.donglingmusic.service.MusicService.PLAY_LIST_CHANGE;
import static com.hudson.donglingmusic.ui.fragment.PlayListFragment.PLAY_LIST_ITEM_DELETE_BROADCAST;

/**
 * Created by Hudson on 2017/3/21.
 * 本地音乐
 */

public class LocalItemPager extends BaseItemPager implements LocalItemRecyclerViewAdapter.OnItemClickListener, LocalItemRecyclerViewAdapter.OnItemMenuClickListener {
    private ArrayList<MusicInfo> mAllMusics;
    private LocalItemRecyclerViewAdapter mAdapter;
    private final RecyclerView.OnScrollListener mListener;
    private IDonglingMusicAidlInterface mInterface;
    private NewMusicReceiver mReceiver;
    private boolean mHasSetPlayList = false;
    private MySharePreferences mInstance;
    public static final String LOCAL_LIST_INFO = "local_list";
    private int mPlayingId = -1;

    public LocalItemPager(Activity activity) {
        super(activity);
        //记得设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    showLetterSelectView();
                }
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(mLetterSelectorView.getVisibility()==View.VISIBLE&&!isAnimatorDoing){
                        startHideAnimation(mLetterSelectorView);
                    }
                }
            }
        };
        mRecyclerView.addOnScrollListener(mListener);
        mLetterSelectorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        showLetterSelectView();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(mLetterSelectorView.getVisibility()==View.VISIBLE&&!isAnimatorDoing){
                            startHideAnimation(mLetterSelectorView);
                        }
                        break;
                }
                return false;
            }
        });
        mLetterSelectorView.setOnLetterSelectedListener(new LetterSelectorView.onLetterSelectedListener() {
            @Override
            public void onLetterSelected(String letter) {
                scrollToFirstLetterShowPosition(letter);
            }
        });
        mReceiver = new NewMusicReceiver();
        IntentFilter filter = new IntentFilter(ACTION_NEW_MUSIC);
        filter.addAction(MusicService.ACTION_STOP);
        filter.addAction(PLAY_LIST_CHANGE);
        filter.addAction(MusicService.LOCAL_PLAYING_INDEX_CHANGED);
        filter.addAction(PLAY_BY_OPENING_LOCAL_PATH);
        filter.addAction(DialogUtils.DELETE_SONG_BROADCAST);
        filter.addAction(NEXT_PLAY_LIST_SET_BROADCAST);
        filter.addAction(PLAY_LIST_ITEM_DELETE_BROADCAST);
        mActivity.registerReceiver(mReceiver, filter);
        mInstance = MySharePreferences.getInstance();
        mLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayingId!=-1){
                    mRecyclerView.scrollToPosition(mPlayingId);
                }else{
                    ToastUtils.showToast("当前播放的歌曲不在本列表！");
                }
            }
        });
        //搜索
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ListSearchActivity.class);
                intent.putExtra("listInfo",LOCAL_LIST_INFO);
                mActivity.startActivityForResult(intent,0);
            }
        });
        ((HomeActivity)mActivity).setOnActivityReturnResultListener(new HomeActivity.OnActivityReturnResultListener() {
            @Override
            public void onResultReturn(int position) {//搜索结果回调
                if(position!=-1){
                    onItemClick(null,position);
                }
            }
        });
    }

    @Override
    public void initData() {
        if(!hasLoadData){
            mAllMusics = MyApplication.getMyApplication().getAllList();
            if(mAllMusics == null){
                if(MySharePreferences.getInstance().getScanMusicsComplete()){
                    ToastUtils.showToast("本地没有音乐！");
                }else{
                    ToastUtils.showToast("正在扫描本地音乐，请稍后重试！");
                }
            }else{
                mAdapter = new LocalItemRecyclerViewAdapter(mActivity,mAllMusics);
                mAdapter.setOnItemClickListener(this);
                mAdapter.setOnItemMenuClickListener(this);
                mRecyclerView.setAdapter(mAdapter);
                hasLoadData = true;
            }
            if(mInterface == null){
                mInterface = ((HomeActivity)mActivity).getInterface();
                try {
                    //如果是本地音乐正在播放，那么高亢播放的音乐
                    if(mInterface.getPlayState()!=MusicService.STATE_IDLE){
                        if(mHasSetPlayList){
                            mPlayingId = mInterface.getCurMusicInfoIndex();
                            mAdapter.setPlayingId(mPlayingId);
                        }else{
                            checkOtherListInLocalListOrnot(mInterface.getCurMusicInfo());
                        }
                    }
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void reLoadData() {
        //刷新数据了额！！！！！
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);//让这个控件停止刷新的旋转动画
            }
        },3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(mListener);
        mActivity.unregisterReceiver(mReceiver);
        mInterface = null;
        if(mAllMusics!=null){
            mAllMusics.removeAll(mAllMusics);
            mAllMusics = null;
        }
    }

    private void showLetterSelectView(){
        if(isAnimatorDoing){
            mAnimatorSet.end();
        }
        mLetterSelectorView.setVisibility(View.VISIBLE);
        mLetterSelectorView.setAlpha(1.0f);
        mMoreToolsView.setVisibility(View.VISIBLE);
        mMoreToolsView.setAlpha(1.0f);
    }

    private AnimatorSet mAnimatorSet;
    private boolean isAnimatorDoing = false;
    private void startHideAnimation(final View v){
        mAnimatorSet = new AnimatorSet();
        ObjectAnimator letterViewAnimator = ObjectAnimator.ofFloat(v,"alpha",1.0f,0.0f);
        ObjectAnimator moreToolsViewAnimator = ObjectAnimator.ofFloat(mMoreToolsView,"alpha",1.0f,0.0f);
        mAnimatorSet.setDuration(3500);
        mAnimatorSet.playTogether(letterViewAnimator,moreToolsViewAnimator);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(View.INVISIBLE);
                mMoreToolsView.setVisibility(View.GONE);
                isAnimatorDoing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimatorSet.start();
        isAnimatorDoing = true;
    }

    /**
     * 根据给定的letter，使得列表滚动到该letter第一次出现的位置
     * @param letter 字母
     */
    private void scrollToFirstLetterShowPosition(String letter){
        MusicInfo info;
        for (int i = 0; i < mAllMusics.size(); i++) {
            info = mAllMusics.get(i);
            if(PinYinUtil.getFirstPinYin(info.getTitle()).toUpperCase().charAt(0) == letter.charAt(0)){
                mAdapter.setSelectedId(i);
                mRecyclerView.scrollToPosition(i);
                return ;
            }
        }
        ToastUtils.showToast("没有以该字母开头的歌曲！");
    }

    //一个歌曲项点击事件
    @Override
    public void onItemClick(View v, int position) {
        try {
            if(!mHasSetPlayList){//第一次点击该列表中的歌曲，我们需要设置该列表为播放列表
                mInterface.setPlayList(mAllMusics);
                mHasSetPlayList = true;
                mInstance.savePlayerPlaylistInfo(LOCAL_LIST_INFO);
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
                        mInterface.setPlayList(mAllMusics);
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
        intent.putExtra("music_info",mAllMusics.get(position));
        intent.putExtra("selected_index",position);
        intent.putExtra("list_info",LOCAL_LIST_INFO);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(0,0);
    }

    private void checkOtherListInLocalListOrnot(MusicInfo musicInfo){
        MusicInfo info;
        for (int i = 0; i < mAllMusics.size(); i++) {
            info = mAllMusics.get(i);
            if(info.equals(musicInfo)){
                mPlayingId = i;
                mAdapter.setPlayingId(i);
                return;
            }
        }
        mPlayingId = -1;
        mAdapter.setPlayingId(mPlayingId);
    }

    class NewMusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(PLAY_LIST_CHANGE)){
                //当前的播放列表不是本地所有列表
                if(!mInstance.getPlayerPlaylistInfo().equals(LOCAL_LIST_INFO)){
                    mHasSetPlayList = false;
                }
            }else if(action.equals(PLAY_BY_OPENING_LOCAL_PATH)){
                mHasSetPlayList = true;
                mInstance.savePlayerPlaylistInfo(LOCAL_LIST_INFO);
            }else if(action.equals(DialogUtils.DELETE_SONG_BROADCAST)){
                String listInfo = intent.getStringExtra("listInfo");
                if(listInfo!=null&&listInfo.equals(LOCAL_LIST_INFO)){
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
                    mAllMusics.remove(musicInfo);
                    MusicDao.getInstance(mActivity).deleteItem(musicInfo);//同步数据库
                    mAdapter.notifyDataSetChanged();
                }
            }else if(action.equals(NEXT_PLAY_LIST_SET_BROADCAST)){
                if(intent.getStringExtra("listInfo").equals(LOCAL_LIST_INFO)){
                    int nextPlayIndex = intent.getIntExtra("nextPlayIndex",-1);
                    if(nextPlayIndex!=-1){
                        try {
                            mInterface.setPlayList(mAllMusics);
                            mHasSetPlayList = true;
                            mInstance.savePlayerPlaylistInfo(LOCAL_LIST_INFO);
                            mInterface.play(nextPlayIndex);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(action.equals(PLAY_LIST_ITEM_DELETE_BROADCAST)&&mHasSetPlayList){
                MusicInfo deleteItem = intent.getParcelableExtra("itemMusic");
                MusicInfo info;
                for (int i = 0; i < mAllMusics.size(); i++) {
                    info = mAllMusics.get(i);
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
                if(mInterface!=null){
                    try {
                        checkOtherListInLocalListOrnot(mInterface.getCurMusicInfo());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
