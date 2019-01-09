package com.revature.jdbcbank.menu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.revature.jdbcbank.exceptions.InputDoesNotExistInDisplayException;;

public class MenuItem {
	private String displayString; // string that will be displayed in the menu
	private String requiredInputString; // string that will select this item in the menu
	private static final Logger logger = LogManager.getLogger(MenuItem.class);
	
	/**
	 * Creates a menu item from a display string and input string.
	 * @param displayString - the string that will be displayed to the user
	 * @param requiredInputString - the string that the user must type if they don't use an integer
	 * @throws InputDoesNotExistInDisplayException - thrown when the input string doesn't exist in the display string
	 */
	public MenuItem(String displayString, String requiredInputString) throws InputDoesNotExistInDisplayException {
		super();
		logger.traceEntry("Creating menu item with display string = {} and required input string = {}", displayString, requiredInputString);
		String tempDisplayString = displayString.toUpperCase();
		StringBuilder inputBuilder = new StringBuilder(requiredInputString.toUpperCase());
		
		// remove backslashes from the input
		for(int c = 0; c < inputBuilder.length(); c++) {
			if(inputBuilder.charAt(c) == '\\') {
				inputBuilder.deleteCharAt(c);
				c--;
			}
		}
		
		String tempInputString = inputBuilder.toString();
		
		Matcher inputStringMatcher = Pattern.compile("\\b" + tempInputString + "\\b").matcher(tempDisplayString); // input string must be the whole word
		
		if(!inputStringMatcher.find()) {
			throw new InputDoesNotExistInDisplayException("Input string does not exist in the display string.");
		}
		
		int startIndexOfInput = inputStringMatcher.start(), endIndexOfInput = inputStringMatcher.end();
		
		String newDisplayString = displayString.substring(0, startIndexOfInput) + tempInputString + displayString.substring(endIndexOfInput);
		this.displayString = newDisplayString;
		this.requiredInputString = tempInputString;
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public String getRequiredInputString() {
		return requiredInputString;
	}

	public void setRequiredInputString(String requiredInputString) {
		this.requiredInputString = requiredInputString;
	}
	
	@Override
	public String toString() {
		return displayString;
	}
}
