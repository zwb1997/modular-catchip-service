package com.zzz.service.ipservices;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;
import com.zzz.service.ipservices.abstractservice.AbsrtactFetchIpService;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NiMaIpFetchService extends AbsrtactFetchIpService {
    private static final Logger LOG = LoggerFactory.getLogger(NiMaIpFetchService.class);

    public List<URI> run() {
        LOG.info(" nima ip works begin ");
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
    

    public void fetchEvertPage(){

    }

    @Override
    protected void serviceEntry() {
        run();
    }

}
