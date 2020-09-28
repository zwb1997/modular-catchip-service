package com.datastorage.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSchedulingService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSchedulingService.class);


    protected abstract void doSchedulingTask();
    @Override
    public void run() {
        doSchedulingTask();
    }
}
