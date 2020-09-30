package com.datastorage.models.basicalenum;

public enum ResponseEnum {
     SUCCESS(1,"ok")
    ,ERROR(0,"err")
    ,TIME_TOO_LONG(-1,"TIME TOO LONG");


    ResponseEnum(int code,String msg){
        this.responseCode = code;
        this.responseMessage = msg;
    }

    private int responseCode;
    private String responseMessage;


    public int getResponseCode() {
        return responseCode;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
}
