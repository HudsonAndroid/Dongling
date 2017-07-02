package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MyGedanRecyclerViewAdapter;

/**
 * Created by Hudson on 2017/6/11.
 * 我的最爱之本地歌单头布局
 */

public class MyLocalGedanHeaderItemViewHolder extends BaseRecyclerViewHolder<String> {
    private TextView mLocalGedanTitle;

    public MyLocalGedanHeaderItemViewHolder(View itemView, final MyGedanRecyclerViewAdapter.OnNewLocalGedanClickListener listener) {
        super(itemView);
        mLocalGedanTitle = (TextView) itemView.findViewById(R.id.tv_local_gedan_title);
        itemView.findViewById(R.id.tv_new_gedan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onNewLocalGedanClick();
                }
            }
        });
    }

    @Override
    public void refreshView(String data) {
        mLocalGedanTitle.setText("我的歌单");
    }
}
