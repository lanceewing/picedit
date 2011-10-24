package com.agifans.picedit.view;

/**
 * A Loop of Cells within a View resource.
 * 
 * @author Lance Ewing
 */
public class Loop {
    
    /** 
     * Cells within this loop.
     */
    protected Cell cells[] = null;

    /** 
     * Constructor for Loop
     * 
     * @param cells The cells within this loop.
     */
    public Loop(Cell[] cells) {
        this.cells = cells;
    }

    /**
     * Constructor for Loop.
     * 
     * @param viewData The byte array containing the full data for the VIEW resource that this Loop belongs to.
     * @param start The offset of where this Loop starts within the VIEW.
     * @param loopNumber The number of the loop within the View.
     */
    public Loop(byte[] viewData, int start, int loopNumber) {
        // Work out the number of cells in this Loop.
        int cellCount = (viewData[start] & 0xFF);
        
        cells = new Cell[cellCount];

        // Create each of the Cells.
        int offset = start + 1;
        for (int cellNumber = 0; cellNumber < cellCount; cellNumber++) {
            int cellOffset = start + ((viewData[offset+1] & 0xFF) << 8) | (viewData[offset] & 0xFF);
            cells[cellNumber] = new Cell(viewData, cellOffset, loopNumber);
            offset += 2;
        }
    }

    /**
     * Gets the given Cell.
     * 
     * @param cellNumber The number of the Cell within this Loop to get.
     * 
     * @return The requested Cell object.
     */
    public Cell getCell(int cellNumber) {
        return cells[cellNumber];
    }

    /**
     * Gets the number of Cells in this Loop.
     * 
     * @return The number of Cells in this Loop.
     */
    public int getNumberOfCells() {
        return cells.length;
    }
}