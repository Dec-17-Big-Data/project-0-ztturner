package com.revature.jdbcbank.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import com.revature.jdbcbank.exceptions.DuplicateMenuInputException;

public class Menu {
	private static final Logger logger = LogManager.getLogger(Menu.class);
	private List<MenuItem> items;
	private Map<String, Integer> requiredInputMap;
	
	/**
	 * Creates an empty menu.
	 */
	public Menu() {
		logger.traceEntry("Creating empty menu");
		this.items = new ArrayList<MenuItem>();
		requiredInputMap = new HashMap<String, Integer>();
	}
	
	/**
	 * Creates a menu from a list of items.
	 * @param items - the items to add to the menu
	 * @throws DuplicateMenuInputException - thrown if two items contain the same required input string
	 */
	public Menu(List<MenuItem> items) throws DuplicateMenuInputException {
		logger.traceEntry("Creating menu from list");
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
	
	/**
	 * Adds a given menu item to the menu.
	 * @param item - the item to be added
	 * @throws DuplicateMenuInputException - thrown if the new item has required input that is equal to an existing menu item
	 */
	public void addMenuItem(MenuItem item) throws DuplicateMenuInputException {
		logger.traceEntry("Adding menu item with display string = {} and required input string = {}", item.getDisplayString(), item.getRequiredInputString());
		String requiredInput = item.getRequiredInputString();
		
		if(requiredInputMap.containsKey(requiredInput)) {
			throw new DuplicateMenuInputException("Menu already contains an item with this required input string.");
		}
		
		items.add(item);
		requiredInputMap.put(requiredInput, items.size());
	}
	
	/**
	 * Selects an item from the menu based on the user's input.
	 * @param input - the input to evaluate
	 * @return the index of the item if a match is found, -1 if not found
	 */
	public int selectMenuItem(String input) {
		logger.traceEntry("Selecting menu item with input = {}", input);
		int itemIndex = -1;
		int matches = 0;
		boolean inputNotInt = false;
		// try to parse an integer from the input
		try {
			itemIndex = Integer.parseInt(input);
		}
		catch (NumberFormatException e) {
			logger.catching(e);
			inputNotInt = true;
		}

		if(!inputNotInt) {
			if(itemIndex < 1 || itemIndex > items.size()) {
				itemIndex = -1;
			}
		}
		else {
			String upperCaseInput = input.toUpperCase();
			StringBuilder inputBuilder = new StringBuilder(upperCaseInput);
			
			// remove backslash characters from the input
			for(int c = 0; c < inputBuilder.length(); c++) {
				if(inputBuilder.charAt(c) == '\\') {
					inputBuilder.deleteCharAt(c);
					c--;
				}
			}
			
			Pattern inputPattern = Pattern.compile("\\b" + inputBuilder.toString());
			
			for(MenuItem item : items) {
				String upperCaseRequiredInput = item.getRequiredInputString();
				Matcher inputMatcher = inputPattern.matcher(upperCaseRequiredInput);		
				
				// if the input matches with a menu item
				if(inputMatcher.find()) {
					// if no matches have been made before, note the index and continue to make sure the input won't match multiple items
					if(matches == 0) {
						itemIndex = requiredInputMap.get(upperCaseRequiredInput);
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
	
	/**
	 * Returns the number of items in the menu.
	 * @return the number of items in the menu
	 */
	public int getSize() {
		return items.size();
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
