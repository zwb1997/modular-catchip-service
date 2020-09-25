package com.ipfetchservice.service.utils.page.extractservice;

import java.util.List;

import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import com.ipfetchservice.service.utils.PageUtil;

import org.jsoup.select.Elements;

@FunctionalInterface
public interface PageExtractStrategy {
    public static final PageUtil PAGE_UTIL = new PageUtil();
    public static final String[] EMPTY_ARR = new String[] { "", "", "", "", "", "", "", "", "", "" };
    List<IpPoolMainDTO> extractInfoWithPage(String page,Elements elements);
}
