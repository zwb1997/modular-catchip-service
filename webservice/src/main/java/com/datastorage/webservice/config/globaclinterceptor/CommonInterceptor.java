package com.datastorage.webservice.config.globaclinterceptor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.datastorage.models.basicalmodels.basicalconstants.IpServiceConstant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.TimeZones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CommonInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CommonInterceptor.class);


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LOG.info("1");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    /**
     * true or false to do or undo controller
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        /**
         * use utf-8
         */
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        printHeaderAndInfo(request, request.getHeaderNames());
        if (!validateRequestOrigin(request)) {
            return false;
        }
        if (!validateReuquestSign(request)) {
            LOG.error(" verification faild ");
            return false;
        }
        return true;
    }

    private boolean validateReuquestSign(HttpServletRequest request) {
        String curSecret = request.getHeader(IpServiceConstant.SECRET_SIGN);
        String curTimeSpan = request.getHeader(IpServiceConstant.CUR_TIME_SPAN);
        if (StringUtils.isBlank(curSecret) || StringUtils.isBlank(curTimeSpan) || !curTimeSpan.matches(IpServiceConstant.NUMBER_PATTERN)) {
            LOG.error(" header lost or format not match ");
            return false;
        }
        String requestTime = DateFormatUtils.format(Long.parseLong(curTimeSpan), IpServiceConstant.TIME_PATTERN_END_WITH_SECOND);
        LOG.info(" current request time : {} ", requestTime);
        String localSecret = DigestUtils.md5Hex(curTimeSpan + IpServiceConstant.SECRET).toLowerCase();
        if (!localSecret.equals(curSecret)) {
            return false;
        }
        return true;
    }

    private boolean validateRequestOrigin(HttpServletRequest request) {
        String originHeader = request.getHeader(IpServiceConstant.ORIGIN_NAME);
        if (StringUtils.isBlank(originHeader) || !IpServiceConstant.ORIGIN_VALUE.equals(originHeader)) {
            LOG.error(" request origin is not right ");
            return false;
        }
        return true;
    }

    private void printHeaderAndInfo(HttpServletRequest request, Enumeration<String> headerEnum) {
        if (ObjectUtils.isEmpty(headerEnum)) {
            LOG.error(" request header is empty ");
            return;
        }
        LOG.info(" === request infos === ");
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        int remotePort = request.getRemotePort();
        String requestUrl = request.getRequestURI();
        String requestSessionId = request.getRequestedSessionId();
        LOG.info("[ requestIp : {} , requestPort : {} , requestHost : {} ,requestSessionId : {},requestFullPath : {}",
                remoteAddr, remotePort, remoteHost, requestSessionId, requestUrl);
        LOG.info(" === request headers === ");
        while (headerEnum.hasMoreElements()) {
            String header = headerEnum.nextElement();
            String headerVal = request.getHeader(header);
            LOG.info(" header :{} ,val :{} ", header, headerVal);
        }
    }

}