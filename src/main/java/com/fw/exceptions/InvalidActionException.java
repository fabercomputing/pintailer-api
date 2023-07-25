package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
public class InvalidActionException extends APIExceptions{


	private static final long serialVersionUID = 1790868175287218829L;

	public InvalidActionException() {
		super();
		
	}
	
	public InvalidActionException(String message) {
		super(message);
		
	}

	public InvalidActionException(Throwable th) {
		super(th);
		
	}
}
