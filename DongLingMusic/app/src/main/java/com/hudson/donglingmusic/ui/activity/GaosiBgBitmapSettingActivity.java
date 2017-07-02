package com.hudson.donglingmusic.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bitmapblurjni.StackBlur;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.view.SelectProgressBar;

/**
 * Created by Hudson on 2017/4/12.
 * playPage高斯模糊图片处理设置页面
 */

public class GaosiBgBitmapSettingActivity extends BaseNormalActivity {
    private SelectProgressBar mRadiusProgressBar;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private MySharePreferences mInstance;

    @Override
    public View initView() {
        setActivityTitle(getString(R.string.gaosi_radius_setting));
        View v = View.inflate(this, R.layout.activity_gaosibitmap_setting,null);
        mRadiusProgressBar = (SelectProgressBar) v.findViewById(R.id.spb_gaosi_radius);
        mImageView = (ImageView) v.findViewById(R.id.iv_preview_gaosi);
        mInstance = MySharePreferences.getInstance();
        int preRadius = mInstance.getGaosiRadius();
        mRadiusProgressBar.setSelectedValue(preRadius);
        updateRadiusToBitmap(preRadius);
        mRadiusProgressBar.setOnValueSelectedListener(new SelectProgressBar.OnValueSelectedListener() {
            @Override
            public void onValueSelected(int selectedValue) {
                updateRadiusToBitmap(selectedValue);
                mInstance.saveGaosiRadius(selectedValue);
            }
        });
        return v;
    }

    /**
     * 避免阻塞主线程
     * @param radius
     */
    private void updateRadiusToBitmap(final int radius){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                mBitmap = StackBlur.blurNatively(BitmapFactory.decodeResource(getResources(),
                        R.drawable.bg_activity_splash),radius,true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(mBitmap !=null){
                    mImageView.setImageBitmap(mBitmap);
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        if(mBitmap!=null){
            mBitmap.recycle();
        }
        super.onDestroy();
    }


}
