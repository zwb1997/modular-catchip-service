package com.ipfetchservice.service;

import java.util.Date;

import com.ipfetchservice.service.ipservices.abstractservice.AbsrtactFetchIpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
    private static final int TASK_DELAY_INTERVAL = 3 * 60 * 1000;
    private static final Logger LOG = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    @Qualifier("taskscheduler")
    private ThreadPoolTaskScheduler taskscheduler;

    @Autowired
    @Qualifier("xiaohuanipfetchservice")
    private AbsrtactFetchIpService xhFetchIpService;

    @Autowired
    @Qualifier("nimaipfetchservice")
    private AbsrtactFetchIpService nimaFetchIpService;

    public void tasksRunEntry() {
        LOG.info(" tasks entry ");
        // taskscheduler.scheduleWithFixedDelay(xhFetchIpService, new Date(), TASK_DELAY_INTERVAL);
        taskscheduler.scheduleWithFixedDelay(nimaFetchIpService, new Date(), TASK_DELAY_INTERVAL);
    }

}
