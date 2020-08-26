package com.zzz.mapper;
import com.zzz.entitymodel.servicebase.DO.IpPoolMainDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface IpDataServiceXiaoHuanMapper {
    /**
     * 新增ippool数据 来源：小幻IP
     * @param videoInfos
     * @return
     */
    int insertIpDataXiaoHuan(@Param("ipInfos") List<IpPoolMainDO> videoInfos);
}
