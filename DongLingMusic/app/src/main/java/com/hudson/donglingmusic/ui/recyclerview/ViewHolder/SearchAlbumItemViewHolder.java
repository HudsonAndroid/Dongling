package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.SearchSuggestionBean;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.SearchResultRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.StringTextUtils;

/**
 * Created by Hudson on 2017/3/21.
 */

public class SearchAlbumItemViewHolder extends BaseRecyclerViewHolder<SearchSuggestionBean.AlbumBean> {
    private TextView mSearchTitle;


    public SearchAlbumItemViewHolder(View itemView, final SearchResultRecyclerViewAdapter.OnItemClickListener listener) {
        super(itemView);
        mSearchTitle = (TextView) itemView.findViewById(R.id.tv_search_content);
        itemView.findViewById(R.id.ll_search_artist_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(v,getLayoutPosition());
                }
            }
        });
    }

    @Override
    public void refreshView(SearchSuggestionBean.AlbumBean data) {
        mSearchTitle.setText(Html.fromHtml(StringTextUtils.getHtmlStringWithColorElement(data.getAlbumname()+"("+data.getArtistname()+")","em","ff0000")));
    }



}
