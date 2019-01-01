package com.revature.jdbcbank.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import com.revature.jdbcbank.exceptions.DuplicateMenuInputException;

public class Menu {
	private List<MenuItem> items;
	private Map<String, Integer> requiredInputMap;
	
	public Menu() {
		this.items = new ArrayList<MenuItem>();
		requiredInputMap = new HashMap<String, Integer>();
	}
	
	public Menu(List<MenuItem> items) throws DuplicateMenuInputException {
		Map<String, Integer> tempInputMap = new HashMap<String, Integer>();
		String currentRequiredInput;
		int interfaceItemIndex = 1; // start at 1 for the user interface
		
		for(MenuItem item : items) {
			currentRequiredInput = item.getRequiredInputString();
			if(tempInputMap.containsKey(currentRequiredInput)) {
				throw new DuplicateMenuInputException("List of menu items contains two items with the same required input string.");
			}
			
			tempInputMap.put(currentRequiredInput, interfaceItemIndex);
			interfaceItemIndex++; // increment the interface index
		}
		
		this.items = items;
		this.requiredInputMap = tempInputMap;
	}
	
	public void addMenuItem(MenuItem item) throws DuplicateMenuInputException {		
		String requiredInput = item.getRequiredInputString();
		if(requiredInputMap.containsKey(requiredInput)) {
			throw new DuplicateMenuInputException("Menu already contains an item with this required input string.");
		}
		
		items.add(item);
		requiredInputMap.put(requiredInput, items.size());
	}
	
	public int selectMenuItem(String input) {
		
		int itemIndex = -1, matches = 0;
		boolean inputNotInt = false;
		// try to parse an integer from the input
		try {
			itemIndex = Integer.parseInt(input);
		}
		catch (NumberFormatException e) {
			inputNotInt = true;
		}
		
		// if the input could be parsed as an integer, make sure the integer is between 1 and the menu's size
		if(!inputNotInt) {
			if(itemIndex < 1 || itemIndex > items.size()) {
				itemIndex = -1;
			}
		}
		// else, check the menu's required input for the index
		else {
			String upperCaseInput = input.toUpperCase();
			Pattern inputPattern = Pattern.compile("\b" + upperCaseInput);
			
			for(MenuItem item : items) {
				Matcher inputMatcher = inputPattern.matcher(item.getRequiredInputString());		
				
				// if the input matches with a menu item
				if(inputMatcher.find()) {
					// if no matches have been made before, note the index and continue to make sure the input won't match multiple items
					if(matches == 0) {
						itemIndex = requiredInputMap.get(item.getRequiredInputString());
						matches++;
					}
					// else, make the index the not found index and break out of the loop
					else {
						itemIndex = -1;
						matches++;
						break;
					}
				}
			}
		}		
		return itemIndex;
	}
	
	@Override
	public String toString() {
		int index = 0;
		StringBuilder result = new StringBuilder();
		
		for(MenuItem item : items) {
			index++;
			
			result.append(index + ". " + item.toString() + "\n");
		}
		
		return result.toString();
	}
}
