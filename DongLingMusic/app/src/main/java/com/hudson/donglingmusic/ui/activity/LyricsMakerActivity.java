package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.pager.BaseLyricsMakePager;
import com.hudson.donglingmusic.ui.pager.LyricsMakeStepOnePager;
import com.hudson.donglingmusic.ui.pager.LyricsMakeStepThreePager;
import com.hudson.donglingmusic.ui.pager.LyricsMakeStepTwoPager;
import com.hudson.donglingmusic.ui.view.PlayPageViewPager;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.ui.view.StepProgressView;

import java.util.ArrayList;

/**
 * 本activity是歌词制作界面
 * 一旦进入本页面，我们开启单曲循环模式，一旦离开本页面，我们恢复原有播放模式
 */
public class LyricsMakerActivity extends Activity {
    private StepProgressView mStepProgressView;
    private PlayPageViewPager mViewPager;
    private int[] mDescs = new int[]{R.string.lyrics_make_step_one_tip,
            R.string.lyrics_make_step_two_tip,R.string.lyrics_make_step_three_tip};
    private ArrayList<BaseLyricsMakePager> mPagerList;
    private ViewPager.OnPageChangeListener mListener;
    private LyricsMakeStepTwoPager mSecondPager;
    private LyricsMakeStepThreePager mThirdPager;
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private LyricsMakeServiceConnection mConnection;

    private int mLastPlayMode = MusicService.MODE_LIST_ORDER;//用于保存进入本页面之前的播放模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_maker);
        init();
        bind();
    }

    private void init() {
        SlideRelativeLayout rootView = (SlideRelativeLayout) findViewById(R.id.srl_lyrics_maker_root);
        rootView.setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                finish();
            }
        });
        mStepProgressView = (StepProgressView) findViewById(R.id.spv_step_tip);
        String[] desc = new String[mDescs.length];
        for (int i = 0; i < mDescs.length; i++) {
            desc[i] = getString(mDescs[i]);
        }
        mStepProgressView.setStepDesc(desc);
        mStepProgressView.setOnItemClickListener(new StepProgressView.ItemClickListener() {
            @Override
            public void onItemClick(int i) {
                mViewPager.setCurrentItem(i);
            }
        });
        mViewPager = (PlayPageViewPager) findViewById(R.id.vp_lyrics_maker);
        mListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mStepProgressView.setCurStepIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(mListener);
        mPagerList = new ArrayList<>();
        LyricsMakeStepOnePager firstPager = new LyricsMakeStepOnePager(this);
        mSecondPager = new LyricsMakeStepTwoPager(this);
        mThirdPager = new LyricsMakeStepThreePager(this);
        mPagerList.add(firstPager);
        mPagerList.add(mSecondPager);
        mPagerList.add(mThirdPager);
        mViewPager.setAdapter(new LyricsMakerPageAdapter());
    }

    private void bind(){
        mConnection = new LyricsMakeServiceConnection();
        //注意：不能使用intent("xxxx.service");的方式，5.0之后规定服务不能这样启动
        bindService(new Intent(LyricsMakerActivity.this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }

    class LyricsMakeServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
            mThirdPager.setInterface(mInterface);
            mThirdPager.setOnLoadLyricsStringListener(new LyricsMakeStepThreePager.onLoadLyricsStringListener() {
                @Override
                public String onLoadLyricsString() {
                    return mSecondPager.getLyricsContent();
                }
            });
            try {
                mLastPlayMode = mInterface.getPlayMode();
                mInterface.setPlayMode(MusicService.MODE_ONE_LOOP);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    @Override
    protected void onDestroy() {
        try {
            mInterface.setPlayMode(mLastPlayMode);//恢复原有播放模式
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mViewPager.removeOnPageChangeListener(mListener);
        mThirdPager.onDestroy();
        unBind();
        super.onDestroy();
    }

    class LyricsMakerPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mPagerList.get(position).mRootView;
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

}
