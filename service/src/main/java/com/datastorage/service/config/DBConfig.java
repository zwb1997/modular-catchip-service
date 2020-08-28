package com.datastorage.service.config;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@PropertySource(value = { "classpath:db/db_dev.properties" })
public class DBConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DBConfig.class);

    @Value("${druid.jdbc.url}")
    private String url;
    @Value("${druid.jdbc.user}")
    private String user;
    @Value("${druid.jdbc.password}")
    private String password;
    @Value("${druid.jdbc.filters}")
    private String filters;
    @Value("${mybatis.mapper.location}")
    private String mapperLocation;

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource createDataSource() {
        LOG.info(" druid datasource init.... ");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        // dataSource.setFilters(filters);
        dataSource.setMaxActive(20);
        dataSource.setInitialSize(1);
        dataSource.setMaxWait(60000);
        dataSource.setMinIdle(1);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(20);
        dataSource.setAsyncInit(true);

        Slf4jLogFilter logFilter = new Slf4jLogFilter();
        logFilter.setStatementExecutableSqlLogEnable(true);
        logFilter.setStatementLogEnabled(true);
        logFilter.setStatementSqlPrettyFormat(true);
        List<Filter> filterList = Arrays.asList(logFilter);
        dataSource.setProxyFilters(filterList);
        return dataSource;
    }
    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(createDataSource());
    }
    @Bean
    public NamedParameterJdbcTemplate getNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(createDataSource());
    }
    @Bean("sqlSessionFactory1")
    public SqlSessionFactory createSqlSessionFactory() {
        LOG.info(" using sqlSessionFactoryBean to create sqlSessionFactory ");
        try {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(createDataSource());
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
            sqlSessionFactoryBean.setFailFast(true);
            sqlSessionFactoryBean.setMapperLocations(new ClassPathResource(mapperLocation));
            return sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            LOG.error(" create sqlSessionFactory error, message :{} ", e.getMessage());
        }
        return null;
    }
    @Bean("createSqlSessionTemplate")
    public SqlSessionTemplate createSqlSessionTemplate(){
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(createSqlSessionFactory());
        return sqlSessionTemplate;
    }
    @Bean
    public static MapperScannerConfigurer createScannerConfigurer(){
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.datastorage.service.mapper");
        scannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory1");
        scannerConfigurer.setSqlSessionTemplateBeanName("createSqlSessionTemplate");
        return scannerConfigurer;
    }
}