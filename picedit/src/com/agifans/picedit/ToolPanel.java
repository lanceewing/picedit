package com.agifans.picedit;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
     * Holds the current "editing" state of everything within PICEDIT.
     */
    private EditStatus editStatus;
    
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
     */
    public ToolPanel(EditStatus editStatus) {
        this.editStatus = editStatus;
        this.picGraphics = new PicGraphics(320, 23);
        
        drawInterface();
    }
    
    /**
     * Paints the tool bar.
     * 
     * @param g the Graphics object to paint on.
     */
    public void paint(Graphics graphics) {
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
        if (editStatus.getLastRenderedPicturePosition() != editStatus.getPicturePosition()) {
            editStatus.setLastRenderedPicturePosition(editStatus.getPicturePosition());
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
        // Draw the current position on the control panel.
        String tempString = String.format("%5d", editStatus.getPicturePosition());
        picGraphics.drawFilledBox(230, 2, 273, 10, 0);
        picGraphics.drawString(tempString, 230, 3, 7, 0);

        // Clear the picture code data box.
        picGraphics.drawFilledBox(210, 12, 317, 20, 0);

        // Now draw the next six picture codes.
        LinkedList<PictureCode> pictureCodes = editStatus.getPictureCodes();
        int startIndex = editStatus.getPicturePosition();
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
        int circleColour, squareColour, solidColour, sprayColour;

        // Draw up/down arrows for adjusting size of the brush.
        picGraphics.drawChar((char) 24, 201, 3, 8, 7);
        picGraphics.drawChar((char) 25, 201, 13, 8, 7);
        picGraphics.drawLine(201, 11, 209, 11, 8);
        picGraphics.drawLine(200, 1, 200, 21, 8);

        // Draw black area for the brush size and then draw the size value.
        picGraphics.drawFilledBox(189, 2, 199, 20, 0);
        picGraphics.drawChar((char) (0x30 + editStatus.getBrushSize()), 191, 7, 7, 0);

        // Work out the colours to use for the brush options.
        circleColour = (editStatus.isCircleBrush() ? 15 : 8);
        squareColour = (editStatus.isSquareBrush() ? 15 : 8);
        solidColour = (editStatus.isSolidBrush() ? 15 : 8);
        sprayColour = (editStatus.isSprayBrush() ? 15 : 8);

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
}
