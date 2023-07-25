package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class InvalidIdException extends APIExceptions{

	private static final long serialVersionUID = 5388612103210114069L;

	public InvalidIdException() {
		super();
		
	}
	
	public InvalidIdException(String message) {
		super(message);
		
	}

	public InvalidIdException(Throwable th) {
		super(th);
		
	}
}
