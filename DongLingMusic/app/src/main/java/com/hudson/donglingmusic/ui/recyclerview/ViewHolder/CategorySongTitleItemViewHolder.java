package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/3/21.
 */

public class CategorySongTitleItemViewHolder extends BaseRecyclerViewHolder<String> {
    private TextView mCategoryTitle;


    public CategorySongTitleItemViewHolder(View itemView) {
        super(itemView);
        mCategoryTitle = (TextView) itemView.findViewById(R.id.tv_category_title);
    }

    @Override
    public void refreshView(String data) {
        mCategoryTitle.setText(data);
    }
}
