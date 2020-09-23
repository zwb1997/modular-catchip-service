package com.ipfetchservice.service.ipservices.task;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpLocation;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.HttpClientUtil;
import com.ipfetchservice.service.utils.PageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Callable;
import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

public class XHTask implements Callable<List<IpPoolMainDTO>> {
    private static final Logger LOG = LoggerFactory.getLogger(XHTask.class);
    private static final HttpClientUtil CLIENT_UTIL = new HttpClientUtil();
    private static final PageUtil PAGE_UTIL = new PageUtil();
    private List<IpLocation> workStack;
    private String curPrefixUrl;
    private Random random = new Random();

    public XHTask(String curPrefixUrl, List<IpLocation> workStack) {
        this.curPrefixUrl = curPrefixUrl;
        this.workStack = workStack;
        LOG.info(" extract url : {}, lists : {}", curPrefixUrl, workStack);
    }

    @Override
    public List<IpPoolMainDTO> call() {
        String threadName = Thread.currentThread().getName();
        if (CollectionUtils.isEmpty(workStack)) {
            LOG.info(" current task : {} workstack is empty, will not work", threadName);
            return null;
        }
        int size = workStack.size();
        LOG.info(" current thread : {} doing job , list size : {}", threadName, size);

        Iterator<IpLocation> ipLocationIterator = workStack.iterator();
        List<IpPoolMainDTO> ipPoolMainDOs = Collections.synchronizedList(new LinkedList<>());
        List<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("user-agent", USER_AGENT));
        while (ipLocationIterator.hasNext()) {
            try {
                Thread.sleep(random.nextInt(4) * 1000);

                IpLocation target = ipLocationIterator.next();
                String fullUrl = curPrefixUrl + target.getLocationHref();
                LOG.info(" begin fetching page proxy infos , page url : {} ", fullUrl);

                URI uri = new URIBuilder(fullUrl).setScheme("https").build();
                String curUriString = uri.toString();
                LOG.info(" do with current url :{} ", curUriString);
                String currentPage = CLIENT_UTIL.ipFetchGetRequest(uri, headerList, true);
                Elements elements = PAGE_UTIL.fetchElementWithSection(currentPage, HAS_PAGE_REGIX);
                if (ObjectUtils.isNotEmpty(elements)) {
                    ipPoolMainDOs.addAll(Objects.requireNonNull(PAGE_UTIL.combineXiaoHuanInfo(elements)));
                } else {
                    LOG.info(" current elements is empty,will not work ,page : {} ", fullUrl);
                }
            } catch (Exception e) {
                LOG.error(" error , message : {} ", e.getMessage());
            }
        }
        headerList = null;
        workStack = null;
        return ipPoolMainDOs;
    }
}
