package com.agifans.picedit.view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import com.agifans.picedit.EditStatus;

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
     * Constructor for EgoTestHandler.
     * 
     * @param editStatus 
     */
    public EgoTestHandler(final EditStatus editStatus) {
        // Load the VIEW resource for Ego.
        try {
            this.egoView = new View(ClassLoader.getSystemResourceAsStream("com/agifans/picedit/view/view.000"));
        } catch (Exception e) {
            //Should never happen since we know that this VIEW is present in the JAR.
            e.printStackTrace();
        }
        
        // Timer that makes Ego walk when Ego Test mode is activated.
        Timer timer = new Timer();
        TimerTask walkingTask = new TimerTask() {
            public void run() {
                if (editStatus.isEgoTestEnabled()) {
                    cycleAndMoveEgo();
                }
            }
        };
        timer.scheduleAtFixedRate(walkingTask, 500, 500);
    }
    
    public Image getCurrentCellImage() {
        return this.egoView.getLoop(currentLoop).getCell(currentCell).convertToImage();
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
        graphics.drawImage(egoImage, x, y, getCurrentCellWidth() * 2 * zoomFactor, getCurrentCellHeight() * zoomFactor, null);
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
                    break;
            
                case SOUTH:
                    break;
            
                case EAST:
                    break;
            
                case WEST:
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
