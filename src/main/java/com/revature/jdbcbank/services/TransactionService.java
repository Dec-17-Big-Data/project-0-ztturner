package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.jdbcbank.dao.TransactionDao;
import com.revature.jdbcbank.dao.TransactionOracle;
import com.revature.jdbcbank.models.Transaction;

public class TransactionService {
	private static TransactionService transactionService;
	final static TransactionDao transactionDao = TransactionOracle.getTranscationDao();
	private static final Logger logger = LogManager.getLogger(TransactionService.class);
	
	private TransactionService() {
		
	}
	
	/**
	 * Gets the transaction service.
	 * @return the transaction service
	 */
	public static TransactionService getTransactionService() {
		if(transactionService == null) {
			logger.info("Creating new transaction service");
			transactionService = new TransactionService();
		}
		
		return transactionService;
	}
	
	/**
	 * Gets all of the transactions in the database.
	 * @return all of the transactions in the database or empty if an error occurred
	 */
	public Optional<List<Transaction>> getAllTransactions() {
		logger.traceEntry("Getting all transactions");
		return logger.traceExit(transactionDao.getAllTransactions());
	}
	
	/**
	 * Gets all of the transactions associated with a given user in the database.
	 * @param userId - the given user ID
	 * @return all of the transactions associated with the user ID or empty if an error occurred
	 */
	public Optional<List<Transaction>> getAllTransactionsByUser(int userId) {
		logger.traceEntry("Getting all transactions by user with ID = {}", userId);
		return transactionDao.getAllTransactionsByUser(userId);
	}
}
