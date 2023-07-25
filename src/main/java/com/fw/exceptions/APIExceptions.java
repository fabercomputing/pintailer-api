package com.fw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class APIExceptions extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6028146387210823305L;


	public 	APIExceptions(){
		super();
	}
	
	public 	APIExceptions(String message){
		super(message);
	}
	
	
	public APIExceptions(Throwable th) {
		super(th);
	}

}

