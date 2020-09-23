package com.ipfetchservice.service.ipservices.abstractservice;

import java.util.Collection;
import java.util.Map;

import com.ipfetchservice.service.utils.HttpClientUtil;
import com.ipfetchservice.service.utils.PageUtil;
import com.ipfetchservice.service.utils.SignUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 抽象ip爬取服务
 */
@Component
public abstract class AbsrtactFetchIpService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbsrtactFetchIpService.class);
    // task name
    protected String taskName;
    // request util
    protected HttpClientUtil clientUtil;
    // page util
    protected PageUtil pageUtil;
    // sign util
    protected SignUtil signUtil;
    // page couts
    protected int pageNums;
    // page details lists
    protected Collection<?> pageList;
    // page details map
    protected Map<?, ?> pageMaps;

    protected static long START_TIME = 0L;
    protected static long END_TIME = 0L;

    protected AbsrtactFetchIpService() {
        this.clientUtil = new HttpClientUtil();
        this.pageUtil = new PageUtil();
        this.signUtil = new SignUtil();
        LOG.info(" combine utils success ");
    }

    protected abstract void serviceEntry();

    public void runTask() {
        serviceEntry();
    }

    public HttpClientUtil getClientUtil() {
        return clientUtil;
    }

    public PageUtil getPageUtils() {
        return pageUtil;
    }

    public SignUtil getSignUtil() {
        return signUtil;
    }

    public int getPageNums() {
        return pageNums;
    }

    public Map<?, ?> getPageMaps() {
        return pageMaps;
    }

    public Collection<?> getPageList() {
        return pageList;
    }

    @Override
    public void run() {
        LOG.info(" task : {} begin...", this.taskName);
        runTask();
    }
}
