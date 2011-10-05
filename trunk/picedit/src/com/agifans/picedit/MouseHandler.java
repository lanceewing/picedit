package com.agifans.picedit;

import java.awt.AWTEvent;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles processing of PICEDIT mouse click and mouse move events.
 * 
 * @author Lance Ewing
 */
public class MouseHandler extends CommonHandler implements MouseMotionListener, MouseListener, MouseWheelListener {

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
     * Constructor for MouseHandler.
     * 
     * @param pictureFrame The PictureFrame that this MouseHandler is for.
     * @param application The PICEDIT application component.
     */
    public MouseHandler(final PictureFrame pictureFrame, PicEdit application) {
        super(pictureFrame.getEditStatus(), pictureFrame.getPicGraphics(), pictureFrame.getPicture(), application);

        this.pictureFrame = pictureFrame;
        
        // Starts timer that injects mouse motion events.
        startMouseMotionTimer();

        // Create a Robot for auto adjusting mouse position when tool restricts movement.
        try {
            robot = new Robot();
        } catch (Exception e) {
        }
        
        // Registers a mouse event listener to keep mouse activity within the the
        // picture panel while in line drawing mode. 
        Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                MouseEvent mouseEvent = (MouseEvent)e;
                
                // TODO: Might need to change how this is done when introducing multiple picture frames.
                
                // If a line is being drawn and the mouse event is outside the picture...
                if (editStatus.isLineBeingDrawn() && 
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
        }, (AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK));
    }

    /**
     * Starts a timer to track the mouse position. This appears to be more
     * reliable that receiving mouse motion events for some reason, and it
     * has the added benefit of tracking the mouse when it is outside of 
     * the PICEDIT window.
     */
    private void startMouseMotionTimer() {
        Timer timer = new Timer();
        TimerTask trackingTask = new TimerTask() {
            private Point lastPoint = getPoint();

            public void run() {
                Point mousePoint = getPoint();

                // Check if mouse point has changed.
                if (!mousePoint.equals(lastPoint)) {
                    if (editStatus.isPaused() || editStatus.isMenuActive()) {
                        // If paused or menu system is active then ignore mouse motion.
                    } else {
                        // Otherwise process the mouse event as per normal.
                        processMouseMove(mousePoint);

                        // Vary the colour of the end point of the temporary line so that it is obvious where it is.
                        if (editStatus.isLineBeingDrawn()) {
                            int index = (editStatus.getMouseY() << 8) + (editStatus.getMouseY() << 6) + (editStatus.getMouseX() << 1);
                            int brightness = (int) ((System.currentTimeMillis() >> 1) & 0xFF);
                            int rgbCode = (new java.awt.Color(brightness, brightness, brightness)).getRGB();
                            picGraphics.getScreen()[index] = rgbCode;
                            picGraphics.getScreen()[index + 1] = rgbCode;
                        }
                    }

                    // Change mouse cursor depending on position.
                    updateMouseCursor(mousePoint);

                    // And check if we need to update the screen.
                    picGraphics.checkDrawFrame();
                }
            }
        };
        timer.scheduleAtFixedRate(trackingTask, 40, 40);
    }

    /**
     * Returns the current mouse position, adjusted to the AGI screen coordinates.
     * 
     * @return the current mouse position.
     */
    public Point getPoint() {
        Point absolutePosition = MouseInfo.getPointerInfo().getLocation();

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
    }

    /**
     * Invoked when the mouse exits the PICEDIT window.
     * 
     * @param event the mouse exited event.
     */
    public void mouseExited(MouseEvent event) {
    }

    /**
     * Invoked when a mouse button is pressed down.
     * 
     * @param event the mouse pressed event.
     */
    public void mousePressed(MouseEvent event) {
        Point mousePoint = getPoint();

        if (editStatus.isPaused()) {
            // If paused then ignore mouse clicks.
        } else if (editStatus.isMenuActive()) {
            // If menu was active and we received a mouse click, then set menu active false again.
            editStatus.setMenuActive(false);
        } else {
            // Otherwise process mouse click as per normal.
            processMouseClick(mousePoint, event.getButton());
        }

        // Change mouse cursor depending on position.
        updateMouseCursor(mousePoint);

        // Check if we need to update the screen.
        picGraphics.checkDrawFrame();
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
    	if (event.getWheelRotation() > 0) {
    		// Zoom in.
            int zoomFactor = editStatus.getZoomFactor();
            if (zoomFactor < 5) {
                application.resizeScreen(zoomFactor + 1);
            }
    	} else {
    		// Zoom out.
            int zoomFactor = editStatus.getZoomFactor();
            if (zoomFactor > 2) {
                application.resizeScreen(zoomFactor - 1);
            }
    	}
    }

    /**
     * Updates the appearance of the mouse cursor depending on where the mouse is.
     * 
     * @param mousePoint the current mouse position.
     */
    private void updateMouseCursor(Point mousePoint) {
        if (!editStatus.isMenuActive()) {
            if ((editStatus.getNumOfClicks() == 0) || editStatus.isFillActive() || editStatus.isBrushActive()) {
                // If the tool is Fill or Brush then show the standard cross hair cursor.
                picGraphics.showCrossHairCursor();
            } else {
                // If the tool is Line, Pen or Step then show no cursor (end of line with 'glow' instead).
                picGraphics.showBlankCursor();
            }
        } else {
            // If the mouse position is over the edit panel, or the status bar, or the menu
            // system is active, then show the default pointer.
            picGraphics.showDefaultCursor();
        }
    }

    /**
     * Processes the movement of the mouse.
     * 
     * @param mousePoint the Point where the mouse currently is.
     */
    public void processMouseMove(Point mousePoint) {
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
                picGraphics.drawTemporaryLine(clickX, clickY, x, y, lineColour, editStatus.getBGLineData());
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
                        picGraphics.drawTemporaryLine(clickX, clickY, x, y, lineColour, editStatus.getBGLineData());
                        break;

                    default:
                        if ((editStatus.isXCornerActive() && ((editStatus.getNumOfClicks() % 2) == 0)) || (editStatus.isYCornerActive() && ((editStatus.getNumOfClicks() % 2) > 0))) {
                            // X and Y corners toggle different direction based on number of clicks.	
                            x = clickX;
                        } else {
                            y = clickY;
                        }
                        picGraphics.drawTemporaryLine(clickX, clickY, x, y, lineColour, editStatus.getBGLineData());
                        break;
                }
            }
            if (editStatus.isPenActive()) {
                x = clickX + adjustForPen(x - clickX, 6);
                y = clickY + adjustForPen(y - clickY, 7);
                picGraphics.drawTemporaryLine(clickX, clickY, x, y, lineColour, editStatus.getBGLineData());
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

        // Mouse click clears the stored temporary line making it permanent.
        editStatus.clearBGLineData();

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
                    picture.updateScreen();
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
                            picture.updateScreen();
                            break;
                        default:
                            picture.addPictureCode(x);
                            picture.addPictureCode(y);
                            picture.drawLine(previousX, previousY, x, y);
                            picture.updateScreen();
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
                            picture.updateScreen();
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
                            picture.updateScreen();
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
                    picture.updateScreen();
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
                            picture.updateScreen();
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
                            picture.updateScreen();
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
                picture.updateScreen();
            }
            if (editStatus.getNumOfClicks() > 0) {
                // If a tool is active (i.e. has a least one click) then right click resets 
                // number of clicks, which allows the user to move to new location.
                editStatus.resetTool();
            } else {
                // If no clicks performed yet then a right click sets tool to None.
                editStatus.setTool(ToolType.NONE);
            }
            picture.updateScreen();
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
