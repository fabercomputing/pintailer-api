package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
public class UnAuthorizedActionException extends APIExceptions{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2988467182461812879L;

	public UnAuthorizedActionException() {
		super();
		
	}
	
	public UnAuthorizedActionException(String message) {
		super(message);
		
	}

	public UnAuthorizedActionException(Throwable th) {
		super(th);
		
	}
}
