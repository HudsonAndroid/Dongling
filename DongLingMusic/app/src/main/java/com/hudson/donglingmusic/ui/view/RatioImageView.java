package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hudson.donglingmusic.R;

/**
 * 按照图片比例显示图片imageView(按照图片大小设置imageView控件的大小)
 * 图片不经过拉伸与裁剪（正确显示完整图片）
 * Created by Hudson on 2017/1/18.
 */

public class RatioImageView extends ImageView {
    private int mImageResId;
    private float ratio;//宽高比

    public RatioImageView(Context context) {
        this(context,null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if(attrs!=null){//避免由于第一个构造方法造成异常
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.RatioImageView);
            mImageResId = ta.getResourceId(R.styleable.RatioImageView_src,R.drawable.icon_player);
            ta.recycle();
        }else {
            mImageResId = R.drawable.icon_player;
        }
        ratio = getImageRatio(mImageResId);
        setImageResource(mImageResId);
    }

    private float getImageRatio(int imageResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =   true;
        BitmapFactory.decodeResource(getResources(), imageResId, options);
        return options.outWidth*1.0f/options.outHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //根据我们图片的宽高设置控件的宽高（padding值我们不应该加入）
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize;
        if(widthMode == MeasureSpec.EXACTLY){//如果我们的宽度确定，直接根据宽度与比例设置高度值
            heightSize = ((int) ((widthSize - getPaddingLeft() - getPaddingRight())/ratio)) + getPaddingTop() + getPaddingBottom();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
        }else{//我们的宽度不是确定的，我们就看高度是否是确定值
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if(heightMode == MeasureSpec.EXACTLY){//由于宽度值不确定，所以我们根据高度与比例设置宽度值
                heightSize = MeasureSpec.getSize(heightMeasureSpec);
                widthSize = (int) ((heightSize - getPaddingBottom() - getPaddingTop()) * ratio) + getPaddingRight() + getPaddingLeft();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
            }else{//说明width与height都不是具体值或Matchparent，那么我们就不做任何处理，使用原始的，并抛出异常
                try {
                    throw new Exception("all dimensions of width and height are ont exactly 宽高都不是确定值！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

