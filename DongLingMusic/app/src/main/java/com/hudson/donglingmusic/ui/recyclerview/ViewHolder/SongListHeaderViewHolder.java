package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.SongListHeaderBean;
import com.hudson.donglingmusic.global.XUtilsManager;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的头布局的ViewHolder
 */

public class SongListHeaderViewHolder extends BaseRecyclerViewHolder<SongListHeaderBean> {
    private ImageView mListBg;
    private TextView mListName,mListSongNum,mListUpdateTime;

    public SongListHeaderViewHolder(View itemView) {
        super(itemView);
        mListBg = (ImageView) itemView.findViewById(R.id.iv_song_list_bg);
        mListName = (TextView) itemView.findViewById(R.id.tv_song_list_name);
        mListSongNum = (TextView) itemView.findViewById(R.id.tv_song_num);
        mListUpdateTime = (TextView) itemView.findViewById(R.id.tv_update_time);
    }

    @Override
    public void refreshView(SongListHeaderBean data) {
        XUtilsManager.getBitmapUtilsInstance().display(mListBg,data.getListBgUrl());
        mListName.setText(data.getListName());

        String listSongNumber = data.getListSongNumber();
        if(!TextUtils.isEmpty(listSongNumber)){
            mListSongNum.setText("歌曲数:"+ listSongNumber);
        }else{
            mListSongNum.setText("歌曲数未知");
        }
        String listUpdateTime = data.getListUpdateTime();
        if(!TextUtils.isEmpty(listUpdateTime)){
            mListUpdateTime.setText("更新时间:"+ listUpdateTime);
        }else{
            mListUpdateTime.setText("更新时间未知");
        }
    }
}
