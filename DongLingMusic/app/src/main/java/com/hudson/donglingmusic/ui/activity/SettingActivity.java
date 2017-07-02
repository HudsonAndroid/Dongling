package com.hudson.donglingmusic.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.ToastUtils;

/**
 * Created by Hudson on 2017/4/20.
 * 设置界面
 */

public class SettingActivity extends BaseNormalActivity implements View.OnClickListener {
    private MySharePreferences mInstance;
    private TextView mDownloadType;
    private CheckedTextView mShowDeskTopLyrics;
    private CheckedTextView mHeadsetPlugPause;
    private CheckedTextView mTelephonyCompleteContinue;
    private CheckedTextView mExitSongInfoSave;
    private CheckedTextView mExitSongProgressSave;
    private ImageView mDefaultBgPreview;

    @Override
    public View initView() {
        setActivityTitle(getString(R.string.setting));
        View v = View.inflate(this,R.layout.activity_setting,null);
        v.findViewById(R.id.ll_download_music_type).setOnClickListener(this);
        v.findViewById(R.id.ll_headset_wire_button).setOnClickListener(this);
        v.findViewById(R.id.ll_app_theme_bg).setOnClickListener(this);
        mInstance = MySharePreferences.getInstance();
        mDownloadType = (TextView) v.findViewById(R.id.tv_music_type);
        mDefaultBgPreview = (ImageView) v.findViewById(R.id.iv_default_bg_preview);
        BitmapMusicUtils.setViewDefaultBackground(this,mDefaultBgPreview);
        mShowDeskTopLyrics = (CheckedTextView) v.findViewById(R.id.ctv_show_desktop_lyrics);
        mHeadsetPlugPause = (CheckedTextView) v.findViewById(R.id.ctv_headset_plug_pause);
        mTelephonyCompleteContinue = (CheckedTextView) v.findViewById(R.id.ctv_phone_complete_continue_play);
        mExitSongInfoSave = (CheckedTextView) v.findViewById(R.id.ctv_exit_save_song_info);
        mExitSongProgressSave = (CheckedTextView) v.findViewById(R.id.ctv_exit_save_song_progress);
        initData();
        return v;
    }

    private void initData() {
        mShowDeskTopLyrics.setChecked(mInstance.getShowDeskTopLyrics());
        mHeadsetPlugPause.setChecked(mInstance.getIsHeadsetPlugPause());
        mTelephonyCompleteContinue.setChecked(mInstance.getIsTelephonyCompleteContinue());
        mExitSongInfoSave.setChecked(mInstance.getExitSongInfoOrNot());
        mExitSongProgressSave.setChecked(mInstance.getExitSongProgressOrNot());
        mShowDeskTopLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = !mShowDeskTopLyrics.isChecked();
                if(Build.VERSION.SDK_INT>23){
                    if(checked){
                        if(!Settings.canDrawOverlays(SettingActivity.this)){//如果权限未获取，需要开启activity请求权限
                            requestAlertWindowPermission();
                        }else{
                            saveAlertWindowPermissionState(checked);
                        }
                    }else{
                        saveAlertWindowPermissionState(checked);
                    }
                }else{//版本低于6.0无需判断
                    saveAlertWindowPermissionState(checked);
                }
            }
        });
        mHeadsetPlugPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPause = !mHeadsetPlugPause.isChecked();
                mHeadsetPlugPause.setChecked(isPause);
                mInstance.saveIsHeadsetPlugPause(isPause);
            }
        });
        mTelephonyCompleteContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isContinue = !mTelephonyCompleteContinue.isChecked();
                mTelephonyCompleteContinue.setChecked(isContinue);
                mInstance.saveIsTelephonyCompleteContinue(isContinue);
            }
        });
        mExitSongInfoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSave = !mExitSongInfoSave.isChecked();
                mExitSongInfoSave.setChecked(isSave);
                mInstance.saveExitSongInfoOrNot(isSave);
            }
        });
        mExitSongProgressSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSave = !mExitSongProgressSave.isChecked();
                mExitSongProgressSave.setChecked(isSave);
                mInstance.saveExitSongProgressOrNot(isSave);
            }
        });
        updateDownloadTypeTipTextView();
    }

    private void saveAlertWindowPermissionState(boolean checked){
        mShowDeskTopLyrics.setChecked(checked);
        mInstance.saveShowDeskTopLyrics(checked);
    }

    //请求系统级窗体的权限
    private static final int REQUEST_CODE = 3;
    private  void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT>23&&Settings.canDrawOverlays(this)) {
                saveAlertWindowPermissionState(!mShowDeskTopLyrics.isChecked());
            }
        }else if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
            Uri uri = data.getData();//得到返回的data数据
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String imagePath = cursor.getString(columnIndex);
            if(imagePath == null){
                ToastUtils.showToast("图片选取失败，可能选取结果不兼容，请更换选取方式！");
            }else{
                if(imagePath.endsWith("png")|imagePath.endsWith("jpg")|imagePath.endsWith("JPG")|
                        imagePath.endsWith("BMP")|imagePath.endsWith("jpeg")|imagePath.endsWith("JPEG")){
                    try{
                        if(!TextUtils.isEmpty(imagePath)){
                            mDefaultBgPreview.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(imagePath)));
                            mInstance.saveDefaultBgPath(imagePath);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        mInstance.saveDefaultBgPath(null);
                        ToastUtils.showToast("背景图片设置失败，请检查文件是否是图片！");
                    }
                }else {
                    ToastUtils.showToast("所选文件并非图片！");
                }
            }
            //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
            if(Integer.parseInt(Build.VERSION.SDK) < 14)
            {
                cursor.close();
            }
        }
    }

    private void updateDownloadTypeTipTextView(){
        switch (mInstance.getDownloadMusicType()){
            case 64:
                mDownloadType.setText("中等");
                break;
            case 128:
                mDownloadType.setText("标准");
                break;
            case 256:
                mDownloadType.setText("高等");
                break;
            case 320:
                mDownloadType.setText("极品");
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_download_music_type://下载音质设置
                DialogUtils.showDownloadMusicTypeChooseDialog(this, mInstance.getDownloadMusicType(), new Runnable() {
                    @Override
                    public void run() {
                        updateDownloadTypeTipTextView();
                    }
                });
                break;
            case R.id.ll_app_theme_bg://修改背景
                Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getImageIntent.setType("image/*");
                getImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(getImageIntent,1);
                break;
            case R.id.ll_headset_wire_button:
                startActivity(new Intent(SettingActivity.this,HeadsetSettingActivity.class));
                break;
        }
    }

}
