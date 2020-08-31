package com.zzz.entitymodel.servicebase.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zzz.entitymodel.servicebase.DO.IpPoolMainDO;
import com.zzz.entitymodel.servicebase.constants.IpServiceConstant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.zzz.entitymodel.servicebase.constants.IpServiceConstant.*;

public class IpPoolMainDTO {
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

    private String locationFirstLevel;
    private String locationSecondLevel;
    private String locationThirdLevel;

    private static final Random RANDOM = new Random();

    public IpPoolMainDTO() {
    }

    public IpPoolMainDTO(String ipNum, int ipPort, String ipLocation, String ipVendor, byte supportHttps, byte supportPost, byte anonymityDegree, String accessSpeed, String insertTime, String lastDetectTime) {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof IpPoolMainDTO)) return false;

        IpPoolMainDTO that = (IpPoolMainDTO) o;

        return new EqualsBuilder()
                .append(ipNum, that.ipNum)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(ipNum)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "IpPoolMainDTO{" +
                "ipNum='" + ipNum + '\'' +
                ", ipPort=" + ipPort +
                ", ipLocation='" + ipLocation + '\'' +
                ", ipVendor='" + ipVendor + '\'' +
                ", supportHttps=" + supportHttps +
                ", supportPost=" + supportPost +
                ", anonymityDegree=" + anonymityDegree +
                ", accessSpeed='" + accessSpeed + '\'' +
                ", insertTime='" + insertTime + '\'' +
                ", lastDetectTime='" + lastDetectTime + '\'' +
                '}';
    }
}
