package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// @SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(UserNotFoundException.class);
    private String message;

    public UserNotFoundException() {
        super();
        logger.error("UserNotFoundException occurred");
    }

    public UserNotFoundException(String message) {
        super(message);
        this.message = message;
        logger.error("UserNotFoundException occurred with message: {}", message);
    }
}