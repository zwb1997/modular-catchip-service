package com.datastorage.service.schedule.impl;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.queryparams.IpPoolMainDOParams;
import com.datastorage.service.config.executorprovider.ThreadPoolProvider;
import com.datastorage.service.schedule.AbstractSchedulingService;
import static com.datastorage.models.basicalmodels.basicalconstants.IpServiceConstant.*;

@Service("detectService")
public class DetectService extends AbstractSchedulingService {

    private static final Logger LOG = LoggerFactory.getLogger(DetectService.class);
    private static final ThreadPoolProvider THREAD_POOL_PROVIDER = ThreadPoolProvider.getInstance();

    @Override
    protected void doSchedulingTask() {
        LOG.info(" ip available detecting service begin... ");
        String sql = "SELECT COUNT(ip_num) as ipcounts FROM ip_pool_main";
        Map<String, Object> sqlResultMap = jdbcTemplate.queryForMap(sql, new Object[] {});
        int ipCounts = Integer.parseInt(sqlResultMap.get("ipcounts").toString());
        int pos = 0;
        int step = ipCounts / (ThreadPoolProvider.getCorePoolSize() * 5 << 1);
        List<Future<List<IpPoolMainDO>>> futures = new LinkedList<>();
        while (pos < ipCounts) {

            int limitCount = Math.min(ipCounts, pos + step);
            List<IpPoolMainDO> models = IpPoolMainDOMapper.selectOnlyIpAndPort(new IpPoolMainDOParams(pos, limitCount));

            try {
                Pair<Future<List<IpPoolMainDO>>, Boolean> results = THREAD_POOL_PROVIDER.submitTaskWork(() -> {
                    List<IpPoolMainDO> lists = models;
                    for (IpPoolMainDO model : lists) {
                        Pair<Boolean, Boolean> detectPair = clientUtil.detectProxyCanUse(model);
                        if (detectPair.getValue0()) {
                            model.setStatusCode(DETECT_DOMESTIC);
                        } else if (detectPair.getValue0()) {
                            model.setStatusCode(DETECT_FOREIGN);
                        } else {
                            model.setStatusCode(DETECT_UNAVAILABLE);
                            LOG.info(" ip :{} is unavailable ", model.getIpNum());
                        }
                    }
                    return lists;
                });
                if (results.getValue1()) {
                    futures.add(results.getValue0());
                } else {
                    LOG.info(" callable not successfully commit ");
                }
            } catch (Exception e) {
                LOG.error(" submit work error , message :{} ", e.getMessage());
            }
            pos += step;
        }
        // update data to database
        refreshDataBack(futures);
    }

    /**
     * one by one update
     * 
     * @param futures
     */
    private void refreshDataBack(List<Future<List<IpPoolMainDO>>> futures) {
        for (Future<List<IpPoolMainDO>> f : futures) {
            try {
                List<IpPoolMainDO> result = f.get();
                IpPoolMainDOMapper.updateIpPoolMainDO(result);
            } catch (Exception ex) {
                LOG.error(" update list error ", ex);
            }
        }
    }
}
