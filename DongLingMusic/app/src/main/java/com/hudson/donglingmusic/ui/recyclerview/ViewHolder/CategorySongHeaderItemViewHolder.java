package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.CategorySongRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/21.
 * 歌曲分类的header
 */

public class CategorySongHeaderItemViewHolder extends BaseRecyclerViewHolder<ArrayList<String>> {
    private TextView mHot1,mHot2,mHot3,mHot4,mHot5,mHot6,mHot7,mHot8;
    private String mAppName;

    public CategorySongHeaderItemViewHolder(View itemView, final CategorySongRecyclerViewAdapter.OnItemClickListener listener) {
        super(itemView);
        mHot1 = (TextView) itemView.findViewById(R.id.tv_hot1);
        mAppName = UIUtils.getString(R.string.app_name);
        mHot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot1.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot2 = (TextView) itemView.findViewById(R.id.tv_hot2);
        mHot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot2.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot3 = (TextView) itemView.findViewById(R.id.tv_hot3);
        mHot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot3.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot4 = (TextView) itemView.findViewById(R.id.tv_hot4);
        mHot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot4.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot5 = (TextView) itemView.findViewById(R.id.tv_hot5);
        mHot5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot5.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot6 = (TextView) itemView.findViewById(R.id.tv_hot6);
        mHot6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot6.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot7 = (TextView) itemView.findViewById(R.id.tv_hot7);
        mHot7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot7.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
        mHot8 = (TextView) itemView.findViewById(R.id.tv_hot8);
        mHot8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String text = mHot8.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)&&!text.equals(mAppName)){
                        listener.onItemClick(text);
                    }
                }
            }
        });
    }

    @Override
    public void refreshView(ArrayList<String> data) {
        if(data == null|(data != null && data.size() == 0)){
            return ;
        }
        mHot1.setText(data.get(0));
        mHot2.setText(data.get(1));
        mHot3.setText(data.get(2));
        mHot4.setText(data.get(3));
        mHot5.setText(data.get(4));
        mHot6.setText(data.get(5));
        mHot7.setText(data.get(6));
        mHot8.setText(data.get(7));
    }
}
