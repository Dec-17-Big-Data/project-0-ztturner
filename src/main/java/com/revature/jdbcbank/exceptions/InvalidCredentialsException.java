package com.revature.jdbcbank.exceptions;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = -2933238204011812972L;

	public InvalidCredentialsException() {
		super();
	}
	
	public InvalidCredentialsException(String message) {
		super(message);
	}
}
