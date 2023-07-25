package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_ACCEPTABLE)
public class ImportDuplicateDataException extends APIExceptions {

	private static final long serialVersionUID = 7312612103210114069L;
	
	public ImportDuplicateDataException() {
		super();
	}

	public ImportDuplicateDataException(String message) {
		super(message);
	}

	public ImportDuplicateDataException(Throwable th) {
		super(th);
	}
}
