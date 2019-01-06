package com.revature.jdbcbank.models;

import java.io.Serializable;

public class Transaction implements Serializable {
	
	private static final long serialVersionUID = 4422013758173740686L;
	private int transactionId;
	private String transactionType;
	private double transactionAmount;
	private BankAccount account;
	
	public Transaction() {
		super();
	}

	public Transaction(int transactionId, String transactionType, double transactionAmount, BankAccount account) {
		super();
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.transactionAmount = transactionAmount;
		this.account = account;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BankAccount getAccount() {
		return account;
	}

	public void setAccount(BankAccount account) {
		this.account = account;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		long temp;
		temp = Double.doubleToLongBits(transactionAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + transactionId;
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
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
		Transaction other = (Transaction) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (Double.doubleToLongBits(transactionAmount) != Double.doubleToLongBits(other.transactionAmount))
			return false;
		if (transactionId != other.transactionId)
			return false;
		if (transactionType == null) {
			if (other.transactionType != null)
				return false;
		} else if (!transactionType.equals(other.transactionType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", transactionType=" + transactionType
				+ ", transactionAmount=" + transactionAmount + ", accountId=" + account.getAccountId() + ", accountName=" + account.getName() + "]";
	}
	
	
}
