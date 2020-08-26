package com.zzz.web.common.webcofig;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zzz.web.common.interceptor.RefereIncterceptor;
import com.zzz.web.common.interceptor.ValidInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 消息转换
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 所有的double类型返回保留三位小数
        module.addSerializer(Double.class, NumberSerializer.instance);
        module.addSerializer(Double.TYPE, NumberSerializer.instance);

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new SimpleModule());
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
    }

    /**
     * 跨域
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*");
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new RefereIncterceptor());
        registry.addInterceptor(new ValidInterceptor());
    }


}
