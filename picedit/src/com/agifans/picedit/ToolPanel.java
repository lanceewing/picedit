package com.agifans.picedit;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * The tool panel that is displayed at the bottom of the picture.
 * 
 * @author Lance Ewing
 */
public class ToolPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * The PicEdit application that this ToolPanel belongs to.
     */
    private PicEdit application;
    
    /**
     * The PicGraphics used by the tool panel (this is a temporary solution).
     */
    private PicGraphics picGraphics;
    
    /**
     * The offscreen image used to prepare the toolbar panel before displaying it.
     */
    private Image offScreenImage;

    /**
     * The Graphics instance associated with the offscreen image.
     */
    private Graphics2D offScreenGC;
    
    /**
     * Constructor for ToolPanel.
     * 
     * @param application The PicEdit application that this ToolPanel belongs to.
     */
    public ToolPanel(PicEdit application) {
        this.application = application;
        this.picGraphics = new PicGraphics(320, 23, this, 25);
        
        drawInterface();
        
        ToolPanelMouseHandler mouseHandler = new ToolPanelMouseHandler(application.getEditStatus(), picGraphics, application.getPicture(), application);
        this.addMouseListener(mouseHandler);
    }
    
    /**
     * Paints the tool bar.
     * 
     * @param graphics the Graphics object to paint on.
     */
    public void paint(Graphics graphics) {
        EditStatus editStatus = application.getEditStatus();
        Picture picture = application.getPicture();
      
        // Create the offscreen image the first time.
        if (offScreenImage == null) {
            offScreenImage = createImage(640, 46);
            offScreenGC = (Graphics2D) offScreenImage.getGraphics();
        }
        
        // Update the brush panel if required.
        if (editStatus.getLastRenderedBrushCode() != editStatus.getBrushCode()) {
            editStatus.setLastRenderedBrushCode(editStatus.getBrushCode());
            updateBrushPanel();
        }

        // Update the picture position.
        if (editStatus.getLastRenderedPicturePosition() != picture.getPicturePosition()) {
            editStatus.setLastRenderedPicturePosition(picture.getPicturePosition());
            updatePositionBox();
        }
        
        offScreenGC.drawImage(this.picGraphics.getScreenImage(), 0, 0, 640, 46, this);
        
        graphics.drawImage(offScreenImage, 0, 0, this);
    }
    
    /**
     * Displays the current buffer position in the relevant field on the 
     * interface. This functions also displays the next six PICTURE codes 
     * if the buffer position is somewhere other than at the end of the
     * picture. This is handy when deleting an action.
     */
    public void updatePositionBox() {
        Picture picture = application.getPicture();
      
        // Draw the current position on the control panel.
        String tempString = String.format("%5d", picture.getPicturePosition());
        picGraphics.drawFilledBox(230, 2, 273, 10, 0);
        picGraphics.drawString(tempString, 230, 3, 7, 0);

        // Clear the picture code data box.
        picGraphics.drawFilledBox(210, 12, 317, 20, 0);

        // Now draw the next six picture codes.
        LinkedList<PictureCode> pictureCodes = picture.getPictureCodes();
        int startIndex = picture.getPicturePosition();
        int endIndex = startIndex + 6;
        endIndex = (endIndex > pictureCodes.size() ? pictureCodes.size() : endIndex);
        for (int index = startIndex; index < endIndex; index++) {
            int code = pictureCodes.get(index).getCode();
            tempString = String.format("%02X", code);
            if (code >= 0xF0) {
                picGraphics.drawString(tempString, 211 + ((index - startIndex) * 16), 13, 4, 0);
            } else {
                picGraphics.drawString(tempString, 211 + ((index - startIndex) * 16), 13, 7, 0);
            }
        }
    }

    /**
     * Draws/Updates the brush panel.
     */
    private void updateBrushPanel() {
        EditStatus editStatus = application.getEditStatus();

        // Draw up/down arrows for adjusting size of the brush.
        picGraphics.drawChar((char) 24, 201, 3, 8, 7);
        picGraphics.drawChar((char) 25, 201, 13, 8, 7);
        picGraphics.drawLine(201, 11, 209, 11, 8);
        picGraphics.drawLine(200, 1, 200, 21, 8);

        // Draw black area for the brush size and then draw the size value.
        picGraphics.drawFilledBox(189, 2, 199, 20, 0);
        picGraphics.drawChar((char) (0x30 + editStatus.getBrushSize()), 191, 7, 7, 0);

        // Work out the colours to use for the brush options.
        int circleColour = (editStatus.isCircleBrush() ? 15 : 8);
        int squareColour = (editStatus.isSquareBrush() ? 15 : 8);
        int solidColour = (editStatus.isSolidBrush() ? 15 : 8);
        int sprayColour = (editStatus.isSprayBrush() ? 15 : 8);

        // Draw the brush option icons.
        picGraphics.drawChar((char) 7, 168, 3, circleColour, 7);
        picGraphics.drawChar((char) 254, 168, 12, squareColour, 7);
        picGraphics.drawChar((char) 176, 179, 3, sprayColour, 7);
        picGraphics.drawChar((char) 219, 179, 12, solidColour, 7);

        // Draw the boxes around the brush option icons.
        picGraphics.drawBox(167, 1, 188, 21, 8);
        picGraphics.drawBox(188, 1, 209, 21, 8);
        picGraphics.drawLine(176, 1, 176, 21, 8);
    }
    
    /**
     * Draws palette and buttons at the bottom of the screen.
     */
    private void drawInterface() {
        int c;

        // Draw the background for the tool panel.
        picGraphics.drawFilledBox(0, 0, 319, 22, 7);

        // Draw each of the tool buttons.
        picGraphics.drawString("Line", 3, 3, 8, 7);
        picGraphics.drawBox(1, 1, 34, 12, 8);
        picGraphics.drawString("Pen", 36, 3, 8, 7);
        picGraphics.drawBox(34, 1, 59, 12, 8);
        picGraphics.drawString("Step", 61, 3, 8, 7);
        picGraphics.drawBox(59, 1, 93, 12, 8);
        picGraphics.drawString("Fill", 95, 3, 8, 7);
        picGraphics.drawBox(93, 1, 125, 12, 8);
        picGraphics.drawString("Brush", 127, 3, 8, 7);
        picGraphics.drawBox(125, 1, 167, 12, 8);

        // Draw the colour part of the palette section.
        picGraphics.drawBox(1, 12, 167, 21, 8);
        picGraphics.drawLine(147, 12, 147, 21, 8);
        for (c = 0; c < 16; c++) {
            picGraphics.drawFilledBox(2 + c * 9, 13, 11 + c * 9, 20, c);
        }

        // Draw the OFF button part of the palette section.
        picGraphics.drawLine(151, 15, 152, 15, 8);
        picGraphics.drawLine(151, 18, 152, 18, 8);
        picGraphics.drawLine(150, 16, 150, 17, 8);
        picGraphics.drawLine(153, 16, 153, 17, 8);
        picGraphics.drawLine(155, 15, 158, 15, 8);
        picGraphics.drawLine(155, 15, 155, 18, 8);
        picGraphics.drawLine(155, 17, 157, 17, 8);
        picGraphics.drawLine(160, 15, 163, 15, 8);
        picGraphics.drawLine(160, 15, 160, 18, 8);
        picGraphics.drawLine(160, 17, 162, 17, 8);

        // Draw border around the navigation section.
        picGraphics.drawBox(209, 1, 294, 11, 8);

        // Draw the navigation buttons.
        picGraphics.drawChar((char) 174, 211, 3, 8, 7);
        picGraphics.drawChar((char) 60, 222, 3, 8, 7);
        picGraphics.drawChar((char) 62, 276, 3, 8, 7);
        picGraphics.drawChar((char) 175, 285, 3, 8, 7);
        picGraphics.drawLine(220, 1, 220, 11, 8);
        picGraphics.drawLine(229, 1, 229, 11, 8);
        picGraphics.drawLine(283, 1, 283, 11, 8);
        picGraphics.drawLine(274, 1, 274, 11, 8);

        // Draw the delete action button.
        picGraphics.drawString("Del", 296, 3, 8, 7);
        picGraphics.drawBox(294, 1, 318, 11, 8);

        // Draw 6 byte picture code section.
        picGraphics.drawFilledBox(210, 12, 317, 20, 0);
        picGraphics.drawBox(209, 11, 318, 21, 8);
        picGraphics.drawFilledBox(230, 2, 273, 10, 0);
    }
    
    /**
     * Mouse handler for the Tool Panel.
     */
    class ToolPanelMouseHandler extends CommonHandler implements MouseListener {
      
        // -------------------- Bound boxes for buttons --------------------
        private Rectangle lineButton;
        private Rectangle penButton;
        private Rectangle stepButton;
        private Rectangle fillButton;
        private Rectangle brushButton;
        private Rectangle paletteRect;
        private Rectangle offButton;
        private Rectangle circleRect;
        private Rectangle squareRect;
        private Rectangle sprayRect;
        private Rectangle solidRect;
        private Rectangle upButton;
        private Rectangle downButton;
        private Rectangle posRect;
        private Rectangle delRect;
        private Rectangle leftButton;
        private Rectangle rightButton;
        private Rectangle homeButton;
        private Rectangle endButton;
      
        /**
         * Constructor for ToolPanelMouseHandler.
         * 
         * @param editStatus
         * @param picGraphics
         * @param picture
         * @param application
         */
        ToolPanelMouseHandler(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
            super(editStatus, picGraphics, picture, application);
            
            // Create the bounding boxes for all of the UI buttons.
            createBoundingBoxes();
        }
      
        /**
         * Creates the bounding rectangles for each of the 'buttons' on 
         * the PICEDIT screen.
         */
        private void createBoundingBoxes() {
            lineButton = new Rectangle(2, 2, 32, 10);
            penButton = new Rectangle(35, 2, 24, 10);
            stepButton = new Rectangle(60, 2, 33, 10);
            fillButton = new Rectangle(94, 2, 31, 10);
            brushButton = new Rectangle(126, 2, 41, 10);
            paletteRect = new Rectangle(2, 13, 145, 8);
            offButton = new Rectangle(148, 13, 19, 8);
            circleRect = new Rectangle(168, 2, 8, 9);
            squareRect = new Rectangle(168, 12, 8, 9);
            sprayRect = new Rectangle(177, 2, 11, 9);
            solidRect = new Rectangle(177, 12, 11, 9);
            upButton = new Rectangle(201, 2, 8, 9);
            downButton = new Rectangle(201, 12, 8, 9);
            homeButton = new Rectangle(210, 2, 10, 9);
            leftButton = new Rectangle(221, 2, 8, 9);
            rightButton = new Rectangle(275, 2, 8, 9);
            endButton = new Rectangle(284, 2, 10, 9);
            delRect = new Rectangle(295, 2, 23, 9);
            posRect = new Rectangle(230, 2, 44, 9);
        }

        /**
         * Invoked when a mouse button is clicked on the tool bar.
         * 
         * @param event the mouse click event.
         */
        public void mouseClicked(MouseEvent e) {
            // Ignored. Mouse clicks are handled by the pressed event.
        }

        /**
         * Invoked when the mouse enters the PICEDIT toolbar.
         * 
         * @param event the mouse entered event.
         */
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * Invoked when the mouse exits the PICEDIT toolbar.
         * 
         * @param event the mouse exited event.
         */
        public void mouseExited(MouseEvent e) {
        }
        
        /**
         * Invoked when a mouse button is pressed down.
         * 
         * @param event the mouse pressed event.
         */
        public void mousePressed(MouseEvent event) { 
            // Toolbar is stretched double in both directions, so adjust the point.
            Point mousePoint = new Point(event.getX() / 2, event.getY() / 2);
            
            if (editStatus.isPaused()) {
                // If paused then ignore mouse clicks.
            } else if (editStatus.isMenuActive()) {
                // If menu was active and we received a mouse click, then set menu active false again.
                editStatus.setMenuActive(false);
            } else {
                processMouseClick(mousePoint, event.getButton());
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

            // Is it the LEFT mouse button?
            if (mouseButton == MouseEvent.BUTTON1) {
                // Process clicks on the tool selection buttons.
                if (lineButton.contains(mousePoint)) {
                    processToolSelect(ToolType.LINE);
                }
                if (penButton.contains(mousePoint)) {
                    processToolSelect(ToolType.PEN);
                }
                if (stepButton.contains(mousePoint)) {
                    processToolSelect(ToolType.STEP);
                }
                if (fillButton.contains(mousePoint)) {
                    processToolSelect(ToolType.FILL);
                }
                if (brushButton.contains(mousePoint)) {
                    processToolSelect(ToolType.BRUSH);
                }

                // Process clicks on the brush shape/texture/size buttons.
                if (upButton.contains(mousePoint)) {
                    editStatus.incrementBrushSize();
                }
                if (downButton.contains(mousePoint)) {
                    editStatus.decrementBrushSize();
                }
                if (circleRect.contains(mousePoint)) {
                    editStatus.setBrushShape(BrushShape.CIRCLE);
                }
                if (squareRect.contains(mousePoint)) {
                    editStatus.setBrushShape(BrushShape.SQUARE);
                }
                if (sprayRect.contains(mousePoint)) {
                    editStatus.setBrushTexture(BrushTexture.SPRAY);
                }
                if (solidRect.contains(mousePoint)) {
                    editStatus.setBrushTexture(BrushTexture.SOLID);
                }

                // Process left mouse button clicks on the palette and OFF buttons. Changes visual colour.
                if (paletteRect.contains(mousePoint)) {
                    processVisualColourChange(picGraphics.getPixel(x, y));
                }
                if (offButton.contains(mousePoint)) {
                    editStatus.setVisualColour(EditStatus.VISUAL_OFF);
                    picture.addPictureCode(0xF1);
                    picture.updateScreen();
                }

                // Process clicks on the delete picture action button.
                if (delRect.contains(mousePoint)) {
                    picture.processDeleteCurrentPictureAction();
                }

                // Process clicks on the picture navigation buttons.
                if (leftButton.contains(mousePoint)) {
                    picture.processMoveBackOnePictureAction();
                }
                if (rightButton.contains(mousePoint)) {
                    picture.processMoveForwardOnePictureAction();
                }
                if (homeButton.contains(mousePoint)) {
                    picture.processMoveToStartOfPictureBuffer();
                }
                if (endButton.contains(mousePoint)) {
                    picture.processMoveToEndOfPictureBuffer();
                }
                if (posRect.contains(mousePoint)) {
                    processEnterPosition();
                }
            }

            // Is it the RIGHT mouse button?
            if (mouseButton == MouseEvent.BUTTON3) {
                // Right-clicking on the palette sets the priority colour.
                if (paletteRect.contains(mousePoint)) {
                    int newPriorityColour = picGraphics.getPixel(x, y);
                    editStatus.setPriorityColour(newPriorityColour);
                    picture.addPictureCode(0xF2);
                    picture.addPictureCode(newPriorityColour);
                    picture.updateScreen();
                }
                // Right-clicking on the OFF button turns off the priority colour.
                if (offButton.contains(mousePoint)) {
                    editStatus.setPriorityColour(EditStatus.PRIORITY_OFF);
                    picture.addPictureCode(0xF3);
                    picture.updateScreen();
                }
            }
        }
        
        /**
         * Invoked when a mouse button is released.
         * 
         * @param event the mouse released event.
         */
        public void mouseReleased(MouseEvent e) {
        }
    }
}
