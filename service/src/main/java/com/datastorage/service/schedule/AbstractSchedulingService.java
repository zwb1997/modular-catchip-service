package com.datastorage.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public abstract class AbstractSchedulingService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSchedulingService.class);


    protected abstract void doSchedulingTask();
    @Override
    public void run() {
        doSchedulingTask();
    }
}
