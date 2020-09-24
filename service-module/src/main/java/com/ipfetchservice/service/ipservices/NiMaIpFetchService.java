package com.ipfetchservice.service.ipservices;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant;
import com.ipfetchservice.service.ipservices.abstractservice.AbsrtactFetchIpService;

import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// 页码检测
// 封装link
// 数据解析
// 入库
@Service("nimaipfetchservice")
public class NiMaIpFetchService extends AbsrtactFetchIpService {
    private static final Logger LOG = LoggerFactory.getLogger(NiMaIpFetchService.class);
    private static final int BEGIN_PAGE_NUMBER = 50;
    private static final int PAGE_STEP = 50;

    public NiMaIpFetchService(){
        this.taskName = NM_TASK_NAME;
    }

    public List<URI> runService() {
        List<URI> nimaTaskUris = new ArrayList<>();
        try {
            URI ptURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_NROMAL + "/" + BEGIN_PAGE_NUMBER).build();
            URI gnURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_HIGH + "/" + BEGIN_PAGE_NUMBER).build();
            URI httpURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_HTTP + "/" + BEGIN_PAGE_NUMBER).build();
            URI httpsURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_HTTPS + "/" + BEGIN_PAGE_NUMBER).build();
            nimaTaskUris.add(ptURI);
            nimaTaskUris.add(gnURI);
            nimaTaskUris.add(httpURI);
            nimaTaskUris.add(httpsURI);
        } catch (Exception e) {
            LOG.error(" create nima uri error , message :{} ", e.getMessage());
        }

        return nimaTaskUris;
    }

    /**
     * 
     * @param nimaUris
     */
    public void fetchEvertPage(List<URI> nimaUris) {
       
      
        for (URI i : nimaUris) {
            int pageSize = pageNumDetect(i);
           
        }
    }
    /**
     * detect current uri page nums
     * @param i
     * @return
     */
    private int pageNumDetect(URI i) {
        String path = i.getPath().split("/")[1];
        List<Header> headers = new LinkedList<>();
        headers.add(new BasicHeader("Host", NI_MA_IP));
        headers.add(new BasicHeader("Referer", NIMA_REFERER));
        HttpGet get = new HttpGet();
        int beginPageSize = BEGIN_PAGE_NUMBER;
        int pageSize = -1;
        boolean flag1 = true;
        boolean flag2 = true;
        try{
            URI uri = new URIBuilder(i).build();
            while(flag1){
                get.setURI(uri);
                String responsetext = clientUtil.exeuteDefaultRequest(get, headers, true);
                if(pageUtil.hasNextPage(responsetext, IpServiceConstant.HAS_PAGE_REGIX)){
                    LOG.info(" page numer :{}  has value ",beginPageSize);
                    beginPageSize += PAGE_STEP;
                    pageSize = beginPageSize;
                    uri= new URIBuilder(i).setPath(path + "/" + beginPageSize).build();
                }else{
                    flag1 = false;
                }
            }
            while(flag2){
                get.setURI(uri);
                String responsetext = clientUtil.exeuteDefaultRequest(get, headers, true);
                if(!pageUtil.hasNextPage(responsetext, IpServiceConstant.HAS_PAGE_REGIX)){
                    LOG.info(" page numer :{}  has no value , will count down page num ",beginPageSize);
                    beginPageSize -= 1;
                    pageSize = beginPageSize;
                    uri= new URIBuilder(i).setPath(path + "/" + beginPageSize).build();
                }else{
                    flag2 = false;
                }
            }
        }catch(Exception e){
            LOG.error(" pageNumDetect error , message :{}  ", e.getMessage());
        }
        LOG.info(" page : {}  has nums :{}", path,pageSize);
        return pageSize;
        
    }

    @Override
    protected void serviceEntry() {
        fetchEvertPage(runService());
    }

}
