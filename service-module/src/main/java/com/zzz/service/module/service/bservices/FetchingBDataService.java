package com.zzz.service.module.service.bservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzz.entitymodel.servicebase.DO.VideoInfoModelDO;
import com.zzz.entitymodel.servicebase.DO.VideoTypeInfoDO;
import com.zzz.entitymodel.servicebase.DTO.QueryCountsDTO;
import com.zzz.entitymodel.servicebase.DTO.VideoUpdateInfoDTO;
import com.zzz.entitymodel.servicebase.constants.FetchServiceConstants;
import com.zzz.service.module.mapper.FetchingDataMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 先把数据 发送 和 入库一套移植过来
 */
@Service
public class FetchingBDataService {
    private final static String NULL_STR = "--";
    private final static Logger LOG = LoggerFactory.getLogger(FetchingBDataService.class);
    private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String VIDEO_ADDRESS_URL="https://www.bilibili.com/video/";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private FetchingDataMapper fetchingDataMapper;

    public VideoUpdateInfoDTO getInfoLisByMid(String mid) throws URISyntaxException, IOException {


        CloseableHttpClient httpclient = HttpClients.createDefault();

        //代理
//        HttpHost proxy = new HttpHost("", 8560);
//        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setRoutePlanner(routePlanner)
//                .build();
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.bilibili.com")
                .setPath("/x/space/arc/search")
                .setParameter("pn", "1")
                //暂时使用 int最大值来处理分页
                .setParameter("ps", "100")
                .setParameter("order", "click")
                .setParameter("keyword", "")
                .setParameter("mid", mid).build();

        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(":authority", "api.bilibili.com");
        httpGet.setHeader(":method", "GET");
        httpGet.setHeader(":path", "/x/space/arc/search?mid=306&pn=1&ps=25&jsonp=jsonp");
        httpGet.setHeader("accept", "application/json, text/plain, */*");
        httpGet.setHeader("accept-encoding", "gzip, deflate, br");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,und;q=0.8,ja;q=0.7");
        httpGet.setHeader("origin", "https://space.bilibili.com");
        httpGet.setHeader("sec-fetch-dest", "document");
        httpGet.setHeader("sec-fetch-mode", "navigate");
        httpGet.setHeader("sec-fetch-site", "none");
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36");
        System.out.println("begin execute http request.....");
        HttpResponse response = httpclient.execute(httpGet);
        System.out.println("HTTP response code : " + response.getStatusLine().getStatusCode());
        System.out.println("analyze execute httpEntity...... ");
        HttpEntity entity = response.getEntity();
        String responseEntity = EntityUtils.toString(entity, "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJsonNode = objectMapper.readTree(responseEntity);
        return extractInfo(responseJsonNode);

    }

    /**
     * 解析JsonNode
     *
     * @param responseJsonNode
     * @return
     */
    public VideoUpdateInfoDTO extractInfo(JsonNode responseJsonNode) {
        Assert.notNull(responseJsonNode, "被提取的JSON节点不能为空!");
        JsonNode dataNode = responseJsonNode.get("data");
        JsonNode listNode = dataNode.get("list");
        JsonNode pageNode = dataNode.get("page");
        String videoCounts = pageNode.get("count").toString();
        LOG.info("该up主有" + videoCounts + "条视频");
        JsonNode tList = listNode.get("tlist");
        JsonNode vList = listNode.get("vlist");
        //存基础信息
        int baseInfos = batchUpdateExtractBaseInfo(tList);
        //存视频信息
        int videoInfos = batchUpdateExtractVideoInfo(vList);
        VideoUpdateInfoDTO updateInfo = new VideoUpdateInfoDTO(baseInfos, videoInfos);
        return updateInfo;
    }
    /**
     * 批量存储视频信息数据
     * @param infoModels
     */
    private int batchQueryExtractInfo(List<VideoInfoModelDO> infoModels) {
        Assert.notNull(infoModels, "插入的集合不能为空");
        LOG.info("开始存储视频信息数据");
        int count =  fetchingDataMapper.insertBVideoInfo(infoModels);
        LOG.info("视频信息数据存储结束");
        return count;
    }

    /**
     * 批量更新视频信息
     *
     * @param videoNode
     */
    public int batchUpdateExtractVideoInfo(JsonNode videoNode) throws IllegalArgumentException {
        List<VideoInfoModelDO> infoModels = new LinkedList<>();
        if (videoNode.isArray() && videoNode.size() != 0) {
            ArrayNode arrayNode = (ArrayNode) videoNode;
            for (JsonNode jsonNode : arrayNode) {
                infoModels.add(buildInfoObject(jsonNode));
            }
            infoModels = extractVideoInfo(infoModels);
            if(infoModels.size() == 0){
                LOG.info("当此获取相关视频数据全已有,不再存储...");
                return 0;
            }else{
                LOG.info("开始执行批量插入...");
                int count = batchQueryExtractInfo(infoModels);
                LOG.info("批量插入结束...");
                return count;
            }
        } else {
            LOG.info("该up主无视频");
            return 0;
        }
    }
    /**
     * 过滤重复视频信息
     * @param infoModels
     * @return
     */
    private List<VideoInfoModelDO> extractVideoInfo(List<VideoInfoModelDO> infoModels) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT aid_b as aid FROM b_video_info WHERE statusCode <> 0");
        List<QueryCountsDTO> aids = jdbcTemplate.query(sql.toString(),new BeanPropertyRowMapper<QueryCountsDTO>(QueryCountsDTO.class));
        Iterator<VideoInfoModelDO> infoModelIterator = infoModels.iterator();
        while(infoModelIterator.hasNext()){
            if(aids.contains(infoModelIterator.next())){
                infoModelIterator.remove();
            }
        }
        return infoModels;
    }
    /**
     * 批量更新视频分类
     *
     * @param tList
     */
    private int batchUpdateExtractBaseInfo(JsonNode tList) {
        if (tList.size() == 0) {
            LOG.info("无类别");
            return 0;
        } else {
            ObjectNode objNode = (ObjectNode) tList;
            Iterator<String> fieldNames = objNode.fieldNames();
            List<VideoTypeInfoDO> typeInfoModels = new ArrayList<>();
            while (fieldNames.hasNext()) {
                String tarCode = fieldNames.next();
                JsonNode node = objNode.get(tarCode);
                String tarName = StringUtils.isNotBlank(node.get("name").toString()) ? node.get("name").toString().replace("\"", "") : "--";
                String videoCount = StringUtils.isNotBlank(node.get("count").toString()) ? node.get("count").toString().replace("\"", "") : "--";
                LOG.info("视频类型->变量名:{},视频类型->变量值{},稿件数量->{} ", tarCode, tarName, videoCount);
                if (NULL_STR.equals(tarName) || NULL_STR.equals(videoCount)) {
                    continue;
                }
                typeInfoModels.add(new VideoTypeInfoDO(tarCode, tarName, 1));
            }
            LOG.info("准备存储数据.....");
            List<VideoTypeInfoDO> batchList = removeExisteatas(typeInfoModels);
            if (!CollectionUtils.isEmpty(batchList)) {
                LOG.info("存在未录入数据，执行插入");
                return fetchingDataMapper.insertBBaseInfo(batchList);
            } else {
                LOG.info("数据全部重复，不插入数据");
                LOG.info("存储数据结束...");
                return 0;
            }
        }
    }

    /**
     * 移除已有视频类别数据
     * 返回移除后的list
     *
     * @param list
     * @return
     */
    private List<VideoTypeInfoDO> removeExisteatas(List<VideoTypeInfoDO> list) {
        LOG.info("检查类别是否存在...");
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT video_type_name as videoTypeName FROM b_video_type");
        List<Map<String, Object>> resList = jdbcTemplate.queryForList(sql.toString());
        Iterator<VideoTypeInfoDO> infoModelIterator = list.iterator();

        while (infoModelIterator.hasNext()) {
            String tarName = infoModelIterator.next().getVideoTypeName();
            for (Map<String, Object> map : resList) {
                if (map.containsValue(tarName)) {
                    LOG.info("数据:{} 重复,将移除", tarName);
                    infoModelIterator.remove();
                }
            }
        }
        return list;
    }


    /**
     * 解析当前 jsonNode 并转成VideoInfoModel 实体
     * 这里返回集合中的对象变量名 应该每次解析出来，不应该存在枚举类中
     * PS:一直set不太好用，应思考其他方案 如动态生成实体类...
     * @param jsonNode
     * @return
     * @throws IllegalArgumentException
     */
    private VideoInfoModelDO buildInfoObject(JsonNode jsonNode) throws IllegalArgumentException {
        if (ObjectUtils.isEmpty(jsonNode)) {
            throw new IllegalArgumentException("maps为空");
        }

        VideoInfoModelDO videoInfoModel = new VideoInfoModelDO(
                UUID.randomUUID().toString().replace("-","").substring(0,16),
                jsonNode.findValue(FetchServiceConstants.COMMENT).intValue(),
                jsonNode.findValue(FetchServiceConstants.TYPEID).intValue(),
                jsonNode.findValue(FetchServiceConstants.PLAY).intValue(),
                jsonNode.findValue(FetchServiceConstants.PIC).textValue(),
                jsonNode.findValue(FetchServiceConstants.SUBTITLE).textValue(),
                jsonNode.findValue(FetchServiceConstants.DESCRIPTION).textValue(),
                jsonNode.findValue(FetchServiceConstants.COPYRIGHT).textValue(),
                jsonNode.findValue(FetchServiceConstants.TITLE).textValue(),
                jsonNode.findValue(FetchServiceConstants.REVIEW).intValue(),
                jsonNode.findValue(FetchServiceConstants.AUTHOR).textValue(),
                jsonNode.findValue(FetchServiceConstants.MID).intValue(),
                DATEFORMAT.format(jsonNode.findValue(FetchServiceConstants.CREATED).longValue()*1000),
                jsonNode.findValue(FetchServiceConstants.LENGTH).textValue(),
                jsonNode.findValue(FetchServiceConstants.VIDEO_REVIEW).intValue(),
                jsonNode.findValue(FetchServiceConstants.AID).intValue(),
                jsonNode.findValue(FetchServiceConstants.BVID).textValue(),
                jsonNode.findValue(FetchServiceConstants.HIDE_CLICK).booleanValue() ? 1 :0,
                jsonNode.findValue(FetchServiceConstants.IS_PAY).intValue(),
                jsonNode.findValue(FetchServiceConstants.IS_UNION_VIDEO).intValue(),
                new String(VIDEO_ADDRESS_URL ).concat(jsonNode.findValue(FetchServiceConstants.BVID).textValue()),
                1
        );
        //统一非空校验
//        validFiles(videoInfoModel);
        return videoInfoModel;
    }
}
