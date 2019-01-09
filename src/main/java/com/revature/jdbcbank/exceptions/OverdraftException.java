package com.revature.jdbcbank.exceptions;

public class OverdraftException extends RuntimeException {
	
	private static final long serialVersionUID = 9080984626109299120L;

	public OverdraftException() {
		super();
	}
	
	public OverdraftException(String message) {
		super(message);
	}
}
