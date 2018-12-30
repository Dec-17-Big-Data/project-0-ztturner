package com.revature.jdbcbank.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
