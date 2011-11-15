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
import java.util.Timer;
import java.util.prefs.Preferences;

import javax.swing.*;

import com.agifans.picedit.gui.PicEditDesktopManager;
import com.agifans.picedit.gui.PictureCodeList;
import com.agifans.picedit.gui.PictureList;
import com.agifans.picedit.gui.StatusBarPanel;
import com.agifans.picedit.gui.frame.PictureFrame;
import com.agifans.picedit.gui.frame.PicturePanel;
import com.agifans.picedit.gui.menu.Menu;
import com.agifans.picedit.gui.toolbar.ToolPanel;
import com.agifans.picedit.gui.toolbar.ToolPanelLocation;
import com.agifans.picedit.picture.EditStatus;
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
     * The scroll pane for the currently active picture code JList.
     */
    private JScrollPane pictureCodeScrollPane;
    
    /**
     * The tool panel containing all of the picture drawing tools.
     */
    private ToolPanel toolPanel;
    
    /**
     * The status bar at the bottom of the PICEDIT screen.
     */
    private StatusBarPanel statusBarPanel;
    
    /**
     * Constructor for PicEdit.
     */
    @SuppressWarnings("unchecked")
    public PicEdit() {
        // Load the preferences saved the last time the application was closed down.
        loadPreferences();
        
        PictureList pictureList = new PictureList(this);
        
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
        statusBarPanel = new StatusBarPanel(this);
        statusBarPanel.setPreferredSize(new Dimension(320, 20));
        this.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
        
        JPanel desktopPanel = new JPanel();
        desktopPanel.setLayout(new BorderLayout());
        
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
        desktopPanel.add(desktopPane, BorderLayout.CENTER);
        
        // Tool panel.
        ToolPanelLocation toolPanelLocation = getToolPanelLocation();
        toolPanel = new ToolPanel(this);
        switch (toolPanelLocation) {
            case DOCKED_LEFT:
                desktopPanel.add(toolPanel, BorderLayout.WEST);
                break;
            case DOCKED_RIGHT:
                desktopPanel.add(toolPanel, BorderLayout.EAST);
                break;
            case DOCKED_TOP:
                toolPanel.setOrientation(JToolBar.HORIZONTAL);
                desktopPanel.add(toolPanel, BorderLayout.NORTH);
                break;
            case FLOATING:
                // TODO: Not sure if this one is possible, so might need to default to WEST.
                desktopPanel.add(toolPanel, BorderLayout.NORTH);
                break;
        }
        
        JTabbedPane leftTabbedPane = new JTabbedPane();
        leftTabbedPane.setFocusable(false);
        pictureCodeScrollPane = new JScrollPane(activePictureFrame.getPictureCodeList());
        pictureCodeScrollPane.setFocusable(false);
        //leftTabbedPane.add("Pictures", pictureListScrollPane);
        leftTabbedPane.add("Commands", pictureCodeScrollPane);
        
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftTabbedPane, desktopPanel);
        centerSplitPane.setFocusable(false);
        centerSplitPane.setDividerLocation(175);
        
        this.getContentPane().add(centerSplitPane, BorderLayout.CENTER);
        
        // Create the menu and register the menu event listeners.
        this.menu = new Menu(this);
        
        // Start a timer to preform regular screen repaints.
        startRepaintTimer();
    }

    /**
     * Starts a timer to trigger regular screen repaints. In addition to 
     * this, it also checks if the currently selected PictureFrame needs
     * to process a mouse motion event. Performing this check at the same
     * time as the screen repaint appears to be more reliable than receiving 
     * mouse motion events to a registered listener for some reason, and it
     * has the added benefit of tracking the mouse when it is outside of the
     * PICEDIT window.
     */
    public void startRepaintTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                // Check if selected PictureFrame needs to process mouse motion.
                PictureFrame selectedPictureFrame = (PictureFrame)getDesktopPane().getSelectedFrame();
                if (selectedPictureFrame != null) {
                    selectedPictureFrame.getMouseHandler().checkForMouseMotion();
                }
                
                // Repaints the changing parts of the PICEDIT screen 25 times a second.
                getPictureFrame().repaint();
                toolPanel.repaint();
                statusBarPanel.repaint();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 40, 40);
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
        
        this.toolPanelLocation = ToolPanelLocation.valueOf(prefs.get("TOOL_PANEL_LOCATION", "DOCKED_TOP"));
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
    
    /**
     * Gets the last used directory.
     * 
     * @return The last used directory.
     */
    public String getLastUsedDirectory() {
        return this.lastUsedDirectory;
    }
    
    /**
     * Sets the last used directory.
     * 
     * @param lastUsedDirectory The last used directory.
     */
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
     * Returns true if the application has at least one visible picture frame.
     * 
     * @return true if the application has at least one visible picture frame.
     */
    public boolean hasVisiblePictureFrame() {
      if (desktopPane != null) {
        return (desktopPane.getAllFrames().length > 0);
      } else {
        return false;
      }
    }
    
    /**
     * Switches to the next picture frame on the desktop. This would normally be invoked when the
     * previously active picture frame is closed thereby automatically activating the next
     * picture frame.
     */
    public void selectNextPictureFrame() {
        JInternalFrame[] allPictureFrames = desktopPane.getAllFrames();
        if ((allPictureFrames == null) || (allPictureFrames.length == 0)) {
            // If there are no frames currently on the desktop then we create a blank one for 
            // the purposes of resetting the tool bar, status bar and picture code list.
            activePictureFrame = new PictureFrame(this, prefs.getInt("ZOOM_FACTOR", 3), "Untitled");
            switchPictureCodeList();
            
        } else {
            // Otherwise proceed with selecting the next picture frame.
            desktopPane.selectFrame(true);
        }
    }
    
    /**
     * Switches the PictureCodeList currently being displayed with the one for the current picture.
     */
    public void switchPictureCodeList() {
        PictureCodeList activeList = this.getPictureFrame().getPictureCodeList();
        pictureCodeScrollPane.setViewportView(activeList);
        pictureCodeScrollPane.repaint();
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
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
      
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.getPictureFrame().resizeForZoomFactor(app.getEditStatus().getZoomFactor());
            }
        });
    }
}
