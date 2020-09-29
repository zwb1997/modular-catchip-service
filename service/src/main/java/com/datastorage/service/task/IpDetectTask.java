package com.datastorage.service.task;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.basicaldto.responsedto.IpPoolMainDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpDetectTask implements Callable<List<IpPoolMainDO>> {
    private static final Logger LOG = LoggerFactory.getLogger(IpDetectTask.class);
    private ConcurrentLinkedDeque<IpPoolMainDO> workList;
    
    public IpDetectTask(List<IpPoolMainDO> workList) {
        this.workList = new ConcurrentLinkedDeque<IpPoolMainDO>(workList);
    }

    @Override
    public List<IpPoolMainDO> call() throws Exception {
        LOG.info("detect work starting...");
        List<IpPoolMainDO> alreadyDetectList = new LinkedList<>();
        try {
            while(true){
                if(workList.isEmpty()){
                    LOG.info("thread >>{} -> workList is empty ,will end task",Thread.currentThread().getName());
                    break;
                }
                IpPoolMainDO curModel = workList.pollLast();
            }
        } catch (Exception e) {
            LOG.error(" IpDetectTask error , message :{}", e.getMessage());
        }
        return alreadyDetectList;
    }
}
