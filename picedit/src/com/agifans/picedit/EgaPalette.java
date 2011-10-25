package com.agifans.picedit;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the 16 colours that make up the EGA palette.
 * 
 * @author Lance Ewing
 */
public class EgaPalette {

    // The Color constants for the 16 EGA colours (and also the transparent colour we use).
    public final static Color BLACK = new Color(0x000000);
    public final static Color BLUE = new Color(0x0000AA);
    public final static Color GREEN = new Color(0x00AA00);
    public final static Color CYAN = new Color(0x00AAAA);
    public final static Color RED = new Color(0xAA0000);
    public final static Color MAGENTA = new Color(0xAA00AA);
    public final static Color BROWN = new Color(0xAA5500);
    public final static Color GREY = new Color(0xAAAAAA);
    public final static Color DARKGREY = new Color(0x555555);
    public final static Color LIGHTBLUE = new Color(0x5555FF);
    public final static Color LIGHTGREEN = new Color(0x55FF55);
    public final static Color LIGHTCYAN = new Color(0x55FFFF);
    public final static Color PINK = new Color(0xFF5555);
    public final static Color LIGHTMAGENTA = new Color(0xFF55FF);
    public final static Color YELLOW = new Color(0xFFFF55);
    public final static Color WHITE = new Color(0xFFFFFF);
    public final static Color TRANSPARENT = new Color(0x00000000, true);

    // RGB values for use in colors array.
    public final static int black = BLACK.getRGB();
    public final static int blue = BLUE.getRGB();
    public final static int green = GREEN.getRGB();
    public final static int cyan = CYAN.getRGB();
    public final static int red = RED.getRGB();
    public final static int magenta = MAGENTA.getRGB();
    public final static int brown = BROWN.getRGB();
    public final static int grey = GREY.getRGB();
    public final static int darkgrey = DARKGREY.getRGB();
    public final static int lightblue = LIGHTBLUE.getRGB();
    public final static int lightgreen = LIGHTGREEN.getRGB();
    public final static int lightcyan = LIGHTCYAN.getRGB();
    public final static int pink = PINK.getRGB();
    public final static int lightmagenta = LIGHTMAGENTA.getRGB();
    public final static int yellow = YELLOW.getRGB();
    public final static int white = WHITE.getRGB();
    public final static int transparent = TRANSPARENT.getRGB();

    /**
     * Holds the RGB values for the 16 EGA colours.
     */
    public final static int[] colours = { black, blue, green, cyan, red, magenta, brown, grey, darkgrey, lightblue, lightgreen, lightcyan, pink, lightmagenta, yellow, white, transparent };

    /**
     * Holds a reverse mapping from RGB value to EGI colour index.
     */
    public final static Map<Integer, Integer> reverseColours = new HashMap<Integer, Integer>();
    static {
        for (int i=0; i<colours.length; i++) {
            reverseColours.put(colours[i], i);
        }
    }
    
    /**
     * Holds the Color objects for the 16 EGA colours.
     */
    public final static Color[] COLOR_OBJECTS = { BLACK, BLUE, GREEN, CYAN, RED, MAGENTA, BROWN, GREY, DARKGREY, LIGHTBLUE, LIGHTGREEN, LIGHTCYAN, PINK, LIGHTMAGENTA, YELLOW, WHITE, TRANSPARENT };
}
