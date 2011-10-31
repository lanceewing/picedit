package com.agifans.picedit.gui.handler;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.gui.frame.PictureFrame;
import com.agifans.picedit.gui.frame.PicturePanel;
import com.agifans.picedit.gui.toolbar.ColourChooserDialog;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.types.BrushTexture;
import com.agifans.picedit.types.PictureType;
import com.agifans.picedit.types.StepType;
import com.agifans.picedit.types.ToolType;

/**
 * Handles processing of mouse click and mouse move events for a PictureFrame.
 * 
 * @author Lance Ewing
 */
public class MouseHandler implements MouseMotionListener, MouseListener, MouseWheelListener {

    /**
     * The PICEDIT application component.
     */
    protected PicEdit application;
    
    /** 
     * Holds the difference between mouse motion event X positions and absolute mouse X positions.
     */
    private int diffX;

    /** 
     * Holds the difference between mouse motion event Y positions and absolute mouse Y positions.
     */
    private int diffY;

    /** 
     * Robot used for programmatically adjusting the mouse cursor position.
     */
    private Robot robot;
    
    /**
     * The picture frame that this MouseHandler is for.
     */
    private PictureFrame pictureFrame;

    /**
     * Helps to protect against clashes between mouse wheel rotation and clicks.
     */
    private int wheelCounter;
    
    /**
     * Cursor to show when moving over the picture.
     */
    private Cursor crossHairCursor;

    /**
     * Cursor to show when moving over status bar, menu and button panel.
     */
    private Cursor defaultCursor;

    /**
     * Cursor to show when hiding the mouse cursor (i.e. a blank cursor).
     */
    private Cursor blankCursor;
    
    /**
     * Whether the mouse is over the picture or not.
     */
    private boolean mouseIsOverPicture;
    
    /**
     * The last Point that the mouse was at. Used for mouse motion checks.
     */
    private Point lastPoint;
    
    /**
     * Constructor for MouseHandler.
     * 
     * @param pictureFrame The PictureFrame that this MouseHandler is for.
     * @param application The PICEDIT application component.
     */
    public MouseHandler(final PictureFrame pictureFrame, final PicEdit application) {
        this.pictureFrame = pictureFrame;
        this.application = application;
        
        // Create the different types of cursor used in different parts of the screen.
        crossHairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        blankCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
        
        // Create a Robot for auto adjusting mouse position when tool restricts movement.
        try {
            robot = new Robot();
        } catch (Exception e) {
        }
        
        // Registers a mouse event listener to keep mouse activity within the the
        // picture panel while in line drawing mode. 
        Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                if (pictureFrame.isSelected()) {
                    MouseEvent mouseEvent = (MouseEvent)e;
                    
                    // If a line is being drawn and the mouse event is outside the picture...
                    if (application.getEditStatus().isLineBeingDrawn() && 
                        !mouseEvent.getSource().equals(pictureFrame.getPicturePanel())) {
                        
                        // If it is a mouse pressed event then we process the click as if 
                        // it was on the picture.
                        if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                            mousePressed(mouseEvent);
                        }
                        
                        // If it is a mouse motion event then we use the robot to move it 
                        // back inside the panel.
                        if (mouseEvent.getID() == MouseEvent.MOUSE_MOVED) {
                            moveMouseToPictureCoordinates();
                        }
                    }
                }
            }
        }, (AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK));
    }
    
    /**
     * Checks if the mouse has moved and if it has then triggers the processing 
     * of mouse motion and the updating of the appearance of the mouse cursor. This
     * method is invoked immediately before the application screen repaint.
     */
    public void checkForMouseMotion() {
        Point mousePoint = getPoint();
        EditStatus editStatus = application.getEditStatus();

        // Check if mouse point has changed.
        if (!mousePoint.equals(lastPoint)) {
            if (editStatus.isMenuActive()) {
                // If paused or menu system is active then ignore mouse motion.
            } else {
                // Otherwise process the mouse event as per normal.
                processMouseMove(mousePoint);
            }

            // Change mouse cursor depending on position.
            updateMouseCursor();
        }
    }

    /**
     * Returns the current mouse position, adjusted to the AGI screen coordinates.
     * 
     * @return the current mouse position.
     */
    public Point getPoint() {
        Point absolutePosition = MouseInfo.getPointerInfo().getLocation();
        EditStatus editStatus = application.getEditStatus();

        int x = ((int) absolutePosition.getX() - diffX) / editStatus.getZoomFactor();
        int y = ((int) absolutePosition.getY() - diffY) / editStatus.getZoomFactor();

        return new Point(x, y);
    }

    /**
     * Invoked when a mouse drag event occurs.
     * 
     * @param event the mouse drag event.
     */
    public void mouseDragged(MouseEvent event) {
    }

    /**
     * Invoked when a mouse motion event occurs. This is used only to track
     * the top left corner of the PICEDIT window. The mouse position returned
     * by MouseInfo is the absolute position on the computer's screen. The 
     * mouse motion tracking timer needs to know the difference between the 
     * absolute mouse position and the X/Y values contained in the real mouse
     * events.
     * 
     * @param event the mouse motion event.
     */
    public void mouseMoved(MouseEvent event) {
        Point absolutePosition = MouseInfo.getPointerInfo().getLocation();
        this.diffX = (int) absolutePosition.getX() - event.getX();
        this.diffY = (int) absolutePosition.getY() - event.getY();
    }

    /**
     * Invoked when a mouse button is click.
     * 
     * @param event the mouse click event.
     */
    public void mouseClicked(MouseEvent event) {
        // Ignored. Button status is handled by presssed.
    }

    /**
     * Invoked when the mouse enters the PICEDIT window.
     * 
     * @param event the mouse entered event.
     */
    public void mouseEntered(MouseEvent event) {
        this.mouseIsOverPicture = true;
    }

    /**
     * Invoked when the mouse exits the PICEDIT window.
     * 
     * @param event the mouse exited event.
     */
    public void mouseExited(MouseEvent event) {
        this.mouseIsOverPicture = false;
    }

    /**
     * Invoked when a mouse button is pressed down.
     * 
     * @param event the mouse pressed event.
     */
    public void mousePressed(MouseEvent event) {
        Point mousePoint = getPoint();
        EditStatus editStatus = application.getEditStatus();

        if (editStatus.isMenuActive()) {
            // If menu was active and we received a mouse click, then set menu active false again.
            editStatus.setMenuActive(false);
        } else {
            // Otherwise process mouse click as per normal.
            processMouseClick(mousePoint, event.getButton());
            
            // Mouse wheel button, AKA. the middle button. This is not a picture related action, so
            // we process outside of the normal processMouseClick.
            if (event.getButton() == MouseEvent.BUTTON2) {
                // Reset the current tool if line is being drawn. Doesn't make sense to keep line
                // drawing enabled while colour is being chosen.
                if (editStatus.isLineBeingDrawn()) {
                    editStatus.resetTool();
                }
                
                Point eventPoint = event.getLocationOnScreen();
                Point dialogPoint = new Point(eventPoint.x - 34, eventPoint.y - 34);
                
                // Pop up colour chooser.
                ColourChooserDialog dialog = new ColourChooserDialog(dialogPoint);
                dialog.setVisible(true);
                
                // Process the chosen visual colour.
                application.getPicture().processVisualColourChange(dialog.getChosenColour());
                
                // This helps to protect against clashes between wheel clicks and rotation.
                wheelCounter = 0;
            }
        }
    }

    /**
     * Invoked when a mouse button is released.
     * 
     * @param event the mouse released event.
     */
    public void mouseReleased(MouseEvent event) {
    }
    
    /**
     * Invoked when the mouse wheel is moved.
     * 
     * @param event the mouse wheel moved event.
     */
    public void mouseWheelMoved(MouseWheelEvent event) {
        EditStatus editStatus = application.getEditStatus();
        
        wheelCounter += event.getWheelRotation();
        
        if (wheelCounter < -1) {
    		// Zoom in.
            int zoomFactor = editStatus.getZoomFactor();
            if (zoomFactor < 5) {
                application.resizeScreen(zoomFactor + 1);
            }
            wheelCounter = 0;
    	} else if (wheelCounter > 1) {
    		// Zoom out.
            int zoomFactor = editStatus.getZoomFactor();
            if (zoomFactor > 1) {
                application.resizeScreen(zoomFactor - 1);
            }
            wheelCounter = 0;
    	}
    }

    /**
     * Updates the appearance of the mouse cursor depending on where the mouse is and 
     * what tool is being used.
     */
    private void updateMouseCursor() {
        EditStatus editStatus = application.getEditStatus();
        
        if (!editStatus.isMenuActive() && mouseIsOverPicture) {
            if ((editStatus.getNumOfClicks() == 0) || editStatus.isFillActive() || editStatus.isBrushActive()) {
                // If the tool is Fill or Brush then show the standard cross hair cursor.
                application.setCursor(crossHairCursor);
            } else {
                // If the tool is Line, Pen or Step then show no cursor (end of line with 'glow' instead).
                application.setCursor(blankCursor);
            }
        } else {
            // If the mouse position is over the edit panel, or the status bar, or the menu
            // system is active, then show the default pointer.
            application.setCursor(defaultCursor);
        }
    }
    
    /**
     * Processes the movement of the mouse.
     * 
     * @param mousePoint the Point where the mouse currently is.
     */
    public void processMouseMove(Point mousePoint) {
        EditStatus editStatus = application.getEditStatus();
        PicturePanel picturePanel = application.getPicturePanel();
        
        // Update the status line on every mouse movement.
        editStatus.updateMousePoint(mousePoint);

        int x = editStatus.getMouseX();
        int y = editStatus.getMouseY();
        
        if (editStatus.getNumOfClicks() > 0) {
            // Make sure that the mouse cursor can't leave the picture while a line
            // is being drawn.
            Point clickPoint = editStatus.getClickPoint();
            int clickX = (int) clickPoint.getX();
            int clickY = (int) clickPoint.getY();
            int lineColour = editStatus.getTemporaryLineColour();

            if (editStatus.isLineActive()) {
            	picturePanel.drawTemporaryLine(clickX, clickY, x, y, lineColour);
            }
            if (editStatus.isStepActive()) {
                int dX = 0;
                int dY = 0;

                switch (editStatus.getNumOfClicks()) {
                    case 1:
                        dX = x - clickX;
                        dY = y - clickY;
                        if (Math.abs(dX) > Math.abs(dY)) {
                            y = clickY;
                        } else {
                            x = clickX;
                        }
                        picturePanel.drawTemporaryLine(clickX, clickY, x, y, lineColour);
                        break;

                    default:
                        if ((editStatus.isXCornerActive() && ((editStatus.getNumOfClicks() % 2) == 0)) || (editStatus.isYCornerActive() && ((editStatus.getNumOfClicks() % 2) > 0))) {
                            // X and Y corners toggle different direction based on number of clicks.	
                            x = clickX;
                        } else {
                            y = clickY;
                        }
                        picturePanel.drawTemporaryLine(clickX, clickY, x, y, lineColour);
                        break;
                }
            }
            if (editStatus.isPenActive()) {
                x = clickX + adjustForPen(x - clickX, 6);
                y = clickY + adjustForPen(y - clickY, 7);
                picturePanel.drawTemporaryLine(clickX, clickY, x, y, lineColour);
            }
            
            // Move the mouse to the tool restricted x/y position (if applicable).
            if ((robot != null) && ((x != editStatus.getMouseX()) || (y != editStatus.getMouseY()))) {
                // Make sure the EditStatus has the tool adjusted mouse point.
                editStatus.setMousePoint(new Point(x, y));

                // Move the mouse back to the tool adjusted point. This is important
                // when the tool is Pen or Step since these tools are restricted in
                // their movements.
                moveMouseToPictureCoordinates();
            }
        }
    }

    /**
     * Processes the given mouse click.
     * 
     * @param mousePoint the Point where the mouse click occurred.
     * @param mouseButton the mouse button that was clicked.
     */
    public void processMouseClick(Point mousePoint, int mouseButton) {
        int x = (int) mousePoint.getX();
        int y = (int) mousePoint.getY();
        
        EditStatus editStatus = application.getEditStatus();
        Picture picture = application.getPicture();

        // Is it the LEFT mouse button?
        if (mouseButton == MouseEvent.BUTTON1) {
            // Is a tool active?
            if (editStatus.getTool() != ToolType.NONE) {
                // Register the new left mouse click in the edit status.
                editStatus.addClickPoint();

                // Get the 'adjusted' X & Y position back from the edit status.
                Point pictureClickPoint = editStatus.getClickPoint();
                x = (int) pictureClickPoint.getX();
                y = (int) pictureClickPoint.getY();

                // Get the previous mouse click point for use with Line, Pen, Step.
                int previousX = 0;
                int previousY = 0;
                Point previousClickPoint = editStatus.getPreviousClickPoint();
                if (previousClickPoint != null) {
                    previousX = (int) previousClickPoint.getX();
                    previousY = (int) previousClickPoint.getY();
                }

                // If a given tool is active then update the AGI picture.
                if (editStatus.isFillActive()) {
                    picture.fill(x, y);
                    if (editStatus.isFirstClick()) {
                        picture.addPictureCode(0xF8);
                    }
                    picture.addPictureCode(x);
                    picture.addPictureCode(y);
                } else if (editStatus.isLineActive()) {
                    switch (editStatus.getNumOfClicks()) {
                        case 1:
                            picture.addPictureCode(0xF6);
                            picture.addPictureCode(x);
                            picture.addPictureCode(y);
                            picture.putPixel(x, y);
                            break;
                        default:
                            picture.addPictureCode(x);
                            picture.addPictureCode(y);
                            picture.drawLine(previousX, previousY, x, y);
                            break;
                    }
                } else if (editStatus.isPenActive()) {
                    int disp = 0;
                    int dX = 0;
                    int dY = 0;

                    switch (editStatus.getNumOfClicks()) {
                        case 1:
                            picture.addPictureCode(0xF7);
                            picture.addPictureCode(x);
                            picture.addPictureCode(y);
                            picture.putPixel(x, y);
                            break;
                        default:
                            dX = adjustForPen(x - previousX, 6);
                            dY = adjustForPen(y - previousY, 7);
                            x = previousX + dX;
                            y = previousY + dY;

                            if (dX < 0) {
                                disp = (0x80 | ((((-1) * dX) - 0) << 4));
                            } else {
                                disp = (dX << 4);
                            }
                            if (dY < 0) {
                                disp |= (0x08 | (((-1) * dY) - 0));
                            } else {
                                disp |= dY;
                            }
                            picture.addPictureCode(disp);
                            picture.drawLine(previousX, previousY, x, y);
                            editStatus.setClickPoint(new Point(x, y));
                            break;
                    }
                } else if (editStatus.isBrushActive()) {
                    int patNum = 0;

                    if (editStatus.isFirstClick()) {
                        picture.addPictureCode(0xF9);
                        picture.addPictureCode(editStatus.getBrushCode());
                        picture.addPictureCode(0xFA);
                    }
                    patNum = (((new java.util.Random().nextInt(255)) % 0xEE) >> 1) & 0x7F;
                    picture.plotPattern(patNum, x, y);
                    if (editStatus.getBrushTexture() == BrushTexture.SPRAY) {
                        picture.addPictureCode(patNum << 1);
                    }
                    picture.addPictureCode(x);
                    picture.addPictureCode(y);
                } else if (editStatus.isStepActive()) {
                    int dX = 0;
                    int dY = 0;

                    switch (editStatus.getNumOfClicks()) {
                        case 1:
                            break;

                        case 2:
                            dX = x - previousX;
                            dY = y - previousY;
                            if (Math.abs(dX) > Math.abs(dY)) { /* X or Y corner */
                                y = previousY;
                                editStatus.setStepType(StepType.XCORNER);
                                picture.addPictureCode(0xF5);
                                picture.addPictureCode(previousX);
                                picture.addPictureCode(previousY);
                                picture.addPictureCode(x);
                            } else {
                                x = previousX;
                                editStatus.setStepType(StepType.YCORNER);
                                picture.addPictureCode(0xF4);
                                picture.addPictureCode(previousX);
                                picture.addPictureCode(previousY);
                                picture.addPictureCode(y);
                            }
                            picture.drawLine(previousX, previousY, x, y);
                            editStatus.setClickPoint(new Point(x, y));
                            break;

                        default:
                            if ((editStatus.isXCornerActive() && ((editStatus.getNumOfClicks() % 2) > 0)) || (editStatus.isYCornerActive() && ((editStatus.getNumOfClicks() % 2) == 0))) {
                                // X and Y corners toggle different direction based on number of clicks.	
                                x = previousX;
                                picture.addPictureCode(y);
                            } else {
                                y = previousY;
                                picture.addPictureCode(x);
                            }
                            picture.drawLine(previousX, previousY, x, y);
                            editStatus.setClickPoint(new Point(x, y));
                            break;
                    }
                }
            }
        }
        
        // Is it the RIGHT mouse button?
        if (mouseButton == MouseEvent.BUTTON3) {
            // Right-clicking on the AGI picture will clear the current tool selection.
            if ((editStatus.getNumOfClicks() == 1) && (editStatus.isStepActive())) {
                // Single point line support for the Step tool.
                
                // Get the 'adjusted' X & Y position back from the edit status.
                editStatus.addClickPoint();
                Point pictureClickPoint = editStatus.getClickPoint();
                x = (int) pictureClickPoint.getX();
                y = (int) pictureClickPoint.getY();

                // Get the previous mouse click point for use with single point Step.
                int previousX = 0;
                int previousY = 0;
                Point previousClickPoint = editStatus.getPreviousClickPoint();
                if (previousClickPoint != null) {
                    previousX = (int) previousClickPoint.getX();
                    previousY = (int) previousClickPoint.getY();
                }
                
                // The X/Y corner decision for single point is based on where right click was.
                int dX = x - previousX;
                int dY = y - previousY;
                if (Math.abs(dX) > Math.abs(dY)) { /* X or Y corner */
                    picture.addPictureCode(0xF5);
                    picture.addPictureCode(previousX);
                    picture.addPictureCode(previousY);
                } else {
                    picture.addPictureCode(0xF4);
                    picture.addPictureCode(previousX);
                    picture.addPictureCode(previousY);
                }
                
                picture.putPixel(previousX, previousY);
            }
            if (editStatus.getNumOfClicks() > 0) {
                // If a tool is active (i.e. has a least one click) then right click resets 
                // number of clicks, which allows the user to move to new location.
                editStatus.resetTool();
            } else {
                // If no clicks performed yet then a right click sets tool to None.
                editStatus.setTool(ToolType.NONE);
            }
        }
        
        // Update active status of the position slider based on whether a line is being
        // drawn or not. It should not be possible to use the slider if line drawing is
        // enabled.
        pictureFrame.getPositionSlider().setEnabled(!editStatus.isLineBeingDrawn());
    }

    /**
     * Moves the mouse cursor to the position that matches what the EditStatus says
     * is the current picture coordinates. This is useful for situations where the
     * current picture coordinates have been restricted due to the bounds of the
     * picture or the bounds of the drawing tool (e.g. Pen or Step).
     */
    private void moveMouseToPictureCoordinates() {
        EditStatus editStatus = application.getEditStatus();
        
        // Get current picture coordinates.
        int x = editStatus.getMouseX();
        int y = editStatus.getMouseY();
        
        // Adjust the picture coordinates back to screen coordinates.
        if (editStatus.getPictureType().equals(PictureType.AGI)) {
            x = ((x << 1) * editStatus.getZoomFactor()) + diffX;
        } else {
            x = (x * editStatus.getZoomFactor()) + diffX;
        }
        y = (y * editStatus.getZoomFactor()) + diffY;
      
        // Use robot to move the mouse cursor to the calculated position.
        robot.mouseMove(x, y);
    }
    
    /**
     * The Pen tool has a very short distance between two points. This
     * method is used for checking those limits.
     * 
     * @param value the value to adjust. Will either be an X or Y value.
     * @param limit the limit in both directs that to enforce.
     */
    private int adjustForPen(int value, int limit) {
        if (value > limit) {
            value = limit;
        }
        if (value < -limit) {
            value = -limit;
        }
        return value;
    }
}
