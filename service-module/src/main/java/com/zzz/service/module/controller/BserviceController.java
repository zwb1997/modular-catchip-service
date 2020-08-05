package com.zzz.service.module.controller;

import com.zzz.basemodels.ResultModel;
import com.zzz.basemodels.enummodel.ResponseCode;
import com.zzz.entitymodel.servicebase.DTO.VideoUpdateInfoDTO;
import com.zzz.service.module.common.exception.DebugException;
import com.zzz.service.module.service.bservices.FetchingBDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping("bsbservice")
public class BserviceController {
    private static Logger LOG = LoggerFactory.getLogger(BserviceController.class);

    @Autowired
    private FetchingBDataService fetchingBDataService;

    @RequestMapping(value = "/service/searchingActors")
    @ResponseBody
    public ResultModel<VideoUpdateInfoDTO> searchingActors(
            @RequestParam("usId")
                    String usId) throws IOException, URISyntaxException {
        if(StringUtils.isBlank(usId)){
            throw new DebugException("查询id不能为空");
        }
        return  ResultModel.buildResult(fetchingBDataService.getInfoLisByMid(usId), ResponseCode.SUCCESS);
    }



}
