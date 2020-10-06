package com.datastorage.webservice;

import com.datastorage.service.schedule.SchedulingServiceEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author
 */
@SpringBootApplication(scanBasePackages = {"com.datastorage.webservice", "com.datastorage.service"})
@ComponentScan({"com.datastorage.service"})
public class WebService extends SpringBootServletInitializer implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(WebService.class);

    @Autowired
    @Qualifier("schedulingServiceEntry")
    private SchedulingServiceEntry serviceEntry;
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        LOG.info(" SpringApplicationBuilder  configure ");
        return builder.sources(WebService.class);
    }
    // public static void main(String[] args) {
    //     LOG.info(" web service start ");
    //     SpringApplication application = new SpringApplication(RUN_CLASSES);
    //     ConfigurableApplicationContext context = application.run(args);
    //     LOG.info(" begin scheduling service ");
    //     context.getBean(SchedulingServiceEntry.class).runSchedulingService();
    // }

    @Override
    public void run(String... args) throws Exception {
        LOG.info(" scheduling service will start... ");
        serviceEntry.runSchedulingService();
    }
}
