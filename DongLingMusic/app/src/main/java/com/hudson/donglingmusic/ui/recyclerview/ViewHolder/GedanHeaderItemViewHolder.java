package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.GedanRecyclerViewAdapter;

/**
 * Created by Hudson on 2017/6/8.
 * 网络歌单头布局
 */

public class GedanHeaderItemViewHolder extends BaseRecyclerViewHolder<String> {
    private TextView mCategoryTitle;

    public GedanHeaderItemViewHolder(View itemView, final GedanRecyclerViewAdapter.OnChooseCategoryClickListener listener) {
        super(itemView);
        mCategoryTitle = (TextView) itemView.findViewById(R.id.tv_category_title);
        itemView.findViewById(R.id.tv_choose_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onChooseCategoryClick();
                }
            }
        });
    }

    @Override
    public void refreshView(String data) {
        mCategoryTitle.setText(data);
    }
}
