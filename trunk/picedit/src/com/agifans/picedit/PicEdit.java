package com.agifans.picedit;

import java.awt.*;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.*;

import com.agifans.picedit.gui.Menu;
import com.agifans.picedit.gui.PicEditDesktopManager;
import com.agifans.picedit.gui.PictureFrame;
import com.agifans.picedit.gui.PicturePanel;
import com.agifans.picedit.gui.StatusBarPanel;
import com.agifans.picedit.gui.ToolPanel;
import com.agifans.picedit.gui.ToolPanelLocation;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.PicGraphics;
import com.agifans.picedit.picture.Picture;

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
    private static final String PICEDIT_NAME = "PICEDIT v1.3M6";
    
    /**
     * The most recently active picture window.
     */
    private PictureFrame activePictureFrame;
    
    /**
     * The desktop pane that the picture frames live in.
     */
    private JDesktopPane desktopPane;
    
    /**
     * The PICEDIT menu handler.
     */
    private Menu menu;
    
    /**
     * PicEdit application preferences.
     */
    private Preferences prefs;
    
    /**
     * The most recently opened or saved pictures.
     */
    private LinkedList<String> recentPictures;
    
    /**
     * The name of the most recently used directory.
     */
    private String lastUsedDirectory;
    
    /**
     * Where the tool panel currently is. Starts on the left.
     */
    private ToolPanelLocation toolPanelLocation;
    
    /**
     * Constructor for PicEdit.
     */
    @SuppressWarnings("unchecked")
    public PicEdit() {
        // Load the preferences saved the last time the application was closed down.
        loadPreferences();
        
        this.activePictureFrame = new PictureFrame(this, prefs.getInt("ZOOM_FACTOR", 3), "Untitled");
        this.activePictureFrame.setLocation(20, 20);
        try {
            this.activePictureFrame.setSelected(true);
        } catch (PropertyVetoException e) {
        }
        
        // This allows us to use TAB in the application (default within Java is that it traverses between fields).
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        
        this.getContentPane().setLayout(new BorderLayout());
        
        // Add the status bar above the picture.
        StatusBarPanel statusbar = new StatusBarPanel(this);
        statusbar.setPreferredSize(new Dimension(320, 20));
        this.getContentPane().add(statusbar, BorderLayout.SOUTH);
        
        // Add the desktop pane that holds the picture that is being edited.
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(0x4169AA));
        desktopPane.setDesktopManager(new PicEditDesktopManager(desktopPane.getDesktopManager()));
        desktopPane.add(activePictureFrame);
        desktopPane.setFocusable(false);
        desktopPane.setPreferredSize(new Dimension(700, 440));
        desktopPane.addMouseWheelListener(new MouseWheelListener() {
          public void mouseWheelMoved(MouseWheelEvent event) {
        	  getPictureFrame().getMouseHandler().mouseWheelMoved(event);
        	}
        });
        this.getContentPane().add(desktopPane, BorderLayout.CENTER);
        
        // Tool panel.
        ToolPanelLocation toolPanelLocation = getToolPanelLocation();
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
                break;
        }
        
        // Starts timer that injects mouse motion events.
        this.activePictureFrame.getMouseHandler().startMouseMotionTimer();
        
        // Create the menu and register the menu event listeners.
        this.menu = new Menu(this);
    }

    /**
     * Loads and applies the user preferences related to the Edit Status.
     */
    public void loadPreferences() {
        prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.lastUsedDirectory = prefs.get("LAST_USED_DIRECTORY", new File(".").getAbsolutePath());
        
        this.recentPictures = new LinkedList<String>();
        this.recentPictures.add(0, prefs.get("RECENT_PICTURE_1", ""));
        this.recentPictures.add(1,prefs.get("RECENT_PICTURE_2", ""));
        this.recentPictures.add(2,prefs.get("RECENT_PICTURE_3", ""));
        this.recentPictures.add(3,prefs.get("RECENT_PICTURE_4", ""));
        
        this.toolPanelLocation = ToolPanelLocation.valueOf(prefs.get("TOOL_PANEL_LOCATION", "DOCKED_LEFT"));
    }
    
    /**
     * Saves the user preferences related to the Edit Status.
     */
    public void savePreferences() {
        prefs.put("LAST_USED_DIRECTORY", this.lastUsedDirectory);
        prefs.putInt("ZOOM_FACTOR", getEditStatus().getZoomFactor());
        prefs.put("RECENT_PICTURE_1", this.recentPictures.get(0));
        prefs.put("RECENT_PICTURE_2", this.recentPictures.get(1));
        prefs.put("RECENT_PICTURE_3", this.recentPictures.get(2));
        prefs.put("RECENT_PICTURE_4", this.recentPictures.get(3));
        prefs.put("TOOL_PANEL_LOCATION", this.toolPanelLocation.name());
    }
    
    /**
     * Gets the list of recently opened pictures.
     * 
     * @return The list of recently opened pictures.
     */
    public LinkedList<String> getRecentPictures() {
        return recentPictures;
    }
    
    /**
     * Updates the list of recently loaded or saved pictures.
     * 
     * @param pictureFile The File to add to the list of recent pictures.
     */
    public void updateRecentPictures(File pictureFile) {
        // Rotate the recent picture name list.
        if (recentPictures.contains(pictureFile.getAbsolutePath())) {
          // If the list already contains this file, then remove it.
          recentPictures.remove(pictureFile.getAbsolutePath());
        } else {
          // Otherwise remove the last item.
          recentPictures.removeLast();
        }
        
        // The most recent is always added as the first item.
        recentPictures.add(0, pictureFile.getAbsolutePath());
    }
    
    public String getLastUsedDirectory() {
        return this.lastUsedDirectory;
    }
    
    public void setLastUsedDirectory(String lastUsedDirectory) {
        this.lastUsedDirectory = lastUsedDirectory;
    }
    
    /**
     * Gets the current tool panel location.
     * 
     * @return The current tool panel location.
     */
    public ToolPanelLocation getToolPanelLocation() {
      return toolPanelLocation;
    }

    /**
     * Sets the current tool panel location.
     * 
     * @param toolPanelLocation The current tool panel location.
     */
    public void setToolPanelLocation(ToolPanelLocation toolPanelLocation) {
      this.toolPanelLocation = toolPanelLocation;
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
    
    /**
     * Gets the currently active PictureFrame.
     * 
     * @return The currently active PictureFrame.
     */
    public PictureFrame getPictureFrame() {
        PictureFrame selectedFrame = (PictureFrame)this.desktopPane.getSelectedFrame();
        if (selectedFrame != null) {
            activePictureFrame = selectedFrame;
        }
        
        return activePictureFrame;
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
                app.savePreferences();
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
