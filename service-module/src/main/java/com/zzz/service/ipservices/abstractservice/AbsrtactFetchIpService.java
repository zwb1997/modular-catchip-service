package com.zzz.service.ipservices.abstractservice;

import com.zzz.utils.HttpClientUtil;
import com.zzz.utils.PageUtils;
import com.zzz.utils.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 试试责任链 让多个服务穿成一串
 * ip爬取通用服务模板
 * 1.httpclient 请求
 * 2.page 页码检测
 * 3.page 信息提取
 * 4.存储数据
 */
public abstract class AbsrtactFetchIpService implements FetchingService {
    private static final Logger LOG = LoggerFactory.getLogger(AbsrtactFetchIpService.class);
    // request util
    protected HttpClientUtil clientUtil;
    // page util
    protected PageUtils pageUtils;
    // sign util
    protected SignUtil signUtil;
    public int num;
    protected AbsrtactFetchIpService(){
        this.clientUtil = new HttpClientUtil();
        this.pageUtils = new PageUtils();
        this.signUtil = new SignUtil();
        LOG.info(" combine utils success ");
    }
    protected abstract int doFetchPageNums();
    protected abstract int doExtractPageInfos();
    protected abstract boolean sendData();
}
