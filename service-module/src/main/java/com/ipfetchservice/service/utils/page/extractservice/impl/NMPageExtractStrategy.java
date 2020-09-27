package com.ipfetchservice.service.utils.page.extractservice.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractStrategy;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
// <th>代理IP</th>
// <th>代理协议</th>
// <th>匿名度</th>
// <th>代理位置</th>
// <th>响应速度</th>
// <th>存活时间</th>
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// <th>打分</th>
public class NMPageExtractStrategy implements PageExtractStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NMPageExtractStrategy.class);

    @Override
    public List<IpPoolMainDTO> extractInfoWithPage(String page, Elements elements) {
        List<IpPoolMainDTO> infoList = new LinkedList<>();
        StringBuffer buffer = new StringBuffer();
        for (Element e : elements) {
            List<Node> curNodes = e.childNodes();
            try {

                for (Node n : curNodes) {
                    buffer.append(n.toString());
                }
                String[] vals = buffer.toString().replace("</td>", "").split("<td>");
                infoList.add(combineInfos(vals));
            } catch (Exception ex) {
                LOG.error(" extract element error ,message :{} ", ex.getMessage());
            } finally {
                // save memory maybe slower
                buffer.delete(0, buffer.length());
            }
        }
        return infoList;
    }

    /**
     * need consider use regex... "120.84.100.203:9999" ip + port ; "HTTP代理" proxy
     * support ; "普通代理" proxy type ; "中国" location ; "广东"location ; "揭阳"location;
     * "联通"location ; "3.17" speed ; "384天" alive time ; "18小时" alive time ; "20分钟"
     * alive time ; "58秒" alive time ; "274" web set score;
     * 
     * @param infoArr
     * @return
     */
    private IpPoolMainDTO combineInfos(String[] infoArr) {
        String[] ipAndPort = infoArr[0].split(":");
        if (ipAndPort.length < 2) {
            LOG.info(" ip or port is lost will ignore ");
            return null;
        }
        IpPoolMainDTO model = null;
        try {
            String ip = ipAndPort[0];
            int port = Integer.valueOf(ipAndPort[1]);
            List<String> locationAndVendor = Arrays.asList(infoArr[3].split(" "));
            int lAndVsize = locationAndVendor.size();
            String location = locationAndVendor.subList(0, lAndVsize - 1).toString();
            location = location.replace("[", "").replace("]", "").replace(" ", "/");
            String vendor = locationAndVendor.get(lAndVsize - 1);
            
        } catch (Exception e) {
            LOG.info(" nima combineinfos error , message :{}", e.getMessage());
        }

        return model;
    }

}
