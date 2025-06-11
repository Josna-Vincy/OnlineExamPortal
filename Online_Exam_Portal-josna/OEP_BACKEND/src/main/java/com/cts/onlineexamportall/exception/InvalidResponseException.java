package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidResponseException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(InvalidResponseException.class);
    private String msg;

    public InvalidResponseException() {
        super();
        logger.error("InvalidResponseException occurred");
    }

    public InvalidResponseException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("InvalidResponseException occurred with message: {}", msg);
    }
}