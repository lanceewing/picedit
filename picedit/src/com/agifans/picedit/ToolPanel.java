package com.agifans.picedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolBarUI;

/**
 * The tool panel that is displayed at the bottom of the picture.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class ToolPanel extends JToolBar {

    /**
     * Constructor for ToolPanel.
     * 
     * @param pictureFrame The PictureFrame that this tool panel is a part of.
     * @param application The PicEdit application.
     */
    public ToolPanel(PictureFrame pictureFrame, PicEdit application) {
        super(JToolBar.VERTICAL);
        
        ButtonGroup toolGroup = new ButtonGroup();
        ToolPanelActionListener actionListener = new ToolPanelActionListener(pictureFrame, application);
        ToolButton selectionButton = new ToolButton("selection.png", toolGroup, actionListener, ToolType.SELECTION);
        ToolButton zoomButton = new ToolButton("zoom.png", toolGroup, actionListener, ToolType.ZOOM);
        ToolButton lineButton = new ToolButton("line.png", toolGroup, actionListener, ToolType.LINE);
        ToolButton shortLineButton = new ToolButton("shortline.png", toolGroup, actionListener, ToolType.SHORTLINE);
        ToolButton stepLineButton = new ToolButton("stepline.png", toolGroup, actionListener, ToolType.STEPLINE);
        ToolButton fillButton = new ToolButton("fill.png", toolGroup, actionListener, ToolType.FILL);
        ToolButton airbrushButton = new ToolButton("airbrush.png", toolGroup, actionListener, ToolType.AIRBRUSH);
        ToolButton brushButton = new ToolButton("brush.png", toolGroup, actionListener, ToolType.BRUSH);
        ToolButton rectangleButton = new ToolButton("rectangle.png", toolGroup, actionListener, ToolType.RECTANGLE);
        ToolButton ellipseButton = new ToolButton("ellipse.png", toolGroup, actionListener, ToolType.ELLIPSE);
        ToolButton eyeDropperButton = new ToolButton("eyedropper.png", toolGroup, actionListener, ToolType.EYEDROPPER);
        ToolButton eraserButton = new ToolButton("eraser.png", toolGroup, actionListener, ToolType.ERASER);

        
        
        final ToolButtonRow buttonRow1 = new ToolButtonRow(selectionButton, zoomButton, lineButton, shortLineButton);
        final ToolButtonRow buttonRow2 = new ToolButtonRow(stepLineButton, fillButton, airbrushButton, brushButton);
        final ToolButtonRow buttonRow3 = new ToolButtonRow(rectangleButton, ellipseButton, eyeDropperButton, eraserButton);
        
        this.add(buttonRow1);
        this.add(buttonRow2);
        this.add(buttonRow3);
        
//        this.add(new ToolButtonRow(stepLineButton, fillButton));
//        this.add(new ToolButtonRow(airbrushButton, brushButton));
//        this.add(new ToolButtonRow(rectangleButton, ellipseButton));
//        this.add(new ToolButtonRow(eyeDropperButton, eraserButton));
        
        this.addSeparator();
        
//        ColourButton visualButton = new ColourButton();
//        this.add(visualButton);
//        ColourButton priorityButton = new ColourButton();
//        this.add(priorityButton);
//        ColourButton controlButton = new ColourButton();
//        controlButton.setEnabled(false);
//        this.add(controlButton);
        
        JPanel filler = new JPanel();
        this.add(filler);
        
        this.setUI(new PicEditToolBarUI());
        this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if ("ancestor".equals(evt.getPropertyName())) {
                    if (evt.getNewValue() != null) {
                        Window window = SwingUtilities.windowForComponent((JComponent)evt.getNewValue());
                        if (window instanceof PicEditToolBarUI.PicEditToolBarUIDialog) {
                            // Floating.
                            buttonRow1.setPreferredSize(new Dimension(128, 32));
                            buttonRow1.setMaximumSize(new Dimension(128, 32));
                            buttonRow2.setPreferredSize(new Dimension(128, 32));
                            buttonRow2.setMaximumSize(new Dimension(128, 32));
                            buttonRow3.setPreferredSize(new Dimension(128, 32));
                            buttonRow3.setMaximumSize(new Dimension(128, 32));
                            ToolPanel.this.setOrientation(JToolBar.VERTICAL);
                        } else {
                            // Docking.
                            if (ToolPanel.this.getOrientation() == JToolBar.VERTICAL) {
                                buttonRow1.setPreferredSize(new Dimension(64, 64));
                                buttonRow1.setMaximumSize(new Dimension(64, 64));
                                buttonRow2.setPreferredSize(new Dimension(64, 64));
                                buttonRow2.setMaximumSize(new Dimension(64, 64));
                                buttonRow3.setPreferredSize(new Dimension(64, 64));
                                buttonRow3.setMaximumSize(new Dimension(64, 64));
                            } else {
                                buttonRow1.setPreferredSize(new Dimension(128, 32));
                                buttonRow1.setMaximumSize(new Dimension(128, 32));
                            }
                        }
                    }
                }
            }
        });
    }
    
    public JToggleButton createVisualColourButton() {
        BufferedImage image = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.GRAY);
        graphics.drawRoundRect(4, 4, 40, 40, 5, 5);
        ImageIcon icon = new ImageIcon(image);
        JToggleButton button = new JToggleButton();
        button.setIcon(icon);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(48, 48));
        return button;
    }
    
    class ColourButton extends JToggleButton {
        
        ColourButton() {
            setFocusPainted(false);
            setPreferredSize(new Dimension(48, 48));
            this.setMargin(new Insets(0, 0, 0, 0));
        }
        
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.GRAY);
            graphics.drawRoundRect(4, 4, 39, 38, 5, 5);
        }
    }
    
    class ToolButtonRow extends JPanel {
        ToolButtonRow(JToggleButton button1, JToggleButton button2, JToggleButton button3, JToggleButton button4) {
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
            this.setLayout(layout);
            //this.setPreferredSize(new Dimension(64, 32));
            //this.setMaximumSize(new Dimension(64, 32));
            //this.add(leftButton);
            //this.add(rightButton);
            this.setPreferredSize(new Dimension(64, 64));
            this.setMaximumSize(new Dimension(64, 64));
            this.add(button1);
            this.add(button2);
            this.add(button3);
            this.add(button4);
        }
    }
    
    /**
     * Tool button class used for all buttons on the internal frames tool bar panel.
     */
    class ToolButton extends JToggleButton {
        
        /**
         * Constructor for PictureTool.
         * 
         * @param iconImageName The name of the image file for the button icon.
         * @param buttonGroup The button group that the button is a part of.
         * @param actionListener The action listener that processes actions on this button.
         * @param tool The tool that this ToolButton is associated with.
         */
        ToolButton(String iconImageName, ButtonGroup buttonGroup, ActionListener actionListener, ToolType tool) {
            super();
            Image iconImage = null;
            Image pressedImage = null;
            Image hoveredImage = null;
            try {
                iconImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/" + iconImageName));
                pressedImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/pressed.png"));
                hoveredImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/hovered.png"));
            } catch (IOException e) {
            }
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
            setToolTipText(tool.toString());
            setActionCommand(tool.name());
            addActionListener(actionListener);
            buttonGroup.add(this);
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
    
    class PicEditToolBarUI extends MetalToolBarUI {
        protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
            return new PicEditToolBarUIDialog();
        }
        
        class PicEditToolBarUIDialog extends JDialog {
            PicEditToolBarUIDialog() {
                this.getRootPane().putClientProperty("Window.style", "small");
                this.setResizable(false);
                this.setAlwaysOnTop(true);
                WindowListener windowListener = createFrameListener();
                this.addWindowListener(windowListener);
            }
        }
    }
    
    /**
     * The listener that processes tool bar button actions.
     */
    class ToolPanelActionListener extends CommonHandler implements ActionListener {

        /**
         * Constructor for ToolPanelActionListener.
         * 
         * @param pictureFrame The PictureFrame that this tool panel is a part of.
         * @param application The PicEdit application.
         */
        public ToolPanelActionListener(PictureFrame pictureFrame, PicEdit application) {
            super(pictureFrame.getEditStatus(), pictureFrame.getPicGraphics(), pictureFrame.getPicture(), application);
        }

        /**
         * Processes a tool panel button press.
         * 
         * @param event The ActionEvent triggered when the tool bar button was pressed.
         */
        public void actionPerformed(ActionEvent event) {
            processToolSelect(ToolType.valueOf(event.getActionCommand()));
        }
    }
}
