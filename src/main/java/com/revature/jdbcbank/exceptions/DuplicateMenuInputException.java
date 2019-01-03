package com.revature.jdbcbank.exceptions;

public class DuplicateMenuInputException extends RuntimeException{

	private static final long serialVersionUID = 5638000820998898350L;

	public DuplicateMenuInputException() {
		super();
	}

	public DuplicateMenuInputException(String message) {
		super(message);
	}

}
