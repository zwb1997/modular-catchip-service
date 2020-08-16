package com.zzz.service.module.utils;

import com.zzz.service.module.common.exception.DebugException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final String RESPONSE_CODE_PREFIX = "2";
    private static final int REQUEST_TIME_OUT = 20;
    private static final int ESTABLISH_TIME_OUT = 30;
    private static final String PROXY_IP = "104.207.151.166";
    private static final int PROXY_IP_PORT = 37720;
    private static final HttpHost HTTP_HOST  = new HttpHost(PROXY_IP,PROXY_IP_PORT);
    /**
     * create a default httpClient
     *
     * @param httpType
     * @param headers
     * @return
     */
    public static HttpResponse exeuteDefaultRequest(HttpRequestBase httpType, List<Header> headers) {
        LOG.info(" begin send a request ");
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            if (CollectionUtils.isEmpty(headers)) {
                LOG.info(" request header is null ");
            } else {
                int headerSize = headers.size();
                httpType.setHeaders(headers.toArray(new Header[headerSize]));
                RequestConfig requestConfig = RequestConfig
                                                .custom()
                                                .setConnectionRequestTimeout(REQUEST_TIME_OUT)
                                                .setProxy(HTTP_HOST)
                                                .build();
                httpType.setConfig(requestConfig);
            }
            response = client.execute(httpType);
        } catch (IOException e) {
            LOG.error(" execute request error : messages {} ", e.getMessage());
        } finally {
            return response;
        }

    }

    /**
     * @param response
     *  vaildate response is success by code is 200 and print response headers
     */
    public static void vaildateReponse(HttpResponse response) {
        if (ObjectUtils.isEmpty(response) && ObjectUtils.isEmpty(response.getStatusLine())) {
            LOG.error(" response is invalid or response cannot get status line ");
            throw new DebugException(" response is invalid or response cannot get status line ");
        } else {
            String responseCodeString = String.valueOf(response.getStatusLine().getStatusCode());
            if (responseCodeString.startsWith(RESPONSE_CODE_PREFIX)) {
                LOG.info(" response success, will show headers ");
                LOG.info(" execute done,response code : {} , will print headers ", responseCodeString);
                Header[] headers = response.getAllHeaders();
                printResponseHeaders(headers);
            }
        }
    }

    /**
     * print headers
     *
     * @param headers
     */
    public static void printResponseHeaders(Header[] headers) {
        if (ArrayUtils.isEmpty(headers)) {
            throw new DebugException(" error , response header is null,messages");
        }
        for (Header h : headers) {
            HeaderElement[] hes = h.getElements();
            for (HeaderElement he : hes) {
                LOG.info(" header : {} value : {} ", he.getName(), he.getValue());
            }
        }
    }


}
