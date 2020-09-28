package com.ipfetchservice.service.ipservices.abstractservice;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant;
import com.ipfetchservice.service.utils.HttpClientUtil;
import com.ipfetchservice.service.utils.PageUtil;
import com.ipfetchservice.service.utils.SignUtil;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

/**
 * 抽象ip爬取服务
 */
@Service
public abstract class AbsrtactFetchIpService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbsrtactFetchIpService.class);
    // task name
    protected String taskName;
    // request util
    protected HttpClientUtil clientUtil;
    // page util
    protected PageUtil pageUtil;
    // sign util
    protected SignUtil signUtil;
    // page couts
    protected int pageNums;
    // page details lists
    protected Collection<?> pageList;
    // page details map
    protected Map<?, ?> pageMaps;

    protected long startTime = 0L;
    protected long endTime = 0L;

    protected AbsrtactFetchIpService() {
        this.clientUtil = new HttpClientUtil();
        this.pageUtil = new PageUtil();
        this.signUtil = new SignUtil();
        LOG.info(" assemble utils success ");
    }

    protected abstract void serviceEntry();

    private void runTask() {

        startTime = System.currentTimeMillis();
        LOG.info(" === {} SERVICE START || start time : {} === ", taskName,
                DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        serviceEntry();
        endTime = System.currentTimeMillis();
        var useTime = endTime - startTime;
        LOG.info(" === {} SERVICE END  || end time : {} === \t using time : miniutes :{} ,seconds :{} ", taskName,
                DateFormatUtils.format(new Date(), IpServiceConstant.COMMON_DATE_FORMAT_REGIX), useTime / 1000 / 60,
                useTime / 1000);

    }

    /**
     * default uploading method
     * 
     * @param futures
     */
    protected void uploadData(List<Future<List<IpPoolMainDTO>>> futures) {
        LOG.info(" === begin stage data === ");
        Assert.notEmpty(futures, " future task wouldn't empty ");
        HttpPost httpPost = new HttpPost();
        for (Future<List<IpPoolMainDTO>> future : futures) {
            try {
                LOG.info(" \\\\waiting to get future result...\\\\ ");
                List<IpPoolMainDTO> lists = future.get(10, TimeUnit.MINUTES);
                if (CollectionUtils.isEmpty(lists)) {
                    LOG.info(" current IpPoolMainDO list is empty , will not do insert ");
                    continue;
                }
                int size = lists.size();
                LOG.debug(" list size :{} ", size);
                ObjectMapper objectMapper = new ObjectMapper();
                String curData = objectMapper.writeValueAsString(lists);
                URI uri = new URIBuilder(STORAGE_SERVICE_LOCATION).setScheme("http").setCharset(StandardCharsets.UTF_8)
                        .setPort(STORAGE_SERVICE_LOCATION_PORT).setPath(STORAGE_SERVICE_PATH).build();
                httpPost.setURI(uri);
                httpPost.setEntity(new StringEntity(curData, Consts.UTF_8));
                List<Header> headerList = new ArrayList<>();
                String curTimeMillions = String.valueOf(System.currentTimeMillis());
                headerList.add(new BasicHeader(ORIGIN_NAME, STORAGE_SERVICE_LOCATION));
                headerList.add(new BasicHeader(CUR_TIME_SPAN, curTimeMillions));
                headerList.add(new BasicHeader(SECRET_SIGN, signUtil.createSign(curTimeMillions, SECRET)));
                headerList.add(new BasicHeader("Content-Type", HTTP_CONTENT_TYPE_JSON));
                String responseText = clientUtil.exeuteDefaultRequest(httpPost, headerList, false);
                LOG.info(" sent storage data success , response text : {} ", responseText);
            } catch (Exception e) {
                LOG.error(" send stage data error , message : {} ", e.getMessage());
            }
        }
    }

    public HttpClientUtil getClientUtil() {
        return clientUtil;
    }

    public PageUtil getPageUtils() {
        return pageUtil;
    }

    public SignUtil getSignUtil() {
        return signUtil;
    }

    public int getPageNums() {
        return pageNums;
    }

    public Map<?, ?> getPageMaps() {
        return pageMaps;
    }

    public Collection<?> getPageList() {
        return pageList;
    }

    @Override
    public void run() {
        LOG.info(" task : {} begin...", this.taskName);
        runTask();
    }
}
