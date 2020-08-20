package com.zzz.service.module.service.ipservices;

import com.zzz.entitymodel.servicebase.DO.IpPoolMainDO;
import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.service.module.common.threadconfig.NamedThreadFactory;
import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;
import com.zzz.service.module.mapper.IpDataServiceXiaoHuanMapper;
import com.zzz.service.module.service.ipservices.work.GetpageInfoTask;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

import static com.zzz.service.module.utils.HttpClientUtil.*;
import static com.zzz.service.module.utils.PageUtils.*;
import static com.zzz.entitymodel.servicebase.constants.IpServiceConstant.*;

/**
 * 记得超时重连
 */
@Service
@EnableScheduling
public class IpFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(IpFetchService.class);
    private static final String A_TAG_PREFIX = "/address";
    private List<String> aTagLists = null;
    private static final String RESPONSE_CODE_PREFIX = "2";
    private static final String PAGE_REGIX = "^(\\d){1,6}$";
    private static final ArrayBlockingQueue<Runnable> ARRAY_BLOCKING_QUEUE = new ArrayBlockingQueue<>(6);
    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, ARRAY_BLOCKING_QUEUE, new NamedThreadFactory()
    );
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private IpDataServiceXiaoHuanMapper ipDataServiceXiaoHuanMapper;

    @Scheduled(fixedDelay =  60 * 1000)
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
            URI uri = new URIBuilder(IpServiceConstant.XIAO_HUAN_IP)
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
            // 增加花刺连接
            removeMutiple(resAtags);
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
     * 移除中国重复地址
     *
     * @param resAtags
     */
    private void removeMutiple(TreeMap<Integer, IpLocation> resAtags) {
//        Set<Map.Entry<Integer, IpLocation>> entrySet = resAtags.entrySet();
//        Iterator<Map.Entry<Integer, IpLocation>> iterator = entrySet.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Integer, IpLocation> m = iterator.next();
//            IpLocation tar = m.getValue();
//            if (XIAO_HUAN_POS_CHINA.equals(tar.getLocationName()) || XIAO_HUAN_POS_AMERICA.equals(tar.getLocationName())) {
//                continue;
//            }
//            iterator.remove();
//        }
        resAtags.put(0,new IpLocation("花刺连接","/Proxies.7z"));


    }

    /**
     * fetch every page info
     *
     * @param ipPrefixLists
     */
    public void fetchIpPages(List<String> ipPrefixLists) {

        for (String targetPrefix : ipPrefixLists) {
            String curUriString = null;
            try {
                // get current page than do fetch page nums
                LOG.info(" begin fetching these pages ");
                URI uri = new URIBuilder(XIAO_HUAN_IP)
                        .setScheme("https")
                        .setPath(targetPrefix)
                        .build();
                curUriString = uri.toString();
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                LOG.info(" do with current type :{} ", curUriString);
                String currentPage = ipFetchCommonRequest(uri, headerList);
                LOG.info(" current html : {} ", currentPage);
                TreeMap<Integer, IpLocation> aTags = matchAtages(currentPage, "a[href^=?page]");
                LOG.info(" done ");
                LOG.info(" start fetching  current pages ");
                Set<Map.Entry<Integer, IpLocation>> aTageEntris = aTags.entrySet();
                Stack<IpLocation> pageNumsList = new Stack<>();
                addForList(aTageEntris, pageNumsList);
                checkAndRemoveLocation(pageNumsList, PAGE_REGIX);
                fetchCurrentPages(curUriString, pageNumsList);
            } catch (URISyntaxException | IOException e1) {
                LOG.error("{}  page error :  message : {}", curUriString, e1.getMessage());
            }
        }
    }

    /**
     * @param curUriString
     * @param pageNumsStack
     */
    private void fetchCurrentPages(String curUriString, Stack<IpLocation> pageNumsStack) {
        if (CollectionUtils.isEmpty(pageNumsStack)) {
            LOG.info(" pageNumsStack is null ");
            return;
        }
        LOG.info(" start getting current  <a> tags ");
        boolean flag = true;

        while (flag) {
            try {
                Thread.sleep(2 * 1000);
                IpLocation topIpLocation = pageNumsStack.pop();
                String curLocatioHref = topIpLocation.getLocationHref();
                String curFullHref = curUriString + curLocatioHref;
                LOG.info(" current page : {} ", curFullHref);
                URI uri = new URIBuilder(curFullHref)
                        .setScheme("https")
                        .build();
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                String currentPage = ipFetchCommonRequest(uri, headerList);
                if (hasNextPage(currentPage, HAS_PAGE_REGIX)) {
                    TreeMap<Integer, IpLocation> curMap = matchAtages(currentPage, PAGE_NUM_COMMON);
                    Set<Map.Entry<Integer, IpLocation>> curSet = curMap.entrySet();
                    addForList(curSet, pageNumsStack);
                    checkAndRemoveLocation(pageNumsStack, PAGE_REGIX);
                } else {
                    LOG.info(" current page : {} has no matches , will pop ", curFullHref);
                    flag = false;
                }
            } catch (Exception e) {
                LOG.error(" error , message : {} ", e.getMessage());
            }
        }
        doWork(curUriString, pageNumsStack);
    }

    private String ipFetchCommonRequest(URI uri, List<Header> headerList) throws IOException {
        HttpGet get = new HttpGet(uri);
        HttpResponse response = exeuteDefaultRequest(get, headerList);
        vaildateReponse(response);
        HttpEntity httpEntity = response.getEntity();
        String currentPage = vaildateEntity(httpEntity);
        return currentPage;
    }

    /**
     * 有问题
     * 分割任务stack
     *
     * @param curUriString
     * @param pageNumsList
     */
    private void doWork(String curUriString, Stack<IpLocation> pageNumsList) {
        //划分任务
        //做任务
        int cur = 0;
        int curWorkSize = pageNumsList.size();
        LOG.info(" current page nums  : {} ,list : {} ",curWorkSize,pageNumsList);
        int step = curWorkSize / CORE_POOL_SIZE;
        List<Future<List<IpPoolMainDO>>> futures = Collections.synchronizedList(new ArrayList<>());
        while (cur < curWorkSize) {
            int curEndPos = (cur + step + 1);
            curEndPos = Math.min(curEndPos, curWorkSize);
            List<IpLocation> syncWorkList = pageNumsList.subList(cur, curEndPos);
            cur = curEndPos;
            Future<List<IpPoolMainDO>> future = EXECUTOR_SERVICE.submit(new GetpageInfoTask(curUriString, syncWorkList));
            futures.add(future);
        }
        uploadData(futures);
    }

    /**
     * 获取每个任务的结果
     *
     * @param futures
     */
    private void uploadData(List<Future<List<IpPoolMainDO>>> futures) {
        LOG.info(" begin stage data ");
        Assert.notEmpty(futures, " future task wouldn't empty ");
        for (Future<List<IpPoolMainDO>> future : futures) {
            try {
                List<IpPoolMainDO> lists = future.get();
                if(CollectionUtils.isEmpty(lists)){
                    LOG.info(" current IpPoolMainDO list is empty , will not do insert ");
                    continue;
                }
                ipDataServiceXiaoHuanMapper.insertIpDataXiaoHuan(lists);
            } catch (InterruptedException | ExecutionException e) {
                LOG.error(" stage data error , message : {} ", e.getMessage());
            }
        }
    }

    /**
     * @param pageNumsList
     * @param expertSection if page text not validated , will remove it
     */
    private void checkAndRemoveLocation(List<IpLocation> pageNumsList, String expertSection) {
        Iterator<IpLocation> itr = pageNumsList.listIterator();
        while (itr.hasNext()) {
            IpLocation ipLocation = itr.next();
            if (!checkLocationName(ipLocation, expertSection)) {
                itr.remove();
            }
        }
    }


    /**
     * add map values to a list
     *
     * @param mapSet
     * @param res
     * @param <K>
     * @param <V>
     */
    private <K, V> void addForList(Set<Map.Entry<K, V>> mapSet, List<V> res) {
        if (res == null) {
            LOG.info(" list is null ");
            return;
        }
        for (Map.Entry<K, V> me : mapSet) {
            V val = me.getValue();
            res.add(val);
            var a = "123";
        }
    }

    /**
     * check location is legal by checkSection
     *
     * @param location
     * @param checkSection
     * @return
     */
    private boolean checkLocationName(IpLocation location, String checkSection) {
        if (ObjectUtils.isEmpty(location) || StringUtils.isBlank(checkSection)) {
            LOG.info(" Object location is null or check regix String is null ");
            return false;
        }
        return location.getLocationName().matches(checkSection);
    }

}
