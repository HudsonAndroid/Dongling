package com.hudson.donglingmusic.bitmapblurjni;

import android.graphics.Bitmap;

/**
 * Created by Hudson on 2017/3/27.
 */

public class BitmapBlur {

    static{
        System.loadLibrary("BitmapBlur");
    }

    /**
     * Blur Image By Bitmap
     *
     * @param bitmap Img Bitmap
     * @param r      Blur radius
     */
    protected static native void blurBitmap(Bitmap bitmap, int r);
}
