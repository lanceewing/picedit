package com.agifans.picedit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * This panel renders the main picture editor image, which includes the 
 * picture being edited, the status line, and tool bar. This code was
 * originally in the PicEdit class but has now been split out into a 
 * separate panel to allow other Swing components to appear within the
 * main applet, including things like a proper menu, scroll bars, etc.
 * 
 * @author Lance Ewing
 */
public class PicturePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    /**
     * The graphics routines with which the application draws the screen.
     */
    private PicGraphics picGraphics;

    /**
     * The AGI picture being edited.
     */
    private Picture picture;

    /**
     * Holds the current "editing" state of everything within PICEDIT.
     */
    private EditStatus editStatus;

    /**
     * The PICEDIT application component.
     */
    private PicEdit application;
    
    /**
     * The offscreen image used to prepare the PICEDIT screen before displaying it.
     */
    private Image offScreenImage;

    /**
     * The Graphics instance associated with the offscreen image.
     */
    private Graphics2D offScreenGC;
    
    /**
     * Constructor for PicturePanel.
     * 
     * @param editStatus the EditStatus holding current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param application the PICEDIT application component.
     */
    public PicturePanel(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
        this.editStatus = editStatus;
        this.picGraphics = picGraphics;
        this.picture = picture;
        this.application = application;
        
        Dimension appDimension = new Dimension(320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
        this.setPreferredSize(appDimension);
        
        // Draws the PICEDIT interface.
        drawInterface();
        
        Menu menu = new Menu(editStatus, picGraphics, picture, application);
        MouseHandler mouseHandler = new MouseHandler(editStatus, picGraphics, picture, menu, application);
        KeyboardHandler keyboardHandler = new KeyboardHandler(editStatus, picGraphics, picture, menu, application);
        
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);
        this.application.addKeyListener(keyboardHandler);
    }
    
    /**
     * Invoked by resizeScreen when the offscreen image needs to be recreated at
     * a new screen size.
     */
    public void resizeOffscreenImage() {
        // If we remove it then paint will recreate it at the new size.
        this.offScreenImage = null;
    }
    
    /**
     * Paints the PICEDIT screen. A double buffering mechanism is implemented
     * in order to reduce flicker as much as possible.
     * 
     * @param g the Graphics object to paint on.
     */
    public void paint(Graphics g) {
        // Create the offscreen image the first time.
        if (offScreenImage == null) {
            offScreenImage = createImage(320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
            offScreenGC = (Graphics2D) offScreenImage.getGraphics();
        }

        // If we're in text mode ("Help" and "View Data"), then just display the image.
        if (editStatus.isTextMode()) {
            offScreenGC.drawImage(picGraphics.getTextImage(), 0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor(), this);
        } else {
            // Other display the PICEDIT screen.

            // Make sure we are displaying the latest status information.
            if (!editStatus.isMenuActive()) {
                drawStatusLine();
            }

            // Update the brush panel if required.
            if (editStatus.getLastRenderedBrushCode() != editStatus.getBrushCode()) {
                editStatus.setLastRenderedBrushCode(editStatus.getBrushCode());
                updateBrushPanel();
            }

            // Update the picture position.
            if (editStatus.getLastRenderedPicturePosition() != editStatus.getPicturePosition()) {
                editStatus.setLastRenderedPicturePosition(editStatus.getPicturePosition());
                updatePositionBox();
            }

            // Draw the background image (if there is one) to the offscreen image.
            if ((picGraphics.getBackgroundImage() != null) && (editStatus.isBackgroundEnabled())) {
                offScreenGC.drawImage(picGraphics.getBackgroundImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), 168 * editStatus.getZoomFactor(), this);
            } else {
                // Otherwise use the default background colour for the corresponding AGI screen (visual/priority).
                if (editStatus.isDualModeEnabled()) {
                    offScreenGC.setColor(EgaPalette.RED);
                } else if (!editStatus.isPriorityShowing()) {
                    offScreenGC.setColor(EgaPalette.WHITE);
                } else if (editStatus.isBandsOn()) {
                    offScreenGC.setColor(EgaPalette.DARKGREY);
                } else {
                    offScreenGC.setColor(EgaPalette.RED);
                }
                offScreenGC.fillRect(0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
            }

            if (editStatus.isDualModeEnabled()) {
                // Dual mode is when the priority and visual screens mix.
                offScreenGC.drawImage(picture.getPriorityImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

                // To create the effect demonstrated by Joakim in APE, we need a solid white.
                BufferedImage tmpVisualImage = new BufferedImage(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), BufferedImage.TYPE_INT_ARGB);
                Graphics tmpVisualGraphics = tmpVisualImage.getGraphics();
                tmpVisualGraphics.setColor(EgaPalette.WHITE);
                tmpVisualGraphics.fillRect(0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
                tmpVisualGraphics.drawImage(picture.getVisualImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

                // Build a RescapeOp to perform the 50% transparency.
                float[] scales = { 1f, 1f, 1f, 0.5f };
                float[] offsets = new float[4];
                RescaleOp rop = new RescaleOp(scales, offsets, null);

                // Draw the visual screen on top of the priority screen with 50% transparency.
                offScreenGC.drawImage(tmpVisualImage, rop, 0, 9 * editStatus.getZoomFactor());

            } else {
                if (editStatus.isPriorityShowing()) {
                    offScreenGC.drawImage(picture.getPriorityImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
                } else {
                    offScreenGC.drawImage(picture.getVisualImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
                }
            }

            if (editStatus.isBandsOn()) {
                offScreenGC.drawImage(picGraphics.getPriorityBandsImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
            }
            
            // Draw the  PICEDIT screen to the offscreen image (transparent pixels will show the background).
            offScreenGC.drawImage(picGraphics.getScreenImage(), 0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor(), this);
        }

        // Now display the screen to the user.
        g.drawImage(offScreenImage, 0, 0, this);    
    }
    
    /**
     * Override the default update behaviour to stop the screen from being
     * cleared each time.
     * 
     * @param g the Graphics object to update.
     */
    public void update(Graphics g) {
        paint(g);
    }
    
    /**
     * Draws the status line based on the editing state within the EditStatus.
     */
    public void drawStatusLine() {
        int x = editStatus.getMouseX();
        int y = editStatus.getMouseY();
        int priorityBand = editStatus.getPriorityBand();
        int controlOffColour = (editStatus.getPictureType().equals(PictureType.AGI) ? 7 : 0);

        // Draw tool section.
        if (!editStatus.getTool().equals(editStatus.getLastRenderedTool())) {
            String toolStr = String.format("Tool:%-5s V", editStatus.getTool().toString());
            picGraphics.drawString(toolStr, 0, 0, 0, 15);
            editStatus.setLastRenderedTool(editStatus.getTool());
        }

        // Draw visual colour section.
        int visualColour = (editStatus.getVisualColour() == EditStatus.TRANSPARENT ? 15 : editStatus.getVisualColour());
        if (editStatus.getLastRenderedVisualColour() != visualColour) {
            if (visualColour == EditStatus.VISUAL_OFF) {
                picGraphics.drawFilledBox(96, 0, 99, 7, 15);
                picGraphics.drawChar((char) 9, 100, 0, 0, 15);
                picGraphics.drawFilledBox(108, 0, 111, 7, 15);
            } else {
                picGraphics.drawString("  ", 96, 0, 0, visualColour);
            }
            picGraphics.drawString(" P", 112, 0, 0, 15);
            editStatus.setLastRenderedVisualColour(visualColour);
        }

        // Draw priority colour section.
        int priorityColour = (editStatus.getPriorityColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getPriorityColour());
        if (editStatus.getLastRenderedPriorityColour() != priorityColour) {
            if (priorityColour == EditStatus.PRIORITY_OFF) {
                picGraphics.drawFilledBox(128, 0, 131, 7, 15);
                picGraphics.drawChar((char) 9, 132, 0, 0, 15);
                picGraphics.drawFilledBox(140, 0, 143, 7, 15);
            } else {
                picGraphics.drawString("  ", 128, 0, 0, priorityColour);
            }
            picGraphics.drawString(" C", 144, 0, controlOffColour, 15);
            editStatus.setLastRenderedPriorityColour(priorityColour);
        }

        // Draw control colour section.
        int controlColour = (editStatus.getControlColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getControlColour());
        if (editStatus.getLastRenderedControlColour() != controlColour) {
            if (controlColour == EditStatus.CONTROL_OFF) {
                picGraphics.drawFilledBox(160, 0, 163, 7, 15);
                picGraphics.drawChar((char) 9, 164, 0, controlOffColour, 15);
                picGraphics.drawFilledBox(172, 0, 175, 7, 15);
            } else {
                picGraphics.drawString("  ", 160, 0, 0, controlColour);
            }
            editStatus.setLastRenderedControlColour(controlColour);
        }

        // Draw X/Y position and priority band section.
        if ((editStatus.getLastRenderedMouseX() != x) || (editStatus.getLastRenderedMouseY() != y)) {
            String xyPriStr = String.format(" X=%-3d Y=%-3d Pri", x, y);
            picGraphics.drawString(xyPriStr, 176, 0, 0, 15);
            picGraphics.drawString("  ", 304, 0, 0, priorityBand);
            editStatus.setLastRenderedMouseX(x);
            editStatus.setLastRenderedMouseY(y);
        }
    }

    /**
     * Displays the current buffer position in the relevant field on the 
     * interface. This functions also displays the next six PICTURE codes 
     * if the buffer position is somewhere other than at the end of the
     * picture. This is handy when deleting an action.
     */
    public void updatePositionBox() {
        // Draw the current position on the control panel.
        String tempString = String.format("%5d", editStatus.getPicturePosition());
        picGraphics.drawFilledBox(230, 179, 273, 187, 0);
        picGraphics.drawString(tempString, 230, 180, 7, 0);

        // Clear the picture code data box.
        picGraphics.drawFilledBox(210, 189, 317, 197, 0);

        // Now draw the next six picture codes.
        LinkedList<PictureCode> pictureCodes = editStatus.getPictureCodes();
        int startIndex = editStatus.getPicturePosition();
        int endIndex = startIndex + 6;
        endIndex = (endIndex > pictureCodes.size() ? pictureCodes.size() : endIndex);
        for (int index = startIndex; index < endIndex; index++) {
            int code = pictureCodes.get(index).getCode();
            tempString = String.format("%02X", code);
            if (code >= 0xF0) {
                picGraphics.drawString(tempString, 211 + ((index - startIndex) * 16), 190, 4, 0);
            } else {
                picGraphics.drawString(tempString, 211 + ((index - startIndex) * 16), 190, 7, 0);
            }
        }
    }

    /**
     * Draws/Updates the brush panel.
     */
    private void updateBrushPanel() {
        int circleColour, squareColour, solidColour, sprayColour;

        // Draw up/down arrows for adjusting size of the brush.
        picGraphics.drawChar((char) 24, 201, 180, 8, 7);
        picGraphics.drawChar((char) 25, 201, 190, 8, 7);
        picGraphics.drawLine(201, 188, 209, 188, 8);
        picGraphics.drawLine(200, 178, 200, 198, 8);

        // Draw black area for the brush size and then draw the size value.
        picGraphics.drawFilledBox(189, 179, 199, 197, 0);
        picGraphics.drawChar((char) (0x30 + editStatus.getBrushSize()), 191, 184, 7, 0);

        // Work out the colours to use for the brush options.
        circleColour = (editStatus.isCircleBrush() ? 15 : 8);
        squareColour = (editStatus.isSquareBrush() ? 15 : 8);
        solidColour = (editStatus.isSolidBrush() ? 15 : 8);
        sprayColour = (editStatus.isSprayBrush() ? 15 : 8);

        // Draw the brush option icons.
        picGraphics.drawChar((char) 7, 168, 180, circleColour, 7);
        picGraphics.drawChar((char) 254, 168, 189, squareColour, 7);
        picGraphics.drawChar((char) 176, 179, 180, sprayColour, 7);
        picGraphics.drawChar((char) 219, 179, 189, solidColour, 7);

        // Draw the boxes around the brush option icons.
        picGraphics.drawBox(167, 178, 188, 198, 8);
        picGraphics.drawBox(188, 178, 209, 198, 8);
        picGraphics.drawLine(176, 178, 176, 198, 8);
    }

    /**
     * Draws palette and buttons at the bottom of the screen.
     */
    private void drawInterface() {
        int c;

        // Draw the blue line between the status bar and the picture.
        picGraphics.drawLine(0, 8, 319, 8, 1);

        // Draw the background for the tool panel.
        picGraphics.drawFilledBox(0, 177, 319, 199, 7);

        // Draw each of the tool buttons.
        picGraphics.drawString("Line", 3, 180, 8, 7);
        picGraphics.drawBox(1, 178, 34, 189, 8);
        picGraphics.drawString("Pen", 36, 180, 8, 7);
        picGraphics.drawBox(34, 178, 59, 189, 8);
        picGraphics.drawString("Step", 61, 180, 8, 7);
        picGraphics.drawBox(59, 178, 93, 189, 8);
        picGraphics.drawString("Fill", 95, 180, 8, 7);
        picGraphics.drawBox(93, 178, 125, 189, 8);
        picGraphics.drawString("Brush", 127, 180, 8, 7);
        picGraphics.drawBox(125, 178, 167, 189, 8);

        // Draw the colour part of the palette section.
        picGraphics.drawBox(1, 189, 167, 198, 8);
        picGraphics.drawLine(147, 189, 147, 198, 8);
        for (c = 0; c < 16; c++) {
            picGraphics.drawFilledBox(2 + c * 9, 190, 11 + c * 9, 197, c);
        }

        // Draw the OFF button part of the palette section.
        picGraphics.drawLine(151, 192, 152, 192, 8);
        picGraphics.drawLine(151, 195, 152, 195, 8);
        picGraphics.drawLine(150, 193, 150, 194, 8);
        picGraphics.drawLine(153, 193, 153, 194, 8);
        picGraphics.drawLine(155, 192, 158, 192, 8);
        picGraphics.drawLine(155, 192, 155, 195, 8);
        picGraphics.drawLine(155, 194, 157, 194, 8);
        picGraphics.drawLine(160, 192, 163, 192, 8);
        picGraphics.drawLine(160, 192, 160, 195, 8);
        picGraphics.drawLine(160, 194, 162, 194, 8);

        // Draw border around the navigation section.
        picGraphics.drawBox(209, 178, 294, 188, 8);

        // Draw the navigation buttons.
        picGraphics.drawChar((char) 174, 211, 180, 8, 7);
        picGraphics.drawChar((char) 60, 222, 180, 8, 7);
        picGraphics.drawChar((char) 62, 276, 180, 8, 7);
        picGraphics.drawChar((char) 175, 285, 180, 8, 7);
        picGraphics.drawLine(220, 178, 220, 188, 8);
        picGraphics.drawLine(229, 178, 229, 188, 8);
        picGraphics.drawLine(283, 178, 283, 188, 8);
        picGraphics.drawLine(274, 178, 274, 188, 8);

        // Draw the delete action button.
        picGraphics.drawString("Del", 296, 180, 8, 7);
        picGraphics.drawBox(294, 178, 318, 188, 8);

        // Draw 6 byte picture code section.
        picGraphics.drawFilledBox(210, 189, 317, 197, 0);
        picGraphics.drawBox(209, 188, 318, 198, 8);
        picGraphics.drawFilledBox(230, 179, 273, 187, 0);
    }
}
