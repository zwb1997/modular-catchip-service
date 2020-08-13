package com.zzz.entitymodel.servicebase.DTO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

public class IpPoolMainDTO {
    private int dataID;
    private int ip_num;
    private int ip_port;
    private String ip_location;
    private String ip_vendor;
    private int support_https;
    private int support_post;
    private int anonymity_degree;
    private BigDecimal access_speed;
    private Date insert_time;
    private Date last_detect_time;
    private String statusCode;

    public IpPoolMainDTO(int dataID, int ip_num, int ip_port, String ip_location, String ip_vendor, int support_https, int support_post, int anonymity_degree, BigDecimal access_speed, Date insert_time, Date last_detect_time, String statusCode) {
        this.dataID = dataID;
        this.ip_num = ip_num;
        this.ip_port = ip_port;
        this.ip_location = ip_location;
        this.ip_vendor = ip_vendor;
        this.support_https = support_https;
        this.support_post = support_post;
        this.anonymity_degree = anonymity_degree;
        this.access_speed = access_speed;
        this.insert_time = insert_time;
        this.last_detect_time = last_detect_time;
        this.statusCode = statusCode;
    }

    public int getDataID() {
        return dataID;
    }

    public int getIp_num() {
        return ip_num;
    }

    public int getIp_port() {
        return ip_port;
    }

    public String getIp_location() {
        return ip_location;
    }

    public String getIp_vendor() {
        return ip_vendor;
    }

    public int getSupport_https() {
        return support_https;
    }

    public int getSupport_post() {
        return support_post;
    }

    public int getAnonymity_degree() {
        return anonymity_degree;
    }

    public BigDecimal getAccess_speed() {
        return access_speed;
    }

    public Date getInsert_time() {
        return insert_time;
    }

    public Date getLast_detect_time() {
        return last_detect_time;
    }

    public String getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpPoolMainDTO that = (IpPoolMainDTO) o;
        return dataID == that.dataID &&
                ip_num == that.ip_num;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataID, ip_num);
    }
}
