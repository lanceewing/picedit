package com.agifans.picedit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

/**
 * Handles processing that is common to both mouse and keyboard events.
 * 
 * @author Lance Ewing
 */
public abstract class CommonHandler {

    /**
     * Holds the current "editing" state of everything within PICEDIT.
     */
    protected EditStatus editStatus;

    /**
     * The graphics routines with which the application draws the screen.
     */
    protected PicGraphics picGraphics;

    /**
     * The AGI picture being edited.
     */
    protected Picture picture;

    /**
     * The PICEDIT application component.
     */
    protected PicEdit application;
    
    /**
     * Constructor for CommonHandler.
     * 
     * @param editStatus the EditStatus holding current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param application the PICEDIT application component.
     */
    public CommonHandler(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
        this.editStatus = editStatus;
        this.picGraphics = picGraphics;
        this.picture = picture;
        this.application = application;
    }

    /**
     * Processes the changing of the visual colour.
     * 
     * @param newVisualColour the new visual colour.
     */
    protected void processVisualColourChange(int newVisualColour) {
        editStatus.setVisualColour(newVisualColour);
        editStatus.addPictureCode(0xF0);
        editStatus.addPictureCode(newVisualColour);
        picture.updateScreen();
    }

    /**
     * Processes the selection of a new tool.
     * 
     * @param tool the tool to process the selection of.
     */
    protected void processToolSelect(ToolType tool) {
        editStatus.setTool(tool);
        picture.updateScreen();
    }

    /**
     * Allow the user to go to a position in the picture buffer immediately 
     * without having to use the navigation buttons. The position will be 
     * set to the start of the drawing action that the given position lies 
     * within.
     */
    protected void processEnterPosition() {
        // Disable processing of key events by the main application (i.e. no menu, no tools)
        editStatus.setPaused(true);

        final StringBuilder inputLine = new StringBuilder();

        // Clear position box reading for input.
        picGraphics.drawFilledBox(230, 179, 273, 187, 0);
        picGraphics.drawString(inputLine.toString(), 231, 180, 7, 0);

        // Register a temporary KeyListener for getting the new position.
        this.application.getPicturePanel().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_ESCAPE) {
                    // Keep current position.
                    application.getPicturePanel().removeKeyListener(this);
                    editStatus.setPaused(false);
                    picture.drawPicture();
                    picture.updateScreen();
                    inputLine.delete(0, inputLine.length());
                    inputLine.append(editStatus.getPicturePosition());

                } else if (key == KeyEvent.VK_BACK_SPACE) {
                    if (inputLine.length() > 0) {
                        inputLine.deleteCharAt(inputLine.length() - 1);
                    }
                } else if (key == KeyEvent.VK_ENTER) {
                    // User has finished entering a position.
                    application.getPicturePanel().removeKeyListener(this);

                    if (inputLine.length() > 0) {
                        // If the entered value is valid, apply the new position.
                        LinkedList<PictureCode> pictureCodes = editStatus.getPictureCodes();
                        int newPosition = Integer.parseInt(inputLine.toString());
                        if ((newPosition >= 0) && (newPosition < pictureCodes.size())) {
                            if (newPosition < (pictureCodes.size() - 1)) {
                                // Find the closest picture action to the entered position.
                                while (pictureCodes.get(newPosition).getCode() < 0xF0) {
                                    newPosition = newPosition - 1;
                                }
                            }
                            editStatus.setPicturePosition(newPosition);
                        }
                    }

                    editStatus.setPaused(false);
                    picture.drawPicture();
                    picture.updateScreen();
                    inputLine.delete(0, inputLine.length());
                    inputLine.append(editStatus.getPicturePosition());

                } else if ((e.getKeyChar() >= '0') && (e.getKeyChar() <= '9')) {
                    // If the character is a number, and the maximum length has not been reached, then add to the input. 
                    if (inputLine.length() < 5) {
                        inputLine.append(e.getKeyChar());
                    }
                }

                String tempString = String.format("%5s", inputLine);
                picGraphics.drawFilledBox(230, 179, 273, 187, 0);
                picGraphics.drawString(tempString, 230, 180, 7, 0);
            }
        });
    }

    /**
     * Process movement back one picture action through the picture code buffer.
     */
    protected void processMoveBackOnePictureAction() {
        // Move back through the codes until we find an Action code.
        PictureCode pictureCode = null;
        do {
            pictureCode = editStatus.decrementPicturePosition();
        } while ((pictureCode != null) && !pictureCode.isActionCode() && (editStatus.getPicturePosition() > 0));

        picture.drawPicture();
        picture.updateScreen();
    }

    /**
     * Process movement forward one picture action through the picture code buffer.
     */
    protected void processMoveForwardOnePictureAction() {
        if (editStatus.getPicturePosition() < (editStatus.getPictureCodes().size() - 1)) {
            PictureCode pictureCode = null;
            do {
                pictureCode = editStatus.incrementPicturePosition();
            } while ((pictureCode != null) && !pictureCode.isActionCode());

            picture.drawPicture();
            picture.updateScreen();
        }
    }

    /**
     * Process movement to the start of the picture code buffer.
     */
    protected void processMoveToStartOfPictureBuffer() {
        editStatus.setPicturePosition(0);
        picture.drawPicture();
        picture.updateScreen();
    }

    /**
     * Process movement to the end of the picture code buffer.
     */
    protected void processMoveToEndOfPictureBuffer() {
        if (editStatus.getPicturePosition() < (editStatus.getPictureCodes().size() - 1)) {
            editStatus.setPicturePosition(editStatus.getPictureCodes().size() - 1);
            picture.drawPicture();
            picture.updateScreen();
        }
    }

    /**
     * Process deletion of the current picture action, i.e. the picture
     * action at the current picture position.
     */
    protected void processDeleteCurrentPictureAction() {
        PictureCode pictureCode = editStatus.deleteAtPicturePosition();
        while ((pictureCode != null) && (pictureCode.isDataCode())) {
            pictureCode = editStatus.deleteAtPicturePosition();
        }
        editStatus.setLastRenderedPicturePosition(EditStatus.LAST_VALUE_NONE);
        picture.drawPicture();
        picture.updateScreen();
    }

    /**
     * Toggles the display of the priority screen.
     */
    public void processTogglePriorityScreen() {
        editStatus.toggleScreen();
        if (editStatus.isPriorityShowing()) {
            application.getMenu().getPriorityMenuItem().setSelected(true);
        } else {
            application.getMenu().getVisualMenuItem().setSelected(true);
        }
        editStatus.setTool(ToolType.NONE);
        picture.updateScreen();
    }

    /**
     * Processes toggling of the display of the background tracking image.
     */
    protected void processToggleBackground() {
        editStatus.setBackgroundEnabled(!editStatus.isBackgroundEnabled());
        application.getMenu().getBackgroundMenuItem().setSelected(editStatus.isBackgroundEnabled());
    }

    /**
     * Disables editing until the user has pressed a key or clicked a mouse button.
     * 
     * @param callback a task to perform once the key stroke or mouse click has happened.
     */
    protected void waitForKeyStrokeOrMouseClick(final Runnable callback) {
        editStatus.setPaused(true);

        // Wrap each listener in an array to get around the inner class restrictions.
        final KeyListener[] keyListener = new KeyListener[1];
        final MouseListener[] mouseListener = new MouseListener[1];

        // Register a temporary key and mouse listener to wait for the key/mouse event. 
        keyListener[0] = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                application.getPicturePanel().removeKeyListener(keyListener[0]);
                application.getPicturePanel().removeMouseListener(mouseListener[0]);
                if (callback != null) {
                    callback.run();
                } else {
                    picture.updateScreen();
                    editStatus.setPaused(false);
                }
            }
        };
        mouseListener[0] = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                application.getPicturePanel().removeKeyListener(keyListener[0]);
                application.getPicturePanel().removeMouseListener(mouseListener[0]);
                if (callback != null) {
                    callback.run();
                } else {
                    picture.updateScreen();
                    editStatus.setPaused(false);
                }
            }
        };
        application.getPicturePanel().addKeyListener(keyListener[0]);
        application.getPicturePanel().addMouseListener(mouseListener[0]);

        // The listeners are registered at this point. The method exits. The listeners
        // take care of the rest. They will unregister themselves once they've been
        // triggered.
    }

    /**
     * Displays the 'About' PICEDIT message box.
     */
    protected void showAboutMessage() {
        picGraphics.drawMessageBox(64, 69, 255, 116);
        picGraphics.drawString("    PICEDIT 1.3M2    ", 72, 77, 0, 15);
        picGraphics.drawString("         by          ", 72, 89, 0, 15);
        picGraphics.drawString("     Lance Ewing     ", 72, 101, 0, 15);
        waitForKeyStrokeOrMouseClick(null);
    }

    /**
     * Displays page 1 of the help.
     */
    private void showHelpPage1() {
        editStatus.setPaused(true);

        Font font = new Font("DialogInput", Font.BOLD, 14);
        Image image = application.createImage(640, 400);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 640, 400);
        graphics.setFont(font);
        graphics.setColor(new Color(0xAAAAAA));
        graphics.drawString("                                  PICEDIT v1.3M2", 0, 16);
        graphics.drawString(" ESC     Brings up the menu system. Use arrow keys to select.", 0, 48);
        graphics.drawString(" TAB     Switch between visual\\priority screens.", 0, 64);
        graphics.drawString(" PALETTE", 0, 96);
        graphics.drawString(" Right click: choose colour for visual screen.", 0, 128);
        graphics.drawString(" Left click: choose colour for priority screen.", 0, 144);
        graphics.drawString(" Off: Disable visual/priority drawing.", 0, 160);
        graphics.drawString(" FEATURES", 0, 192);
        graphics.drawString(" - Click on buffer position field to enter a specific position.", 0, 224);
        graphics.drawString(" - Use navigation buttons to navigate through picture code buffer.", 0, 240);
        graphics.drawString(" - Drawing actions can be inserted anywhere inside the picture.", 0, 256);
        graphics.drawString(" - The delete button removes the drawing action that is currently being", 0, 272);
        graphics.drawString("   shown in the data box (bottom right corner).", 0, 288);
        graphics.drawString(" - The brush tool is set up using the buttons immediately to the right of", 0, 304);
        graphics.drawString("   the off button.", 0, 320);
        graphics.drawString(" - Right clicking on the picture surface once stops current tool.", 0, 336);
        graphics.drawString(" - Right clicking on the picture surface twice switches tool to None.", 0, 352);
        graphics.drawString(" - Use the background image option to enable tracing of PNG, JPG, etc.", 0, 368);

        picGraphics.setTextImage(image);
        editStatus.setTextMode(true);

        waitForKeyStrokeOrMouseClick(new Runnable() {
            public void run() {
                showHelpPage2();
            }
        });
    }

    /**
     * Displays page 2 of the help.
     */
    private void showHelpPage2() {
        Font font = new Font("DialogInput", Font.BOLD, 14);
        Image image = application.createImage(640, 400);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 640, 400);
        graphics.setFont(font);
        graphics.setColor(new Color(0xAAAAAA));
        graphics.drawString("                                  PICEDIT v1.3M2", 0, 16);
        graphics.drawString(" SHORTCUT KEYS", 0, 64);
        graphics.drawString(" L, l, F1: Activates line tool", 0, 96);
        graphics.drawString(" P, p, F2: Activates pen tool", 0, 112);
        graphics.drawString(" S, s, F3: Activates step tool", 0, 128);
        graphics.drawString(" F, f, F4: Activates fill tool", 0, 144);
        graphics.drawString(" B, b, F5: Activates brush tool", 0, 160);
        graphics.drawString(" F6: Toggles background picture", 0, 192);
        graphics.drawString(" Home: Start of picture code buffer", 0, 224);
        graphics.drawString(" Left arrow: Back one drawing action", 0, 240);
        graphics.drawString(" Right arrow: Forward one drawing action", 0, 256);
        graphics.drawString(" End: End of picture code buffer", 0, 272);
        graphics.drawString(" Del: Remove drawing action at current buffer position", 0, 288);

        picGraphics.setTextImage(image);

        waitForKeyStrokeOrMouseClick(new Runnable() {
            public void run() {
                editStatus.setPaused(false);
                editStatus.setTextMode(false);
            }
        });
    }

    /**
     * Displays the help message.
     */
    protected void showHelp() {
        showHelpPage1();
    }

    /**
     * Renders one page of the picture codes in hex format.
     * 
     * @param position the position in the picture code buffer to start rendering from.
     */
    private void showPageOfHexData(int position) {
        LinkedList<PictureCode> pictureCodes = editStatus.getPictureCodes();

        Font font = new Font("DialogInput", Font.BOLD, 14);
        Image textImage = application.createImage(640, 400);
        final Graphics2D graphics = (Graphics2D) textImage.getGraphics();
        graphics.setColor(EgaPalette.BLACK);
        graphics.fillRect(0, 0, 640, 400);
        graphics.setFont(font);
        graphics.setColor(EgaPalette.GREY);

        // Display the View Data heading.
        graphics.drawString("                                 PICTURE DATA", 0, 16);

        // Renders a 20 x 22 grid of hex data.
        for (int y = 48; y < 400; y += 16) {
            if (position <= (pictureCodes.size() - 1)) {
                graphics.setColor(EgaPalette.DARKGREY);
                graphics.drawString(String.format("%04X:", position), 32, y);
            } else {
                break;
            }

            for (int x = 96; x < 608; x += 32) {
                // If we have reached the end then skip.
                if (position <= (pictureCodes.size() - 1)) {
                    int code = pictureCodes.get(position).getCode();
                    if (code >= 0xF0) {
                        if (position == editStatus.getPicturePosition()) {
                            graphics.setColor(EgaPalette.LIGHTMAGENTA);
                        } else {
                            graphics.setColor(EgaPalette.RED);
                        }
                    } else {
                        graphics.setColor(EgaPalette.GREY);
                    }
                    graphics.drawString(String.format("%02X", code), x, y);
                    if (code != 0xFF) {
                        graphics.setColor(EgaPalette.DARKGREY);
                        graphics.drawString("..", x + 16, y);
                    }
                    position++;
                }
            }
        }

        picGraphics.setTextImage(textImage);
    }

    /**
     * Displays the raw hexidecimal data of the AGI picture.
     */
    protected void showHexData() {
        editStatus.setPaused(true);

        // Render first page of data.
        showPageOfHexData(0);

        // Show the text screen.
        editStatus.setTextMode(true);

        // Wrap each listener in an array to get around the inner class restrictions.
        final KeyListener[] keyListener = new KeyListener[1];
        final MouseAdapter[] mouseListener = new MouseAdapter[1];
        final int[] startPos = new int[1];

        // Register a temporary KeyListener for handling the navigation key strokes and redraw.
        keyListener[0] = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if ((key == KeyEvent.VK_ESCAPE) || (key == KeyEvent.VK_ENTER)) {
                    // Exit the view data screen.
                    application.getPicturePanel().removeKeyListener(keyListener[0]);
                    application.getPicturePanel().removeMouseListener(mouseListener[0]);
                    application.getPicturePanel().removeMouseWheelListener(mouseListener[0]);
                    editStatus.setPaused(false);
                    editStatus.setTextMode(false);
                } else if (key == KeyEvent.VK_UP) {
                    startPos[0] -= 16;
                } else if (key == KeyEvent.VK_DOWN) {
                    startPos[0] += 16;
                } else if (key == KeyEvent.VK_PAGE_UP) {
                    startPos[0] -= 352;
                } else if (key == KeyEvent.VK_PAGE_DOWN) {
                    startPos[0] += 352;
                } else if (key == KeyEvent.VK_HOME) {
                    startPos[0] = 0;
                } else if (key == KeyEvent.VK_END) {
                    startPos[0] = ((((editStatus.getPictureCodes().size() - 352) + 16) / 16) * 16);
                }

                // Keep the start pos within the valid range of the picture code buffer.
                if ((startPos[0] + 352) > editStatus.getPictureCodes().size()) {
                    startPos[0] = ((((editStatus.getPictureCodes().size() - 352) + 16) / 16) * 16);
                }
                if (startPos[0] < 0) {
                    startPos[0] = 0;
                }

                // Redraw the hex data.
                showPageOfHexData(startPos[0]);
            }
        };
        mouseListener[0] = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                application.getPicturePanel().removeKeyListener(keyListener[0]);
                application.getPicturePanel().removeMouseListener(mouseListener[0]);
                application.getPicturePanel().removeMouseWheelListener(mouseListener[0]);
                editStatus.setPaused(false);
                editStatus.setTextMode(false);
            }

            public void mouseWheelMoved(MouseWheelEvent e) {
                startPos[0] += (16 * e.getWheelRotation());

                // Keep the start pos within the valid range of the picture code buffer.
                if ((startPos[0] + 352) > editStatus.getPictureCodes().size()) {
                    startPos[0] = ((((editStatus.getPictureCodes().size() - 352) + 16) / 16) * 16);
                }
                if (startPos[0] < 0) {
                    startPos[0] = 0;
                }

                // Redraw the hex data.
                showPageOfHexData(startPos[0]);
            }
        };
        application.getPicturePanel().addKeyListener(keyListener[0]);
        application.getPicturePanel().addMouseListener(mouseListener[0]);
        application.getPicturePanel().addMouseWheelListener(mouseListener[0]);

        // The listeners are registered at this point. The method exits. The listeners
        // take care of the rest. They will unregister themselves once they've been
        // triggered.
    }

    /**
     * Loads a background image from the given File.
     * 
     * @param imageFile the image File to load for the background image.
     */
    protected void loadBackgroundImage(File imageFile) {
        try {
            Image image = ImageIO.read(imageFile);
            if (image != null) {
                picGraphics.setBackgroundImage(image);
                editStatus.setBackgroundEnabled(true);
            } else {
                picGraphics.setBackgroundImage(null);
                editStatus.setBackgroundEnabled(false);
            }
        } catch (IOException e) {
            picGraphics.setBackgroundImage(null);
            editStatus.setBackgroundEnabled(false);
        }
        
        // This will cause the offscreen image to be recreated, which will remove
        // any rendering artifacts of the previous background.
        application.getPicturePanel().resizeOffscreenImage();
    }

    /**
     * Loads an AGI picture from the given File.
     * 
     * @param pictureFile the File to load the AGI picture from.
     */
    protected void loadPicture(File pictureFile) {
        BufferedInputStream in = null;

        try {
            // Make sure we start with a clean picture.
            editStatus.clear();

            // Store file name for display on title bar.
            editStatus.setPictureFile(pictureFile);
            
            // Open the file for reading.
            in = new BufferedInputStream(new FileInputStream(pictureFile));

            // Read each of the bytes and store it in the pictureCodes LinkedList.
            int pictureCode;
            while ((pictureCode = in.read()) != -1) {
                if (pictureCode != 0xFF) {
                    editStatus.addPictureCode(pictureCode);
                } else {
                    // 0xFF is the end of an AGI picture.
                    break;
                }
            }

            picture.drawPicture();
            picture.updateScreen();

        } catch (FileNotFoundException fnfe) {
            System.out.printf("File not found : %s.\n", pictureFile.getPath());
            System.exit(1);
        } catch (IOException ioe) {
            System.out.printf("Error loading picture : %s.\n", pictureFile.getPath());
            System.exit(1);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    // Not worried about file close errors.
                }
            }
        }
    }

    /**
     * Saves the AGI picture to the given File.
     * 
     * @param pictureFile the File to write the AGI picture out to.
     */
    protected void savePicture(File pictureFile) {
        BufferedOutputStream out = null;

        try {
            // Store file name for display on title bar.
            editStatus.setPictureFile(pictureFile);
            
            // Open the file for reading.
            out = new BufferedOutputStream(new FileOutputStream(pictureFile));

            // Write each of the picture codes out to the file.
            for (PictureCode pictureCode : editStatus.getPictureCodes()) {
                out.write(pictureCode.getCode());
            }
        } catch (FileNotFoundException fnfe) {
            System.out.printf("Unable to create picture file : %s. %s\n", pictureFile.getPath(), fnfe.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.out.printf("Error saving picture : %s. %s\n", pictureFile.getPath(), ioe.getMessage());
            System.exit(1);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    // Not worried about file close errors.
                }
            }
        }
    }
}
