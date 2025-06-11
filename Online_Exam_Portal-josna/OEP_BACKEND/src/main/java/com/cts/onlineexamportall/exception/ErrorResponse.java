package com.cts.onlineexamportall.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private static final Logger logger = LogManager.getLogger(ErrorResponse.class);

    private int statusCode;
    private String message;

    public ErrorResponse(String message) {
        super();
        this.message = message;
        logger.error("ErrorResponse created with message: {}", message);
    }
}