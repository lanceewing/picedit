package com.agifans.picedit.view;

import java.awt.*;
import java.awt.image.*;

import com.agifans.picedit.utils.EgaPalette;

/**
 * A single Cell within a Loop of an AGI View resource.
 * 
 * @author Lance Ewing
 */
public class Cell {
    
    /** 
     * The width of the Cell. 
     */
    protected int width;

    /**
     * The height of the Cell.
     */
    protected int height;

    /** 
     * The RGB pixel data for the Cell.
     */
    protected int[] rgbPixelData;

    /** 
     * The RGB colour that is the transparent colour for this Cell.
     */
    protected int transparentColour;

    /**
     * Constructor for Cell.
     * 
     * @param viewData The byte array containing the full data for the VIEW resource that this Loop belongs to.
     * @param start The offset of where this Cell starts within the VIEW.
     * @param loopNumber The number of the Loop that this Cell belongs to.
     */
    public Cell(byte[] viewData, int start, int loopNumber) {
        // Get the width and height of this Cell.
        this.width = (viewData[start] & 0xFF);
        this.height = (viewData[start+1] & 0xFF);
        
        // Get the transparent colour and mirror info.
        int transMirror = (viewData[start+2] & 0xFF);
        int transColorIndex = (transMirror & 0x0F);
        int mirrorInfo = ((transMirror & 0xF0) >> 4);

        // Load the RGB pixel data for this Cell.
        loadRGBPixelData(viewData, start + 3, transColorIndex);

        // If the pixel data is to be mirrored then do this here.
        if ((mirrorInfo & 0x8) != 0) {
            if ((mirrorInfo & 0x7) != loopNumber) {
                mirror();
            }
        }
    }

    /**
     * Loads the RGB pixel data from the given raw VIEW resource byte array.
     * 
     * @param viewData The byte array containing the full data for the VIEW resource that this Loop belongs to.
     * @param offset The offset into the viewData array where the RGB pixel data begins.
     * @param transColorIndex The EGA colour index of the transparent colour.
     */
    protected void loadRGBPixelData(byte[] viewData, int offset, int transColorIndex) {
        int i, pixelIndex, x, y;
        
        // Create the array to hold the RGB pixel data.
        this.rgbPixelData = new int[width * height];
        
        // Store the RGB value for the transparent colour.
        this.transparentColour = EgaPalette.colours[transColorIndex];

        // Decode the cell data and convert to an RGB pixel array.
        for (pixelIndex = 0, y = 0; y < height; y++) {
            // Process the data for this line of the Cell. A count value of 0 ends the line.
            for (x = 0; viewData[offset] != 0; offset++) {
                // Work out the colour index and pixel count.
                int color = (viewData[offset] & 0xF0) >> 4;
                int count = (viewData[offset] & 0x0F);

                // Add pixels of the specified colour for the specified count.
                for (i = 0; i < count; i++, pixelIndex++, x++) {
                    rgbPixelData[pixelIndex] = EgaPalette.colours[color];
                }
            }

            // Pad the rest of the line with the transparent colour.
            for (; x < width; pixelIndex++, x++) {
                rgbPixelData[pixelIndex] = this.transparentColour;
            }

            offset++;
        }
    }

    /**
     * Performs a mirror of the RGB pixel data. What this actually does is a flip
     * of the pixel data horizontally across the vertical axis. 
     */
    protected void mirror() {
        int i1, i2, x1, x2, y;
        int b;

        for (y = 0; y < height; y++) {
            for (x1 = width - 1, x2 = 0; x1 > x2; x1--, x2++) {
                i1 = (y * width) + x1;
                i2 = (y * width) + x2;

                b = rgbPixelData[i1];
                rgbPixelData[i1] = rgbPixelData[i2];
                rgbPixelData[i2] = b;
            }
        }
    }

    /**
     * Gets the width of this Cell.
     * 
     * @return The width of this Cell.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of this Cell.
     * 
     * @return The height of this Cell.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the RGB pixel data for this Cell.
     * 
     * @return The RGB pixel data for his Cell.
     */
    public int[] getRGBPixelData() {
        return rgbPixelData;
    }

    /**
     * Gets the transparent colour for this Cell (RGB).
     * 
     * @return The transparent colour for this Cell (RGB).
     */
    public int getTransparentColour() {
        return transparentColour;
    }

    /**
     * Converts the RGB pixel data in to an Image.
     * 
     * @return The created Image.
     */
    public Image convertToImage() {
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, ColorModel.getRGBdefault(), this.rgbPixelData, 0, width));
    }
}