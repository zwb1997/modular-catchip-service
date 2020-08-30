package com.datastorage.webservice.controller;

import com.datastorage.models.basicalmodels.basicaldto.responsedto.IpPoolMainDTO;
import com.datastorage.models.basicalmodels.basicaldto.responsedto.ResponseModel;
import com.datastorage.service.services.IpPoolMainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(")Pt&Bw+Vk")
public class IpStageController {
    private static final Logger LOG = LoggerFactory.getLogger(IpStageController.class);

    @Autowired
    private IpPoolMainService ipPoolMainService;

    /**
     * 1 -> ip
     * 2 -> port
     * 3 -> ip地址
     * 4 -> ip供应商
     * 5 -> 是否支持https
     * 6 -> 是否支持post请求
     * 7 -> 匿名程度
     * 8 -> 速度
     * 9 -> 网站检测 ip入库时间
     * 10 -> 网站检测 ip最后有效时间
     * @param IpPoolMainDOs
     * @return
     */
    @RequestMapping("=Js)Ns+My")
    @ResponseBody
    public ResponseModel insertIpPoolMainDO(@RequestBody List<IpPoolMainDTO> IpPoolMainDOs) {
        LOG.info(" insert controller mapping  ");
        Assert.notEmpty(IpPoolMainDOs," list cannot empty ");
        ipPoolMainService.insertIpPoolMainDO(IpPoolMainDOs);
        return ResponseModel.buildResult();
    }
}
