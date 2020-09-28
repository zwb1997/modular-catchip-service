package com.datastorage.service.schedule.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datastorage.service.mapper.IpPoolMainDOMapper;
import com.datastorage.service.schedule.AbstractSchedulingService;
@Service("DetectService")
public class DetectService extends AbstractSchedulingService{
    
    private static final Logger LOG = LoggerFactory.getLogger(DetectService.class);

    @Autowired
    private IpPoolMainDOMapper IpPoolMainDOMapper;
    
    @Override
    protected void doSchedulingTask() {
        LOG.info(" ip available detecting service begin... ");


    }
}
