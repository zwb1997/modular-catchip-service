package com.ipfetchservice.service.utils.page.extractservice.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractStrategy;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// <th>代理IP</th>
// <th>代理协议</th>
// <th>匿名度</th>
// <th>代理位置</th>
// <th>响应速度</th>
// <th>存活时间</th>
// <th>打分</th>
public class NMPageExtractStrategy implements PageExtractStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NMPageExtractStrategy.class);

    @Override
    public LinkedHashSet<IpPoolMainDTO> extractInfoWithPage(String page, Elements elements) {
        LinkedHashSet<IpPoolMainDTO> infoSet = new LinkedHashSet<>();
        StringBuffer buffer = new StringBuffer();
        for (Element e : elements) {
            List<Node> curNodes = e.childNodes();
            try {
                for (Node n : curNodes) {
                    buffer.append(n.toString());
                }
                String[] bufferArr = buffer.toString().replace("</td>", "").trim().split("<td>");
                List<String> vals = Lists.newArrayList(bufferArr);
                vals = removeEmptyValue(vals);
                infoSet.add(assembleInfos(vals));
            } catch (Exception ex) {
                LOG.error(" extract element error ,message :{} ", ex.getMessage());
            } finally {
                // save memory maybe slower
                buffer.delete(0, buffer.length());
            }
        }
        return infoSet;
    }

    private List<String> removeEmptyValue(List<String> vals) {
        Iterator<String> iterator = vals.iterator();
        while (iterator.hasNext()) {
            String val = iterator.next();
            if (StringUtils.isBlank(val)) {
                iterator.remove();
            }
        }
        vals = vals.stream().map(v -> v.trim()).collect(Collectors.toList());
        return vals;
    }

    /**
     * "120.84.100.203:9999" ip + port ; "HTTP代理" proxy support ; "普通代理" proxy type
     * ; "中国" location ; "广东"location ; "揭阳"location; "联通"location ; "3.17" speed ;
     * "384天" alive time ; "18小时" alive time ; "20分钟" alive time ; "58秒" alive time
     * ; "274" web set score;
     * 
     * @param infoArr
     * @return
     */
    private IpPoolMainDTO assembleInfos(List<String> infoList) {
        int listSize = infoList.size();
        String[] infoArr = new String[listSize];
        infoList.toArray(infoArr);

        String[] ipAndPort = infoArr[0].split(":");
        if (ipAndPort.length < 2) {
            LOG.info(" ip or port is lost will ignore ");
            return null;
        }
        IpPoolMainDTO model = null;
        String ip = "";
        int port = -1;
        String location = "";
        String vendor = "";
        byte isSupportHttps = NOT_SUPPORT_NUM;
        byte proxyType = DEGREE_UNKNOW_PROXY;
        String speed = "";
        try {
            ip = ipAndPort[0];

            port = Integer.valueOf(ipAndPort[1]);

            List<String> locationAndVendor = Arrays.asList(infoArr[3].split(" "));
            int locationAndVendorSize = locationAndVendor.size();
            location = locationAndVendor.subList(0, locationAndVendorSize - 1).toString();
            location = location.replace("[", "").replace("]", "").replace(" ", "/").replace(",", "");
            vendor = locationAndVendor.get(locationAndVendorSize - 1);

            int supportCounts = infoArr[1].split(",").length;
            if (NI_MA_PROXY_TYPE_HTTPS.equals(infoArr[1]) || supportCounts == 2) {
                isSupportHttps = SUPPORT_NUM;
            }
            switch (infoArr[2]) {
                case NI_MA_PROXY_LEVEL_NORMAL:
                    proxyType = DEGREE_TRANSPARENT_PROXY;
                    break;
                case NI_MA_PROXY_LEVEL_HIGH:
                    proxyType = DEGREE_HIGHT_PROXY;
                    break;
                default:
                    proxyType = DEGREE_UNKNOW_PROXY;
            }
            speed = infoArr[4];
            model = new IpPoolMainDTO(ip, port, location, vendor, isSupportHttps, (byte) -1, proxyType, speed, "", "");
            return model;
        } catch (Exception e) {
            LOG.error(" nima assembleInfos error , message :{}", e.getMessage());
        }
        return model;
    }

}
