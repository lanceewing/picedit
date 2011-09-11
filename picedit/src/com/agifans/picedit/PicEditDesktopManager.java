package com.agifans.picedit;

import java.awt.Dimension;

import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * DesktopManager for managing the internal frames. This is custom 
 * implementation is required to prevent the frames from moving outside
 * of the desktop area. This code partly based on a sample in Oreilly Java
 * Swing book.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class PicEditDesktopManager extends DefaultDesktopManager implements DesktopManager {

    // We'll tag internal frames that are being resized using a client
    // property with the name RESIZING.  Used in setBoundsForFrame().
    private static final String RESIZING = "RESIZING";

    /**
     * The default DesktopManager for this platform.
     */
    private DesktopManager defaultManager;
    
    /**
     * Constructor for PicEditDesktopManager.
     * 
     * @param defaultManager The default DesktopManager for this platform.
     */
    public PicEditDesktopManager(DesktopManager defaultManager) {
        this.defaultManager = defaultManager;
    }
    
    public void beginResizingFrame(JComponent f, int dir) {
        f.putClientProperty(RESIZING, Boolean.TRUE);
    }

    public void endResizingFrame(JComponent f) {
        f.putClientProperty(RESIZING, Boolean.FALSE);
    }

    // This is called any time a frame is moved or resized.  This 
    // implementation keeps the frame from leaving the desktop.
    public void setBoundsForFrame(JComponent f, int x, int y, int w, int h) {
        if (f instanceof JInternalFrame == false) {
            super.setBoundsForFrame(f, x, y, w, h); // only deal w/internal frames
        } else {
            JInternalFrame frame = (JInternalFrame) f;

            // Figure out if we are being resized (otherwise it's just a move)
            boolean resizing = false;
            Object r = frame.getClientProperty(RESIZING);
            if (r != null && r instanceof Boolean) {
                resizing = ((Boolean) r).booleanValue();
            }

            JDesktopPane desk = frame.getDesktopPane();
            Dimension d = desk.getSize();

            // Nothing all that fancy below, just figuring out how to adjust
            // to keep the frame on the desktop.
            if (x < 0) { // too far left?
                if (resizing)
                    w += x; // don't get wider!
                x = 0; // flush against the left side
            } else {
                if (x + w > d.width) { // too far right?
                    if (resizing)
                        w = d.width - x; // don't get wider!
                    else
                        x = d.width - w; // flush against the right side
                }
            }
            if (y < 0) { // too high?
                if (resizing)
                    h += y; // don't get taller!
                y = 0; // flush against the top
            } else {
                if (y + h > d.height) { // too low?
                    if (resizing)
                        h = d.height - y; // don't get taller!
                    else
                        y = d.height - h; // flush against the bottom
                }
            }

            // Set 'em the way we like 'em
            super.setBoundsForFrame(f, x, y, w, h);
        }
    }

    public void activateFrame(JInternalFrame f) {
        defaultManager.activateFrame(f);
    }

    public void beginDraggingFrame(JComponent f) {
        super.beginDraggingFrame(f);
    }

    public void closeFrame(JInternalFrame f) {
        defaultManager.closeFrame(f);
    }

    public void deactivateFrame(JInternalFrame f) {
        defaultManager.deactivateFrame(f);
    }

    public void deiconifyFrame(JInternalFrame f) {
        defaultManager.deiconifyFrame(f);
    }

    public void dragFrame(JComponent f, int newX, int newY) {
        super.dragFrame(f, newX, newY);
    }

    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
    }

    public void iconifyFrame(JInternalFrame f) {
        defaultManager.iconifyFrame(f);
    }

    public void maximizeFrame(JInternalFrame f) {
        defaultManager.maximizeFrame(f);
    }

    public void minimizeFrame(JInternalFrame f) {
        defaultManager.minimizeFrame(f);
    }

    public void openFrame(JInternalFrame f) {
        defaultManager.openFrame(f);
    }

    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        defaultManager.resizeFrame(f, newX, newY, newWidth, newHeight);
    }
}
