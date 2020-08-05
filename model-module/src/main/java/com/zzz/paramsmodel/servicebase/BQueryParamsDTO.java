package com.zzz.paramsmodel.servicebase;

public class BQueryParamsDTO {
    private String videoTypeName;
    private int videoTypeCode;
    private int bMid;
    private String bAuthor;

    public int getbMid() {
        return bMid;
    }

    public void setbMid(int bMid) {
        this.bMid = bMid;
    }

    public String getbAuthor() {
        return bAuthor;
    }

    public void setbAuthor(String bAuthor) {
        this.bAuthor = bAuthor;
    }

    public String getVideoTypeName() {
        return videoTypeName;
    }

    public void setVideoTypeName(String videoTypeName) {
        this.videoTypeName = videoTypeName;
    }

    public int getVideoTypeCode() {
        return videoTypeCode;
    }

    public void setVideoTypeCode(int videoTypeCode) {
        this.videoTypeCode = videoTypeCode;
    }
}
