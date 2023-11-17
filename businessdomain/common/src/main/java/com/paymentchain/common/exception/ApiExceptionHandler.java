package com.paymentchain.common.exception;

import java.net.UnknownHostException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(UnknownHostException.class)
	public ResponseEntity<StandarizedApiExceptionResponse> handleUnknownHostException(UnknownHostException ex) {
			StandarizedApiExceptionResponse response = new StandarizedApiExceptionResponse("Error de conexion","erorr-1024",ex.getMessage());
			return new ResponseEntity<StandarizedApiExceptionResponse>(response, HttpStatus.PARTIAL_CONTENT);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandarizedApiExceptionResponse> handleNoContentException(Exception ex) {
			StandarizedApiExceptionResponse response = new StandarizedApiExceptionResponse("Error de conexion","erorr-1024",ex.getMessage());
			return new ResponseEntity<StandarizedApiExceptionResponse>(response, HttpStatus.PARTIAL_CONTENT);
	}
}
