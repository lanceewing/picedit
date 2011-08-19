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
     * Constructor for PictureCache.
     */
    public PictureCache() {
        this.cache = new TreeMap<Integer, PictureCacheEntry>();
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
        
        // TODO: This needs to store the visual, priority and control line screens.
        // TODO: Should this store the int arrays or the associated BufferedImage?

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

        
        
    }
}
