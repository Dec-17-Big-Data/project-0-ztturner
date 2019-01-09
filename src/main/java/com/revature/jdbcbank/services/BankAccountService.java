package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.jdbcbank.dao.BankAccountDao;
import com.revature.jdbcbank.dao.BankAccountOracle;
import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.models.BankAccount;

public class BankAccountService {
	private static BankAccountService bankAccountService;
	final static BankAccountDao bankAccountDao = BankAccountOracle.getBankAccountDao();
	private static final Logger logger = LogManager.getLogger(BankAccountService.class);
	
	private BankAccountService() {
		
	}
	
	/**
	 * Gets the BankAccount service.
	 * @return the BankAccount service
	 */
	public static BankAccountService getBankAccountService() {
		if(bankAccountService == null) {
			logger.info("Creating new bank account service");
			bankAccountService = new BankAccountService();
		}
		
		return bankAccountService;
	}
	
	/**
	 * Gets all the bank accounts in the database.
	 * @return list of all the bank accounts in the database or empty if an error occurred
	 */
	public Optional<List<BankAccount>> getAllBankAccounts() {
		logger.traceEntry("Getting all bank accounts");
		return logger.traceExit(bankAccountDao.getAllBankAccounts());
	}
	
	/**
	 * Gets all the bank accounts associated with the given user ID.
	 * @param userId - the given user ID
	 * @return list of all the bank accounts associated with the given user or empty if an error occurred
	 */
	public Optional<List<BankAccount>> getAllBankAccountsByUser(int userId) {
		logger.traceEntry("Getting all bank accounts by user ID = {}", userId);
		return logger.traceExit(bankAccountDao.getAllBankAccountsByUser(userId));
	}
	
	/**
	 * Gets the bank account with the given bank account ID.
	 * @param bankAccountId - the given bank account ID
	 * @return the bank account with the given bank account ID or empty if not found
	 */
	public Optional<BankAccount> getBankAccountById(int bankAccountId) {
		logger.traceEntry("Getting bank account with ID = {}", bankAccountId);
		return logger.traceExit(bankAccountDao.getBankAccountById(bankAccountId));
	}
	
	/**
	 * Gets the bank account with the given name that is associated with the given user ID.
	 * @param name - the given account name
	 * @param userId - the given user ID
	 * @return the bank account with the given name and associated user ID or empty if not found
	 */
	public Optional<BankAccount> getBankAccountByName(String name, int userId) {
		logger.traceEntry("Getting bank account with name = {}", name);
		return logger.traceExit(bankAccountDao.getBankAccountByName(name, userId));
	}
	
	/**
	 * Attempts to create a bank account with a 
	 * given name, initial balance, and associated user ID.
	 * @param name - the given account name
	 * @param initialBalance - the given initial balance for the account
	 * @param userId - the associated user ID
	 * @return the account ID if the creation attempt was successful
	 * @throws ItemExistsException - thrown if an account with the given name and associated user ID is found
	 * @throws IllegalArgumentException - thrown if the account name is not within the character length constraint or has whitespace or the given initial balance is negative
	 */
	public int createBankAccount(String name, double initialBalance, int userId) throws ItemExistsException, IllegalArgumentException {		
		logger.traceEntry("Creating bank account with name = {}", name);
		
		Optional<BankAccount> accountWrapper = getBankAccountByName(name, userId); // check if an account with the name exists
		if(accountWrapper.isPresent()) {
			logger.info("Bank account with name = {} exists already", name);
			throw new ItemExistsException("Account with this name is associated with this user.");
		}
		
		// check if the name is within the required character length
		if(name.length() > 50 || name.length() == 0) {
			logger.info("Account name is not within the required character length");
			throw new IllegalArgumentException("Account name must be between 1 and 50 characters in length.");
		}
		
		Matcher whitespaceMatcher = Pattern.compile("\\s").matcher(name); // check if the given name has no spaces
		// check if the name has whitespace
		if(whitespaceMatcher.find()) {
			logger.info("Account name has whitespace");
			throw new IllegalArgumentException("Account name must not contain whitespace characters");
		}
		
		// check if the initial balance is a negative value
		if(initialBalance < 0) {
			logger.info("Initial balance is a negative value");
			throw new IllegalArgumentException("Initial balance amount must be non-negative.");
		}
		
		// round the input value to two decimal places
		initialBalance = Math.round(initialBalance * 100);
		initialBalance = initialBalance / 100;
		
		return logger.traceExit(bankAccountDao.createBankAccount(name, initialBalance, userId));
	}
	
	/**
	 * Attempts to delete the bank account with the given bank account ID.
	 * @param bankAccountId - the given bank account ID
	 * @return 1 if the attempt was successful, 0 if the attempt failed, -1 if there was an error
	 * @throws ItemNotFoundException - thrown when the bank account with the given bank account ID is not found
	 * @throws AccountNotEmptyException - thrown when the bank account with the given bank account ID has a balance greater than 0
	 */
	public int deleteBankAccount(int bankAccountId) throws ItemNotFoundException, AccountNotEmptyException {
		logger.traceEntry("Deleting bank account with ID = {}", bankAccountId);
		
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			logger.info("Bank account with ID = {} does not exist", bankAccountId);
			throw new ItemNotFoundException("Account not found.");
		}
		
		BankAccount account = accountWrapper.get();
		
		// check if the account is empty
		if(account.getBalance() > 0) {
			logger.info("Bank account with ID = {} is not empty", bankAccountId);
			throw new AccountNotEmptyException("Account must be emptied before it can be deleted.");
		}
		
		return logger.traceExit(bankAccountDao.deleteBankAccount(bankAccountId));
	}
	
	/**
	 * Attempts to make a deposit into the bank account
	 * with the given bank account ID.
	 * @param bankAccountId - the given bank account ID
	 * @param amount - the amount to deposit
	 * @return 1 if the deposit was successful, 0 if the deposit failed, -1 if an error occurred
	 * @throws ItemNotFoundException - thrown if the bank account with the given bank account ID is not found
	 * @throws IllegalArgumentException - thrown if the given amount to deposit is not positive
	 */
	public int makeDeposit(int bankAccountId, double amount) throws ItemNotFoundException, IllegalArgumentException {
		logger.traceEntry("Making deposit in bank account ID = {}", bankAccountId);
		
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		
		if(!accountWrapper.isPresent()) {
			logger.info("Bank account with ID = {} not found", bankAccountId);
			throw new ItemNotFoundException("Account not found.");
		}
		
		// check if the amount is non-positive
		if(amount <= 0) {
			logger.info("Amount to deposit was non-positive");
			throw new IllegalArgumentException("Deposit amount must be positive.");
		}
		
		// round the amount to two decimal places
		amount = Math.round(amount * 100);
		amount = amount / 100;
		
		return logger.traceExit(bankAccountDao.makeDeposit(bankAccountId, amount));
	}
	
	/**
	 * Attempts to make a withdrawal from the bank account 
	 * with the given bank account ID.
	 * @param bankAccountId - the given bank account ID
	 * @param amount - the amount to withdraw
	 * @return 1 if the withdrawal was successful, 0 if the withdrawal failed, -1 if an error occurred
	 * @throws ItemNotFoundException - thrown if the bank account with the given bank account ID is not found
	 * @throws IllegalArgumentException - thrown if the given amount to withdraw is not positive
	 * @throws OverdraftException - thrown if the given amount exceeds the balance in the bank account
	 */
	public int makeWithdrawal(int bankAccountId, double amount) throws ItemNotFoundException, IllegalArgumentException, OverdraftException {
		logger.traceEntry("Making withdrawal from bank account with ID = {}", bankAccountId);
		
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			logger.info("Bank account with ID = {} not found", bankAccountId);
			throw new ItemNotFoundException("Account not found.");
		}
		
		BankAccount account = accountWrapper.get();
		// check if the amount to withdraw is non-positive
		if(amount <= 0) {
			logger.info("Withdrawal amount was non-positive");
			throw new IllegalArgumentException("Withdrawal amount must be positive.");
		}
		
		// round the amount to withdraw to two decimal places
		amount = Math.round(amount * 100);
		amount = amount / 100;
		
		// check if the amount to withdraw is greater than the amount in the account
		if(account.getBalance() < amount) {
			logger.info("Withdrawal amount exceeded the balance of bank account with ID = {}");
			throw new OverdraftException("Insufficent funds to withdraw the given amount.");
		}
		
		return logger.traceExit(bankAccountDao.makeWithdrawal(bankAccountId, amount));
	}
}
