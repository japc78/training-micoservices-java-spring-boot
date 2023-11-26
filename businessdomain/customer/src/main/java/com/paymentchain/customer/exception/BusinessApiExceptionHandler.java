package com.paymentchain.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.paymentchain.common.exception.ApiExceptionHandler;
import com.paymentchain.common.exception.StandarizedApiExceptionResponse;


@RestControllerAdvice
public class BusinessApiExceptionHandler extends ApiExceptionHandler {

	@ExceptionHandler(BussinesException.class)
	public ResponseEntity<StandarizedApiExceptionResponse> handleBussinesRuleException(BussinesException ex) {
		StandarizedApiExceptionResponse response = new StandarizedApiExceptionResponse("Error de validacion", ex.getCode(), ex.getMessage());
		return new ResponseEntity<StandarizedApiExceptionResponse>(response, HttpStatus.PARTIAL_CONTENT);
	}
}