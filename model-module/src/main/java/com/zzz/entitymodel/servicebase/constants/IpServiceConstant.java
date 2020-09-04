package com.zzz.entitymodel.servicebase.constants;

public class IpServiceConstant {
    //general
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36";
    public static final String XIAO_HUAN_POS_CHINA = "中国";
    public static final String XIAO_HUAN_POS_AMERICA = "美国";
    public static final String NOT_SUPPORT_CHINESE = "不支持";
    public static final String SUPPORT_CHINESE = "支持";
    public static final byte SUPPORT_NUM = 1;
    public static final byte NOT_SUPPORT_NUM = 0;
    public static final String ANONYMITY_UNKOWN = "未知";
    public static final String ANONYMITY_NORMAL = "普匿";
    public static final String ANONYMITY_HIGH = "高匿";
    public static final String ANONYMITY_MIX = "混淆";
    public static final String USE_CODE = "1";
    public static final String UNUSE_CODE = "0";
    public static final byte DEGREE_UNKNOW_PROXY = 0;
    public static final byte DEGREE_TRANSPARENT_PROXY = 1;
    public static final byte DEGREE_HIGHT_PROXY = 2;
    public static final byte DEGREE_MIX = 3;
    public static final String ORIGIN_NAME = "Origin";
    public static final String SECRET = "";
    public static final String STORAGE_SERVICE_PATH = "";
//    public static final String STORAGE_SERVICE_PATH = "";
    public static final String CUR_TIME_SPAN = "";
    public static final String SECRET_SIGN = "";
    public static final String HTTP_CONTENT_TYPE_JSON = "application/json";
    public static final String COMMON_DATE_FORMAT_REGIX = "yyyy-MM-dd HH:mm:ss";

    public static final String STORAGE_SERVICE_LOCATION="";
//    public static final String STORAGE_SERVICE_LOCATION = "http://127.0.0.1";
//    public static final int STORAGE_SERVICE_LOCATION_PORT = 8010;
    public static final int STORAGE_SERVICE_LOCATION_PORT= 0;

    //XIAO_HUAN
    public static final String XIAO_HUAN_TQDL = "//ip.ihuan.me/tqdl.html";
    public static final String XIAO_HUAN_ME = "https://ip.ihuan.me";
    public static final String XIAO_HUAN_TI = "https://ip.ihuan.me/ti.html";
    public static String HAS_PAGE_REGIX = "tbody > tr";
    public static String PAGE_NUM_REGIS = "a[href^=?page]";
    public static String PAGE_AREA_LIST_REGIX = "a[href~=^/address]";
    public static final String HC_LINK = "/Proxies.7z";
    public static final String HC_LINK_NAME = "花刺连接";
    public static final String XH_THREAD_NAME_PREFIEX = "HXTask";

    //nima
    public static final String NI_MA_IP = "http://www.nimadaili.com/";
    public static final String NI_MA_IP_NROMAL = "putong";
    public static final String NI_MA_IP_HIGH = "gaoni";
    public static final String NI_MA_IP_HTTP = "http";
    public static final String NI_MA_IP_HTTPS = "https";
}
