package com.agifans.picedit;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.agifans.picedit.view.EgoTestHandler;

/**
 * The graphics routines with which the application draws the screen. There
 * are four different images held in this object. The first is the main 
 * PICEDIT screen. The second is the text image shown for the "Help" screen
 * and the "View data" screen. The third image is the background image, which
 * is drawn behind the main PICEDIT screen. The fourth image is the priority
 * bands image that is drawn when bands is enabled. The graphics routines in
 * this class act upon the main PICEDIT screen image.
 * 
 * @author Lance Ewing
 */
public final class PicGraphics {

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
     * The parent component, i.e. the PICEDIT JFrame.
     */
    private Component component;

    /**
     * Duration between frames (eg. 40 * 25 = 1000ms).
     */
    private long frameDuration;

    /**
     * The next time that the frame should be drawn.
     */
    public long nextTime = 0;

    /**
     * Holds the RGB values for the 16 EGA colours.
     */
    private final static int[] colours = EgaPalette.colours;

    /**
     * Holds mapping between RBG colour values and their EGA palette index
     * value.
     */
    private Map<Integer, Integer> colourMap;

    /**
     * Cursor to show when moving over the picture.
     */
    private Cursor crossHairCursor;

    /**
     * Cursor to show when moving over status bar, menu and button panel.
     */
    private Cursor defaultCursor;

    /**
     * Cursor to show when hiding the mouse cursor (i.e. a blank cursor).
     */
    private Cursor blankCursor;

    /**
     * The handler for managing the Ego Test mode.
     */
    private EgoTestHandler egoTestHandler;
    
    /**
     * Constructor for PicGraphics.
     * 
     * @param width The width of the underlying image.
     * @param heigth The height of the underlying image.
     * @param component the GUI component that will create the image.
     * @param framesPerSecond the maximum number of frames to display per second.
     */
    public PicGraphics(int width, int height, Component component, int framesPerSecond) {
        createScreenImage(width, height);
        buildColourMap();
        
        this.component = component;
        this.frameDuration = (1000 / framesPerSecond);
        this.nextTime = System.currentTimeMillis() + this.frameDuration;

        crossHairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        blankCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");

        createPriorityBandsImage(PictureType.AGI);
        
        this.egoTestHandler = new EgoTestHandler();
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
     * Changes the mouse cursor to be a cross hair cursor.
     */
    public void showCrossHairCursor() {
        component.setCursor(crossHairCursor);
    }

    /**
     * Changes the mouse cursor to be the default cursor.
     */
    public void showDefaultCursor() {
        component.setCursor(defaultCursor);
    }

    /**
     * Changes the mouse cursor to be a blank cursor (completely transparent).
     */
    public void showBlankCursor() {
        component.setCursor(blankCursor);
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
     * Builds the Map that holds the mapping between RGB colours values and
     * their EGA palette index value, e.g. red is 4, brown is 6. Used for
     * quickly converting between a pixel on the screen and the associated EGA
     * colour.
     */
    private void buildColourMap() {
        colourMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < colours.length; i++) {
            colourMap.put(colours[i], i);
        }
    }

    /**
     * Draws the screen data onto the Image.
     */
    public void drawFrame() {
        component.repaint();
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
     * Gets the colour of the pixel at the given location.
     * 
     * @param x the X position to get the pixel colour for.
     * @param y the Y position to get the pixel colour for.
     * 
     * @return the colour of the pixel.
     */
    public int getPixel(int x, int y) {
        return colourMap.get(screen[(y << 8) + (y << 6) + x]);
    }

    /**
     * Draws a line in the 320 x 200 PICEDIT coordinate system. Used primarily
     * for drawing the interface components (the AGI picture uses the version in
     * the Picture class).
     * 
     * @param x1 Start X Coordinate.
     * @param y1 Start Y Coordinate.
     * @param x2 End X Coordinate.
     * @param y2 End Y Coordinate.
     * @param c the colour of the line.
     */
    public final void drawLine(int x1, int y1, int x2, int y2, int c) {
        int x, y, index, endIndex, rgbCode;

        // Vertical Line.
        if (x1 == x2) {
            if (y1 > y2) {
                y = y1;
                y1 = y2;
                y2 = y;
            }

            index = (y1 << 8) + (y1 << 6) + x1;
            endIndex = (y2 << 8) + (y2 << 6) + x2;
            rgbCode = colours[c];

            for (; index <= endIndex; index += 320) {
                screen[index] = rgbCode;
            }
        }
        // Horizontal Line.
        else if (y1 == y2) {
            if (x1 > x2) {
                x = x1;
                x1 = x2;
                x2 = x;
            }

            index = (y1 << 8) + (y1 << 6) + x1;
            endIndex = (y2 << 8) + (y2 << 6) + x2;
            rgbCode = colours[c];

            for (; index <= endIndex; index++) {
                screen[index] = rgbCode;
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
            index = (y1 << 8) + (y1 << 6) + x1;
            rgbCode = colours[c];

            screen[index] = rgbCode;

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

                index = (y << 8) + (y << 6) + x;
                screen[index] = rgbCode;
                count--;
            } while (count > 0);

            index = (y << 8) + (y << 6) + x;
            screen[index] = rgbCode;
        }
    }

    /**
     * Draws an (unfilled) box.
     * 
     * @param left the left side of the box.
     * @param top the top of the box.
     * @param right the right side of the box.
     * @param bottom the bottom of the box.
     * @param c the colour of the box.
     */
    public void drawBox(int left, int top, int right, int bottom, int c) {
        drawLine(left, top, right, top, c);
        drawLine(right, top, right, bottom, c);
        drawLine(left, top, left, bottom, c);
        drawLine(left, bottom, right, bottom, c);
    }

    /**
     * Draws a filled box on the screen.
     * 
     * @param left the left side of the filled box.
     * @param top the top of the filled box.
     * @param right the right side of the filled box.
     * @param bottom the bottom of the filled box.
     * @param c the colour of the filled box.
     */
    public void drawFilledBox(int left, int top, int right, int bottom, int c) {
        int colorRGB = colours[c];

        for (int y = top; y <= bottom; y++) {
            int pos = (y << 8) + (y << 6) + left;
            for (int x = left; x <= right; x++) {
                screen[pos++] = colorRGB;
            }
        }
    }

    /**
     * Draws the given character at the given x/y position, using the given
     * foreground and background colours.
     * 
     * @param aChar the character to draw.
     * @param x the X position of the character.
     * @param y the Y position of the character.
     * @param foreColour the foreground colour.
     * @param backColour the background colour.
     */
    public void drawChar(char aChar, int x, int y, int foreColour, int backColour) {
        int[] charData = Font8x8.CHAR_DATA[aChar];
        int foreColourRGB = colours[foreColour];
        int backColourRGB = colours[backColour];
        int index = (y << 8) + (y << 6) + x;

        for (int line = 0; line < 8; line++) {
            int lineData = charData[line];
            screen[index++] = ((lineData & 0x80) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x40) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x20) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x10) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x08) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x04) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x02) > 0 ? foreColourRGB : backColourRGB);
            screen[index++] = ((lineData & 0x01) > 0 ? foreColourRGB : backColourRGB);
            index += 312;
        }
    }

    /**
     * Draws a String of text at the given x/y position, using the given
     * foreground and background colours.
     * 
     * @param text the text String to draw.
     * @param x the X position to draw the String at.
     * @param y the Y position to draw the String at.
     * @param foreColour the foreground colour of the text.
     * @param backColour the background colour of the text.
     */
    public void drawString(String text, int x, int y, int foreColour, int backColour) {
        char[] characters = text.toCharArray();
        int textLength = characters.length;
        int foreColourRGB = colours[foreColour];
        int backColourRGB = colours[backColour];
        int index = (y << 8) + (y << 6) + x;

        for (int charPos = 0; charPos < textLength; charPos++, x += 8) {
            int[] charData = Font8x8.CHAR_DATA[characters[charPos]];

            for (int line = 0; line < 8; line++) {
                int lineData = charData[line];
                screen[index++] = ((lineData & 0x80) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x40) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x20) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x10) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x08) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x04) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x02) > 0 ? foreColourRGB : backColourRGB);
                screen[index++] = ((lineData & 0x01) > 0 ? foreColourRGB : backColourRGB);
                index += 312;
            }

            index -= 2552;
        }
    }

    /**
     * Draws a message box, i.e. a white box with a black border indented
     * slightly, as per the original Sierra games.
     * 
     * @param left the left side of the message box.
     * @param top the top of the message box.
     * @param right the right side of the message box.
     * @param bottom the bottom of the message box.
     */
    public void drawMessageBox(int left, int top, int right, int bottom) {
        drawFilledBox(left, top, right, bottom, 15);
        drawBox(left + 2, top + 1, right - 2, bottom - 1, 4);
        drawBox(left + 3, top + 1, right - 3, bottom - 1, 4);
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
}
