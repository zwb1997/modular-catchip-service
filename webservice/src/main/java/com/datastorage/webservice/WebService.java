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
 * start at webservice
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},scanBasePackages = {"com.datastorage"})
public class WebService extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(WebService.class);
    private static Class<?>[] RUN_CLASSES = new Class[] { ServiceRun.class, WebService.class };

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        LOG.info(" loading springboot start reources ,{} ", getStartUpLoadingClassName());
        return super.configure(builder.sources(RUN_CLASSES));
    }
    public static void main(String[] args) {
        LOG.info(" web service start ");
        SpringApplication.run(WebService.class, args);
    }

    public String getStartUpLoadingClassName(){
        StringBuffer stringBuffer = new StringBuffer();
        for(Class c : RUN_CLASSES){
            stringBuffer.append(c.getName()+",");
        }
        return stringBuffer.toString();
    }
}
