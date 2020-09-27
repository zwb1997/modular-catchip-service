package com.ipfetchservice.service.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SignUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SignUtil.class);
    private static final Lock LOCK = new ReentrantLock();

    /**
     * create sign by timeSpan + localSecret
     *
     * @param timeSpan
     * @param localSecret
     * @return
     */
    public String createSign(String timeSpan, String localSecret) {
        LOCK.lock();
        String md5Hex = "";
        try {
            String fullMessage = timeSpan + localSecret;
            md5Hex = DigestUtils.md5Hex(fullMessage).toLowerCase();
            LOG.info("local md5 : {}", md5Hex);
            return md5Hex;
        } catch (Exception ex) {
            LOG.error(" create sign error , message :{} ", ex.getMessage());
        } finally {
            LOCK.unlock();
        }
        return md5Hex;
    }
}
