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
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpLocation;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.page.extractservice.PageExtractor;
/**
 * list must be 10 elements; 固定格式的集合; 0 -> ip ; 1 -> port ; 2 -> ip地址 ; 3 ->
 * ip供应商 ; 4 -> 是否支持https ; 5 -> 是否支持post请求 ; 6 -> 匿名程度 ; 7 -> 速度; 8 ->
 * 网站检测ip入库时间 ; 9 -> 网站检测 ip最后有效时间; 自定义组装时,请注意一下 具体请看 
 * {@link com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO  IpPoolMainDTO}
 * {@link com.ipfetchservice.service.utils.page.extractservice.impl.XHPageExtractStrategy XHPageExtractStrategy}
 */
@Component
public class PageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtil.class);

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
       
        Elements elements = null;
        LOCK.lock();
        try{
            Element element = Jsoup.parse(html);
            elements = element.select(sectionRegix);
            LOCK.unlock();
        }catch(Exception e){
            LOG.error(" fetchElementWithSection error , message : ", e);
        }finally{
            LOCK.unlock();  
        }
        return elements;
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
            long assembleStartTime = System.currentTimeMillis();
            Elements elements = fetchElementWithSection(page, section);
            if (ObjectUtils.isNotEmpty(elements) && !elements.isEmpty()) {
                infoLists.addAll(extractor.doExtractStrategy(page, elements));
            } else {
                LOG.info(" current elements is empty,will not work ,page : {} ", page);
            }
            long assembleEndTime = System.currentTimeMillis();
            var usingTime = (assembleEndTime - assembleStartTime);
            LOG.info(" assemble dto model use time >> miniutes :{} seconds :{} ", usingTime / 60 / 1000,
                    usingTime / 1000);
            return infoLists;
        } catch (Exception e) {
            LOG.error(" get IpPoolMainDTO infos error , message :{} ", e.getMessage());
            return infoLists;
        } 
    }

}
