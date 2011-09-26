package com.agifans.picedit;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.agifans.picedit.ColourChooserDialog.ColourChooserMouseHandler;

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
     */
    public BrushChooserDialog(Component button) {
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
