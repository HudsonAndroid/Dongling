package com.hudson.donglingmusic.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.activity.AboutActivity;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.activity.SettingActivity;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.ToastUtils;

/**
 * Created by Hudson on 2017/3/15.
 * 侧边栏的fragment
 * 注意：我们的侧边栏是在一个left_menu上盖了一层fragment
 */

public class LeftMenuFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mMode;//播放模式
    private IDonglingMusicAidlInterface mInterface;
    private MySharePreferences mInstance;
    private PlayModeChangeReceiver mReceiver;

    @Override
    public View initView(LayoutInflater inflater) {
        View inflate = inflater.inflate(R.layout.fragment_left_menu, null);
        mMode = (ImageView) inflate.findViewById(R.id.iv_mode);
        inflate.findViewById(R.id.ll_play_mode).setOnClickListener(this);
        inflate.findViewById(R.id.ll_equalizer).setOnClickListener(this);
        inflate.findViewById(R.id.ll_exit_timing).setOnClickListener(this);
        inflate.findViewById(R.id.ll_setting).setOnClickListener(this);
        inflate.findViewById(R.id.ll_about).setOnClickListener(this);
        inflate.findViewById(R.id.ll_exit).setOnClickListener(this);
        mInstance = MySharePreferences.getInstance();
        mReceiver = new PlayModeChangeReceiver();
        IntentFilter filter = new IntentFilter(MusicService.ACTION_PLAY_MODE_CHANGE);
        mActivity.registerReceiver(mReceiver,filter);
        return inflate;
    }

    @Override
    public void initData() {

    }

    public void onMusicServiceConnected(){
        if(mInterface == null){
            mInterface = ((HomeActivity)mActivity).getInterface();
            if(mInterface!=null)
                updateModeImage();
        }
    }

    /**
     * 刷新侧边栏的播放模式图片
     */
    private void updateModeImage(){
        try {
            switch (mInterface.getPlayMode()){
                case MusicService.MODE_LIST_LOOP:
                    mMode.setImageResource(R.drawable.mode_menu_list_loop);
                    break;
                case MusicService.MODE_LIST_ORDER:
                    mMode.setImageResource(R.drawable.mode_menu_list_order);
                    break;
                case MusicService.MODE_LIST_SHUFFLE:
                    mMode.setImageResource(R.drawable.mode_menu_list_shuffle);
                    break;
                case MusicService.MODE_ONE_LOOP:
                    mMode.setImageResource(R.drawable.mode_menu_one_loop);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_play_mode:
                DialogUtils.showPlayModeChooseDialog(mActivity,
                        mInstance.getPlayMode(),
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mInterface.setPlayMode(mInstance.getPlayMode());
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                break;
            case R.id.ll_equalizer:
                ToastUtils.showToast("开发者较懒，本功能不想再处理，谢谢您的支持！");
                break;
            case R.id.ll_exit_timing:
                int exitAtTimeType = mInstance.getExitAtTimeType();
                if(exitAtTimeType !=DialogUtils.TYPE_EXIT_NONE){
                    switch (exitAtTimeType){
                        case DialogUtils.TYPE_EXIT_SONG_COUNT:
                            DialogUtils.showInformationDialog(mActivity, getString(R.string.dialog_exit_song_num), "还需要播放" + mInstance.getExitAtTimeSongCount() + "首歌曲关闭应用", getString(R.string.dialog_button_i_know), getString(R.string.dialog_button_cancel_exit), new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.cancelExitSongCount(mActivity);
                                    ToastUtils.showToast("定时退出已取消！");
                                }
                            },null);
                            break;
                        case DialogUtils.TYPE_EXIT_TIME_OFFSET:
                            DialogUtils.showExitTimeOffsetTimeCountDownDialog(mActivity,
                                    (int) (mInstance.getExitTimeOffsetExitTime() - System.currentTimeMillis()));
                            break;
                        case DialogUtils.TYPE_EXIT_TIME_POINT:
                            DialogUtils.showInformationDialog(mActivity, getString(R.string.dialog_exit_time_point),
                                    "已设置了在" + mInstance.getExitTimePoint()+"时刻退出",
                                    getString(R.string.dialog_button_i_know),
                                    getString(R.string.dialog_button_cancel_exit), new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.cancelExitAtTimeOffset(mActivity);
                                    ToastUtils.showToast("定时退出已取消！");
                                }
                            },null);
                            break;
                    }
                }else{//没有开启定时退出
                    DialogUtils.showExitAtTimeSelectDialog(mActivity);
                }
                break;
            case R.id.ll_setting:
                startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.ll_about:
                startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            case R.id.ll_exit:
                mActivity.finish();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mActivity.unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    class PlayModeChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(mInterface!=null)
                updateModeImage();
        }
    }

}
