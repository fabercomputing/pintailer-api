package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class SMSServiceException extends APIExceptions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8915124811221417111L;

	public SMSServiceException() {
		super();

	}

	public SMSServiceException(String message) {
		super(message);

	}

	public SMSServiceException(Throwable th) {
		super(th);

	}
}
