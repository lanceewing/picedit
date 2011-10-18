package com.agifans.picedit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.agifans.picedit.PictureCache.PictureCacheEntry;

/**
 * This class represents an AGI/SCI Picture.
 * 
 * @author Lance Ewing
 */
public class Picture {

    /**
     * Holds the RGB values for the 16 EGA colours.
     */
    private final static int[] colours = EgaPalette.colours;

    /**
     * Holds the current position within the picture code buffer.
     */
    private int picturePosition;
    
    /**
     * Holds the linked list of picture codes for this picture.
     */
    private LinkedList<PictureCode> pictureCodes;
    
    /**
     * Holds the pixel data for the visual screen of the picture.
     */
    private int visualScreen[];

    /**
     * Holds the pixel data for the priority screen of the picture.
     */
    private int priorityScreen[];

    /**
     * Holds the pixel data for the control screen of the picture.
     */
    private int controlScreen[];

    /**
     * The Image for the visual screen.
     */
    private Image visualImage;

    /**
     * The Image for the priority screen.
     */
    private Image priorityImage;

    /**
     * The Image for the control screen.
     */
    private Image controlImage;

    /**
     * Holds the editing status of the PICEDIT application.
     */
    private EditStatus editStatus;

    /**
     * Holds the main graphics routines for drawing to the PICEDIT screen. 
     */
    private PicGraphics picGraphics;

    /**
     * Holds the cache of picture state at various picture positions. Used for faster picture draws.
     */
    private PictureCache pictureCache;
    
    /**
     * Constructor for Picture.
     * 
     * @param editStatus the EditStatus containing current editing status.
     * @param picGraphics the PicGraphics for drawing on the actual PICEDIT screen.
     */
    public Picture(EditStatus editStatus, PicGraphics picGraphics) {
        PictureType pictureType = editStatus.getPictureType();

        this.visualScreen = new int[pictureType.getNumberOfPixels()];
        this.visualImage = createScreenImage(pictureType.getWidth(), pictureType.getHeight(), visualScreen);
        this.priorityScreen = new int[pictureType.getNumberOfPixels()];
        this.priorityImage = createScreenImage(pictureType.getWidth(), pictureType.getHeight(), priorityScreen);

        if (pictureType.equals(PictureType.SCI0)) {
            this.controlScreen = new int[pictureType.getNumberOfPixels()];
            this.controlImage = createScreenImage(pictureType.getWidth(), pictureType.getHeight(), controlScreen);
        }

        this.editStatus = editStatus;
        this.picGraphics = picGraphics;
        this.pictureCache = new PictureCache(editStatus);
        
        clearPicture();
        updateScreen();
    }

    /**
     * Creates a BufferedImage of the given size using the given data array to hold
     * the pixel data.
     * 
     * @param width the width of the Image to create.
     * @param height the height of the Image to create.
     * @param screenDataArray the int array that will hold the pixel data for this Image.
     * 
     * @return the created Image.
     */
    public Image createScreenImage(int width, int height, int[] screenDataArray) {
        DataBufferInt dataBuffer = new DataBufferInt(screenDataArray, screenDataArray.length);
        ColorModel colorModel = ColorModel.getRGBdefault();
        int[] bandMasks = new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000 };
        WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, width, bandMasks, null);
        return new BufferedImage(colorModel, raster, false, null);
    }

    /**
     * Clears the picture code buffer, picture cache and picture screens. This
     * would usually be done when it is a completely new picture, e.g. when 
     * loading a picture or creating a new picture.
     */
    public void clearPicture() {
        clearPictureCodes();
        clearPictureScreens();
        clearPictureCache();
    }
    
    /**
     * Clears the visual, priority (and control) screens.
     */
    public void clearPictureScreens() {
        Arrays.fill(visualScreen, EgaPalette.transparent);
        Arrays.fill(priorityScreen, EgaPalette.transparent);
        if (editStatus.getPictureType().equals(PictureType.SCI0)) {
            Arrays.fill(controlScreen, EgaPalette.transparent);
        }
    }
    
    /**
     * Clears the picture cache.
     */
    public void clearPictureCache() {
        this.pictureCache.clear();
    }
    
    /**
     * Clears the picture code buffer.
     */
    public void clearPictureCodes() {
        picturePosition = 0;
        pictureCodes = new LinkedList<PictureCode>();
        pictureCodes.add(new PictureCode(0xFF));
    }
    
    /**
     * Gets the current picture position.
     * 
     * @return The current picture position.
     */
    public int getPicturePosition() {
        return picturePosition;
    }

    /**
     * Sets the picture position to the given value.
     * 
     * @param picturePosition The new picture position.
     */
    public void setPicturePosition(int picturePosition) {
        this.picturePosition = picturePosition;
    }
    
    /**
     * Adds a code to the picture code buffer.
     * 
     * @param code The code to add to the picture code buffer.
     */
    public void addPictureCode(int code) {
        pictureCache.clear(picturePosition);
        pictureCodes.add(picturePosition, new PictureCode(code));
        picturePosition = picturePosition + 1;
        editStatus.setUnsavedChanges(true);
    }

    /**
     * Processes the setting of a new visual colour.
     * 
     * @param newVisualColour The new visual colour.
     */
    public void processVisualColourChange(int newVisualColour) {
        editStatus.setVisualColour(newVisualColour);
        this.addPictureCode(0xF0);
        this.addPictureCode(newVisualColour);
        this.updateScreen();
    }
    
    /**
     * Processes the turning off of the visual colour.
     */
    public void processVisualColourOff() {
        editStatus.setVisualColour(EditStatus.VISUAL_OFF);
        this.addPictureCode(0xF1);
        this.updateScreen();
    }
    
    /**
     * Processes the setting of a new priority colour.
     * 
     * @param newPriorityColour The new priority colour.
     */
    public void processPriorityColourChange(int newPriorityColour) {
        editStatus.setPriorityColour(newPriorityColour);
        this.addPictureCode(0xF2);
        this.addPictureCode(newPriorityColour);
        this.updateScreen();
    }
    
    /**
     * Processes the turning off of the priority colour.
     */
    public void processPriorityColourOff() {
        editStatus.setPriorityColour(EditStatus.PRIORITY_OFF);
        this.addPictureCode(0xF3);
        this.updateScreen();
    }
    
    /**
     * Gets the picture code buffer.
     * 
     * @return The picture code buffer.
     */
    public LinkedList<PictureCode> getPictureCodes() {
        return pictureCodes;
    }
    
    public PictureCode getCurrentPictureAction() {
      if (pictureCodes.size() == 1) {
          return null;
      }
      int position = picturePosition;
      while ((position > 0) && !pictureCodes.get(position).isActionCode()) {
          position--;
      }
      return pictureCodes.get(position);
    }

    public PictureCode getNextPictureAction() {
        PictureCode pictureCode = null;
        if (picturePosition < pictureCodes.size() - 1) {
            int position = picturePosition + 1;
            while ((position < pictureCodes.size()) && !pictureCodes.get(position).isActionCode()) {
                position++;
            }
            if (position < pictureCodes.size()) {
                pictureCode = pictureCodes.get(position);
            }
        }
        return pictureCode;
    }
    
    public PictureCode incrementPicturePosition() {
        picturePosition++;
        if (picturePosition >= pictureCodes.size()) {
            picturePosition = pictureCodes.size() - 1;
            return null;
        } else {
            return pictureCodes.get(picturePosition);
        }
    }

    public PictureCode decrementPicturePosition() {
        picturePosition--;
        if (picturePosition < 0) {
            picturePosition = 0;
        }
        return pictureCodes.get(picturePosition);
    }

    public PictureCode deleteAtPicturePosition() {
        PictureCode pictureCode = null;
        if (picturePosition < (pictureCodes.size() - 1)) {
            pictureCodes.remove(picturePosition);
            if (picturePosition < (pictureCodes.size() - 1)) {
                pictureCode = pictureCodes.get(picturePosition);
            }
            editStatus.setUnsavedChanges(true);
        }
        return pictureCode;
    }
    
    /**
     * Process movement back one picture action through the picture code buffer.
     */
    public void moveBackOnePictureAction() {
        // Move back through the codes until we find an Action code.
        PictureCode pictureCode = null;
        do {
            pictureCode = decrementPicturePosition();
        } while ((pictureCode != null) && !pictureCode.isActionCode() && (picturePosition > 0));

        drawPicture();
        updateScreen();
    }

    /**
     * Process movement forward one picture action through the picture code buffer.
     */
    public void moveForwardOnePictureAction() {
        if (picturePosition < (pictureCodes.size() - 1)) {
            PictureCode pictureCode = null;
            do {
                pictureCode = incrementPicturePosition();
            } while ((pictureCode != null) && !pictureCode.isActionCode());

            drawPicture();
            updateScreen();
        }
    }

    /**
     * Process movement to the start of the picture code buffer.
     */
    public void moveToStartOfPictureBuffer() {
        setPicturePosition(0);
        drawPicture();
        updateScreen();
    }

    /**
     * Process movement to the end of the picture code buffer.
     */
    public void moveToEndOfPictureBuffer() {
        if (picturePosition < (pictureCodes.size() - 1)) {
            setPicturePosition(pictureCodes.size() - 1);
            drawPicture();
            updateScreen();
        }
    }

    /**
     * Process deletion of the current picture action, i.e. the picture
     * action at the current picture position.
     */
    public void deleteCurrentPictureAction() {
        PictureCode pictureCode = deleteAtPicturePosition();
        while ((pictureCode != null) && (pictureCode.isDataCode())) {
            pictureCode = deleteAtPicturePosition();
        }
        pictureCache.clear(picturePosition);
        drawPicture();
        updateScreen();
    }
    
    /**
     * Updates the PICEDIT screen with the current contents of the 
     * picture's internal visual or priority buffer.
     */
    public void updateScreen() {
        // TODO: Replace all Picture updateScreen calls with a call to clearDrawingArea.
        picGraphics.clearDrawingArea(editStatus.getPictureType());
    }

    /**
     * Returns the visual image to be drawn on the screen.
     * 
     * @return the visual image to be drawn on the screen.
     */
    public Image getVisualImage() {
        return visualImage;
    }

    /**
     * Returns the priority image to be drawn on the screen.
     * 
     * @return the priority image to be drawn on the screen.
     */
    public Image getPriorityImage() {
        return priorityImage;
    }

    /**
     * Returns the control image to be drawn on the screen.
     * 
     * @return the control image to be drawn on the screen.
     */
    public Image getControlImage() {
        return controlImage;
    }

    /**
     * Draws the picture from the beginning up to the current picture position.
     */
    public void drawPicture() {
    	int action = 0;
        int index = 0;

        PictureCacheEntry cacheEntry = this.pictureCache.getCacheEntry(picturePosition);
        if (cacheEntry != null) {
        	// Copy the cached screen arrays into the main picture images.
        	System.arraycopy(cacheEntry.getVisualScreen(), 0, visualScreen, 0, visualScreen.length);
        	System.arraycopy(cacheEntry.getPriorityScreen(), 0, priorityScreen, 0, priorityScreen.length);
        	if (editStatus.getPictureType().equals(PictureType.SCI0)) {
    	    	System.arraycopy(cacheEntry.getControlScreen(), 0, controlScreen, 0, controlScreen.length);
        	}
        	
        	// Skip straight to the cached position.
        	index = cacheEntry.getPicturePosition();
        	
        	// Restore the settings from the EditStatus that apply to the cached position.
        	editStatus.setBrushShape(cacheEntry.getBrushShape());
        	editStatus.setBrushSize(cacheEntry.getBrushSize());
        	editStatus.setBrushTexture(cacheEntry.getBrushTexture());
        	editStatus.setControlColour(cacheEntry.getControlColour());
        	editStatus.setPriorityColour(cacheEntry.getPriorityColour());
        	editStatus.setTool(cacheEntry.getTool());
        	editStatus.setVisualColour(cacheEntry.getVisualColour());
        	
        } else {
	        // Clear the picture bitmaps to the original colours.
	        clearPictureScreens();
	       
	        // When drawing from the start, we need to clear everything except for the data.
	        editStatus.clear(false);
        }

        if ((picturePosition > 0) && (index < picturePosition)) {
            do {
                boolean isCacheable = true;
                
                // Get the next picture action.
                action = pictureCodes.get(index++).getCode();

                // Process the actions data.
                switch (action) {
                    case 0xF0:
                        editStatus.setVisualColour(pictureCodes.get(index++).getCode());
                        isCacheable = false;
                        break;
                    case 0xF1:
                        editStatus.setVisualColour(EditStatus.VISUAL_OFF);
                        isCacheable = false;
                        break;
                    case 0xF2:
                        editStatus.setPriorityColour(pictureCodes.get(index++).getCode());
                        isCacheable = false;
                        break;
                    case 0xF3:
                        editStatus.setPriorityColour(EditStatus.PRIORITY_OFF);
                        isCacheable = false;
                        break;
                    case 0xF4:
                        editStatus.setTool(ToolType.STEPLINE);
                        index = drawPictureYCorner(pictureCodes, index);
                        break;
                    case 0xF5:
                        editStatus.setTool(ToolType.STEPLINE);
                        index = drawPictureXCorner(pictureCodes, index);
                        break;
                    case 0xF6:
                        editStatus.setTool(ToolType.LINE);
                        index = drawPictureAbsoluteLine(pictureCodes, index);
                        break;
                    case 0xF7:
                        editStatus.setTool(ToolType.SHORTLINE);
                        index = drawPictureRelativeDraw(pictureCodes, index);
                        break;
                    case 0xF8:
                        editStatus.setTool(ToolType.FILL);
                        index = drawPictureFill(pictureCodes, index);
                        break;
                    case 0xF9:
                        editStatus.setBrushCode(pictureCodes.get(index++).getCode());
                        isCacheable = false;
                        break;
                    case 0xFA:
                        editStatus.setTool(ToolType.BRUSH);
                        index = drawPicturePlotBrush(pictureCodes, index);
                        break;
                    case 0xFF:
                        // End of the picture.
                        break;
                    default:
                        // An attempt to load a picture that is corrupt.
                        System.out.printf("Unknown picture code : %X\n", action);
                        System.exit(0);
                        break;
                }
                
                // Add the current picture state to the picture cache.
                if (isCacheable) {
                    if ((cacheEntry == null) || ((index - cacheEntry.getPicturePosition()) > 100)) {
                        cacheEntry = pictureCache.addCacheEntry(index, visualScreen, priorityScreen, controlScreen);
                    }
                }
            } while ((index < picturePosition) && (action != 0xFF));

            updateScreen();
        }
    }

    /**
     * Draws a yCorner (drawing action 0xF4).
     * 
     * @param picturesCodes the List of picture codes to draw Y corners from.
     * @param index the index within the List to start processing from.
     * 
     * @return the index of the next picture action.
     */
    public int drawPictureYCorner(List<PictureCode> pictureCodes, int index) {
        int x1, x2, y1, y2;

        x1 = pictureCodes.get(index++).getCode();
        y1 = pictureCodes.get(index++).getCode();

        // A line must always have a least one point.
        putPixel(x1, y1);
        
        while (true) {
            y2 = pictureCodes.get(index++).getCode();
            if (y2 >= 0xF0) {
                break;
            }
            drawLine(x1, y1, x1, y2);
            y1 = y2;
            x2 = pictureCodes.get(index++).getCode();
            if (x2 >= 0xF0) {
                break;
            }
            drawLine(x1, y1, x2, y1);
            x1 = x2;
        }

        return (index - 1);
    }

    /**
     * Draws an xCorner (drawing action 0xF5).
     * 
     * @param picturesCodes the List of picture codes to draw X corners from.
     * @param index the index within the List to start processing from.
     * 
     * @return the index of the next picture action.
     */
    public int drawPictureXCorner(List<PictureCode> pictureCodes, int index) {
        int x1, x2, y1, y2;

        x1 = pictureCodes.get(index++).getCode();
        y1 = pictureCodes.get(index++).getCode();

        // A line must always have a least one point.
        putPixel(x1, y1);
        
        while (true) {
            x2 = pictureCodes.get(index++).getCode();
            if (x2 >= 0xF0) {
                break;
            }
            drawLine(x1, y1, x2, y1);
            x1 = x2;
            y2 = pictureCodes.get(index++).getCode();
            if (y2 >= 0xF0) {
                break;
            }
            drawLine(x1, y1, x1, y2);
            y1 = y2;
        }

        return (index - 1);
    }

    /**
     * Draws long lines to actual locations (cf. relative) (drawing action 0xF6).
     * 
     * @param picturesCodes the List of picture codes to draw absolute lines from.
     * @param index the index within the List to start processing from. 
     * 
     * @return the index of the next picture action.
     */
    public int drawPictureAbsoluteLine(List<PictureCode> pictureCodes, int index) {
        int x1, y1, x2, y2, lineCount=0;

        x1 = pictureCodes.get(index++).getCode();
        y1 = pictureCodes.get(index++).getCode();

        // A line must always have a least one point.
        putPixel(x1, y1);
        
        while (true) {
            if ((x2 = pictureCodes.get(index++).getCode()) >= 0xF0) {
                break;
            }
            if ((y2 = pictureCodes.get(index++).getCode()) >= 0xF0) {
                break;
            }
            drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
            lineCount++;
        }

        return (index - 1);
    }

    /**
     * Draws short lines relative to last position.  (drawing action 0xF7).
     * 
     * @param picturesCodes the List of picture codes to draw relative lines from.
     * @param index the index within the List to start processing from. 
     * 
     * @return the index of the next picture action.
     */
    public int drawPictureRelativeDraw(List<PictureCode> pictureCodes, int index) {
        int x1, y1, disp;
        int dx, dy;

        x1 = pictureCodes.get(index++).getCode();
        y1 = pictureCodes.get(index++).getCode();

        // A line must always have a least one point.
        putPixel(x1, y1);
        
        while (true) {
            disp = pictureCodes.get(index++).getCode();
            if (disp >= 0xF0) {
                break;
            }
            dx = ((disp & 0xF0) >> 4) & 0x0F;
            dy = (disp & 0x0F);
            if ((dx & 0x08) > 0) {
                dx = (-1) * (dx & 0x07);
            }
            if ((dy & 0x08) > 0) {
                dy = (-1) * (dy & 0x07);
            }
            drawLine(x1, y1, x1 + dx, y1 + dy);
            x1 += dx;
            y1 += dy;
        }

        return (index - 1);
    }

    /**
     * AGI flood fill. (drawing action 0xF8).
     * 
     * @param picturesCodes the List of picture codes to draw fills from.
     * @param index the index within the List to start processing from. 
     * 
     * @return the index of the next picture action.
     */
    public int drawPictureFill(List<PictureCode> pictureCodes, int index) {
        int x1, y1;

        while (true) {
            if ((x1 = pictureCodes.get(index++).getCode()) >= 0xF0) {
                break;
            }
            if ((y1 = pictureCodes.get(index++).getCode()) >= 0xF0) {
                break;
            }
            fill(x1, y1);
        }

        return (index - 1);
    }

    /**
     * Plots points and various brush patterns. (drawing action 0xF8).
     *
     * @param picturesCodes the List of picture codes to plot brushes from.
     * @param index the index within the List to start processing from. 
     * 
     * @return the index of the next picture action.
     */
    public int drawPicturePlotBrush(List<PictureCode> pictureCodes, int index) {
        int x1, y1, patNum = 0;

        int patCode = editStatus.getBrushCode();

        while (true) {
            if ((patCode & 0x20) > 0) {
                if ((patNum = pictureCodes.get(index++).getCode()) >= 0xF0) {
                    break;
                }
                patNum = (patNum >> 1 & 0x7f);
            }
            if ((x1 = pictureCodes.get(index++).getCode()) >= 0xF0) {
                break;
            }
            if ((y1 = pictureCodes.get(index++).getCode()) >= 0xF0) {
                break;
            }
            plotPattern(patNum, x1, y1);
        }

        return (index - 1);
    }

    /**
     * Draws a single pixel on the AGI picture.
     * 
     * @param x The X position of the pixel.
     * @param y The Y position of the pixel.
     */
    public void putPixel(int x, int y) {
        int index = (y << 7) + (y << 5) + x;
        
        if (editStatus.isVisualDrawEnabled()) {
            visualScreen[index] = colours[editStatus.getVisualColour()];
        }
        if (editStatus.isPriorityDrawEnabled()) {
            priorityScreen[index] = colours[editStatus.getPriorityColour()];
        }
    }
    
    /**
     * Draw a line the most efficient way we can. Speed is preferred over
     * removal of duplicated code.
     * 
     * @param x1 Start X Coordinate.
     * @param y1 Start Y Coordinate.
     * @param x2 End X Coordinate.
     * @param y2 End Y Coordinate.
     */
    public final void drawLine(int x1, int y1, int x2, int y2) {
        int x, y, index, endIndex, visualRGBCode, priorityRGBCode;

        // Vertical Line.
        if (x1 == x2) {
            if (y1 > y2) {
                y = y1;
                y1 = y2;
                y2 = y;
            }

            index = (y1 << 7) + (y1 << 5) + x1;
            endIndex = (y2 << 7) + (y2 << 5) + x2;

            if (editStatus.isVisualDrawEnabled()) {
                if (editStatus.isPriorityDrawEnabled()) {
                    // Vertical line on both visual and priority screens.
                    visualRGBCode = colours[editStatus.getVisualColour()];
                    priorityRGBCode = colours[editStatus.getPriorityColour()];

                    for (; index <= endIndex; index += 160) {
                        visualScreen[index] = visualRGBCode;
                        priorityScreen[index] = priorityRGBCode;
                    }
                } else {
                    // Vertical line on only the visual screen.
                    visualRGBCode = colours[editStatus.getVisualColour()];

                    for (; index <= endIndex; index += 160) {
                        visualScreen[index] = visualRGBCode;
                    }
                }
            } else if (editStatus.isPriorityDrawEnabled()) {
                // Vertical line on only the priority screen.
                priorityRGBCode = colours[editStatus.getPriorityColour()];

                for (; index <= endIndex; index += 160) {
                    priorityScreen[index] = priorityRGBCode;
                }
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

            if (editStatus.isVisualDrawEnabled()) {
                if (editStatus.isPriorityDrawEnabled()) {
                    // Horizontal line on both visual and priority screens.
                    visualRGBCode = colours[editStatus.getVisualColour()];
                    priorityRGBCode = colours[editStatus.getPriorityColour()];

                    for (; index <= endIndex; index++) {
                        visualScreen[index] = visualRGBCode;
                        priorityScreen[index] = priorityRGBCode;
                    }
                } else {
                    // Horizontal line on only the visual screen.
                    visualRGBCode = colours[editStatus.getVisualColour()];

                    for (; index <= endIndex; index++) {
                        visualScreen[index] = visualRGBCode;
                    }
                }
            } else if (editStatus.isPriorityDrawEnabled()) {
                // Horizontal line on only the priority screen.
                priorityRGBCode = colours[editStatus.getPriorityColour()];

                for (; index <= endIndex; index++) {
                    priorityScreen[index] = priorityRGBCode;
                }
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

            if (editStatus.isVisualDrawEnabled()) {
                if (editStatus.isPriorityDrawEnabled()) {
                    // Both visual and priority screens.
                    visualRGBCode = colours[editStatus.getVisualColour()];
                    priorityRGBCode = colours[editStatus.getPriorityColour()];

                    index = (y << 7) + (y << 5) + x;
                    visualScreen[index] = visualRGBCode;
                    priorityScreen[index] = priorityRGBCode;

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
                        visualScreen[index] = visualRGBCode;
                        priorityScreen[index] = priorityRGBCode;
                        count--;
                    } while (count > 0);

                    index = (y << 7) + (y << 5) + x;
                    visualScreen[index] = visualRGBCode;
                    priorityScreen[index] = priorityRGBCode;

                } else {
                    // Only the visual screen.
                    visualRGBCode = colours[editStatus.getVisualColour()];

                    visualScreen[(y << 7) + (y << 5) + x] = visualRGBCode;

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

                        visualScreen[(y << 7) + (y << 5) + x] = visualRGBCode;
                        count--;
                    } while (count > 0);

                    visualScreen[(y << 7) + (y << 5) + x] = visualRGBCode;
                }
            } else if (editStatus.isPriorityDrawEnabled()) {
                // Only the priority screen.
                priorityRGBCode = colours[editStatus.getPriorityColour()];

                priorityScreen[(y << 7) + (y << 5) + x] = priorityRGBCode;

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

                    priorityScreen[(y << 7) + (y << 5) + x] = priorityRGBCode;
                    count--;
                } while (count > 0);

                priorityScreen[(y << 7) + (y << 5) + x] = priorityRGBCode;
            }
        }
    }

    /**
     * Performs a fill at the given position on the picture.
     * 
     * @param x the X position to fill at.
     * @param y the Y position to fill at.
     */
    public void fill(int x, int y) {
        // If the fill colour is white then return immediately.
        if (editStatus.getVisualColour() == EditStatus.TRANSPARENT) {
            return;
        }

        int fillQueue[] = new int[8000];
        int rpos = 0;
        int spos = 0;
        int index = (y << 7) + (y << 5) + x;
        int white = EgaPalette.transparent;
        int red = EgaPalette.transparent;

        if (editStatus.isVisualDrawEnabled()) {
            if (editStatus.isPriorityDrawEnabled()) {
                // Fill both visual and priority.
                int visualRGBCode = colours[editStatus.getVisualColour()];
                int priorityRGBCode = colours[editStatus.getPriorityColour()];

                fillQueue[spos++] = index;

                while (rpos != spos) {

                    index = fillQueue[rpos++];

                    if (visualScreen[index] == white) {
                        // Fill current position.
                        visualScreen[index] = visualRGBCode;
                        priorityScreen[index] = priorityRGBCode;

                        int lineStartIndex = (index / 160) * 160;
                        int lineEndIndex = lineStartIndex + 159;

                        // Go west.
                        int westIndex = index - 1;
                        while ((westIndex >= lineStartIndex) && (visualScreen[westIndex] == white)) {
                            westIndex--;
                        }

                        // Go east
                        int eastIndex = index + 1;
                        while ((eastIndex <= lineEndIndex) && (visualScreen[eastIndex] == white)) {
                            eastIndex++;
                        }

                        // Draw line.
                        westIndex++;
                        eastIndex--;
                        for (index = westIndex; index <= eastIndex; index++) {
                            visualScreen[index] = visualRGBCode;
                            priorityScreen[index] = priorityRGBCode;
                        }

                        int lastRGBColour = 0x80000000;

                        // Test above.
                        westIndex -= 160;
                        eastIndex -= 160;
                        if (westIndex > -1) {
                            for (index = westIndex; index <= eastIndex; index++) {
                                int rgbColour = visualScreen[index];
                                if ((rgbColour == white) && (lastRGBColour != white)) {
                                    fillQueue[spos++] = index;
                                }
                                lastRGBColour = rgbColour;
                            }
                        }

                        // Test below.
                        westIndex += 320;
                        eastIndex += 320;
                        lastRGBColour = 0x80000000;
                        if (eastIndex < 26880) {
                            for (index = westIndex; index <= eastIndex; index++) {
                                int rgbColour = visualScreen[index];
                                if ((rgbColour == white) && (lastRGBColour != white)) {
                                    fillQueue[spos++] = index;
                                }
                                lastRGBColour = rgbColour;
                            }
                        }
                    }
                }
            } else {
                // Visual only fill.
                int visualRGBCode = colours[editStatus.getVisualColour()];

                fillQueue[spos++] = index;

                while (rpos != spos) {

                    index = fillQueue[rpos++];

                    if (visualScreen[index] == white) {
                        // Fill current position.
                        visualScreen[index] = visualRGBCode;

                        int lineStartIndex = (index / 160) * 160;
                        int lineEndIndex = lineStartIndex + 159;

                        // Go west.
                        int westIndex = index - 1;
                        while ((westIndex >= lineStartIndex) && (visualScreen[westIndex] == white)) {
                            westIndex--;
                        }

                        // Go east
                        int eastIndex = index + 1;
                        while ((eastIndex <= lineEndIndex) && (visualScreen[eastIndex] == white)) {
                            eastIndex++;
                        }

                        // Draw line.
                        westIndex++;
                        eastIndex--;
                        for (index = westIndex; index <= eastIndex; index++) {
                            visualScreen[index] = visualRGBCode;
                        }

                        int lastRGBColour = 0x80000000;

                        // Test above.
                        westIndex -= 160;
                        eastIndex -= 160;
                        if (westIndex > -1) {
                            for (index = westIndex; index <= eastIndex; index++) {
                                int rgbColour = visualScreen[index];
                                if ((rgbColour == white) && (lastRGBColour != white)) {
                                    fillQueue[spos++] = index;
                                }
                                lastRGBColour = rgbColour;
                            }
                        }

                        // Test below.
                        westIndex += 320;
                        eastIndex += 320;
                        lastRGBColour = 0x80000000;
                        if (eastIndex < 26880) {
                            for (index = westIndex; index <= eastIndex; index++) {
                                int rgbColour = visualScreen[index];
                                if ((rgbColour == white) && (lastRGBColour != white)) {
                                    fillQueue[spos++] = index;
                                }
                                lastRGBColour = rgbColour;
                            }
                        }
                    }
                }
            }
        } else if (editStatus.isPriorityDrawEnabled()) {
            // Priority only fill.
            int priorityRGBCode = colours[editStatus.getPriorityColour()];

            fillQueue[spos++] = index;

            while (rpos != spos) {

                index = fillQueue[rpos++];

                if (priorityScreen[index] == red) {
                    // Fill current position.
                    priorityScreen[index] = priorityRGBCode;

                    int lineStartIndex = (index / 160) * 160;
                    int lineEndIndex = lineStartIndex + 159;

                    // Go west.
                    int westIndex = index - 1;
                    while ((westIndex >= lineStartIndex) && (priorityScreen[westIndex] == red)) {
                        westIndex--;
                    }

                    // Go east
                    int eastIndex = index + 1;
                    while ((eastIndex <= lineEndIndex) && (priorityScreen[eastIndex] == red)) {
                        eastIndex++;
                    }

                    // Draw line.
                    westIndex++;
                    eastIndex--;
                    for (index = westIndex; index <= eastIndex; index++) {
                        priorityScreen[index] = priorityRGBCode;
                    }

                    int lastRGBColour = 0x80000000;

                    // Test above.
                    westIndex -= 160;
                    eastIndex -= 160;
                    if (westIndex > -1) {
                        for (index = westIndex; index <= eastIndex; index++) {
                            int rgbColour = priorityScreen[index];
                            if ((rgbColour == red) && (lastRGBColour != red)) {
                                fillQueue[spos++] = index;
                            }
                            lastRGBColour = rgbColour;
                        }
                    }

                    // Test below.
                    westIndex += 320;
                    eastIndex += 320;
                    lastRGBColour = 0x80000000;
                    if (eastIndex < 26880) {
                        for (index = westIndex; index <= eastIndex; index++) {
                            int rgbColour = priorityScreen[index];
                            if ((rgbColour == red) && (lastRGBColour != red)) {
                                fillQueue[spos++] = index;
                            }
                            lastRGBColour = rgbColour;
                        }
                    }
                }
            }
        }
    }

    /** Circle Bitmaps */
    public static final short circles[][] = new short[][] { { 0x80 }, { 0xfc }, { 0x5f, 0xf4 }, { 0x66, 0xff, 0xf6, 0x60 }, { 0x23, 0xbf, 0xff, 0xff, 0xee, 0x20 }, { 0x31, 0xe7, 0x9e, 0xff, 0xff, 0xde, 0x79, 0xe3, 0x00 }, { 0x38, 0xf9, 0xf3, 0xef, 0xff, 0xff, 0xff, 0xfe, 0xf9, 0xf3, 0xe3, 0x80 }, { 0x18, 0x3c, 0x7e, 0x7e, 0x7e, 0xff, 0xff, 0xff, 0xff, 0xff, 0x7e, 0x7e, 0x7e, 0x3c, 0x18 } };

    /** Splatter Brush Bitmaps */
    public static final short splatterMap[] = new short[] { 0x20, 0x94, 0x02, 0x24, 0x90, 0x82, 0xa4, 0xa2, 0x82, 0x09, 0x0a, 0x22, 0x12, 0x10, 0x42, 0x14, 0x91, 0x4a, 0x91, 0x11, 0x08, 0x12, 0x25, 0x10, 0x22, 0xa8, 0x14, 0x24, 0x00, 0x50, 0x24, 0x04 };

    /** Starting Bit Position */
    public static final short splatterStart[] = new short[] { 0x00, 0x18, 0x30, 0xc4, 0xdc, 0x65, 0xeb, 0x48, 0x60, 0xbd, 0x89, 0x05, 0x0a, 0xf4, 0x7d, 0x7d, 0x85, 0xb0, 0x8e, 0x95, 0x1f, 0x22, 0x0d, 0xdf, 0x2a, 0x78, 0xd5, 0x73, 0x1c, 0xb4, 0x40, 0xa1, 0xb9, 0x3c, 0xca, 0x58, 0x92, 0x34, 0xcc, 0xce, 0xd7, 0x42, 0x90, 0x0f, 0x8b, 0x7f, 0x32, 0xed, 0x5c, 0x9d, 0xc8, 0x99, 0xad, 0x4e, 0x56, 0xa6, 0xf7, 0x68, 0xb7, 0x25, 0x82, 0x37, 0x3a, 0x51, 0x69, 0x26, 0x38, 0x52, 0x9e, 0x9a, 0x4f, 0xa7, 0x43, 0x10, 0x80, 0xee, 0x3d, 0x59, 0x35, 0xcf, 0x79, 0x74, 0xb5, 0xa2, 0xb1, 0x96, 0x23, 0xe0, 0xbe, 0x05, 0xf5, 0x6e, 0x19, 0xc5, 0x66, 0x49, 0xf0, 0xd1, 0x54, 0xa9, 0x70, 0x4b, 0xa4, 0xe2, 0xe6, 0xe5, 0xab, 0xe4, 0xd2, 0xaa, 0x4c, 0xe3, 0x06, 0x6f, 0xc6, 0x4a, 0xa4, 0x75, 0x97, 0xe1 };

    /**
     * Plots a brush pattern. Draws pixels, circles, squares, or splatter 
     * brush patterns depending on the pattern code.
     * 
     * @param patNum the pattern number to use.
     * @param x the X position to plot at. 
     * @param y the Y position to plot at.
     */
    public void plotPattern(int patNum, int x, int y) {
        int circlePos = 0;
        int x1, y1, penSize, bitPos = splatterStart[patNum];
        int visualRGBCode = (editStatus.isVisualDrawEnabled() ? colours[editStatus.getVisualColour()] : 0);
        int priorityRGBCode = (editStatus.isPriorityDrawEnabled() ? colours[editStatus.getPriorityColour()] : 0);
        int patCode = editStatus.getBrushCode();

        penSize = (patCode & 7);

        if (x < ((penSize / 2) + 1)) {
            x = ((penSize / 2) + 1);

        } else if (x > 160 - ((penSize / 2) + 1)) {
            x = 160 - ((penSize / 2) + 1);
        }

        if (y < penSize) {
            y = penSize;

        } else if (y >= 168 - penSize) {
            y = 167 - penSize;
        }

        for (y1 = y - penSize; y1 <= y + penSize; y1++) {
            for (x1 = x - ((int) Math.ceil((float) penSize / 2)); x1 <= x + ((int) Math.floor((float) penSize / 2)); x1++) {
                if ((patCode & 0x10) > 0) { /* Square */
                    if ((patCode & 0x20) > 0) {
                        if (((splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                            if (editStatus.isVisualDrawEnabled()) {
                                visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                            }
                            if (editStatus.isPriorityDrawEnabled()) {
                                priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                            }
                        }
                        bitPos++;
                        if (bitPos == 0xff) {
                            bitPos = 0;
                        }
                    } else {
                        if (editStatus.isVisualDrawEnabled()) {
                            visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                        }
                        if (editStatus.isPriorityDrawEnabled()) {
                            priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                        }
                    }
                } else { /* Circle */
                    if (((circles[patCode & 7][circlePos >> 3] >> (7 - (circlePos & 7))) & 1) > 0) {
                        if ((patCode & 0x20) > 0) {
                            if (((splatterMap[bitPos >> 3] >> (7 - (bitPos & 7))) & 1) > 0) {
                                if (editStatus.isVisualDrawEnabled()) {
                                    visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                                }
                                if (editStatus.isPriorityDrawEnabled()) {
                                    priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                                }
                            }
                            bitPos++;
                            if (bitPos == 0xff) {
                                bitPos = 0;
                            }
                        } else {
                            if (editStatus.isVisualDrawEnabled()) {
                                visualScreen[(y1 << 7) + (y1 << 5) + x1] = visualRGBCode;
                            }
                            if (editStatus.isPriorityDrawEnabled()) {
                                priorityScreen[(y1 << 7) + (y1 << 5) + x1] = priorityRGBCode;
                            }
                        }
                    }
                    circlePos++;
                }
            }
        }
    }
}
