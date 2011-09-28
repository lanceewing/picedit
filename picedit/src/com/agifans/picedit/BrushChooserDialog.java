package com.agifans.picedit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Brush chooser dialog used when selecting brush and airbrush shapes and sizes.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class BrushChooserDialog extends JDialog {

    /**
     * Holds the brush that the user clicked on.
     */
    private int chosenBrush;
    
    /**
     * Holds the current mouse point.
     */
    private Point mousePoint;
    
    /**
     * Constructor for BrushChooserDialog.
     * 
     * @param button The button component under which the dialog will be drawn.
     * @param airBrush true if this is the air brush variant of the brush; otherwise false.
     */
    public BrushChooserDialog(Component button, final boolean airBrush) {
        this.setModal(true);
        this.setUndecorated(true);
        this.setSize(new Dimension(148, 148));
        this.setResizable(false);
        Point buttonLocation = button.getLocationOnScreen();
        this.setLocation(buttonLocation.x, buttonLocation.y + button.getSize().height);
        this.setAlwaysOnTop(true);
        this.setLayout(null);
        this.add(new BrushChooserButtonPanel(airBrush));
    }
    
    /**
     * Plots a brush for the given parameters.
     * 
     * @param x The X position to plot the brush at.
     * @param y The Y position to plot the brush at.
     * @param penSize The size of the brush (0-7).
     * @param isSquare true if the brush is square; false if circle.
     * @param isAirBrush true if the brush is an airbrush; false if solid brush.
     * @param graphics The Graphics to use to draw the brush with.
     */
    public void plotBrush(int x, int y, int penSize, boolean isSquare, boolean isAirBrush, Graphics graphics) {
        int circlePos = 0;
        //int bitPos = Picture.splatterStart[(new Random()).nextInt(120)];
        int bitPos = Picture.splatterStart[10];

        for (int y1 = (y + 8) - penSize; y1 <= (y + 8) + penSize; y1++) {
            for (int x1 = (x + 8) - penSize; x1 <= (x + 8) + penSize; x1+=2) {
                if (isSquare) {
                    if (isAirBrush) {
                        if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                            graphics.fillRect(x1<<1, y1<<1, 4, 2);
                        }
                        bitPos++;
                        if (bitPos == 0xff) {
                            bitPos = 0;
                        }
                    } else {
                        // Not an airbrush implies a solid brush.
                        graphics.fillRect(x1<<1, y1<<1, 4, 2);
                    }
                } else { 
                    // Not a square implies circle.
                    if (((Picture.circles[penSize][circlePos >> 3] >> (7 - (circlePos & 7))) & 1) > 0) {
                        if (isAirBrush) {
                            if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                                graphics.fillRect(x1<<1, y1<<1, 4, 2);
                            }
                            bitPos++;
                            if (bitPos == 0xff) {
                                bitPos = 0;
                            }
                        } else {
                            // Not an airbrush implies a solid brush.
                            graphics.fillRect(x1<<1, y1<<1, 4, 2);
                        }
                    }
                    circlePos++;
                }
            }
        }
    }
    
    /**
     * Returns the brush that the user clicked on.
     * 
     * @return The brush that the user clicked on.
     */
    public int getChosenBrush() {
        return chosenBrush;
    }
    
    /**
     * Panel for holding the brush buttons.
     */
    class BrushChooserButtonPanel extends JPanel {

        /**
         * Whether the brush is an air brush or not.
         */
        private boolean airBrush;
      
        /**
         * Constructor for BrushChooserButtonPanel.
         */
        BrushChooserButtonPanel(boolean airBrush) {
          this.setSize(new Dimension(144, 144));
          this.setLocation(2, 2);
          this.setBackground(Color.LIGHT_GRAY);
          
          this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
          
          ButtonGroup brushButtonGroup = new ButtonGroup();
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          this.add(new BrushChooserButton(brushButtonGroup));
          
          this.addMouseListener(new BrushChooserMouseListener());
          this.addMouseMotionListener(new BrushChooserMouseMotionListener());
        }
        
        class BrushChooserButton extends JToggleButton {
            BrushChooserButton(ButtonGroup buttonGroup) {
                super();
                Image pressedImage = null;
                Image hoveredImage = null;
                try {
                    pressedImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/pressed.png"));
                    hoveredImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/hovered.png"));
                } catch (IOException e) {
                }
                //setIcon(new ImageIcon(iconImage));
                //setSelectedIcon(new ImageIcon(mergeImages(iconImage, pressedImage)));
                //setRolloverIcon(new ImageIcon(mergeImages(iconImage, hoveredImage)));
                //setRolloverSelectedIcon(getSelectedIcon());
                //setPressedIcon(getSelectedIcon());
                setPreferredSize(new Dimension(32, 32));
                setMaximumSize(new Dimension(32, 32));
                setFocusable(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
                //setToolTipText();
                //setActionCommand();
                //addActionListener(actionListener);
                buttonGroup.add(this);
            }
        }
        
//        public void paintComponent(Graphics graphics) {
//            super.paintComponents(graphics);
//            
//            // TODO: Don't think this is going to work. Replace with either grid or separate buttons.
//            if (mousePoint != null) {
//                int x = ((int)(mousePoint.x / 36)) * 36;
//                int y = ((int)(mousePoint.y / 36)) * 36;
//                System.out.println("x: " + x + ", y:  " + y);
//                graphics.setColor(Color.GRAY);
//                graphics.drawRect(x, y, 35, 35);
//                graphics.setColor(Color.BLACK);
//            }
//            
//            // Plot the circle shaped brushes first.
//            int penSize = 0;
//            for (int column=0; column < 36; column = column + 18) {
//                for (int row = 0; row < 72; row = row + 18) {
//                    plotBrush(column, row, penSize++, false, airBrush, graphics);
//                }
//            }
//            
//            // Now plot the square shaped brushes.
//            penSize = 0;
//            for (int column=36; column < 72; column = column + 18) {
//                for (int row = 0; row < 72; row = row + 18) {
//                    plotBrush(column, row, penSize++, true, airBrush, graphics);
//                }
//            }
//            
//            // TODO: Show the brush that is currently selected.
//        }
    }
    
    /**
     * Handler that processes the mouse click event on the brush chooser dialog.
     */
    class BrushChooserMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            Point clickPoint = event.getPoint();
            chosenBrush = (((int)(clickPoint.y / 16)) * 4) + (clickPoint.x / 16);
            BrushChooserDialog.this.dispose();
        }
    }
    
    /**
     * Handler that processes mouse movement over the brush chooser dialog.
     */
    class BrushChooserMouseMotionListener extends MouseMotionAdapter {
        public void mouseMoved(MouseEvent event) {
            mousePoint = event.getPoint();
            System.out.println("Storing mouse point: " + mousePoint);
            ((JPanel)event.getSource()).repaint();
            
            // TODO: Add tooltip over the same brush??
        }
    }
}
