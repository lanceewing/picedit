package com.agifans.picedit;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

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
        //this.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        this.setLayout(new GridBagLayout());
        
        // TODO: Zoom factor?
        
        JPanel toolNamePanel = new JPanel();
        toolNamePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        toolNamePanel.add(new JLabel("Tool"));
        
        JPanel xPanel = new JPanel();
        xPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        xPanel.add(new JLabel("X=100"));
        
        JPanel yPanel = new JPanel();
        yPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        yPanel.add(new JLabel("Y=25"));
        
        JPanel priBandPanel = new JPanel();
        priBandPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        priBandPanel.add(new JLabel("Priority:13"));
        
        JPanel positionPanel = new JPanel();
        positionPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        positionPanel.add(new JLabel("1200"));
        
        JPanel fillerPanel = new JPanel();
        fillerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        GridBagConstraints fillerConstraints = new GridBagConstraints();
        fillerConstraints.weightx = 1.0;
        
        this.add(fillerPanel);
        this.add(toolNamePanel);
        this.add(positionPanel);
        this.add(xPanel);
        this.add(yPanel);
        this.add(priBandPanel);
    }
    
//    /**
//     * Paints the status bar.
//     * 
//     * @param g the Graphics object to paint on.
//     */
//    public void paint(Graphics g) {
//        super.paint(g);
//        drawStatusBar((Graphics2D)g);
//    }
    
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
        String toolStr = String.format("Tool: %-5s  ", editStatus.getTool().toString());
        graphics.setColor(EgaPalette.BLACK);
        graphics.setBackground(EgaPalette.WHITE);
        graphics.drawString(toolStr, 5, 15);
        editStatus.setLastRenderedTool(editStatus.getTool());

        // Draw visual colour section.
        int visualColour = (editStatus.getVisualColour() == EditStatus.TRANSPARENT ? 15 : editStatus.getVisualColour());
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

        // Draw priority colour section.
        int priorityColour = (editStatus.getPriorityColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getPriorityColour());
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

        // Draw control colour section.
        int controlColour = (editStatus.getControlColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getControlColour());
        graphics.drawString("C", 151, 15);
        if (controlColour == EditStatus.CONTROL_OFF) {
            graphics.setColor(EgaPalette.COLOR_OBJECTS[controlOffColour]);
            graphics.drawOval(166, 5, 9, 9);
        } else {
            graphics.setColor(EgaPalette.COLOR_OBJECTS[controlColour]);
            graphics.fillRect(162, 4, 18, 12);
        }
        editStatus.setLastRenderedControlColour(controlColour);

        // Draw X/Y position and priority band section.
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
    }
}
