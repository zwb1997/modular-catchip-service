package com.datastorage.service.config;

import com.datastorage.service.schedule.AbstractSchedulingService;
import com.datastorage.service.schedule.SchedulingServiceEntry;
import com.datastorage.service.schedule.impl.DetectService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleConfig {
    
    @Bean("taskscheduler")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "Task-storage-Scheduler");
        return threadPoolTaskScheduler;
    }
}
