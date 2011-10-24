package com.agifans.picedit.view;

import java.io.IOException;

/**
 * Handles the Ego Test mode when it is activated.
 * 
 * @author Lance Ewing
 */
public class EgoTestHandler {

    // The possibly directions that Ego can be moving in.
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
    protected Direction direction;
    
    /**
     * Constructor for EgoTestHandler.
     */
    public EgoTestHandler() {
        try {
            this.egoView = new View(ClassLoader.getSystemResourceAsStream("com/agifans/picedit/view/view.000"));
            System.out.println("egoView: " + egoView);
        } catch (Exception e) {
            //Should never happen since we know that this VIEW is present in the JAR.
            e.printStackTrace();
        }
    }
    
    
}
