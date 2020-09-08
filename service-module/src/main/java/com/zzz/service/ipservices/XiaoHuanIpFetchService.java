package com.zzz.service.ipservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.common.threadconfig.NamedThreadFactory;
import com.zzz.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;
import com.zzz.service.ipservices.work.XHGetpageInfoTask;
import com.zzz.utils.SignUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.zzz.utils.HttpClientUtil.*;
import static com.zzz.utils.HttpClientUtil.vaildateReponse;
import static com.zzz.utils.PageUtils.*;
import static com.zzz.entitymodel.servicebase.constants.IpServiceConstant.*;

/**
 * 记得超时重连
 */
@Service
@EnableScheduling
public class XiaoHuanIpFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(XiaoHuanIpFetchService.class);

    private static final ArrayBlockingQueue<Runnable> ARRAY_BLOCKING_QUEUE = new ArrayBlockingQueue<>(6);
    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE_TIME, TimeUnit.SECONDS, ARRAY_BLOCKING_QUEUE, new NamedThreadFactory());
    private static long startTime = 0L;
    private static long endTime = 0L;

    @Scheduled(fixedDelay = 60 * 1000)
    public void run() {
        LOG.info(" === SERVICE START === ");
        startTime = System.currentTimeMillis();
        LOG.info(" === time : {} === ", DateFormatUtils.format(startTime, IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        List<String> aTagLists = getXHlist();
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
     * download first page get ip area list
     */
    public List<String> getXHlist() {
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
            // 他这个需要将以下两个参数放请求参数中
            requestParams.add(new BasicNameValuePair("origin", IpServiceConstant.XIAO_HUAN_ME));
            requestParams.add(new BasicNameValuePair("referer", IpServiceConstant.XIAO_HUAN_TI));
            URI uri = new URIBuilder(IpServiceConstant.XIAO_HUAN_TQDL).setParameters(requestParams).setScheme("https")
                    .build();
            List<Header> requestHeaders = new ArrayList<>();
            requestHeaders.add(new BasicHeader("user-agent", USER_AGENT));
            String html = ipFetchGetRequest(uri, requestHeaders);
            TreeMap<Integer, IpLocation> resAtags = matchAtages(1, html, IpServiceConstant.PAGE_AREA_LIST_REGIX);
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
        }
        return ress;
    }

    /**
     * fetch every page info
     *
     * @param ipPrefixLists
     */
    public void fetchIpPages(List<String> ipPrefixLists) {
        for (String targetPrefix : ipPrefixLists) {
            String curUriString = null;
            int pageNum = 1;
            try {
                // get current page than do fetch page nums
                LOG.info(" begin fetching these pages ");
                URI uri = new URIBuilder(XIAO_HUAN_TQDL).setScheme("https").setPath(targetPrefix).build();
                curUriString = uri.toString();
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                LOG.info(" do with current type :{} ", curUriString);
                String currentPage = ipFetchGetRequest(uri, headerList);
                LOG.info(" current html : {} ", currentPage);
                TreeMap<Integer, IpLocation> aTags = matchAtages(pageNum, currentPage,
                        IpServiceConstant.PAGE_NUM_REGIS);
                LOG.info(" done ");
                LOG.info(" start fetching  current pages ");
                Set<Map.Entry<Integer, IpLocation>> aTageEntris = aTags.entrySet();
                Stack<IpLocation> pageNumsList = new Stack<>();
                addMapValueForList(aTageEntris, pageNumsList);
                if (pageNumsList.isEmpty()) {
                    LOG.info(" current ip page have no more infos , will continue. page : {} ", curUriString);
                    continue;
                }
                // 移除'>>'
                pageNumsList.pop();
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
                URI uri = new URIBuilder(curFullHref).setScheme("https").build();
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                String currentPage = ipFetchGetRequest(uri, headerList);
                if (hasNextPage(currentPage, HAS_PAGE_REGIX)) {
                    TreeMap<Integer, IpLocation> curMap = matchAtages(0, currentPage, PAGE_NUM_REGIS);
                    sortAndAdd(curMap, pageNumsStack);
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

    /**
     * 有问题 分割任务stack
     *
     * @param curUriString
     * @param pageNumsList
     */
    private void doWork(String curUriString, Stack<IpLocation> pageNumsList) {
        // 划分任务
        // 做任务
        int cur = 0;
        int curWorkSize = pageNumsList.size();
        LOG.info(" current page nums  : {} ,list : {} ", curWorkSize, pageNumsList);
        int step = curWorkSize / CORE_POOL_SIZE;
        List<Future<List<IpPoolMainDTO>>> futures = Collections.synchronizedList(new ArrayList<>());
        while (cur < curWorkSize) {
            int curEndPos = (cur + step + 1);
            curEndPos = Math.min(curEndPos, curWorkSize);
            List<IpLocation> syncWorkList = pageNumsList.subList(cur, curEndPos);
            cur = curEndPos;
            Future<List<IpPoolMainDTO>> future = EXECUTOR_SERVICE
                    .submit(new XHGetpageInfoTask(curUriString, syncWorkList));
            futures.add(future);
        }
        uploadData(futures);
    }

    /**
     * 获取每个任务的结果
     *
     * @param futures
     */
    private void uploadData(List<Future<List<IpPoolMainDTO>>> futures) {
        LOG.info(" begin stage data ");
        Assert.notEmpty(futures, " future task wouldn't empty ");
        for (Future<List<IpPoolMainDTO>> future : futures) {
            try {
                List<IpPoolMainDTO> lists = future.get();
                if (CollectionUtils.isEmpty(lists)) {
                    LOG.info(" current IpPoolMainDO list is empty , will not do insert ");
                    continue;
                }
                // change to send data to storage service
                URI uri = new URIBuilder(STORAGE_SERVICE_LOCATION).setScheme("http").setCharset(StandardCharsets.UTF_8)
                        .setPort(STORAGE_SERVICE_LOCATION_PORT).setPath(IpServiceConstant.STORAGE_SERVICE_PATH).build();
                HttpPost httpPost = new HttpPost(uri);
                ObjectMapper objectMapper = new ObjectMapper();
                String curData = objectMapper.writeValueAsString(lists);
                httpPost.setEntity(new StringEntity(curData, Consts.UTF_8));
                List<Header> headerList = new ArrayList<>();
                String curTimeMillions = String.valueOf(System.currentTimeMillis());
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                headerList.add(new BasicHeader(ORIGIN_NAME, STORAGE_SERVICE_LOCATION));
                headerList.add(new BasicHeader(CUR_TIME_SPAN, curTimeMillions));
                headerList.add(new BasicHeader(SECRET_SIGN, SignUtil.createSign(curTimeMillions, SECRET)));
                headerList.add(new BasicHeader("Content-Type", HTTP_CONTENT_TYPE_JSON));
                HttpResponse response = exeuteDefaultRequest(httpPost, headerList, false);
                vaildateReponse(response);
                HttpEntity entity = response.getEntity();
                String responseText = vaildateEntity(entity);
                LOG.info(" sent storage data success , response text : {} ", responseText);
            } catch (InterruptedException | ExecutionException | URISyntaxException | UnsupportedEncodingException
                    | JsonProcessingException e) {
                LOG.error(" send stage data error , message : {} ", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;
        LOG.info(" === SERVICE END === ");
        LOG.info(" === time : {} === ", DateFormatUtils.format(endTime, IpServiceConstant.COMMON_DATE_FORMAT_REGIX));
        LOG.info(" data fetch use minutes : {},second :{},millionSecond :{} ", useTime / 1000 / 60, useTime / 1000,
                useTime);
        startTime = 0;
        endTime = 0;
    }

    /**
     * add map values to a list
     *
     * @param mapSet
     * @param res
     * @param <K>
     * @param <V>
     */
    private <K, V> void addMapValueForList(Set<Map.Entry<K, V>> mapSet, List<V> res) {
        if (res == null) {
            LOG.info(" list is null ");
            return;
        }
        for (Map.Entry<K, V> me : mapSet) {
            V val = me.getValue();
            res.add(val);
        }
    }

    private void sortAndAdd(TreeMap<Integer, IpLocation> curMap, Stack<IpLocation> pageNumsStack) {
        Set<Map.Entry<Integer, IpLocation>> curSet = curMap.entrySet();
        // remove name is not numeral
        curSet = checkAndRemoveXHPageNumber(curSet);
        addMapValueForList(curSet, pageNumsStack);
        pageNumsStack.sort((IpLocation o1, IpLocation o2) -> {
            int val = Integer.valueOf(o1.getLocationName()) > Integer.valueOf(o2.getLocationName()) ? 1 : -1;
            return val;
        });
    }

    private Set<Entry<Integer, IpLocation>> checkAndRemoveXHPageNumber(Set<Entry<Integer, IpLocation>> curSet) {
        Assert.notEmpty(curSet, " could not check empty set ");
        var resSet = curSet.stream().filter(v -> {
            var flag = false;
            var target = v.getValue();
            if (ObjectUtils.isNotEmpty(target)) {
                if (checkPageNumberLegal(target.getLocationName(), PAGE_REGIX)) {
                    flag = true;
                }
            }
            return flag;
        }).collect(Collectors.toSet());
        return resSet;
    }

    private String ipFetchGetRequest(URI uri, List<Header> headerList) throws IOException {
        LOG.info(" prepare to send a request ");
        LOG.info(" URI : {} ", uri);
        HttpGet get = new HttpGet(uri);
        HttpResponse response = exeuteDefaultRequest(get, headerList, true);
        vaildateReponse(response);
        HttpEntity httpEntity = response.getEntity();
        String currentPage = vaildateEntity(httpEntity);
        return currentPage;
    }

    /**
     * 移除中国重复地址
     *
     * @param resAtags
     */
    private void removeMutiple(TreeMap<Integer, IpLocation> resAtags) {
        // Set<Map.Entry<Integer, IpLocation>> entrySet = resAtags.entrySet();
        // Iterator<Map.Entry<Integer, IpLocation>> iterator = entrySet.iterator();
        // while (iterator.hasNext()) {
        // Map.Entry<Integer, IpLocation> m = iterator.next();
        // IpLocation tar = m.getValue();
        // if (XIAO_HUAN_POS_CHINA.equals(tar.getLocationName()) ||
        // XIAO_HUAN_POS_AMERICA.equals(tar.getLocationName())) {
        // continue;
        // }
        // if (XIAO_HUAN_POS_AMERICA.equals(tar.getLocationName())) {
        // continue;
        // }
        // iterator.remove();
        // }
        resAtags.put(0, new IpLocation(IpServiceConstant.HC_LINK_NAME, IpServiceConstant.HC_LINK));
    }
}
