package com.zzz.service.module.service.ipservices.work;

import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.zzz.service.module.params.IpServiceConstant.*;
import static com.zzz.service.module.utils.HttpClientUtil.exeuteDefaultRequest;
import static com.zzz.service.module.utils.HttpClientUtil.vaildateReponse;
import static com.zzz.service.module.utils.PageUtils.vaildateEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import static com.zzz.service.module.utils.PageUtils.*;
public class GetpageInfoTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(GetpageInfoTask.class);
    private Stack<IpLocation> workStack;
    public GetpageInfoTask(Stack<IpLocation> workStack){
        this.workStack = workStack;
    }


    @Override
    public void run() {
        boolean flag = true;
        while(flag){
            String name = Thread.currentThread().getName();
            LOG.info(" current thread : {} doing job ",name);
            try{
                if(workStack.isEmpty()){
                    flag = false;
                    continue;
                }
                Thread.sleep(3 * 1000);
                IpLocation target = workStack.pop();
                String fullUrl = XIAO_HUAN_IP + target.getLocationHref();
                LOG.info(" begin fetching these pages ");
                URI uri = new URIBuilder(fullUrl)
                        .setScheme("https")
                        .build();
                String curUriString = uri.toString();
                HttpGet get = new HttpGet(uri);
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                LOG.info(" do with current type :{} ", curUriString);
                HttpResponse response = exeuteDefaultRequest(get, headerList);
                vaildateReponse(response);
                HttpEntity httpEntity = response.getEntity();
                String currentPage = vaildateEntity(httpEntity);
                Elements elements = fetchElementWithSection(currentPage,HAS_PAGE_REGIX);

                flag = false;
            }catch(Exception e){
                LOG.error(" error , message : {} ",e.getMessage());
            }finally {
                LOG.info(" current thread : {0} rest job counts : {1} ");
            }
        }
    }
}
