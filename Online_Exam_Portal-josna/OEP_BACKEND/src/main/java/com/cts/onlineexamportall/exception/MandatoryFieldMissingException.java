package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MandatoryFieldMissingException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(MandatoryFieldMissingException.class);
    private String msg;

    public MandatoryFieldMissingException() {
        super();
        logger.error("MandatoryFieldMissingException occurred");
    }

    public MandatoryFieldMissingException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("MandatoryFieldMissingException occurred with message: {}", msg);
    }
}