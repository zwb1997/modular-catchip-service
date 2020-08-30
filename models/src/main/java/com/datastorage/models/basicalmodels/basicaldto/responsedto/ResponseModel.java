package com.datastorage.models.basicalmodels.basicaldto.responsedto;

import com.datastorage.models.basicalenum.ResponseEnum;

public class ResponseModel<T> {
    private T data;
    private ResponseEnum responseEnum;

    private ResponseModel(T data, ResponseEnum responseEnum) {
        this.data = data;
        this.responseEnum = responseEnum;
    }

    public static <T> ResponseModel<T> buildResult(T data) {
        return new ResponseModel<>(data, ResponseEnum.SUCCESS);
    }

    public static ResponseModel buildResult() {
        return new ResponseModel<>(null, ResponseEnum.SUCCESS);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }
}
