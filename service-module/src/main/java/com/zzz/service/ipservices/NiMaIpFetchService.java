package com.zzz.service.ipservices;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.zzz.model.entitymodel.servicebase.constants.IpServiceConstant;
import com.zzz.service.ipservices.abstractservice.AbsrtactFetchIpService;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("nimaipfetchservice")
public class NiMaIpFetchService extends AbsrtactFetchIpService {
    private static final Logger LOG = LoggerFactory.getLogger(NiMaIpFetchService.class);

    public List<URI> runService() {
        List<URI> nimaTaskUris = new ArrayList<>();
        try {
            URI ptURI = new URIBuilder(IpServiceConstant.NI_MA_IP).setScheme(IpServiceConstant.IP_HTTP)
                    .setPath(IpServiceConstant.NI_MA_IP_NROMAL).build();
            URI gnURI = new URIBuilder(IpServiceConstant.NI_MA_IP).setScheme(IpServiceConstant.IP_HTTP)
                    .setPath(IpServiceConstant.NI_MA_IP_HIGH).build();
            URI httpURI = new URIBuilder(IpServiceConstant.NI_MA_IP).setScheme(IpServiceConstant.IP_HTTP)
                    .setPath(IpServiceConstant.NI_MA_IP_HTTP).build();
            URI httpsURI = new URIBuilder(IpServiceConstant.NI_MA_IP).setScheme(IpServiceConstant.IP_HTTP)
                    .setPath(IpServiceConstant.NI_MA_IP_HTTPS).build();
            nimaTaskUris.add(ptURI);
            nimaTaskUris.add(gnURI);
            nimaTaskUris.add(httpURI);
            nimaTaskUris.add(httpsURI);
        } catch (Exception e) {
            LOG.error(" create nima uri error , message :{} ", e.getMessage());
        }
        // 页码检测
        // 封装link
        // 数据解析
        // 入库
        return nimaTaskUris;
    }

    public void fetchEvertPage(List<URI> nimaUris) {
        List<Header> headers = new LinkedList<>();
        headers.add(new BasicHeader("User_Agent", IpServiceConstant.USER_AGENT));
        headers.add(new BasicHeader("Host", IpServiceConstant.NI_MA_IP));
        headers.add(new BasicHeader("Referer", IpServiceConstant.NIMA_REFERER));
        for (URI uri : nimaUris) {
            try {
                HttpGet get = new HttpGet(uri);
                String responseEntityString = clientUtil.ipFetchGetRequest(uri, headers, true);

            } catch (Exception e) {
                LOG.error(" NIMA fetching every page error ,message :{} ", e.getMessage());
            }
        }
    }

    @Override
    protected void serviceEntry() {
        START_TIME = System.currentTimeMillis();
        LOG.info(" === {} SERVICE START === ", IpServiceConstant.NM_TASK_NAME);
        LOG.info(" start time : {} ", DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        fetchEvertPage(runService());
        END_TIME = System.currentTimeMillis();
        var useTime = END_TIME - START_TIME;
        LOG.info(" === {} SERVICE END === ", IpServiceConstant.NM_TASK_NAME);
        LOG.info(" end time : {} ", DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        LOG.info(" using time : miniutes :{} ,seconds :{} ", useTime / 1000 / 60, useTime / 1000);
    }

}
