package com.agifans.picedit.picture;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum representing the different types of picture action code, which are those
 * picture codes 0xF0 and above.
 * 
 * @author Lance Ewing
 */
public enum PictureActionCodeType {

    SET_VISUAL_COLOR(0xF0, "Visual Color"),
    TURN_VISUAL_OFF(0xF1, "Visual Off"),
    SET_PRIORITY_COLOR(0xF2, "Priority Color"),
    TURN_PRIORITY_OFF(0XF3, "Priority Off"),
    STEP_VERTICAL_FIRST(0xF4, "Step Line"),
    STEP_HORIZONTAL_FIRST(0xF5, "Step Line"),
    LINE(0xF6, "Line"),
    SHORT_LINE(0xF7, "Short Line"),
    FILL(0xF8, "Fill"),
    BRUSH_CODE(0xF9, "Set Brush"),
    BRUSH_PLOT(0xFA, "Plot Brush"),
    END(0xFF, "End");

    /**
     * The picture code associated with this PictureCodeType.
     */
    private int actionCode;
    
    /**
     * The text to display for this PictureCodeType.
     */
    private String displayableText;
    
    /**
     * A Map of PictureCodeTypes keyed by the picture action code.
     */
    static Map<Integer, PictureActionCodeType> pictureCodeTypes = new HashMap<Integer, PictureActionCodeType>();
    static {
        for (PictureActionCodeType pictureCodeType : PictureActionCodeType.values()) {
            pictureCodeTypes.put(pictureCodeType.actionCode, pictureCodeType);
        }
    }
    
    /**
     * Constructor for PictureCodeType.
     * 
     * @param actionCode The picture code associated with this PictureCodeType.
     * @param displayableText The text to display for this PictureCodeType.
     */
    PictureActionCodeType(int actionCode, String displayableText) {
        this.actionCode = actionCode;
        this.displayableText = displayableText;
    }

    /**
     * Gets the text to display for this PictureCodeType.
     * 
     * @return The text to display for this PictureCodeType.
     */
    public String getDisplayableText() {
        return displayableText;
    }
    
    /**
     * Gets the PictureCodeType that matches the given action code.
     * 
     * @param actionCode The picture action code to get the PictureCodeType for.
     * 
     * @return The corresponding PictureCodeType.
     */
    public static PictureActionCodeType getPictureCodeType(int actionCode) {
        return pictureCodeTypes.get(actionCode);
    }
}
