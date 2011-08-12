package com.agifans.picedit;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
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

    // -------------------- Bound boxes for buttons --------------------
    private Rectangle screenRect;
    private Rectangle picRect;
    private Rectangle statusRect;

    /**
     * Constructor for MouseHandler.
     * 
     * @param editStatus the EditStatus holding current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param application the PICEDIT application component.
     */
    public MouseHandler(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
        super(editStatus, picGraphics, picture, application);

        // Create the bounding boxes for all of the UI buttons.
        createBoundingBoxes();

        // Starts timer that injects mouse motion events.
        startMouseMotionTimer();

        // Create a Robot for auto adjusting mouse position when tool restricts movement.
        try {
            robot = new Robot();
        } catch (Exception e) {
        }
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

                        // Is the mouse point within the AGI picture?
                        if (picRect.contains(mousePoint)) {
                            // Vary the colour of the end point of the temporary line so that it is obvious where it is.
                            if ((editStatus.isLineActive() || editStatus.isPenActive() || editStatus.isStepActive()) && (editStatus.getNumOfClicks() > 0)) {
                                int index = (editStatus.getMouseY() << 8) + (editStatus.getMouseY() << 6) + (editStatus.getMouseX() << 1) + 2880;
                                int brightness = (int) ((System.currentTimeMillis() >> 1) & 0xFF);
                                int rgbCode = (new java.awt.Color(brightness, brightness, brightness)).getRGB();
                                picGraphics.getScreen()[index] = rgbCode;
                                picGraphics.getScreen()[index + 1] = rgbCode;
                            }
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
     * Creates the bounding rectangles for each of the 'buttons' on 
     * the PICEDIT screen.
     */
    private void createBoundingBoxes() {
        screenRect = new Rectangle(0, 0, 320, 200);
        picRect = new Rectangle(0, 9, 320, 168);
        statusRect = new Rectangle(0, 0, 320, 8);
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
            if (statusRect.contains(mousePoint)) {
              // TODO: Now that the menu is not activated here, it opens up more possibilities for the status bar.
            } else {
                // Otherwise process mouse click as per normal.
                processMouseClick(mousePoint, event.getButton());
            }
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
    }

    /**
     * Updates the appearance of the mouse cursor depending on where the mouse is.
     * 
     * @param mousePoint the current mouse position.
     */
    private void updateMouseCursor(Point mousePoint) {
        if (!editStatus.isMenuActive() && picRect.contains(mousePoint)) {
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

        if (picRect.contains(mousePoint) || !screenRect.contains(mousePoint)) {
            int x = editStatus.getMouseX();
            int y = editStatus.getMouseY();

            if (editStatus.getNumOfClicks() > 0) {
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

                    // Adjust new x/y pos back to screen coords.
                    x = ((x << 1) * editStatus.getZoomFactor()) + diffX;
                    y = ((y + 9) * editStatus.getZoomFactor()) + diffY;

                    // Use robot to move the mouse cursor.
                    robot.mouseMove(x, y);
                }
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
            // Is the mouse click within the picture?
            if (picRect.contains(mousePoint) && (editStatus.getTool() != ToolType.NONE)) {
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
                        editStatus.addPictureCode(0xF8);
                    }
                    editStatus.addPictureCode(x);
                    editStatus.addPictureCode(y);
                } else if (editStatus.isLineActive()) {
                    switch (editStatus.getNumOfClicks()) {
                        case 1:
                            editStatus.addPictureCode(0xF6);
                            editStatus.addPictureCode(x);
                            editStatus.addPictureCode(y);
                            picture.putPixel(x, y);
                            picture.updateScreen();
                            break;
                        default:
                            editStatus.addPictureCode(x);
                            editStatus.addPictureCode(y);
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
                            editStatus.addPictureCode(0xF7);
                            editStatus.addPictureCode(x);
                            editStatus.addPictureCode(y);
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
                            editStatus.addPictureCode(disp);
                            picture.drawLine(previousX, previousY, x, y);
                            picture.updateScreen();
                            editStatus.setClickPoint(new Point(x, y));
                            break;
                    }
                } else if (editStatus.isBrushActive()) {
                    int patNum = 0;

                    if (editStatus.isFirstClick()) {
                        editStatus.addPictureCode(0xF9);
                        editStatus.addPictureCode(editStatus.getBrushCode());
                        editStatus.addPictureCode(0xFA);
                    }
                    patNum = (((new java.util.Random().nextInt(255)) % 0xEE) >> 1) & 0x7F;
                    picture.plotPattern(patNum, x, y);
                    picture.updateScreen();
                    if (editStatus.getBrushTexture() == BrushTexture.SPRAY) {
                        editStatus.addPictureCode(patNum << 1);
                    }
                    editStatus.addPictureCode(x);
                    editStatus.addPictureCode(y);
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
                                editStatus.addPictureCode(0xF5);
                                editStatus.addPictureCode(previousX);
                                editStatus.addPictureCode(previousY);
                                editStatus.addPictureCode(x);
                            } else {
                                x = previousX;
                                editStatus.setStepType(StepType.YCORNER);
                                editStatus.addPictureCode(0xF4);
                                editStatus.addPictureCode(previousX);
                                editStatus.addPictureCode(previousY);
                                editStatus.addPictureCode(y);
                            }
                            picture.drawLine(previousX, previousY, x, y);
                            picture.updateScreen();
                            editStatus.setClickPoint(new Point(x, y));
                            break;

                        default:
                            if ((editStatus.isXCornerActive() && ((editStatus.getNumOfClicks() % 2) > 0)) || (editStatus.isYCornerActive() && ((editStatus.getNumOfClicks() % 2) == 0))) {
                                // X and Y corners toggle different direction based on number of clicks.	
                                x = previousX;
                                editStatus.addPictureCode(y);
                            } else {
                                y = previousY;
                                editStatus.addPictureCode(x);
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
            if (picRect.contains(mousePoint)) {
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
                        editStatus.addPictureCode(0xF5);
                        editStatus.addPictureCode(previousX);
                        editStatus.addPictureCode(previousY);
                    } else {
                        editStatus.addPictureCode(0xF4);
                        editStatus.addPictureCode(previousX);
                        editStatus.addPictureCode(previousY);
                    }
                    
                    picture.putPixel(previousX, previousY);
                    picture.updateScreen();
                }
                if (editStatus.getNumOfClicks() > 0) {
                    // If a tool is active (i.e. has a least one click) then right click resets 
                    // number of clicks, which allows the user to move to new location.
                    editStatus.clearTool();
                } else {
                    // If no clicks performed yet then a right click sets tool to None.
                    editStatus.setTool(ToolType.NONE);
                }
                picture.updateScreen();
            }
        }
    }

    /**
     * The Pen tool has a very short distance between two points. This
     * method is used for checking those limits.
     * 
     * @param value the value to adjust. Will either be an X or Y value.
     * @param limit the limit in both directs that to enforce.
     */
    private int adjustForPen(int value, int limit) {
        if (value > limit)
            value = limit;
        if (value < -limit)
            value = -limit;
        return value;
    }
}
