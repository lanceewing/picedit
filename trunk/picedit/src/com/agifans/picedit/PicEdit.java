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
    private static final String PICEDIT_NAME = "PICEDIT v1.3M2";
    
    /**
     * The graphics routines with which the application draws the screen.
     */
    private PicGraphics picGraphics;

    /**
     * The AGI picture being edited.
     */
    private Picture picture;

    /**
     * Holds the current "editing" state of everything within PICEDIT.
     */
    private EditStatus editStatus;

    /**
     * The panel containing the picture being edited.
     */
    private PicturePanel picturePanel;
    
    /**
     * The PICEDIT menu handler.
     */
    private Menu menu;
    
    /**
     * Constructor for PicEdit.
     */
    @SuppressWarnings("unchecked")
    public PicEdit() {
        this.editStatus = new EditStatus();
        this.picGraphics = new PicGraphics(this, 25);
        this.picture = new Picture(editStatus, picGraphics);
        this.picturePanel = new PicturePanel(editStatus, picGraphics, picture, this);

        // This allows us to use TAB in the application (default within Java is that it traverses between fields).
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
       
        // Create the menu and register the menu event listeners.
        this.menu = new Menu(editStatus, picGraphics, picture, this);
        
        this.getContentPane().setLayout(new BorderLayout());
        
        // Add the status bar above the picture.
        StatusBarPanel statusbar = new StatusBarPanel(this.editStatus);
        statusbar.setPreferredSize(new Dimension(320, 20));
        this.getContentPane().add(statusbar, BorderLayout.NORTH);
        
        // Add the panel that holds the picture that is being edited.
        JScrollPane pictureScrollPane = new JScrollPane(picturePanel);
        pictureScrollPane.setPreferredSize(picturePanel.getPreferredSize());
        pictureScrollPane.setMinimumSize(new Dimension(10, 10));
        this.getContentPane().add(pictureScrollPane, BorderLayout.CENTER);
        
        // Add the tool panel centered below the picture.
        JPanel toolPanelContainer = new JPanel();
        toolPanelContainer.setBackground(EgaPalette.GREY);
        toolPanelContainer.setOpaque(true);
        toolPanelContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ToolPanel toolPanel = new ToolPanel(this.editStatus);
        toolPanel.setPreferredSize(new Dimension(640, 46));
        toolPanelContainer.add(toolPanel);
        this.getContentPane().add(toolPanelContainer, BorderLayout.SOUTH);
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
        return this.picturePanel;
    }
    
    /**
     * Resizes the screen according to the new zoom factor.
     * 
     * @param zoomFactor the new zoom factor.
     */
    public void resizeScreen(int zoomFactor) {
        this.editStatus.setZoomFactor(zoomFactor);
        
        // There was previously some sort of intermittent issue in this method. It
        // would sometimes fail to resize the window when pack was called. It is 
        // possible it was a timing issue or simply that I don't know enough about
        // what is happening in Swing. So I have altered this a bit in an attempt
        // to solve the issue. It may still be happening though. I'll keep an eye
        // on it and if I see it again then I'll try something else to fix it.
        this.remove(this.picturePanel);
        picturePanel.setPreferredSize(new Dimension(320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor()));
        picturePanel.resizeOffscreenImage();
        this.add(this.picturePanel);
        
        if (SwingUtilities.getRoot(this) instanceof JFrame) {
            JFrame frame = (JFrame) SwingUtilities.getRoot(this);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
    }

    /**
     * Paints the PICEDIT applet.
     */
    public void paint(Graphics g) {
        super.paint(g);
        
        // If we are in a window then update the title to show the current picture name.
        if (SwingUtilities.getRoot(this) instanceof JFrame) {
            StringBuilder title = new StringBuilder(PICEDIT_NAME);
            title.append(" - ");
            if (editStatus.getPictureFile() == null) {
                title.append("Untitled");
            } else {
                title.append(editStatus.getPictureFile().getName());
            }
            ((JFrame) SwingUtilities.getRoot(this)).setTitle(title.toString());
        }
    }
    
    /**
     * Run PICEDIT as a standalone Java application.
     */
    public static void main(String[] args) {
        final PicEdit app = new PicEdit();

        JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                app.editStatus.savePreferences();
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);

        app.requestFocus();
    }
}
