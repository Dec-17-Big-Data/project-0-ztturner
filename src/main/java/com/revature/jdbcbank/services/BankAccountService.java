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
	
	public static BankAccountService getBankAccountService() {
		if(bankAccountService == null) {
			bankAccountService = new BankAccountService();
		}
		
		return bankAccountService;
	}
	
	public Optional<List<BankAccount>> getAllBankAccounts() {
		return bankAccountDao.getAllBankAccounts();
	}
	
	public Optional<List<BankAccount>> getAllBankAccountsByUser(int userId) {
		return bankAccountDao.getAllBankAccountsByUser(userId);
	}
	
	public Optional<BankAccount> getBankAccountById(int bankAccountId) {
		return bankAccountDao.getBankAccountById(bankAccountId);
	}
	
	public Optional<BankAccount> getBankAccountByName(String name, int userId) {
		return bankAccountDao.getBankAccountByName(name, userId);
	}
	
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
	
	public int makeWithdrawal(int bankAccountId, double amount) throws ItemNotFoundException, IllegalArgumentException, OverdraftException {
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			throw new ItemNotFoundException("Account not found.");
		}
		
		BankAccount account = accountWrapper.get();
		
		if(amount < 0) {
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
