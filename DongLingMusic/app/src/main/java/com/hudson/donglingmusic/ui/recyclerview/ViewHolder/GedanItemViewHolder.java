package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.GedanItemBean;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.GedanRecyclerViewAdapter;

/**
 * Created by Hudson on 2017/6/8.
 * 歌单一项的viewHolder
 */

public class GedanItemViewHolder extends BaseRecyclerViewHolder<GedanItemBean.ContentBean> {
    private ImageView mBg;
    private TextView mListenCount,mTag,mTitle;
    private String mListId = null;

    public GedanItemViewHolder(View itemView, final GedanRecyclerViewAdapter.OnItemClickListener listener) {
        super(itemView);
        mBg = (ImageView) itemView.findViewById(R.id.iv_gedan_bg);
        mListenCount = (TextView) itemView.findViewById(R.id.tv_listen_count);
        mTag = (TextView) itemView.findViewById(R.id.tv_gedan_tag);
        mTitle = (TextView) itemView.findViewById(R.id.tv_gedan_title);
        itemView.findViewById(R.id.ll_gedan_item_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null&&mListId!=null){
                    listener.onItemClick(v,mListId);
                }
            }
        });
    }

    @Override
    public void refreshView(GedanItemBean.ContentBean data) {
        String title = data.getTitle();
        Object itemTag = mBg.getTag();
        mListId = data.getListid();
        if(itemTag==null||!itemTag.equals(title)){
            XUtilsManager.getBitmapUtilsInstance().display(mBg,data.getPic_300());
            mBg.setTag(title);
            mListenCount.setText(AlbumListHeaderViewHolder.getHumanData(data.getListenum()));
            mTag.setText(data.getTag());
            mTitle.setText(title);
        }
    }
}
