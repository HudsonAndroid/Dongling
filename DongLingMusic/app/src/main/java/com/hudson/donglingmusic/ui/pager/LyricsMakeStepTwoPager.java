package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/4/1.
 */

public class LyricsMakeStepTwoPager extends BaseLyricsMakePager {
    private EditText mEditText;

    public LyricsMakeStepTwoPager(Activity context) {
        super(context);
    }

    @Override
    public View initView(Context context) {
        View view = View.inflate(context, R.layout.pager_lyricsmaker_two, null);
        mEditText = (EditText) view.findViewById(R.id.et_lyrics_content);
        return view;
    }

    /**
     * 获取编辑的歌词内容
     * @return
     */
    public String getLyricsContent(){
        return mEditText.getText().toString().trim();
    }
}
