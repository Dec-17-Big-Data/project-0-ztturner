package com.revature.jdbcbank.daoTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.dao.UserOracle;
import com.revature.jdbcbank.models.User;

public class UserOracleTest {

	private static UserOracle userOracle;
	
	@BeforeClass
	public static void setup() {
		userOracle = UserOracle.getUserDao();
	}
	
	@Test
	public void testGettingAllUsers() {
		List<User> expectedUsers = new ArrayList<User>();
		
		expectedUsers.add(new User(1, "newuser", "password"));
		expectedUsers.add(new User(2, "belmontr", "begonemonster"));
		expectedUsers.add(new User(3, "anotheruser", "anotherpassword"));

		Optional<List<User>> usersWrapper = userOracle.getAllUsers();
		List<User> actualUsers = usersWrapper.get();
		
		assertEquals(expectedUsers, actualUsers);
	}
	
	@Test
	public void testGettingUserByIdValid() {
		User expectedUser = new User(1, "newuser", "password");
		
		Optional<User> userWrapper = userOracle.getUserById(1);
		User actualUser = userWrapper.get();
		
		assertEquals(expectedUser, actualUser);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGettingUserByIdInvalid() {
		Optional<User> userWrapper = userOracle.getUserById(-5);
		User actualUser = userWrapper.get();
	}
	
	@Test
	public void testGettingUserByUsernameValid() {
		User expectedUser = new User(1, "newuser", "password");
		
		Optional<User> userWrapper = userOracle.getUserByUsername("newuser");
		User actualUser = userWrapper.get();
		
		assertEquals(expectedUser, actualUser);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGettingUserByUsernameInvalid() {
		Optional<User> userWrapper = userOracle.getUserByUsername("nonexistentuser");
		User actualUser = userWrapper.get();
	}
	
	@Test
	public void testCreatingUserValidInput() {
		List<User> expectedUsers = new ArrayList<User>();
		expectedUsers.add(new User(1, "newuser", "password"));
		expectedUsers.add(new User(2, "belmontr", "begonemonster"));
		expectedUsers.add(new User(3, "anotheruser", "anotherpassword"));
		expectedUsers.add(new User(4, "testuser", "testpassword"));
		
		int newUserId = userOracle.createUser("testuser", "testpassword");
		assertTrue(newUserId > 0);
		
		Optional<List<User>> usersWrapper = userOracle.getAllUsers();
		List<User> actualUsers = usersWrapper.get();
		
		assertEquals(expectedUsers, actualUsers);
	}
	
	@Test
	public void testCreatingUserUserAlreadyExists() {		
		int newUserId = userOracle.createUser("newuser", "originalpassword");
		assertEquals(0, newUserId);
	}
	
	@Test
	public void testCreatingUserInvalidInput() {		
		// passing empty strings should return 0
		int newUserId = userOracle.createUser("", "emptystring");
		assertEquals(0, newUserId);
		
		newUserId = userOracle.createUser("goodusername", "");
		assertEquals(0, newUserId);
		
		// passing short password should return 0
		newUserId = userOracle.createUser("goodusername", "pass");
		assertEquals(0, newUserId);
	}
	
	@Test
	public void testLoginValidUser() {
		int loginUserId = userOracle.loginUser("newuser", "password");
		assertTrue(loginUserId > 0);
	}
}
