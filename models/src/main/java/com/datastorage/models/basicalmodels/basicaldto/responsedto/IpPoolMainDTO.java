package com.datastorage.models.basicalmodels.basicaldto.responsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IpPoolMainDTO {
    private static final Logger LOG = LoggerFactory.getLogger(IpPoolMainDTO.class);
    private String locationFirstLevel;
    private String locationSecondLevel;
    private String locationThirdLevel;

    @JsonProperty("in")
    private String ipNum;
    @JsonProperty("ip")
    private int ipPort;
    @JsonProperty("il")
    private String ipLocation;
    @JsonProperty("iv")
    private String ipVendor;
    @JsonProperty("sh")
    private byte supportHttps;
    @JsonProperty("sp")
    private byte supportPost;
    @JsonProperty("ad")
    private byte anonymityDegree;
    @JsonProperty("se")
    private String accessSpeed;
    @JsonProperty("it")
    private String insertTime;
    @JsonProperty("ldt")
    private String lastDetectTime;


    public String getLocationFirstLevel() {
        return locationFirstLevel;
    }

    public void setLocationFirstLevel(String locationFirstLevel) {
        this.locationFirstLevel = locationFirstLevel;
    }

    public String getLocationSecondLevel() {
        return locationSecondLevel;
    }

    public void setLocationSecondLevel(String locationSecondLevel) {
        this.locationSecondLevel = locationSecondLevel;
    }

    public String getLocationThirdLevel() {
        return locationThirdLevel;
    }

    public void setLocationThirdLevel(String locationThirdLevel) {
        this.locationThirdLevel = locationThirdLevel;
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

    public byte getSupportHttps() {
        return supportHttps;
    }

    public void setSupportHttps(byte supportHttps) {
        this.supportHttps = supportHttps;
    }

    public byte getSupportPost() {
        return supportPost;
    }

    public void setSupportPost(byte supportPost) {
        this.supportPost = supportPost;
    }

    public byte getAnonymityDegree() {
        return anonymityDegree;
    }

    public void setAnonymityDegree(byte anonymityDegree) {
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
}
