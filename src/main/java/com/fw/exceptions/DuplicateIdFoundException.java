package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class DuplicateIdFoundException extends APIExceptions {

	private static final long serialVersionUID = 7514117731775011643L;

	public DuplicateIdFoundException() {
		super();
	}

	public DuplicateIdFoundException(String message) {
		super(message);
	}

	public DuplicateIdFoundException(Throwable th) {
		super(th);
	}
}
