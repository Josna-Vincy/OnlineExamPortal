package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExamExistsException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ExamExistsException.class);
    private String msg;

    public ExamExistsException() {
        super();
        logger.error("ExamExistsException occurred");
    }

    public ExamExistsException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("ExamExistsException occurred with message: {}", msg);
    }
}