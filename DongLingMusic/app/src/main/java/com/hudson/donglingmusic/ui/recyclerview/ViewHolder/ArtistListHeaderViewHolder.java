package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.ArtistListHeaderBean;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的头布局的ViewHolder
 */

public class ArtistListHeaderViewHolder extends BaseRecyclerViewHolder<ArtistListHeaderBean> {
    private ImageView mListBg;
    private TextView mArtistName, mArtistCompany, mArea,mListenCount,mSongCount,
            mShareCount,mBirthDay,mArtistInfo;

    public ArtistListHeaderViewHolder(View itemView) {
        super(itemView);
        mListBg = (ImageView) itemView.findViewById(R.id.iv_artist_list_bg);
        mArtistName = (TextView) itemView.findViewById(R.id.tv_artist_name);
        mArtistCompany = (TextView) itemView.findViewById(R.id.tv_company);
        mArea = (TextView) itemView.findViewById(R.id.tv_area);
        mListenCount = (TextView) itemView.findViewById(R.id.tv_listen_count);
        mSongCount = (TextView) itemView.findViewById(R.id.tv_song_count);
        mShareCount = (TextView) itemView.findViewById(R.id.tv_share);
        mBirthDay = (TextView) itemView.findViewById(R.id.tv_birthday);
        mArtistInfo = (TextView) itemView.findViewById(R.id.tv_artist_info);
    }

    @Override
    public void refreshView(ArtistListHeaderBean data) {
        XUtilsManager.getBitmapUtilsInstance().display(mListBg,data.getAvatar_middle());
        mArtistName.setText(data.getName());
        mArtistCompany.setText(data.getCompany());
        mArea.setText(data.getCountry());
        mListenCount.setText(AlbumListHeaderViewHolder.getHumanData(data.getListen_num()));
        mSongCount.setText(AlbumListHeaderViewHolder.getHumanData(data.getSongs_total()));
        mShareCount.setText(AlbumListHeaderViewHolder.getHumanData(data.getShare_num()+""));
        mBirthDay.setText(data.getBirth());
        mArtistInfo.setText(data.getIntro());
        mArtistInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxLines = mArtistInfo.getMaxLines();
                if(maxLines==3){
                    mArtistInfo.setMaxLines(1000);
                    mArtistInfo.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.up_arrow));
                }else{
                    mArtistInfo.setMaxLines(3);
                    mArtistInfo.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.down_arrow));
                }
                mArtistInfo.invalidate();
            }
        });
    }
}
