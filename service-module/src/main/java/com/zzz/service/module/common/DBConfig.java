package com.zzz.service.module.common;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ZZZ
 * druid + mybatis config
 */
@Configuration
//@PropertySources({@PropertySource("classpath:db/dev_db.properties")})
@PropertySources({@PropertySource("classpath:db/prod_db.properties")})
//@ImportResource("classpath:db/dev_db.properties")
public class DBConfig {

    @Value("${druid.jdbc.url}")
    private String url;
    @Value("${druid.jdbc.user}")
    private String user;
    @Value("${druid.jdbc.password}")
    private String password;
    @Value("${druid.filter}")
    private String druidLogFilterSlf4j;

    @Bean(initMethod = "init", destroyMethod = "close")
    public DruidDataSource druidDataSource() throws SQLException {

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(user);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setMaxActive(20);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxWait(60000L);
        druidDataSource.setMinIdle(1);

        druidDataSource.setTimeBetweenEvictionRunsMillis(60000L);
        druidDataSource.setMinEvictableIdleTimeMillis(300000L);

        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);

        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxOpenPreparedStatements(20);
        druidDataSource.setAsyncInit(true);
        List<Filter> filters = new ArrayList<>();
        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        //大量数据 应当关闭
        slf4jLogFilter.setResultSetLogEnabled(false);
        slf4jLogFilter.setStatementLogEnabled(true);
        slf4jLogFilter.setStatementCreateAfterLogEnabled(false);
        slf4jLogFilter.setStatementExecuteAfterLogEnabled(false);
        slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
        slf4jLogFilter.setStatementLogErrorEnabled(true);
        filters.add(slf4jLogFilter);
        druidDataSource.setProxyFilters(filters);
        druidDataSource.setFilters(druidLogFilterSlf4j);
        boolean s = slf4jLogFilter.isStatementParameterSetLogEnabled();
        return druidDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException, SQLException {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(druidDataSource());
        sqlSessionFactoryBean.setFailFast(true);
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mybatis/mappers/*Mapper.xml");
        sqlSessionFactoryBean.setMapperLocations(resources);
        sqlSessionFactoryBean.setConfigLocation(
                new DefaultResourceLoader().getResource("classpath:mybatis/mybatis-config.xml"));
        return sqlSessionFactoryBean;
    }

    /**
     * 这里修饰成static 方法 url 等参数就能注入进来了...暂时不明白为啥
     *
     * @return
     */
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.zzz.service.module.mapper");
        return mapperScannerConfigurer;
    }



}
