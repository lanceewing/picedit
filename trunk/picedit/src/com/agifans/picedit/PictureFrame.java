package com.agifans.picedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * 
 * 
 * @author Lance Ewing
 */
public class PictureFrame extends JInternalFrame {

    /**
     * The scroll pane that holds the picture panel.
     */
    private JScrollPane pictureScrollPane;
    
    /**
     * The panel containing the picture being edited.
     */
    private PicturePanel picturePanel;
    
    /**
     * 
     */
    private EditStatus editStatus;
    
    /**
     * 
     */
    private int widthPadding;
    
    /**
     * 
     */
    private int heightPadding;
    
    /**
     * Constructor for PictureFrame.
     * 
     * @param editStatus 
     * 
     * TODO: Move creation of the PicturePanel into this class.
     * TODO: Move creation of EditStatus, PicGraphics, Picture into this class.
     */
    public PictureFrame(EditStatus editStatus, PicturePanel picturePanel) {
        this.editStatus = editStatus;
        
        this.picturePanel = picturePanel;
        
        //int storedZoomFactor = editStatus.getZoomFactor();
        //this.resizeForZoomFactor(2);
        
        // Add the panel that holds the picture that is being edited.
        pictureScrollPane = new JScrollPane(picturePanel);
        pictureScrollPane.setMinimumSize(new Dimension(10, 10));
        pictureScrollPane.setOpaque(true);
        pictureScrollPane.setBackground(Color.lightGray);
        this.getContentPane().add(pictureScrollPane, BorderLayout.CENTER);
        this.pack();
        this.widthPadding = this.getWidth() - ((int)picturePanel.getPreferredSize().getWidth());
        this.heightPadding = this.getHeight() - ((int)picturePanel.getPreferredSize().getHeight());
        this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
        this.setIconifiable(true);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
        
        //this.resizeForZoomFactor(storedZoomFactor);
    }
    
    public void resizeForZoomFactor(int zoomFactor) {
        this.editStatus.setZoomFactor(zoomFactor);
        
        // Update the size of the picture according to new zoom factor.
        picturePanel.setPreferredSize(new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor()));
        picturePanel.resizeOffscreenImage();
        
        // This will tell the scroll pane to adjust itself.
        picturePanel.revalidate();
        
        this.setMaximumSize(new Dimension(picturePanel.getWidth() + widthPadding, picturePanel.getHeight() + heightPadding));
        this.revalidate();
    }
    
    /**
     * Paints the PICEDIT applet.
     */
    public void paint(Graphics g) {
        super.paint(g);
        
        // Make sure the slider is up to date with the picture position.
        //positionSlider.getModel().setValue(picture.getPicturePosition());
        
        // If we are in a window then update the title to show the current picture name.
        if (editStatus.getPictureFile() == null) {
            this.setTitle("Untitled");
        } else {
            this.setTitle(editStatus.getPictureFile().getName());
        }
    }
}
