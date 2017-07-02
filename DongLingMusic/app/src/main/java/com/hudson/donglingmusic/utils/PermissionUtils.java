package com.hudson.donglingmusic.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Hudson on 2017/3/15.
 */

public class PermissionUtils {

    public static void requestPermission(Activity activity,String permission,int requestCode,Runnable runnable){
        if (!(ContextCompat.checkSelfPermission(UIUtils.getContext(),permission) == PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(UIUtils.getContext(), "Please grant the permission this time", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
        } else {//权限允许时的操作
            if(runnable!=null)
            runnable.run();
        }
    }
}
