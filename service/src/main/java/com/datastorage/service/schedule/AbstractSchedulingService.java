package com.datastorage.service.schedule;

import com.datastorage.service.util.HttpClientUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author
 */
@Service("AbstractSchedulingService")
public abstract class AbstractSchedulingService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSchedulingService.class);
    // @Autowired
    // @Qualifier("httpclientutil")
    // protected HttpClientUtil clientUtil;
    protected HttpClientUtil clientUtil;
    @Autowired
    protected com.datastorage.service.mapper.IpPoolMainDOMapper IpPoolMainDOMapper;
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected AbstractSchedulingService(){
        clientUtil = new HttpClientUtil();
    }
    @Override
    public void run() {
        doSchedulingTask();
    }

    /**
     *  programming details in sub-class
     */
    protected abstract void doSchedulingTask();
}
