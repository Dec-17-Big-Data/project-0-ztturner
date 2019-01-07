package com.revature.jdbcbank.services;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.jdbcbank.dao.UserDao;
import com.revature.jdbcbank.dao.UserOracle;
import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.models.User;

public class UserService {
	private static UserService userService;
	final static UserDao userDao = UserOracle.getUserDao();
	private static final Logger logger = LogManager.getLogger(UserService.class);
	
	private UserService() {
		
	}
	
	/**
	 * Gets the user service
	 * @return the user service
	 */
	public static UserService getUserService() {
		if(userService == null) {
			logger.info("Creating new user service");
			userService = new UserService();
		}
		
		return userService;
	}
	
	/**
	 * Gets all the users in the database.
	 * @return all the users in the database or empty if an error occurred
	 */
	public Optional<List<User>> getAllUsers() {
		logger.traceEntry("Getting all users");
		return logger.traceExit(userDao.getAllUsers());
	}
	
	/**
	 * Gets the user with the given UserID.
	 * @param userId - the given user ID
	 * @return the user with the given user ID or empty if the user could not be found
	 */
	public Optional<User> getUserById(int userId) {
		logger.traceEntry("Getting user with ID = {}", userId);
		return logger.traceExit(userDao.getUserById(userId));
	}
	
	/**
	 * Gets the user with the given username
	 * @param username - the given username
	 * @return the user with the given username or empty if the user could not be found
	 */
	public Optional<User> getUserByUsername(String username) {
		logger.traceEntry("Getting user with username = {}", username);
		return logger.traceExit(userDao.getUserByUsername(username));
	}
	
	/**
	 * Attempts to login a user by matching the given username and password
	 * with one in the database.
	 * @param username - the given username
	 * @param password - the given password
	 * @return the user ID of the user if the login attempt was successful
	 * @throws InvalidCredentialsException - thrown when the login attempt is unsuccessful and returns 0
	 */
	public int loginUser(String username, String password) throws InvalidCredentialsException {
		logger.traceEntry("Attempting login for user");
		int userId = userDao.loginUser(username, password);
		
		if(userId == 0) {
			logger.info("User login attempt failed");
			throw new InvalidCredentialsException("Invalid username or password");
		}
		
		return logger.traceExit("User successfully logged in", userId);
	}
	
	/**
	 * Attempts to create a user with the given username and password
	 * and add them to the database.
	 * @param username - the given username
	 * @param password - the given password
	 * @return the user ID of the user if the creation attempt was successful
	 * @throws ItemExistsException - thrown when a user with the given username already exists
	 * @throws IllegalArgumentException - thrown if the given username or password is not within the character length constraints or has whitespace characters
	 */
	public int createUser(String username, String password) throws ItemExistsException, IllegalArgumentException {
		logger.traceEntry("Creating user with username = {}", username);
		
		Optional<User> userWrapper = getUserByUsername(username);	
		if(userWrapper.isPresent()) {
			logger.info("User with username = {} already exists", username);
			throw new ItemExistsException("A user with this username exists already.");
		}
		
		// check if the username is within the required character length
		if(username.length() > 50 || username.length() == 0) {
			throw new IllegalArgumentException("Username must be between 1 and 50 characters in length");
		}
		
		// check if the password is within the required character length
		if(password.length() > 50 || password.length() < 8) {
			throw new IllegalArgumentException("Password must be between 8 and 50 characters in length");
		}
		
		Matcher usernameMatcher = Pattern.compile("\\s").matcher(username);
		Matcher passwordMatcher = Pattern.compile("\\s").matcher(password);
		
		// check if the username or password contain whitespace characters
		if(usernameMatcher.find() || passwordMatcher.find()) {
			throw new IllegalArgumentException("Username and password must not contain any whitespace");
		}
		
		return logger.traceExit(userDao.createUser(username, password));
	}
	
	/**
	 * Attempts to delete the user with the given user ID from the database.
	 * @param userId - the given user ID
	 * @return 1 if the deletion attempt was successful, 0 if it failed, -1 if an error occurred
	 * @throws ItemNotFoundException - thrown when no user with the given user ID exists in the database
	 */
	public int deleteUser(int userId) throws ItemNotFoundException {
		logger.traceEntry("Deleting user with ID = {}", userId);
		
		Optional<User> userWrapper = getUserById(userId);
		if(!userWrapper.isPresent()) {
			logger.info("User with ID = {} not found", userId);
			throw new ItemNotFoundException("User not found.");
		}
		
		return logger.traceExit(userDao.deleteUser(userId));
	}
	
	/**
	 * Attempts to update a user's password and make the changes in the database.
	 * @param userId - the user ID of the user to update
	 * @param password - the new password
	 * @return 1 if the update attempt was successful, 0 if it failed, -1 if an error occurred
	 * @throws ItemNotFoundException - thrown when no user with the given user ID exists in the database
	 * @throws IllegalArgumentException - thrown when the given password is not within the character length constraints or has whitespace characters
	 */
	public int updateUserPassword(int userId, String password) throws ItemNotFoundException, IllegalArgumentException {
		logger.traceEntry("Changing user with ID = {}", userId);
		
		Optional<User> userWrapper = getUserById(userId);
		if(!userWrapper.isPresent()) {
			logger.info("User with ID = {} not found", userId);
			throw new ItemNotFoundException("User not found.");
		}
		
		// check if the password is within the required character length
		if(password.length() > 50 || password.length() < 8) {
			throw new IllegalArgumentException("Password must be between 8 and 50 characters in length");
		}
		
		Matcher whitespaceMatcher = Pattern.compile("\\s").matcher(password);
		
		// check if the password contains any whitespace characters
		if(whitespaceMatcher.find()) {
			throw new IllegalArgumentException("Password must not contain any whitespace");
		}
		
		return logger.traceExit(userDao.updateUserPassword(userId, password));
	}
}
