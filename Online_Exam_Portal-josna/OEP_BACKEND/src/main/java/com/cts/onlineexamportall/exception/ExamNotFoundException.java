package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExamNotFoundException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ExamNotFoundException.class);
    private String msg;

    public ExamNotFoundException() {
        super();
        logger.error("ExamNotFoundException occurred");
    }

    public ExamNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("ExamNotFoundException occurred with message: {}", msg);
    }
}