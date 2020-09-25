package com.ipfetchservice.service.utils.page.extractservice.impl;

import java.util.LinkedList;
import java.util.List;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractStrategy;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
// <th>代理IP</th>
// <th>代理协议</th>
// <th>匿名度</th>
// <th>代理位置</th>
// <th>响应速度</th>
// <th>存活时间</th>

// <th>打分</th>
public class NMPageExtractStrategy implements PageExtractStrategy{
    
    @Override
    public List<IpPoolMainDTO> extractInfoWithPage(String page,Elements elements) {
        List<IpPoolMainDTO> infoList = new LinkedList<>();
        for(Element e : elements){
            String infos = e.text();
            String[] infoArr = infos.split(" ");
            infoList.add(combineInfos(infoArr));
        }
        return infoList;
    }
    /**
     * need consider use regex...
     * @param infoArr
     * @return
     */
    private IpPoolMainDTO combineInfos(String[] infoArr) {
        for(String s : infoArr){

        }
        return null;
    }


}
