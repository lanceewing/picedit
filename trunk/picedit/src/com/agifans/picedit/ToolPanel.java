package com.agifans.picedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import javax.swing.plaf.basic.BasicToolBarUI;

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
        
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setBackground(Color.pink);
        this.setMaximumSize(new Dimension(70, 2000));
        
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

        JPanel row1 = new JPanel();
        FlowLayout layout1 = new FlowLayout(FlowLayout.LEFT, 0, 0);
        row1.setLayout(layout1);
        row1.setBackground(Color.orange);
        row1.setPreferredSize(new Dimension(64, 32));
        row1.setMaximumSize(new Dimension(64, 32));
        row1.add(selectionButton);
        row1.add(zoomButton);
        this.add(row1);
        
        this.addSeparator();
        this.add(lineButton);
        this.add(shortLineButton);
        this.add(stepLineButton);
        this.add(fillButton);
        this.add(airbrushButton);
        this.add(brushButton);
        this.add(rectangleButton);
        this.add(ellipseButton);
        this.addSeparator();
        this.add(eyeDropperButton);
        this.add(eraserButton);
        
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
        
        System.out.println("" + this.getUI().getClass().getName());
        
        //frame.setUndecorated(true); frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME); frame.setSize(50,50);
        
        //((BasicToolBarUI)this.getUI()).setFloatingLocation(500,500);
        //((BasicToolBarUI)this.getUI()).setFloating(true, new Point(500,500));
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
        
        public ColourButton() {
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
