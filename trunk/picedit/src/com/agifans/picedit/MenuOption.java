package com.agifans.picedit;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * An Enum presenting the possible menu selections.
 * 
 * @author Lance Ewing
 */
public enum MenuOption {

	// This is effectively the menu system configuration.
	ABOUT("About", 0, 0, 2, new Rectangle(0, 0, 44, 8), new Rectangle(8, 16, 40, 8)),
	HELP("Help", 0, 1, 2, null, new Rectangle(8, 24, 40, 8)),
	NEWPIC("New Picture", 1, 0, 4, new Rectangle(45, 0, 40, 8), new Rectangle(48, 16, 96, 8)),
	SAVEPIC("Save Picture", 1, 1, 4, null, new Rectangle(48, 24, 96, 8)),
	LOADPIC("Load Picture", 1, 2, 4, null, new Rectangle(48, 32, 96, 8)),
	QUIT("Quit", 1, 3, 4, null, new Rectangle(48, 40, 96, 8)),
	VIEWDATA("View Data", 2, 0, 5, new Rectangle(84, 0, 40, 8), new Rectangle(88, 16, 72, 8)),
	ZOOMX2("Zoom x2", 2, 1, 5, null, new Rectangle(88, 24, 72, 8)),
	ZOOMX3("Zoom x3", 2, 2, 5, null, new Rectangle(88, 32, 72, 8)),
	ZOOMX4("Zoom x4", 2, 3, 5, null, new Rectangle(88, 40, 72, 8)),
	ZOOMX5("Zoom x5", 2, 4, 5, null, new Rectangle(88, 48, 72, 8)),
	BACKGROUND("Background", 3, 0, 1, new Rectangle(124, 0, 196, 8), new Rectangle(136, 16, 80, 8));

	private String displayName;
	private int barOption;
	private int itemOption;
	private int maxItem;
	private Rectangle barBox;
	private Rectangle itemBox;
	
	/**
	 * Constructor for MenuOption.
	 * 
	 * @param displayName the name of the option as it appears in the menu system.
	 * @param barOption a code for a related group of menu items, i.e. the top level menu bar category. 
	 * @param itemOption a code for a specific menu item.
	 * @param maxItem the number of items within this category of items.
	 * @param barBox the bounding box for the top level category item on the menu bar (so mouse clicks can be matched to menu bar groups).
	 * @param itemBox the bounding box for the menu item (so mouse clicks can be matched to menu items).
	 */
	MenuOption(String displayName, int barOption, int itemOption, int maxItem, Rectangle barBox, Rectangle itemBox) {
		this.displayName = displayName;
		this.barOption = barOption;
		this.itemOption = itemOption;
		this.maxItem = maxItem;
		this.barBox = barBox;
		this.itemBox = itemBox;
	}
	
	public int getBarOption() {
		return barOption;
	}
	
	public int getItemOption() {
		return itemOption;
	}
	
	public int getMaxItem() {
		return maxItem;
	}
	
	public Rectangle getBarBox() {
		return barBox;
	}
	
	public Rectangle getItemBox() {
		return itemBox;
	}
	
	/**
	 * Gets the MenuOption that matches the given mouse point.
	 * 
	 * @param point the position of the mouse event.
	 * @param lastMenuOption the last menu option that was in effect.
	 * 
	 * @return the associated MenuOption or null if there is none.
	 */
	public static MenuOption getMenuOption(Point point, MenuOption lastMenuOption) {
		MenuOption menuOption = null;
		
		for (MenuOption value : MenuOption.values()) {
			if ((value.barBox != null) && value.barBox.contains(point)) {
				menuOption = value;
				break;
			} else if (value.itemBox.contains(point) && ((lastMenuOption == null) || (lastMenuOption.barOption == value.barOption))) {
				menuOption = value;
				break;
			}
		}
		
		return menuOption;
	}
	
	/**
	 * Gets the MenuOption that matches the given bar option and item option. This
	 * method is here to support selection of a menu item using key events from 
	 * navigation keys.
	 * 
	 * @param barOption the bar option.
	 * @param itemOption the item option.
	 * 
	 * @return the matching MenuOption. 
	 */
	public static MenuOption getMenuOption(int barOption, int itemOption) {
		MenuOption menuOption = null;
		
		int optionCode = ((barOption * 0x10) + itemOption);
		switch (optionCode) {
			case 0x00:
				menuOption = ABOUT;
				break;
			case 0x01:
				menuOption = HELP;
				break;
			case 0x10:
				menuOption = NEWPIC;
				break;
			case 0x11:
				menuOption = SAVEPIC;
				break;
			case 0x12:
				menuOption = LOADPIC;
				break;
			case 0x13:
				menuOption = QUIT;
				break;
			case 0x20:
				menuOption = VIEWDATA;
				break;
			case 0x30:
				menuOption = BACKGROUND;
				break;
		}
		
		return menuOption;
	}
	
	public String toString() {
		return displayName;
	}
}
