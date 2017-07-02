package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/2/20.
 * 圆形imageView或者圆角矩形imageView
 * 知识点：getDrawable是获取imageView的src
 */

public class CircleRoundImageView extends ImageView {
    private Paint mPaint;
    private static final int TYPE_CIRCLE = 0;//图片类型
    private static final int TYPE_ROUND = 1;
    private int mType;
    private int mRectRoundRadius;
    private RectF mRoundRectf;//圆角矩形的矩形原型
    private static final int DEFAULT_RECT_ROUND_RADIUS = 10;//dp
    private int mCircleRadius;//圆形图片的半径，不需要我们设置，我们根据控件大小设置
    private Matrix mMatrix;//用于设置缩放比，设置为成员变量，因为在ondraw方法中重复使用
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap;

    public CircleRoundImageView(Context context) {
        this(context,null);
    }

    public CircleRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mMatrix = new Matrix();
        if(attrs!=null){//避免由于第一个构造方法造成异常
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.CircleRoundImageView);
            mType = ta.getInt(R.styleable.CircleRoundImageView_type, TYPE_CIRCLE);
            mRectRoundRadius = (int)dp2px(ta.getDimension(R.styleable.CircleRoundImageView_round_rect_radius,DEFAULT_RECT_ROUND_RADIUS),context);
            ta.recycle();
        }else {
            mType = TYPE_CIRCLE;
            mRectRoundRadius = dp2px(DEFAULT_RECT_ROUND_RADIUS,context);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mType == TYPE_CIRCLE){//如果是圆形imageView则强制修改宽高
            int mCircleTypeViewWidth = Math.min(getMeasuredWidth(),getMeasuredHeight());
            mCircleRadius = mCircleTypeViewWidth /2;
            setMeasuredDimension(mCircleTypeViewWidth,mCircleTypeViewWidth);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mType == TYPE_ROUND){
            mRoundRectf = new RectF(0,0,getWidth(),getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null){
            return ;
        }else{
            //利用shader修改paint
            modifyPaint();
            //绘制
            if(mType == TYPE_CIRCLE){
                canvas.drawCircle(mCircleRadius,mCircleRadius,mCircleRadius,mPaint);
            }else if(mType == TYPE_ROUND){
                canvas.drawRoundRect(mRoundRectf,mRectRoundRadius,mRectRoundRadius,mPaint);
            }
        }
    }

    private void modifyPaint(){
        mBitmap = drawableToBitamp(getDrawable());
        mBitmapShader = new BitmapShader(mBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //修改比例,我们的图片必须比控件大，才会有圆形或者圆角矩形的效果。所以我们的图片尽可能放大
        float scale = 1.0f;
        if(mType == TYPE_CIRCLE){
            scale = mCircleRadius*2*1.0f/Math.min(mBitmap.getWidth(), mBitmap.getHeight());
        }else if(mType == TYPE_ROUND){//放大倍数尽可能大
            scale = Math.max(getWidth()*1.0f/ mBitmap.getWidth(),getHeight()*1.0f/ mBitmap.getHeight());
        }
        //将缩放比例设置到bitmapShader上去
        mMatrix.setScale(scale,scale);
        mBitmapShader.setLocalMatrix(mMatrix);
        //将bitmapShader设置到paint上
        mPaint.setShader(mBitmapShader);
    }


    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitamp(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            if(mType == TYPE_CIRCLE){//将图片设置成正方形（取中央部分）
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                int bitmapWidth = Math.min(w,h);//取较小
                bitmap = Bitmap.createBitmap(bitmap,(w - bitmapWidth)/2,(h - bitmapWidth)/2,bitmapWidth,bitmapWidth);
            }
            return bitmap;
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    // dp值转px 参数整型值
    public static int dp2px(int dp, Context context) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics())+0.5f);
    }

    // dp值转PX 参数浮点数
    public static float dp2px(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
