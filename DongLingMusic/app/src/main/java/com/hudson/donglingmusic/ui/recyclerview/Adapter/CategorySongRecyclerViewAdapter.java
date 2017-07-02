package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.CategoryAllSongBean;
import com.hudson.donglingmusic.ui.itempager.CategoryItemPager;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.CategorySongContentItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.CategorySongHeaderItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.CategorySongTitleItemViewHolder;

import java.util.ArrayList;


/**
 * Created by Hudson on 2017/3/21.
 */

public class CategorySongRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    public static final int TYPE_HEADER = 1;//头部热门分类
    public static final int TYPE_TITLE = 2;//每一类的标题
    public static final int TYPE_CONTENT = 3;//内容
    private ArrayList<String> mHotCategory;
    private CategoryAllSongBean mCategoryAllSongBean;
    private int mSecond;
    private int mThird;
    private int mFourth;
    private int mFifth;
    private int mSixth;
    private int mSeventh;
    private int mEighth;
    private int mNineth;
    private int mTenth;
    private int mEleventh;

    public void setCategoryAllSongBean(CategoryAllSongBean categoryAllSongBean) {
        mCategoryAllSongBean = categoryAllSongBean;
        notifyDataSetChanged();
    }

    public void setHotCategory(ArrayList<String> hotCategory) {
        mHotCategory = hotCategory;
        notifyDataSetChanged();
    }


    public CategorySongRecyclerViewAdapter(Context context){
        System.out.println("正在viewAdapter中");
        mLayoutInflater = LayoutInflater.from(context);
        mHotCategory = new ArrayList<>();
        mCategoryAllSongBean = new CategoryAllSongBean();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_CONTENT:
                return new CategorySongContentItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_category_content,parent,false),mOnItemClickListener);
            case TYPE_HEADER:
                return new CategorySongHeaderItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_category_header,parent,false),mOnItemClickListener);
            case TYPE_TITLE:
                return new CategorySongTitleItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_category_title,parent,false));
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_CONTENT:
                if(position >1&&position<mSecond){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mZhuti.get(position - 2).title);
                }else if(position>mSecond&&position<mThird){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mYueqi.get(position -mSecond-1).title);
                }else if(position>mThird&&position<mFourth){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mRensheng.get(position -mThird- 1).title);
                }else if(position>mFourth&&position<mFifth){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mChangjing.get(position -mFourth- 1).title);
                }else if(position>mFifth&&position<mSixth){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mNiandai.get(position -mFifth- 1).title);
                }else if(position>mSixth&&position<mSeventh){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mYingshi.get(position -mSixth- 1).title);
                }else if(position>mSeventh&&position<mEighth){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mXinqing.get(position -mSeventh- 1).title);
                }else if(position>mEighth&&position<mNineth){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mXiqu.get(position -mEighth- 1).title);
                }else if(position>mNineth&&position<mTenth){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mLiupai.get(position -mNineth- 1).title);
                }else if(position>mTenth&&position<mEleventh){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mYuyan.get(position -mTenth- 1).title);
                }else if(position>mEleventh){
                    ((BaseRecyclerViewHolder)holder).refreshView(mCategoryAllSongBean.mFengge.get(position -mEleventh- 1).title);
                }
                break;
            case TYPE_HEADER:
                ((CategorySongHeaderItemViewHolder)holder).refreshView(mHotCategory);
                break;
            case TYPE_TITLE:
                if(position == 1){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[0]);
                }else if(position == mSecond){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[1]);
                }else if(position == mThird){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[2]);
                }else if(position == mFourth){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[3]);
                }else if(position == mFifth){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[4]);
                }else if(position == mSixth){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[5]);
                }else if(position == mSeventh){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[6]);
                }else if(position == mEighth){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[7]);
                }else if(position == mNineth){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[8]);
                }else if(position == mTenth){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[9]);
                }else if(position == mEleventh){
                    ((BaseRecyclerViewHolder)holder).refreshView(CategoryItemPager.CATEGORY_TITLES[10]);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mCategoryAllSongBean.mZhuti!=null){
            mSecond = (mCategoryAllSongBean.mZhuti.size() + 1 + 1);
            mThird = (mSecond+mCategoryAllSongBean.mYueqi.size() + 1);
            mFourth = (mThird + mCategoryAllSongBean.mRensheng.size() + 1);
            mFifth = (mFourth + mCategoryAllSongBean.mChangjing.size() + 1);
            mSixth = (mFifth+mCategoryAllSongBean.mNiandai.size() + 1);
            mSeventh = (mSixth + mCategoryAllSongBean.mYingshi.size() + 1);
            mEighth = (mSeventh + mCategoryAllSongBean.mXinqing.size() + 1);
            mNineth = (mEighth + mCategoryAllSongBean.mXiqu.size() + 1);
            mTenth = (mNineth + mCategoryAllSongBean.mLiupai.size() + 1);
            mEleventh = (mTenth + mCategoryAllSongBean.mYuyan.size() + 1);
            return mCategoryAllSongBean.mFengge.size() + 1 + mEleventh;
        }else if(mHotCategory.size()!=0){
            return 1;
        }else {
            return 0;
        }
    }

    //顺序是：歌手、歌曲、专辑
    @Override
    public int getItemViewType(int position) {
        if(getItemCount()!=0){
            if(position == 0){
                return TYPE_HEADER;
            }else if (mCategoryAllSongBean.mZhuti!=null&&(position == 1|position == mSecond |position == mThird
                    |position == mFourth|position == mFifth|position == mSixth|position == mSeventh|
                    position == mEighth|position == mNineth|position== mTenth|position == mEleventh)){
                return TYPE_TITLE;
            }else{
                return TYPE_CONTENT;
            }
        }
        return -1;
    }

    //每一项点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(String tag);
    }
}
