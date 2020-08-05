package com.zzz.service.module.controller;


import com.zzz.service.module.service.ipservices.IpFetchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("ipser")
@Controller
public class IpFetchController {
    private  static final Logger LOG = LoggerFactory.getLogger(IpFetchController.class);
    @Autowired
    private IpFetchService ipFetchService;
    @RequestMapping("xh")
    @ResponseBody
    public void doXiaohuanIps(){
        ipFetchService.run();
    }
}
