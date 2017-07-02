package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hudson on 2017/3/12.
 * 本控件是一个自动排列子View的ViewGroup。
 * 本控件是第四个自定义ViewGroup
 * 使用场景：标签
 * 总结：
 *      1.我们的View与ViewGroup自定义时wrap_content默认情况是不同的，
 *        View在布局文件中设置了wrap_content时，在onMeasure方法中getSize获取到是默认值是0；
 *        而ViewGroup在布局文件中设置了wrap_content时，在onMeasure方法中getSize获取默认值是父ViewGroup的宽高，相当于fill_parent的值
 * 问题：
 *      1.如果一个子View本身就超过了本ViewGroup的宽度怎么处理？
 *      2.没有考虑padding
 */

public class AutoAdapterLayout extends ViewGroup {

    public AutoAdapterLayout(Context context) {
        this(context,null);
    }

    public AutoAdapterLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AutoAdapterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //==================第二步，计算子View，并确定自身宽高===========
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realWidth = 0,realHeight = 0;
        //2.1 计算上级ViewGroup推荐宽高及计算模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //这个地方如果xml中设置了wrap_content，默认不是0，是父ViewGroup的宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //2.2 测量子View，以确定其宽高。通过子View的宽高，确定viewGroup自身在wrap_content时的大小
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        //2.3 计算viewGroup在wrap_content时大小
        int wrapWidth = 0;
        int wrapHeight = 0;
        int childWidth,childHeight;
        int lineWidth = 0,lineHeight = 0;
        MarginLayoutParams childMarginLayoutParams;
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            childMarginLayoutParams = (MarginLayoutParams) child.getLayoutParams();
            //当前子View实际占据的宽高
            childWidth = child.getMeasuredWidth() + childMarginLayoutParams.leftMargin + childMarginLayoutParams.rightMargin;
            childHeight = child.getMeasuredHeight() + childMarginLayoutParams.topMargin+childMarginLayoutParams.bottomMargin;
            //如果超过所指定的最大width，那么换行，同时height增加
            if(lineWidth + childWidth>widthSize){
                wrapWidth = Math.max(wrapWidth,lineWidth);//跟上一个wrapWidth比较,取最大值
                //重新累加
                lineWidth = childWidth;
                //高度累加(此时lineHeight是该行最大高度)
                wrapHeight += lineHeight;
                //开启下一行高度比较
                lineHeight = childHeight;
            }else{//一般情况
                lineWidth += childWidth;//宽度累加
                lineHeight = Math.max(lineHeight,childHeight);//跟上一个lineHeight比较，取最大值
            }
            //如果是最后一个，并没有分行，所以也需要比较width和累加height
            if(i == (getChildCount()-1)){
                wrapWidth = Math.max(wrapWidth,lineWidth);
                wrapHeight += lineHeight;
            }
        }


        //2.4 确定真实宽高
        switch (widthMode){
            case MeasureSpec.EXACTLY:
                realWidth = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                realWidth = wrapWidth;
                break;
            case MeasureSpec.AT_MOST:
                realWidth = Math.min(widthSize,wrapWidth);
                break;
        }
        switch (heightMode){
            case MeasureSpec.EXACTLY:
                realHeight = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                realHeight = wrapHeight;
                break;
            case MeasureSpec.AT_MOST:
                realHeight = Math.min(heightSize,wrapHeight);
        }
        setMeasuredDimension(realWidth,realHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int viewGroupWidth = getWidth();//获取本ViewGroup的宽度
            View child;
            MarginLayoutParams layoutParams;
            int childWidth,childHeight;
            int tokenWidth = 0;//行占据的空间
            int tokenHeight = 0;
            int maxLineHeight = 0;
            int curChildLeft = 0,curChildTop;
            for (int i = 0; i < getChildCount(); i++) {
                child = getChildAt(i);
                if(child.getVisibility() == GONE){//如果则跳过
                    continue;
                }
                layoutParams = (MarginLayoutParams) child.getLayoutParams();
                int leftMargin = layoutParams.leftMargin;
                int topMargin = layoutParams.topMargin;
                int measuredWidth = child.getMeasuredWidth();
                int measuredHeight = child.getMeasuredHeight();
                if(i==0){//如果是第一个
                    curChildLeft = leftMargin;
                }
                //子View实际占据的空间
                childWidth = measuredWidth + leftMargin + layoutParams.rightMargin;
                childHeight = measuredHeight + topMargin + layoutParams.bottomMargin;
                if(tokenWidth + childWidth>viewGroupWidth){
                    tokenWidth = 0;
                    tokenHeight += maxLineHeight;
                    maxLineHeight = 0;//把高度置0，开始下一行高度选择
                    curChildLeft = leftMargin;
                }
                curChildTop = tokenHeight + topMargin;
                child.layout(curChildLeft,curChildTop,curChildLeft+measuredWidth,curChildTop+measuredHeight);
                curChildLeft += childWidth;
                tokenWidth += childWidth;
                maxLineHeight = Math.max(maxLineHeight,childHeight);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
