package com.revature.jdbcbank.exceptions;

public class AccountNotEmptyException extends RuntimeException{

	private static final long serialVersionUID = 506627308507470379L;

	public AccountNotEmptyException() {
		super();
	}
	
	public AccountNotEmptyException(String message) {
		super(message);
	}
}
