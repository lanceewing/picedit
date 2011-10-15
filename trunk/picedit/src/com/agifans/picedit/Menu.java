package com.agifans.picedit;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * This class manages the menu system.
 * 
 * @author Lance Ewing
 */
public class Menu extends CommonHandler implements ActionListener, MenuListener {

    /**
     * File chooser used by all open and save dialogs.
     */
    private JFileChooser fileChooser;
    
    /**
     * The underlying Swing menu bar component.
     */
    private JMenuBar menuBar;
    
    /**
     * The menu item used for toggling display of the background image.
     */
    private JCheckBoxMenuItem backgroundMenuItem;
    
    /**
     * The Open Recent sub-menu item.
     */
    private JMenu openRecentMenu;
    
    /**
     * The View menu.
     */
    private JMenu viewMenu;
    
    /**
     * Constructor for Menu.
     * 
     * @param application the PICEDIT application component (used for opening dialogs)
     */
    public Menu(PicEdit application) {
        super(application);
        
        // Set up a single JFileChooser for use with all open and save dialogs. This
        // allows the directory to be remembered between uses. Default to the current
        // directory.
        this.fileChooser = new JFileChooser(application.getLastUsedDirectory());
        
        createMenuItems();
    }

    /**
     * Gets the underlying Swing menu bar component.
     * 
     * @return The underlying Swing menu bar component.
     */
    public JMenuBar getMenuBar() {
        return this.menuBar;
    }
    
    /**
     * Gets the menu item that toggles the background image.
     * 
     * @return The menu item that toggles the background image.
     */
    public JCheckBoxMenuItem getBackgroundMenuItem() {
        return this.backgroundMenuItem;
    }
    
    /**
     * Creates and configures the menu items used by PICEDIT.
     */
    private void createMenuItems() {
        EditStatus editStatus = application.getEditStatus();
        
        this.menuBar = new JMenuBar();
        
        // Get the shortcut key for this platform (e.g. "cmd" key on the Mac).
        int acceleratorKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        
        // Create the File menu.
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem newMenuItem = new JMenuItem(MenuOption.NEW.getDisplayValue(), KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, acceleratorKey));
        JMenuItem loadMenuItem = new JMenuItem(MenuOption.OPEN.getDisplayValue(), KeyEvent.VK_O);
        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, acceleratorKey));
        openRecentMenu = new JMenu(MenuOption.OPEN_RECENT.getDisplayValue());
        openRecentMenu.addMenuListener(this);
        JMenuItem saveMenuItem = new JMenuItem(MenuOption.SAVE.getDisplayValue(), KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, acceleratorKey));
        JMenuItem saveAsMenuItem = new JMenuItem(MenuOption.SAVE_AS.getDisplayValue(), KeyEvent.VK_A);
        JMenuItem loadBackgroundItem = new JMenuItem(MenuOption.LOAD_BACKGROUND.getDisplayValue(), KeyEvent.VK_B);
        JMenuItem quitMenuItem = new JMenuItem(MenuOption.EXIT.getDisplayValue(), KeyEvent.VK_X);
        newMenuItem.addActionListener(this);
        loadMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        saveAsMenuItem.addActionListener(this);
        loadBackgroundItem.addActionListener(this);
        quitMenuItem.addActionListener(this);
        fileMenu.add(newMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(openRecentMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(loadBackgroundItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);
        fileMenu.addMenuListener(this);
        menuBar.add(fileMenu);
        
        // Create the Edit menu.
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        JMenuItem deleteMenuItem = new JMenuItem(MenuOption.DELETE.getDisplayValue(), KeyEvent.VK_D);
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteMenuItem.addActionListener(this);
        editMenu.add(deleteMenuItem);
        editMenu.addMenuListener(this);
        menuBar.add(editMenu);
        
        // Create the View menu.
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        ButtonGroup screenGroup = new ButtonGroup();
        JRadioButtonMenuItem visualMenuItem = new JRadioButtonMenuItem(MenuOption.VISUAL.getDisplayValue());
        screenGroup.add(visualMenuItem);
        visualMenuItem.setSelected(true);
        JRadioButtonMenuItem priorityMenuItem = new JRadioButtonMenuItem(MenuOption.PRIORITY.getDisplayValue());
        priorityMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        screenGroup.add(priorityMenuItem);
        backgroundMenuItem = new JCheckBoxMenuItem(MenuOption.BACKGROUND.getDisplayValue());
        backgroundMenuItem.setMnemonic(KeyEvent.VK_G);
        backgroundMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        backgroundMenuItem.setSelected(editStatus.isBackgroundEnabled());
        JMenuItem bandsMenuItem = new JCheckBoxMenuItem(MenuOption.BANDS.getDisplayValue());
        bandsMenuItem.setMnemonic(KeyEvent.VK_B);
        bandsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, acceleratorKey));
        bandsMenuItem.setSelected(editStatus.isBandsOn());
        JMenuItem dualModeMenuItem = new JCheckBoxMenuItem(MenuOption.DUAL_MODE.getDisplayValue());
        dualModeMenuItem.setMnemonic(KeyEvent.VK_D);
        dualModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, acceleratorKey));
        dualModeMenuItem.setSelected(editStatus.isDualModeEnabled());
        JMenuItem zoomInMenuItem = new JMenuItem(MenuOption.ZOOM_IN.getDisplayValue());
        zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke('+'));
        JMenuItem zoomOutMenuItem = new JMenuItem(MenuOption.ZOOM_OUT.getDisplayValue());
        zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke('-'));
        JMenuItem zoomx1MenuItem = new JMenuItem(MenuOption.ZOOM_X1.getDisplayValue());
        JMenuItem zoomx2MenuItem = new JMenuItem(MenuOption.ZOOM_X2.getDisplayValue());
        JMenuItem zoomx3MenuItem = new JMenuItem(MenuOption.ZOOM_X3.getDisplayValue());
        JMenuItem zoomx4MenuItem = new JMenuItem(MenuOption.ZOOM_X4.getDisplayValue());
        JMenuItem zoomx5MenuItem = new JMenuItem(MenuOption.ZOOM_X5.getDisplayValue());
        backgroundMenuItem.addActionListener(this);
        visualMenuItem.addActionListener(this);
        priorityMenuItem.addActionListener(this);
        bandsMenuItem.addActionListener(this);
        dualModeMenuItem.addActionListener(this);
        zoomInMenuItem.addActionListener(this);
        zoomOutMenuItem.addActionListener(this);
        zoomx1MenuItem.addActionListener(this);
        zoomx2MenuItem.addActionListener(this);
        zoomx3MenuItem.addActionListener(this);
        zoomx4MenuItem.addActionListener(this);
        zoomx5MenuItem.addActionListener(this);
        viewMenu.add(visualMenuItem);
        viewMenu.add(priorityMenuItem);
        viewMenu.addSeparator();
        viewMenu.add(backgroundMenuItem);
        viewMenu.add(bandsMenuItem);
        viewMenu.add(dualModeMenuItem);
        viewMenu.addSeparator();
        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setMnemonic(KeyEvent.VK_Z);
        zoomMenu.add(zoomx1MenuItem);
        zoomMenu.add(zoomx2MenuItem);
        zoomMenu.add(zoomx3MenuItem);
        zoomMenu.add(zoomx4MenuItem);
        zoomMenu.add(zoomx5MenuItem);
        zoomMenu.addSeparator();
        zoomMenu.add(zoomInMenuItem);
        zoomMenu.add(zoomOutMenuItem);
        zoomMenu.addMenuListener(this);
        viewMenu.add(zoomMenu);
        viewMenu.addMenuListener(this);
        menuBar.add(viewMenu);

        // Create the Navigate menu.
        JMenu navigateMenu = new JMenu("Navigate");
        navigateMenu.setMnemonic(KeyEvent.VK_N);
        JMenuItem startMenuItem = new JMenuItem(MenuOption.START.getDisplayValue(), KeyEvent.VK_H);
        startMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0));
        JMenuItem prevMenuItem = new JMenuItem(MenuOption.PREV.getDisplayValue(), KeyEvent.VK_P);
        prevMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        JMenuItem nextMenuItem = new JMenuItem(MenuOption.NEXT.getDisplayValue(), KeyEvent.VK_N);
        nextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        JMenuItem endMenuItem = new JMenuItem(MenuOption.END.getDisplayValue(), KeyEvent.VK_E);
        endMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0));
        JMenuItem gotoMenuItem = new JMenuItem(MenuOption.GOTO.getDisplayValue(), KeyEvent.VK_G);
        gotoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, acceleratorKey));
        startMenuItem.addActionListener(this);
        prevMenuItem.addActionListener(this);
        nextMenuItem.addActionListener(this);
        endMenuItem.addActionListener(this);
        gotoMenuItem.addActionListener(this);
        navigateMenu.add(startMenuItem);
        navigateMenu.add(prevMenuItem);
        navigateMenu.add(nextMenuItem);
        navigateMenu.add(endMenuItem);
        navigateMenu.addSeparator();
        navigateMenu.add(gotoMenuItem);
        navigateMenu.addMenuListener(this);
        menuBar.add(navigateMenu);
        
        // Create the Tools menu.
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        JMenuItem lineMenuItem = new JMenuItem(MenuOption.LINE.getDisplayValue(), KeyEvent.VK_L);
        lineMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
        JMenuItem penMenuItem = new JMenuItem(MenuOption.PEN.getDisplayValue(), KeyEvent.VK_P);
        penMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        JMenuItem stepMenuItem = new JMenuItem(MenuOption.STEP.getDisplayValue(), KeyEvent.VK_S);
        stepMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
        JMenuItem fillMenuItem = new JMenuItem(MenuOption.FILL.getDisplayValue(), KeyEvent.VK_F);
        fillMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
        JMenuItem brushMenuItem = new JMenuItem(MenuOption.BRUSH.getDisplayValue(), KeyEvent.VK_B);
        brushMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0));
        JMenuItem airbrushMenuItem = new JMenuItem(MenuOption.AIRBRUSH.getDisplayValue(), KeyEvent.VK_A);
        airbrushMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));
        lineMenuItem.addActionListener(this);
        penMenuItem.addActionListener(this);
        stepMenuItem.addActionListener(this);
        fillMenuItem.addActionListener(this);
        brushMenuItem.addActionListener(this);
        airbrushMenuItem.addActionListener(this);
        toolsMenu.add(lineMenuItem);
        toolsMenu.add(penMenuItem);
        toolsMenu.add(stepMenuItem);
        toolsMenu.add(fillMenuItem);
        toolsMenu.add(brushMenuItem);
        toolsMenu.add(airbrushMenuItem);
        toolsMenu.addMenuListener(this);
        menuBar.add(toolsMenu);
        
        // Create the Info menu.
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem helpMenuItem = new JMenuItem(MenuOption.HELP.getDisplayValue(), KeyEvent.VK_H);
        JMenuItem aboutMenuItem = new JMenuItem(MenuOption.ABOUT.getDisplayValue(), KeyEvent.VK_A);
        helpMenuItem.addActionListener(this);
        aboutMenuItem.addActionListener(this);
        helpMenu.add(helpMenuItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutMenuItem);
        helpMenu.addMenuListener(this);
        menuBar.add(helpMenu);
        
        application.setJMenuBar(menuBar);
    }

    /**
     * Invoked when one of the menus is cancelled.
     */
    public void menuCanceled(MenuEvent e) {
    }

    /**
     * Invoked when one of the menus is deselected.
     */
    public void menuDeselected(MenuEvent e) {
    }

    /**
     * Invoked when one of the menus is selected.
     */
    public void menuSelected(MenuEvent e) {
        EditStatus editStatus = application.getEditStatus();
        editStatus.setMenuActive(true);
        if (openRecentMenu.equals(e.getSource())) {
            openRecentMenu.removeAll();
            for (String pictureName : application.getRecentPictures()) {
                if (!pictureName.equals("")) {
                    final File pictureFile = new File(pictureName);
                    JMenuItem pictureMenuItem = new JMenuItem(pictureFile.getName());
                    pictureMenuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            loadPicture(pictureFile);
                        }
                    });
                    openRecentMenu.add(pictureMenuItem);
                }
            }
        }
    }

    /**
     * Processes ActionEvents generated by the JMenuItems. This is done by 
     * converting the event into a MenuOption and then delegating to the 
     * processMenuSelection method.
     * 
     * @param event The ActionEvent to process.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        boolean success = processMenuSelection(MenuOption.getMenuOption(event.getActionCommand()));
        if (!success) {
            // If the selection was not successfully processed, and it was a checkbox
            // menu item, then uncheck it.
            if (event.getSource() instanceof JCheckBoxMenuItem) {
                ((JCheckBoxMenuItem)event.getSource()).setState(false);
            }
        }
        EditStatus editStatus = application.getEditStatus();
        editStatus.setMenuActive(false);
    }

    /**
     * Processes the selection of a menu item.
     * 
     * @param menuOption the selected MenuOption to process.
     * 
     * @return true if the MenuOption was successfully processed.
     */
    private boolean processMenuSelection(MenuOption menuOption) {
        boolean success = true;
        EditStatus editStatus = application.getEditStatus();
        Picture picture = application.getPicture();
        PicGraphics picGraphics = application.getPicGraphics();
        
        switch (menuOption) {
            case NEW:
                Object[] newOptions = { "New", "Cancel" };
                int newAnswer = JOptionPane.showOptionDialog(application, "Are you sure you want to create a new picture?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, newOptions, newOptions[0]);
                if (newAnswer == JOptionPane.YES_OPTION) {
                    editStatus.clear();
                    picture.clearPicture();
                    picture.drawPicture();
                    picture.updateScreen();
                    picGraphics.setBackgroundImage(null);
                }
                break;

            case OPEN:
                if (fileChooser.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        loadPicture(selectedFile);
                    }
                }
                break;

            case SAVE_AS:
            case SAVE:
                if ((editStatus.getPictureFile() == null) || MenuOption.SAVE_AS.equals(menuOption)) {
                    if (fileChooser.showSaveDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile != null) {
                            savePicture(selectedFile);
                        }
                    }
                } else {
                    savePicture(editStatus.getPictureFile());
                }
                break;

            case LOAD_BACKGROUND:
                if (fileChooser.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        loadBackgroundImage(selectedFile);
                    }
                }
                success = editStatus.isBackgroundEnabled();
                backgroundMenuItem.setSelected(success);
                break;
                
            case EXIT:
                Object[] quitOptions = { "Quit", "Cancel" };
                int quitAnswer = JOptionPane.showOptionDialog(application, "Are you sure you want to Quit?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, quitOptions, quitOptions[0]);
                if (quitAnswer == JOptionPane.YES_OPTION) {
                    application.savePreferences();
                    System.exit(0);
                }
                break;

            case ABOUT:
                showAboutMessage();
                break;

            case HELP:
                showHelp();
                break;

            case VIEW_DATA:
                showHexData();
                break;
            
            case ZOOM_IN:
                if (editStatus.getZoomFactor() < 5) {
                    application.resizeScreen(editStatus.getZoomFactor() + 1);
                }
                break;
                
            case ZOOM_OUT:
                if (editStatus.getZoomFactor() > 1) {
                    application.resizeScreen(editStatus.getZoomFactor() - 1);
                }
                break;
                
            case ZOOM_X1:
                application.resizeScreen(1);
                break;

            case ZOOM_X2:
                application.resizeScreen(2);
                break;

            case ZOOM_X3:
                application.resizeScreen(3);
                break;

            case ZOOM_X4:
                application.resizeScreen(4);
                break;

            case ZOOM_X5:
                application.resizeScreen(5);
                break;

            case BACKGROUND:
                processToggleBackground();
                break;

            case BANDS:
                editStatus.setBandsOn(!editStatus.isBandsOn());
                break;

            case DUAL_MODE:
                editStatus.setDualModeEnabled(!editStatus.isDualModeEnabled());
                break;
                
            case LINE:
                processToolSelect(ToolType.LINE);
                break;
                
            case PEN:
                processToolSelect(ToolType.SHORTLINE);
                break;
                
            case STEP:
                processToolSelect(ToolType.STEPLINE);
                break;
            
            case FILL:
                processToolSelect(ToolType.FILL);
                break;
            
            case BRUSH:
                processToolSelect(ToolType.BRUSH);
                application.getEditStatus().setBrushShape(BrushShape.CIRCLE);
                application.getEditStatus().setBrushTexture(BrushTexture.SOLID);
                application.getEditStatus().setBrushSize(0);
                break;
                
            case AIRBRUSH:
                processToolSelect(ToolType.AIRBRUSH);
                application.getEditStatus().setBrushShape(BrushShape.CIRCLE);
                application.getEditStatus().setBrushTexture(BrushTexture.SPRAY);
                application.getEditStatus().setBrushSize(0);
                break;
            
            case START:
                picture.moveToStartOfPictureBuffer();
                break;
                
            case PREV:
                picture.moveBackOnePictureAction();
                break;
                
            case NEXT:
                picture.moveForwardOnePictureAction();
                break;
                
            case END:
                picture.moveToEndOfPictureBuffer();
                break;
                
            case GOTO:
                processEnterPosition();
                break;
                
            case DELETE:
                picture.deleteCurrentPictureAction();
                break;
                
            case VISUAL:
                if (editStatus.isPriorityShowing()) {
                    processTogglePriorityScreen();
                    recreateScreenMenuItems();
                }
                break;
                
            case PRIORITY:
                if (!editStatus.isPriorityShowing()) {
                    processTogglePriorityScreen();
                    recreateScreenMenuItems();
                }
                break;
        }
        
        // Store the current directory in the edit status so it is saved in preferences.
        application.setLastUsedDirectory(fileChooser.getCurrentDirectory().getAbsolutePath());
        
        return success;
    }
    
    /**
     * Recreates the menu items for selecting the picture screen (visual/priority).
     */
    private void recreateScreenMenuItems() {
        EditStatus editStatus = application.getEditStatus();
        
        // Remove the previously created menu items.
        viewMenu.remove(0);
        viewMenu.remove(0);
        
        // Create the new menu items for the visual and priority screen selection.
        JRadioButtonMenuItem visualMenuItem = new JRadioButtonMenuItem(MenuOption.VISUAL.getDisplayValue());
        JRadioButtonMenuItem priorityMenuItem = new JRadioButtonMenuItem(MenuOption.PRIORITY.getDisplayValue());
        if (!editStatus.isPriorityShowing()) {
            visualMenuItem.setSelected(true);
            priorityMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        } else {
            priorityMenuItem.setSelected(true);
            visualMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        }
        ButtonGroup screenGroup = new ButtonGroup();
        screenGroup.add(visualMenuItem);
        screenGroup.add(priorityMenuItem);
        visualMenuItem.addActionListener(this);
        priorityMenuItem.addActionListener(this);
        
        // Add the new menu items to the View menu.
        viewMenu.add(visualMenuItem, 0);
        viewMenu.add(priorityMenuItem, 1);
        
        // Make sure that the menu is redrawn, just in case.
        viewMenu.revalidate();
        viewMenu.repaint();
    }
}
