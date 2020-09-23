package com.ipfetchservice.model.exceptions;

public class DebugException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -790238942356034071L;
    private int code;

    public DebugException(String message){
        super(message);
        this.code = -1;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
