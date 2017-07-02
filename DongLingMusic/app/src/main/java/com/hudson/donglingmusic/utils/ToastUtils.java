package com.hudson.donglingmusic.utils;

import android.widget.Toast;

/**
 * Created by Hudson on 2017/3/19.
 * 让整体只有一个Toast
 */

public class ToastUtils {

    private static Toast mToast = Toast.makeText(UIUtils.getContext(),"",Toast.LENGTH_SHORT);

    public static void showToast(String info){
        mToast.setText(info);
        mToast.show();
    }

}
