package com.agifans.picedit.gui.toolbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.agifans.picedit.utils.EgaPalette;

/**
 * Colour chooser dialog used when selecting visual, priority and control colours.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class ColourChooserDialog extends JDialog {

    /**
     * Holds the colour that the user clicked on.
     */
    private int chosenColour = -1;
    
    /**
     * Constructor for ColourChooserDialog.
     * 
     * @param button The button component under which the palette will be drawn.
     */
    public ColourChooserDialog(Component button) {
        this(new Point(button.getLocationOnScreen().x, button.getLocationOnScreen().y + button.getSize().height));
    }
    
    /**
     * Constructor for ColourChooserDialog.
     * 
     * @param point The point at which the palette will be drawn.
     */
    public ColourChooserDialog(Point point) {
        this.setModal(true);
        this.setUndecorated(true);
        this.setSize(new Dimension(68, 68));
        this.setResizable(false);
        this.setLocation(point);
        this.setAlwaysOnTop(true);
        this.setLayout(null);
        
        ColourChooserPanel palettePanel = new ColourChooserPanel();
        this.add(palettePanel);
        
        palettePanel.addMouseListener(new ColourChooserMouseHandler());
        
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    ColourChooserDialog.this.dispose();
                }
            }
        });
    }
    
    /**
     * Returns the colour that the user clicked on.
     * 
     * @return The colour that the user clicked on.
     */
    public int getChosenColour() {
        return chosenColour;
    }
    
    /**
     * The panel that holds the colour palette.
     */
    class ColourChooserPanel extends JPanel {
      
        ColourChooserPanel() {
            this.setSize(new Dimension(64, 64));
            this.setLocation(2, 2);
            this.setBorder(new BevelBorder(BevelBorder.RAISED));
        }
      
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
    }
    
    /**
     * Handler that processes the mouse click event on the colour chooser palette.
     */
    class ColourChooserMouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                Point clickPoint = event.getPoint();
                chosenColour = (((int)(clickPoint.y / 16)) * 4) + (clickPoint.x / 16);
            }
            ColourChooserDialog.this.dispose();
        }
    }
}
