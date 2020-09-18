package com.zzz.service.ipservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class NiMaIpFetchService {
    private static final Logger LOG = LoggerFactory.getLogger(NiMaIpFetchService.class);

//    @Scheduled(fixedDelay = 3000)
    public void run(){
        LOG.info(" nima ip works begin ");
        // 页码检测
        // 封装link
        // 数据解析
        // 入库
    }






}
