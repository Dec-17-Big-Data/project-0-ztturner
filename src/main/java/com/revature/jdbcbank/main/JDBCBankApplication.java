package com.revature.jdbcbank.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.jdbcbank.exceptions.*;
import com.revature.jdbcbank.menu.*;
import com.revature.jdbcbank.models.*;
import com.revature.jdbcbank.services.*;

public class JDBCBankApplication {
	private static final Logger logger = LogManager.getLogger(JDBCBankApplication.class);
	
	/**
	 * Main method for the JDBCBank application
	 * @param args
	 */
	public static void main(String[] args) {
		logger.traceEntry("Starting application.");
		Menu loginMenu = new Menu();
		int currentUserId = 0;
		int loginMenuIndex = 0;
		Scanner kb = new Scanner(System.in);
		
		UserService userService = UserService.getUserService();
		
		logger.info("Generating main menu");
		// create the main menu for the program
		try {
			loginMenu.addMenuItem(new MenuItem("Register as a user", "Register"));
			loginMenu.addMenuItem(new MenuItem("Login as a user", "User"));
			loginMenu.addMenuItem(new MenuItem("Login as a superuser", "Superuser"));
			loginMenu.addMenuItem(new MenuItem("Exit the program", "Exit"));
		}
		catch (DuplicateMenuInputException e) {
			logger.catching(e);
			logger.error("There is a duplicate required input in the menu", e);
			kb.close();
			return;
		}
		catch (InputDoesNotExistInDisplayException e) {
			logger.catching(e);
			logger.error("The input does not exist in the menu item", e);
			kb.close();
			return;
		}
		
		// user selects an option
		System.out.println("Welcome to the JDBCBank Application");
		while(loginMenuIndex != 4) {
			System.out.println("");
			System.out.println(loginMenu.toString());
			System.out.println("Select an option.");
			String input = kb.nextLine();
			
			String[] userInfo;
			int userId;
			
			loginMenuIndex = loginMenu.selectMenuItem(input);
			switch(loginMenuIndex) {
			// register a new user
			case 1:
				userInfo = getUserInfo(kb);
				try {
					userId = userService.createUser(userInfo[0], userInfo[1]);
					if(userId == -1) {
						System.out.println("Error while trying to create user. Please try again.");
					}
					else if(userId == 0) {
						System.out.println("The given username or password was not the appropriate length.");
					}
					else {
						currentUserId = userId;
						loggedInUserMenu(currentUserId, kb);
					}
				} 
				catch (IllegalArgumentException a) {
					logger.catching(a);
					System.out.println(a.getMessage());
				} 
				catch (ItemExistsException e) {
					logger.catching(e);
					System.out.println(e.getMessage());
				}
				
				currentUserId = 0; // userId is set to 0 after logging out
				break;
			// login as an existing user
			case 2:
				userInfo = getUserInfo(kb);
				try {
					userId = userService.loginUser(userInfo[0], userInfo[1]);
					
					if(userId == -1) {
						System.out.println("Could not login. Please try again");
					}
					else {
							currentUserId = userId;
							loggedInUserMenu(currentUserId, kb);
					}
				}
				catch (InvalidCredentialsException e) {
					logger.catching(e);
					System.out.println(e.getMessage());
				}
				
				currentUserId = 0; // userId is set to 0 after logging out
				break;
			// login as the superuser
			case 3:
				userInfo = getUserInfo(kb);
				
				try {
					int success = loginSuperUser(userInfo[0], userInfo[1]);
					
					if(success == -1) {
						System.out.println("Error while trying to login as a superuser. Please try again.");
					}
					else {
						loggedInSuperUserMenu(kb);
					}
				}
				catch (InvalidCredentialsException e) {
					logger.catching(e);
					System.out.println("Invalid username or password.");
				}
				break;
			// exit the application
			case 4:
				System.out.println("Exiting the application.");
				break;
			// invalid input
			default:
				System.out.println("Invalid selection.");
				break;
			}
		}
		
		kb.close(); // close the Scanner before exiting the application
		logger.traceExit();
	}
	
	/**
	 * Generates the logged in user menu and gives the user options to select from
	 * @param currentUserId - the ID of the logged in user
	 * @param kb - keyboard Scanner
	 */
	private static void loggedInUserMenu(int currentUserId, Scanner kb) {
		// only logged in users are allowed
		if(currentUserId <= 0) {
			logger.info("Invalid user id. Exiting logged in user menu");
			return;
		}
		
		// setup the logged in user menu
		Menu userMenu = new Menu();
		int menuIndex = 0;
		
		try {
			userMenu.addMenuItem(new MenuItem("View bank accounts", "Accounts"));
			userMenu.addMenuItem(new MenuItem("Create new bank account", "Create"));
			userMenu.addMenuItem(new MenuItem("Delete bank account", "Delete"));
			userMenu.addMenuItem(new MenuItem("Deposit into bank account", "Deposit"));
			userMenu.addMenuItem(new MenuItem("Withdraw from bank account", "Withdraw"));
			userMenu.addMenuItem(new MenuItem("Change password", "Change"));
			userMenu.addMenuItem(new MenuItem("View transactions", "Transactions"));
			userMenu.addMenuItem(new MenuItem("Logout", "Logout"));
		}
		catch (DuplicateMenuInputException e) {
			logger.catching(e);
			logger.error("There is a duplicate required input in the menu", e);
			return;
		}
		catch (InputDoesNotExistInDisplayException e) {
			logger.catching(e);
			logger.error("The input does not exist in the menu item", e);
			return;
		}
		
		// setup the services
		BankAccountService bankAccountService = BankAccountService.getBankAccountService();
		UserService userService = UserService.getUserService();
		TransactionService transactionService = TransactionService.getTransactionService();

		double amount = 0;
		Optional<List<BankAccount>> accountsWrapper;
		String input;
		String[] accountInfo;
		int accountId;
		String username = userService.getUserById(currentUserId).get().getUserName();
		
		System.out.println("Welcome, " + username);
		
		// user selects an option
		while(menuIndex != 8) {
			System.out.println("");
			System.out.println(userMenu.toString());
			System.out.println("Select an option.");
			input = kb.nextLine();
			menuIndex = userMenu.selectMenuItem(input);
			
			switch(menuIndex) {
			// view user bank accounts
			case 1:
				accountsWrapper = bankAccountService.getAllBankAccountsByUser(currentUserId);
				if(accountsWrapper.isPresent()) {
					List<BankAccount> accounts = accountsWrapper.get();
					if(accounts.size() == 0) {
						System.out.println("No existing accounts.");
					}
					else {
						for(BankAccount item : accounts) {
							System.out.println(item.toString());
						}
					}
				}
				else {
					System.out.println("Error while getting accounts.");
				}
				break;
				
			// create a new bank account
			case 2:
				try {
					accountInfo = getAccountInfo(kb);
					
					try {
						accountId = bankAccountService.createBankAccount(accountInfo[0], Double.parseDouble(accountInfo[1]), currentUserId);
						
						if(accountId == -1) {
							System.out.println("Could not create an account. Please try again.");
						}
						else {
							Optional<BankAccount> accountWrapper = bankAccountService.getBankAccountById(accountId);
							
							if(accountWrapper.isPresent()) {
								BankAccount account = accountWrapper.get();
								if(account != null) {
									System.out.println("Account successfully created");
								}
							}
							else {
								System.out.println("Count not create an account.");
							}
						}
					}
					catch (IllegalArgumentException e) {
						logger.catching(e);
						System.out.println(e.getMessage());
					}
					catch (ItemExistsException e) {
						logger.catching(e);
						System.out.println(e.getMessage());
					}
				}
				catch (NumberFormatException f) {
					logger.catching(f);
					System.out.println("The given input was not a valid number.");
				}
				break;
				
			// delete a bank account
			case 3:
				accountsWrapper = bankAccountService.getAllBankAccountsByUser(currentUserId);
				
				if(accountsWrapper.isPresent()) {
					List<BankAccount> accounts = accountsWrapper.get();
					
					if(accounts.size() == 0) {
						System.out.println("No existing accounts.");
					}
					else {
						int accountIndex = selectItem(accounts, "account", kb);
						
						if(accountIndex == -1) {
							System.out.println("Invalid account selection.");
						}
						else {
							try {
								int success = bankAccountService.deleteBankAccount(accounts.get(accountIndex - 1).getAccountId());
								
								if(success == -1) {
									System.out.println("Could not delete the account. Please try again.");
								}
								
								if(success == 1) {
									System.out.println("Account successfully deleted");
								}
							} 
							catch (ItemNotFoundException e) {
								logger.catching(e);
								System.out.println("Account not found.");
							} 
							catch (AccountNotEmptyException e) {
								logger.catching(e);
								System.out.println("Chosen account is not empty. Accounts must be emptied before being able to be deleted.");
							}
						}
					}
				}
				else {
					System.out.println("Error while getting accounts");
				}
				break;
				
			// make a deposit to a bank account
			case 4:
				accountsWrapper = bankAccountService.getAllBankAccountsByUser(currentUserId);
				if(accountsWrapper.isPresent()) {
					List<BankAccount> accounts = accountsWrapper.get();
					
					if(accounts.size() == 0) {
						System.out.println("No existing accounts.");
					}
					else {
						int accountIndex = selectItem(accounts, "account", kb);
						
						if(accountIndex == -1) {
							System.out.println("Invalid account selection.");
						}
						else {
							try {
								amount = getAmount(kb);
								int success = bankAccountService.makeDeposit(accounts.get(accountIndex - 1).getAccountId(), amount);
								
								if(success == -1) {
									System.out.println("Could not make a deposit. Please try again.");
								}
								
								if(success == 1) {
									System.out.println("Deposit successfully completed");
								}
							} 
							catch (ItemNotFoundException e) {
								System.out.println("Account not found.");
							} 
							catch (NumberFormatException n) {
								System.out.println("Input cannot be converted.");
							}
							catch (IllegalArgumentException a) {
								System.out.println(a.getMessage());
							}							
						}
					}
				}
				else {
					System.out.println("Error while getting accounts.");
				}
				break;
				
			// make a withdrawal from a bank account
			case 5:
				accountsWrapper = bankAccountService.getAllBankAccountsByUser(currentUserId);
				if(accountsWrapper.isPresent()) {
					List<BankAccount> accounts = accountsWrapper.get();
					
					if(accounts.size() == 0) {
						System.out.println("No existing accounts.");
					}
					else {
						int accountIndex = selectItem(accounts, "account", kb);
						
						if(accountIndex == -1) {
							System.out.println("Invalid account selection.");
						}
						else {
							try {
								amount = getAmount(kb);
								int success = bankAccountService.makeWithdrawal(accounts.get(accountIndex - 1).getAccountId(), amount);
								
								if(success == -1) {
									System.out.println("Error while making a withdrawal. Please try again.");
								}
								else if (success == 0) {
									System.out.println("Withdrawal failed");
								}
								else {
									System.out.println("Withdrawal successfully completed");
								}
							} 
							catch (ItemNotFoundException e) {
								System.out.println("Account not found.");
							} 
							catch (NumberFormatException n) {
								System.out.println("Input cannot be converted.");
							}
							catch (IllegalArgumentException a) {
								System.out.println(a.getMessage());
							}
							catch (OverdraftException o) {
								System.out.println("Insufficent funds to make the withdrawal.");
							}
						}
					}
				}
				else {
					System.out.println("Error while getting accounts");
				}
				break;
				
			// change user's password
			case 6:
				System.out.println("Enter a password (must contain no whitespace, between 8 and 50 characters):");
				input = kb.nextLine();
				
				try {
					int success = userService.updateUserPassword(currentUserId, input);
					
					if(success == -1) {
						System.out.println("Error while trying to change password. Please try again.");
					}
					else if(success == 0) {
						System.out.println("Input password was not long enough. Password must be between 8 and 50 characters long");
					}
					else {
						System.out.println("Password successfully changed.");
					}
				}
				catch (IllegalArgumentException e) {
					logger.catching(e);
					System.out.println(e.getMessage());
				}
				catch (ItemNotFoundException e) {
					logger.catching(e);
					System.out.println(e.getMessage());
				}
				break;
				
			// view user's transactions
			case 7:
				logger.info("Viewing the user's transactions");
				Optional<List<Transaction>> transactionsWrapper = transactionService.getAllTransactionsByUser(currentUserId);
				
				if(transactionsWrapper.isPresent()) {
					List<Transaction> transactions = transactionsWrapper.get();
					
					if(transactions.size() == 0) {
						System.out.println("No existing transactions.");
					}
					else {
						for(Transaction item : transactions) {
							System.out.println(item.toString());
						}
					}
				}
				else {
					System.out.println("Error while getting the transactions. Please try again");
				}
				break;
				
			// logout
			case 8:
				logger.info("Logging out");
				System.out.println("Logging out.");
				break;
			// invalid selection
			default:
				logger.info("Invalid menu selection");
				System.out.println("Invalid menu selection.");
				break;
			}
		}
	}
	
	/**
	 * Generates the superuser menu and gives the user options to select from
	 * @param kb - keyboard Scanner
	 */
	private static void loggedInSuperUserMenu(Scanner kb) {
		logger.traceEntry("Generating superuser menu");
		Menu superUserMenu = new Menu();
		int menuIndex = 0;
		
		// Generate the superuser menu
		try {
			superUserMenu.addMenuItem(new MenuItem("View all users", "View"));
			superUserMenu.addMenuItem(new MenuItem("Create a user", "Create"));
			superUserMenu.addMenuItem(new MenuItem("Change a user's password", "Change"));
			superUserMenu.addMenuItem(new MenuItem("Delete a user", "Delete"));
			superUserMenu.addMenuItem(new MenuItem("Logout", "Logout"));
		}
		catch (DuplicateMenuInputException e) {
			logger.catching(e);
			logger.error("There is a duplicate required input in the menu", e);
			return;
		}
		catch (InputDoesNotExistInDisplayException e) {
			logger.catching(e);
			logger.error("The input does not exist in the menu item", e);
			return;
		}
		
		Optional<List<User>> usersWrapper;
		List<User> users;
		UserService userService = UserService.getUserService();
		String input;
		String[] userInfo;
		
		// User selects an option
		while(menuIndex != 5) {
			System.out.println("");
			System.out.println(superUserMenu.toString());
			System.out.println("Select an option.");
			input = kb.nextLine();
			menuIndex = superUserMenu.selectMenuItem(input);
			
			switch(menuIndex) {
			// view all users
			case 1:
				usersWrapper = userService.getAllUsers();
				if(usersWrapper.isPresent()) {
					users = usersWrapper.get();
					if(users.size() == 0) {
						System.out.println("No existing users.");
					}
					else {
						for(User user : users) {
							System.out.println(user.toString());
						}
					}
				}
				else {
					System.out.println("Error occurred while getting users. Please try again.");
				}
				break;
			// create a user
			case 2:
				userInfo = getUserInfo(kb);
				try {
					int newUserId = userService.createUser(userInfo[0], userInfo[1]);
					
					if(newUserId == -1) {
						System.out.println("Error while creating user. Please try again.");
					}
					else if(newUserId == 0) {
						System.out.println("The given username or password was not the appropriate length.");
					}
					else {
						Optional<User> newUser = userService.getUserById(newUserId);
						if(newUser.isPresent()) {
							System.out.println("The user was successfully created.");
						}
						else {
							System.out.println("User could not be created.");
						}
					}
				} catch (IllegalArgumentException e) {
					logger.catching(e);
					System.out.println(e.getMessage());
				} catch (ItemExistsException e) {
					logger.catching(e);
					System.out.println(e.getMessage());
				}
				break;
			// change a user's password
			case 3:
				usersWrapper = userService.getAllUsers();
				
				if(usersWrapper.isPresent()) {
					users = usersWrapper.get();
					
					if(users.size() == 0) {
						System.out.println("No existing users.");
					}
					else {
						int userIndex = selectItem(users, "user", kb);
						
						if(userIndex == -1) {
							System.out.println("Invalid input");
						}
						else {
							System.out.println("Enter a password (must contain no whitespace, between 8 and 50 characters):");
							input = kb.nextLine();
							
							try {
								int success = userService.updateUserPassword(users.get(userIndex - 1).getUserId(), input);
								
								if(success == -1) {
									System.out.println("Error occurred while changing password");
								}
								else if(success == 0) {
									System.out.println("Input password was not long enough. Password must be between 8 and 50 characters long");
								}
								else {
									System.out.println("Password successfully changed");
								}
							}
							catch (IllegalArgumentException e) {
								logger.catching(e);
								System.out.println(e.getMessage());
							}
							catch (ItemNotFoundException e) {
								logger.catching(e);
								System.out.println(e.getMessage());
							}
						}
					}
				}
				else {
					System.out.println("Error while getting users. Please try again.");
				}
				break;
			// delete a user
			case 4:
				usersWrapper = userService.getAllUsers();
				
				if(usersWrapper.isPresent()) {
					users = usersWrapper.get();
					
					if(users.size() == 0) {
						System.out.println("No existing users.");
					}
					else {
						int userIndex = selectItem(users, "user", kb);
						
						if(userIndex == -1) {
							System.out.println("Invalid input");
						}
						else {
							try {
								int success = userService.deleteUser(users.get(userIndex - 1).getUserId());
								
								if(success == -1) {
									System.out.println("Error while trying to delete user. Please try again.");
								}
								else if(success == 0) {
									System.out.println("User deletion failed.");
								}
								else {
									System.out.println("User successfully deleted.");
								}
							} catch (ItemNotFoundException e) {
								logger.catching(e);
								System.out.println("User not found.");
							}
						}
					}
				}
				else {
					System.out.println("Error while getting users. Please try again.");
				}
				break;
			// log out
			case 5:
				System.out.println("Logging out.");
				break;
			// invalid selection
			default:
				System.out.println("Invalid selection.");
				break;
			}
		}
	}
	
	/**
	 * Gets the user's username and password from the user
	 * @param kb - keyboard Scanner
	 * @return the input user info
	 */
	private static String[] getUserInfo(Scanner kb) {
		logger.traceEntry("Getting user Info");
		String[] userInfo = new String[2];
		
		System.out.println("Enter a username (must contain no whitespace, be non-empty, and less than 50 characters):");
		userInfo[0] = kb.nextLine();
		System.out.println("Enter a password (must contain no whitespace, between 8 and 50 characters):");
		userInfo[1] = kb.nextLine();
		
		return logger.traceExit(userInfo);
	}
	
	/**
	 * Gets the account name and initial balance from the user
	 * @param kb - keyboard Scanner
	 * @return the input account info
	 * @throws NumberFormatException - thrown if the initial balance input cannot be converted into a double
	 */
	private static String[] getAccountInfo(Scanner kb) throws NumberFormatException {
		logger.traceEntry("Getting account info");
		String[] accountInfo = new String[2];
		
		System.out.println("Enter the idenifier for this account (must contain no whitespace, be non-empty, and less than 50 characters)");
		accountInfo[0] = kb.nextLine();
		System.out.println("Enter the initial balance for this account");
		accountInfo[1] = kb.nextLine();
		
		// check if the initial balance can be converted to a double
		Double.parseDouble(accountInfo[1]);
		return logger.traceExit(accountInfo);
	}
	
	/**
	 * Gets the amount that the user wants to deposit or withdraw
	 * @param kb - keyboard Scanner
	 * @return the input converted to a double
	 * @throws NumberFormatException - thrown if the user's input cannot be converted to a double
	 */
	private static double getAmount(Scanner kb) throws NumberFormatException {
		logger.traceEntry("Getting amount value");
		String input;
		double amount;
		
		System.out.println("Type in the amount to deposit/withdraw");
		input = kb.nextLine();
		
		// check if the amount can be converted to a double
		amount = Double.parseDouble(input);
		return logger.traceExit(amount);
	}
	
	/**
	 * Takes a list of items and generates the display to select an item and return the item selected
	 * @param items - the list of items to display
	 * @param typeName - the type of the items
	 * @param kb - keyboard Scanner
	 * @return the selected item's index or -1 for invalid input
	 */
	private static <T> int selectItem(List<T> items, String typeName, Scanner kb) {
		logger.traceEntry("Generating display from list of items.");
		String input;
		int selectedIndex = -1;
		int index = 0;
		for(T item : items) {
			index++;
			System.out.println(index + ". " + item.toString());
		}
		
		System.out.println("Select the " + typeName + " you want to modify.");
		input = kb.nextLine();
		
		try {
			selectedIndex = Integer.parseInt(input);	
			if(selectedIndex <= 0 || selectedIndex > items.size()) {
				selectedIndex = -1;
			}
		}
		catch (NumberFormatException e) {			
		}
		
		return logger.traceExit(selectedIndex);
	}
	
	/**
	 * Attempts to log in as the superuser using the given input and comparing against the string in the properties file
	 * @param loginUsername - the input login username
	 * @param loginPassword - the input login password
	 * @return - whether or not the login attempt was successful
	 * @throws InvalidCredentialsException - if the login username or password was incorrect
	 */
	private static int loginSuperUser(String loginUsername, String loginPassword) throws InvalidCredentialsException {
		logger.traceEntry("Attempting login to superuser");
		InputStream in = null;
		int success = -1;
		
		try {
			Properties props = new Properties();
			in = new FileInputStream("src\\main\\resources\\superuser.properties");
			props.load(in);
			
			String actualUsername = props.getProperty("jdbcbank.superusername");
			String actualPassword = props.getProperty("jdbcbank.superuserpassword");
			
			if(loginUsername.compareTo(actualUsername) == 0 && loginPassword.compareTo(actualPassword) == 0) {
				success = 1;
			}
			else {
				throw new InvalidCredentialsException("Invalid username or password");
			}
		}
		catch(FileNotFoundException f) {
			logger.catching(f);
			logger.error("Specified properties file could not be found", f);
		}
		catch(IOException e) {
			logger.catching(e);
			logger.error("Properties file failed to be read", e);
		}
		finally {
			try {
				in.close();
			} 
			catch (IOException e) {
				
			}
		}
		
		return logger.traceExit(success);
	}
}
