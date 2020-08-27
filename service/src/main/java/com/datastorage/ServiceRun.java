package com.datastorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class ServiceRun {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceRun.class);

    public static void main(String[] args) {
        LOG.info(" service start ");
        SpringApplication.run(ServiceRun.class, args);
    }
}
