package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.PlayListRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/6/19.
 */

public class PlayListItemViewHolder extends BaseRecyclerViewHolder<MusicInfo> {
    private TextView mTextView;
    private View mContainer;

    public PlayListItemViewHolder(View itemView,
                                  final PlayListRecyclerViewAdapter.OnItemClickListener itemClickListener,
                                  final PlayListRecyclerViewAdapter.OnDeleteItemClickListener deleteListener ) {
        super(itemView);
        itemView.findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteListener!=null){
                    deleteListener.onDeleteItemClick(getLayoutPosition());
                }
            }
        });
        mContainer = itemView.findViewById(R.id.ll_container);
        mTextView = (TextView) itemView.findViewById(R.id.tv_song_desc);
        itemView.findViewById(R.id.ll_playlist_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemClick(v,getLayoutPosition());
                }
            }
        });
    }

    @Override
    public void refreshView(MusicInfo data) {

    }

    public void refreshView(MusicInfo data,boolean playing) {
        mTextView.setText(data.getTitle());
        if(playing){
            mContainer.setBackgroundColor(UIUtils.getColor(R.color.half_yellow));
        }else{
            mContainer.setBackgroundColor(UIUtils.getColor(R.color.transparent));
        }
    }
}
