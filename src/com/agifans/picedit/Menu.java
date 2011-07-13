package com.agifans.picedit;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
     * Constructor for Menu.
     * 
     * @param editStatus the EditStatus object holding the current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param application the PICEDIT application component (used for opening dialogs)
     */
    public Menu(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
        super(editStatus, picGraphics, picture, application);
        
        // Set up a single JFileChooser for use with all open and save dialogs. This
        // allows the directory to be remembered between uses. Default to the current
        // directory.
        this.fileChooser = new JFileChooser(editStatus.getLastUsedDirectory());
        
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
     * Creates and configures the menu items used by PICEDIT.
     */
    private void createMenuItems() {
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
        JMenuItem saveMenuItem = new JMenuItem(MenuOption.SAVE.getDisplayValue(), KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, acceleratorKey));
        JMenuItem saveAsMenuItem = new JMenuItem(MenuOption.SAVE_AS.getDisplayValue(), KeyEvent.VK_A);
        JMenuItem quitMenuItem = new JMenuItem(MenuOption.EXIT.getDisplayValue(), KeyEvent.VK_X);
        newMenuItem.addActionListener(this);
        loadMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        saveAsMenuItem.addActionListener(this);
        quitMenuItem.addActionListener(this);
        fileMenu.add(newMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);
        fileMenu.addMenuListener(this);
        menuBar.add(fileMenu);
        
        // Create the Edit menu.
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.setEnabled(false);
        editMenu.addMenuListener(this);
        menuBar.add(editMenu);
        
        // Create the View menu.
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        JMenuItem viewDataMenuItem = new JMenuItem(MenuOption.VIEW_DATA.getDisplayValue(), KeyEvent.VK_V);
        JMenuItem zoomx2MenuItem = new JMenuItem(MenuOption.ZOOM_X2.getDisplayValue());
        JMenuItem zoomx3MenuItem = new JMenuItem(MenuOption.ZOOM_X3.getDisplayValue());
        JMenuItem zoomx4MenuItem = new JMenuItem(MenuOption.ZOOM_X4.getDisplayValue());
        JMenuItem zoomx5MenuItem = new JMenuItem(MenuOption.ZOOM_X5.getDisplayValue());
        viewDataMenuItem.addActionListener(this);
        zoomx2MenuItem.addActionListener(this);
        zoomx3MenuItem.addActionListener(this);
        zoomx4MenuItem.addActionListener(this);
        zoomx5MenuItem.addActionListener(this);
        viewMenu.add(viewDataMenuItem);
        viewMenu.addSeparator();
        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setMnemonic(KeyEvent.VK_Z);
        zoomMenu.add(zoomx2MenuItem);
        zoomMenu.add(zoomx3MenuItem);
        zoomMenu.add(zoomx4MenuItem);
        zoomMenu.add(zoomx5MenuItem);
        zoomMenu.addMenuListener(this);
        viewMenu.add(zoomMenu);
        viewMenu.addMenuListener(this);
        menuBar.add(viewMenu);
        
        // Create the Special menu.
        JMenu specialMenu = new JMenu("Special");
        specialMenu.setMnemonic(KeyEvent.VK_S);
        JMenuItem backgroundMenuItem = new JCheckBoxMenuItem(MenuOption.BACKGROUND.getDisplayValue());
        backgroundMenuItem.setMnemonic(KeyEvent.VK_G);
        backgroundMenuItem.setSelected(editStatus.isBackgroundEnabled());
        JMenuItem bandsMenuItem = new JCheckBoxMenuItem(MenuOption.BANDS.getDisplayValue());
        bandsMenuItem.setMnemonic(KeyEvent.VK_B);
        bandsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, acceleratorKey));
        bandsMenuItem.setSelected(editStatus.isBandsOn());
        JMenuItem dualModeMenuItem = new JCheckBoxMenuItem(MenuOption.DUAL_MODE.getDisplayValue());
        dualModeMenuItem.setMnemonic(KeyEvent.VK_D);
        dualModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, acceleratorKey));
        dualModeMenuItem.setSelected(editStatus.isDualModeEnabled());
        backgroundMenuItem.addActionListener(this);
        bandsMenuItem.addActionListener(this);
        dualModeMenuItem.addActionListener(this);
        specialMenu.add(backgroundMenuItem);
        specialMenu.add(bandsMenuItem);
        specialMenu.add(dualModeMenuItem);
        specialMenu.addMenuListener(this);
        menuBar.add(specialMenu);

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
        editStatus.setMenuActive(true);
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
        
        switch (menuOption) {
            case NEW:
                Object[] newOptions = { "New", "Cancel" };
                int newAnswer = JOptionPane.showOptionDialog(application, "Are you sure you want to create a new picture?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, newOptions, newOptions[0]);
                if (newAnswer == JOptionPane.YES_OPTION) {
                    editStatus.clear();
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
                if ((editStatus.getPictureName() == null) || MenuOption.SAVE_AS.equals(menuOption)) {
                    if (fileChooser.showSaveDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile != null) {
                            savePicture(selectedFile);
                        }
                    }
                } else {
                    File selectedFile = new File(fileChooser.getCurrentDirectory(), editStatus.getPictureName());
                    savePicture(selectedFile);
                }
                break;

            case EXIT:
                Object[] quitOptions = { "Quit", "Cancel" };
                int quitAnswer = JOptionPane.showOptionDialog(application, "Are you sure you want to Quit?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, quitOptions, quitOptions[0]);
                if (quitAnswer == JOptionPane.YES_OPTION) {
                    editStatus.savePreferences();
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
                if (editStatus.isBackgroundEnabled()) {
                    processToggleBackground();
                    picGraphics.setBackgroundImage(null);
                } else {
                    if (fileChooser.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile != null) {
                            loadBackgroundImage(selectedFile);
                        }
                    }
                    success = editStatus.isBackgroundEnabled();
                }
                break;

            case BANDS:
                editStatus.setBandsOn(!editStatus.isBandsOn());
                break;

            case DUAL_MODE:
                editStatus.setDualModeEnabled(!editStatus.isDualModeEnabled());
                break;
        }
        
        // Store the current directory in the edit status so it is saved in preferences.
        editStatus.setLastUsedDirectory(fileChooser.getCurrentDirectory().getAbsolutePath());
        
        return success;
    }
}
