package com.zzz.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;
import com.zzz.service.ipservices.NiMaIpFetchService;
import com.zzz.service.ipservices.XiaoHuanIpFetchService;
import com.zzz.service.ipservices.abstractservice.AbsrtactFetchIpService;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class BatchService {
    private static final Logger LOG = LoggerFactory.getLogger(BatchService.class);
    private static final List<AbsrtactFetchIpService> TASK_LIST = new LinkedList<>();
    private static long START_TIME;
    private static long END_TIME;

    static{
        TASK_LIST.add(new XiaoHuanIpFetchService());
        TASK_LIST.add(new NiMaIpFetchService());
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void doBatchWorkj() {
        START_TIME = System.currentTimeMillis();
        LOG.info(" batch servie start , time :{} ",
                DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        for (AbsrtactFetchIpService service : TASK_LIST) {
            service.runTask();
        }
        END_TIME = System.currentTimeMillis();
        LOG.info(
                " all batch serice were done , will waiting 10 miniutes to Reincarnation.\ncurrent time :{}\tusing Time : total miniutes:{}\ttotal seconds:{}",
                DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX),
                (END_TIME - START_TIME) / 1000 / 60, (END_TIME - START_TIME) / 1000);
        START_TIME = 0L;
        END_TIME = 0L;
    }

}
