package com.hudson.donglingmusic.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.WidgetUpdateService;

/**
 * Created by Hudson on 2017/5/2.
 * 桌面控件
 * 实际上是一个广播接受者
 *
 * 由于我们使用了一个服务来更新这个窗口小工具，但是如果我们的APP关闭了，而小工具
 * 还在桌面上，如果系统内存不足了，那么系统会把更新小工具的服务关闭，那随之而来的
 * 问题就会导致下次启动APP，而服务又被关闭了，所以导致这次窗口小工具不会随着app变化
 * 而变化。因此我们需要在widget创建的时候保存一个flag，说明widget有了，这时候，我们
 * 在启动app的时候检测一下这个flag，如果有，那么我们就重启服务(当然如果服务没有被
 * 销毁，直接使用原有的）。在销毁widget时，我们重设flag。
 */

public class DeskTopWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i  = new Intent(context,WidgetUpdateService.class);
        context.startService(i);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    //当第一个widget创建时回调
    @Override
    public void onEnabled(Context context) {
        Intent intent  = new Intent(context,WidgetUpdateService.class);
        context.startService(intent);
        //保存widget第一次被创建了
        MySharePreferences.getInstance().saveIsWidgetCreated(true);
        super.onEnabled(context);
    }

    //当最后一个widget被销毁时调用
    @Override
    public void onDisabled(Context context) {
        Intent intent  = new Intent(context,WidgetUpdateService.class);
        context.stopService(intent);
        //保存widget全部被销毁了
        MySharePreferences.getInstance().saveIsWidgetCreated(false);
        super.onDisabled(context);
    }
}
