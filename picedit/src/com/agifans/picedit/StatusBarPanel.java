package com.agifans.picedit;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * The status bar that appears above the picture showing the current mouse position,
 * tool, visual colour, priority colour, etc.
 * 
 * @author Lance Ewing
 */
public class StatusBarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Holds the current "editing" state of everything within PICEDIT.
     */
    private EditStatus editStatus;
    
    /**
     * Constructor for StatusBarPanel.
     * 
     * @param editStatus The EditStatus holding current picture editor state.
     */
    public StatusBarPanel(EditStatus editStatus) {
        this.editStatus = editStatus;
    }
    
    /**
     * Paints the status bar. A double buffering mechanism is implemented
     * in order to reduce flicker as much as possible.
     * 
     * @param g the Graphics object to paint on.
     */
    public void paint(Graphics g) {
        drawStatusBar((Graphics2D)g);
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
     * 
     * @param graphics The Graphics2D to draw on.
     */
    public void drawStatusBar(Graphics2D graphics) {
        int x = editStatus.getMouseX();
        int y = editStatus.getMouseY();
        int priorityBand = editStatus.getPriorityBand();
        int controlOffColour = (editStatus.getPictureType().equals(PictureType.AGI) ? 7 : 0);

        // Draw tool section.
        //if (!editStatus.getTool().equals(editStatus.getLastRenderedTool())) {
            String toolStr = String.format("Tool: %-5s  ", editStatus.getTool().toString());
            graphics.setColor(EgaPalette.BLACK);
            graphics.setBackground(EgaPalette.WHITE);
            graphics.drawString(toolStr, 5, 15);
            editStatus.setLastRenderedTool(editStatus.getTool());
        //}

        // Draw visual colour section.
        int visualColour = (editStatus.getVisualColour() == EditStatus.TRANSPARENT ? 15 : editStatus.getVisualColour());
        //if (editStatus.getLastRenderedVisualColour() != visualColour) {
            graphics.drawString("V", 85, 15);
            if (visualColour == EditStatus.VISUAL_OFF) {
                graphics.setColor(EgaPalette.BLACK);
                graphics.drawOval(100, 5, 9, 9);
            } else {
                graphics.setColor(EgaPalette.COLOR_OBJECTS[visualColour]);
                graphics.fillRect(96, 4, 18, 12);
            }
            graphics.setColor(EgaPalette.BLACK);
            editStatus.setLastRenderedVisualColour(visualColour);
        //}

        // Draw priority colour section.
        int priorityColour = (editStatus.getPriorityColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getPriorityColour());
        //if (editStatus.getLastRenderedPriorityColour() != priorityColour) {
            graphics.drawString("P", 118, 15);
            if (priorityColour == EditStatus.PRIORITY_OFF) {
                graphics.setColor(EgaPalette.BLACK);
                graphics.drawOval(133, 5, 9, 9);
            } else {
                graphics.setColor(EgaPalette.COLOR_OBJECTS[priorityColour]);
                graphics.fillRect(128, 4, 18, 12);
            }
            graphics.setColor(EgaPalette.COLOR_OBJECTS[controlOffColour]);
            editStatus.setLastRenderedPriorityColour(priorityColour);
        //}

        // Draw control colour section.
        int controlColour = (editStatus.getControlColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getControlColour());
        //if (editStatus.getLastRenderedControlColour() != controlColour) {
            graphics.drawString("C", 151, 15);
            if (controlColour == EditStatus.CONTROL_OFF) {
                graphics.setColor(EgaPalette.COLOR_OBJECTS[controlOffColour]);
                graphics.drawOval(166, 5, 9, 9);
            } else {
                graphics.setColor(EgaPalette.COLOR_OBJECTS[controlColour]);
                graphics.fillRect(162, 4, 18, 12);
            }
            editStatus.setLastRenderedControlColour(controlColour);
        //}

        // Draw X/Y position and priority band section.
        //if ((editStatus.getLastRenderedMouseX() != x) || (editStatus.getLastRenderedMouseY() != y)) {
            String xStr = String.format("X=%-3d", x);
            String yStr = String.format("Y=%-3d", y);
            graphics.setColor(EgaPalette.BLACK);
            graphics.drawString(xStr, 184, 15);
            graphics.drawString(yStr, 232, 15);
            graphics.drawString("Pri", 280, 15);
            graphics.setColor(EgaPalette.COLOR_OBJECTS[priorityBand]);
            graphics.fillRect(300, 4, 18, 12);
            editStatus.setLastRenderedMouseX(x);
            editStatus.setLastRenderedMouseY(y);
        //}
    }
}
