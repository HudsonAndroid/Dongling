package com.hudson.donglingmusic.net.download;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hudson on 2017/4/23.
 *
 * 线程池管理
 */

public class ThreadManager {

    private static ThreadPool mThreadPool;

    public static ThreadPool getThreadPool(){
        synchronized (ThreadManager.class) {
            if(mThreadPool==null){
//                //根据当前硬件，设置
//                int cpuCount = Runtime.getRuntime().availableProcessors();//获取cpu个数
//                int threadCount = cpuCount*2 + 1;
                mThreadPool = new ThreadPool(10,10,1l);
            }
        }
        return mThreadPool;
    }

    //线程池
    public static class ThreadPool{
        private int mCoreThreadSize;//核心线程数
        private int mMaxThreadSize;//最大线程数
        private long mKeepAliveTime;//休息时间
        private ThreadPoolExecutor mExecutor;

        //私有，外部不可New
        private ThreadPool(int coreThreadSize, int maxThreadSize, long keepAliveTime) {
            mCoreThreadSize = coreThreadSize;
            mMaxThreadSize = maxThreadSize;
            mKeepAliveTime = keepAliveTime;
        }

        public void execute(Runnable run){
            if(mExecutor==null){
                //参数四：休息时间的时间单位,参数五：排队等待运行的线程队列,参数六：生产线程的工厂，当当前
                //工作量较大时需要开放新的线程；参数七，异常时处理策略
                mExecutor = new ThreadPoolExecutor(mCoreThreadSize,
                        mMaxThreadSize,mKeepAliveTime, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
            }
            mExecutor.execute(run);//执行run
        }

        public void cancelTask(Runnable run){
            if(mExecutor!=null)
            //从等待下载的线程队列中移除对象，注意正在下载的无法移除
            mExecutor.getQueue().remove(run);
        }
    }

}
