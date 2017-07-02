package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/6/16.
 * 添加到歌单的Item
 */

public class AddGedanItemView extends LinearLayout {
    private ImageView mGedanBg;
    private TextView mGedanTitle;

    public AddGedanItemView(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.item_add_gedan,null);
        mGedanBg = (ImageView) view.findViewById(R.id.iv_gedan_bg);
        mGedanTitle = (TextView) view.findViewById(R.id.tv_gedan_title);
        view.findViewById(R.id.ll_add_gedan_item_root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnAddSongToGedanClickListener!=null){
                    mOnAddSongToGedanClickListener.onAddSongToGedan();
                }
            }
        });
        addView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void updateUI(String imagePath,String gedanTitle){
        mGedanTitle.setText(gedanTitle);
        if(!TextUtils.isEmpty(imagePath)){
            mGedanBg.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }else{
            mGedanBg.setImageResource(R.drawable.gedan_disk);
        }
    }

    private OnAddSongToGedanClickListener mOnAddSongToGedanClickListener;
    public void setOnAddSongToGedanClickListener(OnAddSongToGedanClickListener onAddSongToGedanClickListener) {
        mOnAddSongToGedanClickListener = onAddSongToGedanClickListener;
    }
    public interface OnAddSongToGedanClickListener{
        void onAddSongToGedan();
    }


}
