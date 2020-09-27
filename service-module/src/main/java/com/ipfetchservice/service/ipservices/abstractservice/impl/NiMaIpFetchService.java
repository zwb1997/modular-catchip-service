package com.ipfetchservice.service.ipservices.abstractservice.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant;
import com.ipfetchservice.service.common.provider.TaskThreadPoolProvider;
import com.ipfetchservice.service.ipservices.abstractservice.AbsrtactFetchIpService;
import com.ipfetchservice.service.ipservices.task.impl.NMTask;

import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// 页码检测
// 封装link
// 数据解析
// 入库
@Service("nimaipfetchservice")
public class NiMaIpFetchService extends AbsrtactFetchIpService {
    private static final Logger LOG = LoggerFactory.getLogger(NiMaIpFetchService.class);
    private static final int BEGIN_PAGE_NUMBER = 50;
    private static final int PAGE_STEP = 50;
    private static final Random RANDOM = new Random();
    private static final TaskThreadPoolProvider TASK_PROVIDER = TaskThreadPoolProvider.getInstance();
    public NiMaIpFetchService() {
        this.taskName = NM_TASK_NAME;
    }

    public List<URI> runService() {
        List<URI> nimaTaskUris = new ArrayList<>();
        try {
            URI ptURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP)
                    .setPath(NI_MA_IP_NROMAL + "/" + BEGIN_PAGE_NUMBER).build();
            URI gnURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP)
                    .setPath(NI_MA_IP_HIGH + "/" + BEGIN_PAGE_NUMBER).build();
            URI httpURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP)
                    .setPath(NI_MA_IP_HTTP + "/" + BEGIN_PAGE_NUMBER).build();
            URI httpsURI = new URIBuilder().setHost(NI_MA_IP).setScheme(IP_HTTP)
                    .setPath(NI_MA_IP_HTTPS + "/" + BEGIN_PAGE_NUMBER).build();
            nimaTaskUris.add(ptURI);
            nimaTaskUris.add(gnURI);
            nimaTaskUris.add(httpURI);
            nimaTaskUris.add(httpsURI);
        } catch (Exception e) {
            LOG.error(" create nima uri error , message :{} ", e.getMessage());
        }

        return nimaTaskUris;
    }

    /**
     * 
     * @param nimaUris
     */
    public void fetchEvertPage(List<URI> nimaUris) {

        for (URI i : nimaUris) {
            int pageSize = pageNumDetect(i);
            // 直接提交任务
            doWork(pageSize, i);
        }
    }

    /**
     * create callable to fetch ip proxy data details
     * 
     * @param pageSize
     */
    private void doWork(int pageSize, URI i) {
        List<Future<List<IpPoolMainDTO>>> waitingResutls = new LinkedList<>();
        try {
            List<URI> workUris = new LinkedList<>();
            String path = i.getPath().split("/")[1];
            URI uri;
            while (pageSize-- > 0) {
                uri = new URIBuilder(i).setPath(path + "/" + pageSize).build();
                workUris.add(uri);
            }
            int workSize = workUris.size();
            int workPos = 0;
            int workGrowStep = workSize / TaskThreadPoolProvider.getCorePoolSize();
            while (workPos < workSize) {
                int endPos = Math.min(workSize, workPos + workGrowStep);
                List<URI> subWorkUris = workUris.subList(workPos, endPos);
                Future<List<IpPoolMainDTO>> future = TASK_PROVIDER.submitTaskWork(new NMTask(subWorkUris,path));
                waitingResutls.add(future);
                workPos += workGrowStep;
            }
        } catch (Exception e) {
            LOG.error(" doWork error , message :{} ", e.getMessage());
        }
        doUpload(waitingResutls);
    }

    /**
     * sending ip data to remote service
     * @param waitingResutls
     */
    private void doUpload(List<Future<List<IpPoolMainDTO>>> waitingResutls) {
        LOG.info(" waiting for get futures... ");
        int taskResultSize = waitingResutls.size();
        boolean flag = true;
        while(flag){
            
        }
    }

    /**
     * detect current uri page nums
     * 先以每50页为步长查下个页面有没有值; 当没有值时 从当前页数开始往回按每页查，并直到页面有值为止
     * @param URI i
     * @return int pageSize
     */
    private int pageNumDetect(URI i) {
        String path = i.getPath().split("/")[1];
        List<Header> headers = new LinkedList<>();
        headers.add(new BasicHeader("Host", NI_MA_IP));
        headers.add(new BasicHeader("Referer", NIMA_REFERER));
        HttpGet get = new HttpGet();
        int beginPageSize = BEGIN_PAGE_NUMBER;
        int pageSize = -1;
        boolean flag1 = true;
        boolean flag2 = false;
        try {
            URI uri = new URIBuilder(i).build();
            while (flag1 || flag2) {
                get.setURI(uri);
                String responsetext = clientUtil.exeuteDefaultRequest(get, headers, true);
                boolean haseffectivePage = pageUtil.hasNextPage(responsetext, IpServiceConstant.HAS_PAGE_REGIX);
                LOG.info(" if page has tbody > tr : {} ", haseffectivePage);
                if (flag1) {
                    if (haseffectivePage) {
                        LOG.info(" page numer :{}  has value ", beginPageSize);
                        beginPageSize += PAGE_STEP;
                    } else {
                        flag1 = false;
                        flag2 = true;
                    }
                } else if (flag2) {
                    if (!haseffectivePage) {
                        LOG.info(" page numer :{}  has no value , will count down page num ", beginPageSize);
                        beginPageSize -= 1;
                    } else {
                        LOG.info(" find the last effective page ");
                        flag2 = false;
                    }
                }
                pageSize = beginPageSize;
                uri = new URIBuilder(i).setPath(path + "/" + beginPageSize).build();

                /**
                 * random pause 1 - 5 seconds
                 */
                Thread.sleep(RANDOM.nextInt(5) * 1000);
            }
        } catch (Exception e) {
            LOG.error(" pageNumDetect error , message :{}  ", e.getMessage());
        }
        LOG.info(" page : {}  has nums :{}", path, pageSize);
        return pageSize;

    }

    @Override
    protected void serviceEntry() {
        fetchEvertPage(runService());
    }

}
