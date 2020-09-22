package com.zzz.model.basemodels.enummodel;

public enum ResponseCode {


    SUCCESS(1,"ok")
    ,ERROR(0,"err")
    ,TOO_LONG(-1,"TIME TOO LONG");


    ResponseCode(int code,String msg){
        this.responseCode = code;
        this.responseMessage = msg;
    }

    private int responseCode;
    private String responseMessage;
}
