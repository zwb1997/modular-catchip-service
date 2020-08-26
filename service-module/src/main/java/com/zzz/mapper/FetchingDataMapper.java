package com.zzz.mapper;

import com.zzz.entitymodel.servicebase.DO.VideoInfoModelDO;
import com.zzz.entitymodel.servicebase.DO.VideoTypeInfoDO;
import com.zzz.paramsmodel.servicebase.BQueryParamsDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface FetchingDataMapper {
    List<VideoTypeInfoDO> queryInfo(@Param("keyword") BQueryParamsDTO queryParams);

    int insertBBaseInfo(@Param("infoList")List<VideoTypeInfoDO> videoInfos);

    int insertBVideoInfo(@Param("videoModels")List<VideoInfoModelDO> videoModels);

    List<VideoTypeInfoDO> queryVideoTypeInfo(@Param("queryParams")BQueryParamsDTO queryParams);
}
