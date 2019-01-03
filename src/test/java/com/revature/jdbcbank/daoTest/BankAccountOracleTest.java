package com.revature.jdbcbank.daoTest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.dao.BankAccountOracle;
import com.revature.jdbcbank.models.BankAccount;

public class BankAccountOracleTest {
	
	private static BankAccountOracle bankAccountOracle;
	
	@BeforeClass
	public static void setup() {
		bankAccountOracle = BankAccountOracle.getBankAccountDao();
	}
	
	@Test
	public void testGettingAllBankAccounts() {
		List<BankAccount> expectedAccounts = new ArrayList<BankAccount>();
		expectedAccounts.add(new BankAccount(1, "MySavings", 5.00, 1));
		expectedAccounts.add(new BankAccount(2, "Checking", 5.00, 1));
		expectedAccounts.add(new BankAccount(3, "FamilySavings", 1000.50, 2));
		
		Optional<List<BankAccount>> accountsWrapper = bankAccountOracle.getAllBankAccounts();
		List<BankAccount> actualAccounts = accountsWrapper.get();
		
		assertEquals(expectedAccounts, actualAccounts);
	}
	
	@Test
	public void testGettingBankAccountByIdValid() {
		BankAccount expectedAccount = new BankAccount(1, "MySavings", 5.00, 1);
		
		Optional<BankAccount> accountWrapper = bankAccountOracle.getBankAccountById(1);
		BankAccount actualAccount = accountWrapper.get();
		
		assertEquals(expectedAccount, actualAccount);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGettingBankAccountByIdInvalid() {
		Optional<BankAccount> accountWrapper = bankAccountOracle.getBankAccountById(-2);
		BankAccount actualAccount = accountWrapper.get();
	}
	
	@Test
	public void testGettingBankAccountByNameValid() {
		BankAccount expectedAccount = new BankAccount(3, "FamilySavings", 1000.50, 2);
		
		Optional<BankAccount> accountWrapper = bankAccountOracle.getBankAccountByName("FamilySavings", 2);
		BankAccount actualAccount = accountWrapper.get();
		
		assertEquals(expectedAccount, actualAccount);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGettingBankAccountByNameInvalid() {
		Optional<BankAccount> accountWrapper = bankAccountOracle.getBankAccountByName("FamilySavings", 1);
		BankAccount actualAccount = accountWrapper.get();
	}
	
	@Test
	public void testGettingAllBankAccountsByUserValidUserId() {
		List<BankAccount> expectedAccounts = new ArrayList<BankAccount>();
		expectedAccounts.add(new BankAccount(1, "MySavings", 5.00, 1));
		expectedAccounts.add(new BankAccount(2, "Checking", 5.00, 1));
		
		Optional<List<BankAccount>> accountsWrapper = bankAccountOracle.getAllBankAccountsByUser(1);
		List<BankAccount> actualAccounts = accountsWrapper.get();
		
		assertEquals(expectedAccounts, actualAccounts);
	}
	
	@Test
	public void testGettingAllBankAccountsByUserInvalidUserId() {
		List<BankAccount> expectedAccounts = new ArrayList<BankAccount>();
		
		Optional<List<BankAccount>> accountsWrapper = bankAccountOracle.getAllBankAccountsByUser(0);
		List<BankAccount> actualAccounts = accountsWrapper.get();
		
		assertEquals(expectedAccounts, actualAccounts);
	}
	
	@Test
	public void testGettingAllBankAccountsByUserValidUserNoAccounts() {
		List<BankAccount> expectedAccounts = new ArrayList<BankAccount>();
		
		Optional<List<BankAccount>> accountsWrapper = bankAccountOracle.getAllBankAccountsByUser(3);
		List<BankAccount> actualAccounts = accountsWrapper.get();
		
		assertEquals(expectedAccounts, actualAccounts);
	}
}
