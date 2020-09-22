package com.zzz.service.ipservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzz.model.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.zzz.model.entitymodel.servicebase.constants.IpServiceConstant;
import com.zzz.service.common.provider.TaskThreadPoolProvider;
import com.zzz.service.ipservices.abstractservice.AbsrtactFetchIpService;
import com.zzz.service.ipservices.task.XHGetpageInfoTask;
import com.zzz.service.utils.SignUtil;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.zzz.model.entitymodel.servicebase.constants.IpServiceConstant.*;

/**
 * 记得超时重连
 */
@Service("xiaohuanipfetchservice")
public class XiaoHuanIpFetchService extends AbsrtactFetchIpService {

    public XiaoHuanIpFetchService() {
        taskName = IpServiceConstant.XH_TASK_NAME;
    }

    private static final Logger LOG = LoggerFactory.getLogger(XiaoHuanIpFetchService.class);

    @Override
    protected void serviceEntry() {
        START_TIME = System.currentTimeMillis();
        LOG.info(" === {} SERVICE START === ", XH_TASK_NAME);
        LOG.info(" start time : {} ", DateFormatUtils.format(new Date(), COMMON_DATE_FORMAT_REGIX));
        fetchIpPages(runService());
        END_TIME = System.currentTimeMillis();
        var useTime = END_TIME - START_TIME;
        LOG.info(" === {} SERVICE END === ", XH_TASK_NAME);
        LOG.info(" end time : {} ", DateFormatUtils.format(new Date(), COMMON_DATE_FORMAT_REGIX));
        LOG.info(" using time : miniutes :{} ,seconds :{} ", useTime / 1000 / 60, useTime / 1000);
    }

    public List<String> runService() {
        List<String> aTagList = new LinkedList<>();
        List<String> aTagLists = getXHlist();
        if (CollectionUtils.isEmpty(aTagLists)) {
            LOG.info(" ip prefix nums is 0 , will not fetch full page nums.... ");
            return aTagList;
        }
        int size = aTagLists.size();
        LOG.info(" ip prefix nums : {} ", size);
        LOG.info(" prepare to fetch full page nums ");
        return aTagLists;
    }

    /**
     * download first page get ip area list
     */
    public List<String> getXHlist() {
        LOG.info(" === prepare to get xiaohuan ip pages === ");
        List<String> ress = new LinkedList<>();
        try {
            // 小幻的参数 暂时用uuid代替 能正常请求
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
            String html = clientUtil.ipFetchGetRequest(uri, requestHeaders, true);
            TreeMap<Integer, IpLocation> resAtags = pageUtil.matchAtages(1, html,
                    IpServiceConstant.PAGE_AREA_LIST_REGIX);
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
            LOG.info(" === done === ");
        } catch (Exception e) {
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
                LOG.info(" === begin fetching these pages === ");
                URI uri = new URIBuilder(XIAO_HUAN_TQDL).setScheme("https").setPath(targetPrefix).build();
                curUriString = uri.toString();
                List<Header> headerList = new ArrayList<>();
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                LOG.info(" do with current type :{} ", curUriString);
                String currentPage = clientUtil.ipFetchGetRequest(uri, headerList, true);
                LOG.info(" current html : {} ", currentPage);
                TreeMap<Integer, IpLocation> aTags = pageUtil.matchAtages(pageNum, currentPage,
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
     * fetch every page links
     * 
     * @param curUriString
     * @param pageNumsStack
     */
    private void fetchCurrentPages(String curUriString, Stack<IpLocation> pageNumsStack) {
        if (CollectionUtils.isEmpty(pageNumsStack)) {
            LOG.info(" pageNumsStack is null ");
            return;
        }
        LOG.info(" === start getting current  <a> tags === ");
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
                String currentPage = clientUtil.ipFetchGetRequest(uri, headerList, true);
                if (pageUtil.hasNextPage(currentPage, HAS_PAGE_REGIX)) {
                    TreeMap<Integer, IpLocation> curMap = pageUtil.matchAtages(0, currentPage, PAGE_NUM_REGIS);
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
     * assigned works to threads
     * 
     * @param curUriString
     * @param pageNumsList
     */
    private void doWork(String curUriString, Stack<IpLocation> pageNumsList) {
        // 划分任务
        // 做任务
        int cur = 0;
        int curWorkSize = pageNumsList.size();
        LOG.info(" === current page nums  : {} ,list : {} === ", curWorkSize, pageNumsList);
        int step = curWorkSize / TaskThreadPoolProvider.getCorePoolSize();
        List<Future<List<IpPoolMainDTO>>> futures = new LinkedList<>();
        while (cur < curWorkSize) {
            int curEndPos = (cur + step + 1);
            curEndPos = Math.min(curEndPos, curWorkSize);
            List<IpLocation> workList = pageNumsList.subList(cur, curEndPos);
            cur = curEndPos;
            try {
                Future<List<IpPoolMainDTO>> future = TaskThreadPoolProvider
                        .submitTaskWork(new XHGetpageInfoTask(curUriString, workList));
                futures.add(future);
            } catch (Exception e) {
                LOG.error(" submit task error , message :{} ", e);
            }
        }
        uploadData(futures);
    }

    /**
     * 获取每个任务的结果
     * 
     * @param futures
     */
    private void uploadData(List<Future<List<IpPoolMainDTO>>> futures) {
        LOG.info(" === begin stage data === ");
        Assert.notEmpty(futures, " future task wouldn't empty ");
        for (Future<List<IpPoolMainDTO>> future : futures) {
            try {
                LOG.info(" \\\\waiting to get future result...\\\\ ");
                List<IpPoolMainDTO> lists = future.get(10, TimeUnit.MINUTES);
                if (CollectionUtils.isEmpty(lists)) {
                    LOG.info(" current IpPoolMainDO list is empty , will not do insert ");
                    continue;
                }
                ObjectMapper objectMapper = new ObjectMapper();
                String curData = objectMapper.writeValueAsString(lists);
                URI uri = new URIBuilder(STORAGE_SERVICE_LOCATION).setScheme("http").setCharset(StandardCharsets.UTF_8)
                        .setPort(STORAGE_SERVICE_LOCATION_PORT).setPath(IpServiceConstant.STORAGE_SERVICE_PATH).build();
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setEntity(new StringEntity(curData, Consts.UTF_8));
                List<Header> headerList = new ArrayList<>();
                String curTimeMillions = String.valueOf(System.currentTimeMillis());
                headerList.add(new BasicHeader("user-agent", USER_AGENT));
                headerList.add(new BasicHeader(ORIGIN_NAME, STORAGE_SERVICE_LOCATION));
                headerList.add(new BasicHeader(CUR_TIME_SPAN, curTimeMillions));
                headerList.add(new BasicHeader(SECRET_SIGN, signUtil.createSign(curTimeMillions, SECRET)));
                headerList.add(new BasicHeader("Content-Type", HTTP_CONTENT_TYPE_JSON));
                HttpResponse response = clientUtil.exeuteDefaultRequest(httpPost, headerList, false);
                clientUtil.vaildateReponse(response);
                HttpEntity entity = response.getEntity();
                String responseText = pageUtil.vaildateEntity(entity);
                LOG.info(" sent storage data success , response text : {} ", responseText);
            } catch (Exception e) {
                LOG.error(" send stage data error , message : {} ", e.getMessage());
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

    /**
     * 保证顺序加入link
     * 
     * @param curMap
     * @param pageNumsStack
     */
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

    /**
     * 移除重复link
     * 
     * @param curSet
     * @return
     */
    private Set<Entry<Integer, IpLocation>> checkAndRemoveXHPageNumber(Set<Entry<Integer, IpLocation>> curSet) {
        Assert.notEmpty(curSet, " could not check empty set ");
        var resSet = curSet.stream().filter(v -> {
            var flag = false;
            var target = v.getValue();
            if (ObjectUtils.isNotEmpty(target)) {
                if (pageUtil.checkPageLegal(target.getLocationName(), PAGE_REGIX)) {
                    flag = true;
                }
            }
            return flag;
        }).collect(Collectors.toSet());
        return resSet;
    }

    /**
     * 增加指定的页面
     * 
     * @param resAtags
     */
    private void removeMutiple(TreeMap<Integer, IpLocation> resAtags) {
        resAtags.put(0, new IpLocation(IpServiceConstant.HC_LINK_NAME, IpServiceConstant.HC_LINK));
    }
}
