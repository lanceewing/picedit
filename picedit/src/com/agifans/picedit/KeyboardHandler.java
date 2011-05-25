package com.agifans.picedit;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles processing of PICEDIT key pressed events.
 * 
 * @author Lance Ewing
 */
public class KeyboardHandler extends CommonHandler implements KeyListener {

	/**
	 * Constructor for KeyboardHandler.
	 * 
     * @param editStatus the EditStatus holding current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param menu the menu system used by PICEDIT.
     * @param application the PICEDIT application component.
	 */
	public KeyboardHandler(EditStatus editStatus, PicGraphics picGraphics, Picture picture, Menu menu, PicEdit application) {
		super(editStatus, picGraphics, picture, menu, application);
	}

	/**
	 * Processes the given key pressed KeyEvent.
	 * 
	 * @param e the KeyEvent representing the key that was typed.
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (editStatus.isPaused()) {
			// Ignore key events if the application is paused.
		} else if (editStatus.isMenuActive()) {
			// If menu system is active, send the event to the meny system to process.
			menu.processKeyEvent(e);
		} else {
			if (key == KeyEvent.VK_ESCAPE) {
				// Activate menu system.
				menu.processKeyEvent(e);
			}
			if (key == KeyEvent.VK_TAB) {
				// Switches between the two screens.
			   editStatus.toggleScreen();
			   editStatus.setTool(ToolType.NONE);
			   picture.updateScreen();
			}
			
			// Handle picture buffer navigation keys.
			if (key == KeyEvent.VK_HOME) {
				processMoveToStartOfPictureBuffer();
			}
			if (key == KeyEvent.VK_LEFT) {
				processMoveBackOnePictureAction();
			}
			if (key == KeyEvent.VK_RIGHT) {
				processMoveForwardOnePictureAction();
			}
			if (key == KeyEvent.VK_END) {
				processMoveToEndOfPictureBuffer();
			}
			
			// Handle tool selection keys.
			if ((key == KeyEvent.VK_F1) || (key == KeyEvent.VK_L)) {
				processToolSelect(ToolType.LINE);
			}
			if ((key == KeyEvent.VK_F2) || (key == KeyEvent.VK_P)) {
				processToolSelect(ToolType.PEN);
			}
			if ((key == KeyEvent.VK_F3) || (key == KeyEvent.VK_S)) {
				processToolSelect(ToolType.STEP);
			}
			if ((key == KeyEvent.VK_F4) || (key == KeyEvent.VK_F)) {
				processToolSelect(ToolType.FILL);
			}
			if ((key == KeyEvent.VK_F5) || (key == KeyEvent.VK_B)) {
				processToolSelect(ToolType.BRUSH);
			}
			
			// F6 toggles the display of the current background image.
   			if (key == KeyEvent.VK_F6) {
   				processToggleBackground();
   			}
   			
   			// Handle picture action delete key.
   			if (key == KeyEvent.VK_DELETE) {
   				processDeleteCurrentPictureAction();
   			}
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
