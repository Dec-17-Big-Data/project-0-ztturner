package com.revature.jdbcbank.serviceTest;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.services.UserService;

import static org.junit.Assert.*;

public class UserServiceTest {
	
	private static UserService userService;
	
	@BeforeClass
	public static void setup() {
		userService = UserService.getUserService();
	}
	
	@Test
	public void testCreatingUserValidInput() {
		String inputUsername = "available";
		String inputPassword = "agoodpassword";
		
		int newUserId = userService.createUser(inputUsername, inputPassword);

		assertTrue(newUserId > 0);
		
		userService.deleteUser(newUserId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingUserWhitespaceUsername() {
		String inputUserame = "whitespace username";
		String inputPassword = "password";
		
		int newUserId = userService.createUser(inputUserame, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingUserWhitespacePassword() {
		String inputUserame = "username";
		String inputPassword = "whitespace password";
		
		int newUserId = userService.createUser(inputUserame, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingUserEmptyUsername() {
		String inputUsername = "";
		String inputPassword = "password";
		
		int newUserId = userService.createUser(inputUsername, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingUserTooLongUsername() {
		String inputUsername = "afw;oifjawebafw;oifjas9fasiuhfawsbaw9823hfalsehr298hrewpaf9hfwa3iurhaw98havaiudh";
		String inputPassword = "password";
		
		int newUserId = userService.createUser(inputUsername, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingUserTooShortPassword() {
		String inputUsername = "okayuser";
		String inputPassword = "pass";
		
		int newUserId = userService.createUser(inputUsername, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreatingUserTooLongPassword() {
		String inputUsername = "okayuser";
		String inputPassword = "afw;oifjawebafw;oifjas9fasiuhfawsbaw9823hfalsehr298hrewpaf9hfwa3iurhaw98havaiudh";
		
		int newUserId = userService.createUser(inputUsername, inputPassword);
	}
	
	@Test(expected=ItemExistsException.class)
	public void testCreatingUserExistingUser() {
		String inputUsername = "newuser";
		String inputPassword = "password";
		
		int newUserId = userService.createUser(inputUsername, inputPassword);
	}
	
	@Test
	public void testLoggingInValidCredentials() {
		String inputUsername = "newuser";
		String inputPassword = "password";
		
		int loginUserId = userService.loginUser(inputUsername, inputPassword);
		
		assertTrue(loginUserId > 0);
	}
	
	@Test(expected=InvalidCredentialsException.class)
	public void testLoggingInInvalidCredentials() {
		String inputUsername = "newuser";
		String inputPassword = "wrongpassword";
		
		int loginUserId = userService.loginUser(inputUsername, inputPassword);
	}
	
	@Test
	public void testUpdatingPasswordValidPassword() {
		String inputPassword = "password";
		int userId = 1;
		
		int updateSuccess = userService.updateUserPassword(userId, inputPassword);
		
		assertEquals(1, updateSuccess);
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void testUpdatingPasswordInvalidUserId() {
		String inputPassword = "password";
		int userId = 0;
		
		int updateSuccess = userService.updateUserPassword(userId, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdatingPasswordShortPassword() {
		String inputPassword = "pass";
		int userId = 1;
		
		int updateSuccess = userService.updateUserPassword(userId, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testUpdatingPasswordLongPassword() {
		String inputPassword = "afw;oifjawebafw;oifjas9fasiuhfawsbaw9823hfalsehr298hrewpaf9hfwa3iurhaw98havaiudh";
		int userId = 1;
		
		int updateSuccess = userService.updateUserPassword(userId, inputPassword);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdatingPasswordWhitespacePassword() {
		String inputPassword = "whitespacepassword    ";
		int userId = 1;
		
		int updateSuccess = userService.updateUserPassword(userId, inputPassword);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdatingPasswordJustWhitespacePassword() {
		String inputPassword = "                \r\t\n";
		int userId = 1;
		
		int updateSuccess = userService.updateUserPassword(userId, inputPassword);
	}
}
