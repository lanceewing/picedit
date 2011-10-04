package com.agifans.picedit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
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
    private BrushType chosenBrush;
    
    /**
     * Constructor for BrushChooserDialog.
     * 
     * @param button The button component under which the dialog will be drawn.
     * @param airBrush true if this is the air brush variant of the brush; otherwise false.
     */
    public BrushChooserDialog(Component button, final boolean airBrush) {
        this.setModal(true);
        this.setUndecorated(true);
        this.setSize(new Dimension(132, 132));
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
        int bitPos = Picture.splatterStart[10];

        for (int y1 = (y + 8) - penSize; y1 <= (y + 8) + penSize; y1++) {
            for (int x1 = (x + 8) - penSize; x1 <= (x + 8) + penSize; x1+=2) {
                if (isSquare) {
                    if (isAirBrush) {
                        if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                            graphics.fillRect((x1<<1) - 2, (y1<<1) - 1, 4, 2);
                        }
                        bitPos++;
                        if (bitPos == 0xff) {
                            bitPos = 0;
                        }
                    } else {
                        // Not an airbrush implies a solid brush.
                        graphics.fillRect((x1<<1) - 2, (y1<<1) - 1, 4, 2);
                    }
                } else { 
                    // Not a square implies circle.
                    if (((Picture.circles[penSize][circlePos >> 3] >> (7 - (circlePos & 7))) & 1) > 0) {
                        if (isAirBrush) {
                            if (((Picture.splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                                graphics.fillRect((x1<<1) - 2, (y1<<1) - 1, 4, 2);
                            }
                            bitPos++;
                            if (bitPos == 0xff) {
                                bitPos = 0;
                            }
                        } else {
                            // Not an airbrush implies a solid brush.
                            graphics.fillRect((x1<<1) - 2, (y1<<1) - 1, 4, 2);
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
    public BrushType getChosenBrush() {
        return chosenBrush;
    }
    
    /**
     * Panel for holding the brush buttons.
     */
    class BrushChooserButtonPanel extends JPanel {

        /**
         * Constructor for BrushChooserButtonPanel.
         */
        BrushChooserButtonPanel(boolean airBrush) {
          this.setSize(new Dimension(128, 128));
          this.setLocation(2, 2);
          this.setBackground(Color.LIGHT_GRAY);
          
          this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
          
          ButtonGroup brushButtonGroup = new ButtonGroup();
          if (airBrush) {
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_0));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_4));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_0));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_4));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_1));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_5));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_1));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_5));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_2));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_6));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_2));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_6));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_3));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SPRAY_7));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_3));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SPRAY_7));
          } else {
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_0));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_4));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_0));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_4));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_1));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_5));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_1));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_5));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_2));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_6));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_2));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_6));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_3));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.CIRCLE_SOLID_7));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_3));
              this.add(new BrushChooserButton(brushButtonGroup, BrushType.SQUARE_SOLID_7));
          }
        }
        
        /**
         * Toggle button used for showing the various brush types for selection.
         */
        class BrushChooserButton extends JToggleButton {
            private BrushType brushType;
          
            BrushChooserButton(ButtonGroup buttonGroup, BrushType brushType) {
                super();
                this.brushType = brushType;
                Image pressedImage = null;
                Image hoveredImage = null;
                try {
                    pressedImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/pressed.png"));
                    hoveredImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/hovered.png"));
                } catch (IOException e) {
                }
                BufferedImage iconImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	            Graphics graphics = iconImage.getGraphics();
	            graphics.setColor(Color.BLACK);
	            plotBrush(0, 0, brushType.getSize(), brushType.getShape().equals(BrushShape.SQUARE), brushType.getTexture().equals(BrushTexture.SPRAY), graphics);
                setIcon(new ImageIcon(iconImage));
                setSelectedIcon(new ImageIcon(mergeImages(iconImage, pressedImage)));
                setRolloverIcon(new ImageIcon(mergeImages(iconImage, hoveredImage)));
                setRolloverSelectedIcon(getSelectedIcon());
                setPressedIcon(getSelectedIcon());
                setPreferredSize(new Dimension(32, 32));
                setMaximumSize(new Dimension(32, 32));
                setFocusable(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
                setToolTipText(brushType.getDisplayName());
                setActionCommand(brushType.name());
                addActionListener(new BrushChooserButtonActionListener());
                buttonGroup.add(this);
            }
	        
            /**
             * ActionListener for the BrushChooserButtons. Stores the selected brush.
             */
            class BrushChooserButtonActionListener implements ActionListener {
                public void actionPerformed(ActionEvent event) {
                    chosenBrush = ((BrushChooserButton)event.getSource()).brushType;
                    BrushChooserDialog.this.dispose();
                }
            }
            
	        /**
	         * Merges the two images together by firstly drawing the backgroundImage
	         * and then the foregroundImage on top of it.
	         * 
	         * @param foregroundImage The image to draw on top of the background image.
	         * @param backgroundImage The image to draw behind the foreground image.
	         * 
	         * @return the merged Image.
	         */
	        Image mergeImages(Image foregroundImage, Image backgroundImage) {
	            BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	            Graphics graphics = image.getGraphics();
	            graphics.drawImage(backgroundImage, 0, 0, 32, 32, this);
	            graphics.drawImage(foregroundImage, 0, 0, 32, 32, this);
	            return image;
	        }
        }
    }
}