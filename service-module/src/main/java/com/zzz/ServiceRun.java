package com.zzz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ServiceRun {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceRun.class);
    public static void main(String[] args) {
        LOG.info(" fetching data service service module start ");
        SpringApplication.run(ServiceRun.class,args);
    }
}
