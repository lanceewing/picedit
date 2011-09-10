package com.agifans.picedit;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import javax.swing.*;

/**
 * The main class for the PICEDIT application.
 * 
 * @author Lance Ewing
 */
public final class PicEdit extends JApplet {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constant for the name of the current version of PICEDIT.
     */
    private static final String PICEDIT_NAME = "PICEDIT v1.3M3";
    
    /**
     * The internal frame for the picture panel.
     */
    private PictureFrame pictureFrame;
    
    /**
     * The PICEDIT menu handler.
     */
    private Menu menu;
    
    /**
     * Constructor for PicEdit.
     */
    @SuppressWarnings("unchecked")
    public PicEdit() {
        this.pictureFrame = new PictureFrame(this);

        // This allows us to use TAB in the application (default within Java is that it traverses between fields).
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        
        // Create the menu and register the menu event listeners.
        this.menu = new Menu(pictureFrame.getEditStatus(), pictureFrame.getPicGraphics(), pictureFrame.getPicture(), this);
        
        this.getContentPane().setLayout(new BorderLayout());
        
        // Add the status bar above the picture.
        StatusBarPanel statusbar = new StatusBarPanel(pictureFrame.getEditStatus());
        statusbar.setPreferredSize(new Dimension(320, 20));
        this.getContentPane().add(statusbar, BorderLayout.NORTH);
        
        // Add the desktop pane that holds the picture that is being edited.
        JDesktopPane desktop = new JDesktopPane();
        desktop.add(pictureFrame);
        this.getContentPane().add(desktop, BorderLayout.CENTER);
        
        // Add the tool panel centered below the picture.
        JPanel toolPanelContainer = new JPanel();
        toolPanelContainer.setBackground(EgaPalette.GREY);
        toolPanelContainer.setOpaque(true);
        toolPanelContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ToolPanel toolPanel = new ToolPanel(this);
        toolPanel.setPreferredSize(new Dimension(640, 46));
        toolPanelContainer.add(toolPanel);
        this.getContentPane().add(toolPanelContainer, BorderLayout.SOUTH);
    }

    /**
     * Gets the Picture that is currently being edited with PicEdit.
     * 
     * @return The Picture that is currently being edited.
     */
    public Picture getPicture() {
        return pictureFrame.getPicture();
    }
    
    /**
     * Gets the EditStatus for this PicEdit application. This object contains the
     * editing state of everything within PicEdit.
     * 
     * @return The EditStatus.
     */
    public EditStatus getEditStatus() {
        return pictureFrame.getEditStatus();
    }
    
    /**
     * Returns the Menu used by PICEDIT.
     * 
     * @return The Menu used by PICEDIT.
     */
    public Menu getMenu() {
        return menu;
    }
    
    /**
     * Returns the panel that holds the picture.
     * 
     * @return The panel that holds the picture.
     */
    public PicturePanel getPicturePanel() {
        return pictureFrame.getPicturePanel();
    }
    
    public PictureFrame getPictureFrame() {
        return pictureFrame;
    }
    
    /**
     * Resizes the screen according to the new zoom factor.
     * 
     * @param zoomFactor the new zoom factor.
     */
    public void resizeScreen(int zoomFactor) {
        pictureFrame.resizeForZoomFactor(zoomFactor);
    }

    /**
     * Paints the PICEDIT applet.
     */
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    /**
     * Run PICEDIT as a standalone Java application.
     */
    public static void main(String[] args) {
        final PicEdit app = new PicEdit();

        JFrame frame = new JFrame();
        frame.setTitle(PICEDIT_NAME);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                app.getEditStatus().savePreferences();
            }
        });
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        app.requestFocus();
    }
}
