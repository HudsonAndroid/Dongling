package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.HotRecyclerViewItemMusic;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.HotNormalViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.HotViewPagerViewHolder;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/18.
 */

public class HotItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> mViewPagerPicUrls;
    private ArrayList<HotRecyclerViewItemMusic> mDatas;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_VIEWPAGER = 2;//ViewPager
    private LayoutInflater mLayoutInflater;

    public HotItemRecyclerViewAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
        mViewPagerPicUrls = new ArrayList<>();
    }

    public HotItemRecyclerViewAdapter(Context context, ArrayList<String> mPicUrls,ArrayList<HotRecyclerViewItemMusic> datas){
        mDatas = datas;
        mViewPagerPicUrls = mPicUrls;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setViewPagerPicUrls(ArrayList<String> viewPagerPicUrls){
        mViewPagerPicUrls = viewPagerPicUrls;
        notifyDataSetChanged();
    }

    public void setDatas(ArrayList<HotRecyclerViewItemMusic> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                return new HotNormalViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_hot,parent,false),mOnItemClickListener);
            case TYPE_VIEWPAGER:
                return new HotViewPagerViewHolder(mLayoutInflater.inflate(R.layout.item_viewpager,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_VIEWPAGER://添加头布局数据,注意需要初入的参数是list
                if(mViewPagerPicUrls!=null)
                ((BaseRecyclerViewHolder)holder).refreshView(mViewPagerPicUrls);
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
            return TYPE_VIEWPAGER;
        }else{
            return TYPE_NORMAL;
        }
    }

    //榜单点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
}
