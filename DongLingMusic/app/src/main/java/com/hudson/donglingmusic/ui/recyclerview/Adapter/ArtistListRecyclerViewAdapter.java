package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.ArtistListHeaderBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.ArtistListHeaderViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.ArtistListNormalViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SongListBottomViewHolder;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 * 歌手歌单详情的adapter
 */

public class ArtistListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArtistListHeaderBean mHeaderBean;//头布局的实例
    private ArrayList<MusicInfo> mDatas;//歌曲集合
    private static final int TYPE_NORMAL = 1;//normal
    private static final int TYPE_HEADER = 2;//header
    private static final int TYPE_BOTTOM = 3;//bottom
    private LayoutInflater mLayoutInflater;
    private int mLoadMoreStatus = SongListBottomViewHolder.STATUS_NONE;

    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void setLoadMoreStatus(int loadMoreStatus) {
        mLoadMoreStatus = loadMoreStatus;
        notifyDataSetChanged();
    }

    public void setHeaderBean(ArtistListHeaderBean headerBean) {
        mHeaderBean = headerBean;
        notifyDataSetChanged();
    }

    public void setDatas(ArrayList<MusicInfo> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public ArtistListRecyclerViewAdapter(Context context){
        mDatas = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public ArtistListRecyclerViewAdapter(Context context, ArtistListHeaderBean artistListHeaderBean, ArrayList<MusicInfo> datas){
        mDatas = datas;
        mHeaderBean = artistListHeaderBean;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(ArtistListHeaderBean artistListHeaderBean, ArrayList<MusicInfo> datas){
        mHeaderBean = artistListHeaderBean;
        mDatas = datas;
        notifyDataSetChanged();
    }

    /**
     * 加载更多后的结果刷新
     * @param newData
     */
    public void updateMusicList(ArrayList<MusicInfo> newData){
        if(newData.size() == 0){
            setLoadMoreStatus(SongListBottomViewHolder.STATUS_NO_MORE);//没有更多数据了
        }else{
            setLoadMoreStatus(SongListBottomViewHolder.STATUS_NONE);//去除加载更多的布局
            mDatas.addAll(newData);
            System.out.println("最后一个是"+mDatas.get(mDatas.size()-1).toString());
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new ArtistListNormalViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_local,parent,false),mOnItemClickListener,
                        mOnItemMenuClickListener);
            case TYPE_HEADER:
                return new ArtistListHeaderViewHolder(mLayoutInflater.inflate(R.layout.item_artist_list_header,parent,false));
            case TYPE_BOTTOM:
                return new SongListBottomViewHolder(mLayoutInflater.inflate(R.layout.item_music_list_bottom,parent,false));
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
            default:
                break;
        }
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
