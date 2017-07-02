package com.hudson.donglingmusic.ui.fragment;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.ui.activity.HomeActivity;
import com.hudson.donglingmusic.ui.activity.SearchActivity;
import com.hudson.donglingmusic.ui.pager.BasePager;
import com.hudson.donglingmusic.ui.pager.FavoritePager;
import com.hudson.donglingmusic.ui.pager.HistoryPager;
import com.hudson.donglingmusic.ui.pager.MusicPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/15.
 * 主页面的fragment
 *
 *      大问题：由于我们的ViewPager与RadioGroup结合在一起了，所以我们的ViewPager一旦滑动切换了（onPageSelected调用），
 *             我们肯定需要将变化更新到我们的RadioGroup上来，但是由于我们的RadioGroup的check状态变化又会触发ViewPager
 *             的onPageSelected方法，从而导致我们的onPageSelected执行两次，导致数据更新多次
 *      解决：我们需要利用一个mLastSelected变量来保存上一次选中的位置，如果onPageSelected方法的position与上一次不同，我们
 *           就更新数据
 */

public class HomeContentFragment extends BaseFragment {
    private ViewPager mViewPager;
    private ArrayList<BasePager> mPagers;//标签页集合
    private RadioGroup mRadioGroup;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private SlidingMenu mSlidingMenu;
    private int mLastSelected = 0;

    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_home_content, null);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_content);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        view.findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//search
                mActivity.startActivity(new Intent(mActivity, SearchActivity.class));
            }
        });
        view.findViewById(R.id.toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggle();
            }
        });
        return view;
    }

    //给ViewPager填充数据
    @Override
    public void initData() {
        mPagers = new ArrayList<>();
        //添加标签页
        mPagers.add(new MusicPager(mActivity));
        mPagers.add(new FavoritePager(mActivity));
        mPagers.add(new HistoryPager(mActivity));
        mViewPager.setAdapter(new ContentPagerAdapter());
        //标签切换监听
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.music:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.favorite:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.history:
                        mViewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
            }
        });
        //给ViewPager中的页面加载数据(为何在这里，目的是为了节省资源.只有在被选中的时候才加载数据)
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mLastSelected !=position){
                    BasePager pager = mPagers.get(position);
                    pager.initData();
                    ((RadioButton)mRadioGroup.getChildAt(position)).setChecked(true);
                    //使用mRadioGroup.setCheckId(mRadioGroup.getChildAt(position).getId());有BUG，系统问题
                    // （这种方式会多次调用，虽然最终选择正确，但是中间出现偏离正确的值出现，例如我们目标id是1，他会
                    // 先是1，然后0，然后再1）
                    //只有外部与内部的musicPager的ViewPager的位置都为0时才可以让slidingMenu有用
                    if(pager instanceof MusicPager&&pager.getInnerViewPagerCurPosition() == 0&&position==0){
                        mSlidingMenu.setSlidingEnabled(true);
                    }else{
                        mSlidingMenu.setSlidingEnabled(false);
                    }
                    mLastSelected = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mPagers.get(0).initData();//手动加载第一页数据
        mSlidingMenu = ((HomeActivity)mActivity).getSlidingMenu();
    }

    class ContentPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //添加并返回页面的主布局
            View pagerView = mPagers.get(position).mRootView;
            container.addView(pagerView);
            return pagerView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    @Override
    public void onDestroyView() {
        for(BasePager pager:mPagers){
            pager.onDestroy();
        }
        mViewPager.removeOnPageChangeListener(mOnPageChangeListener);
        super.onDestroyView();
    }
}
