package com.ipfetchservice.service.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

@Component
public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final int RESPONSE_CODE_PREFIX = 200;
    private static final int REQUEST_TIME_OUT = 20;
    // private static final int ESTABLISH_TIME_OUT = 30;

    private static final HttpHost HTTP_HOST = new HttpHost(PROXY_IP, PROXY_IP_PORT);

    private static final PoolingHttpClientConnectionManager poolClientConnectionManager = new PoolingHttpClientConnectionManager(
            60L, TimeUnit.SECONDS);
    private static CloseableHttpClient CLIENT;
    static {
        poolClientConnectionManager.setMaxTotal(10);
        poolClientConnectionManager.setDefaultMaxPerRoute(500);
        CLIENT = HttpClientBuilder.create().setUserAgent(USER_AGENT).setConnectionManager(poolClientConnectionManager)
                .build();
    }

    /**
     * create a default httpClient ,reuse httpclient
     * @param httpType
     * @param headers
     * @return String the response text
     */
    public String exeuteDefaultRequest(HttpRequestBase httpType, List<Header> headers, boolean useProxy) {
        LOG.info(" begin send a request ");
        LOG.info(" prepare to send a request ");
        String targetUri = httpType.getURI().toString();
        LOG.info(" URI : {} ", targetUri);
        String responseText = "";
        try {
            if(!CollectionUtils.isEmpty(headers)){
                int headerSize = headers.size();
                httpType.setHeaders(headers.toArray(new Header[headerSize]));
            }
            RequestConfig requestConfig = useProxy
                    ? RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIME_OUT).setProxy(HTTP_HOST).build()
                    : RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIME_OUT).build();
            httpType.setConfig(requestConfig);
            printHeaders(httpType.getAllHeaders());
            LOG.info(" waiting for response ");
            try(CloseableHttpResponse response = CLIENT.execute(httpType)){
                LOG.info(" response send back ");
                printHeaders(response.getAllHeaders());
                HttpEntity responseEntity = response.getEntity();
                if(ObjectUtils.isEmpty(responseEntity)){
                    LOG.info(" request to uri :{} response text is null ",targetUri);
                    return responseText;
                }
                responseText = EntityUtils.toString(responseEntity, "utf-8");
                return responseText;
            }
        } catch (IOException e) {
            LOG.error(" execute request error : messages {} ", e.getMessage());
        }
        return responseText;
    }

    /**
     * print headers
     *
     * @param headers
     */
    public void printHeaders(Header[] headers) {
      
        if(ArrayUtils.isEmpty(headers)){
            LOG.debug(" === request/response headers empty === ");
            return;
        }
        LOG.debug(" === request/response headers start === ");
        for (Header h : headers) {
            LOG.debug(" header : {} value : {} ", h.getName(), h.getValue());
        }
        LOG.debug(" === request/response headers end === ");
    }
}
