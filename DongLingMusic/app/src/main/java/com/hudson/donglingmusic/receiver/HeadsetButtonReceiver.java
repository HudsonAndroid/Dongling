package com.hudson.donglingmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by Hudson on 2017/6/22.
 * 这个广播有点特殊：它需要通过AudioManager对象注册； 该广播必须在AndroidManifest.xml文件中进行声明，否则就监听不到该MEDIA_BUTTON广播了
 */

public class HeadsetButtonReceiver extends BroadcastReceiver {
    public static final String MEDIA_BUTTON_CLICK_TIMES_BROADCAST = "com.hudson.donglingmusic.media_button_click";
    public static int mClickTimes = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyEvent event =  intent
                .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            return;
        }
        Intent clickIntent = new Intent(MEDIA_BUTTON_CLICK_TIMES_BROADCAST);
        int keycode = event.getKeyCode();
        int event_action = event.getAction();
        if(keycode == 86&&event_action == 1){//长按
            clickIntent.putExtra("isLongClick", true);
        }else if(event_action == 1){//抬一次发一次
            mClickTimes+=1;
        }
        context.sendBroadcast(clickIntent);
    }
}
