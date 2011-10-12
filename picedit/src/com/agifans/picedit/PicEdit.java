package com.agifans.picedit;

import java.awt.*;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import javax.swing.*;

/**
 * The main class for the PICEDIT application.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public final class PicEdit extends JApplet {

    /**
     * Constant for the name of the current version of PICEDIT.
     */
    private static final String PICEDIT_NAME = "PICEDIT v1.3M5";
    
    /**
     * The most recently active picture window.
     */
    private PictureFrame pictureFrame;
    
    /**
     * The desktop pane that the picture frames live in.
     */
    private JDesktopPane desktopPane;
    
    /**
     * The PICEDIT menu handler.
     */
    private Menu menu;
    
    /**
     * Constructor for PicEdit.
     */
    @SuppressWarnings("unchecked")
    public PicEdit() {
        this.pictureFrame = new PictureFrame(this);
        this.pictureFrame.setLocation(20, 20);

        // This allows us to use TAB in the application (default within Java is that it traverses between fields).
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        
        // Create the menu and register the menu event listeners.
        this.menu = new Menu(pictureFrame.getEditStatus(), pictureFrame.getPicGraphics(), pictureFrame.getPicture(), this);
        
        this.getContentPane().setLayout(new BorderLayout());
        
        // Add the status bar above the picture.
        StatusBarPanel statusbar = new StatusBarPanel(this);
        statusbar.setPreferredSize(new Dimension(320, 20));
        this.getContentPane().add(statusbar, BorderLayout.SOUTH);
        
        // Add the desktop pane that holds the picture that is being edited.
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(0x4169AA));
        desktopPane.setDesktopManager(new PicEditDesktopManager(desktopPane.getDesktopManager()));
        desktopPane.add(pictureFrame);
        desktopPane.setFocusable(false);
        desktopPane.setPreferredSize(new Dimension(700, 440));
        desktopPane.addMouseWheelListener(new MouseWheelListener() {
          public void mouseWheelMoved(MouseWheelEvent event) {
        	  getPictureFrame().getMouseHandler().mouseWheelMoved(event);
        	}
        });
        this.getContentPane().add(desktopPane, BorderLayout.CENTER);
        
        // Tool panel.
        ToolPanelLocation toolPanelLocation = getEditStatus().getToolPanelLocation();
        ToolPanel toolPanel = new ToolPanel(this);
        switch (toolPanelLocation) {
            case DOCKED_LEFT:
            	this.getContentPane().add(toolPanel, BorderLayout.WEST);
                break;
            case DOCKED_RIGHT:
            	this.getContentPane().add(toolPanel, BorderLayout.EAST);
            	break;
            case DOCKED_TOP:
            	toolPanel.setOrientation(JToolBar.HORIZONTAL);
            	this.getContentPane().add(toolPanel, BorderLayout.NORTH);
            	break;
            case FLOATING:
            	// TODO: Not sure if this one is possible, so might need to default to WEST.
            	this.getContentPane().add(toolPanel, BorderLayout.WEST);
            	//((BasicToolbarUI)toolPanel.getUI()).setFloating(true, new Point(x,y));
                break;
        }
    }

    /**
     * Gets the desktop pane that the picture frames live within.
     * 
     * @return The desktop pane that the picture frames live within.
     */
    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
    
    /**
     * Gets the Picture that is currently being edited with PicEdit.
     * 
     * @return The Picture that is currently being edited.
     */
    public Picture getPicture() {
        return getPictureFrame().getPicture();
    }
    
    /**
     * Gets the PicGraphics associated with the currently selected PictureFrame.
     * 
     * @return The PicGraphics associated with the currently selected PictureFrame.
     */
    public PicGraphics getPicGraphics() {
        return getPictureFrame().getPicGraphics();
    }
    
    /**
     * Gets the EditStatus for this PicEdit application. This object contains the
     * editing state of everything within PicEdit.
     * 
     * @return The EditStatus.
     */
    public EditStatus getEditStatus() {
        return getPictureFrame().getEditStatus();
    }
    
    /**
     * Returns the Menu used by PICEDIT.
     * 
     * @return The Menu used by PICEDIT.
     */
    public Menu getMenu() {
        return menu;
    }
    
    /**
     * Returns the panel that holds the picture.
     * 
     * @return The panel that holds the picture.
     */
    public PicturePanel getPicturePanel() {
        return getPictureFrame().getPicturePanel();
    }
    
    public PictureFrame getPictureFrame() {
        // TODO: Change this to get the active frame from the desktop and fall back on previous value if it returns null.
        return pictureFrame;
    }
    
    /**
     * Resizes the screen according to the new zoom factor.
     * 
     * @param zoomFactor the new zoom factor.
     */
    public void resizeScreen(int zoomFactor) {
        getPictureFrame().resizeForZoomFactor(zoomFactor);
    }

    /**
     * Paints the PICEDIT applet.
     */
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    /**
     * Run PICEDIT as a standalone Java application.
     */
    public static void main(String[] args) {
        try {
          //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }
        //JDialog.setDefaultLookAndFeelDecorated(true);
        //JFrame.setDefaultLookAndFeelDecorated(false);
      
        final PicEdit app = new PicEdit();
        JFrame frame = new JFrame();
        frame.setTitle(PICEDIT_NAME);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                app.getEditStatus().savePreferences();
            }
        });
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Register a listener for when the window is resizing so that we can resize the 
        // internal frames. The HierarchyBoundsListener gets events while it is resizing,
        // whereas the ComponentListener only gets them after the resize.
        frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsAdapter(){
            @Override
            public void ancestorResized(HierarchyEvent e) {
                // Start by checking that the internal frame will fit and adjust if required.
                PictureFrame pictureFrame = app.getPictureFrame();
                Dimension desktopSize = app.getDesktopPane().getSize();
                int newWidth = Math.min(desktopSize.width, pictureFrame.getWidth());
                int newHeight = Math.min(desktopSize.height, pictureFrame.getHeight());
                pictureFrame.setSize(new Dimension(newWidth, newHeight));
                
                // Now check to see if we need to move it to keep within the viewable area.
                int newTop = pictureFrame.getY();
                int newLeft = pictureFrame.getX();
                if ((pictureFrame.getX() + pictureFrame.getWidth()) > desktopSize.width) {
                    newLeft = desktopSize.width - pictureFrame.getWidth();
                }
                if ((pictureFrame.getY() + pictureFrame.getHeight()) > desktopSize.height) {
                    newTop = desktopSize.height - pictureFrame.getHeight();
                }
                if ((newLeft != pictureFrame.getY()) || (newTop != pictureFrame.getX())) {
                    pictureFrame.setLocation(new Point(newLeft, newTop));
                }
            }           
        });
        
        app.requestFocusInWindow();
        app.getDesktopPane().selectFrame(true);
    }
}
