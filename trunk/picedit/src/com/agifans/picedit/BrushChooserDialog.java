package com.agifans.picedit;

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
                
                // Pick a random pattern number.
                int bitPos = Picture.splatterStart[10];
                
                for (int row = 0; row < 64; row = row + 16) {
                    for (int column=0; column < 64; column = column + 16) {
                        
                        
                        
                    }
                }
                
                int colourCode = 0;
                for (int row = 0; row < 64; row = row + 16) {
                    for (int column=0; column < 64; column = column + 16) {
                        graphics.setColor(EgaPalette.COLOR_OBJECTS[colourCode++]);
                        graphics.fillRect(column, row, 16, 16);
                    }
                }
            }
        };
        palettePanel.setSize(new Dimension(64, 64));
        palettePanel.setLocation(2, 2);
        this.add(palettePanel);
        
        palettePanel.addMouseListener(new BrushChooserMouseHandler());
    }
    
    public void plotPattern(int patNum, int x, int y) {
        int circlePos = 0;
        int x1, y1, penSize, bitPos = Picture.splatterStart[10];
        int patCode = 0;

        penSize = (patCode & 7);

        if (x < ((penSize / 2) + 1)) {
            x = ((penSize / 2) + 1);

        } else if (x > 160 - ((penSize / 2) + 1)) {
            x = 160 - ((penSize / 2) + 1);
        }

        if (y < penSize) {
            y = penSize;

        } else if (y >= 168 - penSize) {
            y = 167 - penSize;
        }

        for (y1 = y - penSize; y1 <= y + penSize; y1++) {
            for (x1 = x - ((int) Math.ceil((float) penSize / 2)); x1 <= x + ((int) Math.floor((float) penSize / 2)); x1++) {
                if ((patCode & 0x10) > 0) { /* Square */
                    if ((patCode & 0x20) > 0) {
                        if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                            if (editStatus.isVisualDrawEnabled()) {
                                visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                            }
                            if (editStatus.isPriorityDrawEnabled()) {
                                priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                            }
                        }
                        bitPos++;
                        if (bitPos == 0xff) {
                            bitPos = 0;
                        }
                    } else {
                        if (editStatus.isVisualDrawEnabled()) {
                            visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                        }
                        if (editStatus.isPriorityDrawEnabled()) {
                            priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                        }
                    }
                } else { /* Circle */
                    if (((circles[patCode & 7][circlePos >> 3] >> (7 - (circlePos & 7))) & 1) > 0) {
                        if ((patCode & 0x20) > 0) {
                            if (((splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                                if (editStatus.isVisualDrawEnabled()) {
                                    visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                                }
                                if (editStatus.isPriorityDrawEnabled()) {
                                    priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                                }
                            }
                            bitPos++;
                            if (bitPos == 0xff) {
                                bitPos = 0;
                            }
                        } else {
                            if (editStatus.isVisualDrawEnabled()) {
                                visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                            }
                            if (editStatus.isPriorityDrawEnabled()) {
                                priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                            }
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
