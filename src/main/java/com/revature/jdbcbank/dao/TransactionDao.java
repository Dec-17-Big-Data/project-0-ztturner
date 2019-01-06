package com.revature.jdbcbank.dao;

import java.util.List;
import java.util.Optional;

import com.revature.jdbcbank.models.Transaction;

public interface TransactionDao {
	Optional<List<Transaction>> getAllTransactions();
	Optional<List<Transaction>> getAllTransactionsByUser(int userId);
}
