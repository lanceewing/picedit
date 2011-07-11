package com.agifans.picedit;

import java.awt.*;
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
    private static final String PICEDIT_NAME = "PICEDIT v1.3M1";
    
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
     * Constructor for PicEdit.
     */
    @SuppressWarnings("unchecked")
    public PicEdit() {
        this.editStatus = new EditStatus();
        this.editStatus.setZoomFactor(1);
        this.picGraphics = new PicGraphics(this, 25);
        this.picture = new Picture(editStatus, picGraphics);
        this.picturePanel = new PicturePanel(editStatus, picGraphics, picture, this);

        // This allows us to use TAB in the application (default within Java is that it traverses between fields).
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
       
        this.add(picturePanel);
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
            if (editStatus.getPictureName() == null) {
                title.append("Untitled");
            } else {
                title.append(editStatus.getPictureName());
            }
            ((JFrame) SwingUtilities.getRoot(this)).setTitle(title.toString());
        }
    }
    
    /**
     * Run PICEDIT as a standalone Java application.
     */
    public static void main(String[] args) {
        PicEdit app = new PicEdit();

        JFrame frame = new JFrame("PICEDIT v1.2.1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        app.requestFocus();
    }
}
