package com.revature.jdbcbank.serviceTest;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.services.BankAccountService;

public class BankAccountServiceTest {
	
	private static BankAccountService bankAccountService;
	
	@BeforeClass
	public static void setup() {
		bankAccountService = BankAccountService.getBankAccountService();
	}
	
	@Test
	public void testCreatingBankAccountValidInput() {
		String accountName = "GoodAccountName";
		double initialBalance = 25.75;
		int userId = 1;
		
		int newAccountId = bankAccountService.createBankAccount(accountName, initialBalance, userId);
		
		assertTrue(newAccountId > 0);
		
		bankAccountService.makeWithdrawal(newAccountId, initialBalance);
		bankAccountService.deleteBankAccount(newAccountId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountEmptyAccountName() {
		String accountName = "";
		double initialBalance = 5.55;
		int userId = 1;
		
		int newAccountId = bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountWhitespaceAccountName() {
		String accountName = "Account Name";
		double initialBalance = 5.55;
		int userId = 1;
		
		int newAccountId = bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountJustWhitespaceAccountName() {
		String accountName = "   \t\r\n";
		double initialBalance = 5.55;
		int userId = 1;
		
		int newAccountId = bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountNegativeInitialBalance() {
		String accountName = "AccountName";
		double initialBalance = -1.00;
		int userId = 1;
		
		int newAccountId = bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=ItemExistsException.class)
	public void testCreatingBankAccountAlreadyExists() {
		String accountName = "Checking";
		double initialBalance = 5.55;
		int userId = 1;
		
		int newAccountId = bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testDeletingBankAccountNonExistentAccount() {
		int accountId = 0;
		
		int deleteSuccess = bankAccountService.deleteBankAccount(accountId);
	}
	
	@Test(expected=AccountNotEmptyException.class)
	public void testDeletingBankAccountAccountNotEmpty() {
		int accountId = 1;
		
		int deleteSuccess = bankAccountService.deleteBankAccount(accountId);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testMakingDepositNonExistentAccount() {
		int accountId = 0;
		double amount = 5.55;
		
		int depositSuccess = bankAccountService.makeDeposit(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingDepositNegativeAmount() {
		int accountId = 1;
		double amount = -1.11;
		
		int depositSuccess = bankAccountService.makeDeposit(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingDepositZeroAmount() {
		int accountId = 1;
		double amount = 0;
		
		int depositSuccess = bankAccountService.makeDeposit(accountId, amount);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testMakingWithdrawalNonExistentAccount() {
		int accountId = 0;
		double amount = 1.11;
		
		int withdrawalSuccess = bankAccountService.makeWithdrawal(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingWithdrawalNegativeAmount() {
		int accountId = 1;
		double amount = -1.11;
		
		int withdrawalSuccess = bankAccountService.makeWithdrawal(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingWithdrawalZeroAmount() {
		int accountId = 1;
		double amount = 0;
		
		int withdrawalSuccess = bankAccountService.makeWithdrawal(accountId, amount);
	}
	
	@Test(expected=OverdraftException.class)
	public void testMakingWithdrawalOverdraft() {
		int accountId = 1;
		double amount = 1000000;
		
		int withdrawalSuccess = bankAccountService.makeWithdrawal(accountId, amount);
	}
}
