package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.MyGedanListHeaderViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.MyGedanListNormalViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hudson on 2017/3/18.
 * 用户创建的歌单详情的adapter
 */

public class MyLocalGedanListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MyGedanBean mHeaderBean;//头布局的实例
    private List<MusicInfo> mDatas;//歌曲集合
    private static final int TYPE_NORMAL = 1;//normal
    private static final int TYPE_HEADER = 2;//header
    private LayoutInflater mLayoutInflater;

    public MyLocalGedanListRecyclerViewAdapter(Context context){
        mDatas = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setHeaderBean(MyGedanBean headerBean) {
        mHeaderBean = headerBean;
        notifyDataSetChanged();
    }

    public void setData(MyGedanBean gedanListHeaderBean, List<MusicInfo> datas){
        mHeaderBean = gedanListHeaderBean;
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new MyGedanListNormalViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_local,parent,false),mOnItemClickListener,
                        mOnItemMenuClickListener);
            case TYPE_HEADER:
                return new MyGedanListHeaderViewHolder(mLayoutInflater.inflate(
                        R.layout.item_my_gedan_list_header,parent,false),mOnManageGedanClickListener);
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


    private OnManageGedanClickListener mOnManageGedanClickListener;
    public void setOnManageGedanClickListener(OnManageGedanClickListener onManageGedanClickListener) {
        mOnManageGedanClickListener = onManageGedanClickListener;
    }
    public interface OnManageGedanClickListener{
        void onManageGedanClick();
    }
}
