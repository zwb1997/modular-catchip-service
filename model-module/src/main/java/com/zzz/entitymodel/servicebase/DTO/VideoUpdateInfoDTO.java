package com.zzz.entitymodel.servicebase.DTO;

public class VideoUpdateInfoDTO {
    private int baseInfoCount;
    private int videoInfoCount;

    public VideoUpdateInfoDTO() {

    }

    public VideoUpdateInfoDTO(int baseInfoCount, int videoInfoCount) {
        this.baseInfoCount = baseInfoCount;
        this.videoInfoCount = videoInfoCount;
    }

    public int getBaseInfoCount() {
        return baseInfoCount;
    }

    public void setBaseInfoCount(int baseInfoCount) {
        this.baseInfoCount = baseInfoCount;
    }

    public int getVideoInfoCount() {
        return videoInfoCount;
    }

    public void setVideoInfoCount(int videoInfoCount) {
        this.videoInfoCount = videoInfoCount;
    }
}
