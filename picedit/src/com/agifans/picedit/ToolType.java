package com.agifans.picedit;

/**
 * Enum for the available tool types.
 *  
 * @author Lance Ewing
 */
public enum ToolType {

    NONE("None"), 
    LINE("Line"), 
    SHORTLINE("Short line"), 
    STEPLINE("Step line"), 
    AIRBRUSH("Airbrush"),
    BRUSH("Brush"), 
    FILL("Fill"),
    ZOOM("Zoom"),
    SELECTION("Selection"),
    RECTANGLE("Rectangle"),
    ELLIPSE("Ellipse"),
    EYEDROPPER("Eyedropper"),
    ERASER("Eraser")
    ;

    private String displayName;

    /**
     * Constructor for ToolType.
     * 
     * @param displayName the name of the tool that is displayed in the status bar.
     */
    ToolType(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }
}
