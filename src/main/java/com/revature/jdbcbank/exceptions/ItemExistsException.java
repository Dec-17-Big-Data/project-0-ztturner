package com.revature.jdbcbank.exceptions;

public class ItemExistsException extends Exception{

	private static final long serialVersionUID = -1610115877063323512L;

	public ItemExistsException() {
		super();
	}

	public ItemExistsException(String message) {
		super(message);
	}
	
}
