package com.zzz.service.module.service.ipservices;

import com.zzz.basemodels.enummodel.HttpResponseCodes;
import com.zzz.service.module.common.exception.DebugException;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IpFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(IpFetchService.class);

    private static final String XIAO_HUAN_IP = "//ip.ihuan.me/tqdl.html";

    private static final String A_TAG_PREFIX = "/address";

    private List<String> aTagLists = null;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public void run() {
        aTagLists = getPages();
        if (!CollectionUtils.isEmpty(aTagLists)) {
            int size = aTagLists.size();
            LOG.info(" ip prefix nums : {} ", size);
            LOG.info(" prepare to fetch full page nums ");
            fetchIpPages(aTagLists);
        } else {
            LOG.info(" ip prefix nums is 0 , will not fetch full page nums.... ");
        }
    }

    /**
     * download first page
     */
    public List<String> getPages() {
        LOG.info(" prepare to get xiaohuan ip pages... ");
        List<String> ress = new ArrayList<>(100);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        try {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            List<NameValuePair> requestParams = new ArrayList<>();
            requestParams.add(new BasicNameValuePair("num", "100"));
            requestParams.add(new BasicNameValuePair("port", ""));
            requestParams.add(new BasicNameValuePair("kill_port", ""));
            requestParams.add(new BasicNameValuePair("kill_address", ""));
            requestParams.add(new BasicNameValuePair("address", ""));
            requestParams.add(new BasicNameValuePair("anonymity", ""));
            requestParams.add(new BasicNameValuePair("type", ""));
            requestParams.add(new BasicNameValuePair("post", ""));
            requestParams.add(new BasicNameValuePair("sort", ""));
            requestParams.add(new BasicNameValuePair("key", uuid));
            //他这个需要将以下两个参数放请求参数中
            requestParams.add(new BasicNameValuePair("origin", "https://ip.ihuan.me"));
            requestParams.add(new BasicNameValuePair("referer", "https://ip.ihuan.me/ti.html"));
            URI uri = new URIBuilder(XIAO_HUAN_IP)
                    .setParameters(requestParams)
                    .setScheme("https")
                    .build();
            httpGet.setURI(uri);
            httpGet.setHeader("user-agent", USER_AGENT);
            String requestURI = httpGet.getURI().toString();
            LOG.info(" request uri : {} ", requestURI);
            LOG.info(" executing request... ");
            HttpResponse response = client.execute(httpGet);
            String responseCode = String.valueOf(response.getStatusLine().getStatusCode());
            LOG.info(" execute done,response code : {} , will print headers ",response.getStatusLine().getStatusCode());
            Header[] headers = response.getAllHeaders();
            for(Header h : headers) {
                HeaderElement[] hes = h.getElements();
                for(HeaderElement he : hes){
                    LOG.info(" header : {} value : {} ",he.getName(),he.getValue());
                }
            }
            if (responseCode.startsWith("2")) {
                LOG.info(" response success ");
                LOG.info(" analysis <a> tag... ");
                HttpEntity entity = response.getEntity();
                if(entity == null){
                    throw new DebugException(" response entity is null ");
                }
                String html = EntityUtils.toString(entity);
                if(StringUtils.isBlank(html)){
                    throw new DebugException(" response html is empty ");
                }
                Document doc = Jsoup.parse(html);
                Elements ipDocs = doc.select("a[href]");
                LOG.info(" finish lists :");
                for (Element e : ipDocs) {
                    String href = String.valueOf(e.attr("href"));
                    LOG.info(href);
                    if (href.startsWith(A_TAG_PREFIX)) {
                        ress.add(href);
                    }
                }
                LOG.info(" done ");
            }else{
                LOG.info(" response not useful, would't do anything... check reponse headers ");
            }
        } catch (URISyntaxException | IOException e) {
            LOG.error(" executing request error , messages : {} ", e.getMessage());
        } finally {
            return ress;
        }
    }


    /**
     * fetch every page info
     */
    public void fetchIpPages(List<String> ipPrefixLists) {
        try {
            for (String targetPrefix : ipPrefixLists) {
                // get current page than do fetch page nums
                HttpClient client = HttpClientBuilder.create().build();
                URI uri = new URIBuilder(XIAO_HUAN_IP)
                        .setScheme("https")
                        .setParameters()
                        .setPath(targetPrefix)
                        .build();
                HttpGet get = new HttpGet(uri);
                get.setHeader("user-agent", USER_AGENT);
                HttpResponse response = client.execute(get);
                String responseCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (responseCode.startsWith("2")){
                    HttpEntity httpEntity = response.getEntity();
                    String currentPage = EntityUtils.toString(httpEntity);
                    LOG.info(" current html : {} ",currentPage);
                }
            }
        } catch (URISyntaxException e1) {
            LOG.error(" errors : {} ", e1.getMessage());
        } catch (IOException e2) {
            LOG.error(" errors : {} ", e2.getMessage());
        }
    }
}
