package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的加载更多viewHolder
 */

public class SongListBottomViewHolder extends BaseRecyclerViewHolder<Integer> {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_NO_MORE = 2;
    private View mLoadingMoreTipView;
    private TextView mLoadMoreDataTipView;

    public SongListBottomViewHolder(View itemView) {
        super(itemView);
        mLoadingMoreTipView = itemView.findViewById(R.id.ll_loading_more);
        mLoadMoreDataTipView = (TextView) itemView.findViewById(R.id.tv_no_more_data_tip);
    }

    @Override
    public void refreshView(Integer data) {
        switch (data){
            case STATUS_NONE:
                mLoadingMoreTipView.setVisibility(View.INVISIBLE);
                mLoadMoreDataTipView.setVisibility(View.VISIBLE);
                break;
            case STATUS_LOADING:
                mLoadingMoreTipView.setVisibility(View.VISIBLE);
                mLoadMoreDataTipView.setVisibility(View.INVISIBLE);
                break;
            case STATUS_NO_MORE:
                mLoadingMoreTipView.setVisibility(View.INVISIBLE);
                mLoadMoreDataTipView.setText(UIUtils.getString(R.string.no_more_data));
                mLoadMoreDataTipView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
