package com.zzz.service.module.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class ValidParams {

    private final static Logger LOG = LoggerFactory.getLogger(ValidParams.class);

    /**
     * 校验对象参数是否包含Null(空字符串不算)
     * @param o
     * @return
     */
    public static boolean validFiles(Object o) {
        LOG.debug("参数校验开始..");
        Class clazz = o.getClass();
        Field[] fields =  clazz.getDeclaredFields();
        int i = 0;
        for(Field f: fields){
            f.setAccessible(true);
            try {
                String name = f.getName();
                Object tar = f.get(o);
//                if(BserviceBackParams.AID.equals(name) || BserviceBackParams.AID.equals(name)){
//                    if(tar == null){
//                        LOG.error("数据没有视频id{}",o);
//                        return false;
//                    }
//                }
                LOG.debug("参数名:{},参数值:{}",name,tar.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
