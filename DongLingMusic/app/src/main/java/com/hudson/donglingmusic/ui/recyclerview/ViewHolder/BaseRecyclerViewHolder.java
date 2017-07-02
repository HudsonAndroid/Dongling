package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Hudson on 2017/3/18.
 * ViewHolder基类
 */

public abstract class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {
    private T mData;//需要更新到UI上的数据

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 设置更新Item的数据
     * @param data
     */
    public void setData(T data){
        mData = data;
        refreshView(data);
    }

    /**
     * 必须由子类实现，因为我们不知道怎么更新
     * @param data
     */
    public abstract void refreshView(T data);

    public T getData(){
        return mData;
    }
}
