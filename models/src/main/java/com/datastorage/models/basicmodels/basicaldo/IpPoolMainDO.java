package com.datastorage.models.basicmodels.basicaldo;

import java.util.Date;

public class IpPoolMainDO {
    private String dataID;
    private String ipNum;
    private short ipPort;
    private String ipLocation;
    private String ipVendor;
    private byte supportHttps;
    private byte supportPost;
    private byte anonymityDegree;
    private String accessSpeed;
    private String insertTime;
    private String lastDetectTime;
    private byte statusCode;
    private Date dataInsertTime;
    private Date dataDetectTime;


    public IpPoolMainDO(){

    }
    public IpPoolMainDO(
        String dataID,
        String ipNum,
        short ipPort,
        String ipLocation,
        String ipVendor,
        byte supportHttps,
        byte supportPost,
        byte anonymityDegree,
        String accessSpeed,
        String insertTime,
        String lastDetectTime,
        byte statusCode,
        Date dataInsertTime,
        Date dataDetectTime
    ){
        this.dataID = dataID;
        this.ipNum = ipNum;
        this.ipPort = ipPort;
        this.ipLocation = ipLocation;
        this.ipVendor = ipVendor;
        this.supportHttps = supportHttps;
        this.supportPost = supportPost;
        this.accessSpeed = accessSpeed;
        this.insertTime = insertTime;
        this.anonymityDegree = anonymityDegree;
        this.lastDetectTime = lastDetectTime;
        this.statusCode = statusCode;
        this.dataInsertTime = dataInsertTime;
        this.dataDetectTime = dataDetectTime;
    }

    public String getAccessSpeed() {
        return accessSpeed;
    }

    public byte getAnonymityDegree() {
        return anonymityDegree;
    }

    public Date getDataDetectTime() {
        return dataDetectTime;
    }

    public String getDataID() {
        return dataID;
    }

    public Date getDataInsertTime() {
        return dataInsertTime;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public String getIpLocation() {
        return ipLocation;
    }

    public String getIpNum() {
        return ipNum;
    }

    public short getIpPort() {
        return ipPort;
    }

    public String getIpVendor() {
        return ipVendor;
    }

    public String getLastDetectTime() {
        return lastDetectTime;
    }

    public byte getStatusCode() {
        return statusCode;
    }

    public byte getSupportHttps() {
        return supportHttps;
    }

    public byte getSupportPost() {
        return supportPost;
    }

    public void setAccessSpeed(String accessSpeed) {
        this.accessSpeed = accessSpeed;
    }

    public void setAnonymityDegree(byte anonymityDegree) {
        this.anonymityDegree = anonymityDegree;
    }

    public void setDataDetectTime(Date dataDetectTime) {
        this.dataDetectTime = dataDetectTime;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }

    public void setDataInsertTime(Date dataInsertTime) {
        this.dataInsertTime = dataInsertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public void setIpLocation(String ipLocation) {
        this.ipLocation = ipLocation;
    }

    public void setIpNum(String ipNum) {
        this.ipNum = ipNum;
    }

    public void setIpPort(short ipPort) {
        this.ipPort = ipPort;
    }

    public void setIpVendor(String ipVendor) {
        this.ipVendor = ipVendor;
    }

    public void setLastDetectTime(String lastDetectTime) {
        this.lastDetectTime = lastDetectTime;
    }

    public void setStatusCode(byte statusCode) {
        this.statusCode = statusCode;
    }

    public void setSupportHttps(byte supportHttps) {
        this.supportHttps = supportHttps;
    }

    public void setSupportPost(byte supportPost) {
        this.supportPost = supportPost;
    }
}