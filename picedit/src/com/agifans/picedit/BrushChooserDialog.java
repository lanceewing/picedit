package com.agifans.picedit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

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
     * Constructor for BrushChooserDialog.
     * 
     * @param button The button component under which the dialog will be drawn.
     * @param airBrush true if this is the air brush variant of the brush; otherwise false.
     */
    public BrushChooserDialog(Component button, final boolean airBrush) {
        this.setModal(true);
        this.setUndecorated(true);
        this.setSize(new Dimension(68, 68));
        this.setResizable(false);
        Point buttonLocation = button.getLocationOnScreen();
        this.setLocation(buttonLocation.x, buttonLocation.y + button.getSize().height);
        this.setAlwaysOnTop(true);
        this.setLayout(null);
        
        JPanel palettePanel = new JPanel() {
            public void paintComponent(Graphics graphics) {
                super.paintComponents(graphics);
                
                graphics.setColor(Color.BLACK);
                
                // Plot the circle shaped brushes first.
                int penSize = 0;
                for (int row = 0; row < 64; row = row + 16) {
                    for (int column=0; column < 32; column = column + 16) {
                        plotBrush(column, row, penSize++, false, airBrush, graphics);
                    }
                }
                
                // Now plot the square shaped brushes.
                penSize = 0;
                for (int row = 0; row < 64; row = row + 16) {
                  for (int column=32; column < 64; column = column + 16) {
                      plotBrush(column, row, penSize++, true, airBrush, graphics);
                  }
                }
            }
        };
        palettePanel.setSize(new Dimension(64, 64));
        palettePanel.setLocation(2, 2);
        this.add(palettePanel);
        
        palettePanel.addMouseListener(new BrushChooserMouseHandler());
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
        int bitPos = Picture.splatterStart[10];

        for (int y1 = y - penSize; y1 <= y + penSize; y1++) {
            for (int x1 = x - ((int) Math.ceil((float) penSize / 2)); x1 <= x + ((int) Math.floor((float) penSize / 2)); x1++) {
                if (isSquare) {
                    if (isAirBrush) {
                        if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                            graphics.fillRect(x1, y1, 1, 1);
                        }
                        bitPos++;
                        if (bitPos == 0xff) {
                            bitPos = 0;
                        }
                    } else {
                        // Not an airbrush implies a solid brush.
                        graphics.fillRect(x1, y1, 1, 1);
                    }
                } else { 
                    // Not a square implies circle.
                    if (((Picture.circles[penSize][circlePos >> 3] >> (7 - (circlePos & 7))) & 1) > 0) {
                        if (isAirBrush) {
                            if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                                graphics.fillRect(x1, y1, 1, 1);
                            }
                            bitPos++;
                            if (bitPos == 0xff) {
                                bitPos = 0;
                            }
                        } else {
                            // Not an airbrush implies a solid brush.
                            graphics.fillRect(x1, y1, 1, 1);
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
     * Handler that processes the mouse click event on the brush chooser dialog.
     */
    class BrushChooserMouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            Point clickPoint = event.getPoint();
            chosenBrush = (((int)(clickPoint.y / 16)) * 4) + (clickPoint.x / 16);
            BrushChooserDialog.this.dispose();
        }
    }
}
