package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuestionNotFoundException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(QuestionNotFoundException.class);
    private String msg;

    public QuestionNotFoundException() {
        super();
        logger.error("QuestionNotFoundException occurred");
    }

    public QuestionNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("QuestionNotFoundException occurred with message: {}", msg);
    }
}