package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.DownloadCompleteItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/3/21.
 * 下载完成的列表适配器
 */

public class DownloadCompleteItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<MusicInfo> mMusicInfos;
    private int mPlayingId = -1;

    public int getPlayingId() {
        return mPlayingId;
    }

    public void setPlayingId(int playingId) {
        mPlayingId = playingId;
        notifyDataSetChanged();
    }

    public void setMusicInfos(List<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
        notifyDataSetChanged();
    }

    public DownloadCompleteItemRecyclerViewAdapter(Context context,List<MusicInfo> musicInfos){
        mLayoutInflater = LayoutInflater.from(context);
        mMusicInfos = musicInfos;
    }

    public DownloadCompleteItemRecyclerViewAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        mMusicInfos = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadCompleteItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_local,null),
                mOnItemClickListener,mOnItemMenuClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DownloadCompleteItemViewHolder itemViewHolder = (DownloadCompleteItemViewHolder) holder;
        if(position == mPlayingId){
            itemViewHolder.refreshItemState(LocalItemRecyclerViewAdapter.STATE_PLAYING);
        }else{
            itemViewHolder.refreshItemState(LocalItemRecyclerViewAdapter.STATE_NORMAL);
        }
        itemViewHolder.refreshView(mMusicInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicInfos.size();
    }

    //每一项点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    //每一项更多的点击事件
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        mOnItemMenuClickListener = onItemMenuClickListener;
    }
    private OnItemMenuClickListener mOnItemMenuClickListener;
    public interface  OnItemMenuClickListener{
        void onItemMenuClick(View v, int position);
    }

}
