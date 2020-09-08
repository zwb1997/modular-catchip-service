package com.zzz.web;
import com.zzz.ServiceRun;
import com.zzz.web.common.banner.ServiceBanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RunApp  {
    private static final Logger LOG = LoggerFactory.getLogger(RunApp.class);
    private static final Class<?>[] START_RESOURCES = {RunApp.class,ServiceRun.class};
    // @Override
    // protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    //     return super.configure(builder.sources(RunApp.class, ServiceRun.class));
    // }

    public static void main(String[] args) {
        LOG.info(" fetching data service web module start ");
        SpringApplication application = new SpringApplication(RunApp.class);
        application.setBanner(new ServiceBanner());
        SpringApplication.run(START_RESOURCES,args);
    }
}
