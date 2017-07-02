package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.LocalItemViewHolder;

import java.util.List;

/**
 * Created by Hudson on 2017/3/21.
 */

public class LocalItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_PLAYING = 2;
    public static final int STATE_NORMAL = 3;
    private List<MusicInfo> mMusicInfos;
    private int mSelectedId = -1;
    private int mPlayingId = -1;

    public int getSelectedId() {
        return mSelectedId;
    }

    public void setSelectedId(int selectedId) {
        mSelectedId = selectedId;
    }

    public int getPlayingId() {
        return mPlayingId;
    }

    public void setPlayingId(int playingId) {
        mPlayingId = playingId;
        mSelectedId = -1;
        notifyDataSetChanged();
    }

    public void setMusicInfos(List<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
        notifyDataSetChanged();
    }

    public LocalItemRecyclerViewAdapter(Context context,List<MusicInfo> musicInfos){
        mLayoutInflater = LayoutInflater.from(context);
        mMusicInfos = musicInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocalItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_local,null),
                mOnItemClickListener,mOnItemMenuClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LocalItemViewHolder itemViewHolder = (LocalItemViewHolder) holder;
        if(position == mPlayingId){
            itemViewHolder.refreshItemState(STATE_PLAYING);
        }else if(position == mSelectedId){
            itemViewHolder.refreshItemState(STATE_SELECTED);
        }else{
            itemViewHolder.refreshItemState(STATE_NORMAL);
        }
        mSelectedId = -1;
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
        void onItemClick(View v,int position);
    }

    //每一项更多的点击事件
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        mOnItemMenuClickListener = onItemMenuClickListener;
    }
    private OnItemMenuClickListener mOnItemMenuClickListener;
    public interface  OnItemMenuClickListener{
        void onItemMenuClick(View v,int position);
    }

}
