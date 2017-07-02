package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.GedanItemBean;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.GedanHeaderItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.GedanItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SongListBottomViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/3/18.
 * 网络歌单详情的adapter
 */

public class GedanRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String mCategoryTitle;
    private List<GedanItemBean.ContentBean> mDatas;//歌单集合
    public static final int TYPE_NORMAL = 1;//normal
    public static final int TYPE_HEADER = 2;//header
    public static final int TYPE_BOTTOM = 3;//bottom
    private LayoutInflater mLayoutInflater;
    private int mLoadMoreStatus = SongListBottomViewHolder.STATUS_NONE;


    public void setLoadMoreStatus(int loadMoreStatus) {
        mLoadMoreStatus = loadMoreStatus;
        notifyDataSetChanged();
    }

    public void setCategoryTitle(String categoryTitle) {
        mCategoryTitle = categoryTitle;
        notifyDataSetChanged();
    }

    public void setDatas(List<GedanItemBean.ContentBean> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public GedanRecyclerViewAdapter(Context context){
        mDatas = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public GedanRecyclerViewAdapter(Context context,List<GedanItemBean.ContentBean> datas){
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 加载更多后的结果刷新
     * @param newData
     */
    public void updateMusicList(ArrayList<GedanItemBean.ContentBean> newData){
        if(newData.size() == 0){
            setLoadMoreStatus(SongListBottomViewHolder.STATUS_NO_MORE);//没有更多数据了
        }else{
            setLoadMoreStatus(SongListBottomViewHolder.STATUS_NONE);//去除加载更多的布局
            mDatas.addAll(newData);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new GedanItemViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_gedan,parent,false),mOnItemClickListener);
            case TYPE_HEADER:
                return new GedanHeaderItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_gedan_header,parent,false),
                        mOnChooseCategoryClickListener);
            case TYPE_BOTTOM:
                return new SongListBottomViewHolder(mLayoutInflater.inflate(R.layout.item_music_list_bottom,parent,false));
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_HEADER://添加头布局数据,注意需要初入的参数是list
                if(mCategoryTitle!=null){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryTitle);
                }
                break;
            case TYPE_NORMAL:
                if(mDatas!=null)
                    ((BaseRecyclerViewHolder)holder).refreshView(mDatas.get(position-1));
                break;
            case TYPE_BOTTOM:
                switch (mLoadMoreStatus){
                    case SongListBottomViewHolder.STATUS_NONE:
                        ((BaseRecyclerViewHolder)holder).refreshView(SongListBottomViewHolder.STATUS_NONE);
                        break;
                    case SongListBottomViewHolder.STATUS_LOADING:
                        ((BaseRecyclerViewHolder)holder).refreshView(SongListBottomViewHolder.STATUS_LOADING);
                        break;
                    case SongListBottomViewHolder.STATUS_NO_MORE:
                        ((BaseRecyclerViewHolder)holder).refreshView(SongListBottomViewHolder.STATUS_NO_MORE);
                        break;
                }
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDatas.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }else if(position == mDatas.size()+1){
            return TYPE_BOTTOM;
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
        void onItemClick(View v, String gedanListId);
    }

    public void setOnChooseCategoryClickListener(OnChooseCategoryClickListener onChooseCategoryClickListener) {
        mOnChooseCategoryClickListener = onChooseCategoryClickListener;
    }
    private OnChooseCategoryClickListener mOnChooseCategoryClickListener;
    public interface OnChooseCategoryClickListener{
        void onChooseCategoryClick();
    }
}
