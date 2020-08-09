package com.zzz.service.module.service.ipservices;

import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.service.module.common.exception.DebugException;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
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
import java.util.*;

import static com.zzz.service.module.utils.HttpClientUtil.*;
import static com.zzz.service.module.utils.PageUtils.*;

/**
 * 记得超时重连
 */
@Service
public class IpFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(IpFetchService.class);

    private static final String XIAO_HUAN_IP = "//ip.ihuan.me/tqdl.html";

    private static final String A_TAG_PREFIX = "/address";

    private List<String> aTagLists = null;

    private static final String RESPONSE_CODE_PREFIX = "2";

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

            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);

            List<Header> requestHeaders = new ArrayList<>();
            requestHeaders.add(new BasicHeader("user-agent", USER_AGENT));

            String requestUri = httpGet.getURI().toString();
            LOG.info(" request uri : {} ", requestUri);
            LOG.info(" executing request... ");

            HttpResponse response = exeuteDefaultRequest(httpGet, requestHeaders);
            vaildateReponse(response);

            LOG.info(" analysis <a> tag... ");
            HttpEntity entity = response.getEntity();
            String html = vaildateEntity(entity);
            TreeMap<Integer, IpLocation> resAtags = matchAtages(html, "a[href~=^/address]");
            LOG.info(" finish lists :");
            int locationNums = resAtags.size();
            LOG.info(" ip location nums :{} ", locationNums);
            Set<Map.Entry<Integer, IpLocation>> entries = resAtags.entrySet();
            for (Map.Entry<Integer, IpLocation> me : entries) {
                Integer num = me.getKey();
                IpLocation ipLocation = me.getValue();
                LOG.info(" current num : {} ,current ip location : {} ", num, ipLocation);
                ress.add(ipLocation.getLocationHref());
            }
            LOG.info(" done ");
        } catch (URISyntaxException | IOException e) {
            LOG.error(" executing request error , messages : {} ", e.getMessage());
        } finally {
            return ress;
        }
    }


    /**
     * fetch every page info
     *
     * @param ipPrefixLists
     */
    public void fetchIpPages(List<String> ipPrefixLists) {
        try {
            for (String targetPrefix : ipPrefixLists) {
                // get current page than do fetch page nums
                LOG.info(" begin fetching these pages ");
                URI uri = new URIBuilder(XIAO_HUAN_IP)
                        .setScheme("https")
                        .setParameters()
                        .setPath(targetPrefix)
                        .build();
                String curUriString = uri.toString();
                HttpGet get = new HttpGet(uri);
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));

                LOG.info(" do with current type :{} ", curUriString);

                HttpResponse response = exeuteDefaultRequest(get, headerList);
                vaildateReponse(response);

                HttpEntity httpEntity = response.getEntity();
                String currentPage = EntityUtils.toString(httpEntity);
                LOG.info(" current html : {} ", currentPage);
                TreeMap<Integer, IpLocation> aTags = matchAtages(currentPage, "a[href^=?page]");

                LOG.info(" done ");
                LOG.info(" start fetching  current pages ");
                fetchCurrentPages(curUriString, aTags);
                break;

            }
        } catch (URISyntaxException e1) {
            LOG.error(" errors : {} ", e1.getMessage());
        } catch (IOException e2) {
            LOG.error(" errors : {} ", e2.getMessage());
        }
    }

    /**
     * @param curUriString
     * @param aTags
     */
    private void fetchCurrentPages(String curUriString, TreeMap<Integer, IpLocation>  aTags) {
        LOG.info(" start getting current  <a> tags ");
        boolean flag = true;
        Stack<Integer> endStack = new Stack<>();
        List<String> fetchLinks = new ArrayList<>(500);
        while (flag) {
            TreeMap<Integer, String> sortPageMap = new TreeMap<>();
            Set<Map.Entry<Integer, IpLocation>> entries = aTags.entrySet();
            for (Map.Entry<Integer, IpLocation> e : entries) {
                IpLocation location = e.getValue();
                LOG.info(" combine current work pages ,current location : {} ",location);
                String pageNumber = location.getLocationName();
                String escapePageNumber = location.getLocationHref();
                if (pageNumber.matches("^\\d{1,10}$")) {
                    sortPageMap.put(Integer.valueOf(pageNumber), escapePageNumber);
                }
            }

            //sortPageMap.put(Integer.valueOf("7"), "?page=881aaf7b5");
            LOG.info(" done ");
            Map.Entry<Integer, String> endEntry = sortPageMap.lastEntry();
            Integer key = endEntry.getKey();
            String page = endEntry.getValue();
            LOG.info(" last key : {} , last value : {} ", key, page);

            String fullEndLink = curUriString + page;
            LOG.info(" prepare fetching current end link : {} ", fullEndLink);
            try {
                URI uri = new URIBuilder(fullEndLink).build();
                HttpGet get = new HttpGet(uri);
                List<Header> headers = new ArrayList<Header>() {{
                    add(new BasicHeader("user-agent", USER_AGENT));
                }};
                HttpResponse response = exeuteDefaultRequest(get, headers);
                vaildateReponse(response);
                String html = vaildateEntity(response.getEntity());
                if (hasNextPage(html)) {
                    // have more page will continue

                }
            } catch (Exception e) {
                LOG.error(" error , current fullLink : {},message : {} ", fullEndLink, e.getMessage());
            }

            flag = false;
        }

    }
}
