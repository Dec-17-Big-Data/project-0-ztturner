package com.revature.jdbcbank.dao;

import java.util.List;
import java.util.Optional;

import com.revature.jdbcbank.models.User;

public interface UserDao {
	Optional<List<User>> getAllUsers();
	Optional<User> getUserById(int userId);
	Optional<User> getUserByUsername(String username);
	int loginUser(String username, String password);
	int createUser(String username, String password);
	int deleteUser(int userId);
	int updateUserPassword(int userId, String password);
}
