package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.AlbumListHeaderBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.AlbumListHeaderViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.AlbumListNormalViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 * 专辑歌单详情页面adapter
 */

public class AlbumListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AlbumListHeaderBean mHeaderBean;//头布局的实例
    private ArrayList<MusicInfo> mDatas;//歌曲集合
    private static final int TYPE_NORMAL = 1;//normal
    private static final int TYPE_HEADER = 2;//header
    private LayoutInflater mLayoutInflater;

    public AlbumListRecyclerViewAdapter(Context context){
        mDatas = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public AlbumListRecyclerViewAdapter(Context context, AlbumListHeaderBean albumListHeaderBean, ArrayList<MusicInfo> datas){
        mDatas = datas;
        mHeaderBean = albumListHeaderBean;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(AlbumListHeaderBean albumListHeaderBean, ArrayList<MusicInfo> datas){
        mHeaderBean = albumListHeaderBean;
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new AlbumListNormalViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_local,parent,false),mOnItemClickListener,
                        mOnItemMenuClickListener);
            case TYPE_HEADER:
                return new AlbumListHeaderViewHolder(mLayoutInflater.inflate(R.layout.item_album_list_header,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_HEADER://添加头布局数据,注意需要初入的参数是list
                if(mHeaderBean!=null){
                    ((BaseRecyclerViewHolder)holder).refreshView(mHeaderBean);
                }
                break;
            case TYPE_NORMAL:
                if(mDatas!=null)
                    ((BaseRecyclerViewHolder)holder).refreshView(mDatas.get(position-1));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }else{
            return TYPE_NORMAL;
        }
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
