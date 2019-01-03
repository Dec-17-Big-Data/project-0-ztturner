package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.revature.jdbcbank.dao.UserDao;
import com.revature.jdbcbank.dao.UserOracle;
import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.models.User;

public class UserService {
	private static UserService userService;
	final static UserDao userDao = UserOracle.getUserDao();
	
	private UserService() {
		
	}
	
	public static UserService getUserService() {
		if(userService == null) {
			userService = new UserService();
		}
		
		return userService;
	}
	
	public Optional<List<User>> getAllUsers() {
		return userDao.getAllUsers();
	}
	
	public Optional<User> getUserById(int userId) {
		return userDao.getUserById(userId);
	}
	
	public Optional<User> getUserByUsername(String username) {
		return userDao.getUserByUsername(username);
	}
	
	public int loginUser(String username, String password) throws InvalidCredentialsException {
		int userId = userDao.loginUser(username, password);
		
		if(userId == 0) {
			throw new InvalidCredentialsException("Invalid username or password");
		}
		
		return userId;
	}
	
	public int createUser(String username, String password) throws ItemExistsException, IllegalArgumentException {
		Optional<User> userWrapper = getUserByUsername(username);		
		if(userWrapper.isPresent()) {
			throw new ItemExistsException("A user with this username exists already.");
		}
		
		if(username.length() > 50 || username.length() == 0) {
			throw new IllegalArgumentException("Username must be between 1 and 50 characters in length");
		}
		if(password.length() > 50 || password.length() < 8) {
			throw new IllegalArgumentException("Password must be between 8 and 50 characters in length");
		}
		
		Matcher usernameMatcher = Pattern.compile("\\s").matcher(username);
		Matcher passwordMatcher = Pattern.compile("\\s").matcher(password);
		
		if(usernameMatcher.find() || passwordMatcher.find()) {
			throw new IllegalArgumentException("Username and password must not contain any whitespace");
		}
		
		return userDao.createUser(username, password);
	}
	
	public int deleteUser(int userId) throws ItemNotFoundException {
		Optional<User> userWrapper = getUserById(userId);
		if(!userWrapper.isPresent()) {
			throw new ItemNotFoundException("User not found.");
		}
		
		return userDao.deleteUser(userId);
	}
	
	public int updateUserPassword(int userId, String password) throws ItemNotFoundException, IllegalArgumentException {
		Optional<User> userWrapper = getUserById(userId);
		if(!userWrapper.isPresent()) {
			throw new ItemNotFoundException("User not found.");
		}
		
		if(password.length() > 50 || password.length() < 8) {
			throw new IllegalArgumentException("Password must be between 8 and 50 characters in length");
		}
		
		Matcher whitespaceMatcher = Pattern.compile("\\s").matcher(password);
		
		if(whitespaceMatcher.find()) {
			throw new IllegalArgumentException("Password must not contain any whitespace");
		}
		
		return userDao.updateUserPassword(userId, password);
	}
}
