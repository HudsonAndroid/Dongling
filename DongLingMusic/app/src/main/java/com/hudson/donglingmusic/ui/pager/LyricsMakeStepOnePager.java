package com.hudson.donglingmusic.ui.pager;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/4/1.
 */

public class LyricsMakeStepOnePager extends BaseLyricsMakePager {

    public LyricsMakeStepOnePager(Activity context) {
        super(context);
    }

    @Override
    public View initView(Context context) {
        return View.inflate(context, R.layout.pager_lyricsmaker_one,null);
    }
}
