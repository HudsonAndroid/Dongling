package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.AlbumListHeaderBean;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的头布局的ViewHolder
 */

public class AlbumListHeaderViewHolder extends BaseRecyclerViewHolder<AlbumListHeaderBean> {
    private ImageView mListBg;
    private TextView mListName,mListSongNum, mPublishTime,mAuthor,mCollect
            ,mListen,mComment,mShare,mInfo;
    private boolean mIsMore = false;


    public AlbumListHeaderViewHolder(View itemView) {
        super(itemView);
        mListBg = (ImageView) itemView.findViewById(R.id.iv_album_list_bg);
        mListName = (TextView) itemView.findViewById(R.id.tv_album_list_name);
        mListSongNum = (TextView) itemView.findViewById(R.id.tv_song_num);
        mPublishTime = (TextView) itemView.findViewById(R.id.tv_publish_time);
        mAuthor = (TextView) itemView.findViewById(R.id.tv_album_author);
        mCollect = (TextView) itemView.findViewById(R.id.tv_album_collect_num);
        mListen = (TextView) itemView.findViewById(R.id.tv_album_listen);
        mComment = (TextView) itemView.findViewById(R.id.tv_album_comment_num);
        mShare = (TextView) itemView.findViewById(R.id.tv_album_share);
        mInfo = (TextView) itemView.findViewById(R.id.tv_album_info);
    }

    @Override
    public void refreshView(AlbumListHeaderBean data) {
        XUtilsManager.getBitmapUtilsInstance().display(mListBg,data.getListBgUrl());
        mListName.setText(data.getAlbumName());
        mAuthor.setText(data.getAuthor());
        mCollect.setText(getHumanData(data.getCollectCount()));
        mListen.setText(getHumanData(data.getListenCount()));
        mComment.setText(getHumanData(data.getCommentCount()));
        mShare.setText(getHumanData(data.getShareNum()));
        mInfo.setText(data.getInfo());
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxLines = mInfo.getMaxLines();
                if(maxLines==3){
                    mInfo.setMaxLines(1000);
                    mInfo.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.up_arrow));
                }else{
                    mInfo.setMaxLines(3);
                    mInfo.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.down_arrow));
                }
                mInfo.invalidate();
            }
        });
        String listSongNumber = data.getSongCount();
        if(!TextUtils.isEmpty(listSongNumber)){
            mListSongNum.setText(listSongNumber);
        }else{
            mListSongNum.setText("未知");
        }
        String listUpdateTime = data.getPublishTime();
        if(!TextUtils.isEmpty(listUpdateTime)){
            mPublishTime.setText(listUpdateTime);
        }else{
            mPublishTime.setText("未知");
        }
    }

    public static String getHumanData(String srcNumber){
        try {
            Integer number = Integer.valueOf(srcNumber);
            if(number>10000){
                int wan = number/10000;
                if(number<1000000){
                   int qian = number%10000/1000;
                    return wan+"."+qian+"万";
                }else{
                    return wan+"万";
                }

            }else{
                return srcNumber;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "未知";
        }
    }
}
