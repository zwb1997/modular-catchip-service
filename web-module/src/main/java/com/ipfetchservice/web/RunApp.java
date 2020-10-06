package com.ipfetchservice.web;

import java.util.Arrays;

import com.ipfetchservice.web.common.banner.ServiceBanner;
import com.ipfetchservice.service.BatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }, scanBasePackages = {
        "com.ipfetchservice.service", "com.ipfetchservice.web" })
public class RunApp extends SpringBootServletInitializer implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(RunApp.class);
    private static final Class<?>[] START_RESOURCES = { RunApp.class };

    @Autowired
    @Qualifier("BatchService")
    private BatchService batchService;
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        LOG.info(" SpringApplicationBuilder  configure ");
        return builder.sources(RunApp.class).banner(new ServiceBanner());
    }
//
//    public static void main(String[] args) {
//        LOG.info(" fetching data service web module start ");
//        SpringApplication application = new SpringApplication(RunApp.class);
//        application.setBanner(new ServiceBanner());
//        application.addPrimarySources(Arrays.asList(START_RESOURCES));
//        ConfigurableApplicationContext applicationContext = application.run(args);
//        // start batch tasks
//        applicationContext.getBean(BatchService.class).tasksRunEntry();
//    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info(" batchService starting... ");
        batchService.tasksRunEntry();
    }
}
