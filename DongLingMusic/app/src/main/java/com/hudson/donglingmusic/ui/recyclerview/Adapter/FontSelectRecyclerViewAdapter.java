package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.FontSelectViewHolder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Hudson on 2017/4/9.
 * 字体选择的adapter
 */

public class FontSelectRecyclerViewAdapter extends RecyclerView.Adapter {
    private LayoutInflater mLayoutInflater;
    private ArrayList<File> mData;
    private int mSelectedPosition = -1;


    public FontSelectRecyclerViewAdapter(Context context, ArrayList<File> data){
        mLayoutInflater = LayoutInflater.from(context);
        mData = data;
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mSelectedPosition = position;
                notifyDataSetChanged();
            }
        });
        mSelectedPosition = getPreFontFileIndex();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FontSelectViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_fonts,
                null),mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FontSelectViewHolder fontSelectViewHolder = (FontSelectViewHolder) holder;
        fontSelectViewHolder.refreshView(mData.get(position));
        fontSelectViewHolder.refreshSelectedItem(position == mSelectedPosition);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }


    public int getPreFontFileIndex(){
        String fontFilePath = MySharePreferences.getInstance().getFontFilePath();
        if(fontFilePath==null){
            return -1;
        }
        File f;
        for (int i = 0; i < mData.size(); i++) {
            f = mData.get(i);
            if(f.getAbsolutePath().equals(fontFilePath)){
                return i;
            }
        }
        return -1;
    }
}
