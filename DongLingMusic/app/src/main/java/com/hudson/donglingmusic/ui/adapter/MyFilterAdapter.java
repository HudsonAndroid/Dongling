package com.hudson.donglingmusic.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hudson.donglingmusic.utils.StringTextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hudson on 2017/6/20.
 * 注意：这个布局文件的头布局必须是TextView，数据类型必须是String
 */

public class MyFilterAdapter<T> extends BaseAdapter implements Filterable {
    private List<T> mOriginalValues;
    private List<T> mObject;
    private final Object mLock = new Object();
    private int mResouce;
    private MyFilter myFilter = null;
    private LayoutInflater inflater;

    public MyFilterAdapter(Context context, int TextViewResouceId, List<T> objects)
    {
        init(context,TextViewResouceId,objects);
    }

    private void init(Context context, int textViewResouceId, List<T> objects)
    {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObject = objects;
        mResouce = textViewResouceId;
        myFilter = new MyFilter();
    }

    @Override
    public int getCount() {
        return mObject.size();
    }

    @Override
    public T getItem(int position) {
        return mObject.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewFromResouce(position,convertView,parent,mResouce);
    }

    private View getViewFromResouce(int position, View convertView,
                                    ViewGroup parent, int mResouce2) {
        if(convertView == null)
        {
            convertView = inflater.inflate(mResouce2, parent,false);
        }
        TextView tv = (TextView)convertView;
        T item = getItem(position);
        tv.setText(Html.fromHtml(item.toString()));
//        if(item instanceof CharSequence)
//        {
//            System.out.println("设置文本不是字符串");
//            tv.setText((CharSequence)item);
//        }
//        else
//        {
//            System.out.println("设置文本");
//            tv.setText(Html.fromHtml(item.toString()));
//        }
        return tv;
    }

    //返回过滤器
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    //自定义过滤器
    private class MyFilter extends Filter
    {
        //得到过滤结果
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(mOriginalValues == null)
            {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<T>(mObject);
                }
            }

            int count = mOriginalValues.size();
            ArrayList<T> values = new ArrayList<T>();
            for(int i = 0;i < count;i++)
            {
                T value = mOriginalValues.get(i);
                //自定义匹配规则为包含结果 contains
                java.lang.String s = value.toString();
                if(s != null && constraint != null && s.contains(constraint))
                {
                    //这里由于我们知道肯定是String类型，所以强制转换
                    values.add((T)StringTextUtils.getHtmlStringWithColorElement(s,constraint.toString(),0xff0000));
                }
            }
            results.values = values;
            results.count = values.size();
            return results;
        }
        //发布过滤结果
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //把搜索结果赋值给mObject这样每次输入字符串的时候就不必
            //从所有的字符串中查找，从而提高了效率
            mObject = (List<T>)results.values;
            if(results.count > 0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }

    }
}
