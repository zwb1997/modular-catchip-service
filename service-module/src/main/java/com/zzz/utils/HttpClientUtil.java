package com.zzz.utils;

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
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import static com.zzz.entitymodel.servicebase.constants.IpServiceConstant.*;

@Service
public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final int RESPONSE_CODE_PREFIX = 200;
    private static final int REQUEST_TIME_OUT = 20;
    private static final int ESTABLISH_TIME_OUT = 30;

    private static final HttpHost HTTP_HOST = new HttpHost(PROXY_IP, PROXY_IP_PORT);

    /**
     * create a default httpClient
     *
     * @param httpType
     * @param headers
     * @return
     */
    public HttpResponse exeuteDefaultRequest(HttpRequestBase httpType, List<Header> headers, boolean useProxy) {
        LOG.info(" begin send a request ");
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            Assert.notNull(headers, " request header is null ");
            int headerSize = headers.size();
            httpType.setHeaders(headers.toArray(new Header[headerSize]));
            RequestConfig requestConfig = useProxy
                    ? RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIME_OUT).setProxy(HTTP_HOST).build()
                    : RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIME_OUT).build();
            httpType.setConfig(requestConfig);
            printHeaders(httpType.getAllHeaders());
            response = client.execute(httpType);
        } catch (IOException e) {
            LOG.error(" execute request error : messages {} ", e.getMessage());
        }
        return response;
    }

    /**
     * @param response vaildate response is success by code is 200 and print
     *                 response headers
     */
    public void vaildateReponse(HttpResponse response) {

        Assert.notNull(response, " respnse could't empty ");
        if (response.getStatusLine() == null) {
            LOG.info(" response statusline is null , will not validate");
            return;
        }
        int responseCodeString = response.getStatusLine().getStatusCode();
        if (responseCodeString == RESPONSE_CODE_PREFIX) {
            LOG.info(" response success, will show headers ");
        } else {
            LOG.info(" response code is not '200', will show headers ");
        }
        LOG.info(" execute done,response code : {} , will print headers ", responseCodeString);
        Header[] headers = response.getAllHeaders();
        printHeaders(headers);
    }

    /**
     * print headers
     *
     * @param headers
     */
    public void printHeaders(Header[] headers) {
        Assert.notEmpty(headers, " error , response header is null,messages");
        LOG.info(" === request/response headers === ");
        for (Header h : headers) {
            HeaderElement[] hes = h.getElements();
            for (HeaderElement he : hes) {
                LOG.info(" header : {} value : {} ", he.getName(), he.getValue());
            }
        }
        LOG.info(" === request/response headers === ");
    }
}
