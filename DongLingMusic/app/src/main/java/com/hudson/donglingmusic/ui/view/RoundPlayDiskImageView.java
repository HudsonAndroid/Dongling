package com.hudson.donglingmusic.ui.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/3/28.
 * 本控件是play界面的圆形disk
 * 更新图片请调用set
 */

public class RoundPlayDiskImageView extends ImageView {
    private int mSideDimension;//边长
    private Paint mPaint;
    private int mStrokeColor = Color.parseColor("#998B8B8A");
    private int mStrokeWidth = 10;//px
    private Bitmap mCircleBitmap;
    private Bitmap mDiscBitmap;
    private ObjectAnimator mRotateAnimator;

    //添加了圆环，根据圆环内圆面积占比（约为0.7），我们重新设置了专辑图片的缩放大小
    private float mRate = 0.71f;


    public RoundPlayDiskImageView(Context context) {
        this(context,null);
    }

    public RoundPlayDiskImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundPlayDiskImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mStrokeColor);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initTargetCirlceImageFromDrawable();
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
//        System.out.println("为空吗"+(bm==null));
        super.setImageBitmap(bm);
        initTargetCirlceImageFromDrawable();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int dimension = Math.min(widthSize,heightSize);
        setMeasuredDimension(dimension,dimension);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mSideDimension = Math.min(w,h);
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_disc);
        mDiscBitmap = Bitmap.createScaledBitmap(mDiscBitmap,mSideDimension - mStrokeWidth,mSideDimension - mStrokeWidth,false);
        initTargetCirlceImageFromDrawable();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mSideDimension/2,mSideDimension/2,mSideDimension/2,mPaint);
        canvas.drawBitmap(mCircleBitmap,(mSideDimension - mCircleBitmap.getWidth())/2,
                (mSideDimension- mCircleBitmap.getHeight())/2,null);
        canvas.drawBitmap(mDiscBitmap,mStrokeWidth-5,mStrokeWidth-5,null);
    }

    /**
     * 获取需要的图片的边长像素值
     * 原因：由于我们获取专辑图片时需要传入一个指定的尺寸大小，为了避免
     * 图片失真，所以我们需要给目标传入一个适合的尺寸
     * @return
     */
    public int getRequireImageSide(){
        return (int)((mSideDimension - mStrokeWidth * 2)*mRate + 0.5f);
    }

    /**
     * 根据src初始化需要绘制的圆形图片
     */
    private void initTargetCirlceImageFromDrawable(){
        Drawable drawable = getDrawable();
        //此处需要对drawable的width判断，原因在于就算setImageBitmap方法传入了null,getDrawable仍然不为null，
        //只是width为0而已
        int imageSide = mSideDimension - mStrokeWidth * 2;
        if(drawable!=null&&drawable.getIntrinsicWidth()>0){
            mCircleBitmap = drawableToBitmap(drawable);
            //我们的图片不需要缩放那么大
            mCircleBitmap = Bitmap.createScaledBitmap(mCircleBitmap, (int)(imageSide*mRate+0.5f),
                    (int)(imageSide*mRate+0.5f),false);
        }else{
            mCircleBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_play_disk);
            mCircleBitmap = Bitmap.createScaledBitmap(mCircleBitmap, imageSide,
                    imageSide,false);
        }
        //由于使用了圆环，所以不需要创建圆形图片
//        mCircleBitmap = createCircleImage(mSongBitmap, imageSide);
    }

    /**
     * 根据原图和变长绘制圆形图片
     * 步骤：先绘制圆形，然后绘制图片，利用xfermode模式来显示圆形图片
     * @param source 原始图片
     * @param sideSize 图片的边长
     * @return 圆形图片
     */
    private Bitmap createCircleImage(Bitmap source, int sideSize) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(sideSize, sideSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(sideSize / 2, sideSize / 2, sideSize / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }


    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return 一个正方形图片
     */
    private Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            int intrinsicWidth = bd.getIntrinsicWidth();
            int intrinsicHeight = bd.getIntrinsicHeight();
            int side = Math.min(intrinsicWidth, intrinsicHeight);
            return Bitmap.createBitmap(bd.getBitmap(),(intrinsicWidth-side)/2,
                    (intrinsicHeight - side)/2,side,side);
        }
        int side = Math.min(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, side, side);
        drawable.draw(canvas);
        return bitmap;
    }

    //============================旋转动画==============
    /*
     * 在android API19以前，属性动画是不支持pause和resume的，所以进行sdk的判断
     */
    public void startRotateAnimator(){
        initRotateAnimator();
        mRotateAnimator.start();
    }

    private void initRotateAnimator() {
        mRotateAnimator = ObjectAnimator.ofFloat(this,"rotation",0f,360f);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.setDuration(40000);//设置旋转一圈的时间
    }

    public void stopRotateAnimator(){
        if(mRotateAnimator!=null&&mRotateAnimator.isRunning()){
            mRotateAnimator.cancel();
        }
    }

    public void pauseRotateAnimator(){
        if(mRotateAnimator!=null&&mRotateAnimator.isRunning()){
            if(Build.VERSION.SDK_INT>=19){
                mRotateAnimator.pause();
            }else{
                mRotateAnimator.end();
            }
        }
    }

    public void reStartRotateAnimator(){
        if(mRotateAnimator == null){
            initRotateAnimator();
        }
        if(Build.VERSION.SDK_INT>=19&&mRotateAnimator.isPaused()){
            mRotateAnimator.resume();
        }else{
            mRotateAnimator.start();
        }
    }
}
