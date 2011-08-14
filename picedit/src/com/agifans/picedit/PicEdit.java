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
     * The scroll pane that holds the picture panel.
     */
    private JScrollPane pictureScrollPane;
    
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
        this.picGraphics = new PicGraphics(320, editStatus.getPictureType().getHeight(), this, 25);
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
        pictureScrollPane = new JScrollPane(picturePanel);
        pictureScrollPane.setMinimumSize(new Dimension(10, 10));
        pictureScrollPane.setOpaque(true);
        pictureScrollPane.setBackground(Color.lightGray);
        this.getContentPane().add(pictureScrollPane, BorderLayout.CENTER);
        
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
        return picture;
    }
    
    /**
     * Gets the EditStatus for this PicEdit application. This object contains the
     * editing state of everything within PicEdit.
     * 
     * @return The EditStatus.
     */
    public EditStatus getEditStatus() {
        return editStatus;
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
        
        // Update the size of the picture according to new zoom factor.
        picturePanel.setPreferredSize(new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor()));
        picturePanel.resizeOffscreenImage();
        
        // This will tell the scroll pane to adjust itself.
        picturePanel.revalidate();
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
        frame.setMinimumSize(new Dimension(644, 450));
        frame.setResizable(true);
        frame.setVisible(true);
        
        app.requestFocus();
    }
}
