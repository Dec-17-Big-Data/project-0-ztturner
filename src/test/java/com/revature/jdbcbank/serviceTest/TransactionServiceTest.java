package com.revature.jdbcbank.serviceTest;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.models.Transaction;
import com.revature.jdbcbank.services.TransactionService;

public class TransactionServiceTest {
	private static TransactionService transactionService;
	
	@BeforeClass
	public static void setup() {
		transactionService = TransactionService.getTransactionService();
	}
	
	@Test
	public void testGettingAllTransactions() { 
		Optional<List<Transaction>> transactions = transactionService.getAllTransactions();
		
		assertTrue(transactions.isPresent());
	}
	
	@Test
	public void testGettingTransactionsByUserValidId() {
		Optional<List<Transaction>> transactions = transactionService.getAllTransactionsByUser(1);
		
		assertTrue(transactions.isPresent());
		
		assertTrue(transactions.get().size() > 0);
	}
	
	@Test
	public void testGettingTransactionsByUserInvalidId() {
		Optional<List<Transaction>> transactions = transactionService.getAllTransactionsByUser(-1);
		
		assertTrue(transactions.isPresent());
		
		assertTrue(transactions.get().size() == 0);
	}
}
