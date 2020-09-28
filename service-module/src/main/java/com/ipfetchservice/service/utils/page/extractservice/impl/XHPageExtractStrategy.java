package com.ipfetchservice.service.utils.page.extractservice.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

public class XHPageExtractStrategy implements PageExtractStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(XHPageExtractStrategy.class);

    @Override
    public LinkedHashSet<IpPoolMainDTO> extractInfoWithPage(String page, Elements elements) {
        if (CollectionUtils.isEmpty(elements)) {
            LOG.info(" elements is empty,will not work ");
            return null;
        }
        Iterator<Element> iterator = elements.iterator();
        LinkedHashSet<IpPoolMainDTO> res = new LinkedHashSet<IpPoolMainDTO>();
        while (iterator.hasNext()) {
            try {
                Element e = iterator.next();
                List<String> infos = findTextNode(e);
                // LOG.info(" current infos : {} ", infos);
                res.add(createInstance(infos));
            } catch (Exception e) {
                LOG.error(" error , message :{} ", e.getMessage());
            }
        }
        return res;
    }

    /**
     * 替代所有空格 为 斜杠
     * 
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

    /**
     * list must be 10 elements; 固定格式的集合; 0 -> ip ; 1 -> port ; 2 -> ip地址 ; 3 ->
     * ip供应商 ; 4 -> 是否支持https ; 5 -> 是否支持post请求 ; 6 -> 匿名程度 ; 7 -> 速度; 8 ->
     * 网站检测ip入库时间 ; 9 -> 网站检测 ip最后有效时间; 具体请看 {@link IpPoolMainDTO }
     * 
     * @param infos
     * @return IpPoolMainDTO
     */
    private IpPoolMainDTO createInstance(List<String> infos) {
        if (CollectionUtils.isEmpty(infos) || infos.size() < COMBINE_ARRAY_REQUIRE_LENGTH
                || infos.size() > COMBINE_ARRAY_REQUIRE_LENGTH) {
            LOG.error(" error instance size must be 10 ");
            return null;
        }
        IpPoolMainDTO model = null;
        try {
            byte anonymityDegree = 0;
            switch (infos.get(6)) {
                case ANONYMITY_NORMAL:
                    anonymityDegree = DEGREE_TRANSPARENT_PROXY;
                    break;
                case ANONYMITY_HIGH:
                    anonymityDegree = DEGREE_HIGHT_PROXY;
                    break;
                default:
                    anonymityDegree = DEGREE_UNKNOW_PROXY;
            }
            model = new IpPoolMainDTO(String.valueOf(infos.get(0)), Integer.parseInt(infos.get(1)), infos.get(2),
                    infos.get(3), NOT_SUPPORT_CHINESE.equals(infos.get(4)) ? SUPPORT_NUM : NOT_SUPPORT_NUM,
                    NOT_SUPPORT_CHINESE.equals(infos.get(5)) ? SUPPORT_NUM : NOT_SUPPORT_NUM, anonymityDegree,
                    infos.get(7), infos.get(8), infos.get(9));
        } catch (Exception e) {
            LOG.error(" xiaohuan createInstance error,messgae : {} ", e.getMessage());
        }
        return model;

    }
}
