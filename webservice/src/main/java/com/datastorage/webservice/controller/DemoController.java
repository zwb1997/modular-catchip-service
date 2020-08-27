package com.datastorage.webservice.controller;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.datastorage.service.services.DemoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("d1")
public class DemoController {
    private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private DemoService demoService;

    @RequestMapping("c1")
    @ResponseBody
    public List<String> getStrings(HttpServletRequest req, HttpServletResponse res) {
        LOG.info(" inside  getStrings() ,print request hreaders : ");
        Enumeration<String> enums = req.getHeaderNames();
        while (enums.hasMoreElements()) {
            String header = enums.nextElement();
            String val = req.getHeader(header);
            LOG.info(" header : {} ,val : {}", header, val);
        }
        return demoService.getStrings();
    }
}