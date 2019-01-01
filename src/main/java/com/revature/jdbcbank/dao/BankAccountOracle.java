package com.revature.jdbcbank.dao;

import java.sql.CallableStatement;
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
import com.revature.jdbcbank.util.ConnectionUtil;

public class BankAccountOracle implements BankAccountDao {
	private static BankAccountOracle bankAccountOracle;
	private static final Logger logger = LogManager.getLogger(BankAccountOracle.class);

	private BankAccountOracle() {

	}
	
	public static BankAccountOracle getBankAccountDao() {
		if(bankAccountOracle == null) {
			bankAccountOracle = new BankAccountOracle();
		}
		
		return bankAccountOracle;
	}
	
	@Override
	public Optional<List<BankAccount>> getAllBankAccounts() {
		logger.traceEntry("Getting all bank accounts.");
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_accounts;";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<BankAccount> accounts = new ArrayList<BankAccount>();
			
			while(rs.next()) {
				accounts.add(new BankAccount(rs.getInt("bank_account_id"),rs.getString("name"),
						rs.getDouble("balance"), rs.getInt("user_id")));
			}
			
			return logger.traceExit(Optional.of(accounts));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while getting all accounts", e);
		}
		
		return logger.traceExit(Optional.empty());
	}

	@Override
	public Optional<List<BankAccount>> getAllBankAccountsByUser(int userId) {
		logger.traceEntry("Getting bank accounts with userId = {}", userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_accounts where user_id = ?;";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			List<BankAccount> userAccounts = new ArrayList<BankAccount>();
			
			while(rs.next()) {
				userAccounts.add(new BankAccount(rs.getInt("bank_account_id"),rs.getString("name"),
						rs.getDouble("balance"), rs.getInt("user_id")));
			}
			
			return logger.traceExit(Optional.of(userAccounts));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while getting all accounts by user", e);
		}
		
		return logger.traceExit(Optional.empty());
	}

	@Override
	public Optional<BankAccount> getBankAccountById(int bankAccountId) {
		logger.traceEntry("Getting bank account with accountId = {}", bankAccountId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_accounts where bank_account_id = ?;";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, bankAccountId);
			ResultSet rs = ps.executeQuery();
			BankAccount account = null;
			
			while(rs.next()) {
				account = new BankAccount(rs.getInt("bank_account_id"),rs.getString("name"),
						rs.getDouble("balance"), rs.getInt("user_id"));
			}
			
			if(account == null) {
				return logger.traceExit(Optional.empty());
			}
			
			return logger.traceExit(Optional.of(account));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while getting account by id", e);
		}
		
		return logger.traceExit(Optional.empty());
	}

	@Override
	public Optional<BankAccount> getBankAccountByName(String name, int userId) {
		logger.traceEntry("Getting account with name = {} from user = {}", name, userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_accounts where name = ? and user_id = ?;";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, name);
			ps.setInt(2, userId);
			ResultSet rs = ps.executeQuery();
			BankAccount account = null;
			
			while(rs.next()) {
				account = new BankAccount(rs.getInt("bank_account_id"), rs.getString("name"),
						rs.getDouble("balance"), rs.getInt("user_id"));
			}
			
			if(account == null) {
				return logger.traceExit(Optional.empty());
			}
			
			return logger.traceExit(Optional.of(account));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception errored while getting account by name", e);
		}
		
		return logger.traceExit(Optional.empty());
	}

	@Override
	public int createBankAccount(String name, double initialBalance, int userId) {
		logger.traceEntry("Creating account with name = {} for user = {}", name, userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int newAccountId = -1;
			
			CallableStatement cs = con.prepareCall("call createBankAccount(?, ?, ?, ?);");
			cs.setString(1, name);
			cs.setDouble(2, initialBalance);
			cs.setInt(3, userId);
			cs.registerOutParameter(4, java.sql.Types.INTEGER);
			cs.execute();
			
			newAccountId = cs.getInt(4);
			
			return logger.traceExit(newAccountId);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while creating account", e);
		}
		
		return logger.traceExit(-1);
	}

	@Override
	public int deleteBankAccount(int bankAccountId) {
		logger.traceEntry("Deleting account with id = {}", bankAccountId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int success = -1;
			
			CallableStatement cs = con.prepareCall("call deleteBankAccount(?, ?);");
			cs.setInt(1, bankAccountId);
			cs.registerOutParameter(2, java.sql.Types.INTEGER);
			cs.execute();
			
			success = cs.getInt(2);
			
			return logger.traceExit(success);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL Exception occurred while deleting account", e);
		}
		return logger.traceExit(-1);
	}

	@Override
	public int makeDeposit(int bankAccountId, double amount) {
		logger.traceEntry("Making deposit to account with id = {}", bankAccountId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int success = -1;
			
			CallableStatement cs = con.prepareCall("call makeDeposit(?, ?, ?);");
			cs.setInt(1, bankAccountId);
			cs.setDouble(2, amount);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);
			cs.execute();
			
			success = cs.getInt(3);
			
			return logger.traceExit(success);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while making a deposit", e);
		}
		return logger.traceExit(-1);
	}

	@Override
	public int makeWithdrawal(int bankAccountId, double amount) {
		logger.traceEntry("Making withdrawal from account with id = {}", bankAccountId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int success = -1;
			
			CallableStatement cs = con.prepareCall("call makeWithdrawal(?, ?, ?);");
			cs.setInt(1, bankAccountId);
			cs.setDouble(2, amount);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);
			cs.execute();
			
			success = cs.getInt(3);
			
			return logger.traceExit(success);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while making a withdrawal", e);
		}
		
		return logger.traceExit(-1);
	}

}
