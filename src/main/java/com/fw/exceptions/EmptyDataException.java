package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_ACCEPTABLE)
public class EmptyDataException extends APIExceptions {

	private static final long serialVersionUID = 7312612103210114069L;
	
	public EmptyDataException() {
		super();
	}

	public EmptyDataException(String message) {
		super(message);
	}

	public EmptyDataException(Throwable th) {
		super(th);
	}
}
