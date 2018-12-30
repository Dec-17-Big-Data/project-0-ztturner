package com.revature.jdbcbank.models;

import java.io.Serializable;

public class BankAccount implements Serializable {

	private static final long serialVersionUID = -5704455646558522705L;
	private int accountId;
	private String name; // identifier that the user gives
	private double balance;
	private int userId;
	
	
	
	public BankAccount() {
		super();
	}

	public BankAccount(int accountId, String name, double balance, int userId) {
		super();
		this.accountId = accountId;
		this.name = name;
		this.balance = balance;
		this.userId = userId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}	

	public int getUserId() {
		return userId;
	}

	public void setUser(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return name + " " + balance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accountId;
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + userId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankAccount other = (BankAccount) obj;
		if (accountId != other.accountId)
			return false;
		return true;
	}
	
	
}
