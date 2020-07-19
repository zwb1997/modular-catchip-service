package com.zzz.entitymodel.servicebase.DO;

import com.zzz.entitymodel.servicebase.DTO.QueryCountsDTO;

public class VideoInfoModelDO {
    private String dataID;
    private int comment;
    private int typeId;
    private int play;
    private String pic;
    private String subtitle;
    private String description;
    private String copyright;
    private String title;
    private int review;
    private String author;
    private int mid;
    private String created;
    private String length;
    private int videoReview;
    private int aid;
    private String bvid;
    private int hideClick;
    private int isPay;
    private int isUnionVideo;
    private int statusCode;

    public VideoInfoModelDO(String dataID
            , int comment
            , int typeId
            , int play
            , String pic
            , String subtitle
            , String description
            , String copyright
            , String title
            , int review
            , String author
            , int mid
            , String created
            , String length
            , int videoReview
            , int aid, String bvid, int hideClick, int isPay, int isUnionVideo, int statusCode) {
        this.dataID = dataID;
        this.comment = comment;
        this.typeId = typeId;
        this.play = play;
        this.pic = pic;
        this.subtitle = subtitle;
        this.description = description;
        this.copyright = copyright;
        this.title = title;
        this.review = review;
        this.author = author;
        this.mid = mid;
        this.created = created;
        this.length = length;
        this.videoReview = videoReview;
        this.aid = aid;
        this.bvid = bvid;
        this.hideClick = hideClick;
        this.isPay = isPay;
        this.isUnionVideo = isUnionVideo;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "VideoInfoModelDO{" +
                "dataID='" + dataID + '\'' +
                ", comment=" + comment +
                ", typeId=" + typeId +
                ", play=" + play +
                ", pic='" + pic + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", description='" + description + '\'' +
                ", copyright='" + copyright + '\'' +
                ", title='" + title + '\'' +
                ", review=" + review +
                ", author='" + author + '\'' +
                ", mid=" + mid +
                ", created='" + created + '\'' +
                ", length='" + length + '\'' +
                ", videoReview=" + videoReview +
                ", aid=" + aid +
                ", bvid='" + bvid + '\'' +
                ", hideClick=" + hideClick +
                ", isPay=" + isPay +
                ", isUnionVideo=" + isUnionVideo +
                ", statusCode=" + statusCode +
                '}';
    }

    public String getDataID() {
        return dataID;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getVideoReview() {
        return videoReview;
    }

    public void setVideoReview(int videoReview) {
        this.videoReview = videoReview;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public int getHideClick() {
        return hideClick;
    }

    public void setHideClick(int hideClick) {
        this.hideClick = hideClick;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public int getIsUnionVideo() {
        return isUnionVideo;
    }

    public void setIsUnionVideo(int isUnionVideo) {
        this.isUnionVideo = isUnionVideo;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    /**
     * 通过视频aid判断相等
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof VideoInfoModelDO) {
            return this.aid == ((VideoInfoModelDO) o).getAid();
        } else if (o instanceof QueryCountsDTO) {
            return this.aid == ((QueryCountsDTO) o).getAid();
        }else{
            return false;
        }
    }
}
