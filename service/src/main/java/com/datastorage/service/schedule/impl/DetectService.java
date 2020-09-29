package com.datastorage.service.schedule.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.queryparams.IpPoolMainDOParams;
import com.datastorage.service.config.executorprovider.ThreadPoolProvider;
import com.datastorage.service.mapper.IpPoolMainDOMapper;
import com.datastorage.service.schedule.AbstractSchedulingService;

@Service("DetectService")
public class DetectService extends AbstractSchedulingService {

    private static final Logger LOG = LoggerFactory.getLogger(DetectService.class);
    private static final ThreadPoolProvider THREAD_POOL_PROVIDER = ThreadPoolProvider.getInstance();
    @Autowired
    private IpPoolMainDOMapper IpPoolMainDOMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    protected void doSchedulingTask() {
        LOG.info(" ip available detecting service begin... ");
        String sql = "SELECT COUNT(ip_num) as ipcounts FROM ip_pool_main";
        Map<String,Object> sqlResultMap = jdbcTemplate.queryForMap(sql, new Object[] {});
        int ipCounts = (Integer)sqlResultMap.get("ipcounts");
        int pos = 0;
        int step = ipCounts / ThreadPoolProvider.getCorePoolSize() << 1;
        while (pos < ipCounts) {
            int limitCount = Math.min(ipCounts, pos + step);
            List<IpPoolMainDO> models = IpPoolMainDOMapper.selectOnlyIpAndPort(new IpPoolMainDOParams(pos, limitCount));
            convertInetAddress(models);
            try {
                THREAD_POOL_PROVIDER.submitTaskWork(null);
            } catch (Exception e) {
                LOG.error(" submit work error , message :{} ", e.getMessage());
            }
            pos += step;
        }
    }

    private void convertInetAddress(List<IpPoolMainDO> models) {
        for(IpPoolMainDO ipPoolMainDO : models){
            
        }
        
    }

}
