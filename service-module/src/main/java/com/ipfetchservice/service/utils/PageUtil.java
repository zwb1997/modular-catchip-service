package com.ipfetchservice.service.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpLocation;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractor;

import static com.ipfetchservice.model.entitymodel.servicebase.constants.IpServiceConstant.*;

@Component
public class PageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtil.class);
    private static final int COMBINE_ARRAY_REQUIRE_LENGTH = 10;
    private static final Lock LOCK = new ReentrantLock();

    /**
     * @param html
     * @param selectParmas
     * @return TreeMap<Integer, Map < String, String>> get page a tags text value
     *         and href value
     */
    public TreeMap<Integer, IpLocation> matchAtages(int num, String html, String selectParmas) {
        TreeMap<Integer, IpLocation> res = new TreeMap<>();
        if (StringUtils.isBlank(html)) {
            LOG.info(" current html is null ");
            return res;
        }
        int curNum = num;
        Document doc = Jsoup.parse(html);
        Elements es = StringUtils.isBlank(selectParmas) ? doc.select("a[href]") : doc.select(selectParmas);
        for (Element e : es) {
            String tagText = e.text();
            curNum = tagText.matches("^\\d+$") ? Integer.valueOf(tagText) : ++curNum;
            String tagHrefArrt = e.attr("href");
            res.put(curNum, new IpLocation(tagText, tagHrefArrt));
        }
        return res;
    }

    /**
     * detect if page has value by section
     * 
     * @param curDocmentHtml
     * @param exceptSection
     * @return boolean
     */
    public boolean hasNextPage(String curDocmentHtml, String exceptSection) {
        Document doc = Jsoup.parse(curDocmentHtml);
        Elements bodyEs = doc.select(exceptSection);

        if (bodyEs.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * match element by given regix
     *
     * @param html
     * @param sectionRegix
     * @return
     */
    public Elements fetchElementWithSection(String html, String sectionRegix) {
        LOCK.lock();
        Element element = Jsoup.parse(html);
        Elements elements = element.select(sectionRegix);
        LOCK.unlock();
        return elements;
    }

    /**
     * list must be 10 elements; 固定格式的集合; 0 -> ip ; 1 -> port ; 2 -> ip地址 ; 3 ->
     * ip供应商 ; 4 -> 是否支持https ; 5 -> 是否支持post请求 ; 6 -> 匿名程度 ; 7 -> 速度; 8 -> 网站检测
     * ip入库时间 ; 9 -> 网站检测 ip最后有效时间; 具体请看 {@link IpPoolMainDTO }
     * 
     * @param infos
     * @return IpPoolMainDTO
     */
    public IpPoolMainDTO createInstance(List<String> infos) {
        if (CollectionUtils.isEmpty(infos) || infos.size() < COMBINE_ARRAY_REQUIRE_LENGTH
                || infos.size() > COMBINE_ARRAY_REQUIRE_LENGTH) {
            LOG.error(" error instance size must be 10 ");
            return null;
        }
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
        IpPoolMainDTO model = null;
        try {
            model = new IpPoolMainDTO(String.valueOf(infos.get(0)), Integer.parseInt(infos.get(1)), infos.get(2),
                    infos.get(3), NOT_SUPPORT_CHINESE.equals(infos.get(4)) ? SUPPORT_NUM : NOT_SUPPORT_NUM,
                    NOT_SUPPORT_CHINESE.equals(infos.get(5)) ? SUPPORT_NUM : NOT_SUPPORT_NUM, anonymityDegree,
                    infos.get(7), infos.get(8), infos.get(9));
        } catch (Exception e) {
            LOG.error(" combine error,messgae : {} ", e.getMessage());
        }
        return model;
    }

    /**
     * check page name is match the section
     * 
     * @param pageText : use to check
     * @param section  : regex
     * @return true if match the section
     */
    public boolean checkPageLegal(String pageText, String section) {
        Assert.notNull(section, " section could not null ");
        Assert.notNull(pageText, " num could not null ");
        return pageText.matches(section);
    }

    /**
     * extract page infos method ; use strategy pattern to adapt different web page
     * structure
     * 
     * @param page
     * @param section
     * @param extractor
     * @return List<IpPoolMainDTO> the extract info stationary infos
     *         {@link IpPoolMainDTO}
     */
    public List<IpPoolMainDTO> getInfos(String page, String section, PageExtractor extractor) {
        List<IpPoolMainDTO> infoLists = new LinkedList<>();
        try {
            LOCK.lock();
            Elements elements = fetchElementWithSection(page, HAS_PAGE_REGIX);
            if (ObjectUtils.isNotEmpty(elements)) {
                infoLists.addAll(extractor.doExtractStrategy(page, elements));
            } else {
                LOG.info(" current elements is empty,will not work ,page : {} ", page);
            }
            return infoLists;
        } catch (Exception e) {
            LOG.error(" get IpPoolMainDTO infos error , message :{} ", e.getMessage());
            return infoLists;
        } finally {
            try {
                LOCK.unlock();
            } catch (Exception e) {
                LOG.info(" here is no lock ,cannot unlock,message :{}", e.getMessage());
            }
        }
    }

}
