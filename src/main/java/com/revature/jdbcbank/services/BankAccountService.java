package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;

import com.revature.jdbcbank.dao.BankAccountDao;
import com.revature.jdbcbank.dao.BankAccountOracle;
import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.models.BankAccount;

public class BankAccountService {
	private static BankAccountService bankAccountService;
	final static BankAccountDao bankAccountDao = BankAccountOracle.getBankAccountDao();
	
	private BankAccountService() {
		
	}
	
	public BankAccountService getBankAccountService() {
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
		
		if(name.length() > 50) {
			throw new IllegalArgumentException("Account name must be 50 characters or less.");
		}
		
		String[] splitName = name.split("\\s+"); // check if the given name has no spaces		
		if(splitName.length > 1) {
			throw new IllegalArgumentException("Account name must not contain whitespace characters");
		}
		
		if(initialBalance < 0) {
			throw new IllegalArgumentException("Initial balance amount must be positive.");
		}
		
		if(initialBalance % 0.01 != 0) {
			throw new IllegalArgumentException("Initial balance amount must be less precise than the thousandths place (ex: 2.25)");
		}
		
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
	
	public int makeDeposit(int bankAccountId, double amount) throws ItemNotFoundException {
		Optional<BankAccount> accountWrapper = getBankAccountById(bankAccountId);
		if(!accountWrapper.isPresent()) {
			throw new ItemNotFoundException("Account not found.");
		}
		
		if(amount < 0) {
			throw new IllegalArgumentException("Deposit amount must be positive.");
		}
		
		if(amount % 0.01 != 0) {
			throw new IllegalArgumentException("Deposit amount must be less precise than the thousandths place (ex: 2.25)");
		}
		
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
		
		if(amount % 0.01 != 0) {
			throw new IllegalArgumentException("Withdrawal amount must be less precise than the thousandths place (ex: 2.25)");
		}
		
		if(account.getBalance() < amount) {
			throw new OverdraftException("Insufficent funds to withdraw the given amount.");
		}
		
		return bankAccountDao.makeWithdrawal(bankAccountId, amount);
	}
}
