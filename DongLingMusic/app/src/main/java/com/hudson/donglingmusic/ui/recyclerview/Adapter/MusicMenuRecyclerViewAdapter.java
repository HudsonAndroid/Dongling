package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MusicMenuItemBean;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.MusicMenuItemViewHolder;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/21.
 */

public class MusicMenuRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private ArrayList<MusicMenuItemBean> mMusicMenuDatas;

    public void setMusicMenuDatas(ArrayList<MusicMenuItemBean> musicMenuDatas) {
        mMusicMenuDatas = musicMenuDatas;
        notifyDataSetChanged();
    }

    public MusicMenuRecyclerViewAdapter(Context context, ArrayList<MusicMenuItemBean> musicMenuDatas){
        mLayoutInflater = LayoutInflater.from(context);
        mMusicMenuDatas = musicMenuDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicMenuItemViewHolder(
                mLayoutInflater.inflate(R.layout.item_recyclerview_music_menu,null),
                mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseRecyclerViewHolder)holder).refreshView(mMusicMenuDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicMenuDatas.size();//+1我们有一个头布局(title)
    }

    //每一项点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
}
