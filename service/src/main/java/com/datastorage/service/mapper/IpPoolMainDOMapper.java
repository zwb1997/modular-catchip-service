package com.datastorage.service.mapper;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IpPoolMainDOMapper {

    void insertIpPoolMainDO(@Param("ipPoolMainModels") List<IpPoolMainDO> models);
}