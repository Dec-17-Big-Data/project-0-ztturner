package com.revature.jdbcbank.exceptions;

public class InputDoesNotExistInDisplayException extends RuntimeException{

	private static final long serialVersionUID = -6691178107548175475L;

	public InputDoesNotExistInDisplayException() {
		super();
	}

	public InputDoesNotExistInDisplayException(String message) {
		super(message);
	}

}
