package com.agifans.picedit.gui.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.picture.PictureCode;
import com.agifans.picedit.types.PictureType;
import com.agifans.picedit.utils.EgaPalette;
import com.agifans.picedit.view.EgoTestHandler;

/**
 * This panel is responsible for rendering the picture, temporary lines, the
 * background image if it is activated, the bands if it is activated, the
 * dual mode if it is activated and Ego if Ego test mode is activated,
 * essentially everything that is drawn within the picture part of a PictureFrame.
 * 
 * @author Lance Ewing
 */
public class PicturePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    /**
     * Holds the RGB values for the 16 EGA colours.
     */
    private final static int[] colours = EgaPalette.colours;
    
    /**
     * The Image for the background image.
     */
    private Image backgroundImage;

    /**
     * The Image that is drawn for the priority bands when activated.
     */
    private Image bandsImage;
    
    /**
     * The Image for the overlay screen.
     */
    private Image overlayScreenImage;

    /**
     * The RGB data array for the overlay screen.
     */
    private int overlayScreen[];
    
    /**
     * The AGI picture being edited.
     */
    private Picture picture;

    /**
     * Holds the current "editing" state of everything within PICEDIT.
     */
    private EditStatus editStatus;

    /**
     * Holds two off screen images that are used interchangeably for offscreen rendering.
     */
    private OffScreenGraphics offScreenGraphics;
    
    /**
     * The handler for managing the Ego Test mode.
     */
    private EgoTestHandler egoTestHandler;
    
    /**
     * Holds the RGB pixel data that existed on the overlay screen underneath the temporary line.
     */
    private int[] bgLineData;
    
    /**
     * Constructor for PicturePanel.
     * 
     * @param editStatus The EditStatus holding current picture editor state.
     * @param picture The AGI PICTURE currently being edited.
     * @param egoTestHandler The handler for managing the Ego Test mode.
     */
    public PicturePanel(EditStatus editStatus, Picture picture, EgoTestHandler egoTestHandler) {
        this.editStatus = editStatus;
        this.picture = picture;
        this.egoTestHandler = egoTestHandler;
        this.bgLineData = new int[1024];
        this.bgLineData[0] = 0;
        
        createOverlayScreenImage(160, editStatus.getPictureType().getHeight());
        createPriorityBandsImage(PictureType.AGI);
        
        Dimension appDimension = new Dimension(320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor());
        this.setPreferredSize(appDimension);
    }
    
    /**
     * Invoked when a new background image is loaded.
     */
    public void clearOffscreenGraphics() {
        offScreenGraphics.clear();
    }
    
    /**
     * Paints the picture panel on the offscreen Graphics. This is kept separate from the 
     * paint() method for performance reasons. The screen refresh timer invokes this method
     * at regular intervals.
     */
    public void paintOffscreenImage() {
    	if (offScreenGraphics != null) {
	    	Graphics2D offScreenGC = offScreenGraphics.getActiveGraphics();
	    	
	        // Draw the background image (if there is one) to the offscreen image.
	        if ((this.backgroundImage != null) && (editStatus.isBackgroundEnabled())) {
	            offScreenGC.drawImage(this.backgroundImage, 0, 0, 320, editStatus.getPictureType().getHeight(), this);
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
	            offScreenGC.fillRect(0, 0, 320, editStatus.getPictureType().getHeight());
	        }
	
	        if (editStatus.isDualModeEnabled()) {
	            // Dual mode is when the priority and visual screens mix.
	            offScreenGC.drawImage(picture.getPriorityImage(), 0, 0, 320, editStatus.getPictureType().getHeight(), this);
	
	            // To create the effect demonstrated by Joakim in APE, we need a solid white.
	            BufferedImage tmpVisualImage = new BufferedImage(320, editStatus.getPictureType().getHeight(), BufferedImage.TYPE_INT_ARGB);
	            Graphics tmpVisualGraphics = tmpVisualImage.getGraphics();
	            tmpVisualGraphics.setColor(EgaPalette.WHITE);
	            tmpVisualGraphics.fillRect(0, 0, 320, editStatus.getPictureType().getHeight());
	            tmpVisualGraphics.drawImage(picture.getVisualImage(), 0, 0, 320, editStatus.getPictureType().getHeight(), this);
	
	            // Build a RescapeOp to perform the 50% transparency.
	            float[] scales = { 1f, 1f, 1f, 0.5f };
	            float[] offsets = new float[4];
	            RescaleOp rop = new RescaleOp(scales, offsets, null);
	
	            // Draw the visual screen on top of the priority screen with 50% transparency.
	            offScreenGC.drawImage(tmpVisualImage, rop, 0, 0);
	
	        } else {
	            if (editStatus.isPriorityShowing()) {
	                offScreenGC.drawImage(picture.getPriorityImage(), 0, 0, 320, editStatus.getPictureType().getHeight(), this);
	            } else {
	                offScreenGC.drawImage(picture.getVisualImage(), 0, 0, 320, editStatus.getPictureType().getHeight(), this);
	            }
	        }
	
	        if (editStatus.isBandsOn()) {
	            offScreenGC.drawImage(this.bandsImage, 0, 0, 320, editStatus.getPictureType().getHeight(), this);
	        }
	        
	        if (editStatus.isEgoTestEnabled()) {
	            egoTestHandler.drawEgo(offScreenGC, 1);
	        }
	        
	        offScreenGraphics.toggle();
    	}
    }
    
    /**
     * Paints the PICEDIT screen. A double buffering mechanism is implemented
     * in order to reduce flicker as much as possible.
     * 
     * @param g the Graphics object to paint on.
     */
    public void paint(Graphics g) {
    	if (offScreenGraphics == null) {
    		offScreenGraphics = new OffScreenGraphics();
    	}
    	
        // Display the off screen image to the user, stretched by the zoom factor.
        g.drawImage(offScreenGraphics.getImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
        
        // Draw the overlay screen on top of everything else. This is mainly for the temporary lines.
        if (editStatus.isLineBeingDrawn()) {
        	g.drawImage(this.overlayScreenImage, 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
        } else if (bgLineData[0] != 0) {
        	// Clear temporary line if line is no longer being drawn.
        	clearTemporaryLine();
        }
        
        // Highlight the current selection if the zoom factor is big enough.
        if (editStatus.getZoomFactor() > 1) {
            highlightSelection(g);
        }
    }
    
    /**
     * Highlights the currently selected picture codes by drawing boxes around the points.
     * 
     * @param graphics The Graphics2D to draw the highlight boxes on.
     */
    private void highlightSelection(Graphics graphics) {
        // TODO: This falls over if the selection is at the end of the picture and the selection is deleted.
        int firstSelectedPosition = picture.getFirstSelectedPosition();
        int lastSelectedPosition = picture.getLastSelectedPosition();
        
        if ((firstSelectedPosition > -1) && (lastSelectedPosition > -1)) {
            List<PictureCode> pictureCodes = picture.getPictureCodes();
            for (int picturePosition = firstSelectedPosition; picturePosition <= lastSelectedPosition; picturePosition++) {
                PictureCode pictureCode = pictureCodes.get(picturePosition);
                // It only makes sense to do something for data codes, and only if they're points.
                if (pictureCode.isDataCode()) {
                    Point point = pictureCode.getPoint();

                    if (point != null) {
                        // Calculate the x and y position of the point, scaling for zoom factor.
                        // TODO: Need to adjust this code for SCI0.
                        int x = (point.x << 1) * editStatus.getZoomFactor();
                        int y = (point.y) * editStatus.getZoomFactor();
                        
                        graphics.setColor(Color.RED);
                        graphics.drawLine(x - 2, y - 2, (x - 2) + (editStatus.getZoomFactor() << 1) + 3, (y - 2) + editStatus.getZoomFactor() + 3);
                        graphics.drawLine(x - 2, (y - 2) + editStatus.getZoomFactor() + 3, (x - 2) + (editStatus.getZoomFactor() << 1) + 3, y - 2);
                      
//                        // Different tools might be highlighted in different ways, so this is 
//                        // deliberately written as a switch to allow for this.
//                        switch (pictureCode.getType()) {
//                            case ABSOLUTE_POINT_DATA:
//                                graphics.setColor(Color.RED);
//                                graphics.drawRect(x - 2, y - 2, (editStatus.getZoomFactor() << 1) + 3, editStatus.getZoomFactor() + 3);
//                                graphics.setColor(EgaPalette.WHITE);
//                                graphics.drawRect(x - 1, y - 1, (editStatus.getZoomFactor() << 1) + 1, editStatus.getZoomFactor() + 1);
//                                break;
//                            case BRUSH_POINT_DATA:
//                                graphics.setColor(Color.RED);
//                                graphics.drawRect(x - 2, y - 2, (editStatus.getZoomFactor() << 1) + 3, editStatus.getZoomFactor() + 3);
//                                graphics.setColor(EgaPalette.WHITE);
//                                graphics.drawRect(x - 1, y - 1, (editStatus.getZoomFactor() << 1) + 1, editStatus.getZoomFactor() + 1);
//                                break;
//                            case FILL_POINT_DATA:
//                                graphics.setColor(Color.RED);
//                                graphics.drawRect(x - 2, y - 2, (editStatus.getZoomFactor() << 1) + 3, editStatus.getZoomFactor() + 3);
//                                graphics.setColor(EgaPalette.WHITE);
//                                graphics.drawRect(x - 1, y - 1, (editStatus.getZoomFactor() << 1) + 1, editStatus.getZoomFactor() + 1);
//                                break;
//                            case RELATIVE_POINT_DATA:
//                                graphics.setColor(Color.RED);
//                                graphics.drawRect(x - 2, y - 2, (editStatus.getZoomFactor() << 1) + 3, editStatus.getZoomFactor() + 3);
//                                graphics.setColor(EgaPalette.WHITE);
//                                graphics.drawRect(x - 1, y - 1, (editStatus.getZoomFactor() << 1) + 1, editStatus.getZoomFactor() + 1);
//                                break;
//                            case X_POSITION_DATA:
//                                graphics.setColor(Color.RED);
//                                graphics.drawRect(x - 2, y - 2, (editStatus.getZoomFactor() << 1) + 3, editStatus.getZoomFactor() + 3);
//                                graphics.setColor(EgaPalette.WHITE);
//                                graphics.drawRect(x - 1, y - 1, (editStatus.getZoomFactor() << 1) + 1, editStatus.getZoomFactor() + 1);
//                                break;
//                            case Y_POSITION_DATA:
//                                graphics.setColor(Color.RED);
//                                graphics.drawRect(x - 2, y - 2, (editStatus.getZoomFactor() << 1) + 3, editStatus.getZoomFactor() + 3);
//                                graphics.setColor(EgaPalette.WHITE);
//                                graphics.drawRect(x - 1, y - 1, (editStatus.getZoomFactor() << 1) + 1, editStatus.getZoomFactor() + 1);
//                                break;
//                        }
                    }
                }
            }
        }
    }
    
    /**
     * Creates the Image that is displayed when the show priority bands feature
     * is turned on.
     * 
     * @param pictureType The type of picture being edited (AGI/SCI0).
     */
    private void createPriorityBandsImage(PictureType pictureType) {
        bandsImage = new BufferedImage(pictureType.getWidth(), pictureType.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics bandsGraphics = bandsImage.getGraphics();

        // Draw the bands onto the image so it is ready to be displayed when
        // needed.
        if (pictureType.equals(PictureType.SCI0)) {
            int currentPriorityBand = 0;
            
            // For SCI0, the top 42 lines are for priority 0. The other 14 bands
            // get an even share of the 148 remaining lines (which, btw, doesn't
            // divide evenly, so the bands are not even as then are in AGI).
            for (int y = 0; y < 190; y++) {
                int priorityBand = ((int) ((y - 42) / ((190 - 42) / 14))) + 1;

                if (priorityBand != currentPriorityBand) {
                    currentPriorityBand = priorityBand;
                    bandsGraphics.setColor(EgaPalette.COLOR_OBJECTS[priorityBand]);
                    bandsGraphics.drawLine(0, y, 319, y);
                }
            }

        } else if (pictureType.equals(PictureType.AGI)) {
            int currentPriorityBand = 4;
            
            for (int y = 0; y < 168; y++) {
                // For AGI it is evenly split, 168 lines split 14 ways.
                int priorityBand = (y / 12) + 1;

                // Make sure priority band is 4 or above for AGI since the
                // bottom four priority colours are reserved as control lines.
                if (priorityBand < 4) {
                    priorityBand = 4;
                }

                if (priorityBand != currentPriorityBand) {
                    currentPriorityBand = priorityBand;
                    bandsGraphics.setColor(EgaPalette.COLOR_OBJECTS[priorityBand]);
                    bandsGraphics.drawLine(0, y, 319, y);
                }
            }
        }
    }

    /**
     * Clears the overlay screen that temporary lines are drawn to.
     */
    public void clearOverlayScreen() {
        Arrays.fill(this.overlayScreen, EgaPalette.transparent);
    }

    /**
     * Creates the overlay image on which the temporary lines are drawn.
     */
    private void createOverlayScreenImage(int width, int height) {
        this.overlayScreen = new int[width * height];
        Arrays.fill(this.overlayScreen, EgaPalette.transparent);
        DataBufferInt dataBuffer = new DataBufferInt(this.overlayScreen, this.overlayScreen.length);
        ColorModel colorModel = ColorModel.getRGBdefault();
        int[] bandMasks = new int[] { 0x00ff0000, // red mask
                0x0000ff00, // green mask
                0x000000ff, // blue mask
                0xff000000 }; // alpha mask
        WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, width, bandMasks, null);
        this.overlayScreenImage = new BufferedImage(colorModel, raster, false, null);
    }

    /**
     * Sets the background image.
     * 
     * @param backgroundImage the background image.
     */
    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
    /**
     * Clears the previously drawn temporary line by redrawing the pixels that where
     * behind it prior to the line being drawn. Usually this would be transparent 
     * pixels, but it supports other things being on the overlay screen... just in 
     * case this is ever needed.
     */
    private void clearTemporaryLine() {
        // Redraw the pixels that were behind the previous temporary line.
        int[] lineData = this.bgLineData;
        int bgLineLength = lineData[0];
        if (bgLineLength > 0) {
            for (int i = 1; i < bgLineLength;) {
                int index = lineData[i++];
                overlayScreen[index] = lineData[i++];
            }
            
            // Start again with a fresh array.
            this.bgLineData = new int[1024];
            this.bgLineData[0] = 0;
        }
    }
    
    /**
     * Draws a temporary picture line. These are the lines that are drawn while
     * a line drawing tool is active (line, pen, step). The line follows the
     * mouse's movements and allows the user to see where the line is going to
     * fall if they click in that position.
     * 
     * @param x1 Start X Coordinate.
     * @param y1 Start Y Coordinate.
     * @param x2 End X Coordinate.
     * @param y2 End Y Coordinate.
     * @param c the colour of the line.
     */
    public final void drawTemporaryLine(int x1, int y1, int x2, int y2, int c) {
        int x, y, index, endIndex, rgbCode;

        // Calculate flash index up front before x1/y1/x2/y2 are adjusted.
        int flashIndex = (y2 << 7) + (y2 << 5) + x2;
        
        // Redraw the pixels that were behind the previous temporary line.
        clearTemporaryLine();

        // Start storing at index 1. We'll use 0 for the length.
        int bgIndex = 1;
        
        // Vertical Line.
        if (x1 == x2) {
            if (y1 > y2) {
                y = y1;
                y1 = y2;
                y2 = y;
            }

            index = (y1 << 7) + (y1 << 5) + x1;
            endIndex = (y2 << 7) + (y2 << 5) + x2;
            rgbCode = colours[c];

            for (; index <= endIndex; index += 160) {
                bgLineData[bgIndex++] = index;
                bgLineData[bgIndex++] = overlayScreen[index];
                overlayScreen[index] = rgbCode;
            }
        }
        // Horizontal Line.
        else if (y1 == y2) {
            if (x1 > x2) {
                x = x1;
                x1 = x2;
                x2 = x;
            }

            index = (y1 << 7) + (y1 << 5) + x1;
            endIndex = (y2 << 7) + (y2 << 5) + x2;
            rgbCode = colours[c];

            for (; index <= endIndex; index++) {
                bgLineData[bgIndex++] = index;
                bgLineData[bgIndex++] = overlayScreen[index];
                overlayScreen[index] = rgbCode;
            }

        } else {
            int deltaX = x2 - x1;
            int deltaY = y2 - y1;
            int stepX = 1;
            int stepY = 1;
            int detDelta;
            int errorX;
            int errorY;
            int count;

            if (deltaY < 0) {
                stepY = -1;
                deltaY = -deltaY;
            }

            if (deltaX < 0) {
                stepX = -1;
                deltaX = -deltaX;
            }

            if (deltaY > deltaX) {
                count = deltaY;
                detDelta = deltaY;
                errorX = deltaY / 2;
                errorY = 0;
            } else {
                count = deltaX;
                detDelta = deltaX;
                errorX = 0;
                errorY = deltaX / 2;
            }

            x = x1;
            y = y1;
            index = (y1 << 7) + (y1 << 5) + x1;
            rgbCode = colours[c];

            bgLineData[bgIndex++] = index;
            bgLineData[bgIndex++] = overlayScreen[index];
            overlayScreen[index] = rgbCode;

            do {
                errorY = (errorY + deltaY);
                if (errorY >= detDelta) {
                    errorY -= detDelta;
                    y += stepY;
                }

                errorX = (errorX + deltaX);
                if (errorX >= detDelta) {
                    errorX -= detDelta;
                    x += stepX;
                }

                index = (y << 7) + (y << 5) + x;
                bgLineData[bgIndex++] = index;
                bgLineData[bgIndex++] = overlayScreen[index];
                overlayScreen[index] = rgbCode;
                count--;
            } while (count > 0);
        }

        // Make the end of the temporary line flash so that it is obvious where the mouse is.
        int brightness = (int) ((System.currentTimeMillis() >> 1) & 0xFF);
        overlayScreen[flashIndex] = (new Color(brightness, brightness, brightness)).getRGB();
        
        // Store the length of the stored pixel data in first slot.
        bgLineData[0] = bgIndex;
    }
    
    /**
     * Holds two off screen images that are used interchangeably for offscreen rendering of 
     * the PicturePanel content.
     */
    class OffScreenGraphics {
    	
        /**
         * The offscreen images used to prepare the PICEDIT screen before displaying it.
         */
    	private Image[] offScreenImages;

        /**
         * The Graphics instances associated with each offscreen image.
         */
        private Graphics2D[] offScreenGCs;

        /**
         * The currently active offscreen Image/Graphics index.
         */
        private int activeGraphics;
        
        OffScreenGraphics() {
        	clear();
        }
        
        void clear() {
            this.offScreenImages = new Image[2];
            this.offScreenGCs = new Graphics2D[2];
            this.offScreenImages[0] = PicturePanel.this.createImage(320, editStatus.getPictureType().getHeight());
            this.offScreenImages[1] = PicturePanel.this.createImage(320, editStatus.getPictureType().getHeight());
            this.offScreenGCs[0] = (Graphics2D) offScreenImages[0].getGraphics();
            this.offScreenGCs[1] = (Graphics2D) offScreenImages[1].getGraphics();
            this.activeGraphics = 0;
        }
        
        Image getImage() {
        	return offScreenImages[(activeGraphics + 1) % 2];
        }
        
        Graphics2D getActiveGraphics() {
        	return offScreenGCs[activeGraphics];
        }
        
        void toggle() {
        	activeGraphics = ((activeGraphics + 1) % 2);
        }
    }
}
