package com.agifans.picedit;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ColourChooserDialog extends JDialog {

    public ColourChooserDialog(Component button) {
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
        
        this.addMouseListener(new ColourChooserMouseHandler());
    }
    
    class ColourChooserMouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            ColourChooserDialog.this.dispose();
        }
    }
}
