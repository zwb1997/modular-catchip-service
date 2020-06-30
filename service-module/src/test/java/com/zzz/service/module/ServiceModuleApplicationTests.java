package com.zzz.service.module;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@SpringBootTest
class ServiceModuleApplicationTests {
    @Autowired
    DruidDataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;


    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void contextLoads() {
        System.out.println(dataSource);
    }

}
