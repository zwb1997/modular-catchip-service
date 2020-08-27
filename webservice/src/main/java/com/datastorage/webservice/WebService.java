package com.datastorage.webservice;

import com.datastorage.service.ServiceRun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},scanBasePackages = "com.datastorage")
public class WebService extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(WebService.class);
    private static Class<?>[] RUN_CLASSES = new Class[] { ServiceRun.class, WebService.class };

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // TODO Auto-generated method stub
        LOG.info(" loading springboot start reources ,{} ",String.valueOf(RUN_CLASSES));
        return super.configure(builder.sources(RUN_CLASSES));
    }
    public static void main(String[] args) {
        LOG.info(" web service start ");
        SpringApplication.run(WebService.class, args);
    }
}
