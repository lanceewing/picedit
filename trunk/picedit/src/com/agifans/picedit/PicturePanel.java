package com.agifans.picedit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * This panel renders the main picture editor image, which includes the 
 * picture being edited, the status line, and tool bar. This code was
 * originally in the PicEdit class but has now been split out into a 
 * separate panel to allow other Swing components to appear within the
 * main applet, including things like a proper menu, scroll bars, etc.
 * 
 * @author Lance Ewing
 */
public class PicturePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
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
     * The PICEDIT application component.
     */
    private PicEdit application;
    
    /**
     * The offscreen image used to prepare the PICEDIT screen before displaying it.
     */
    private Image offScreenImage;

    /**
     * The Graphics instance associated with the offscreen image.
     */
    private Graphics2D offScreenGC;
    
    /**
     * Constructor for PicturePanel.
     * 
     * @param editStatus the EditStatus holding current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param application the PICEDIT application component.
     */
    public PicturePanel(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
        this.editStatus = editStatus;
        this.picGraphics = picGraphics;
        this.picture = picture;
        this.application = application;
        
        Dimension appDimension = new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor());
        this.setPreferredSize(appDimension);
        
        MouseHandler mouseHandler = new MouseHandler(editStatus, picGraphics, picture, application);
        KeyboardHandler keyboardHandler = new KeyboardHandler(editStatus, picGraphics, picture, application);
        
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);
        this.application.addKeyListener(keyboardHandler);
    }
    
    /**
     * Invoked by resizeScreen when the offscreen image needs to be recreated at
     * a new screen size.
     */
    public void resizeOffscreenImage() {
        // If we remove it then paint will recreate it at the new size.
        this.offScreenImage = null;
    }
    
    /**
     * Paints the PICEDIT screen. A double buffering mechanism is implemented
     * in order to reduce flicker as much as possible.
     * 
     * @param g the Graphics object to paint on.
     */
    public void paint(Graphics g) {
        // Create the offscreen image the first time.
        if (offScreenImage == null) {
            offScreenImage = createImage(320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
            offScreenGC = (Graphics2D) offScreenImage.getGraphics();
        }

        // If we're in text mode ("Help" and "View Data"), then just display the image.
        if (editStatus.isTextMode()) {
            offScreenGC.drawImage(picGraphics.getTextImage(), 0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor(), this);
        } else {
            // Draw the background image (if there is one) to the offscreen image.
            if ((picGraphics.getBackgroundImage() != null) && (editStatus.isBackgroundEnabled())) {
                offScreenGC.drawImage(picGraphics.getBackgroundImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), 168 * editStatus.getZoomFactor(), this);
            } else {
                // Otherwise use the default background colour for the corresponding AGI screen (visual/priority).
                if (editStatus.isDualModeEnabled()) {
                    offScreenGC.setColor(EgaPalette.RED);
                } else if (!editStatus.isPriorityShowing()) {
                    offScreenGC.setColor(EgaPalette.WHITE);
                } else if (editStatus.isBandsOn()) {
                    offScreenGC.setColor(EgaPalette.DARKGREY);
                } else {
                    offScreenGC.setColor(EgaPalette.RED);
                }
                offScreenGC.fillRect(0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
            }

            if (editStatus.isDualModeEnabled()) {
                // Dual mode is when the priority and visual screens mix.
                offScreenGC.drawImage(picture.getPriorityImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

                // To create the effect demonstrated by Joakim in APE, we need a solid white.
                BufferedImage tmpVisualImage = new BufferedImage(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), BufferedImage.TYPE_INT_ARGB);
                Graphics tmpVisualGraphics = tmpVisualImage.getGraphics();
                tmpVisualGraphics.setColor(EgaPalette.WHITE);
                tmpVisualGraphics.fillRect(0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor());
                tmpVisualGraphics.drawImage(picture.getVisualImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

                // Build a RescapeOp to perform the 50% transparency.
                float[] scales = { 1f, 1f, 1f, 0.5f };
                float[] offsets = new float[4];
                RescaleOp rop = new RescaleOp(scales, offsets, null);

                // Draw the visual screen on top of the priority screen with 50% transparency.
                offScreenGC.drawImage(tmpVisualImage, rop, 0, 9 * editStatus.getZoomFactor());

            } else {
                if (editStatus.isPriorityShowing()) {
                    offScreenGC.drawImage(picture.getPriorityImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
                } else {
                    offScreenGC.drawImage(picture.getVisualImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
                }
            }

            if (editStatus.isBandsOn()) {
                offScreenGC.drawImage(picGraphics.getPriorityBandsImage(), 0, 9 * editStatus.getZoomFactor(), 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
            }
            
            // Draw the  PICEDIT screen to the offscreen image (transparent pixels will show the background).
            offScreenGC.drawImage(picGraphics.getScreenImage(), 0, 0, 320 * editStatus.getZoomFactor(), 200 * editStatus.getZoomFactor(), this);
        }

        // Now display the screen to the user.
        g.drawImage(offScreenImage, 0, 0, this);    
    }
    
    /**
     * Override the default update behaviour to stop the screen from being
     * cleared each time.
     * 
     * @param g the Graphics object to update.
     */
    public void update(Graphics g) {
        paint(g);
    }
}
