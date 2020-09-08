package com.zzz.utils;


import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.zzz.exceptions.DebugException;

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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.util.*;

import static com.zzz.entitymodel.servicebase.constants.IpServiceConstant.*;

@Service
public class PageUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtils.class);
    private static final String[] EMPTY_ARR = new String[]{"", "", "", "", "", "", "", "", "", ""};
    private static final int COMBINE_ARRAY_REQUIRE_LENGTH = 10;

    /**
     * @param html
     * @param selectParmas
     * @return TreeMap<Integer, Map < String, String>>
     * <p>
     * get page a tags text value and href value
     */
    public static TreeMap<Integer, IpLocation> matchAtages(int num,String html, String selectParmas) {
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
     * detect page if has next page
     *
     * @param curDocmentHtml
     * @return
     */
    public static boolean hasNextPage(String curDocmentHtml, String exceptSection) {
        Document doc = Jsoup.parse(curDocmentHtml);
        Elements bodyEs = doc.select(exceptSection);
        if (bodyEs.isEmpty()) {
            return false;
        }
        return true;
    }

    public static String vaildateEntity(HttpEntity entity) throws IOException, ParseException {
        if(ObjectUtils.isEmpty(entity)){
            LOG.info(" current response entity is empty ");
            return "";
        }
        String html = EntityUtils.toString(entity,"utf-8");
        return html;
    }

    /**
     * match element by given regix
     *
     * @param html
     * @param sectionRegix
     * @return
     */
    public static synchronized Elements fetchElementWithSection(String html, String sectionRegix) {
        Element element = Jsoup.parse(html);
        Elements elements = element.select(sectionRegix);
        return elements;
    }

    public static synchronized List<IpPoolMainDTO> combineXiaoHuanInfo(Elements elements) {
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

    private static IpPoolMainDTO createInstance(List<String> infos) {
        if (CollectionUtils.isEmpty(infos) || infos.size() < COMBINE_ARRAY_REQUIRE_LENGTH || infos.size() > COMBINE_ARRAY_REQUIRE_LENGTH) {
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
            model = new IpPoolMainDTO(
                    String.valueOf(infos.get(0))
                    , Integer.parseInt(infos.get(1))
                    , infos.get(2)
                    , infos.get(3)
                    , NOT_SUPPORT_CHINESE.equals(infos.get(4)) ? SUPPORT_NUM : NOT_SUPPORT_NUM
                    , NOT_SUPPORT_CHINESE.equals(infos.get(5)) ? SUPPORT_NUM : NOT_SUPPORT_NUM
                    , anonymityDegree
                    , infos.get(7)
                    , infos.get(8)
                    , infos.get(9)
            );
        } catch (Exception e) {
            LOG.error(" combine error,messgae : {} ", e.getMessage());
        }
        return model;
    }

    /**
     * 替代所有空格 为 斜杠
     *
     * @param cure
     * @return
     */
    public static List<String> findTextNode(Element cure) {
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

    public static boolean checkPageNumberLegal(Object num,String section){
        Assert.notNull(section, " section could not null ");
        return String.valueOf(num).matches(section);
    }
}
