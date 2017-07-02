package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/6/11.
 * 我的最爱之网络歌单头布局
 */

public class MyNetGedanHeaderItemViewHolder extends BaseRecyclerViewHolder<String> {
    private TextView mNetGedanTitle;

    public MyNetGedanHeaderItemViewHolder(View itemView) {
        super(itemView);
        mNetGedanTitle = (TextView) itemView.findViewById(R.id.tv_local_gedan_title);
        itemView.findViewById(R.id.tv_new_gedan).setVisibility(View.GONE);
    }

    @Override
    public void refreshView(String data) {
        mNetGedanTitle.setText("收藏歌单");
    }
}
