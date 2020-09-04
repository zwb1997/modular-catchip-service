package com.zzz.common.threadconfig;

import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;

public class NamedThreadFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NamedThreadFactory.class);
    private final AtomicInteger workId = new AtomicInteger(0);
    private String workName = IpServiceConstant.XH_THREAD_NAME_PREFIEX;

    @Override
    public Thread newThread(Runnable r) {
        int id = workId.incrementAndGet();
        String nowTime = DateUtils.formatDate(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX);
        workName += "-" + id + "-" + nowTime;
        LOG.info(" create task : {} ", workName);
        return new Thread(r, workName);
    }
}
