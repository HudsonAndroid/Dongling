package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.FontSelectRecyclerViewAdapter;

import java.io.File;

/**
 * Created by Hudson on 2017/4/9.
 * 字体的ViewHolder
 */

public class FontSelectViewHolder extends BaseRecyclerViewHolder<File> {
    private TextView mPreviewText,mFontTitle;
    private View mRootView;
    private ImageView mSelected;
    private String mFontPath;

    public FontSelectViewHolder(View itemView,
                                final FontSelectRecyclerViewAdapter.OnItemClickListener listener) {
        super(itemView);
        mPreviewText = (TextView) itemView.findViewById(R.id.tv_fonts_preview);
        mFontTitle = (TextView) itemView.findViewById(R.id.tv_fonts_title);
        mRootView = itemView.findViewById(R.id.rl_font_root);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(v,getLayoutPosition());
                }
                MySharePreferences.getInstance().saveFontFilePath(mFontPath);
            }
        });
        mSelected = (ImageView) itemView.findViewById(R.id.iv_font_selected);
    }

    @Override
    public void refreshView(File data) {
        mPreviewText.setTypeface(Typeface.createFromFile(data));
        mFontPath = data.getAbsolutePath();
        String fontName = data.getName();
        if(fontName.contains(".ttf")){
            fontName = fontName.replace(".ttf","");
        }else if(fontName.contains(".TTF")){
            fontName = fontName.replace(".TTF","");
        }
        mFontTitle.setText(fontName);
    }

    public void refreshSelectedItem(boolean selected){
        if(selected){
            mSelected.setVisibility(View.VISIBLE);
        }else{
            mSelected.setVisibility(View.INVISIBLE);
        }
    }
}
