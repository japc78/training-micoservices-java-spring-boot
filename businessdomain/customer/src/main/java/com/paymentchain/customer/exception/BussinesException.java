package com.paymentchain.customer.exception;

import org.springframework.http.HttpStatus;

public class BussinesException extends Exception {
	private static final long serialVersionUID = -3416737139235150944L;

	private long id;
	private String code;
	private HttpStatus httpStatus;

	public BussinesException(long id, String code, String message,HttpStatus httpStatus) {
			super(message);
			this.id = id;
			this.code = code;
			this.httpStatus = httpStatus;
	}

	public BussinesException(String code, String message,HttpStatus httpStatus) {
			super(message);
			this.code = code;
			this.httpStatus = httpStatus;
	}

	public BussinesException(String message, Throwable cause) {
			super(message, cause);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
