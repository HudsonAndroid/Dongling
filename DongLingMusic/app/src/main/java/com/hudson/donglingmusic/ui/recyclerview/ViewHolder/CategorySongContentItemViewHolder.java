package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.CategorySongRecyclerViewAdapter;

/**
 * Created by Hudson on 2017/3/21.
 */

public class CategorySongContentItemViewHolder extends BaseRecyclerViewHolder<String> {
    private TextView mCategoryContent;


    public CategorySongContentItemViewHolder(View itemView, final CategorySongRecyclerViewAdapter.OnItemClickListener listener) {
        super(itemView);
        mCategoryContent = (TextView) itemView.findViewById(R.id.tv_category_content);
        mCategoryContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String tag = mCategoryContent.getText().toString().trim();
                    if(!TextUtils.isEmpty(tag)){
                        listener.onItemClick(tag);
                    }
                }
            }
        });
    }

    @Override
    public void refreshView(String data) {
        mCategoryContent.setText(data);
    }
}
