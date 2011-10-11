package com.agifans.picedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * An internal picture frame to display in the desktop pane. There is one 
 * such frame for each picture that is loaded. It is in these frames that
 * the pictures are displayed.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class PictureFrame extends JInternalFrame implements InternalFrameListener {

    /**
     * The PicEdit application.
     */
    private PicEdit application;
    
    /**
     * The scroll pane that holds the picture panel.
     */
    private JScrollPane pictureScrollPane;
    
    /**
     * The panel containing the picture being edited.
     */
    private PicturePanel picturePanel;
    
    /**
     * Holds the current edit status of the picture in this frame.
     */
    private EditStatus editStatus;
    
    /**
     * The picture that is displayed in this frame.
     */
    private Picture picture;
    
    /**
     * The graphics routines with which the application draws the screen.
     */
    private PicGraphics picGraphics;
    
    /**
     * Holds the maximum frame size for each zoom factor.
     */
    private Map<Integer, Dimension> maximumSizeMap;
    
    /**
     * The slider that sets the picture position.
     */
    private JSlider positionSlider;
    
    /**
     * The mouse handler for this picture frame.
     */
    private MouseHandler mouseHandler;
    
    /**
     * Constructor for PictureFrame.
     * 
     * @param application The PicEdit application.
     */
    public PictureFrame(final PicEdit application) {
        this.application = application;
        this.editStatus = new EditStatus();
        this.picGraphics = new PicGraphics(320, editStatus.getPictureType().getHeight(), application, 25);
        this.picture = new Picture(editStatus, picGraphics);
        this.picturePanel = new PicturePanel(editStatus, picGraphics, picture);
        
        mouseHandler = new MouseHandler(this, application);
        picturePanel.addMouseListener(mouseHandler);
        picturePanel.addMouseMotionListener(mouseHandler);
        picturePanel.addMouseWheelListener(mouseHandler);
        
        this.calculateResizeDimensions();
        this.setLayout(new BorderLayout());
        
        KeyboardHandler keyboardHandler = new KeyboardHandler(editStatus, picGraphics, picture, application);
        this.getContentPane().addKeyListener(keyboardHandler);
        
        // Add the panel that holds the picture that is being edited.
        pictureScrollPane = new JScrollPane(picturePanel);
        pictureScrollPane.setMinimumSize(new Dimension(10, 10));
        pictureScrollPane.setOpaque(true);
        pictureScrollPane.setBackground(Color.lightGray);
        this.add(pictureScrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(new NavigationButton("Back24.gif", NavigationButtonType.BACK), BorderLayout.WEST);
        bottomPanel.add(new NavigationButton("Forward24.gif", NavigationButtonType.FORWARD), BorderLayout.EAST);
        
        positionSlider = new JSlider();
        positionSlider.setModel(new PositionSliderModel(picture));
        positionSlider.setFocusable(false);
        bottomPanel.add(positionSlider, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        
        this.setIconifiable(true);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(this.maximumSizeMap.get(editStatus.getZoomFactor()));
        this.setMaximumSize(this.maximumSizeMap.get(editStatus.getZoomFactor()));
        this.addInternalFrameListener(this);
        this.setVisible(true);
    }
    
    /**
     * Buttons used for picture navigation.
     */
    class NavigationButton extends JButton implements ActionListener {
        NavigationButton(String iconImageName, NavigationButtonType type) {
            Image iconImage = null;
            try {
                iconImage = ImageIO.read(ClassLoader.getSystemResource("com/agifans/picedit/images/" + iconImageName));
            } catch (IOException e) {
            }
            setIcon(new ImageIcon(iconImage));
            setPreferredSize(new Dimension(24, 24));
            setMaximumSize(new Dimension(24, 24));
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setMargin(new Insets(0, 0, 0, 0));
            setActionCommand(type.name());
            addActionListener(this);
        }

        /**
         * Processes the navigation button clicks.
         * 
         * @param event The ActionEvent for the button click.
         */
        public void actionPerformed(ActionEvent event) {
            NavigationButtonType type = NavigationButtonType.valueOf(event.getActionCommand());
            switch (type) {
                case FORWARD:
                    application.getPicture().moveForwardOnePictureAction();
                    break;
                case BACK:
                    application.getPicture().moveBackOnePictureAction();
                    break;
            }
        }
    }
    
    public MouseHandler getMouseHandler() {
    	return mouseHandler;
    }
    
    public EditStatus getEditStatus() {
        return editStatus;
    }
    
    public Picture getPicture() {
        return picture;
    }
    
    public PicturePanel getPicturePanel() {
        return picturePanel;
    }
    
    public PicGraphics getPicGraphics() {
        return picGraphics;
    }
    
    public JSlider getPositionSlider() {
        return positionSlider;
    }
    
    public void calculateResizeDimensions() {
        this.maximumSizeMap = new HashMap<Integer, Dimension>();
        for (int i=1; i<=5; i++) {
            JInternalFrame frame = new JInternalFrame();
            JPanel panel = new JPanel();
            Dimension appDimension = new Dimension(320 * i, editStatus.getPictureType().getHeight() * i);
            panel.setPreferredSize(appDimension);
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setMinimumSize(new Dimension(10, 10));
            frame.setLayout(new BorderLayout());
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            JSlider slider = new JSlider();
            JButton backButton = new JButton();
            backButton.setPreferredSize(new Dimension(24, 24));
            JButton forwardButton = new JButton();
            forwardButton.setPreferredSize(new Dimension(24, 24));
            bottomPanel.add(backButton, BorderLayout.WEST);
            bottomPanel.add(slider, BorderLayout.CENTER);
            bottomPanel.add(forwardButton, BorderLayout.EAST);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.add(bottomPanel, BorderLayout.SOUTH);
            frame.pack();
            frame.invalidate();
            this.maximumSizeMap.put(i, frame.getSize());
        }
    }
    
    public void resizeForZoomFactor(int zoomFactor) {
        this.editStatus.setZoomFactor(zoomFactor);
        
        // Update the size of the picture according to new zoom factor.
        picturePanel.setPreferredSize(new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor()));
        picturePanel.resizeOffscreenImage();
        
        // Apply the new maximum size to the frame.
        Dimension maximumSize = maximumSizeMap.get(editStatus.getZoomFactor());
        this.setMaximumSize(maximumSize);
        
        // Use max size for the new size of the frame. Works best this way when zooming out and in.
        Dimension desktopSize = this.application.getDesktopPane().getSize();
        Point frameLocation = this.getLocation();
        this.setSize(new Dimension(
                Math.min(maximumSize.width, desktopSize.width - frameLocation.x),
                Math.min(maximumSize.height, desktopSize.height - frameLocation.y)));
        
        // This will tell the scroll pane to adjust itself.
        picturePanel.revalidate();
    }
    
    /**
     * Paints the PictureFrame.
     */
    public void paint(Graphics g) {
        super.paint(g);
        
        // Make sure the slider is up to date with the picture position.
        positionSlider.getModel().setValue(picture.getPicturePosition());
        
        // If we are in a window then update the title to show the current picture name.
        if (editStatus.getPictureFile() == null) {
            this.setTitle("Untitled");
        } else {
            this.setTitle(editStatus.getPictureFile().getName());
        }
    }

    public void internalFrameActivated(InternalFrameEvent event) {
        Dimension desktopSize = application.getDesktopPane().getSize();
        int newWidth = Math.min(desktopSize.width, this.getWidth());
        int newHeight = Math.min(desktopSize.height, this.getHeight());
        this.setSize(new Dimension(newWidth, newHeight));
    }

    public void internalFrameClosed(InternalFrameEvent event) {
    }

    public void internalFrameClosing(InternalFrameEvent event) {
    }

    /**
     * Invokes when the picture frame is deactivated. If after being deactivated there is
     * no active frame then it asks to be selected again.
     */
    public void internalFrameDeactivated(InternalFrameEvent event) {
        final JDesktopPane desktopPane = application.getDesktopPane();
        if (desktopPane != null) {
            if (desktopPane.getSelectedFrame() == null) {
                try {
                    this.setSelected(true);
                } catch (PropertyVetoException e) {
                }
            }
        }
    }

    public void internalFrameDeiconified(InternalFrameEvent event) {
    }

    public void internalFrameIconified(InternalFrameEvent event) {
    }

    public void internalFrameOpened(InternalFrameEvent event) {
    }
}
