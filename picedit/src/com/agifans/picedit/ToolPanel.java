package com.agifans.picedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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

        final JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonContainer.setPreferredSize(new Dimension(64, 192));
        buttonContainer.setMaximumSize(new Dimension(64, 192));
        buttonContainer.add(selectionButton);
        buttonContainer.add(zoomButton);
        buttonContainer.add(lineButton);
        buttonContainer.add(shortLineButton);
        buttonContainer.add(stepLineButton);
        buttonContainer.add(fillButton);
        buttonContainer.add(airbrushButton);
        buttonContainer.add(brushButton);
        buttonContainer.add(rectangleButton);
        buttonContainer.add(ellipseButton);
        buttonContainer.add(eyeDropperButton);
        buttonContainer.add(eraserButton);
        this.add(buttonContainer);
        
        this.addSeparator();
        
        final JPanel colourPanel = new JPanel();
        colourPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        colourPanel.setPreferredSize(new Dimension(64, 64));
        colourPanel.setMaximumSize(new Dimension(64, 64));
        ColourButton visualButton = new ColourButton(ColourType.VISUAL, application);
        colourPanel.add(visualButton);
        ColourButton priorityButton = new ColourButton(ColourType.PRIORITY, application);
        colourPanel.add(priorityButton);
        this.add(colourPanel);
        
        this.addSeparator();
        
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
                            buttonContainer.setPreferredSize(new Dimension(128, 96));
                            buttonContainer.setMaximumSize(new Dimension(128, 96));
                            colourPanel.setPreferredSize(new Dimension(128, 32));
                            colourPanel.setMaximumSize(new Dimension(128, 32));
                            ToolPanel.this.setOrientation(JToolBar.VERTICAL);
                        } else {
                            // Docking.
                            if (ToolPanel.this.getOrientation() == JToolBar.VERTICAL) {
                                buttonContainer.setPreferredSize(new Dimension(64, 192));
                                buttonContainer.setMaximumSize(new Dimension(64, 192));
                                colourPanel.setPreferredSize(new Dimension(64, 64));
                                colourPanel.setMaximumSize(new Dimension(64, 64));
                            } else {
                                buttonContainer.setPreferredSize(new Dimension(384, 32));
                                buttonContainer.setMaximumSize(new Dimension(384, 32));
                                colourPanel.setPreferredSize(new Dimension(128, 32));
                                colourPanel.setMaximumSize(new Dimension(128, 32));
                            }
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Colour button class used for the visual and priority colour changing buttons.
     */
    class ColourButton extends JCheckBox {
        
        private PicEdit application;
        
        private ColourType colourType;
        
        ColourButton(final ColourType colourType, final PicEdit application) {
            super();
            this.colourType = colourType;
            this.application = application;
            setPreferredSize(new Dimension(64, 32));
            setMaximumSize(new Dimension(64, 32));
            setFocusable(false);
            setFocusPainted(false);
            setMargin(new Insets(0, 0, 0, 0));
            this.setBackground(Color.red);
            this.setModel(new DefaultButtonModel() {
                public boolean isSelected() {
                    switch (colourType) {
                        case VISUAL:
                            return application.getEditStatus().isVisualDrawEnabled();
                        case PRIORITY:
                            return application.getEditStatus().isPriorityDrawEnabled();
                        default:
                            return false;
                    }
                }
            });
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    Point mousePoint = event.getPoint();
                    Rectangle colourBox = new Rectangle(30, 5, 30, 23);
                    boolean clickInColourBox = colourBox.contains(mousePoint);
                    switch (colourType) {
                        case VISUAL:
                            if (application.getEditStatus().isVisualDrawEnabled() && !clickInColourBox) {
                                application.getEditStatus().setVisualColour(EditStatus.VISUAL_OFF);
                            } else {
                                // TODO: Pop up colour chooser.
                                application.getEditStatus().setVisualColour(1);
                                ColourChooserDialog dialog = new ColourChooserDialog(ColourButton.this);
                                dialog.setVisible(true);
                            }
                            break;
                        case PRIORITY:
                            if (application.getEditStatus().isPriorityDrawEnabled() && !clickInColourBox) {
                                application.getEditStatus().setPriorityColour(EditStatus.PRIORITY_OFF);
                            } else {
                                // TODO: Pop up colour chooser.
                                application.getEditStatus().setPriorityColour(2);
                            }
                            break;
                    }
                }
            });
        }
        
        public void paintComponent(Graphics graphics) {
            int colourCode = -1;
            switch (colourType) {
                case VISUAL:
                    if (application.getEditStatus().isVisualDrawEnabled()) {
                        colourCode = application.getEditStatus().getVisualColour();
                        graphics.setColor(EgaPalette.COLOR_OBJECTS[colourCode]);
                        graphics.fillRect(30, 5, 30, 23);
                    }
                    break;
                case PRIORITY:
                    if (application.getEditStatus().isPriorityDrawEnabled()) {
                        colourCode = application.getEditStatus().getPriorityColour();
                        graphics.setColor(EgaPalette.COLOR_OBJECTS[colourCode]);
                        graphics.fillRect(30, 5, 30, 23);
                    }
                    break;
            }
            super.paintComponent(graphics);
            graphics.setColor(Color.GRAY);
            graphics.drawRect(2, 2, 60, 28);
            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            if ((colourCode >= 0) && (colourCode <= 8)) {
                graphics.setColor(Color.WHITE);
            } else if ((colourCode >= 9) && (colourCode <= 15)) {
                graphics.setColor(Color.BLACK);
            }
            graphics.drawString("" + colourType.getDisplayName().charAt(0), 38, 23);
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
