package com.cts.onlineexamportall.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter@Setter
public class Response<T> {
	private Boolean success;
	private HttpStatus status;
	private T data;
	private String errorMessage = "";
}


