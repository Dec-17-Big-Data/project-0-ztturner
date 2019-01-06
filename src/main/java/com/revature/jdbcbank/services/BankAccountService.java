package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.revature.jdbcbank.dao.BankAccountDao;
import com.revature.jdbcbank.dao.BankAccountOracle;
import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.models.BankAccount;

public class BankAccountService {
	private static BankAccountService bankAccountService;
	final static BankAccountDao bankAccountDao = BankAccountOracle.getBankAccountDao();
	
	private BankAccountService() {
		
	}
	
	/**
	 * Gets the BankAccount service.
	 * @return the BankAccount service
	 */
	public static BankAccountService getBankAccountService() {
		if(bankAccountService == null) {
			bankAccountService = new BankAccountService();
		}
		
		return bankAccountService;
	}
	
	/**
	 * Gets all the bank accounts in the database.
	 * @return list of all the bank accounts in the database or empty if an error occurred
	 */
	public Optional<List<BankAccount>> getAllBankAccounts() {
		return bankAccountDao.getAllBankAccounts();
	}
	
	/**
	 * Gets all the bank accounts associated with given user ID
	 * @param userId - the given user ID
	 * @return list of all the bank accounts associated with the given user or empty if an error occurred
	 */
	public Optional<List<BankAccount>> getAllBankAccountsByUser(int userId) {
		return bankAccountDao.getAllBankAccountsByUser(userId);
	}
	
	/**
	 * Gets the bank account with the given bank account ID
	 * @param bankAccountId - the given bank account ID
	 * @return the bank account with the given bank account ID or empty if not found
	 */
	public Optional<BankAccount> getBankAccountById(int bankAccountId) {
		return bankAccountDao.getBankAccountById(bankAccountId);
	}
	
	/**
	 * Gets the bank account with the given name that is associated with the given user ID
	 * @param name - the given account name
	 * @param userId - the given user ID
	 * @return the bank account with the given name and associated user ID or empty if not found
	 */
	public Optional<BankAccount> getBankAccountByName(String name, int userId) {
		return bankAccountDao.getBankAccountByName(name, userId);
	}
	
	/**
	 * Attempts to create a bank account with a given name, initial balance, and associated user ID.
	 * @param name - the given account name
	 * @param initialBalance - the given initial balance for the account
	 * @param userId - the associated user ID
	 * @return the account ID if the creation attempt was successful
	 * @throws ItemExistsException - thrown if an account with the given name and associated user ID is found
	 * @throws IllegalArgumentException - thrown if the account name is not within the character length constraint or has whitespace or the given initial balance is negative
	 */
	public int createBankAccount(String name, double initialBalance, int userId) throws ItemExistsException, IllegalArgumentException {		
		Optional<BankAccount> accountWrapper = getBankAccountByName(name, userId); // check if an account with the name exists
		if(accountWrapper.isPresent()) {
			throw new ItemExistsException("Account with this name is associated with this user.");
		}
		
		if(name.length() > 50 || name.length() == 0) {
			throw new IllegalArgumentException("Account name must be between 1 and 50 characters in length.");
		}
		
		Matcher whitespaceMatcher = Pattern.compile("\\s").matcher(name); // check if the given name has no spaces		
		if(whitespaceMatcher.find()) {
			throw new IllegalArgumentException("Account name must not contain whitespace characters");
		}
		
		if(initialBalance < 0) {
			throw new IllegalArgumentException("Initial balance amount must be non-negative.");
		}
		
		initialBalance = Math.round(initialBalance * 100);
		initialBalance = initialBalance / 100;
		
		return bankAccountDao.createBankAccount(name, initialBalance, userId);
	}
	
	/**
	 * Attempts to delete the bank account with the given bank account ID
	 * @param bankAccountId - the given bank account ID
	 * @return 1 if the attempt was successful, 0 if the attempt failed, -1 if there was an error
	 * @throws ItemNotFoundException - thrown when the bank account with the given bank account ID is not found
	 * @throws AccountNotEmptyException - thrown when the bank account with the given bank account ID has a balance greater than 0
	 */
	public int deleteBankAccount(int bankAccountId) throws ItemNotFoundException, AccountNotEmptyException {
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			throw new ItemNotFoundException("Account not found.");
		}
		
		BankAccount account = accountWrapper.get();
		
		if(account.getBalance() > 0) {
			throw new AccountNotEmptyException("Account must be emptied before it can be deleted.");
		}
		
		return bankAccountDao.deleteBankAccount(bankAccountId);
	}
	
	/**
	 * Attempts to make a deposit into the bank account with the given bank account ID
	 * @param bankAccountId - the given bank account ID
	 * @param amount - the amount to deposit
	 * @return 1 if the deposit was successful, 0 if the deposit failed, -1 if an error occurred
	 * @throws ItemNotFoundException - thrown if the bank account with the given bank account ID is not found
	 * @throws IllegalArgumentException - thrown if the given amount to deposit is not positive
	 */
	public int makeDeposit(int bankAccountId, double amount) throws ItemNotFoundException, IllegalArgumentException {
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			throw new ItemNotFoundException("Account not found.");
		}
		
		if(amount <= 0) {
			throw new IllegalArgumentException("Deposit amount must be positive.");
		}
		
		amount = Math.round(amount * 100);
		amount = amount / 100;
		
		return bankAccountDao.makeDeposit(bankAccountId, amount);
	}
	
	/**
	 * Attempts to make a withdrawal from the bank account with the given bank account ID
	 * @param bankAccountId - the given bank account ID
	 * @param amount - the amount to withdraw
	 * @return 1 if the withdrawal was successful, 0 if the withdrawal failed, -1 if an error occurred
	 * @throws ItemNotFoundException - thrown if the bank account with the given bank account ID is not found
	 * @throws IllegalArgumentException - thrown if the given amount to withdraw is not positive
	 * @throws OverdraftException - thrown if the given amount exceeds the balance in the bank account
	 */
	public int makeWithdrawal(int bankAccountId, double amount) throws ItemNotFoundException, IllegalArgumentException, OverdraftException {
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			throw new ItemNotFoundException("Account not found.");
		}
		
		BankAccount account = accountWrapper.get();
		
		if(amount <= 0) {
			throw new IllegalArgumentException("Withdrawal amount must be positive.");
		}
		
		amount = Math.round(amount * 100);
		amount = amount / 100;
		
		if(account.getBalance() < amount) {
			throw new OverdraftException("Insufficent funds to withdraw the given amount.");
		}
		
		return bankAccountDao.makeWithdrawal(bankAccountId, amount);
	}
}
