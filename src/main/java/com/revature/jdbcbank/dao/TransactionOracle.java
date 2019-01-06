package com.revature.jdbcbank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.jdbcbank.models.BankAccount;
import com.revature.jdbcbank.models.Transaction;
import com.revature.jdbcbank.util.ConnectionUtil;

public class TransactionOracle implements TransactionDao {
	private static TransactionOracle transactionOracle;
	private static final Logger logger = LogManager.getLogger(TransactionOracle.class);
	
	private TransactionOracle() {
		
	}
	
	public static TransactionOracle getTranscationDao() {
		if(transactionOracle == null) {
			transactionOracle = new TransactionOracle();
		}
		
		return transactionOracle;
	}

	@Override
	public Optional<List<Transaction>> getAllTransactions() {
		logger.traceEntry("Getting all transactions.");
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select t.transaction_id, t.transaction_type, t.transaction_amount, a.bank_account_id, a.account_name, a.balance, a.user_id " + 
		                 "from bank_transactions t inner join bank_accounts a on t.bank_account_id = a.bank_account_id order by transaction_id";			
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<Transaction> transactions = new ArrayList<Transaction>();
			
			while(rs.next()) {
				transactions.add(new Transaction(rs.getInt("transaction_id"),
												 rs.getString("transaction_type"), 
												 rs.getDouble("transaction_amount"),
												 new BankAccount(rs.getInt("bank_account_id"),
														 		 rs.getString("account_name"),
														 		 rs.getDouble("balance"),
														 		 rs.getInt("user_id"))));
			}
			
			return logger.traceExit(Optional.of(transactions));
		}
		catch(SQLException e) {
			logger.catching(e);
			logger.error("SQL Exception occurred while getting all transactions", e);
		}
		
		return logger.traceExit(Optional.empty());
	}

	@Override
	public Optional<List<Transaction>> getAllTransactionsByUser(int userId) {
		logger.traceEntry("Getting all transactions by userId = {}.", userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select t.transaction_id, t.transaction_type, t.transaction_amount, a.bank_account_id, a.account_name, a.balance, a.user_id " + 
		                 "from bank_transactions t inner join bank_accounts a on t.bank_account_id = a.bank_account_id where user_id = ? order by transaction_id";			
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			List<Transaction> transactions = new ArrayList<Transaction>();
			
			while(rs.next()) {
				transactions.add(new Transaction(rs.getInt("transaction_id"),
												 rs.getString("transaction_type"), 
												 rs.getDouble("transaction_amount"),
												 new BankAccount(rs.getInt("bank_account_id"),
														 		 rs.getString("account_name"),
														 		 rs.getDouble("balance"),
														 		 rs.getInt("user_id"))));
			}
			
			return logger.traceExit(Optional.of(transactions));
		}
		catch(SQLException e) {
			logger.catching(e);
			logger.error("SQL Exception occurred while getting all transactions by user", e);
		}
		
		return logger.traceExit(Optional.empty());
	}
}
