package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MusicMenuItemBean;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MusicMenuRecyclerViewAdapter;

/**
 * Created by Hudson on 2017/3/21.
 * 歌曲详情item的viewHolder
 */

public class MusicMenuItemViewHolder extends BaseRecyclerViewHolder<MusicMenuItemBean> {
    private ImageView mImageView;
    private TextView mTextView;

    public MusicMenuItemViewHolder(View itemView,
                                   final MusicMenuRecyclerViewAdapter.OnItemClickListener itemClickListener
                                   ) {
        super(itemView);
        mImageView = (ImageView) itemView.findViewById(R.id.iv_music_menu_icon);
        mTextView = (TextView) itemView.findViewById(R.id.tv_music_menu_desc);
        itemView.findViewById(R.id.ll_music_item_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemClick(v,getLayoutPosition());
                }
            }
        });
    }

    @Override
    public void refreshView(MusicMenuItemBean data) {
        mImageView.setImageResource(data.getImageId());
        mTextView.setText(data.getDesc());
    }
}
