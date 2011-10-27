package com.agifans.picedit.gui.frame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.JPanel;

import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.utils.EgaPalette;
import com.agifans.picedit.view.EgoTestHandler;

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
     * The offscreen image used to prepare the PICEDIT screen before displaying it.
     */
    private Image offScreenImage;

    /**
     * The Graphics instance associated with the offscreen image.
     */
    private Graphics2D offScreenGC;
    
    /**
     * The handler for managing the Ego Test mode.
     */
    private EgoTestHandler egoTestHandler;
    
    /**
     * Constructor for PicturePanel.
     * 
     * @param editStatus the EditStatus holding current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param egoTestHandler The handler for managing the Ego Test mode.
     */
    public PicturePanel(EditStatus editStatus, PicGraphics picGraphics, Picture picture, EgoTestHandler egoTestHandler) {
        this.editStatus = editStatus;
        this.picGraphics = picGraphics;
        this.picture = picture;
        this.egoTestHandler = egoTestHandler;
        
        Dimension appDimension = new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor());
        this.setPreferredSize(appDimension);
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
            offScreenImage = createImage(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor());
            offScreenGC = (Graphics2D) offScreenImage.getGraphics();
        }

        // Draw the background image (if there is one) to the offscreen image.
        if ((picGraphics.getBackgroundImage() != null) && (editStatus.isBackgroundEnabled())) {
            offScreenGC.drawImage(picGraphics.getBackgroundImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
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
            offScreenGC.fillRect(0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor());
        }

        if (editStatus.isDualModeEnabled()) {
            // Dual mode is when the priority and visual screens mix.
            offScreenGC.drawImage(picture.getPriorityImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

            // To create the effect demonstrated by Joakim in APE, we need a solid white.
            BufferedImage tmpVisualImage = new BufferedImage(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), BufferedImage.TYPE_INT_ARGB);
            Graphics tmpVisualGraphics = tmpVisualImage.getGraphics();
            tmpVisualGraphics.setColor(EgaPalette.WHITE);
            tmpVisualGraphics.fillRect(0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor());
            tmpVisualGraphics.drawImage(picture.getVisualImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

            // Build a RescapeOp to perform the 50% transparency.
            float[] scales = { 1f, 1f, 1f, 0.5f };
            float[] offsets = new float[4];
            RescaleOp rop = new RescaleOp(scales, offsets, null);

            // Draw the visual screen on top of the priority screen with 50% transparency.
            offScreenGC.drawImage(tmpVisualImage, rop, 0, 0);

        } else {
            if (editStatus.isPriorityShowing()) {
                offScreenGC.drawImage(picture.getPriorityImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
            } else {
                offScreenGC.drawImage(picture.getVisualImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
            }
        }

        if (editStatus.isBandsOn()) {
            offScreenGC.drawImage(picGraphics.getPriorityBandsImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
        }
        
        if (editStatus.isEgoTestEnabled()) {
            egoTestHandler.drawEgo(offScreenGC, editStatus.getZoomFactor());
        }
        
        // Draw the PicGraphics screen on top of everything else. This is mainly for the temporary lines.
        offScreenGC.drawImage(picGraphics.getScreenImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

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
