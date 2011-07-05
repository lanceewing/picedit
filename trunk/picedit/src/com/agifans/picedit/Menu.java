package com.agifans.picedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * This class manages the menu system.
 * 
 * @author Lance Ewing
 */
public class Menu extends CommonHandler implements ActionListener {

    /**
     * File chooser used by all open and save dialogs.
     */
    private JFileChooser fileChooser;
    
    /**
     * Constructor for Menu.
     * 
     * @param editStatus the EditStatus object holding the current picture editor state.
     * @param picGraphics the PicGraphics object providing custom graphics API for PICEDIT.
     * @param picture the AGI PICTURE currently being edited.
     * @param application the PICEDIT application component (used for opening dialogs)
     */
    public Menu(EditStatus editStatus, PicGraphics picGraphics, Picture picture, PicEdit application) {
        super(editStatus, picGraphics, picture, null, application);
        
        // Set up a single JFileChooser for use with all open and save dialogs. This
        // allows the directory to be remembered between uses. Default to the current
        // directory.
        this.fileChooser = new JFileChooser(new File(".").getAbsolutePath());
        
        createMenuItems();
    }

    /**
     * Creates and configures the menu items used by PICEDIT.
     */
    private void createMenuItems() {
        JMenuBar menuBar = new JMenuBar();
        
        // Create the Info menu.
        JMenu infoMenu = new JMenu("Info");
        JMenuItem aboutMenuItem = new JMenuItem(MenuOption.ABOUT.getDisplayValue());
        JMenuItem helpMenuItem = new JMenuItem(MenuOption.HELP.getDisplayValue());
        aboutMenuItem.addActionListener(this);
        helpMenuItem.addActionListener(this);
        infoMenu.add(aboutMenuItem);
        infoMenu.add(helpMenuItem);
        menuBar.add(infoMenu);
        
        // Create the File menu.
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem(MenuOption.NEW_PICTURE.getDisplayValue());
        JMenuItem loadMenuItem = new JMenuItem(MenuOption.LOAD_PICTURE.getDisplayValue());
        JMenuItem saveMenuItem = new JMenuItem(MenuOption.SAVE_PICTURE.getDisplayValue());
        JMenuItem quitMenuItem = new JMenuItem(MenuOption.QUIT.getDisplayValue());
        newMenuItem.addActionListener(this);
        loadMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        quitMenuItem.addActionListener(this);
        fileMenu.add(newMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);
        menuBar.add(fileMenu);
        
        // Create the View menu.
        JMenu viewMenu = new JMenu("View");
        JMenuItem viewDataMenuItem = new JMenuItem(MenuOption.VIEW_DATA.getDisplayValue());
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
        viewMenu.add(zoomx2MenuItem);
        viewMenu.add(zoomx3MenuItem);
        viewMenu.add(zoomx4MenuItem);
        viewMenu.add(zoomx5MenuItem);
        menuBar.add(viewMenu);
        
        // Create the Special menu.
        JMenu specialMenu = new JMenu("Special");
        JMenuItem backgroundMenuItem = new JCheckBoxMenuItem(MenuOption.BACKGROUND.getDisplayValue());
        JMenuItem bandsMenuItem = new JCheckBoxMenuItem(MenuOption.BANDS.getDisplayValue());
        JMenuItem dualModeMenuItem = new JCheckBoxMenuItem(MenuOption.DUAL_MODE.getDisplayValue());
        backgroundMenuItem.addActionListener(this);
        bandsMenuItem.addActionListener(this);
        dualModeMenuItem.addActionListener(this);
        specialMenu.add(backgroundMenuItem);
        specialMenu.add(bandsMenuItem);
        specialMenu.add(dualModeMenuItem);
        menuBar.add(specialMenu);
        
        application.setJMenuBar(menuBar);
    }

    /**
     * Closes the menu system.
     */
    public void closeMenuSystem() {
        editStatus.setMenuActive(false);
        editStatus.clearLastRenderedState();
        picture.updateScreen();
        picGraphics.drawLine(0, 8, 319, 8, 1);
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
            case NEW_PICTURE:
                Object[] newOptions = { "New", "Cancel" };
                int newAnswer = JOptionPane.showOptionDialog(application, "Are you sure you want to create a new picture?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, newOptions, newOptions[0]);
                if (newAnswer == JOptionPane.YES_OPTION) {
                    editStatus.clear();
                    picture.drawPicture();
                    picture.updateScreen();
                    picGraphics.setBackgroundImage(null);
                }
                break;

            case LOAD_PICTURE:
                if (fileChooser.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        loadPicture(selectedFile);
                    }
                }
                break;

            case SAVE_PICTURE:
                if (fileChooser.showSaveDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        savePicture(selectedFile);
                    }
                }
                break;

            case QUIT:
                Object[] quitOptions = { "Quit", "Cancel" };
                int quitAnswer = JOptionPane.showOptionDialog(application, "Are you sure you want to Quit?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, quitOptions, quitOptions[0]);
                if (quitAnswer == JOptionPane.YES_OPTION) {
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
        
        return success;
    }
}
