package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.CategorySongListRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.LocalItemRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.view.MusicListItemLayout;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的歌曲的ViewHolder
 */

public class CategorySongListNormalViewHolder extends BaseRecyclerViewHolder<MusicInfo> {
    private TextView mTitle;
    private TextView mMusicInfo;
    private MusicListItemLayout mItemLayout;

    public CategorySongListNormalViewHolder(View itemView,
                                            final CategorySongListRecyclerViewAdapter.OnItemClickListener itemClickListener,
                                            final CategorySongListRecyclerViewAdapter.OnItemMenuClickListener menuClickListener) {
        super(itemView);
        mItemLayout = (MusicListItemLayout) itemView.findViewById(R.id.item_local);
        mTitle = (TextView) itemView.findViewById(R.id.tv_music_title);
        mMusicInfo = (TextView) itemView.findViewById(R.id.tv_music_info);
        //更多的点击事件
        itemView.findViewById(R.id.iv_more_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuClickListener!=null){
                    menuClickListener.onItemMenuClick(v,getLayoutPosition());
                }
            }
        });
        //该项的点击事件
        itemView.findViewById(R.id.item_local).setOnClickListener(new View.OnClickListener() {
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
        mTitle.setText(data.getTitle());
        mMusicInfo.setText(data.getMusicSingerInfo());
    }

    /**
     * 刷新状态，当某个Item是正在播放的item，我们高亢显示
     * @param state
     */
    public void refreshItemState(int state){
        if(state == LocalItemRecyclerViewAdapter.STATE_SELECTED){
            mItemLayout.setStateItemSelected(true);
        }else if(state == LocalItemRecyclerViewAdapter.STATE_PLAYING){
            mItemLayout.setStateItemPlaying(true);
        }else{
            mItemLayout.setStateItemPlaying(false);
            mItemLayout.setStateItemSelected(false);
        }
    }
}
