package com.agifans.picedit.gui.handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.view.EgoTestHandler;

/**
 * Handles processing of PICEDIT key pressed events.
 * 
 * @author Lance Ewing
 */
public class KeyboardHandler implements KeyListener {

    /**
     * The PICEDIT application component.
     */
    protected PicEdit application;
    
    /**
     * The handler for managing the Ego Test mode.
     */
    private EgoTestHandler egoTestHandler;
    
    /**
     * Constructor for KeyboardHandler.
     * 
     * @param application the PICEDIT application component.
     * @param egoTestHandler The handler for managing the Ego Test mode.
     */
    public KeyboardHandler(PicEdit application, EgoTestHandler egoTestHandler) {
        this.application = application;
        this.egoTestHandler = egoTestHandler;
    }

    /**
     * Processes the given key pressed KeyEvent.
     * 
     * @param e the KeyEvent representing the key that was typed.
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        EditStatus editStatus = application.getEditStatus();
        Picture picture = application.getPicture();
        
        // If any key is pressed, we'll always clear the temporary line... just in case.
        application.getPicturePanel().clearTemporaryLine();
        
        if (editStatus.isEgoTestEnabled()) {
            // If Ego Test mode enabled, delegate to the EgoTestHandler.
            this.egoTestHandler.handleKeyEvent(e);
            
        } else {
            // Handle picture buffer navigation keys.
            if (key == KeyEvent.VK_HOME) {
                picture.moveToStartOfPictureBuffer();
            }
            if (key == KeyEvent.VK_LEFT) {
                picture.moveBackOnePictureAction();
            }
            if (key == KeyEvent.VK_RIGHT) {
                picture.moveForwardOnePictureAction();
            }
            if (key == KeyEvent.VK_END) {
                picture.moveToEndOfPictureBuffer();
            }
        }

        // Handle picture action delete key.
        if (key == KeyEvent.VK_DELETE) {
            picture.deleteCurrentPictureAction();
        }
    }

    /**
     * Invoked when a key is released.
     * 
     * @param e the key released event.
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Invoked when a key is typed (pressed then released).
     * 
     * @param e the key typed event.
     */
    public void keyTyped(KeyEvent e) {
    }
}
