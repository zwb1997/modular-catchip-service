package com.zzz.service.module.mapper;

import com.zzz.entitymodel.servicebase.DO.ActorModel;
import com.zzz.service.module.params.TestParams;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface ActorMapper {
    List<ActorModel> queryActors(@Param("params") TestParams params);
}
