package com.revature.jdbcbank.menu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.revature.jdbcbank.exceptions.InputDoesNotExistInDisplayException;;

public class MenuItem {
	private String displayString; // string that will be displayed in the menu
	private String requiredInputString; // string that will select this item in the menu
	
	public MenuItem(String displayString, String requiredInputString) throws InputDoesNotExistInDisplayException {
		super();
		String tempDisplayString = displayString.toUpperCase();
		String tempInputString = requiredInputString.toUpperCase();
		Matcher inputStringMatcher = Pattern.compile("\b" + tempInputString + "\b").matcher(tempDisplayString);
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
