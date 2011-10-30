package com.agifans.picedit.gui.toolbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.types.ColourType;
import com.agifans.picedit.types.ToolType;
import com.agifans.picedit.utils.EgaPalette;
import com.agifans.picedit.utils.OSChecker;

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
     * @param application The PicEdit application.
     */
    public ToolPanel(final PicEdit application) {
        super(JToolBar.VERTICAL);
        
        ToolPanelActionListener actionListener = new ToolPanelActionListener(application);
        ToolButton selectionButton = new ToolButton("selection.png", application, actionListener, ToolType.SELECTION);
        selectionButton.setEnabled(false);
        ToolButton zoomButton = new ToolButton("zoom.png", application, actionListener, ToolType.ZOOM);
        zoomButton.setEnabled(false);
        ToolButton lineButton = new ToolButton("line.png", application, actionListener, ToolType.LINE);
        ToolButton shortLineButton = new ToolButton("shortline.png", application, actionListener, ToolType.SHORTLINE);
        ToolButton stepLineButton = new ToolButton("stepline.png", application, actionListener, ToolType.STEPLINE);
        ToolButton fillButton = new ToolButton("fill.png", application, actionListener, ToolType.FILL);
        ToolButton airbrushButton = new ToolButton("airbrush.png", application, actionListener, ToolType.AIRBRUSH);
        ToolButton brushButton = new ToolButton("brush.png", application, actionListener, ToolType.BRUSH);
        ToolButton rectangleButton = new ToolButton("rectangle.png", application, actionListener, ToolType.RECTANGLE);
        rectangleButton.setEnabled(false);
        ToolButton ellipseButton = new ToolButton("ellipse.png", application, actionListener, ToolType.ELLIPSE);
        ellipseButton.setEnabled(false);
        ToolButton eyeDropperButton = new ToolButton("eyedropper.png", application, actionListener, ToolType.EYEDROPPER);
        eyeDropperButton.setEnabled(false);
        ToolButton eraserButton = new ToolButton("eraser.png", application, actionListener, ToolType.ERASER);
        eraserButton.setEnabled(false);

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
        ColourButtonPanel visualButton = new ColourButtonPanel(ColourType.VISUAL, application);
        colourPanel.add(visualButton);
        ColourButtonPanel priorityButton = new ColourButtonPanel(ColourType.PRIORITY, application);
        colourPanel.add(priorityButton);
        this.add(colourPanel);
        
        this.addSeparator();
        
        JPanel filler = new JPanel();
        this.add(filler);
        
        this.setUI(new PicEditToolBarUI());
        this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(final java.beans.PropertyChangeEvent evt) {
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
                            application.setToolPanelLocation(ToolPanelLocation.FLOATING);
                        } else {
                            // Docking.
                            if (ToolPanel.this.getOrientation() == JToolBar.VERTICAL) {
                                buttonContainer.setPreferredSize(new Dimension(64, 192));
                                buttonContainer.setMaximumSize(new Dimension(64, 192));
                                colourPanel.setPreferredSize(new Dimension(64, 64));
                                colourPanel.setMaximumSize(new Dimension(64, 64));
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        String borderConstraints = (String)((BorderLayout)((JComponent)evt.getNewValue()).getLayout()).getConstraints(ToolPanel.this);
                                            if (BorderLayout.EAST.equals(borderConstraints)) {
                                                application.setToolPanelLocation(ToolPanelLocation.DOCKED_RIGHT);
                                            } else {
                                                application.setToolPanelLocation(ToolPanelLocation.DOCKED_LEFT);
                                            }
                                        }
                                    }
                                );
                            } else {
                                buttonContainer.setPreferredSize(new Dimension(384, 32));
                                buttonContainer.setMaximumSize(new Dimension(384, 32));
                                colourPanel.setPreferredSize(new Dimension(128, 32));
                                colourPanel.setMaximumSize(new Dimension(128, 32));
                                application.setToolPanelLocation(ToolPanelLocation.DOCKED_TOP);
                            }
                        }
                    }
                }
            }
        });
    }
    
    /**
     * A panel to hold the ColourButton. This panel will make sure there is a visually
     * sufficient gap between the left side and the checkbox.
     */
    class ColourButtonPanel extends JPanel {
        
        /**
         * Constructor for ColourButtonPanel.
         * 
         * @param colourType The type of colour button.
         * @param application The PicEdit application.
         */
        ColourButtonPanel(ColourType colourType, PicEdit application) {
            this.setPreferredSize(new Dimension(64, 32));
            this.setMaximumSize(new Dimension(64, 32));
            this.setLayout(null);
            ColourButton colourButton = new ColourButton(colourType, application);
            if (OSChecker.isMac()) {
                colourButton.setBounds(3, 3, 58, 26);
            } else {
                colourButton.setBounds(7, 3, 54, 26);
            }
            this.add(colourButton);
        }
        
        /**
         * Paints the ColourButtonPanel component.
         * 
         * @param graphics The Graphics to use to drawn the ColourButtonPanel component.
         */
        public void paintComponent(Graphics graphics) {
          super.paintComponent(graphics);
          graphics.setColor(Color.GRAY);
          graphics.drawRect(2, 2, 60, 27);
        }
    }
    
    /**
     * Colour button class used for the visual and priority colour changing buttons.
     */
    class ColourButton extends JCheckBox {
        
        /**
         * The PicEdit application.
         */
        private PicEdit application;
        
        /**
         * The type of colour button.
         */
        private ColourType colourType;
        
        /**
         * Constructor for ColourButton.
         * 
         * @param colourType The type of colour button.
         * @param application The PicEdit application.
         */
        ColourButton(final ColourType colourType, final PicEdit application) {
            super();
            
            this.colourType = colourType;
            this.application = application;
            
            if (OSChecker.isMac()) {
                this.setPreferredSize(new Dimension(58, 26));
                this.setMaximumSize(new Dimension(58, 26));
            } else {
                this.setPreferredSize(new Dimension(54, 26));
                this.setMaximumSize(new Dimension(54, 26));
            }
            this.setFocusable(false);
            this.setFocusPainted(false);
            this.setMargin(new Insets(0, 0, 0, 0));
            this.setToolTipText(colourType.getDisplayName());
            this.setModel(new ColourButtonModel());
            this.addMouseListener(new ColourButtonMouseListener());
        }
        
        /**
         * Paints the ColourButton component.
         * 
         * @param graphics The Graphics to use to drawn the ColourButton component.
         */
        public void paintComponent(Graphics graphics) {
        	  super.paintComponent(graphics);
            int colourCode = -1;
            switch (colourType) {
                case VISUAL:
                    if (application.getEditStatus().isVisualDrawEnabled()) {
                        colourCode = application.getEditStatus().getVisualColour();
                        colourCode = (colourCode == EditStatus.TRANSPARENT? 15 : colourCode);
                        graphics.setColor(EgaPalette.COLOR_OBJECTS[colourCode]);
                        if (OSChecker.isMac()) {
                            graphics.fillRect(26, 2, 31, 22);
                        } else {
                            graphics.fillRect(22, 2, 31, 22);
                        }
                    }
                    break;
                case PRIORITY:
                    if (application.getEditStatus().isPriorityDrawEnabled()) {
                        colourCode = application.getEditStatus().getPriorityColour();
                        colourCode = (colourCode == EditStatus.TRANSPARENT? 4 : colourCode);
                        graphics.setColor(EgaPalette.COLOR_OBJECTS[colourCode]);
                        if (OSChecker.isMac()) {
                            graphics.fillRect(26, 2, 31, 22);
                        } else {
                            graphics.fillRect(22, 2, 31, 22);
                        }
                    }
                    break;
            }
            graphics.setColor(Color.GRAY);
            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            if ((colourCode >= 0) && (colourCode <= 8)) {
                graphics.setColor(Color.WHITE);
            } else if ((colourCode >= 9) && (colourCode <= 15)) {
                graphics.setColor(Color.BLACK);
            }
            if (OSChecker.isMac()) {
            	graphics.drawString("" + colourType.getDisplayName().charAt(0), 36, 20);
            } else {
            	graphics.drawString("" + colourType.getDisplayName().charAt(0), 32, 20);
            }
        }
        
        /**
         * ButtonModel used to determine the ColourButton state.
         */
        class ColourButtonModel extends DefaultButtonModel {
            
            /**
             * Determines whether the ColourButton checkbox is checked on not based on the
             * visual or priority enabled flag in the EditStatus.
             * 
             * @return true if the ColourButton is selected; otherwise false.
             */
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
        }
        
        /**
         * MouseListener that processes clicks on a ColourButton.
         */
        class ColourButtonMouseListener extends MouseAdapter {
            
            /**
             * Processes mouse pressed events on the ColourButton.
             * 
             * @param event The mouse pressed event to process.
             */
            public void mousePressed(MouseEvent event) {
                Point mousePoint = event.getPoint();
                Rectangle colourBox = new Rectangle(30, 5, 30, 23);
                boolean clickInColourBox = colourBox.contains(mousePoint);
                EditStatus editStatus = application.getEditStatus();
                Picture picture = application.getPicture();
                
                switch (colourType) {
                    case VISUAL:
                        if (editStatus.isVisualDrawEnabled() && !clickInColourBox) {
                            // If click is on the visual button but not in the colour box and visual
                            // drawing is currently on then turn off visual drawing.
                            picture.processVisualColourOff();
                        } else {
                            // Pop up colour chooser.
                            ColourChooserDialog dialog = new ColourChooserDialog(ColourButton.this);
                            dialog.setVisible(true);
                            
                            // Process the chosen visual colour.
                            if (dialog.getChosenColour() != -1) {
                                picture.processVisualColourChange(dialog.getChosenColour());
                            }
                        }
                        break;
                    case PRIORITY:
                        if (editStatus.isPriorityDrawEnabled() && !clickInColourBox) {
                            // If click is on the priority button but not in the colour box and priority
                            // drawing is currently on then turn off priority drawing.
                            picture.processPriorityColourOff();
                        } else {
                            // Pop up colour chooser.
                            ColourChooserDialog dialog = new ColourChooserDialog(ColourButton.this);
                            dialog.setVisible(true);
                            
                            // Process the chosen priority colour.
                            if (dialog.getChosenColour() != -1) {
                                picture.processPriorityColourChange(dialog.getChosenColour());
                            }
                        }
                        break;
                }
            }
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
         * @param application The PicEdit application.
         * @param actionListener The action listener that processes actions on this button.
         * @param tool The tool that this ToolButton is associated with.
         */
        ToolButton(String iconImageName, PicEdit application, ActionListener actionListener, ToolType tool) {
            super();
            setModel(new ToolButtonModel(tool, application));
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
     * Model for the ToolButton class. Uses the EditStatus's currently selected tool to determine
     * whether this toggle button is selected or not.
     */
    class ToolButtonModel extends JToggleButton.ToggleButtonModel {
        
        /**
         * The type of tool that the button selects.
         */
        private ToolType tool;
        
        /**
         * The PicEdit application.
         */
        private PicEdit application;
        
        /**
         * Constructor for ToolButtonModel.
         * 
         * @param tool The type of tool that the button selects.
         * @param application The PicEdit application.
         */
        ToolButtonModel(ToolType tool, PicEdit application) {
            this.tool = tool;
            this.application = application;
        }
        
        /**
         * Invoked when one of the tool panel buttons is pressed or depressed.
         * 
         * @param pressed true if the button was pressed; false if it was depressed.
         */
        public void setPressed(boolean pressed) {
        	super.setPressed(pressed);
        	if (pressed) {
        		// Clear the previous tool as soon as a new one is pressed down.
        		application.getEditStatus().setTool(ToolType.NONE);
        		ToolPanel.this.repaint();
        	}
        }
        
        /**
         * Returns true if the currently selected tool is this tool; otherwise false.
         * 
         * @return true if the currently selected tool is this tool; otherwise false.
         */
        public boolean isSelected() {
        	return application.getEditStatus().getTool().equals(tool);
        }
    }
    
    /**
     * ToolBar UI that customises the behaviour of the floating toolbar dialog.
     */
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
    class ToolPanelActionListener implements ActionListener {

        /**
         * The PICEDIT application component.
         */
        protected PicEdit application;
        
        /**
         * Constructor for ToolPanelActionListener.
         * 
         * @param application The PicEdit application.
         */
        public ToolPanelActionListener(PicEdit application) {
            this.application = application;
        }

        /**
         * Processes a tool panel button press.
         * 
         * @param event The ActionEvent triggered when the tool bar button was pressed.
         */
        public void actionPerformed(ActionEvent event) {
            ToolPanelLocation toolPanelLocation = application.getToolPanelLocation();
            ToolType tool = ToolType.valueOf(event.getActionCommand());
            switch (tool) {
                case BRUSH:
                    // Pop up brush chooser.
                    BrushChooserDialog brushDialog = new BrushChooserDialog((Component)event.getSource(), false, toolPanelLocation);
                    brushDialog.setVisible(true);
                    if (brushDialog.getChosenBrush() != null) {
                        application.getEditStatus().setBrushCode(brushDialog.getChosenBrush().getBrushCode());
                    }
                    break;
                case AIRBRUSH:
                    // Pop up brush chooser.
                    BrushChooserDialog airBrushDialog = new BrushChooserDialog((Component)event.getSource(), true, toolPanelLocation);
                    airBrushDialog.setVisible(true);
                    if (airBrushDialog.getChosenBrush() != null) {
                        application.getEditStatus().setBrushCode(airBrushDialog.getChosenBrush().getBrushCode());
                    }
                    break;
            }
            
            // Make sure that temporary line is cleared and slider reenabled.
            application.getPicturePanel().clearTemporaryLine();
            application.getPictureFrame().getPositionSlider().setEnabled(true);
            
            // Process the selected tool.
            application.getEditStatus().setTool(tool);
        }
    }
}
