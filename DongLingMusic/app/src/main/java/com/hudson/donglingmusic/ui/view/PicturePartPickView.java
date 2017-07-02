package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.utils.StorageUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Hudson on 2017/6/16.
 * 图片部分选择器
 */

public class PicturePartPickView extends View {
    private Bitmap mBg;
    private Paint mPaint;
    private Rect mRect;
    private int mSlideWidth = 100;
    private int mCurLeft = 0;
    private int mCurTop = 0;
    private int mHeight,mWidth;

    public PicturePartPickView(Context context) {
        this(context,null);
    }

    public PicturePartPickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PicturePartPickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBg = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_player);
        getScreenWidth();
        mHeight = mBg.getHeight();
        mWidth = mBg.getWidth();
        if(mHeight>mScreenHeight|mWidth>mScreenWidth){
            float scale = Math.max(mHeight*1.0f/mScreenHeight,mWidth*1.0f/mScreenWidth)+0.2f;
            mHeight = (int) (mHeight / scale);
            mWidth = (int) (mWidth / scale);
        }
        mSlideWidth = Math.min(mHeight,mWidth)-5;
        mPaint = new Paint();
        mRect = new Rect(mCurLeft,mCurTop,mCurLeft+mSlideWidth,mCurTop+mSlideWidth);
    }

    public void setImageFilePath(String path){
        int preWidth = mWidth;
        int preHeight = mHeight;
        mBg = BitmapFactory.decodeFile(path);
        mHeight = mBg.getHeight();
        mWidth = mBg.getWidth();
        if(mHeight>mScreenHeight|mWidth>mScreenWidth){
            float scale = Math.max(mHeight*1.0f/mScreenHeight,mWidth*1.0f/mScreenWidth)+0.2f;
            mHeight = (int) (mHeight / scale);
            mWidth = (int) (mWidth / scale);
        }
        mSlideWidth = Math.min(mHeight,mWidth)-5;
        int leftOffset = (preWidth - mWidth)/2;
        int topOffset = (preHeight - mHeight)/2;
        int left = getLeft() + leftOffset;
        int top = getTop() + topOffset;
        layout(left,top,left+mWidth,top+mHeight);
        mRect.set(mCurLeft,mCurTop,mCurLeft+mSlideWidth,mCurTop+mSlideWidth);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //将背景图设置成与控件一样大小
        mBg = Bitmap.createScaledBitmap(mBg,mWidth,mHeight,false);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBg,0,0,null);
        canvas.drawColor(0x99000000);
        mPaint.setShader(new BitmapShader(mBg, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        canvas.drawRect(mRect,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int mCenterX = (int) event.getX();
                int halfSlidWidth = mSlideWidth/2;
                mCenterX = (mCenterX<halfSlidWidth)?halfSlidWidth:mCenterX;
                mCenterX = (mCenterX>getWidth() - halfSlidWidth)?getWidth() - halfSlidWidth:mCenterX;
                int mCenterY = (int) event.getY();
                mCenterY = (mCenterY<halfSlidWidth)?halfSlidWidth:mCenterY;
                mCenterY = (mCenterY>getHeight() - halfSlidWidth)?getHeight() - halfSlidWidth:mCenterY;
                mCurLeft = mCenterX - halfSlidWidth;
                mCurTop = mCenterY - halfSlidWidth;
                mRect.set(mCurLeft,mCurTop,mCurLeft+mSlideWidth,mCurTop+mSlideWidth);
                break;
        }
        invalidate();
        return true;
    }


    public String save(String title){
        String path = StorageUtils.getAppMusicPicAbsolutePath()+"gedanBG"+title+".png";
        Bitmap bitmap = Bitmap.createBitmap(mBg, mCurLeft, mCurTop, mSlideWidth, mSlideWidth);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return path;
    }


    private int mScreenWidth,mScreenHeight;
    /*
	 * 获取屏幕的宽高
	 */
    public void getScreenWidth(){
        // 获取屏幕密度（方法2）
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }
}
