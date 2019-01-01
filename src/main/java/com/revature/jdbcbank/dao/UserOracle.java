package com.revature.jdbcbank.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.jdbcbank.models.User;
import com.revature.jdbcbank.util.ConnectionUtil;

public class UserOracle implements UserDao{
	private static UserOracle userOracle;
	private static final Logger logger = LogManager.getLogger(UserOracle.class);
	
	private UserOracle() {
		
	}
	
	public static UserOracle getUserDao() {
		if(userOracle == null) {
			userOracle = new UserOracle();
		}
		
		return userOracle;
	}

	@Override
	public Optional<List<User>> getAllUsers() {
		logger.traceEntry("Getting all users.");
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_users;";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			List<User> users = new ArrayList<User>();
			while(rs.next()) {
				users.add(new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password")));
			}
			
			return logger.traceExit(Optional.of(users));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while getting users", e);
		}
		return logger.traceExit(Optional.empty());
	}

	@Override
	public Optional<User> getUserById(int userId) {
		logger.traceEntry("Getting user with userID = {}", userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_users where user_id = ?;";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			
			User user = null;
			while(rs.next()) {
				user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
			}
			
			if(user == null) {
				return logger.traceExit(Optional.empty());
			}
			return logger.traceExit(Optional.of(user));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL Exception occurred while getting user by id", e);
		}
		return logger.traceExit(Optional.empty());
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		logger.traceEntry("Getting user with username = {}", username);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(Optional.empty());
		}
		
		try {
			String sql = "select * from bank_users where username = ?;";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			
			User user = null;
			while(rs.next()) {
				user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
			}
			
			if(user == null) {
				return logger.traceExit(Optional.empty());
			}
			return logger.traceExit(Optional.of(user));
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL Exception occurred while getting a user by username", e);
		}
		
		return logger.traceExit(Optional.empty());
	}
	
	

	@Override
	public int loginUser(String username, String password) {
		logger.traceEntry("Attempting to login");
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int loginUserId = -1;
			
			CallableStatement cs = con.prepareCall("call createUser(?, ?, ?);");
			cs.setString(1, username);
			cs.setString(2, password);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);
			cs.execute();
			
			loginUserId = cs.getInt(3);
			
			return logger.traceExit(loginUserId);
		}
		catch(SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while attempting to login", e);
		}
		
		return logger.traceExit(-1);
	}

	@Override
	public int createUser(String username, String password) {
		logger.traceEntry("Creating user with username = {}", username);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int newUserId = -1;
			
			CallableStatement cs = con.prepareCall("call createUser(?, ?, ?);");
			cs.setString(1, username);
			cs.setString(2, password);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);
			cs.execute();
			
			newUserId = cs.getInt(3);
			
			return logger.traceExit(newUserId);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while creating a user", e);
		}
		
		return logger.traceExit(-1);
	}

	@Override
	public int deleteUser(int userId) {
		logger.traceEntry("Deleting user with userId = {}" , userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int success = -1;
			
			CallableStatement cs = con.prepareCall("call deleteUser(?, ?);");
			cs.setInt(1, userId);
			cs.registerOutParameter(2, java.sql.Types.INTEGER);
			cs.execute();
			
			success = cs.getInt(2);
			
			return logger.traceExit(success);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while deleting a user", e);
		}
		
		return logger.traceExit(-1);
	}

	@Override
	public int updateUserPassword(int userId,  String password) {
		logger.traceEntry("Updating user with userId = {}", userId);
		Connection con = ConnectionUtil.getConnection();
		
		if(con == null) {
			return logger.traceExit(-1);
		}
		
		try {
			int success = -1;
			
			CallableStatement cs = con.prepareCall("call updateUser(?, ?, ?);");
			cs.setInt(1, userId);
			cs.setString(2, password);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);
			cs.execute();
			success = cs.getInt(3);
			
			return logger.traceExit(success);
		}
		catch (SQLException e) {
			logger.catching(e);
			logger.error("SQL exception occurred while updating a user", e);
		}
		
		return logger.traceExit(-1);
	}
}
