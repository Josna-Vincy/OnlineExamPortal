package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PasswordMisMatchException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(PasswordMisMatchException.class);
    private String msg;

    public PasswordMisMatchException() {
        super();
        logger.error("PasswordMisMatchException occurred");
    }

    public PasswordMisMatchException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("PasswordMisMatchException occurred with message: {}", msg);
    }
}