package com.ipfetchservice.service.utils.page.extractservice;

import java.util.LinkedHashSet;
import com.ipfetchservice.model.entitymodel.servicebase.DTO.IpPoolMainDTO;
import org.jsoup.select.Elements;

public class PageExtractor {
    private PageExtractStrategy extractStrategy;
    public PageExtractor(PageExtractStrategy extractStrategy){
        this.extractStrategy = extractStrategy;
    }

    public LinkedHashSet<IpPoolMainDTO> doExtractStrategy(String page,Elements elements){
        return extractStrategy.extractInfoWithPage(page,elements);
    }
}
