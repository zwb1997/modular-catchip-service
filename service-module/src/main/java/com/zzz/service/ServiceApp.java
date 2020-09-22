package com.zzz.service;

import java.util.Date;

import com.zzz.model.entitymodel.servicebase.constants.IpServiceConstant;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ServiceApp {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceApp.class);
    public static void main(String[] args) {
        LOG.info(" fetching data service module start ");
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ServiceApp.class,args);
        LOG.info(" batch service start , time : {} ",DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        applicationContext.getBean(BatchService.class).tasksRunEntry();
    }
}
