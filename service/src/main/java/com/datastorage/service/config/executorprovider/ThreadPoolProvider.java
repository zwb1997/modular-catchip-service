package com.datastorage.service.config.executorprovider;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.basicaldto.responsedto.IpPoolMainDTO;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolProvider.class);
    // the executorService instance
    private ThreadPoolExecutor poolExecutor;
    private static volatile ThreadPoolProvider POOL_PROVIDER;
    private static final int BLOCKING_QUEUE_SIZE = 12;
    private final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(BLOCKING_QUEUE_SIZE);
    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 6;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int MAX_TASK_SIZE = MAX_POOL_SIZE + BLOCKING_QUEUE_SIZE;
    private static final TimeUnit ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final Lock LOCK = new ReentrantLock();

    private ThreadPoolProvider() {
        poolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, ALIVE_TIME_UNIT,
                BLOCKING_QUEUE, (Runnable r, ThreadPoolExecutor executor) -> {
                    LOG.info(" current task type is not FutureTask will discard {}", r.getClass().getSimpleName());
                });
    }

    /**
     * instance will lazy loading
     * 
     * @return
     */
    public static ThreadPoolProvider getInstance() {
        try {
            if (POOL_PROVIDER == null) {
                LOCK.tryLock();
                if (POOL_PROVIDER == null) {
                    POOL_PROVIDER = new ThreadPoolProvider();
                    return POOL_PROVIDER;
                }
            }
        } catch (Exception e) {
            LOG.error(" get executorService error will return null; message :{} ", e.getMessage());
        } finally {
            try {
                LOCK.unlock();
            } catch (Exception e) {
                LOG.info(" instance already exists, here is no lock , message : {} ", e.getMessage());
            }
        }
        return POOL_PROVIDER;
    }

    /**
     * 判断任务队列是否满、判断当前最大线程数是否满
     * 
     * @param task
     * @return Pair<Future<List<IpPoolMainDTO>>,Boolean>
     * @throws Exception
     */
    public Pair<Future<List<IpPoolMainDO>>, Boolean> submitTaskWork(Callable<List<IpPoolMainDO>> task)
            throws Exception {
        Pair<Future<List<IpPoolMainDO>>, Boolean> pair = Pair.with(null, false);
        try {
            var largestPoolSize = poolExecutor.getLargestPoolSize();
            LOG.info(" thread pool appear larget size :{} ", largestPoolSize);
            boolean flag = true;
            int workingSize = poolExecutor.getPoolSize();
            while (flag) {
                LOG.info(String.format(
                        "thread: %5s\tthread pool core size :%5d\tmax core size : %5d\tmax queue size %5d\ton queue size :%5d\trunning task counts :%5d",
                        Thread.currentThread().getName(), CORE_POOL_SIZE, MAX_POOL_SIZE, BLOCKING_QUEUE_SIZE,
                        BLOCKING_QUEUE.size(), workingSize));
                if (workingSize + BLOCKING_QUEUE.size() == MAX_TASK_SIZE) {
                    LOG.info(
                            "thread : {}\tinfo: task pool cannot accept new task size overflow,will waiting 1 mininutes to continue detect ",
                            Thread.currentThread().getName());
                    Thread.sleep(1 * 60 * 1000);
                } else {
                    LOG.info(" Thread : {} ,submit work ", Thread.currentThread().getName());
                    pair = Pair.with(poolExecutor.submit(task), true);
                    flag = false;
                }
            }
        } catch (Exception e) {
            LOG.error(" submit work error ", e.getMessage());
        }
        return pair;
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
