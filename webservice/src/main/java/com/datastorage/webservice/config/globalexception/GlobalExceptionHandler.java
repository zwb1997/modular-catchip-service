package com.datastorage.webservice.config.globalexception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler implements HandlerExceptionResolver {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        LOG.error(" occure exception ");
        String requestIp = httpServletRequest.getRemoteAddr();
        String sessionId = httpServletRequest.getRequestedSessionId();
        String requestURI = httpServletRequest.getRequestURI();
        String requestHost = httpServletRequest.getRemoteHost();
        int requestPort = httpServletRequest.getRemotePort();
        LOG.error(" requestURI :{} ,requestIp:{},sessionId:{},requestHost:{},requestPort:{}"
                ,requestURI
                ,requestIp
                ,sessionId
                ,requestHost
                ,requestPort);
        ModelAndView mv = new ModelAndView();
        MappingJackson2JsonView jackson2JsonView = new MappingJackson2JsonView();
        Map<String, Object> maps = new HashMap<>();
        maps.put("error","0");
        maps.put("message",e.getMessage());
        jackson2JsonView.setAttributesMap(maps);
        mv.setView(jackson2JsonView);
        return mv;
    }
}
