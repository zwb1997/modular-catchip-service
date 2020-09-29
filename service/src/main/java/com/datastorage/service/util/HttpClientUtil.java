package com.datastorage.service.util;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
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

@Component
public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
            60, TimeUnit.SECONDS);
    private static final DynamicProxyRoutePlanner routePlanner = new DynamicProxyRoutePlanner(null);
    private static CloseableHttpClient CLIENT = null;
    private static final HttpGet REQUEST_HTTP_GET = new HttpGet();
    private static URI BAIDU_URI;
    private static URI GOOGLE_URI;
    static {
        CONNECTION_MANAGER.setMaxTotal(10);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(50);
        CLIENT = HttpClientBuilder.create().setConnectionManager(CONNECTION_MANAGER).setUserAgent(USER_AGENT)
                .setRoutePlanner(routePlanner).build();

    }

    public HttpClientUtil() {
        initUri();
    }

    private void initUri() {
        try {
            BAIDU_URI = new URI(BAIDU_URI_STRING);
            BAIDU_URI = new URI(GOOGLE_URI_STRING);
        } catch (Exception e) {
            LOG.error(" initUri error , message :{} ", e.getMessage());
        }
    }

    /**
     * 
     * @param model
     * @return
     */
    public Pair<Boolean, Boolean> detectProxyCanUse(IpPoolMainDO model) {
        Pair<Boolean, Boolean> detectPair = Pair.with(false, false);
        try {
            REQUEST_HTTP_GET.setURI(BAIDU_URI);
        } catch (Exception e) {
            LOG.error(" detectProxyCanUse error , message :{} ", e.getMessage());
        }

        return detectPair;

    }

    public boolean detectDomestic() {
        return true;
    }

    public boolean detectForeign() {
        return true;
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
