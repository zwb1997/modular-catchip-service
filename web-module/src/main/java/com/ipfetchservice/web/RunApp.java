package com.ipfetchservice.web;

import java.util.Arrays;

import com.ipfetchservice.web.common.banner.ServiceBanner;
import com.ipfetchservice.service.BatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }, scanBasePackages = {
        "com.ipfetchservice.service", "com.ipfetchservice.web" })
public class RunApp {
    private static final Logger LOG = LoggerFactory.getLogger(RunApp.class);
    private static final Class<?>[] START_RESOURCES = { RunApp.class };
    // @Override
    // protected SpringApplicationBuilder configure(SpringApplicationBuilder
    // builder) {
    // return super.configure(builder.sources(RunApp.class, ServiceRun.class));
    // }

    public static void main(String[] args) {
        LOG.info(" fetching data service web module start ");
        SpringApplication application = new SpringApplication(RunApp.class);
        application.setBanner(new ServiceBanner());
        application.addPrimarySources(Arrays.asList(START_RESOURCES));
        ConfigurableApplicationContext applicationContext = application.run(args);
        // start batch tasks
        applicationContext.getBean(BatchService.class).tasksRunEntry();
        // var pageUtil = applicationContext.getBean(PageUtil.class);
        // LOG.info(" page util entity :{} ",pageUtil);
    }
}
