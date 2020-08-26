package com.zzz.web;
import com.zzz.ServiceRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RunApp extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(RunApp.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder.sources(RunApp.class, ServiceRun.class));
    }

    public static void main(String[] args) {
        LOG.info(" fetching data service web module start ");
        SpringApplication.run(RunApp.class, args);
    }
}
