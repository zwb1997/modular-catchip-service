package com.zzz.service.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.util.*;

import com.zzz.model.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.zzz.model.exceptions.DebugException;

import static com.zzz.model.entitymodel.servicebase.constants.IpServiceConstant.*;

@Component
public class PageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtil.class);
    private static final String[] EMPTY_ARR = new String[] { "", "", "", "", "", "", "", "", "", "" };
    private static final int COMBINE_ARRAY_REQUIRE_LENGTH = 10;

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
     * validate httpEntity is empty
     * 
     * @param entity
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public String vaildateEntity(HttpEntity entity) throws IOException, ParseException {
        if (ObjectUtils.isEmpty(entity)) {
            LOG.info(" current response entity is empty ");
            return "";
        }
        String responseEntityString = EntityUtils.toString(entity, "utf-8");
        if (StringUtils.isBlank(responseEntityString)) {
            LOG.info(" current response entity is empty ");
        }
        return responseEntityString;
    }

    /**
     * match element by given regix
     *
     * @param html
     * @param sectionRegix
     * @return
     */
    public synchronized Elements fetchElementWithSection(String html, String sectionRegix) {
        Element element = Jsoup.parse(html);
        Elements elements = element.select(sectionRegix);
        return elements;
    }

    public synchronized List<IpPoolMainDTO> combineXiaoHuanInfo(Elements elements) {
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
                res.add(createInstance(infos));
            } catch (Exception e) {
                LOG.error(" error , message :{} ", e.getMessage());
            }
        }
        return res;
    }

    /**
     * list must be 10 elements; 
     * 固定格式的集合;
     * 0 -> ip ;
     * 1 -> port ;
     * 2 -> ip地址 ;
     * 3 -> ip供应商 ;
     * 4 -> 是否支持https ;
     * 5 -> 是否支持post请求 ;
     * 6 -> 匿名程度 ;
     * 7 -> 速度; 
     * 8 -> 网站检测 ip入库时间 ;
     * 9 -> 网站检测 ip最后有效时间;
     * @param infos
     * @return IpPoolMainDTO
     */
    private IpPoolMainDTO createInstance(List<String> infos) {
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
     * 替代所有空格 为 斜杠
     *
     * @param cure
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
     * check page name is match the section
     * 
     * @param num
     * @param section
     * @return could match the section
     */
    public boolean checkPageLegal(Object num, String section) {
        Assert.notNull(section, " section could not null ");
        return String.valueOf(num).matches(section);
    }
}
