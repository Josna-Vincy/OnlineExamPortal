package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserExistsException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(UserExistsException.class);
    private String msg;

    public UserExistsException() {
        super();
        logger.error("UserExistsException occurred");
    }

    public UserExistsException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("UserExistsException occurred with message: {}", msg);
    }
}