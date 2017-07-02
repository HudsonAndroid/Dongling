package com.hudson.donglingmusic.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.FontSelectRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Hudson on 2017/4/8.
 * 歌词字体设置页面
 *      一旦本页面开启，就去app的字体目录下读取字体文件相关信息
 */

public class LyricsFontSelectorActivity extends BaseNormalActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<File> mFontsFiles;//存储了字体文件的名称
    private View mNoFontTipView;


    @Override
    public View initView() {
        setActivityTitle(getString(R.string.lyrics_font_setting));
        View v = View.inflate(this,R.layout.activity_lyrics_font_setting,null);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_fonts_list);
        mNoFontTipView = v.findViewById(R.id.ll_no_font_show);
        GridLayoutManager manager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(manager);
        initData();
        return v;
    }

    /**
     * 读取字体目录下所有的字体文件
     */
    private void initData() {
        mFontsFiles = new ArrayList<>();
        File files[] = new File(StorageUtils.getAppLyricsFontAbsolutePath()).listFiles();
        for(File f:files){
            if(f.isDirectory()){
                continue;
            }else{
                if(f.getName().endsWith(".ttf")|f.getName().endsWith(".TTF")){
                    mFontsFiles.add(f);
                }
            }
        }
        if(mFontsFiles!=null&&mFontsFiles.size()!=0){
            mRecyclerView.setAdapter(new FontSelectRecyclerViewAdapter(this, mFontsFiles));
        }else{
            mNoFontTipView.setVisibility(View.VISIBLE);
        }
    }
}
