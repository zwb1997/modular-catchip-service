package com.zzz.service.common.provider;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zzz.model.entitymodel.servicebase.DTO.IpPoolMainDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskThreadPoolProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TaskThreadPoolProvider.class);
    // the executorService instance
    private static volatile ThreadPoolExecutor EXECUTOR_SERVICE;
    private static final int BLOCKING_QUEUE_SIZE = 12;
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(BLOCKING_QUEUE_SIZE);
    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 6;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int MAX_TASK_SIZE = MAX_POOL_SIZE + BLOCKING_QUEUE_SIZE;
    private static final TimeUnit ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    public static final Lock LOCK = new ReentrantLock();

    /**
     * instance will lazy loading
     * 
     * @return
     */
    private static boolean getInstance() {
        try {
            if (EXECUTOR_SERVICE == null) {
                LOCK.tryLock();
                if (EXECUTOR_SERVICE == null) {
                    EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                            ALIVE_TIME_UNIT, BLOCKING_QUEUE, (Runnable r, ThreadPoolExecutor executor) -> {
                                LOG.info(" current task type is not FutureTask will discard {}",
                                        r.getClass().getSimpleName());
                            });
                }
            }
        } catch (Exception e) {
            LOG.error(" get executorService error , message :{} ", e.getMessage());
            return false;
        } finally {
            try {
                LOCK.unlock();
            } catch (Exception e) {
                LOG.info(" here is no lock ");
            }
        }
        return true;
    }

    /**
     * 判断任务队列是否满、判断当前最大线程数是否满
     * 
     * @param task
     * @return
     * @throws Exception
     */
    public static Future<List<IpPoolMainDTO>> submitTaskWork(Callable<List<IpPoolMainDTO>> task) throws Exception {
        getInstance();
        Future<List<IpPoolMainDTO>> resFuture = null;
        try {
            var largestPoolSize = EXECUTOR_SERVICE.getLargestPoolSize();
            LOG.info(" thread pool appear larget size :{} ", largestPoolSize);
            boolean flag = true;
            while (flag) {
                LOG.info(String.format(" thread pool core size :%5d\tmax core size : %5d\ton queue size :%5d",
                        CORE_POOL_SIZE, MAX_POOL_SIZE, BLOCKING_QUEUE.size()));
                if (EXECUTOR_SERVICE.getPoolSize() + BLOCKING_QUEUE.size() == MAX_TASK_SIZE) {
                    LOG.info(" over capacity, thread : {} will waiting 1 mininutes to continue detect ",
                            Thread.currentThread().getName());
                    Thread.sleep(1 * 60 * 1000);
                } else {
                    LOG.info(" Thread : {} ,submit work ", Thread.currentThread().getName());
                    resFuture = EXECUTOR_SERVICE.submit(task);
                    flag = false;
                }
            }
        } catch (Exception e) {
            LOG.error(" submit work error ", e.getMessage());
        }
        return resFuture;
    }

    private boolean checkThreadPoolService() {
        return EXECUTOR_SERVICE == null;
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
