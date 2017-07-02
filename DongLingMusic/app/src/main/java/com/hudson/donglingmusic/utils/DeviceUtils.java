package com.hudson.donglingmusic.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Hudson on 2017/6/15.
 */

public class DeviceUtils {

    /**
     * 手机振动
     * @param context 上下文
     * @param milliseconds 振动时长
     */
    public static void Vibrate(final Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context
                .getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
}
