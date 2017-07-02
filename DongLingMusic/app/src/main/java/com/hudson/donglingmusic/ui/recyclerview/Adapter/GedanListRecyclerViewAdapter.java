package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.GedanListHeaderBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.GedanListHeaderViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.GedanListNormalViewHolder;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 * 歌手歌单详情的adapter
 */

public class GedanListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private GedanListHeaderBean mHeaderBean;//头布局的实例
    private ArrayList<MusicInfo> mDatas;//歌曲集合
    private static final int TYPE_NORMAL = 1;//normal
    private static final int TYPE_HEADER = 2;//header
    private LayoutInflater mLayoutInflater;


    public void setHeaderBean(GedanListHeaderBean headerBean) {
        mHeaderBean = headerBean;
        notifyDataSetChanged();
    }

    public void setDatas(ArrayList<MusicInfo> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public GedanListRecyclerViewAdapter(Context context){
        mDatas = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public GedanListRecyclerViewAdapter(Context context, GedanListHeaderBean gedanListHeaderBean, ArrayList<MusicInfo> datas){
        mDatas = datas;
        mHeaderBean = gedanListHeaderBean;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(GedanListHeaderBean gedanListHeaderBean, ArrayList<MusicInfo> datas){
        mHeaderBean = gedanListHeaderBean;
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new GedanListNormalViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_local,parent,false),mOnItemClickListener,
                        mOnItemMenuClickListener);
            case TYPE_HEADER:
                return new GedanListHeaderViewHolder(mLayoutInflater.inflate(R.layout.item_gedan_list_header,parent,false));
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
