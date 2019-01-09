package com.revature.jdbcbank.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.models.BankAccount;
import com.revature.jdbcbank.services.BankAccountService;

public class BankAccountServiceTest {
	
	private static BankAccountService bankAccountService;
	
	@BeforeClass
	public static void setup() {
		bankAccountService = BankAccountService.getBankAccountService();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountEmptyAccountName() {
		String accountName = "";
		double initialBalance = 5.55;
		int userId = 1;
		
		bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountWhitespaceAccountName() {
		String accountName = "Account Name";
		double initialBalance = 5.55;
		int userId = 1;
		
		bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountJustWhitespaceAccountName() {
		String accountName = "   \t\r\n";
		double initialBalance = 5.55;
		int userId = 1;
		
		bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingBankAccountNegativeInitialBalance() {
		String accountName = "AccountName";
		double initialBalance = -1.00;
		int userId = 1;
		
		bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=ItemExistsException.class)
	public void testCreatingBankAccountAlreadyExists() {
		String accountName = "Checking";
		double initialBalance = 5.55;
		int userId = 1;
		
		bankAccountService.createBankAccount(accountName, initialBalance, userId);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testDeletingBankAccountNonExistentAccount() {
		int accountId = 0;
		
		bankAccountService.deleteBankAccount(accountId);
	}
	
	@Test(expected=AccountNotEmptyException.class)
	public void testDeletingBankAccountAccountNotEmpty() {
		int accountId = 1;
		
		bankAccountService.deleteBankAccount(accountId);
	}
	
	@Test
	public void testMakingDepositValidInput() {
		int accountId = 1;
		double amount = 5.55;
		
		int success = bankAccountService.makeDeposit(accountId, amount);
		
		assertEquals(1, success);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testMakingDepositNonExistentAccount() {
		int accountId = 0;
		double amount = 5.55;
		
		bankAccountService.makeDeposit(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingDepositNegativeAmount() {
		int accountId = 1;
		double amount = -1.11;
		
		bankAccountService.makeDeposit(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingDepositZeroAmount() {
		int accountId = 1;
		double amount = 0;
		
		bankAccountService.makeDeposit(accountId, amount);
	}
	
	@Test
	public void testMakingWithdrawalValidInput() {
		int accountId = 1;
		double amount = 5.55;
		
		int success = bankAccountService.makeWithdrawal(accountId, amount);
		
		assertEquals(1, success);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testMakingWithdrawalNonExistentAccount() {
		int accountId = 0;
		double amount = 1.11;
		
		bankAccountService.makeWithdrawal(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingWithdrawalNegativeAmount() {
		int accountId = 1;
		double amount = -1.11;
		
		bankAccountService.makeWithdrawal(accountId, amount);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMakingWithdrawalZeroAmount() {
		int accountId = 1;
		double amount = 0;
		
		bankAccountService.makeWithdrawal(accountId, amount);
	}
	
	@Test(expected=OverdraftException.class)
	public void testMakingWithdrawalOverdraft() {
		int accountId = 1;
		double amount = Double.MAX_VALUE;
		
		bankAccountService.makeWithdrawal(accountId, amount);
	}
}
