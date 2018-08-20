package com.wf.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Junhua Lv on 2015/7/7.
 * 在主线程或者后台线程执行任务
 */
public class TaskDispatcher {

    // 单例
    static private TaskDispatcher mTaskDispatcher;
    // 主线程Handler
    static private Handler mHandler;

    private TaskDispatcher(){
    }

    static public TaskDispatcher getDefault(){
        synchronized (TaskDispatcher.class){
            if (null == mTaskDispatcher)
                mTaskDispatcher = new TaskDispatcher();
        }

        return mTaskDispatcher;
    }

    static public Handler getHandler(){
        synchronized (TaskDispatcher.class){
            if (null == mHandler)
                mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    static public void runInMainThread(Runnable r){
        synchronized (TaskDispatcher.class){
            if (null == mHandler)
                mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(r);
    }

    static public void runInMainThread(Runnable r, long delayMillis){
        synchronized (TaskDispatcher.class){
            if (null == mHandler)
                mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void runInBackground(Runnable r){
        AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
//        new AsyncTask<Object, Void, Void>() {
//            @Override
//            protected Void doInBackground(Object[] params) {
//                Runnable r = (Runnable)params[0];
//                r.run();
//                return null;
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, r);
        //AsyncTask.execute(r);
    }
}
