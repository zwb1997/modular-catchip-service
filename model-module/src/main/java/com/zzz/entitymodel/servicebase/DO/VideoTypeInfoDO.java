package com.zzz.entitymodel.servicebase.DO;

public class VideoTypeInfoDO {
    private int dataID;
    private String videoTypeCode;
    private String videoTypeName;
    private int statusCode;

    public VideoTypeInfoDO(String videoTypeCode, String videoTypeName, int statusCode) {
        this.videoTypeCode = videoTypeCode;
        this.videoTypeName = videoTypeName;
        this.statusCode = statusCode;
    }

    public VideoTypeInfoDO() {
    }



    public int getDataID() {
        return dataID;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }

    public String getVideoTypeCode() {
        return videoTypeCode;
    }

    public void setVideoTypeCode(String videoTypeCode) {
        this.videoTypeCode = videoTypeCode;
    }

    public String getVideoTypeName() {
        return videoTypeName;
    }

    public void setVideoTypeName(String videoTypeName) {
        this.videoTypeName = videoTypeName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    @Override
    public String toString() {
        return "VideoInfoModel{" +
                "dataID=" + dataID +
                ", videoTypeCode=" + videoTypeCode +
                ", videoTypeName='" + videoTypeName + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
