package com.hudson.donglingmusic.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.hudson.donglingmusic.global.MySharePreferences;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Hudson on 2017/3/26.
 */

public class BitmapMusicUtils {

    /**
     * 获取专辑封面位图对象
     *
     * @param title 歌曲的标题
     * @param albumId 专辑Id
     * @param containerWidth 目标View的宽度
     * @param containerHeight 目标View的高度
     * @return
     */
    public static Bitmap getArtwork(String title,long albumId,int containerWidth,int containerHeight) {
        Context context = UIUtils.getContext();
        //从系统数据库中获取
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
                albumId);
        if (uri != null) {
            InputStream in = null;
            ContentResolver contentResolver = context.getContentResolver();
            try {
                //对图片进行缩放处理
                in = contentResolver.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                int scaleX = options.outWidth/containerWidth;
                int scaleY = options.outHeight/containerHeight;
                options.inSampleSize = Math.max(Math.max(scaleX,scaleY),1);
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = contentResolver.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (Exception e) {
                return getAppMusicPicFromDirectory(title,containerWidth,containerHeight);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return getAppMusicPicFromDirectory(title,containerWidth,containerHeight);
    }

    /**
     * 从本地的app文件夹中获取背景图
     */
    private static Bitmap getAppMusicPicFromDirectory(String netMusicTitle,int containerWidth,int containerHeight){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            String pathName = new StringBuilder(StorageUtils.
                    getAppMusicPicAbsolutePath()).append(netMusicTitle).append(".jpg").toString();
            BitmapFactory.decodeFile(pathName,options);
            int scaleX = options.outWidth/containerWidth;
            int scaleY = options.outHeight/containerHeight;
            options.inSampleSize = Math.max(Math.max(scaleX,scaleY),1);
            // 我们得到了缩放比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(pathName,options);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 给控件设置app默认的背景图片
     * @param context
     * @param view
     * @return true表示设置背景成功
     */
    public static boolean setViewDefaultBackground(Context context,View view){
        MySharePreferences instance = MySharePreferences.getInstance();
        String defaultBgPath = instance.getDefaultBgPath();
        if(!TextUtils.isEmpty(defaultBgPath)){
            try{
                view.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(defaultBgPath)));
                return true;
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showToast("默认背景异常，是否被您删除？已恢复原始默认背景");
                instance.saveDefaultBgPath(null);
                return false;
            }
        }
        return false;
    }

}
