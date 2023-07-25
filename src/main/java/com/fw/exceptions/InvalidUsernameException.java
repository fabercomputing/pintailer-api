package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
public class InvalidUsernameException extends APIExceptions{

	    private static final long serialVersionUID = 4054034655638486281L;

		public InvalidUsernameException() {
			super();
			
		}
		
		public InvalidUsernameException(String message) {
			super(message);
			
		}

		public InvalidUsernameException(Throwable th) {
			super(th);
			
		}
}
