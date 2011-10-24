package com.agifans.picedit.view;

import java.awt.*;
import java.awt.image.*;

import com.agifans.picedit.EgaPalette;

/**
 * 
 */
public class Cell {
    
    /** Cell's Width */
    protected int width;

    /** Cell's Height */
    protected int height;

    /** Cell's Data */
    protected int[] data;

    /** Cell's Transparent Color */
    protected int transparent;

    /**
     * Constructor for Cell.
     * 
     * @param viewData
     * @param start
     * @param loopNumber
     */
    public Cell(byte[] viewData, int start, int loopNumber) {
        int trans;
        short mirrorInfo;
        byte transColor;

        width = (viewData[start] & 0xFF);
        height = (viewData[start+1] & 0xFF);
        trans = (viewData[start+2] & 0xFF);
        
        transColor = (byte) (trans & 0x0F);
        mirrorInfo = (short) ((trans & 0xF0) >> 4);

        loadData(viewData, start + 3, transColor);

        if ((mirrorInfo & 0x8) != 0) {
            if ((mirrorInfo & 0x7) != loopNumber) {
                mirror();
            }
        }
    }

    protected void loadData(byte b[], int off, byte transColor) {
        int i, j, x, y, color, count;
        data = new int[width * height];

        for (j = 0, y = 0; y < height; y++) {
            for (x = 0; b[off] != 0; off++) {
                color = (b[off] & 0xF0) >> 4;
                count = (b[off] & 0x0F);

                for (i = 0; i < count; i++, j++, x++) {
                    data[j] = EgaPalette.colours[color];
                }
            }

            for (; x < width; j++, x++) {
                data[j] = EgaPalette.colours[transColor];
            }

            off++;
        }

        transparent = EgaPalette.colours[transColor];
    }

    protected void mirror() {
        int i1, i2, x1, x2, y;
        int b;

        for (y = 0; y < height; y++) {
            for (x1 = width - 1, x2 = 0; x1 > x2; x1--, x2++) {
                i1 = (y * width) + x1;
                i2 = (y * width) + x2;

                b = data[i1];
                data[i1] = data[i2];
                data[i2] = b;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixelData() {
        return data;
    }

    public int getTransparentPixel() {
        return transparent;
    }

    /**
     * Obtain an standard Image object that is a graphical representation of the
     * cell.
     *
     * @param context Game context used to generate the image.
     */
    public Image getImage() {
        int[] data = (int[]) this.data.clone();
        DirectColorModel colorModel = (DirectColorModel) ColorModel.getRGBdefault();
        int i;

        for (i = 0; i < (width * height); i++) {
            if (data[i] != transparent) {
                data[i] = EgaPalette.colours[data[i]];
            } else {
                data[i] = 0x00ffffff;
            }
        }

        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, colorModel, data, 0, width));
    }
}