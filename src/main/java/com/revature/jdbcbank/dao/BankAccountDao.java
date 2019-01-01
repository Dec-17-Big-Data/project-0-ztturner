package com.revature.jdbcbank.dao;

import java.util.List;
import java.util.Optional;

import com.revature.jdbcbank.models.BankAccount;

public interface BankAccountDao {
	Optional<List<BankAccount>> getAllBankAccounts();
	Optional<List<BankAccount>> getAllBankAccountsByUser(int userId);
	Optional<BankAccount> getBankAccountById(int bankAccountId);
	Optional<BankAccount> getBankAccountByName(String name, int userId);
	int createBankAccount(String name, double initialBalance, int userId);
	int deleteBankAccount(int bankAccountId);
	int makeDeposit(int bankAccountId, double amount);
	int makeWithdrawal(int bankAccountId, double amount);
}
