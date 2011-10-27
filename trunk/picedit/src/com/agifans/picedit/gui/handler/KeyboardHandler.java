package com.agifans.picedit.gui.handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.PicGraphics;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.types.ToolType;
import com.agifans.picedit.view.EgoTestHandler;

/**
 * Handles processing of PICEDIT key pressed events.
 * 
 * @author Lance Ewing
 */
public class KeyboardHandler extends CommonHandler implements KeyListener {

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
        super(application);
        
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
        PicGraphics picGraphics = application.getPicGraphics();
        
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

        // Handle tool selection keys.
        if ((key == KeyEvent.VK_F1) || (key == KeyEvent.VK_L)) {
            processToolSelect(ToolType.LINE);
        }
        if ((key == KeyEvent.VK_F2) || (key == KeyEvent.VK_P)) {
            processToolSelect(ToolType.SHORTLINE);
        }
        if ((key == KeyEvent.VK_F3) || (key == KeyEvent.VK_S)) {
            processToolSelect(ToolType.STEPLINE);
        }
        if ((key == KeyEvent.VK_F4) || (key == KeyEvent.VK_F)) {
            processToolSelect(ToolType.FILL);
        }
        if ((key == KeyEvent.VK_F5) || (key == KeyEvent.VK_B)) {
            processToolSelect(ToolType.BRUSH);
        }

        // Handle picture action delete key.
        if (key == KeyEvent.VK_DELETE) {
            picture.deleteCurrentPictureAction();
        }

        // Check if the screen needs to be updated.
        picGraphics.checkDrawFrame();
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
