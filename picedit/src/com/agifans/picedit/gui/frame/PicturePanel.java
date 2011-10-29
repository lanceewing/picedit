package com.agifans.picedit.gui.frame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.swing.JPanel;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.types.PictureType;
import com.agifans.picedit.utils.EgaPalette;
import com.agifans.picedit.view.EgoTestHandler;

/**
 * This panel is responsible for rendering the picture, temporary lines, the
 * background image if it is activated, the bands if it is activated, the
 * dual mode if it is activated and Ego if Ego test mode is activated. 
 * Essentially everything that is drawn within the picture part of a PictureFrame.
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
     * The Image for the PICEDIT screen.
     */
    private Image screenImage;

    /**
     * The Image that is drawn for the priority bands when activated.
     */
    private Image bandsImage;

    /**
     * The RGB data array for the PICEDIT screen.
     */
    private int screen[];

    /**
     * Duration between frames (eg. 40 * 25 = 1000ms).
     */
    private long frameDuration;

    /**
     * The next time that the frame should be drawn.
     */
    public long nextTime = 0;

    /**
     * The PICEDIT application.
     */
    private PicEdit application;
    
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
     * @param editStatus The EditStatus holding current picture editor state.
     * @param picture The AGI PICTURE currently being edited.
     * @param application The PICEDIT application.
     * @param egoTestHandler The handler for managing the Ego Test mode.
     */
    public PicturePanel(EditStatus editStatus, Picture picture, PicEdit application, EgoTestHandler egoTestHandler) {
        this.editStatus = editStatus;
        this.picture = picture;
        this.application = application;
        this.egoTestHandler = egoTestHandler;
        
        createScreenImage(320, editStatus.getPictureType().getHeight());
        
        this.frameDuration = (1000 / 25);
        this.nextTime = System.currentTimeMillis() + this.frameDuration;

        createPriorityBandsImage(PictureType.AGI);
        
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
        if ((this.getBackgroundImage() != null) && (editStatus.isBackgroundEnabled())) {
            offScreenGC.drawImage(this.getBackgroundImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
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
            offScreenGC.drawImage(this.getPriorityBandsImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);
        }
        
        if (editStatus.isEgoTestEnabled()) {
            egoTestHandler.drawEgo(offScreenGC, editStatus.getZoomFactor());
        }
        
        // TODO: Temporary line screen does not need to be drawn every time; only when a temp line exists.
        // TODO: Only the Temporary line part of the image needs to be drawn, not the whole temp line screen.
        
        // Draw the PicGraphics screen on top of everything else. This is mainly for the temporary lines.
        offScreenGC.drawImage(this.getScreenImage(), 0, 0, 320 * editStatus.getZoomFactor(), editStatus.getPictureType().getHeight() * editStatus.getZoomFactor(), this);

        // Now display the screen to the user.
        g.drawImage(offScreenImage, 0, 0, this);
    }
    
    /**
     * Creates the Image that is displayed when the show priority bands feature
     * is turned on.
     * 
     * @param pictureType The type of picture being edited (AGI/SCI0).
     */
    public void createPriorityBandsImage(PictureType pictureType) {
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
     * Clears the picture part of the PICEDIT editor screen.
     * 
     * @param pictureType The type of picture currently being edited.
     */
    public void clearDrawingArea(PictureType pictureType) {
        Arrays.fill(this.screen, 0, pictureType.getNumberOfEGAPixels(), EgaPalette.transparent);
    }

    /**
     * Creates the image for the editor screen on which the editor panel, menu and temporary
     * lines are drawn.
     */
    public void createScreenImage(int width, int height) {
        this.screen = new int[width * height];
        Arrays.fill(this.screen, EgaPalette.transparent);
        DataBufferInt dataBuffer = new DataBufferInt(this.screen, this.screen.length);
        ColorModel colorModel = ColorModel.getRGBdefault();
        int[] bandMasks = new int[] { 0x00ff0000, // red mask
                0x0000ff00, // green mask
                0x000000ff, // blue mask
                0xff000000 }; // alpha mask
        WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, width, bandMasks, null);
        this.screenImage = new BufferedImage(colorModel, raster, false, null);
    }

    /**
     * Draws the screen data onto the Image.
     */
    public void drawFrame() {
        application.repaint();
    }

    /**
     * Checks whether the frame should be drawn based on the frame rate. If so
     * then it draws the frame.
     */
    public void checkDrawFrame() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > nextTime) {
            drawFrame();
            nextTime = currentTime + frameDuration;
        }
    }

    /**
     * Gets the underlying screen data array.
     * 
     * @return the underlying screen data array.
     */
    public int[] getScreen() {
        return screen;
    }
    
    /**
     * Returns the main editor screen Image.
     * 
     * @return the main editor screen Image.
     */
    public Image getScreenImage() {
        return screenImage;
    }

    /**
     * Gets the background image.
     * 
     * @return the background image.
     */
    public Image getBackgroundImage() {
        return backgroundImage;
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
     * Gets the priority bands image.
     * 
     * @return the priority bands image.
     */
    public Image getPriorityBandsImage() {
        return bandsImage;
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
     * @param bgLineData an array to store the original pixels behind the temporary line.
     */
    public final void drawTemporaryLine(int x1, int y1, int x2, int y2, int c, int[] bgLineData) {
        int x, y, index, endIndex, rgbCode;

        // Redraw the pixels that were behind the previous temporary line.
        int bgLineLength = bgLineData[0];
        for (int i = 1; i < bgLineLength;) {
            index = bgLineData[i++];
            screen[index + 1] = screen[index] = bgLineData[i++];
        }

        // Start storing at index 1. We'll use 0 for the length.
        int bgIndex = 1;

        // Vertical Line.
        if (x1 == x2) {
            if (y1 > y2) {
                y = y1;
                y1 = y2;
                y2 = y;
            }

            index = (y1 << 8) + (y1 << 6) + (x1 << 1);
            endIndex = (y2 << 8) + (y2 << 6) + (x2 << 1);
            rgbCode = colours[c];

            for (; index <= endIndex; index += 320) {
                bgLineData[bgIndex++] = index;
                bgLineData[bgIndex++] = screen[index];
                screen[index] = rgbCode;
                screen[index + 1] = rgbCode;
            }
        }
        // Horizontal Line.
        else if (y1 == y2) {
            if (x1 > x2) {
                x = x1;
                x1 = x2;
                x2 = x;
            }

            index = (y1 << 8) + (y1 << 6) + (x1 << 1);
            endIndex = (y2 << 8) + (y2 << 6) + (x2 << 1);
            rgbCode = colours[c];

            for (; index <= endIndex; index += 2) {
                bgLineData[bgIndex++] = index;
                bgLineData[bgIndex++] = screen[index];
                screen[index] = rgbCode;
                screen[index + 1] = rgbCode;
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
            index = (y1 << 8) + (y1 << 6) + (x1 << 1);
            rgbCode = colours[c];

            bgLineData[bgIndex++] = index;
            bgLineData[bgIndex++] = screen[index];
            screen[index] = rgbCode;
            screen[index + 1] = rgbCode;

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

                index = (y << 8) + (y << 6) + (x << 1);
                bgLineData[bgIndex++] = index;
                bgLineData[bgIndex++] = screen[index];
                screen[index] = rgbCode;
                screen[index + 1] = rgbCode;
                count--;
            } while (count > 0);
        }

        // Store the length of the stored pixel data in first slot.
        bgLineData[0] = bgIndex;
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
