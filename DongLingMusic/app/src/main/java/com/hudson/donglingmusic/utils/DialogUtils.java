package com.hudson.donglingmusic.utils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.itempager.MyGedanItemPager;
import com.hudson.donglingmusic.ui.view.AddGedanItemView;
import com.hudson.donglingmusic.ui.view.SelectProgressBar;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ALARM_SERVICE;
import static com.hudson.donglingmusic.R.id.bt_cancel;
import static com.hudson.donglingmusic.R.id.bt_ok;
import static com.hudson.donglingmusic.ui.itempager.MyGedanItemPager.MY_GEDAN_DATABASE;

/**
 * Created by Hudson on 2017/4/13.
 */

public class DialogUtils {

    public static final int TYPE_NORMAL_LYRICS_COLOR = 0;
    public static final int TYPE_FOCUS_LYRICS_COLOR = 1;
    public static final int TYPE_VISIBLE_CIRCLE_ADD = 2;
    public static final int TYPE_VISIBLE_CIRCLE_MODIFY = 3;
    public static final int TYPE_VISIBLE_BG_COLOR = 4;
    public static final int TYPE_MODIFY_GEDAN_TITLE = 5;
    public static final int TYPE_MODIFY_GEDAN_TAG = 6;
    public static final int TYPE_MODIFY_GEDAN_DESC = 7;


    /**
     * 颜色选择对话框
     * 使用了开源框架holoColorPicker
     * @param context
     * @param dialogTitle
     * @param oldColor
     * @param type
     * @param okRun
     */
    public static void showColorPickerDialog(Context context, String dialogTitle, int oldColor,
                                         final int type, final Runnable okRun,boolean showOldColor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_color_picker, null);
        ((TextView) view.findViewById(R.id.tv_dialog_title)).setText(dialogTitle);
        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        OpacityBar opacityBar = (OpacityBar) view.findViewById(R.id.opacitybar);
        SVBar svBar = (SVBar) view.findViewById(R.id.svbar);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.valuebar);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

        //To get the color
        picker.getColor();

        //To set the old selected color u can do it like this
        picker.setOldCenterColor(oldColor);
        //to turn of showing the old color
        picker.setShowOldCenterColor(showOldColor);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(type == TYPE_NORMAL_LYRICS_COLOR){
                    MySharePreferences.getInstance().saveNormalLyricsColor(picker.getColor());
                }else if(type == TYPE_FOCUS_LYRICS_COLOR){
                    MySharePreferences.getInstance().saveFocusLyricsColor(picker.getColor());
                }else if(type == TYPE_VISIBLE_CIRCLE_ADD){
                    MySharePreferences.getInstance().saveVisibleCircleAddColor(picker.getColor());
                }else if(type == TYPE_VISIBLE_CIRCLE_MODIFY){
                    MySharePreferences.getInstance().saveVisibleCircleModifyColor(picker.getColor());
                }else if(type == TYPE_VISIBLE_BG_COLOR){
                    MySharePreferences.getInstance().saveVisibleBgColor(picker.getColor());
                }
                dialog.dismiss();
                okRun.run();
            }
        });
//        dialog.setCancelable(false);// 设置不可通过返回键取消掉对话框。用于非常重要的对话框
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    //下载品质设置
    public static void showDownloadMusicTypeChooseDialog(Context context, int preCheckId, final Runnable okRun) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        // 自定义一个布局文件
        View view = View.inflate(context, R.layout.dialog_music_download_type, null);
        RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
        switch (preCheckId){
            case 64:
                group.check(R.id.type_64);
                break;
            case 128:
                group.check(R.id.type_128);
                break;
            case 256:
                group.check(R.id.type_256);
                break;
            case 320:
                group.check(R.id.type_320);
                break;
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.type_64:
                        MySharePreferences.getInstance().saveDownloadMusicType(64);
                        break;
                    case R.id.type_128:
                        MySharePreferences.getInstance().saveDownloadMusicType(128);
                        break;
                    case R.id.type_256:
                        MySharePreferences.getInstance().saveDownloadMusicType(256);
                        break;
                    case R.id.type_320:
                        MySharePreferences.getInstance().saveDownloadMusicType(320);
                        break;
                }
                dialog.dismiss();
                okRun.run();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }


    // 播放模式选择对话框
    public static void showPlayModeChooseDialog(Context context, int preCheckId, final Runnable okRun) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        // 自定义一个布局文件
        View view = View.inflate(context, R.layout.dialog_playmode_choose, null);
        RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
        switch (preCheckId){
            case MusicService.MODE_LIST_LOOP:
                group.check(R.id.list_loop);
                break;
            case MusicService.MODE_LIST_ORDER:
                group.check(R.id.order_play);
                break;
            case MusicService.MODE_ONE_LOOP:
                group.check(R.id.one_loop);
                break;
            case MusicService.MODE_LIST_SHUFFLE:
                group.check(R.id.shuffle);
                break;
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.one_loop:
                        MySharePreferences.getInstance().savePlayMode(MusicService.MODE_ONE_LOOP);
                        break;
                    case R.id.list_loop:
                        MySharePreferences.getInstance().savePlayMode(MusicService.MODE_LIST_LOOP);
                        break;
                    case R.id.shuffle:
                        MySharePreferences.getInstance().savePlayMode(MusicService.MODE_LIST_SHUFFLE);
                        break;
                    case R.id.order_play:
                        MySharePreferences.getInstance().savePlayMode(MusicService.MODE_LIST_ORDER);
                        break;
                }
                dialog.dismiss();
                okRun.run();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    // 歌曲信息对话框
    public static void showMusicInformationDialog(Context context,String title,String fileName,String singer,
                                                  String album,String filePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_music_information, null);
        ((TextView)(view.findViewById(R.id.tv_dialog_title))).setText(title);
        if(album.equals("<unknown>")){
            album = context.getString(R.string.music_info_unknown_album);
        }
        ((TextView) view.findViewById(R.id.tv_file_name)).setText(fileName);
        ((TextView) view.findViewById(R.id.tv_singer)).setText(singer);
        ((TextView) view.findViewById(R.id.tv_album)).setText(album);
        if(filePath.startsWith("http")){
            filePath = "抱歉，网络歌曲";
        }
        ((TextView) view.findViewById(R.id.tv_file_path)).setText(filePath);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    // 设置铃声对话框
    public static void showSetRingDialog(final Context context, final MusicInfo selectedMusic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_set_ring, null);
        final AlertDialog dialog = builder.create();
        view.findViewById(R.id.bt_alarm).setOnClickListener(
                new View.OnClickListener() {// 设置为闹铃
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        setRing(context,selectedMusic, RingtoneManager.TYPE_ALARM, false,
                                false, true, false, "设置闹铃成功！");
                    }
                });
        view.findViewById(R.id.bt_notification).setOnClickListener(
                new View.OnClickListener() {// 设置为通知铃声

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        setRing(context,selectedMusic, RingtoneManager.TYPE_NOTIFICATION,
                                false, true, false, false, "设置通知铃声成功！");
                    }
                });
        view.findViewById(R.id.bt_ringtone).setOnClickListener(
                new View.OnClickListener() {// 设置为来电铃声

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        setRing(context,selectedMusic, RingtoneManager.TYPE_RINGTONE, true,
                                false, false, false, "设置来电铃声成功！");
                    }
                });
        view.findViewById(R.id.bt_allring).setOnClickListener(
                new View.OnClickListener() {// 设置为所有铃声

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        setRing(context,selectedMusic, RingtoneManager.TYPE_ALL, false, false,
                                false, true, "设置为所有铃声成功！");
                    }
                });
        dialog.setView(view, 0, 0, 0, 0);// 为了在低版本上适配
        dialog.show();
    }

    // 设置铃声
    private static void setRing(Context context,MusicInfo musicInfo,int ringType, boolean isRingtone,
                         boolean isNotification, boolean isAlarm, boolean isMusic, String msg) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, musicInfo.getData());
        values.put(MediaStore.MediaColumns.TITLE, musicInfo.getTitle());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, isRingtone);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, isNotification);
        values.put(MediaStore.Audio.Media.IS_ALARM, isAlarm);
        values.put(MediaStore.Audio.Media.IS_MUSIC, isMusic);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(musicInfo.getData());// 获取系统音频文件的Uri
        Uri newUri = context.getContentResolver().insert(uri, values);// 将文件插入系统媒体库，并获取新的Uri
        RingtoneManager.setActualDefaultRingtoneUri(context, ringType, newUri);// 设置铃声
        ToastUtils.showToast(msg);
    }

    //定时时间段退出
    public static final String EXIT_BROADCAST = "com.hudson.donglingmusic.exit_broadcast";
    public static final int TYPE_EXIT_SONG_COUNT = 0;
    public static final int TYPE_EXIT_TIME_OFFSET = 1;
    public static final int TYPE_EXIT_TIME_POINT = 2;
    public static final int TYPE_EXIT_NONE = -1;

    // 定时退出选择对话框
    public static void showExitAtTimeSelectDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_exit_at_time, null);
        final AlertDialog dialog = builder.create();
        view.findViewById(R.id.bt_num).setOnClickListener(
                new View.OnClickListener() {// 定时歌曲数退出
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showExitTimeSongCountSelectDialog(context);
                    }
                });
        view.findViewById(R.id.bt_time).setOnClickListener(
                new View.OnClickListener() {// 定时时间段退出

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showExitTimeOffsetSelectDialog(context);
                    }
                });
        view.findViewById(R.id.bt_dealtime).setOnClickListener(
                new View.OnClickListener() {// 定时时间点退出

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showExitAtTimePointSelectDialog(context);
                    }
                });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    // 定时时间段退出选择时间对话框
    public static void showExitTimeOffsetSelectDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_exit_time_offset_select, null);
        final SelectProgressBar timeSelect = (SelectProgressBar) view.findViewById(R.id.spb_exit_select_time);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startAlarmExit(context,timeSelect.getSelectedValue());
                MySharePreferences.getInstance().saveExitAtTimeType(TYPE_EXIT_TIME_OFFSET);//保存类型
                ToastUtils.showToast("定时退出已启动！");
                dialog.dismiss();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    public static int downCountTime = 0;
    // 定时时间段退出剩余时间对话框

    /**
     * 定时时间段退出显示剩余时间对话框
     * @param context
     * @param leftTime 剩余时间 毫秒
     */
    public static void showExitTimeOffsetTimeCountDownDialog(final Context context, final int leftTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_exit_time_offset_time_countdown, null);
        final TextView timer = (TextView) view.findViewById(R.id.timer);
        timer.setText(TimeUtils.toTime(leftTime));
        downCountTime = leftTime;//剩余时间
        final Timer countTimer = new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                downCountTime -= 1000;
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.setText(TimeUtils.toLargeTime(downCountTime));
                    }
                });
            }
        };
        countTimer.schedule(task,0,1000);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //取消该定时退出
                dialog.dismiss();
                cancelExitAtTimeOffset(context);
                ToastUtils.showToast("定时退出已取消！");
            }
        });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(task!=null){
                    task.cancel();
                    countTimer.cancel();
                }
            }
        });
//        dialog.setCancelable(false);// 设置不可通过返回键取消掉对话框。用于非常重要的对话框,原因在于task未经过ok和cancel的话没法取消
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    /**
     * 取消定时退出，包括时间点和时间段
     * @param context
     */
    public static void cancelExitAtTimeOffset(Context context){
        MySharePreferences instance = MySharePreferences.getInstance();
        int exitAtTimeType = instance.getExitAtTimeType();
        if(exitAtTimeType ==TYPE_EXIT_TIME_OFFSET|exitAtTimeType == TYPE_EXIT_TIME_POINT){
            //注意取消闹钟的参数必须与设置时一模一样
            Intent intent = new Intent(EXIT_BROADCAST);
            PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm=(AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarm.cancel(pi);
            instance.saveExitAtTimeType(TYPE_EXIT_NONE);
        }
    }

    // 定时歌曲数退出选择时间对话框
    public static final String EXIT_SONG_COUNT_BROADCAST = "com.hudson.dongling.music.exit_song_count";
    public static void showExitTimeSongCountSelectDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_exit_time_song_count_select, null);
        final SelectProgressBar songCountSelect = (SelectProgressBar) view.findViewById(R.id.spb_exit_select_count);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MySharePreferences instance = MySharePreferences.getInstance();
                instance.saveExitAtTimeSongCount(songCountSelect.getSelectedValue());
                Intent exitSongCountIntent = new Intent(EXIT_SONG_COUNT_BROADCAST);
                context.sendBroadcast(exitSongCountIntent);
                instance.saveExitAtTimeType(TYPE_EXIT_SONG_COUNT);//保存类型
                ToastUtils.showToast("定时退出已启动！");
                dialog.dismiss();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    /**
     * 信息对话框
     * @param context 上下文
     * @param dialogTitle 对话框标题
     * @param content 对话框内容（信息）
     * @param okText 确定按钮的提示
     * @param cancelText 取消按钮的提示
     * @param cancelRun 点击取消时的响应
     * @param okRun 点击确定时的响应
     */
    public static void showInformationDialog(final Context context, String dialogTitle, String content, String okText, String cancelText, final Runnable cancelRun,final Runnable okRun) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_information, null);
        ((TextView) view.findViewById(R.id.tv_dialog_title)).setText(dialogTitle);
        ((TextView) view.findViewById(R.id.tv_dialog_content)).setText(content);
        final Button cancelBtn = (Button) view.findViewById(bt_cancel);
        cancelBtn.setText(cancelText);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(cancelRun!=null){
                    cancelRun.run();
                }
            }
        });
        Button okBtn = (Button) view.findViewById(bt_ok);
        okBtn.setText(okText);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(okRun!=null){
                    okRun.run();
                }
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }


    public static final String DELETE_SONG_BROADCAST = "com.hudson.donglingMusic.deleteSong";
    /**
     * 删除对话框
     * @param context 上下文
     * @param dialogTitle 对话框标题
     * @param content 对话框内容（信息）
     * @param okText 确定按钮的提示
     * @param cancelText 取消按钮的提示
     * @param deleteMusic 需要删除的歌曲
     * @param listInfo 所属歌单
     */
    public static void showDeleteSongDialog(final Context context, String dialogTitle, final String content, String okText, String cancelText, final MusicInfo deleteMusic, final String listInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_delete_with_check, null);
        ((TextView) view.findViewById(R.id.tv_dialog_title)).setText(dialogTitle);
        ((TextView) view.findViewById(R.id.tv_dialog_content)).setText(content);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_check);
        final Button cancelBtn = (Button) view.findViewById(bt_cancel);
        cancelBtn.setText(cancelText);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button okBtn = (Button) view.findViewById(bt_ok);
        okBtn.setText(okText);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent deleteIntent = new Intent(DELETE_SONG_BROADCAST);
                deleteIntent.putExtra("musicInfo",deleteMusic);
                deleteIntent.putExtra("deleteFileOrNot",checkBox.isChecked());
                deleteIntent.putExtra("listInfo",listInfo);
                context.sendBroadcast(deleteIntent);
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    /**
     * 取消歌曲数目定时退出
     * @param context
     */
    public static void cancelExitSongCount(Context context){
        MySharePreferences instance = MySharePreferences.getInstance();
        if(instance.getExitAtTimeType() == TYPE_EXIT_SONG_COUNT){
            MySharePreferences.getInstance().saveExitAtTimeSongCount(-1);
            Intent exitSongCountIntent = new Intent(EXIT_SONG_COUNT_BROADCAST);
            context.sendBroadcast(exitSongCountIntent);
            instance.saveExitAtTimeType(TYPE_EXIT_NONE);
        }
    }

    /**
     * 开启一个系统闹钟，来定时
     * @param context
     * @param offsetTime 分钟，当前时间到退出时间的时间差
     */
    public static void startAlarmExit(Context context,int offsetTime){
        Intent exitIntent = new Intent(EXIT_BROADCAST);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,exitIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        long exitTime = System.currentTimeMillis() +offsetTime*60*1000;
        MySharePreferences instance = MySharePreferences.getInstance();
        instance.saveExitTimeOffsetExitTime(exitTime);//保存退出的时间
        if(Build.VERSION.SDK_INT>=19){//在大于19的版本中，google为了节约电量，不保证set和setRepeating的精确性
            am.setExact(AlarmManager.RTC_WAKEUP,
                    exitTime,pi);
        }else{
            am.set(AlarmManager.RTC_WAKEUP,
                    exitTime,pi);
        }
    }

    // 定时时间点退出
    protected static void showExitAtTimePointSelectDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_exit_time_point, null);
        final TimePicker time_choose = (TimePicker) view
                .findViewById(R.id.time_choose);
        time_choose.setIs24HourView(true);// 设置24小时
        view.findViewById(bt_cancel).findViewById(bt_cancel)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 设置退出时间点
                int chooseHour = time_choose.getCurrentHour();
                int chooseMinute = time_choose.getCurrentMinute();
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY); // 必须放在内部，因为时间问题
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);
                int offset = 0,minuteOffset,hourOffset;
                if(chooseMinute < minute){
                    chooseHour --;
                    minuteOffset = chooseMinute + 60 - minute;
                }else{
                    minuteOffset = chooseMinute - minute;
                }
                if(chooseHour<hour){
                    chooseHour += 24;
                }
                hourOffset = chooseHour - hour;
                offset = hourOffset*60 + minuteOffset;
                if(offset<60*5){
                    startAlarmExit(context,offset);
                    MySharePreferences instance = MySharePreferences.getInstance();
                    instance.saveExitAtTimeType(TYPE_EXIT_TIME_POINT);//保存类型
                    instance.saveExitTimePoint(String.format(" %02d:%02d:%02d ",chooseHour, chooseMinute,second));
                    ToastUtils.showToast("定时退出已启动！");
                    dialog.dismiss();
                }else{
                    ToastUtils.showToast("您设置的时间过长！");
                }
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    public static void showModifyLyricsDialog(Context context, final String srcLyrics, final Runnable okRun){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_modify_lrc, null);
        final EditText modifyEdit = (EditText) view.findViewById(R.id.et_modify);
        modifyEdit.setText(srcLyrics);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String modifyLrc = modifyEdit.getText().toString().trim();
                if(TextUtils.isEmpty(modifyLrc)){
                    ToastUtils.showToast(UIUtils.getString(R.string.lrc_should_not_empty));
                }else{
                    if(modifyLrc.equals(srcLyrics)){
                        ToastUtils.showToast(UIUtils.getString(R.string.lrc_not_modify));
                    }else{
                        dialog.dismiss();
                        MySharePreferences.getInstance().saveModifyLyrics(modifyLrc);
                        if(okRun!=null){
                            okRun.run();
                        }
                    }
                }
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    public static void showEditInfoDialog(Context context, final int type, final String emptyTip, String title,
                                          String srcText,String hint, final Runnable okRun){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_modify_lrc, null);
        final EditText modifyEdit = (EditText) view.findViewById(R.id.et_modify);
        if(!TextUtils.isEmpty(srcText)){
            modifyEdit.setText(srcText);
        }
        modifyEdit.setHint(hint);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView)view.findViewById(R.id.title_dialog)).setText(title);
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String modifyLrc = modifyEdit.getText().toString().trim();
                if(TextUtils.isEmpty(modifyLrc)){
                    ToastUtils.showToast(emptyTip);
                }else{
                    dialog.dismiss();
                    MySharePreferences instance = MySharePreferences.getInstance();
                    if(type == TYPE_MODIFY_GEDAN_TITLE){
                        instance.saveNewGedanTitle(modifyLrc);
                    }else if(type == TYPE_MODIFY_GEDAN_TAG){
                        instance.saveNewGedanTag(modifyLrc);
                    }
                    if(okRun!=null){
                        okRun.run();
                    }
                }
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }


    public static void showEditBigInfoDialog(Context context, final int type, final String emptyTip, String title,
                                             String srcText,String hint, final Runnable okRun){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_modify_big_info, null);
        final EditText modifyEdit = (EditText) view.findViewById(R.id.et_modify);
        if(!TextUtils.isEmpty(srcText)){
            modifyEdit.setText(srcText);
        }
        modifyEdit.setHint(hint);
        view.findViewById(bt_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView)view.findViewById(R.id.title_dialog)).setText(title);
        view.findViewById(bt_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String modifyLrc = modifyEdit.getText().toString().trim();
                if(TextUtils.isEmpty(modifyLrc)){
                    ToastUtils.showToast(emptyTip);
                }else{
                    dialog.dismiss();
                    MySharePreferences instance = MySharePreferences.getInstance();
                    if(type == TYPE_MODIFY_GEDAN_DESC){
                        instance.saveNewGedanTitle(modifyLrc);
                    }
                    if(okRun!=null){
                        okRun.run();
                    }
                }
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }


    /**
     * 添加到歌单对话框
     * @param context activity的上下文
     * @param musicInfo 添加的实例
     * @param newGedanRun 如果点击了新建歌单时的处理
     * @param successfulTips 添加成功的提示
     */
    public static void showGedanChooseDialog(Context context, final MusicInfo musicInfo,
                                             final Runnable newGedanRun,
                                             final String successfulTips) {
        //查询数据库，看有哪些用户歌单
        List<MyGedanBean> userGedans = new ArrayList<>();
        LitePalDB litePalDB = new LitePalDB(MY_GEDAN_DATABASE, 1);
        litePalDB.addClassName(MyGedanBean.class.getName());
        LitePal.use(litePalDB);
        List<MyGedanBean> myAllGedanBeen = DataSupport.findAll(MyGedanBean.class);
        if(myAllGedanBeen!=null&&myAllGedanBeen.size()!=0){
            for (MyGedanBean myGedanBean : myAllGedanBeen) {
                if(myGedanBean.getNetGedanId() == null){
                    userGedans.add(myGedanBean);
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_gedanlist_choose, null);
        view.findViewById(R.id.tv_new_gedan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(newGedanRun!=null){
                    newGedanRun.run();
                }
            }
        });
        LinearLayout container = (LinearLayout) view.findViewById(R.id.ll_gedan_container);
        AddGedanItemView itemView;
        MyGedanBean gedanBean;
        for (int i = 0; i < userGedans.size(); i++) {
            gedanBean = userGedans.get(i);
            itemView = new AddGedanItemView(context);
            itemView.updateUI(gedanBean.getImagePath(),gedanBean.getTitle());
            final String databaseName = gedanBean.getDatabaseName();
            itemView.setOnAddSongToGedanClickListener(new AddGedanItemView.OnAddSongToGedanClickListener() {
                @Override
                public void onAddSongToGedan() {
                    dialog.dismiss();
                    LitePalDB litePalDB = new LitePalDB(MyGedanItemPager.getMyGedanDatabaseName(databaseName), 1);
                    litePalDB.addClassName(MusicInfo.class.getName());
                    LitePal.use(litePalDB);
                    List<MusicInfo> musics = DataSupport.findAll(MusicInfo.class);
                    for (MusicInfo music : musics) {
                        if(music.equals(musicInfo)){
                            ToastUtils.showToast("歌单已经存在该歌曲！");
                            return ;
                        }
                    }
                    musicInfo.save();
                    ToastUtils.showToast(successfulTips);
                }
            });
            container.addView(itemView,new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

}
