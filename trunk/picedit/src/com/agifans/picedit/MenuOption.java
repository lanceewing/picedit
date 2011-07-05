package com.agifans.picedit;

import java.util.HashMap;
import java.util.Map;

/**
 * An Enum presenting the possible menu selections.
 * 
 * @author Lance Ewing
 */
public enum MenuOption {

    ABOUT("About"), 
    HELP("Help"), 
    NEW_PICTURE("New Picture"), 
    SAVE_PICTURE("Save Picture"), 
    LOAD_PICTURE("Load Picture"), 
    QUIT("Quit"), 
    VIEW_DATA("View Data"), 
    ZOOM_X2("Zoom x2"), 
    ZOOM_X3("Zoom x3"), 
    ZOOM_X4("Zoom x4"), 
    ZOOM_X5("Zoom x5"), 
    BACKGROUND("Background"), 
    BANDS("Bands"), 
    DUAL_MODE("Dual Mode");

    // Holds mapping between the display values and the MenuOption it belongs to.
    private static Map<String, MenuOption> displayValueMap;
    static {
        displayValueMap = new HashMap<String, MenuOption>();
        for (MenuOption menuOption : MenuOption.values()) {
            displayValueMap.put(menuOption.displayValue, menuOption);
        }
    }
    
    /**
     * The name of the option as it appears in the menu system.
     */
    private String displayValue;

    /**
     * Constructor for MenuOption.
     * 
     * @param displayValue The display value of the option as it appears in the menu system.
     */
    MenuOption(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * Gets the display value for this MenuOption.
     *  
     * @return The display value for this MenuOption.
     */
    public String getDisplayValue() {
      return displayValue;
    }
    
    /**
     * Gets the MenuOption that has a display value that matches the given value.
     * 
     * @param displayValue The display value to get the MenuOption for.
     * 
     * @return The matching MenuOption.
     */
    public static MenuOption getMenuOption(String displayValue) {
        return displayValueMap.get(displayValue);
    }
}
