package com.agifans.picedit.view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Timer;
import java.util.TimerTask;

import com.agifans.picedit.EditStatus;
import com.agifans.picedit.EgaPalette;
import com.agifans.picedit.Picture;
import com.agifans.picedit.PictureType;

/**
 * Handles the Ego Test mode when it is activated.
 * 
 * @author Lance Ewing
 */
public class EgoTestHandler {

    // The possible directions that Ego can be moving in.
    enum Direction { 
        NONE, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
    };
    
    /**
     * The X position of Ego.
     */
    protected int x;
    
    /**
     * The Y position of Ego.
     */
    protected int y;
    
    /**
     * The View that contains the data for Ego.
     */
    protected View egoView;
    
    /**
     * The current loop that is being animated for Ego.
     */
    protected int currentLoop;
    
    /**
     * The current cell that is being displayed for Ego.
     */
    protected int currentCell;
    
    /**
     * The Direction that Ego is moving.
     */
    protected Direction direction = Direction.NONE;
    
    /**
     * The Picture that this EgoTestHandler is testing.
     */
    private Picture picture;
    
    /**
     * Holds the current edit status of the picture.
     */
    private EditStatus editStatus;
    
    /**
     * Constructor for EgoTestHandler.
     * 
     * @param editStatus Holds the current edit status of the picture.
     * @param picture The Picture being tested, i.e. that Ego will be drawn on.
     */
    public EgoTestHandler(final EditStatus editStatus, Picture picture) {
        this.picture = picture;
        this.editStatus = editStatus;
        
        // Load the VIEW resource for Ego.
        try {
            this.egoView = new View(ClassLoader.getSystemResourceAsStream("com/agifans/picedit/view/view.000"));
        } catch (Exception e) {
            //Should never happen since we know that this VIEW is present in the JAR.
        }
        
        this.x = 80 - (this.getCurrentCellWidth() / 2);
        this.y = 150 - this.getCurrentCellHeight();
        
        // Timer that makes Ego walk when Ego Test mode is activated.
        Timer timer = new Timer();
        TimerTask walkingTask = new TimerTask() {
            public void run() {
                if (editStatus.isEgoTestEnabled()) {
                    cycleAndMoveEgo();
                }
            }
        };
        timer.scheduleAtFixedRate(walkingTask, 100, 100);
    }
    
    public Image getCurrentCellImage() {
        // TODO: Use picture.getPriorityScreen to work out where to add transparent pixels.
        // TODO: Add transparent pixels for cell transparent colour as well.
        
        Cell cell = this.egoView.getLoop(currentLoop).getCell(currentCell);
        int[] rgbPixelData = cell.getRGBPixelData().clone();
        int transparentColour = cell.getTransparentColour();
        int priorityBand = getPriorityBand();
        int width = cell.getWidth();
        int height = cell.getHeight();
        
        // TODO: This has to be done using x, y and priority screen data.
        for (int i = 0; i < (width * height); i++) {
            if (rgbPixelData[i] == transparentColour) {
                rgbPixelData[i] = EgaPalette.transparent;
            }
        }
        
        //return this.egoView.getLoop(currentLoop).getCell(currentCell).convertToImage();
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, ColorModel.getRGBdefault(), rgbPixelData, 0, width));
    }
    
    /**
     * Gets the priority band associated with where Ego currently is.
     * 
     * @return The priority band associated with where Ego currently is.
     */
    public int getPriorityBand() {
        int priorityBand = 0;
        
        // Priority is calculated based on where the bottom of the cell is.
        int egoBottom = this.y + this.getCurrentCellHeight();
        
        if (editStatus.getPictureType().equals(PictureType.SCI0)) {
            // For SCI0, the top 42 lines are for priority 0. The other 14 bands
            // get an even share of the 148 remaining lines (which, btw, doesn't
            // divide evenly, so the bands are not even as then are in AGI).
            priorityBand = ((int) ((egoBottom - 42) / ((190 - 42) / 14))) + 1;
        } else if (editStatus.getPictureType().equals(PictureType.AGI)) {
            // For AGI it is evenly split, 168 lines split 14 ways.
            priorityBand = (egoBottom / 12) + 1;

            // Make sure priority band is 4 or above for AGI since the bottom
            // four priority colours are reserved as control lines.
            if (priorityBand < 4) {
                priorityBand = 4;
            }
        }
        
        return priorityBand;
    }
    
    public int getCurrentCellWidth() {
        return this.egoView.getLoop(currentLoop).getCell(currentCell).getWidth();
    }
    
    public int getCurrentCellHeight() {
        return this.egoView.getLoop(currentLoop).getCell(currentCell).getHeight();
    }
    
    /**
     * Draws Ego on to the given Graphics2D
     * 
     * @param graphics
     * @param zoomFactor 
     */
    public void drawEgo(Graphics graphics, int zoomFactor) {
        Image egoImage = this.getCurrentCellImage();
        graphics.drawImage(egoImage, x * 2 * zoomFactor, y * zoomFactor, getCurrentCellWidth() * 2 * zoomFactor, getCurrentCellHeight() * zoomFactor, null);
    }
    
    /**
     * Cycles Ego to the next Cell and moves in the current direction.
     */
    public void cycleAndMoveEgo() {
        if (direction != Direction.NONE) {
            Loop loop = this.egoView.getLoop(currentLoop);
            
            // Cycle to the next cell in this loop.
            this.currentCell++;
            if (this.currentCell >= loop.getNumberOfCells()) {
                this.currentCell = 0;
            }
            
            // Move in the appropriate direction.
            switch (direction) {
                case NORTH:
                    y = y - 1;
                    if (y < 0) {
                        y = 167 - this.getCurrentCellHeight();
                    }
                    break;
            
                case SOUTH:
                    y = y + 1;
                    if (y > (167 - this.getCurrentCellHeight())) {
                        y = 0;
                    }
                    break;
            
                case EAST:
                    x = x + 1;
                    if (x > (159 - this.getCurrentCellWidth())) {
                        x = 0;
                    }
                    break;
            
                case WEST:
                    x = x - 1;
                    if (x < 0) {
                        x = 159 - this.getCurrentCellWidth();
                    }
                    break;
            }
        }
    }
    
    /**
     * Handles given KeyEvent if applicable.
     * 
     * @param keyEvent The KeyEvent to handle (if applicable).
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        
        switch (key) {
            case KeyEvent.VK_UP:
                if (this.direction == Direction.NORTH) {
                    this.direction = Direction.NONE;
                } else {
                    this.direction = Direction.NORTH;
                    this.currentLoop = 3;
                    this.currentCell = 0;
                }
                break;
                
            case KeyEvent.VK_DOWN:
                if (this.direction == Direction.SOUTH) {
                    this.direction = Direction.NONE;
                } else {
                    this.direction = Direction.SOUTH;
                    this.currentLoop = 2;
                    this.currentCell = 0;
                }
                break;
                
            case KeyEvent.VK_LEFT:
                if (this.direction == Direction.WEST) {
                    this.direction = Direction.NONE;
                } else {
                    this.direction = Direction.WEST;
                    this.currentLoop = 1;
                    this.currentCell = 0;
                }
                break;
                
            case KeyEvent.VK_RIGHT:
                if (this.direction == Direction.EAST) {
                    this.direction = Direction.NONE;
                } else {
                    this.direction = Direction.EAST;
                    this.currentLoop = 0;
                    this.currentCell = 0;
                }
                break;
        }
    }
}
