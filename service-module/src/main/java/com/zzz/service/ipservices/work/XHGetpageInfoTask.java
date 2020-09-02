package com.zzz.service.ipservices.work;

import com.zzz.entitymodel.servicebase.DO.IpPoolMainDO;
import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.entitymodel.servicebase.DTO.IpPoolMainDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.zzz.entitymodel.servicebase.constants.IpServiceConstant.*;
import static com.zzz.utils.HttpClientUtil.exeuteDefaultRequest;
import static com.zzz.utils.HttpClientUtil.vaildateReponse;
import static com.zzz.utils.PageUtils.vaildateEntity;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Callable;


import static com.zzz.utils.PageUtils.*;

public class XHGetpageInfoTask implements Callable<List<IpPoolMainDTO>> {
    private static final Logger LOG = LoggerFactory.getLogger(XHGetpageInfoTask.class);
    private List<IpLocation> workStack;
    private String curPrefixUrl;
    private Random random = new Random();

    public XHGetpageInfoTask(String curPrefixUrl, List<IpLocation> workStack) {
        this.curPrefixUrl = curPrefixUrl;
        this.workStack = workStack;
    }


    @Override
    public List<IpPoolMainDTO> call() {
        String threadName = Thread.currentThread().getName();
        if (workStack == null || workStack.isEmpty()) {
            LOG.info(" current task : {} workstack is empty, will not work", threadName);
            return null;
        }
        int size = workStack.size();
        LOG.info(" current thread : {} doing job , list size : {}", threadName, size);

        Iterator<IpLocation> ipLocationIterator = workStack.iterator();
        List<IpPoolMainDTO> ipPoolMainDOs = Collections.synchronizedList(new LinkedList<>());
        while (ipLocationIterator.hasNext()) {
            try {
                IpLocation target = ipLocationIterator.next();
                Thread.sleep(random.nextInt(4) * 1000);
                String fullUrl = curPrefixUrl + target.getLocationHref();
                LOG.info(" begin fetching page info , page : {} ", fullUrl);
                URI uri = new URIBuilder(fullUrl)
                        .setScheme("https")
                        .build();
                String curUriString = uri.toString();
                HttpGet get = new HttpGet(uri);
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                LOG.info(" do with current type :{} ", curUriString);
                HttpResponse response = exeuteDefaultRequest(get, headerList,true);
                vaildateReponse(response);
                HttpEntity httpEntity = response.getEntity();
                String currentPage = vaildateEntity(httpEntity);
                Elements elements = fetchElementWithSection(currentPage, HAS_PAGE_REGIX);
                if (ObjectUtils.isNotEmpty(elements)) {
                    ipPoolMainDOs.addAll(Objects.requireNonNull(combineXiaoHuanInfo(elements)));
                } else {
                    LOG.info(" current elements is empty,will not work ,page : {} ", fullUrl);
                }
            } catch (Exception e) {
                LOG.error(" error , message : {} ", e.getMessage());
            }
        }
        //调用传输接口 暂时没写
        //现在这里写个插入测测
        //滞空 等待垃圾回收
        workStack = null;
        return ipPoolMainDOs;
    }
}

