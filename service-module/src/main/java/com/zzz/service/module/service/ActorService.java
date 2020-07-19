package com.zzz.service.module.service;

import com.zzz.entitymodel.servicebase.DO.ActorModel;
import com.zzz.service.module.mapper.ActorMapper;
import com.zzz.service.module.params.TestParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActorService {

    @Autowired
    private ActorMapper actorMapper;
    public List<ActorModel> actorModels(TestParams params){
        if(StringUtils.isBlank(params.getLastName())){
            return new ArrayList<>();
        }
        return actorMapper.queryActors(params);
    }

}
