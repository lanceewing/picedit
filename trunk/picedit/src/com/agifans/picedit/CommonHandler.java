package com.agifans.picedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * Handles processing that is common to both mouse and keyboard events.
 * 
 * @author Lance Ewing
 */
public abstract class CommonHandler {

    /**
     * The PICEDIT application component.
     */
    protected PicEdit application;
    
    /**
     * The help window.
     */
    private JFrame helpFrame;
     
    /**
     * Constructor for CommonHandler.
     * 
     * @param application the PICEDIT application component.
     */
    public CommonHandler(PicEdit application) {
        this.application = application;
    }

    /**
     * Processes the selection of a new tool.
     * 
     * @param tool the tool to process the selection of.
     */
    protected void processToolSelect(ToolType tool) {
        application.getEditStatus().setTool(tool);
        application.getPicture().updateScreen();
    }

    /**
     * Allow the user to go to a position in the picture buffer immediately 
     * without having to use the navigation buttons. The position will be 
     * set to the start of the drawing action that the given position lies 
     * within.
     */
    protected void processEnterPosition() {
        String positionStr = JOptionPane.showInputDialog(application, "Enter a picture position:", "Goto", JOptionPane.QUESTION_MESSAGE);
        if ((positionStr != null) && (!positionStr.trim().equals(""))) {
            try {
                Picture picture = application.getPicture();
                
                // If the entered value is valid, apply the new position.
                LinkedList<PictureCode> pictureCodes = picture.getPictureCodes();
                int newPosition = Integer.parseInt(positionStr.toString());
                if ((newPosition >= 0) && (newPosition < pictureCodes.size())) {
                    if (newPosition < (pictureCodes.size() - 1)) {
                        // Find the closest picture action to the entered position.
                        while (pictureCodes.get(newPosition).getCode() < 0xF0) {
                            newPosition = newPosition - 1;
                        }
                    }
                    picture.setPicturePosition(newPosition);
                    picture.drawPicture();
                    picture.updateScreen();
                }
            } catch (NumberFormatException nfe) {
                // Ignore. The user has entered a non-numeric value.
            }
        }
    }

    /**
     * Toggles the display of the priority screen.
     */
    public void processTogglePriorityScreen() {
        EditStatus editStatus = application.getEditStatus();
        editStatus.toggleScreen();
        editStatus.setTool(ToolType.NONE);
        application.getPicture().updateScreen();
    }

    /**
     * Processes toggling of the display of the background tracking image.
     */
    protected void processToggleBackground() {
        EditStatus editStatus = application.getEditStatus();
        editStatus.setBackgroundEnabled(!editStatus.isBackgroundEnabled());
        application.getMenu().getBackgroundMenuItem().setSelected(editStatus.isBackgroundEnabled());
    }

    /**
     * Disables editing until the user has pressed a key or clicked a mouse button.
     * 
     * @param callback a task to perform once the key stroke or mouse click has happened.
     */
    protected void waitForKeyStrokeOrMouseClick(final Runnable callback) {
        final EditStatus editStatus = application.getEditStatus();
        final Picture picture = application.getPicture();
        
        editStatus.setPaused(true);

        // Wrap each listener in an array to get around the inner class restrictions.
        final KeyListener[] keyListener = new KeyListener[1];
        final MouseListener[] mouseListener = new MouseListener[1];

        // Register a temporary key and mouse listener to wait for the key/mouse event. 
        keyListener[0] = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                application.removeKeyListener(keyListener[0]);
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
                application.removeKeyListener(keyListener[0]);
                application.getPicturePanel().removeMouseListener(mouseListener[0]);
                if (callback != null) {
                    callback.run();
                } else {
                    picture.updateScreen();
                    editStatus.setPaused(false);
                }
            }
        };
        application.addKeyListener(keyListener[0]);
        application.getPicturePanel().addMouseListener(mouseListener[0]);

        // The listeners are registered at this point. The method exits. The listeners
        // take care of the rest. They will unregister themselves once they've been
        // triggered.
    }

    /**
     * Displays the 'About' PICEDIT message box.
     */
    protected void showAboutMessage() {
        JOptionPane.showMessageDialog(application, 
                "<html><h2 style=\"text-align: center\">PICEDIT v1.3M6</h2><br/><p style=\"text-align: center\">by Lance Ewing</p><br/></html>", 
                "About PICEDIT", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the help message.
     */
    protected void showHelp() {
        if (helpFrame == null) {
            helpFrame  = new JFrame("PICEDIT Help");
            helpFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    helpFrame = null;
                }
            });
            JEditorPane helpEditorPane = new JEditorPane();
            helpEditorPane.setEditable(false);
            java.net.URL helpURL = ClassLoader.getSystemResource("com/agifans/picedit/help/help.html");
            try {
                helpEditorPane.setPage(helpURL);
            } catch (IOException e) {
            }
            JScrollPane helpScrollPane = new JScrollPane(helpEditorPane);
            helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            helpScrollPane.setPreferredSize(application.getPreferredSize());
            helpScrollPane.setMinimumSize(new Dimension(10, 10));
            helpFrame.setLayout(new BorderLayout());
            helpFrame.getContentPane().add(helpScrollPane, BorderLayout.CENTER);
            helpFrame.pack();
            helpFrame.setLocationRelativeTo(null);
            helpFrame.setResizable(true);
            helpFrame.setVisible(true);
        } else {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    helpFrame.toFront();
                    helpFrame.repaint();
                    helpFrame.requestFocus();
                }
            });
        }
    }

    /**
     * Loads a background image from the given File.
     * 
     * @param imageFile the image File to load for the background image.
     */
    protected void loadBackgroundImage(File imageFile) {
        EditStatus editStatus = application.getEditStatus();
        PicGraphics picGraphics = application.getPicGraphics();
        
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
     * Creates a new picture frame on the desktop.
     */
    protected void newPicture() {
        JDesktopPane desktop = application.getDesktopPane();

        // Work out the next number to use for the default Untitled picture name.
        int maximumUntitledFrameNum = 0;
        for (JInternalFrame frame : desktop.getAllFrames()) {
            PictureFrame pictureFrame = (PictureFrame)frame;
            // Untitled picture frames are those without a picture file associated.
            if (pictureFrame.getEditStatus().getPictureFile() == null) {
                String frameTitle = pictureFrame.getTitle();
                if (frameTitle.contains("Untitled") && (!frameTitle.endsWith("Untitled"))) {
                    int untitledFrameNum = Integer.parseInt(frameTitle.substring(frameTitle.indexOf("Untitled") + 8));
                    if (untitledFrameNum > maximumUntitledFrameNum) {
                        maximumUntitledFrameNum = untitledFrameNum;
                    }
                }
            }
        }
        String defaultPictureName = "Untitled" + (maximumUntitledFrameNum + 1);
        
        // Now create the new PictureFrame.
        PictureFrame newPictureFrame = new PictureFrame(application, application.getEditStatus().getZoomFactor(), defaultPictureName);
        int initialFrameIndent = 20 + (desktop.getAllFrames().length * 25);
        newPictureFrame.setLocation(initialFrameIndent, initialFrameIndent);
        
        // Add to the desktop, start up the mouse motion timer and then autoselect.
        desktop.add(newPictureFrame);
        newPictureFrame.getMouseHandler().startMouseMotionTimer();
        try {
            newPictureFrame.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
}
