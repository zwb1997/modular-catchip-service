package com.ipfetchservice.service.utils.page.extractservice.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.model.exceptions.DebugException;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractStrategy;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class XHPageExtractStrategy implements PageExtractStrategy{
    
    private static final Logger LOG = LoggerFactory.getLogger(XHPageExtractStrategy.class);
    @Override
    public List<IpPoolMainDTO> extractInfoWithPage(String page,Elements elements) {
        if (CollectionUtils.isEmpty(elements)) {
            LOG.info(" elements is empty,will not work ");
            return null;
        }
        Iterator<Element> iterator = elements.iterator();
        LinkedList<IpPoolMainDTO> res = new LinkedList<>();
        while (iterator.hasNext()) {
            try {
                Element e = iterator.next();
                List<String> infos = findTextNode(e);
                LOG.info(" current infos : {} ", infos);
                res.add(PAGE_UTIL.createInstance(infos));
            } catch (Exception e) {
                LOG.error(" error , message :{} ", e.getMessage());
            }
        }
        return res;
    }

    /**
     * 替代所有空格 为 斜杠
     * @param Element cure
     * @return List<String>
     */
    public List<String> findTextNode(Element cure) {
        if (cure == null) {
            LOG.info(" current Element is empty ");
            return null;
        }
        Elements tds = cure.select("td");
        List<String> resList = Arrays.asList(EMPTY_ARR);
        int i = 0;
        for (Element e : tds) {
            if (i >= 10) {
                throw new DebugException(" parameters size is overflow ");
            }
            String val = e.text().replace(" ", "/");
            resList.set(i++, val);
        }
        return resList;
    }
}
