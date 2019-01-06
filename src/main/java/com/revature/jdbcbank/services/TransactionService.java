package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;

import com.revature.jdbcbank.dao.TransactionDao;
import com.revature.jdbcbank.dao.TransactionOracle;
import com.revature.jdbcbank.models.Transaction;

public class TransactionService {
	private static TransactionService transactionService;
	final static TransactionDao transactionDao = TransactionOracle.getTranscationDao();
	
	private TransactionService() {
		
	}
	
	/**
	 * Gets the transaction service
	 * @return the transaction service
	 */
	public static TransactionService getTransactionService() {
		if(transactionService == null) {
			transactionService = new TransactionService();
		}
		
		return transactionService;
	}
	
	/**
	 * Gets all of the transactions in the database
	 * @return all of the transactions in the database or empty if an error occurred
	 */
	public Optional<List<Transaction>> getAllTransactions() {
		return transactionDao.getAllTransactions();
	}
	
	/**
	 * Gets all of the transactions associated with a given user in the database
	 * @param userId - the given user ID
	 * @return all of the transactions associated with the user ID or empty if an error occurred
	 */
	public Optional<List<Transaction>> getAllTransactionsByUser(int userId) {
		return transactionDao.getAllTransactionsByUser(userId);
	}
}
