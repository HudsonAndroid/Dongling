package com.hudson.donglingmusic.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.fragment.MusicMenuFragment;
import com.hudson.donglingmusic.ui.fragment.PlayListFragment;
import com.hudson.donglingmusic.utils.ToastUtils;

import static com.hudson.donglingmusic.ui.fragment.MusicMenuFragment.REQUEST_CODE_WRITE_SETTINGS;

/**
 * 本activity是我们的歌曲选项与播放列表的容器
 *
 *  本activity有以下处理：
 *      1.显示时背景是逐渐由完全透明转向半透明
 *      2.容器是从下往上推的
 *
 *      3.销毁时是反向的
 */
public class MoreInfoActivity extends FragmentActivity {
    private View mInfoContainer;
    public int mSelectedIndex;//歌曲详情时使用
    public MusicInfo mSelectedMusic;//歌曲详情时使用
    public String mSelectedMusicListInfo;//歌曲详情使用，用于表示当前的歌曲所属列表的信息
    private boolean mNeedBind = false;
    private IDonglingMusicAidlInterface mInterface;
    private PlayListServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        findViewById(R.id.rl_info_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTranOutAnimator();
            }
        });
        mInfoContainer = findViewById(R.id.fl_info_container);
        mInfoContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//onLayout执行结束的回调方法
                mInfoContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //测量完成
                mInfoContainer.setVisibility(View.VISIBLE);
                startTranInAnimator();
            }
        });
        initInputExtraData();
        initFragment();
    }

    private void initInputExtraData() {
        Intent intent = getIntent();
        if(intent!=null){
            mSelectedIndex = intent.getIntExtra("selected_index",-1);
            mSelectedMusic = intent.getParcelableExtra("music_info");
            mSelectedMusicListInfo = intent.getStringExtra("list_info");
            //array还没处理
            mNeedBind = intent.getBooleanExtra("playListPageOrNot", false);
            if(mNeedBind){
                bind();
            }
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(mNeedBind){
            transaction.add(R.id.fl_info_container,new PlayListFragment());
        }else{
            transaction.add(R.id.fl_info_container,new MusicMenuFragment());
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        startTranOutAnimator();
    }

    /**
     * 内容显示动画
     */
    private void startTranInAnimator(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mInfoContainer,"translationY",
                mInfoContainer.getHeight(),0);
        animator.setDuration(300);
        animator.start();
    }

    public void startTranOutAnimator(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mInfoContainer,"translationY",
                0,mInfoContainer.getHeight());
        animator.setDuration(300);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0,0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT>=23&&Settings.System.canWrite(this)) {
                if(mListener!=null){
                    mListener.onPermissionAllowOrNot();
                }
            }else{
                ToastUtils.showToast("您拒绝了权限授予，无法设置铃声！");
            }
        }
    }

    private PermissionGrantedOrNotListener mListener;
    public void setListener(PermissionGrantedOrNotListener listener) {
        mListener = listener;
    }
    public interface  PermissionGrantedOrNotListener{
        void onPermissionAllowOrNot();
    }


    private void bind(){
        mConnection = new PlayListServiceConnection();
        bindService(new Intent(MoreInfoActivity.this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }


    class PlayListServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
            if(mOnServiceConnectedListener!=null){
                mOnServiceConnectedListener.onServiceConnected(mInterface);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private OnServiceConnectedListener mOnServiceConnectedListener;

    public void setOnServiceConnectedListener(OnServiceConnectedListener onServiceConnectedListener) {
        mOnServiceConnectedListener = onServiceConnectedListener;
    }

    public interface OnServiceConnectedListener{
        void onServiceConnected(IDonglingMusicAidlInterface face);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNeedBind)
            unBind();
    }

    public IDonglingMusicAidlInterface getInterface(){
        return mInterface;
    }
}
