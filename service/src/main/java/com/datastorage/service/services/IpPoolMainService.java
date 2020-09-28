package com.datastorage.service.services;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.basicaldto.responsedto.IpPoolMainDTO;
import com.datastorage.service.mapper.IpPoolMainDOMapper;
import com.datastorage.service.util.ModelConvertUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class IpPoolMainService {
    private static final Logger LOG = LoggerFactory.getLogger(IpPoolMainService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private ModelConvertUtil modelConvertUtil;
    @Autowired
    private IpPoolMainDOMapper ipPoolMainDOMapper;

    public void insertIpPoolMainDO(List<IpPoolMainDTO> IpPoolMainDTOs) {
        LOG.info(" insert ip services start ");
        List<IpPoolMainDO> poolMainDOs = modelConvertUtil.createIpPoolMainDOInstance(IpPoolMainDTOs);
        long startTime = System.currentTimeMillis();
        ipPoolMainDOMapper.insertIpPoolMainDO(poolMainDOs);
        long endTime = System.currentTimeMillis();
        long usingTime = (endTime - startTime);
        LOG.info(" insert ip services done ,use seconds :{},million seconds :{}", usingTime / 1000, usingTime);
    }
}
