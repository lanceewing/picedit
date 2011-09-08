package com.agifans.picedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
     * The maximum allowed size of the frame.
     */
    private Dimension maximumSize;
    
    /**
     * 
     */
    private Map<Integer, Dimension> maximumSizeMap;
    
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
        this.calculateResizeDimensions();
        
        // Add the panel that holds the picture that is being edited.
        pictureScrollPane = new JScrollPane(picturePanel);
        pictureScrollPane.setMinimumSize(new Dimension(10, 10));
        pictureScrollPane.setOpaque(true);
        pictureScrollPane.setBackground(Color.lightGray);
        this.getContentPane().add(pictureScrollPane, BorderLayout.CENTER);
        this.setIconifiable(true);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addComponentListener(new PictureFrameResizeListener(this));
        this.setSize(this.maximumSizeMap.get(editStatus.getZoomFactor()));
        this.setMaximumSize(this.maximumSizeMap.get(editStatus.getZoomFactor()));
        this.setVisible(true);
    }
    
    public void calculateResizeDimensions() {
        this.maximumSizeMap = new HashMap<Integer, Dimension>();
        for (int i=2; i<=5; i++) {
            JInternalFrame frame = new JInternalFrame();
            JPanel panel = new JPanel();
            Dimension appDimension = new Dimension(320 * i, editStatus.getPictureType().getHeight() * i);
            panel.setPreferredSize(appDimension);
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setMinimumSize(new Dimension(10, 10));
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.pack();
            frame.invalidate();
            this.maximumSizeMap.put(i, frame.getSize());
        }
    }
    
    public void resizeForZoomFactor(int zoomFactor) {
        System.out.println("resizeForZoomFactor: " + zoomFactor);
        
        // TODO: Either get the frame to pack() at all sizes on creation and store in Map OR...
        // TODO: ...OR remove scroll bars before pack. 
        // TODO: ...OR do a double pack()  (before invalidate and after)
      
      
        this.editStatus.setZoomFactor(zoomFactor);
        
        // Get the current size of the picture frame.
        Dimension currentSize = getSize();
        
        // Update the size of the picture according to new zoom factor.
        picturePanel.setPreferredSize(new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor()));
        picturePanel.resizeOffscreenImage();
        
        // TODO: We need to take the desktop maximum size into account. Either that or put scroll bars on the desktop.
        maximumSize = maximumSizeMap.get(editStatus.getZoomFactor());
        
        // Apply the new maximum size to the frame.
        this.setMaximumSize(maximumSize);
        
        // Calculate the new current size. If the current size was below the packed 
        // size then we stick with that; otherwise we set it at the packed size.
        this.setSize(new Dimension(
            (int)Math.min(currentSize.getWidth(), maximumSize.getWidth()),
            (int)Math.min(currentSize.getHeight(), maximumSize.getHeight())));
        
        // This will tell the scroll pane to adjust itself.
        picturePanel.revalidate();
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
    
    /**
     * A ComponentListener for the internal picture frame that manages the maximum and minimum 
     * sizes of the internal frame when resized.
     */
    class PictureFrameResizeListener extends ComponentAdapter {
        
        /**
         * The frame that this resize listener is managing.
         */
        private PictureFrame frame;
        
        /**
         * Constructor for PictureFrameComponentListener.
         * 
         * @param frame The frame that this resize listener is managing.
         */
        PictureFrameResizeListener(PictureFrame frame) {
            this.frame = frame;
        }
        
        public void componentResized(ComponentEvent event) {
            //frame.setMinimumSize(new Dimension(300, 300));
            //frame.setMaximumSize(new Dimension(500, 500));
            //int newWidth = Math.max(Math.min(500, frame.getWidth()), 300);
            //int newHeight = Math.max(Math.min(500, frame.getHeight()), 300);
            //frame.setSize(newWidth, newHeight);
        }
    }
}
