package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.PlayListItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/6/19.
 * 播放队列adapter
 */

public class PlayListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<MusicInfo> mDatas;
    private int mPlayingPosition = -1;

    public void setPlayingPosition(int playingPosition) {
        mPlayingPosition = playingPosition;
        notifyDataSetChanged();
    }

    public void setMusicMenuDatas(List<MusicInfo> musicDatas) {
        mDatas = musicDatas;
        notifyDataSetChanged();
    }

    public PlayListRecyclerViewAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlayListItemViewHolder(
                mLayoutInflater.inflate(R.layout.item_recyclerview_play_list,null),
                mOnItemClickListener,mOnDeleteItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PlayListItemViewHolder)holder).refreshView(mDatas.get(position),position == mPlayingPosition);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //每一项点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }


    private OnDeleteItemClickListener mOnDeleteItemClickListener;

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener onDeleteItemClickListener) {
        mOnDeleteItemClickListener = onDeleteItemClickListener;
    }

    public interface OnDeleteItemClickListener{
        void onDeleteItemClick(int position);
    }
}
