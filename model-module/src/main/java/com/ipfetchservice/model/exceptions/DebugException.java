package com.ipfetchservice.model.exceptions;

public class DebugException extends RuntimeException {
    private String message;
    private int code;

    public DebugException(String message){
        super(message);
        this.message = message;
        this.code = -1;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
