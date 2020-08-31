package com.zzz.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SignUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SignUtil.class);


    /**
     * create sign by timeSpan + localSecret
     * @param timeSpan
     * @param localSecret
     * @return
     */
    public static String createSign(String timeSpan,String localSecret){
        String fullMessage = timeSpan + localSecret;
        String md5Hex = DigestUtils.md5Hex(fullMessage).toLowerCase();
        return md5Hex;
    }
}
