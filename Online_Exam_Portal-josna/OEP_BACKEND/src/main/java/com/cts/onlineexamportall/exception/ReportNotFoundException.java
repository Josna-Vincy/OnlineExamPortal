package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportNotFoundException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ReportNotFoundException.class);
    private String msg;

    public ReportNotFoundException() {
        super();
        logger.error("ReportNotFoundException occurred");
    }

    public ReportNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
        logger.error("ReportNotFoundException occurred with message: {}", msg);
    }
}