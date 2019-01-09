package com.revature.jdbcbank.menuTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.revature.jdbcbank.exceptions.InputDoesNotExistInDisplayException;
import com.revature.jdbcbank.menu.MenuItem;

public class MenuItemTest {
	@Test
	public void testCreateMenuItemValidInput() {
		MenuItem item = new MenuItem("Test item", "Test");
		
		assertEquals("TEST item", item.getDisplayString());
		
		assertEquals("TEST", item.getRequiredInputString());
	}
	
	@Test(expected=InputDoesNotExistInDisplayException.class)
	public void testCreateMenuItemInputNotInDisplay() {
		MenuItem item = new MenuItem("Test item", "Show");
	}
	
	@Test(expected=InputDoesNotExistInDisplayException.class)
	public void testCreateMenuItemInputPartOfWord() {
		MenuItem item = new MenuItem("Testing item", "Test");
	}
	
	@Test
	public void testCreateMenuItemBackslashInDisplay() {
		MenuItem item = new MenuItem("Test deposit\\withdraw", "Test");
		
		assertEquals("TEST deposit\\withdraw", item.getDisplayString());
		assertEquals("TEST", item.getRequiredInputString());
	}
	
	@Test
	public void testCreateMenuItemBackslashInInput() {
		MenuItem item = new MenuItem("Test item", "T\\es\\t");
		
		assertEquals("TEST item", item.getDisplayString());
		assertEquals("TEST", item.getRequiredInputString());
	}
}
