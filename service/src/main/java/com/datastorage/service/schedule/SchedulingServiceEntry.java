package com.datastorage.service.schedule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("schedulingServiceEntry")
public class SchedulingServiceEntry {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulingServiceEntry.class);
    @Autowired
    private ThreadPoolTaskScheduler poolScheduler;
    @Autowired
    @Qualifier("detectService")
    private AbstractSchedulingService detectService;

    public void runSchedulingService() {
        poolScheduler.scheduleWithFixedDelay(detectService, new Date(), 60 * 60 * 1000);
    }

}
