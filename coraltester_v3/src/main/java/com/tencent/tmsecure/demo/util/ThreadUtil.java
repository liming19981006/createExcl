package com.tencent.tmsecure.demo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程工具类
 * Created by wanghl on 2020/5/28
 */
public final class ThreadUtil {
    /**
     * 线程池
     */
    private static ExecutorService mWorkerPool;

    /**
     * 执行线程
     *
     * @param runnable
     */
    public static void doWork(Runnable runnable) {
        if (mWorkerPool == null) {
            synchronized (ThreadUtil.class) {
                if (mWorkerPool == null) {
                    mWorkerPool = Executors.newFixedThreadPool(3);
                }
            }
        }
        mWorkerPool.execute(runnable);
    }
}
