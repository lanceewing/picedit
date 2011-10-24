package com.agifans.picedit.view;

import java.io.*;

/**
 * Holds the data for an AGI VIEW resource.
 * 
 * @author Lance Ewing 
 */
public class View {

    /** 
     * The Loops that are a part of this View. Each Loop is in turn made up of Cells. 
     */
    protected Loop loops[];

    /**
     * Constructor for View.
     * 
     * @param inputStream The InputStream to read the VIEW data from.
     * 
     * @throws IOException If an error occurs while reading in the VIEW data.
     */
    public View(InputStream inputStream) throws IOException {
        // Read the data into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(inputStream);
        byte[] buf = new byte[1024];
        int numOfBytesRead = 0;
        while ((numOfBytesRead = in.read(buf)) != -1) {
            baos.write(buf, 0, numOfBytesRead);
        }
        
        byte[] viewData = baos.toByteArray();
        
        // Determine the number of loops in this view.
        short numberOfLoops = (short) viewData[2];

        loops = new Loop[numberOfLoops];
        
        // Create each of the Loops.
        int offset = 5;
        for (int loopNum = 0; loopNum < numberOfLoops; loopNum++) {
            int loopOffset = ((viewData[offset+1] & 0xFF) << 8) | (viewData[offset] & 0xFF);
            loops[loopNum] = new Loop(viewData, loopOffset, loopNum);
            offset += 2;
        }
    }

    /**
     * Gets the given Loop within this View.
     *
     * @param loopNumber The number of the Loop to get.
     * 
     * @return The Loop object.
     */
    public Loop getLoop(int loopNumber) {
        return loops[loopNumber];
    }

    /**
     * Gets the number of Loops in this View.
     *
     * @return The loop number of Loops in this View.
     */
    public int getNumberOfLoops() {
        return loops.length;
    }
}