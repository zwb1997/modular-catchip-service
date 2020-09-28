package com.datastorage.service.mapper;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.queryparams.IpPoolMainDOParams;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IpPoolMainDOMapper {

    void insertIpPoolMainDO(@Param("ipPoolMainModels") List<IpPoolMainDO> models);

    List<IpPoolMainDO> selectOnlyIpAndPort(@Param("queryParam")IpPoolMainDOParams queryParam);
}