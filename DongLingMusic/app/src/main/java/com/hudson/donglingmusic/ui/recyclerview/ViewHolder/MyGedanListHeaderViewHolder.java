package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MyLocalGedanListRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的头布局的ViewHolder
 */

public class MyGedanListHeaderViewHolder extends BaseRecyclerViewHolder<MyGedanBean> {
    private ImageView mListBg;
    private TextView mListTitle, mSongCount,mTag,mDesc;

    public MyGedanListHeaderViewHolder(View itemView, final MyLocalGedanListRecyclerViewAdapter.OnManageGedanClickListener listener) {
        super(itemView);
        itemView.findViewById(R.id.iv_manage_gedan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onManageGedanClick();
                }
            }
        });
        mListBg = (ImageView) itemView.findViewById(R.id.iv_gedan_list_bg);
        mListTitle = (TextView) itemView.findViewById(R.id.tv_gedan_name);
        mSongCount = (TextView) itemView.findViewById(R.id.tv_song_count);
        mTag = (TextView) itemView.findViewById(R.id.tv_tag);
        mDesc = (TextView) itemView.findViewById(R.id.tv_gedan_info);
    }

    @Override
    public void refreshView(MyGedanBean data) {
        String imagePath = data.getImagePath();
        if(TextUtils.isEmpty(imagePath)){
            mListBg.setImageResource(R.drawable.gedan_disk);
        }else{
            mListBg.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
        mListTitle.setText(data.getTitle());
        mSongCount.setText(data.getSongCount()+"");
        mTag.setText(data.getTag());
        mDesc.setText(data.getDesc());
        mDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxLines = mDesc.getMaxLines();
                if(maxLines==3){
                    mDesc.setMaxLines(1000);
                    mDesc.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.up_arrow));
                }else{
                    mDesc.setMaxLines(3);
                    mDesc.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.down_arrow));
                }
                mDesc.invalidate();
            }
        });
    }
}
