package com.agifans.picedit.picture;

import java.awt.Point;
import java.io.File;

import com.agifans.picedit.types.BrushShape;
import com.agifans.picedit.types.BrushTexture;
import com.agifans.picedit.types.PictureType;
import com.agifans.picedit.types.StepType;
import com.agifans.picedit.types.ToolType;

/**
 * This class holds everything about the current picture editing status.
 * 
 * @author Lance Ewing
 */
public class EditStatus {

    public static final int TRANSPARENT = 16;

    public static final int VISUAL_OFF = -1;

    public static final int PRIORITY_OFF = -1;

    public static final int CONTROL_OFF = -1;

    private ToolType tool;

    private int visualColour;

    private int priorityColour;

    private int controlColour;

    private Point mousePoint;

    private Point previousMousePoint;

    private int priorityBand;

    private Point clickPoint;

    private Point previousClickPoint;

    private int brushSize;

    private BrushShape brushShape;

    private BrushTexture brushTexture;

    private int numOfClicks;

    private StepType stepType;

    private boolean backgroundEnabled;

    private boolean menuActive;

    private boolean priorityShowing;

    /**
     * The type of Sierra picture being edited.
     */
    private PictureType pictureType;
    
    /**
     * The picture File currently being edited.
     */
    private File pictureFile;
    
    /**
     * Whether the priority bands are currently on or not.
     */
    private boolean bandsOn;

    /**
     * Whether the dual visual/priority mode is enabled or not.
     */
    private boolean dualModeEnabled;
    
    /**
     * Whether the EGO Test mode is enabled or not.
     */
    private boolean egoTestEnabled;
    
    /**
     * The factor by which to multiply the screen size by.
     */
    private int zoomFactor;
    
    /**
     * Whether the picture has unsaved changes or not.
     */
    private boolean unsavedChanges;
    
    /**
     * Whether the EditStatus has changes that have not been rendered yet.
     */
    private boolean unrenderedChanges;
    
    /**
     * Constructor for EditStatus.
     */
    public EditStatus() {
        clear();
    }
    
    /**
     * Clears the state of the EditStatus. This method is invoked whenever
     * a picture is loaded or a new picture is created.
     */
    public void clear() {
        clear(true);
    }

    /**
     * Clears the state of the EditStatus. The newPicture flag says whether
     * the state is being cleared for a completely new picture (e.g. when
     * a picture is loaded or when a picture is created) or whether it is
     * just the current picture that is being redrawn from the start again.
     * 
     * @param newPicture true if it is being cleared for a new picture; otherwise false.
     */
    public void clear(boolean newPicture) {
        tool = ToolType.NONE;
        menuActive = false;
        visualColour = VISUAL_OFF;
        priorityColour = PRIORITY_OFF;
        controlColour = CONTROL_OFF;
        mousePoint = new Point(0, 0);
        previousMousePoint = new Point(0, 0);
        brushSize = 0;
        brushShape = BrushShape.CIRCLE;
        brushTexture = BrushTexture.SOLID;
        if (newPicture) {
            // These are the bits that get cleared for a new picture.
            priorityShowing = false;
            pictureType = PictureType.AGI;
            backgroundEnabled = false;
            pictureFile = null;
            unsavedChanges = true;
        }
        resetTool();
    }

    /**
     * Sets the current tool back to its initial state. For line, pen and
     * step this means that if a line is currently being drawn then that
     * line is completed thereby allowing a new line to be drawn using
     * the same tool. It is this method that is invoked when the right
     * mouse button is clicked once after drawing a line.
     */
    public void resetTool() {
        numOfClicks = 0;
        stepType = null;
        clickPoint = null;
        previousClickPoint = null;
    }

    /**
     * Gets the colour of the temporary line to draw when line drawing is 
     * enabled. This is the "rubber band" line that is drawn when a segment
     * of a line is being placed. 
     * 
     * @return the colour of the temporary line.
     */
    public int getTemporaryLineColour() {
        int lineColour = 0;
        if (isPriorityShowing() && isPriorityDrawEnabled()) {
            lineColour = getPriorityColour();
        } else if (!isPriorityShowing() && isVisualDrawEnabled()) {
            lineColour = getVisualColour();
        }
        return lineColour;
    }

    /**
     * Returns true if the priority screen is currently showing.
     * 
     * @return true if the priority screen is currently showing.
     */
    public boolean isPriorityShowing() {
        return priorityShowing;
    }

    /**
     * Toggles between the visual and priority screens.
     */
    public void toggleScreen() {
        priorityShowing = !priorityShowing;
        unrenderedChanges = true;
    }

    /**
     * Returns true if the currently active tool is on its first click.
     *  
     * @return true if the currently active tool is on its first click; otherwise false.
     */
    public boolean isFirstClick() {
        return (numOfClicks == 1);
    }

    /**
     * Gets the number of clicks that have been made since activating the 
     * current tool. For lines, this is effectively the number of vertices.
     *  
     * @return the number of clicks that have been made since activating the current tool.
     */
    public int getNumOfClicks() {
        return numOfClicks;
    }

    /**
     * Gets the currently active tool.
     * 
     * @return the currently active tool.
     */
    public ToolType getTool() {
        return tool;
    }

    /**
     * Sets the given tool to be active.
     * 
     * @param tool The tool to activate.
     */
    public void setTool(ToolType tool) {
        resetTool();
        this.tool = tool;
    }

    /**
     * Gets the current visual colour.
     * 
     * @return the current visual colour.
     */
    public int getVisualColour() {
        return visualColour;
    }

    public void setVisualColour(int visualColour) {
        resetTool();
        this.visualColour = (visualColour == 15 ? TRANSPARENT : visualColour);
    }

    public int getPriorityColour() {
        return priorityColour;
    }

    public void setPriorityColour(int priorityColour) {
        resetTool();
        if (this.pictureType.equals(PictureType.AGI)) {
            // For AGI, priority starts at 4, so background is red.
            this.priorityColour = (priorityColour == 4 ? TRANSPARENT : priorityColour);
        } else if (this.pictureType.equals(PictureType.SCI0)) {
            // For SCI0, priority starts at 0, so background is black.
            this.priorityColour = (priorityColour == 0 ? TRANSPARENT : priorityColour);
        }
    }

    public int getControlColour() {
        return controlColour;
    }

    public void setControlColour(int controlColour) {
        resetTool();
        this.controlColour = (controlColour == 0 ? TRANSPARENT : controlColour);
    }

    public Point getMousePoint() {
        return mousePoint;
    }

    public void setMousePoint(Point mousePoint) {
        this.mousePoint = mousePoint;
    }

    public void updateMousePoint(Point mousePoint) {
        this.previousMousePoint = this.mousePoint;
        this.mousePoint = adjustPoint(mousePoint);

        // Calculate the corresponding priority band on the fly.
        if (this.pictureType.equals(PictureType.SCI0)) {
            // For SCI0, the top 42 lines are for priority 0. The other 14 bands
            // get an even share of the 148 remaining lines (which, btw, doesn't
            // divide evenly, so the bands are not even as then are in AGI).
            this.priorityBand = ((int) ((getMouseY() - 42) / ((190 - 42) / 14))) + 1;
        } else if (this.pictureType.equals(PictureType.AGI)) {
            // For AGI it is evenly split, 168 lines split 14 ways.
            this.priorityBand = (getMouseY() / 12) + 1;

            // Make sure priority band is 4 or above for AGI since the bottom
            // four priority colours are reserved as control lines.
            if (this.priorityBand < 4) {
                this.priorityBand = 4;
            }
        }
    }

    public boolean hasMouseMoved() {
        return !mousePoint.equals(previousMousePoint);
    }

    public int getMouseX() {
        return (int) mousePoint.getX();
    }

    public int getMouseY() {
        return (int) mousePoint.getY();
    }

    public int getPriorityBand() {
        return priorityBand;
    }

    public void addClickPoint() {
        if (tool != ToolType.NONE) {
            this.previousClickPoint = this.clickPoint;

            // Use the current mouse point as shown to the user. This keeps
            // the behaviour consistent and doesn't result in a line
            // 'jumping' to a pixel position the user didn't expect.
            this.clickPoint = mousePoint;

            // Automatically keep track of how many clicks for the
            // currently active tool.
            this.numOfClicks++;
        }
    }

    public void updateClickPoint(Point clickPoint) {
        if (tool != ToolType.NONE) {
            this.previousClickPoint = this.clickPoint;

            // The actual click point that the EditStatus will store is
            // adjusted to the AGI PICTURE canvas coordinate system and
            // bounds.
            this.clickPoint = adjustPoint(clickPoint);

            // Automatically keep track of how many clicks for the
            // currently active tool.
            this.numOfClicks++;
        }
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
    }

    public Point getClickPoint() {
        return clickPoint;
    }

    public Point getPreviousClickPoint() {
        return previousClickPoint;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public void decrementBrushSize() {
        brushSize--;
        if (brushSize < 0) {
            brushSize = 0;
        }
    }

    public void incrementBrushSize() {
        brushSize++;
        if (brushSize > 7) {
            brushSize = 7;
        }
    }

    public BrushShape getBrushShape() {
        return brushShape;
    }

    public void setBrushShape(BrushShape brushShape) {
        this.brushShape = brushShape;
    }

    public BrushTexture getBrushTexture() {
        return brushTexture;
    }

    public void setBrushTexture(BrushTexture brushTexture) {
        this.brushTexture = brushTexture;
    }

    public int getBrushCode() {
        int brushCode = (brushSize & 0x07);
        if (brushShape == BrushShape.SQUARE)
            brushCode |= 0x10;
        if (brushTexture == BrushTexture.SPRAY)
            brushCode |= 0x20;
        return brushCode;
    }

    public void setBrushCode(int brushCode) {
        brushShape = ((brushCode & 0x10) > 0 ? BrushShape.SQUARE : BrushShape.CIRCLE);
        brushTexture = ((brushCode & 0x20) > 0 ? BrushTexture.SPRAY : BrushTexture.SOLID);
        brushSize = (brushCode & 0x07);
    }

    public boolean isCircleBrush() {
        return (brushShape == BrushShape.CIRCLE);
    }

    public boolean isSquareBrush() {
        return (brushShape == BrushShape.SQUARE);
    }

    public boolean isSprayBrush() {
        return (brushTexture == BrushTexture.SPRAY);
    }

    public boolean isSolidBrush() {
        return (brushTexture == BrushTexture.SOLID);
    }

    public boolean isBrushActive() {
        return (tool == ToolType.BRUSH) || (tool == ToolType.AIRBRUSH);
    }

    public boolean isFillActive() {
        return (tool == ToolType.FILL);
    }

    public boolean isLineActive() {
        return (tool == ToolType.LINE);
    }

    public boolean isStepActive() {
        return (tool == ToolType.STEPLINE);
    }

    public boolean isPenActive() {
        return (tool == ToolType.SHORTLINE);
    }

    public boolean isVisualDrawEnabled() {
        return (visualColour != VISUAL_OFF);
    }

    public boolean isPriorityDrawEnabled() {
        return (priorityColour != PRIORITY_OFF);
    }

    public StepType getStepType() {
        return stepType;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public boolean isXCornerActive() {
        return (stepType == StepType.XCORNER);
    }

    public boolean isYCornerActive() {
        return (stepType == StepType.YCORNER);
    }

    public boolean isBackgroundEnabled() {
        return backgroundEnabled;
    }

    public void setBackgroundEnabled(boolean backgroundEnabled) {
        this.backgroundEnabled = backgroundEnabled;
        this.unrenderedChanges = true;
    }

    public boolean isMenuActive() {
        return menuActive;
    }

    public void setMenuActive(boolean menuActive) {
        this.menuActive = menuActive;
    }

    public PictureType getPictureType() {
        return pictureType;
    }

    public void setPictureType(PictureType pictureType) {
        this.pictureType = pictureType;
    }

    public File getPictureFile() {
        return this.pictureFile;
    }
    
    public void setPictureFile(File pictureFile) {
        if (!pictureFile.equals(this.pictureFile)) {
            this.pictureFile = pictureFile;
        }
    }

    public boolean isBandsOn() {
        return bandsOn;
    }

    public void setBandsOn(boolean bandsOn) {
        this.bandsOn = bandsOn;
        this.unrenderedChanges = true;
    }

    public boolean isDualModeEnabled() {
        return this.dualModeEnabled;
    }

    public void setDualModeEnabled(boolean dualModeEnabled) {
        this.dualModeEnabled = dualModeEnabled;
        this.unrenderedChanges = true;
    }
    
    public boolean isEgoTestEnabled() {
        return egoTestEnabled;
    }

    public void setEgoTestEnabled(boolean egoTestEnabled) {
        this.egoTestEnabled = egoTestEnabled;
    }

    public int getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(int zoomFactor) {
        this.zoomFactor = zoomFactor;
    }
    
    /**
     * Returns true if the picture has unsaved changes.
     * 
     * @return true if the picture has unsaved changes; otherwise false.
     */
    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }
    
    /**
     * Sets whether the picture has unsaved changes or not.
     * 
     * @param unsavedChanges true if the picture has unsaved changes; otherwise false.
     */
    public void setUnsavedChanges(boolean unsavedChanges) {
        this.unsavedChanges = unsavedChanges;
    }
    
    /**
     * Returns true if a line is currently being drawn.
     * 
     * @return true if a line is currently being drawn.
     */
    public boolean isLineBeingDrawn() {
        return (isLineActive() || isPenActive() || isStepActive()) && (numOfClicks > 0);
    }
    
    /**
     * Clears flag that says whether this EditStatus has changes that haven't been rendered. 
     */
    public void clearUnrenderedChanges() {
    	this.unrenderedChanges = false;
    }
    
    /**
     * Returns true if this EditStatus has changes that haven't been rendered yet.
     * 
     * @return true if this EditStatus has changes that haven't been rendered yet; otherwise false.
     */
    public boolean hasUnrenderedChanges() {
    	return unrenderedChanges;
    }
    
    /**
     * Adjusts the mouse point to the coordinate system of the AGI/SCI0 
     * picture canvas. The EditStatus doesn't care about anything outside
     * of the canvas, and it also doesn't care about what the actual
     * screen location was. All it keeps track of is AGI/SCI0 picture X/Y
     * position.
     * 
     * @param point the Point to adjust.
     * 
     * @return the adjusted Point. 
     */
    private Point adjustPoint(Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();

        // PICEDIT screen is 320 pixels wide but AGI PICTURE is 160
        // pixels wide. So start by dividing x by 2.
        if (pictureType.equals(PictureType.AGI)) {
            x = x >> 1;
        }

        // Now do the bounds checking. AGI PICTURE is 160x168. SCI0 is 320x190.
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        
        switch (pictureType) {
          case AGI:
            if (x > 159) {
              x = 159;
            }
            if (y > 167) {
              y = 167;
            }
            break;
            
          case SCI0:
            if (x > 319) {
              x = 319;
            }
            if (y > 189) {
              y = 189;
            }
            break;
        }

        // And finally we return the adjusted Point.
        return new Point(x, y);
    }
}
