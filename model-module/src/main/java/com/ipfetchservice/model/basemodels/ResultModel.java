package com.ipfetchservice.model.basemodels;

import com.ipfetchservice.model.basemodels.enummodel.ResponseCode;

public class ResultModel<T> {
    private T data;
    private ResponseCode responseCode;

    private ResultModel(T data,ResponseCode responseCode){
        this.data = data;
        this.responseCode = responseCode;
    }

    public static <T> ResultModel<T> buildResult(T data,ResponseCode responseCode){
        return new ResultModel<>(data,responseCode);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
