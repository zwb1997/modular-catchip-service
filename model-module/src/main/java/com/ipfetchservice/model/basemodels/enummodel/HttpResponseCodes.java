package com.ipfetchservice.model.basemodels.enummodel;

public enum HttpResponseCodes {

    SUCCESS("200");

    private HttpResponseCodes(String code){
        this.code = code;
    }
    private String code;

    public String StringtCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
