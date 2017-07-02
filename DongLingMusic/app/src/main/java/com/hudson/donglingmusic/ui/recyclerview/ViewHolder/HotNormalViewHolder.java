package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.HotRecyclerViewItemMusic;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.HotItemRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.view.RatioImageView;

/**
 * Created by Hudson on 2017/3/18.
 * 对应的布局文件是R.layout.item_recyclerview_hot
 */

public class HotNormalViewHolder extends BaseRecyclerViewHolder<HotRecyclerViewItemMusic> {
    private TextView mTitle;
    private TextView mMoreTextView;
    private RelativeLayout mSong1,mSong2,mSong3;
    private RatioImageView mSong1Bg,mSong2Bg,mSong3Bg;
    private TextView mSong1Singer,mSong2Singer,mSong3Singer;
    private TextView mSong1Title,mSong2Title,mSong3Title;

    public HotNormalViewHolder(View itemView, final HotItemRecyclerViewAdapter.OnItemClickListener listener) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        mMoreTextView = (TextView) itemView.findViewById(R.id.tv_more);
        mMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(v,getLayoutPosition()-1);
                }
            }
        });
        itemView.findViewById(R.id.ll_hot_item_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(v,getLayoutPosition()-1);
                }
            }
        });
        mSong1 = (RelativeLayout) itemView.findViewById(R.id.rl_song1);
        mSong1Bg = (RatioImageView) itemView.findViewById(R.id.iv_music_bg);
        mSong1Singer = (TextView) itemView.findViewById(R.id.tv_singer);
        mSong1Title = (TextView) itemView.findViewById(R.id.tv_song_title);

        mSong2 = (RelativeLayout) itemView.findViewById(R.id.rl_song2);
        mSong2Bg = (RatioImageView) itemView.findViewById(R.id.iv_music_bg2);
        mSong2Singer = (TextView) itemView.findViewById(R.id.tv_singer2);
        mSong2Title = (TextView) itemView.findViewById(R.id.tv_song_title2);

        mSong3 = (RelativeLayout) itemView.findViewById(R.id.rl_song3);
        mSong3Bg = (RatioImageView) itemView.findViewById(R.id.iv_music_bg3);
        mSong3Singer = (TextView) itemView.findViewById(R.id.tv_singer3);
        mSong3Title = (TextView) itemView.findViewById(R.id.tv_song_title3);
    }

    @Override
    public void refreshView(HotRecyclerViewItemMusic data) {
        mTitle.setText(data.title);
//        mSong1Bg
        mSong1Singer.setText(data.mSongInfos.get(0).singerName);
        mSong2Singer.setText(data.mSongInfos.get(1).singerName);
        mSong3Singer.setText(data.mSongInfos.get(2).singerName);
        XUtilsManager.getBitmapUtilsInstance().display(mSong1Bg,data.mSongInfos.get(0).bgUrl);
        XUtilsManager.getBitmapUtilsInstance().display(mSong2Bg,data.mSongInfos.get(1).bgUrl);
        XUtilsManager.getBitmapUtilsInstance().display(mSong3Bg,data.mSongInfos.get(2).bgUrl);
        mSong1Title.setText(data.mSongInfos.get(0).songTitle);
        mSong2Title.setText(data.mSongInfos.get(1).songTitle);
        mSong3Title.setText(data.mSongInfos.get(2).songTitle);

    }
}
