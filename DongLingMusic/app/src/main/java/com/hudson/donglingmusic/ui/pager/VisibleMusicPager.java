package com.hudson.donglingmusic.ui.pager;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.view.BigBackgroundView;
import com.hudson.donglingmusic.ui.view.CircleVisualizerFFTView;
import com.hudson.donglingmusic.ui.view.RoundPlayDiskImageView;
import com.hudson.donglingmusic.utils.BitmapMusicUtils;

/**
 * Created by Hudson on 2017/3/25.
 */

public class VisibleMusicPager extends BasePlayPager{
    private CircleVisualizerFFTView mCircleVisualizerFFTView;
    private Visualizer mVisualizer;
    private RoundPlayDiskImageView mDiskImageView;//圆形disk
    private BigBackgroundView mBigBackgroundView;
    private MySharePreferences mInstance;

    public VisibleMusicPager(Context context) {
        super(context);
    }

    @Override
    public View initView(Context context) {
        View view = View.inflate(context, R.layout.pager_visiblemusic, null);
        mCircleVisualizerFFTView = (CircleVisualizerFFTView) view.findViewById(R.id.cvf_music_view);
        mInstance = MySharePreferences.getInstance();
        //设置频谱块宽度、数目、颜色、旋转颜色
        mCircleVisualizerFFTView.setStrokeWidth(mInstance.getVisibleCircleColumnWidth());
        mCircleVisualizerFFTView.setCakeCount(mInstance.getVisibleCircleColumnCount());
        mCircleVisualizerFFTView.setColors(mInstance.getColorsArray());
        mCircleVisualizerFFTView.setRotateColor(mInstance.getRotateCircleVisibleMusicColor());
        mDiskImageView = (RoundPlayDiskImageView) view.findViewById(R.id.rpdiv_disk);
        mBigBackgroundView = (BigBackgroundView) view.findViewById(R.id.bbv_back);
        mBigBackgroundView.setEnable(mInstance.getVisibleBgEnable());
        mBigBackgroundView.setRadius(mInstance.getVisibleBgRadius());
        mBigBackgroundView.setColor(mInstance.getVisibleBgColor());
        return view;
    }

    @Override
    public void initData() {
        try {
            clear();
            if(mInterface == null){
                return ;
            }
            mVisualizer = new Visualizer(mInterface.getAudioSessionId());
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            //参数三是采样频率
            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                                                  int samplingRate) {
                }

                //这个回调采集的是快速傅里叶变换有关的数据,fft就是采集到的byte数据（频域波形图）
                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft,
                                             int samplingRate) {
                    mCircleVisualizerFFTView.updateVisualizer(fft);
                    mBigBackgroundView.updateView(fft[2],fft[3]);
                }
            }, Visualizer.getMaxCaptureRate()/2, true, true);
            mVisualizer.setEnabled(true);
            if(mInterface.isPlaying()){
                startRotateDisk();
            }
            //等待roundImageView测量完成（原因在于mdiskImageView的width可能还没测好）
            if(mDiskImageView.getWidth()<=0||mDiskImageView.getHeight()<=0)
                mDiskImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mDiskImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        setRoundImageViewBitmap();
                    }
                });
            else
                setRoundImageViewBitmap();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        if(mVisualizer!=null){
            mVisualizer.setEnabled(false);
            mVisualizer = null;
        }
    }

    private void startRotateDisk(){
        mDiskImageView.startRotateAnimator();
    }

    public void stopRotateDisk(){
        mDiskImageView.stopRotateAnimator();
    }

    public void pauseRotateDisk(){
        mDiskImageView.pauseRotateAnimator();
    }

    public void reStartRotateDisk(){
        mDiskImageView.reStartRotateAnimator();
    }

    private void setRoundImageViewBitmap(){
        try {
            if(mInterface.getPlayState() != MusicService.STATE_IDLE) {
                MusicInfo curMusicInfo = mInterface.getCurMusicInfo();
                if(mDiskImageView!=null&&curMusicInfo!=null)
                    mDiskImageView.setImageBitmap(BitmapMusicUtils.getArtwork(curMusicInfo.getTitle(),
                        curMusicInfo.getAlbumId(),mDiskImageView.getRequireImageSide(),
                        mDiskImageView.getRequireImageSide()));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
