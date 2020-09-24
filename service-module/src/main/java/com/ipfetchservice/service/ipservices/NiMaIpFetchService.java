package com.ipfetchservice.service.ipservices;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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


    public NiMaIpFetchService(){
        this.taskName = NM_TASK_NAME;
    }

    public List<URI> runService() {
        List<URI> nimaTaskUris = new ArrayList<>();
        try {
            URI ptURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_NROMAL).build();
            URI gnURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_HIGH).build();
            URI httpURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_HTTP).build();
            URI httpsURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP).setPath(NI_MA_IP_HTTPS).build();
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
     * nima proxy 页数没有加密 直接先搞页数
     * 
     * @param nimaUris
     */
    public void fetchEvertPage(List<URI> nimaUris) {
        List<Header> headers = new LinkedList<>();
        headers.add(new BasicHeader("Host", NI_MA_IP));
        headers.add(new BasicHeader("Referer", NIMA_REFERER));
        HttpGet get = new HttpGet();
        for (URI uri : nimaUris) {
            try {
                get.setURI(uri);
                String responseEntityString = clientUtil.exeuteDefaultRequest(get, headers, true);
                if (pageUtil.hasNextPage(responseEntityString, HAS_PAGE_REGIX)) {

                }
                // LOG.info(" response text : {} ", responseEntityString);
            } catch (Exception e) {
                LOG.error(" NIMA fetching every page error ,message :{} ", e.getMessage());
            }
        }
    }

    @Override
    protected void serviceEntry() {
        fetchEvertPage(runService());
    }

}
