package com.agifans.picedit;

import java.util.TreeMap;

/**
 * This class is a cache of data related to the Picture drawn up to 
 * each of the navigatable picture positions, i.e. for each position within
 * the picture that the user can navigate to there will be an entry in 
 * this cache that will allow the Picture class to quickly render the
 * picture as it would be drawn for that given position. The idea is that
 * the Picture class does not have to draw the picture from the start 
 * every time the user navigates through the picture code buffer if that
 * data already exists in this cache.
 * 
 * @author Lance Ewing
 */
public class PictureCache {

    /**
     * A map of picture position to 
     */
    private TreeMap<Integer, PictureCacheEntry> cache;

    /**
     * Holds the editing status of the PICEDIT application.
     */
    private EditStatus editStatus;
    
    /**
     * Constructor for PictureCache.
     * 
     * @param editStatus The editing status of the PICEDIT application.
     */
    public PictureCache(EditStatus editStatus) {
        this.cache = new TreeMap<Integer, PictureCacheEntry>();
        this.editStatus = editStatus;
    }

    // Some notes about when the cache will be used or updated.
    //
    //    1. Load picture: Obviously has to draw the new picture from the start.
    //    2. Delete current picture action: Can redraw from current position to end of picture.
    //    3. Enter position: Can use cached image for that position since no change involved.
    //    4. Back one action: Can use cached image.
    //    5. Move forward one action: Can use cached image.
    //    6. Move to end of picture: Can use cached image.
    //    7. Move to start of picture: Blank screen anyway.
    //    8. Escaping out of enter position: Cached image.
    //    9. New picture: Clear all cached images and draw blank screen.
    //    10. Slider position change: Use cached image.
    //    11. Drawing actions use Picture to draw that action only, on top of the current picture. Needs to cache every position from that point to the end? (or invalidate the cache from that point to the end)

    /**
     * An entry in the picture cache is of this type. It contains the screen
     * data as it is at the associated picture position and also a subset of
     * the EditStatus attributes that are relevant to that picture position.
     */
    public class PictureCacheEntry {

    	/**
    	 * The position that this entry relates to.
    	 */
    	private int picturePosition;
    	
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
        
        // This is the subset of data from the EditStatus that the Picture class
        // alters when drawing the picture. For this reason it needs to be cached
        // along with the screen data. This is so that the EditStatus can be adjusted
        // to match the given picture position.
        private ToolType tool;
        private int visualColour;
        private int priorityColour;
        private int controlColour;
        private int brushSize;
        private BrushShape brushShape;
        private BrushTexture brushTexture;

        /**
         * Constructor for PictureCache.
         * 
         * @param visualScreen 
         * @param priorityScreen 
         * @param controlScreen 
         */
        public PictureCacheEntry(int[] visualScreen, int[] priorityScreen, int[] controlScreen) {
        	
        }
        
        public int[] getVisualScreen() {
            return visualScreen;
        }
        
        public void setVisualScreen(int[] visualScreen) {
            this.visualScreen = visualScreen;
        }
        
        public int[] getPriorityScreen() {
            return priorityScreen;
        }
        
        public void setPriorityScreen(int[] priorityScreen) {
            this.priorityScreen = priorityScreen;
        }
        
        public int[] getControlScreen() {
            return controlScreen;
        }
        
        public void setControlScreen(int[] controlScreen) {
            this.controlScreen = controlScreen;
        }
        
        public ToolType getTool() {
            return tool;
        }
        
        public void setTool(ToolType tool) {
            this.tool = tool;
        }
        
        public int getVisualColour() {
            return visualColour;
        }
        
        public void setVisualColour(int visualColour) {
            this.visualColour = visualColour;
        }
        
        public int getPriorityColour() {
            return priorityColour;
        }
        
        public void setPriorityColour(int priorityColour) {
            this.priorityColour = priorityColour;
        }
        
        public int getControlColour() {
            return controlColour;
        }
        
        public void setControlColour(int controlColour) {
            this.controlColour = controlColour;
        }
        
        public int getBrushSize() {
            return brushSize;
        }
        
        public void setBrushSize(int brushSize) {
            this.brushSize = brushSize;
        }
        
        public BrushShape getBrushShape() {
            return brushShape;
        }
        
        public void setBrushShape(BrushShape brushShape) {
            this.brushShape = brushShape;
        }
        
        public BrushTexture getBrushTexture() {
            return brushTexture;
        }
        
        public void setBrushTexture(BrushTexture brushTexture) {
            this.brushTexture = brushTexture;
        }
    }
}
