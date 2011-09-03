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
     * The slider that sets the picture position.
     */
    private JSlider positionSlider;
    
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
        
        // Add the desktop pane that holds the picture that is being edited.
        JDesktopPane desktop = new JDesktopPane();
        PictureFrame pictureFrame = new PictureFrame(editStatus, picturePanel);
        desktop.add(pictureFrame);
        this.getContentPane().add(desktop, BorderLayout.CENTER);
        
        // Add the tool panel centered below the picture.
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        positionSlider = new JSlider();
        positionSlider.setModel(new PositionSliderModel(picture));
        positionSlider.setFocusable(false);
        southPanel.add(positionSlider, BorderLayout.NORTH);
        JPanel toolPanelContainer = new JPanel();
        toolPanelContainer.setBackground(EgaPalette.GREY);
        toolPanelContainer.setOpaque(true);
        toolPanelContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ToolPanel toolPanel = new ToolPanel(this);
        toolPanel.setPreferredSize(new Dimension(640, 46));
        toolPanelContainer.add(toolPanel);
        southPanel.add(toolPanelContainer, BorderLayout.CENTER);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);
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
     * Gets the JSlider that sets the picture position.
     * 
     * @return The JSlider that set the picture position.
     */
    public JSlider getPositionSlider() {
        return this.positionSlider;
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
        
        // Make sure the slider is up to date with the picture position.
        positionSlider.getModel().setValue(picture.getPicturePosition());
    }
    
    /**
     * Run PICEDIT as a standalone Java application.
     */
    public static void main(String[] args) {
        final PicEdit app = new PicEdit();
        int storedZoomFactor = app.getEditStatus().getZoomFactor();
        app.resizeScreen(2);

        JFrame frame = new JFrame();
        frame.setTitle(PICEDIT_NAME);
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
        frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
        app.resizeScreen(storedZoomFactor);
        frame.setResizable(true);
        frame.setVisible(true);
        
        app.requestFocus();
    }
}
