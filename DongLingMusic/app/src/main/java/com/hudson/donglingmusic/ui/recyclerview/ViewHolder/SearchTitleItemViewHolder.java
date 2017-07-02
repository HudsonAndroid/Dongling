package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.SearchResultRecyclerViewAdapter;

/**
 * Created by Hudson on 2017/3/21.
 */

public class SearchTitleItemViewHolder extends BaseRecyclerViewHolder<String> {
    private ImageView mSearchImage;
    private TextView mSearchTitle;


    public SearchTitleItemViewHolder(View itemView) {
        super(itemView);
        mSearchImage = (ImageView) itemView.findViewById(R.id.iv_search_title);
        mSearchTitle = (TextView) itemView.findViewById(R.id.tv_search_title);
    }

    @Override
    public void refreshView(String data) {

    }

    public void refreshView(int type,String data){
        switch (type){
            case SearchResultRecyclerViewAdapter.TYPE_TITLE_ARTIST:
                mSearchImage.setImageResource(R.drawable.search_artist);
                break;
            case SearchResultRecyclerViewAdapter.TYPE_TITLE_SONG:
                mSearchImage.setImageResource(R.drawable.search_song);
                break;
            case SearchResultRecyclerViewAdapter.TYPE_TITLE_ALBUM:
                mSearchImage.setImageResource(R.drawable.search_album);
                break;
        }
        mSearchTitle.setText(data);
    }

}
