package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/6/4.
 * 历史搜索的一项
 * 这里使用一个类来包装xml布局
 */

public class SearchHistoryItemView extends LinearLayout {
    private TextView mSearchHistoryTitle;
    private String mQuery;


    public SearchHistoryItemView(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.item_search_history,null);
        mSearchHistoryTitle = (TextView) view.findViewById(R.id.tv_search_history_title);
        view.findViewById(R.id.iv_delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickHappenedListener!=null){
                    mOnClickHappenedListener.onDeleteClick(mQuery);
                }
            }
        });
        view.findViewById(R.id.ll_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickHappenedListener!=null){
                    mOnClickHappenedListener.onViewClick(mQuery);
                }
            }
        });
        //这一步别忘了（我们这个类是xml布局的父布局）
        addView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setText(String query){
        mQuery = query;
        mSearchHistoryTitle.setText(query);
    }


    private OnClickHappenedListener mOnClickHappenedListener;

    public OnClickHappenedListener getOnClickHappenedListener() {
        return mOnClickHappenedListener;
    }

    public void setOnClickHappenedListener(OnClickHappenedListener onClickHappenedListener) {
        mOnClickHappenedListener = onClickHappenedListener;
    }

    public interface OnClickHappenedListener{
        void onViewClick(String query);
        void onDeleteClick(String query);
    }

}
