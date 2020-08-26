package com.zzz.entitymodel.servicebase.DTO;

import com.zzz.entitymodel.servicebase.DO.IpPoolMainDO;
import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;

import java.math.BigDecimal;
import java.util.*;

public class IpPoolMainDTO {
    private String dataID;
    private String ipNum;
    private int ipPort;
    private String ipLocation;
    private String ipVendor;
    private int supportHttps;
    private int supportPost;
    private int anonymityDegree;
    private String accessSpeed;
    private String insertTime;
    private String lastDetectTime;
    private String statusCode;
    private Date dataInsertTime;
    private Date dataDetectTime;

    private String locationFirstLevel;
    private String locationSecondLevel;
    private String locationThirdLevel;

    private static final Random RANDOM = new Random();

    public IpPoolMainDTO(String dataID, String ipNum, int ipPort, String ipLocation, String ipVendor, int supportHttps, int supportPost, int anonymityDegree, String accessSpeed, String insertTime, String lastDetectTime, String statusCode, Date dataInsertTime, Date dataDetectTime) {
        this.dataID = dataID;
        this.ipNum = ipNum;
        this.ipPort = ipPort;
        this.ipLocation = ipLocation;
        this.ipVendor = ipVendor;
        this.supportHttps = supportHttps;
        this.supportPost = supportPost;
        this.anonymityDegree = anonymityDegree;
        this.accessSpeed = accessSpeed;
        this.insertTime = insertTime;
        this.lastDetectTime = lastDetectTime;
        this.statusCode = statusCode;
        this.dataInsertTime = dataInsertTime;
        this.dataDetectTime = dataDetectTime;
    }

    public String getDataID() {
        return dataID;
    }

    public String getIpNum() {
        return ipNum;
    }

    public int getIpPort() {
        return ipPort;
    }

    public String getIpLocation() {
        return ipLocation;
    }

    public String getIpVendor() {
        return ipVendor;
    }

    public int getSupportHttps() {
        return supportHttps;
    }

    public int getSupportPost() {
        return supportPost;
    }

    public int getAnonymityDegree() {
        return anonymityDegree;
    }

    public String getAccessSpeed() {
        return accessSpeed;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public String getLastDetectTime() {
        return lastDetectTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Date getDataInsertTime() {
        return dataInsertTime;
    }

    public Date getDataDetectTime() {
        return dataDetectTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        IpPoolMainDTO that = (IpPoolMainDTO) o;
        return ipNum == that.ipNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipNum);
    }

    /**
     * 固定格式的集合
     * 0 -> ip
     * 1 -> port
     * 2 -> ip地址
     * 3 -> ip供应商
     * 4 -> 是否支持https
     * 5 -> 是否支持post请求
     * 6 -> 匿名程度
     * 7 -> 速度
     * 8 -> 网站检测 ip入库时间
     * 9 -> 网站检测 ip最后有效时间
     * @param params
     * @return
     */
    public static IpPoolMainDO createInstance(List<String> params) {

        String dataID = UUID.randomUUID().toString().replace("-","").substring(0,16);
        String ipNum = params.get(0);
        int ipPort = Integer.parseInt(params.get(1));
        String ipLocation = params.get(2);
        String ipVendor = params.get(3);
        int supportHttps = IpServiceConstant.SUPPORT_CHINESE.equals(params.get(4)) ? IpServiceConstant.SUPPORT_NUM : IpServiceConstant.NOT_SUPPORT_NUM;
        int supportPost = IpServiceConstant.SUPPORT_CHINESE.equals(params.get(5)) ? IpServiceConstant.SUPPORT_NUM : IpServiceConstant.NOT_SUPPORT_NUM;
        String anonymityDegreeVal = params.get(6) ;
        int anonymityDegree;
        switch (anonymityDegreeVal){
            case IpServiceConstant.ANONYMITY_HIGH:
                anonymityDegree = IpServiceConstant.DEGREE_HIGHT_PROXY;
                break;
            case IpServiceConstant.ANONYMITY_NORMAL:
                anonymityDegree = IpServiceConstant.DEGREE_TRANSPARENT_PROXY;
                break;
            default:
                anonymityDegree = IpServiceConstant.DEGREE_TRANSPARENT;
                break;
        }
        String accessSpeed = params.get(7);
        String insertTime = params.get(8);
        String lastDetectTime = params.get(9);
        String statusCode = IpServiceConstant.USE_CODE;
        Date dataInsertTime = new Date();
        Date dataDetectTime = null;
        return new IpPoolMainDO(
                dataID,
                ipNum,
                ipPort,
                ipLocation,
                ipVendor,
                supportHttps,
                supportPost,
                anonymityDegree,
                accessSpeed,
                insertTime,
                lastDetectTime,
                statusCode,
                dataInsertTime,
                dataDetectTime
        );
    }
}
