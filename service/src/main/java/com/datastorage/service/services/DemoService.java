package com.datastorage.service.services;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DemoService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    public List<String> getStrings(){
        LOG.info(" inside DemoService() ");
        return Arrays.asList("123","456","789");
    }
}