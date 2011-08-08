package com.agifans.picedit;

import java.awt.Graphics;
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
     * Constructor for ToolPanel.
     */
    public ToolPanel(EditStatus editStatus) {
        this.editStatus = editStatus;
        this.picGraphics = new PicGraphics();
        
        drawInterface();
    }
    
    /**
     * Paints the tool bar.
     * 
     * @param g the Graphics object to paint on.
     */
    public void paint(Graphics g) {
        
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
