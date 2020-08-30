package com.datastorage.models.basicalexceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DebugException extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugException.class);
    public DebugException() {
        super();
    }

    public DebugException(String message) {
        super(message);
    }

    public DebugException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugException(Throwable cause) {
        super(cause);
    }
}
