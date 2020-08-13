package com.zzz.service.module.common.threadconfig;

import org.apache.http.client.utils.DateUtils;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger workId = new AtomicInteger(0);
    private String workName = "IpFetchTask";

    @Override
    public Thread newThread(Runnable r) {
        int id = workId.incrementAndGet();
        String nowTime = DateUtils.formatDate(new Date()," yyyy-MM-dd HH:mm:ss ");
        workName += id + nowTime;
        return new Thread(workName);
    }
}
