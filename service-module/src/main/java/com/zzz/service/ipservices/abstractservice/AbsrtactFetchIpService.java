package com.zzz.service.ipservices.abstractservice;

import java.util.Collection;
import java.util.Map;

import com.zzz.utils.HttpClientUtil;
import com.zzz.utils.PageUtil;
import com.zzz.utils.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 试试责任链 让多个服务穿成一串 ip爬取通用服务模板 1.httpclient 请求 2.page 页码检测 3.page 信息提取 4.存储数据
 */
public abstract class AbsrtactFetchIpService {
    private static final Logger LOG = LoggerFactory.getLogger(AbsrtactFetchIpService.class);
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

    protected void runTask() {
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
}
