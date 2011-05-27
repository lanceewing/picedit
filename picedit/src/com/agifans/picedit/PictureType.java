package com.agifans.picedit;

/**
 * Enum representing the different sierra picture types supported.
 * 
 * @author Lance Ewing
 */
public enum PictureType {

	AGI(320, 168),
	SCI0(320, 200);

	/**
	 * The width of the picture in pixels.
	 */
	private int width;

	/**
	 * The height of the picture in pixels.
	 */
	private int height;
	
	/**
	 * Constructor for PictureType.
	 * 
	 * @param width The width of the picture in pixels.
	 * @param height The height of the picture in pixels.
	 */
	PictureType(int width, int height) {
	  this.width = width;
	  this.height = height;
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
}
