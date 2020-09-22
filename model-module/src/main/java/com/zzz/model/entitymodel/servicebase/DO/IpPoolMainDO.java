package com.zzz.model.entitymodel.servicebase.DO;

import java.util.Date;
import java.util.Objects;

public class IpPoolMainDO {
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

    public IpPoolMainDO(String dataID, String ipNum, int ipPort, String ipLocation, String ipVendor, int supportHttps, int supportPost, int anonymityDegree, String accessSpeed, String insertTime, String lastDetectTime, String statusCode, Date dataInsertTime, Date dataDetectTime) {
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

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }

    public String getIpNum() {
        return ipNum;
    }

    public void setIpNum(String ipNum) {
        this.ipNum = ipNum;
    }

    public int getIpPort() {
        return ipPort;
    }

    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    public String getIpLocation() {
        return ipLocation;
    }

    public void setIpLocation(String ipLocation) {
        this.ipLocation = ipLocation;
    }

    public String getIpVendor() {
        return ipVendor;
    }

    public void setIpVendor(String ipVendor) {
        this.ipVendor = ipVendor;
    }

    public int getSupportHttps() {
        return supportHttps;
    }

    public void setSupportHttps(int supportHttps) {
        this.supportHttps = supportHttps;
    }

    public int getSupportPost() {
        return supportPost;
    }

    public void setSupportPost(int supportPost) {
        this.supportPost = supportPost;
    }

    public int getAnonymityDegree() {
        return anonymityDegree;
    }

    public void setAnonymityDegree(int anonymityDegree) {
        this.anonymityDegree = anonymityDegree;
    }

    public String getAccessSpeed() {
        return accessSpeed;
    }

    public void setAccessSpeed(String accessSpeed) {
        this.accessSpeed = accessSpeed;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getLastDetectTime() {
        return lastDetectTime;
    }

    public void setLastDetectTime(String lastDetectTime) {
        this.lastDetectTime = lastDetectTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getDataInsertTime() {
        return dataInsertTime;
    }

    public void setDataInsertTime(Date dataInsertTime) {
        this.dataInsertTime = dataInsertTime;
    }

    public Date getDataDetectTime() {
        return dataDetectTime;
    }

    public void setDataDetectTime(Date dataDetectTime) {
        this.dataDetectTime = dataDetectTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpPoolMainDO that = (IpPoolMainDO) o;
        return ipNum == that.ipNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipNum);
    }

    @Override
    public String toString() {
        return "IpPoolMainDO{" +
                "dataID='" + dataID + '\'' +
                ", ipNum=" + ipNum +
                ", ipPort=" + ipPort +
                ", ipLocation='" + ipLocation + '\'' +
                ", ipVendor='" + ipVendor + '\'' +
                ", supportHttps=" + supportHttps +
                ", supportPost=" + supportPost +
                ", anonymityDegree=" + anonymityDegree +
                ", accessSpeed=" + accessSpeed +
                ", insertTime='" + insertTime + '\'' +
                ", lastDetectTime='" + lastDetectTime + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", dataInsertTime=" + dataInsertTime +
                ", dataDetectTime=" + dataDetectTime +
                '}';
    }
}
