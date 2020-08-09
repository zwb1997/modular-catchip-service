package com.zzz.service.module.utils;

import com.sun.xml.internal.org.jvnet.mimepull.DecodingException;
import com.zzz.entitymodel.servicebase.DTO.IpLocation;
import com.zzz.service.module.common.exception.DebugException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;

@Service
public class PageUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtils.class);


    /**
     * @param html
     * @param selectParmas
     * @return TreeMap<Integer, Map < String, String>>
     * <p>
     * get page a tags text value and href value
     */
    public static TreeMap<Integer, IpLocation> matchAtages(String html, String selectParmas) {
        TreeMap<Integer, IpLocation> res = new TreeMap<>();
        if (StringUtils.isBlank(html)) {
            LOG.info(" current html is null ");
            return res;
        }

        Document doc = Jsoup.parse(html);
        Elements es = StringUtils.isBlank(selectParmas) ? doc.select("a[href]") : doc.select(selectParmas);
        int number = 1;
        for (Element e : es) {
            String tagText = e.text();
            String tagHrefArrt = e.attr("href");
            res.put(number++, new IpLocation(tagText,tagHrefArrt));
        }
        return res;
    }

    /**
     * detect page if has next page
     *
     * @param curDocmentHtml
     * @return
     */
    public static boolean hasNextPage(String curDocmentHtml,String exceptSection)  {
        Document doc = Jsoup.parse(curDocmentHtml);
        Elements bodyEs = doc.select(exceptSection);
        if(bodyEs.isEmpty()){
            return false;
        }
        return true;
    }

    public static String vaildateEntity(HttpEntity entity)  throws IOException, ParseException{
        if (ObjectUtils.isEmpty(entity)) {
            throw new DebugException(" current http enity is null ");
        }
        String html = EntityUtils.toString(entity);
        if(StringUtils.isBlank(html)){
            throw new DecodingException(" current http entity's html is null ");
        }
        return html;
    }
}
