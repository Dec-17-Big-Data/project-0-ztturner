package com.revature.jdbcbank.menuTest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.jdbcbank.exceptions.DuplicateMenuInputException;
import com.revature.jdbcbank.menu.Menu;
import com.revature.jdbcbank.menu.MenuItem;

public class MenuTest {
	private static Menu validMenu;
	@BeforeClass
	public static void setup() {
		validMenu = new Menu();
		
		validMenu.addMenuItem(new MenuItem("Test Item", "Test"));
		validMenu.addMenuItem(new MenuItem("Next Item", "Next"));
	}
	
	@Test
	public void testCreatingMenuAndAddingItems() {
		Menu menu = new Menu();
		
		menu.addMenuItem(new MenuItem("Test Item", "Test"));
		menu.addMenuItem(new MenuItem("Next Item", "Next"));
		
		assertEquals(2, menu.getSize());
		String expectedString = "1. TEST Item\n2. NEXT Item\n";
		
		assertEquals(expectedString, menu.toString());
	}
	
	@Test(expected=DuplicateMenuInputException.class)
	public void testDuplicateMenuInputs() {
		Menu menu = new Menu();
		
		menu.addMenuItem(new MenuItem("Test Item", "Item"));
		menu.addMenuItem(new MenuItem("Next Item", "Item"));
	}
	
	@Test
	public void testCreatingMenuFromExistingItemsList() {
		List<MenuItem> items = new ArrayList<MenuItem>();
		
		items.add(new MenuItem("Test Item", "Test"));
		items.add(new MenuItem("Next Item", "Next"));
		
		Menu menu = new Menu(items);
		
		assertEquals(2, menu.getSize());
	}
	
	@Test(expected=DuplicateMenuInputException.class)
	public void testCreatingMenuFromExistingListDuplicateInput() {
		List<MenuItem> items = new ArrayList<MenuItem>();
		
		items.add(new MenuItem("Test Item", "Item"));
		items.add(new MenuItem("Next Item", "Item"));
		
		Menu menu = new Menu(items);
	}
	
	@Test
	public void testSelectingWithValidInteger() {		
		String input = "1";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
		
		selectedIndex = validMenu.selectMenuItem("2");
		
		assertEquals(2, selectedIndex);
	}
	
	@Test
	public void testSelectingWithIntegerZero() {		
		String input = "000";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithNegativeInteger() {	
		String input = "-1";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithIntegerBeyondSize() {		
		String input = "3";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithDecimal() {
		String input = "1.0";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithMixedNumbersAndCharacters() {		
		String input = "209fssef09se";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithFullInputExactCase() {
		String input = "Test";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithFullInputMixedCase() {		
		String input = "TeSt";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithFullInputLowerCase() {		
		String input = "test";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithFullInputUpperCase() {		
		String input = "TEST";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithValidInputIncludingBackslash() {
		String input = "T\\e\\s\\t";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithPartialInputExactCase() {
		String input = "Tes";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithPartialInputMixedCase() {
		String input = "TeS";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithPartialInputLowerCase() {
		String input = "tes";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithPartialInputUpperCase() {
		String input = "TES";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(1, selectedIndex);
	}
	
	@Test
	public void testSelectingWithWrongSpelling() {
		String input = "Tset";
		
		int selectedIndex = validMenu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
	
	@Test
	public void testSelectionInputEvaluatesToMultipleOptions() {
		Menu menu = new Menu();
		
		menu.addMenuItem(new MenuItem("Deposit amount", "Deposit"));
		menu.addMenuItem(new MenuItem("Delete account", "Delete"));
		
		String input = "De";
		
		int selectedIndex = menu.selectMenuItem(input);
		
		assertEquals(-1, selectedIndex);
	}
}
