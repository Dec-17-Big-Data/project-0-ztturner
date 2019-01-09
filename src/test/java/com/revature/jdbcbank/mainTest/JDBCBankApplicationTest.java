package com.revature.jdbcbank.mainTest;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.exceptions.InvalidCredentialsException;
import com.revature.jdbcbank.main.JDBCBankApplication;

import oracle.net.aso.j;

public class JDBCBankApplicationTest {
	private static List<String> strings;
	
	@BeforeClass
	public static void setup() { 
		strings = new ArrayList<String>();
		
		strings.add("Option 1");
		strings.add("Option 2");
		strings.add("Option 3");
	}
	
	@Test(expected=InvalidCredentialsException.class)
	public void testLoginSuperuserInvalidCredentials() {
		String username = "password";
		String password = "username";
		
		int success = JDBCBankApplication.loginSuperUser(username, password);
	}
	
	@Test 
	public void testGetAmountValidPositiveInteger() {
		Scanner scanner = new Scanner(new StringReader("5\n"));
		
		double amount = JDBCBankApplication.getAmount(scanner);
		
		assertEquals(Double.doubleToLongBits(5), Double.doubleToLongBits(amount));
	}
	
	@Test
	public void testGetAmountValidNegativeInteger() {
		Scanner scanner = new Scanner(new StringReader("-5\n"));
		
		double amount = JDBCBankApplication.getAmount(scanner);
		
		assertEquals(Double.doubleToLongBits(-5), Double.doubleToLongBits(amount));
	}
	
	@Test
	public void testGetAmountZero() {
		Scanner scanner = new Scanner(new StringReader("0.00"));
		
		double amount = JDBCBankApplication.getAmount(scanner);
		
		assertEquals(Double.doubleToLongBits(0), Double.doubleToLongBits(amount));
	}
	
	@Test
	public void testGetAmountValidPositiveDecimal() {
		Scanner scanner = new Scanner(new StringReader("5.25\n"));
				
		double amount = JDBCBankApplication.getAmount(scanner);
		
		assertEquals(Double.doubleToLongBits(5.25), Double.doubleToLongBits(amount));
	}
	
	@Test
	public void testGetAmountValidNegativeDecimal() {
		Scanner scanner = new Scanner(new StringReader("-5.25\n"));

		double amount = JDBCBankApplication.getAmount(scanner);
		
		assertEquals(Double.doubleToLongBits(-5.25), Double.doubleToLongBits(amount));
	}
	
	@Test(expected=NumberFormatException.class)
	public void testGetAmountInvalidInput() {
		Scanner scanner = new Scanner(new StringReader("25.250asdsd\n"));
		
		double amount = JDBCBankApplication.getAmount(scanner);
	}
	
	@Test
	public void testGetAccountInfoValidPositiveInteger() {
		Scanner scanner = new Scanner(new StringReader("Checking\n5\n"));
		
		String[] accountInfo = JDBCBankApplication.getAccountInfo(scanner);
		
		assertEquals("Checking", accountInfo[0]);
		assertEquals("5", accountInfo[1]);
	}
	
	@Test
	public void testGetAccountInfoValidNegativeInteger() {
		Scanner scanner = new Scanner(new StringReader("Checking\n-5\n"));
		
		String[] accountInfo = JDBCBankApplication.getAccountInfo(scanner);
		
		assertEquals("Checking", accountInfo[0]);
		assertEquals("-5", accountInfo[1]);
	}
	
	@Test
	public void testGetAccountInfoValidPositiveDecimal() {
		Scanner scanner = new Scanner(new StringReader("Checking\n5.25\n"));
		
		String[] accountInfo = JDBCBankApplication.getAccountInfo(scanner);
		
		assertEquals("Checking", accountInfo[0]);
		assertEquals("5.25", accountInfo[1]);
	}
	
	@Test
	public void testGetAccountInfoValidNegativeDecimal() {
		Scanner scanner = new Scanner(new StringReader("Checking\n-5.25\n"));
		
		String[] accountInfo = JDBCBankApplication.getAccountInfo(scanner);
		
		assertEquals("Checking", accountInfo[0]);
		assertEquals("-5.25", accountInfo[1]);
	}
	
	@Test(expected=NumberFormatException.class)
	public void testGetAccountInfoInvalidNumber() {
		Scanner scanner = new Scanner(new StringReader("Checking\n5.25sjie\n"));
		
		String[] accountInfo = JDBCBankApplication.getAccountInfo(scanner);
	}
	
	@Test
	public void testSelectItemEmptyList() {
		Scanner scanner = new Scanner(new StringReader("1\n"));
		
		int selectedIndex = JDBCBankApplication.selectItem(new ArrayList<String>(), "String", scanner);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectItemValidInput() {
		Scanner scanner = new Scanner(new StringReader("2\n"));
		
		int selectedIndex = JDBCBankApplication.selectItem(strings, "String", scanner);
		
		assertEquals(2, selectedIndex);
	}
	
	@Test
	public void testSelectItemZeroIndex() {
		Scanner scanner = new Scanner(new StringReader("0\n"));
		
		int selectedIndex = JDBCBankApplication.selectItem(strings, "String", scanner);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test 
	public void testSelectItemNegativeIndex() {
		Scanner scanner = new Scanner(new StringReader("-2\n"));
			
		int selectedIndex = JDBCBankApplication.selectItem(strings, "String", scanner);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectItemMixingDigitsAndCharacters() {
		Scanner scanner = new Scanner(new StringReader("1fe\n"));
		
		int selectedIndex = JDBCBankApplication.selectItem(strings, "String", scanner);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testGetUserInfoValidInput() {
		Scanner scanner = new Scanner(new StringReader("username\npassword\n"));
		
		String[] userInfo = JDBCBankApplication.getUserInfo(scanner);
		
		assertEquals("username", userInfo[0]);
		assertEquals("password", userInfo[1]);
	}
	
	@Test
	public void testGetUserInfoEmptyStrings() {
		Scanner scanner = new Scanner(new StringReader("\n\n"));
		
		String[] userInfo = JDBCBankApplication.getUserInfo(scanner);
		
		assertEquals("", userInfo[0]);
		assertEquals("", userInfo[1]);
	}
}
