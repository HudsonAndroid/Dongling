package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/3/22.
 * 此处使用自定义state来显示不同状态下的背景，自定义的状态有：item的选中、item的正在播放
 */

public class MusicListItemLayout extends RelativeLayout {
    //创建属性集
    private static final int[] STATE_ITEM_SELECTED = {R.attr.state_selected};
    private static final int[] STATE_ITEM_PLAYING = {R.attr.state_playing};
    private static final int[] STATE_ITEM_SELECTED_PLAYING = {R.attr.state_playing,R.attr.state_selected};

    private boolean mItemSelected = false;
    private boolean mItemPlaying = false;

    public MusicListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置Item为选中
     * @param selected
     */
    public void setStateItemSelected(boolean selected){
        if(mItemSelected != selected){
            mItemSelected = selected;
            refreshDrawableState();
        }
    }

    public void setStateItemPlaying(boolean playing){
        if(mItemPlaying!= playing){
            mItemPlaying = playing;
            refreshDrawableState();
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if(mItemPlaying&&mItemSelected){
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
            mergeDrawableStates(drawableState, STATE_ITEM_SELECTED_PLAYING);
            return drawableState;
        }
        if(mItemPlaying){
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState, STATE_ITEM_PLAYING);
            return drawableState;
        }
        if(mItemSelected){
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState, STATE_ITEM_SELECTED);
            return drawableState;
        }
        return super.onCreateDrawableState(extraSpace);
    }
}
