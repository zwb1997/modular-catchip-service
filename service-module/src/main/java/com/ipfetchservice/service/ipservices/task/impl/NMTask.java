package com.ipfetchservice.service.ipservices.task.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;
import com.ipfetchservice.service.utils.HttpClientUtil;
import com.ipfetchservice.service.utils.PageUtil;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractor;
import com.ipfetchservice.service.utils.page.extractservice.impl.NMPageExtractStrategy;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NMTask implements Callable<List<IpPoolMainDTO>> {
    private static final Logger LOG = LoggerFactory.getLogger(NMTask.class);
    private static final HttpClientUtil CLIENT_UTIL = new HttpClientUtil();
    private static final PageUtil PAGE_UTIL = new PageUtil();
    private List<URI> workUris = new LinkedList<>();
    private String curPrefixUrl;
    private static final Random RANDOM = new Random();
    private List<Header> commonHeaders = new LinkedList<>();
    private static final PageExtractor EXTRACTOR = new PageExtractor(new NMPageExtractStrategy());

    public NMTask() {

    }

    public NMTask(List<URI> workUris, String curPrefixUrl) {
        this.workUris = workUris;
        this.curPrefixUrl = curPrefixUrl;
        String targetReferer = NIMA_REFERER + this.curPrefixUrl;
        commonHeaders.add(new BasicHeader("Referer", targetReferer));
        commonHeaders.add(new BasicHeader("Host", "www.nimadaili.com"));
        LOG.info(" extract url : {}, lists size : {}", curPrefixUrl, workUris.size());
    }

    @Override
    public List<IpPoolMainDTO> call() throws Exception {
        HttpGet get = new HttpGet();
        List<IpPoolMainDTO> resultDtos = new LinkedList<>();
        for (URI targetUri : workUris) {
            LOG.info(" thread :{} doing job ,uri :{} ", Thread.currentThread().getName(), targetUri);
            get.setURI(targetUri);
            String responseText = CLIENT_UTIL.exeuteDefaultRequest(get, commonHeaders, true);
            // combine infos to IpPoolMainDTO
            resultDtos.addAll(PAGE_UTIL.getInfos(responseText, HAS_PAGE_REGIX, EXTRACTOR));
            Thread.sleep(RANDOM.nextInt(3) * 1000);
        }
        return resultDtos;
    }
}
