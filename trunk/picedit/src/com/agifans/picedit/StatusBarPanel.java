package com.agifans.picedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
     * The PicEdit application. Provides access to active EditStatus.
     */
    private PicEdit application;
    
    /**
     * Constructor for StatusBarPanel.
     * 
     * @param editStatus The EditStatus holding current picture editor state.
     */
    public StatusBarPanel(final PicEdit application) {
        this.application = application;
        
        this.setLayout(new BorderLayout());
        
        JPanel fillerPanel = new JPanel();
        fillerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(fillerPanel, BorderLayout.CENTER);
        
        // TODO: Zoom factor?
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        StatusBarSection toolNamePanel = new StatusBarSection(200) {
            void drawSectionDetail(Graphics2D graphics) {
                graphics.setColor(EgaPalette.BLACK);
                graphics.drawString(String.format("%s", application.getEditStatus().getTool().toString()), 8, 15);
            }
        };
        
        StatusBarSection xPanel = new StatusBarSection(75) {
            void drawSectionDetail(Graphics2D graphics) {
                graphics.setColor(EgaPalette.BLACK);
                graphics.drawString(String.format("X: %-3d", application.getEditStatus().getMouseX()), 8, 15);
            }
        };
        
        StatusBarSection yPanel = new StatusBarSection(75) {
            void drawSectionDetail(Graphics2D graphics) {
                graphics.setColor(EgaPalette.BLACK);
                graphics.drawString(String.format("Y: %-3d", application.getEditStatus().getMouseY()), 8, 15);
            }
        };
        
        StatusBarSection priBandPanel = new StatusBarSection(200) {
            void drawSectionDetail(Graphics2D graphics) {
                EditStatus editStatus = application.getEditStatus();
                int priorityBand = editStatus.getPriorityBand();
                graphics.drawString("Priority: " + priorityBand, 8, 15);
                graphics.setColor(EgaPalette.COLOR_OBJECTS[priorityBand]);
                graphics.fillRect(70, 4, 18, 12);
            }
        };
        
        StatusBarSection positionPanel = new StatusBarSection(200) {
            void drawSectionDetail(Graphics2D graphics) {
                Picture picture = application.getPicture();
                int picturePosition = picture.getPicturePosition();
                int pictureSize = picture.getPictureCodes().size() - 1;
                String posStr = String.format("Position: %d/%d", picturePosition, pictureSize);
                graphics.drawString(posStr, 8, 15);
            }
        };
        
        JPanel gapPanel = new JPanel();
        gapPanel.setPreferredSize(new Dimension(100, 25));
        gapPanel.setMaximumSize(new Dimension(100, 25));
        gapPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        mainPanel.add(toolNamePanel);
        mainPanel.add(positionPanel);
        mainPanel.add(xPanel);
        mainPanel.add(yPanel);
        mainPanel.add(priBandPanel);
        //mainPanel.add(gapPanel);
        
        this.add(mainPanel, BorderLayout.WEST);
    }
    
    @SuppressWarnings("serial")
    abstract class StatusBarSection extends JPanel {
      
        StatusBarSection(int width) {
          this.setBorder(new BevelBorder(BevelBorder.LOWERED));
          this.setPreferredSize(new Dimension(width, 20));
          this.setMaximumSize(new Dimension(width, 20));
        }
      
        /**
         * Paints the status section.
         * 
         * @param g the Graphics object to paint on.
         */
        public void paint(Graphics g) {
            super.paint(g);
            drawSectionDetail((Graphics2D)g);
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
      
        abstract void drawSectionDetail(Graphics2D graphics);
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
        EditStatus editStatus = application.getEditStatus();
        int x = editStatus.getMouseX();
        int y = editStatus.getMouseY();
        int priorityBand = editStatus.getPriorityBand();
        int controlOffColour = (editStatus.getPictureType().equals(PictureType.AGI) ? 7 : 0);

        // Draw tool section.
        String toolStr = String.format("%s  ", editStatus.getTool().toString());
        graphics.setColor(EgaPalette.BLACK);
        graphics.setBackground(EgaPalette.WHITE);
        graphics.drawString(toolStr, 5, 15);
        editStatus.setLastRenderedTool(editStatus.getTool());

//        // Draw visual colour section.
//        int visualColour = (editStatus.getVisualColour() == EditStatus.TRANSPARENT ? 15 : editStatus.getVisualColour());
//        graphics.drawString("V", 85, 15);
//        if (visualColour == EditStatus.VISUAL_OFF) {
//            graphics.setColor(EgaPalette.BLACK);
//            graphics.drawOval(100, 5, 9, 9);
//        } else {
//            graphics.setColor(EgaPalette.COLOR_OBJECTS[visualColour]);
//            graphics.fillRect(96, 4, 18, 12);
//        }
//        graphics.setColor(EgaPalette.BLACK);
//        editStatus.setLastRenderedVisualColour(visualColour);
//
//        // Draw priority colour section.
//        int priorityColour = (editStatus.getPriorityColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getPriorityColour());
//        graphics.drawString("P", 118, 15);
//        if (priorityColour == EditStatus.PRIORITY_OFF) {
//            graphics.setColor(EgaPalette.BLACK);
//            graphics.drawOval(133, 5, 9, 9);
//        } else {
//            graphics.setColor(EgaPalette.COLOR_OBJECTS[priorityColour]);
//            graphics.fillRect(128, 4, 18, 12);
//        }
//        graphics.setColor(EgaPalette.COLOR_OBJECTS[controlOffColour]);
//        editStatus.setLastRenderedPriorityColour(priorityColour);
//
//        // Draw control colour section.
//        int controlColour = (editStatus.getControlColour() == EditStatus.TRANSPARENT ? 4 : editStatus.getControlColour());
//        graphics.drawString("C", 151, 15);
//        if (controlColour == EditStatus.CONTROL_OFF) {
//            graphics.setColor(EgaPalette.COLOR_OBJECTS[controlOffColour]);
//            graphics.drawOval(166, 5, 9, 9);
//        } else {
//            graphics.setColor(EgaPalette.COLOR_OBJECTS[controlColour]);
//            graphics.fillRect(162, 4, 18, 12);
//        }
//        editStatus.setLastRenderedControlColour(controlColour);

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
