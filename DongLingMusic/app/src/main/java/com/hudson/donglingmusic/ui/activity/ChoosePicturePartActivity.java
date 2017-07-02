package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.view.PicturePartPickView;

public class ChoosePicturePartActivity extends Activity {
    private PicturePartPickView mPicturePartPickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture_part);
        Intent intent = getIntent();
        String srcImagePath = intent.getStringExtra("srcImagePath");
        final String title = intent.getStringExtra("title");
        if(!TextUtils.isEmpty(srcImagePath)){
            mPicturePartPickView = (PicturePartPickView) this.findViewById(R.id.ppp_view);
            mPicturePartPickView.setImageFilePath(srcImagePath);
            findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backIntent = new Intent();
                    backIntent.putExtra("path",mPicturePartPickView.save(title));
                    setResult(RESULT_OK,backIntent);//RESULT_OK是在Activity原系统源码中定义的
                    finish();
                }
            });
        }else{
            finish();
        }
    }

}
