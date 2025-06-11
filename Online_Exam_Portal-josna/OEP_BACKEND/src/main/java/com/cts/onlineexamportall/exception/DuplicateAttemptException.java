package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DuplicateAttemptException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(DuplicateAttemptException.class);
    private String msg;

    public DuplicateAttemptException() {
        super();
        logger.error("DuplicateAttemptException occurred");
    }

    public DuplicateAttemptException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("DuplicateAttemptException occurred with message: {}", msg);
    }
}