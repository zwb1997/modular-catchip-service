package com.zzz.service.module;

import com.zzz.service.ipservices.XHFetchService;
import com.zzz.service.ipservices.XHFetchService1;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceModuleApplicationTests {


    @Test
    void contextLoads() {
       XHFetchService XH = new XHFetchService();
       XHFetchService1 XH1 = new XHFetchService1();
       System.out.println(XH.num);
       System.out.println(XH1.num);
       XH.num = 111;
       XH1.num=222;
       System.out.println(XH.num);
       System.out.println(XH1.num);
    }

}
