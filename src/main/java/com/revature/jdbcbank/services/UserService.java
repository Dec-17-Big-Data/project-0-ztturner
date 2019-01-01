package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;

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
	
	public int loginUser(String username, String password) {
		return userDao.loginUser(username, password);		
	}
	
	public int createUser(String username, String password) throws ItemExistsException, IllegalArgumentException {
		Optional<User> userWrapper = getUserByUsername(username);		
		if(userWrapper.isPresent()) {
			throw new ItemExistsException("A user with this username exists already.");
		}
		
		if(username.length() > 50 || password.length() > 50) {
			throw new IllegalArgumentException("Username and password must be 50 characters or less in length");
		}
		
		String[] splitName = username.split("\\s+"); // check that the username has no spaces
		String[] splitPass = password.split("\\s+"); // check that the password has no spaces
		
		if(splitName.length > 1 || splitPass.length > 1) {
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
		
		if(password.length() > 50) {
			throw new IllegalArgumentException("Password must be 50 characters or less in length");
		}
		String[] splitPass = password.split("\\s+");
		
		if(splitPass.length > 1) {
			throw new IllegalArgumentException("Password must not contain any whitespace");
		}
		
		return userDao.updateUserPassword(userId, password);
	}
}
