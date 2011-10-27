package com.agifans.picedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
     * Constructor for StatusBarPanel.
     * 
     * @param editStatus The EditStatus holding current picture editor state.
     */
    public StatusBarPanel(final PicEdit application) {
        this.setLayout(new BorderLayout());
        
        JPanel fillerPanel = new JPanel();
        fillerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(fillerPanel, BorderLayout.CENTER);
        
        // TODO: Zoom factor?
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        StatusBarSection toolNamePanel = new StatusBarSection(200) {
            void drawSectionDetail(Graphics2D graphics) {
            	EditStatus editStatus = application.getEditStatus();
                graphics.setColor(EgaPalette.BLACK);
                String toolName = null;
                if (editStatus.getTool().equals(ToolType.AIRBRUSH) || editStatus.getTool().equals(ToolType.BRUSH)) {
                	toolName = BrushType.getBrushTypeForBrushCode(editStatus.getBrushCode()).getDisplayName();
                } else {
                	toolName = application.getEditStatus().getTool().toString();
                }
                graphics.drawString(toolName, 8, 15);
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
                graphics.fillRect(85, 4, 18, 12);
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
        
        StatusBarSection memoryPanel = new StatusBarSection(300) {
            void drawSectionDetail(Graphics2D graphics) {
              Runtime runtime = Runtime.getRuntime();
              String memStr = String.format("%d/%d/%d", runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory());
              graphics.drawString(memStr, 8, 15);
            }
        };
        
        mainPanel.add(toolNamePanel);
        mainPanel.add(positionPanel);
        mainPanel.add(xPanel);
        mainPanel.add(yPanel);
        mainPanel.add(priBandPanel);
        mainPanel.add(memoryPanel);
        
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
}
