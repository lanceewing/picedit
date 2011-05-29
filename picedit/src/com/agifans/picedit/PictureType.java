package com.agifans.picedit;

/**
 * Enum representing the different sierra picture types supported.
 * 
 * @author Lance Ewing
 */
public enum PictureType {

    AGI(160, 168), 
    SCI0(320, 190);

    /**
     * The width of the picture in pixels.
     */
    private int width;

    /**
     * The height of the picture in pixels.
     */
    private int height;

    /**
     * The number of the pixels in this picture type.
     */
    private int numberOfPixels;

    /**
     * Constructor for PictureType.
     * 
     * @param width The width of the picture in pixels.
     * @param height The height of the picture in pixels.
     */
    PictureType(int width, int height) {
        this.width = width;
        this.height = height;
        this.numberOfPixels = (this.width * this.height);
    }

    /**
     * Gets the width of the picture in pixels.
     * 
     * @return the width of the picture in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the picture in pixels.
     * 
     * @return the height of the picture in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the number of pixels in this type of picture.
     * 
     * @return the number of pixels in this type of picture.
     */
    public int getNumberOfPixels() {
        return numberOfPixels;
    }
}
