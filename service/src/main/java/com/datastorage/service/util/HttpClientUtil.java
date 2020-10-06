package com.datastorage.service.util;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.datastorage.models.basicalmodels.basicalconstants.IpServiceConstant.*;

/**
 * @author
 */
@Component("httpclientutil")
public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
            60, TimeUnit.SECONDS);
    private static final DynamicProxyRoutePlanner ROUTE_PLANNER = new DynamicProxyRoutePlanner(
            new HttpHost("127.0.0.1", 8080));
    private static CloseableHttpClient CLIENT = null;

    // private static final ReentrantLock LOCK = new ReentrantLock();
    private static URI BAIDU_URI;
    private static URI GOOGLE_URI;
    // milliseconds
    private static final RequestConfig BAIDU_CONFIG = RequestConfig.custom().setConnectTimeout(5 * 1000).build();
    private static final RequestConfig GOOGLE_CONFIG = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
    private static HttpGet BAIDU_GET;
    private static HttpGet GOOGLE_GET;

    static {
        try {
            CONNECTION_MANAGER.setMaxTotal(10);
            CONNECTION_MANAGER.setDefaultMaxPerRoute(50);
            CLIENT = HttpClientBuilder.create().setConnectionManager(CONNECTION_MANAGER).setUserAgent(USER_AGENT)
                    .setRoutePlanner(ROUTE_PLANNER).build();

            BAIDU_URI = new URIBuilder(BAIDU_URI_STRING).build();
            GOOGLE_URI = new URIBuilder(GOOGLE_URI_STRING).build();

            BAIDU_GET = new HttpGet(BAIDU_URI);
            GOOGLE_GET = new HttpGet(GOOGLE_URI);
            BAIDU_GET.setConfig(BAIDU_CONFIG);
            GOOGLE_GET.setConfig(GOOGLE_CONFIG);
        } catch (Exception ex) {
            LOG.error(" HttpClientUtil init  error ", ex);
        }
    }

    public HttpClientUtil() {

    }

    /**
     * 
     * @param model
     * @return Pair<Boolean, Boolean> value0 for baidu ,value1 for google
     * 
     */
    public Pair<Boolean, Boolean> detectProxyCanUse(IpPoolMainDO model) {
        String ip = model.getIpNum();
        int port = model.getIpPort();
        Pair<Boolean, Boolean> detectPair = Pair.with(false, false);
        ROUTE_PLANNER.setHttpHost(new HttpHost(ip, port));
        LOG.info(" ip :{} port :{} begin detect ", ip, port);
        try {
            // 检测方法 先拆成两个方法写 以后可能会单独扩展
            detectPair = Pair.with(detectDomestic(), detectForeign());
            if (detectPair.getValue0()) {
                LOG.info(" ip :{} ping domestic success ", ip);
            }
            if (detectPair.getValue1()) {
                LOG.info(" ip :{} ping foreign success ", ip);
            }
        } catch (Exception e) {
            LOG.error(" detectProxyCanUse error , message :{} ", e.getMessage());
        }
        return detectPair;
    }

    /**
     * detectDomestic
     * 
     * @return
     */
    public boolean detectDomestic() {
        boolean canUse = false;
        try {
            try(CloseableHttpResponse response = CLIENT.execute(BAIDU_GET)){
                StatusLine status = response.getStatusLine();
                if (status != null) {
                    int code = status.getStatusCode();
                    if (code == RESPONSE_SUCCESS_CODE) {
                        canUse = true;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(" detectDomestic error ", e);
        } 
        return canUse;
    }

    /**
     * detectForeign
     * 
     * @return
     */
    public boolean detectForeign() {
        boolean canUse = false;
        try {
            try(CloseableHttpResponse response = CLIENT.execute(GOOGLE_GET)){
                StatusLine status = response.getStatusLine();
                if (status != null) {
                    int code = status.getStatusCode();
                    if (code == RESPONSE_SUCCESS_CODE) {
                        canUse = true;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(" detectDomestic error ", e);
        } 
        return canUse;
    }

    /**
     * change proxy class
     */
    private static class DynamicProxyRoutePlanner implements HttpRoutePlanner {
        private DefaultProxyRoutePlanner proxyRoutePlanner;

        public DynamicProxyRoutePlanner(HttpHost host) {
            proxyRoutePlanner = new DefaultProxyRoutePlanner(host);
        }

        public void setHttpHost(HttpHost host) {
            proxyRoutePlanner = new DefaultProxyRoutePlanner(host);
        }

        @Override
        public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context)
                throws HttpException {
            return proxyRoutePlanner.determineRoute(target, request, context);
        }
    }
}
