package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.MyGedanItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.MyLocalGedanHeaderItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.MyNetGedanHeaderItemViewHolder;

import java.util.List;

/**
 * Created by Hudson on 2017/6/11.
 * 我的歌单详情的adapter
 */

public class MyGedanRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MyGedanBean> mLocalGedan;
    private List<MyGedanBean> mNetGedan;
    public static final int TYPE_NORMAL = 1;//normal
    public static final int TYPE_TITLE_LOCAL = 2;
    public static final int TYPE_TITLE_NET = 3;
    private LayoutInflater mLayoutInflater;

    public void setGedanData(List<MyGedanBean> localGedan,List<MyGedanBean> netGedan) {
        mLocalGedan = localGedan;
        mNetGedan = netGedan;
        notifyDataSetChanged();
    }

    public MyGedanRecyclerViewAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new MyGedanItemViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_gedan,parent,false),mOnItemClickListener,mOnDeleteGedanHappenedListener);
            case TYPE_TITLE_LOCAL:
                return new MyLocalGedanHeaderItemViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_localgedan_title,parent,false),mOnNewLocalGedanClickListener);
            case TYPE_TITLE_NET:
                return new MyNetGedanHeaderItemViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_localgedan_title,parent,false));
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_TITLE_LOCAL:
                if(mLocalGedan!=null){
                    ((BaseRecyclerViewHolder)holder).refreshView(null);
                }
                break;
            case TYPE_TITLE_NET:
                if(mNetGedan!=null){
                    ((BaseRecyclerViewHolder)holder).refreshView(null);
                }
                break;
            case TYPE_NORMAL:
                if(position>0&&position<(mLocalGedan.size()+1)){
                    ((BaseRecyclerViewHolder)holder).refreshView(mLocalGedan.get(position - 1));
                }else{
                    ((BaseRecyclerViewHolder)holder).refreshView(mNetGedan.get(position - mLocalGedan.size()- 2));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mNetGedan!=null&&mLocalGedan!=null){
            return mNetGedan.size() + mLocalGedan.size() + 2;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_TITLE_LOCAL;
        }else if(position == mLocalGedan.size()+1){
            return TYPE_TITLE_NET;
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
        /**
         * 回调
         * @param netOrNot 是不是网络歌单
         * @param netIdOrLocalIndex 网络歌单的歌单Id或者是本地歌单对应的列表的index
         */
        void onItemClick(boolean netOrNot, String netIdOrLocalIndex);
    }

    public void setOnNewLocalGedanClickListener(OnNewLocalGedanClickListener onOnNewLocalGedanClickListener) {
        mOnNewLocalGedanClickListener = onOnNewLocalGedanClickListener;
    }

    private OnNewLocalGedanClickListener mOnNewLocalGedanClickListener;
    public interface OnNewLocalGedanClickListener {
        void onNewLocalGedanClick();
    }


    private OnDeleteGedanHappenedListener mOnDeleteGedanHappenedListener;

    public void setOnDeleteGedanHappenedListener(OnDeleteGedanHappenedListener onDeleteGedanHappenedListener) {
        mOnDeleteGedanHappenedListener = onDeleteGedanHappenedListener;
    }

    public interface OnDeleteGedanHappenedListener{
        void onDeleteGedan(boolean isNet,String title);
    }

}
