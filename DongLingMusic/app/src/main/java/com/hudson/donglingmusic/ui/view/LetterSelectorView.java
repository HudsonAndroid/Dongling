package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hudson.donglingmusic.R;

/**
 * Created by Hudson on 2017/3/22.
 * recyclerView侧边选中控件
 *
 * 问题：单独的这个控件时没有问题，但是在整个app中就有问题，第一次点击，提示的textView不会显示(down方法也进去了）
 */

public class LetterSelectorView extends ViewGroup {
    // 26个字母+#
    public static String[] b = {  "#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    private Paint mPaint;//画笔
    private TextView mTipTextView;//选中的提示
    private int mStrokeHeight;//每一个块的高度(宽度默认等于高度）
    private int mTextColor = Color.BLUE;//文字颜色
    private int mTextSize = 20;//字体大小
    private Rect mRect;//每一个块的限制矩形
    private int mOffset = 10;//x偏移量，防止tipTextView盖住a-z

    public LetterSelectorView(Context context) {
        this(context,null);
    }

    public LetterSelectorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LetterSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);//加粗字体
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mRect = new Rect();
        mPaint.getTextBounds("A",0,1,mRect);
        mTipTextView = new TextView(context);
        mTipTextView.setText("A");
        mTipTextView.setTextSize(25);
        mTipTextView.setTextColor(Color.BLACK);
        mTipTextView.setBackgroundResource(R.drawable.tip_tv_bg);
        mTipTextView.setVisibility(INVISIBLE);
        addView(mTipTextView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //+0.5f是四舍五入避免由于int/int导致的精度失真
        mStrokeHeight = (int)(heightSize*1.0f/b.length+0.5f);//初始化块的尺寸值
        //默认我们文字的宽度与高度相同，padding值也一样
        //根据子View大小，修改自身大小
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(mTipTextView.getMeasuredWidth()+mStrokeHeight+mOffset,heightSize);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            mTipTextView.layout(0,0,mTipTextView.getMeasuredWidth(),mTipTextView.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int startX = getWidth() - mStrokeHeight;
        for (int i = 0; i < b.length; i++) {
            canvas.drawText(b[i],startX,mStrokeHeight*i+(mStrokeHeight+mRect.height())/2,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(event.getX()>=getWidth() - mStrokeHeight){//只有按在a-z上面才生效
                    mTipTextView.setVisibility(VISIBLE);
                    int v = (int) (event.getY() - mTipTextView.getHeight() / 2);
//                    System.out.println("宽高"+mTipTextView.getWidth()+"高"+mTipTextView.getHeight()+"显示的底部"+(v+mTipTextView.getHeight()));
                    mTipTextView.layout(0, v,mTipTextView.getWidth(),v+mTipTextView.getHeight());
                    String locationLetter = getLocationLetter((int) (event.getY() + 0.5f));
                    mTipTextView.setText(locationLetter);
                    if(mOnLetterSelectedListener!=null){
                        mOnLetterSelectedListener.onLetterSelected(locationLetter);
                    }
                    return true;
                }else{
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                    mTipTextView.setVisibility(INVISIBLE);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取指定坐标处的字母
     * @param locationY
     * @return
     */
    private String getLocationLetter(int locationY){
        int index = locationY/mStrokeHeight;
        if(index>=b.length){
            index = b.length-1;
        }
        return b[index];
    }

    public void setOnLetterSelectedListener(onLetterSelectedListener onLetterSelectedListener) {
        mOnLetterSelectedListener = onLetterSelectedListener;
    }

    private onLetterSelectedListener mOnLetterSelectedListener;
    public interface onLetterSelectedListener{
        void onLetterSelected(String letter);
    }
}
