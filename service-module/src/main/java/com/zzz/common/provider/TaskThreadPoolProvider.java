package com.zzz.common.provider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskThreadPoolProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TaskThreadPoolProvider.class);
    // the executorService instance
    private static volatile ExecutorService EXECUTOR_SERVICE;
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(12);
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 6;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final TimeUnit ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    public static final Lock LOCK = new ReentrantLock();

    public static ExecutorService getInstance() {
        try {
            if (EXECUTOR_SERVICE == null) {
                LOG.info(" {} try to access lock for 3 seconds ", Thread.currentThread().getName());
                LOCK.lock();
                if (EXECUTOR_SERVICE == null) {
                    EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                            ALIVE_TIME_UNIT, BLOCKING_QUEUE, (Runnable r, ThreadPoolExecutor executor) -> {
                                if (r instanceof FutureTask) {
                                    FutureTask cFutureTask = (FutureTask) r;
                                } else {
                                    LOG.info(" current task type is not FutureTask will discard {}",
                                            r.getClass().getSimpleName());
                                }
                            });
                }
            }
        } catch (Exception e) {
            LOG.error(" get executorService error , message :{} ", e.getMessage());
        } finally {
            LOCK.unlock();
        }
        return EXECUTOR_SERVICE;
    }

    public static int getKeepAliveTime() {
        return KEEP_ALIVE_TIME;
    }
    public static int getCorePoolSize() {
        return CORE_POOL_SIZE;
    }
    public static int getMaxPoolSize() {
        return MAX_POOL_SIZE;
    }
}
