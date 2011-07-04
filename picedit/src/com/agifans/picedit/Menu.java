package com.agifans.picedit;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

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
     * The currently highlighted menu option.
     */
    private MenuOption menuOption;

    /**
     * The previously highlighted menu option.
     */
    private MenuOption lastMenuOption;

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
        JMenuItem aboutMenuItem = new JMenuItem("About");
        JMenuItem helpMenuItem = new JMenuItem("Help");
        aboutMenuItem.addActionListener(this);
        helpMenuItem.addActionListener(this);
        infoMenu.add(aboutMenuItem);
        infoMenu.add(helpMenuItem);
        menuBar.add(infoMenu);
        
        // Create the File menu.
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New Picture");
        JMenuItem loadMenuItem = new JMenuItem("Load Picture");
        JMenuItem saveMenuItem = new JMenuItem("Save Picture");
        JMenuItem quitMenuItem = new JMenuItem("Quit");
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
        JMenuItem viewDataMenuItem = new JMenuItem("View Data");
        JMenuItem zoomx2MenuItem = new JMenuItem("Zoom x2");
        JMenuItem zoomx3MenuItem = new JMenuItem("Zoom x3");
        JMenuItem zoomx4MenuItem = new JMenuItem("Zoom x4");
        JMenuItem zoomx5MenuItem = new JMenuItem("Zoom x5");
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
        JMenuItem backgroundMenuItem = new JMenuItem("Background");
        JMenuItem bandsMenuItem = new JMenuItem("Bands");
        JMenuItem dualModeMenuItem = new JMenuItem("Dual Mode");
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
     * Draws the menu bar and the currently selected drop down and menu item.
     */
    public void drawMenuBar() {
        // Make sure we always have an option to work with.
        if (menuOption == null) {
            menuOption = MenuOption.ABOUT;
        }

        int barOption = menuOption.getBarOption();
        int oldBarOption = (lastMenuOption != null ? lastMenuOption.getBarOption() : -1);
        int itemOption = menuOption.getItemOption();

        // If the top level menu category item has changed then redraw the whole
        // menu bar.
        if (oldBarOption != barOption) {
            picGraphics.drawString(" Info File View Special                 ", 0, 0, 0, 15);
            picGraphics.drawLine(0, 8, 319, 8, 1);
            picture.updateScreen();
        }

        // Draw the selected menu drop down and item.
        switch (barOption) {
            case 0:
                picGraphics.drawString("Info", 8, 0, 15, 0);
                if (barOption != oldBarOption) {
                    picGraphics.drawFilledBox(0, 8, 55, 39, 15);
                    picGraphics.drawBox(2, 9, 53, 38, 0);
                    picGraphics.drawBox(3, 9, 52, 38, 0);
                }
                picGraphics.drawString("About", 8, 16, 0, 15);
                picGraphics.drawString("Help", 8, 24, 0, 15);
                switch (itemOption) {
                    case 0:
                        picGraphics.drawString("About", 8, 16, 15, 0);
                        break;
                    case 1:
                        picGraphics.drawString("Help", 8, 24, 15, 0);
                        break;
                }
                break;
            case 1:
                picGraphics.drawString("File", 48, 0, 15, 0);
                if (barOption != oldBarOption) {
                    picGraphics.drawFilledBox(40, 8, 151, 55, 15);
                    picGraphics.drawBox(42, 9, 149, 54, 0);
                    picGraphics.drawBox(43, 9, 148, 54, 0);
                }
                picGraphics.drawString("New Picture", 48, 16, 0, 15);
                picGraphics.drawString("Save Picture", 48, 24, 0, 15);
                picGraphics.drawString("Load Picture", 48, 32, 0, 15);
                picGraphics.drawString("Quit", 48, 40, 0, 15);
                switch (itemOption) {
                    case 0:
                        picGraphics.drawString("New Picture", 48, 16, 15, 0);
                        break;
                    case 1:
                        picGraphics.drawString("Save Picture", 48, 24, 15, 0);
                        break;
                    case 2:
                        picGraphics.drawString("Load Picture", 48, 32, 15, 0);
                        break;
                    case 3:
                        picGraphics.drawString("Quit", 48, 40, 15, 0);
                        break;
                }
                break;
            case 2:
                picGraphics.drawString("View", 88, 0, 15, 0);
                // View data option.
                if (barOption != oldBarOption) {
                    picGraphics.drawFilledBox(80, 8, 167, 64, 15);
                    picGraphics.drawBox(82, 9, 165, 63, 0);
                    picGraphics.drawBox(83, 9, 164, 63, 0);
                }
                picGraphics.drawString("View Data", 88, 16, 0, 15);
                picGraphics.drawString("Zoom x2", 88, 24, 0, 15);
                picGraphics.drawString("Zoom x3", 88, 32, 0, 15);
                picGraphics.drawString("Zoom x4", 88, 40, 0, 15);
                picGraphics.drawString("Zoom x5", 88, 48, 0, 15);
                switch (itemOption) {
                    case 0:
                        picGraphics.drawString("View Data", 88, 16, 15, 0);
                        break;
                    case 1:
                        picGraphics.drawString("Zoom x2", 88, 24, 15, 0);
                        break;
                    case 2:
                        picGraphics.drawString("Zoom x3", 88, 32, 15, 0);
                        break;
                    case 3:
                        picGraphics.drawString("Zoom x4", 88, 40, 15, 0);
                        break;
                    case 4:
                        picGraphics.drawString("Zoom x5", 88, 48, 15, 0);
                        break;
                }
                break;
            case 3:
                picGraphics.drawString("Special", 128, 0, 15, 0);
                if (barOption != oldBarOption) {
                    picGraphics.drawFilledBox(120, 8, 223, 48, 15);
                    picGraphics.drawBox(122, 9, 221, 47, 0);
                    picGraphics.drawBox(123, 9, 220, 47, 0);
                }
                picGraphics.drawString("Background", 136, 16, 0, 15);
                if (editStatus.isBackgroundEnabled()) {
                    picGraphics.drawChar((char) 7, 126, 16, 4, 15);
                }
                picGraphics.drawString("Bands", 136, 24, 0, 15);
                if (editStatus.isBandsOn()) {
                    picGraphics.drawChar((char) 7, 126, 24, 4, 15);
                }
                picGraphics.drawString("Dual Mode", 136, 32, 0, 15);
                if (editStatus.isDualModeEnabled()) {
                    picGraphics.drawChar((char) 7, 126, 32, 4, 15);
                }
                switch (itemOption) {
                    case 0:
                        picGraphics.drawString("Background", 136, 16, 15, 0);
                        break;
                    case 1:
                        picGraphics.drawString("Bands", 136, 24, 15, 0);
                        break;
                    case 2:
                        picGraphics.drawString("Dual Mode", 136, 32, 15, 0);
                }
                break;
        }

        lastMenuOption = menuOption;
    }

    /**
     * Closes the menu system.
     */
    public void closeMenuSystem() {
        menuOption = null;
        lastMenuOption = null;
        editStatus.setMenuActive(false);
        editStatus.clearLastRenderedState();
        picture.updateScreen();
        picGraphics.drawLine(0, 8, 319, 8, 1);
    }

    /**
     * Processes a key event in the context of the menu system being active,
     * which handles such things as moving around with the arrows keys, selected
     * an option with the ENTER key or ESCaping out of the menu system.
     * 
     * @param e the KeyEvent to process.
     */
    public void processKeyEvent(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            if (menuOption == null) {
                // This is the ESC that activated the menu, so start with ABOUT
                // selected.
                menuOption = MenuOption.ABOUT;
                editStatus.setMenuActive(true);
            } else {
                // Clear selected menu option.
                closeMenuSystem();
                return;
            }

        } else {
            int menuBarOption = menuOption.getBarOption();
            int menuItemOption = menuOption.getItemOption();
            int maxItem = menuOption.getMaxItem();

            // Process the menu navigation using the arrow keys.
            if (key == KeyEvent.VK_UP) {
                menuItemOption--;
                if (menuItemOption < 0) {
                    menuItemOption = maxItem - 1;
                }
            } else if (key == KeyEvent.VK_LEFT) {
                menuBarOption--;
                if (menuBarOption < 0) {
                    menuBarOption = 3;
                }
                menuItemOption = 0;
            } else if (key == KeyEvent.VK_RIGHT) {
                menuBarOption++;
                if (menuBarOption > 3) {
                    menuBarOption = 0;
                }
                menuItemOption = 0;
            } else if (key == KeyEvent.VK_DOWN) {
                menuItemOption++;
                if (menuItemOption == maxItem) {
                    menuItemOption = 0;
                }
            } else if (key == KeyEvent.VK_ENTER) {
                // Close menu system before responding to selection.
                closeMenuSystem();
                processMenuSelection(MenuOption.getMenuOption(menuBarOption, menuItemOption));
                return;
            }

            // Update the menu selection based on key event.
            menuOption = MenuOption.getMenuOption(menuBarOption, menuItemOption);
        }

        // Redraw the menu.
        drawMenuBar();
    }

    /**
     * Processes a mouse event in the context of the menu system being active.
     * 
     * @param eventType the type of mouse event.
     * @param mousePoint the position of the mouse event.
     */
    public void processMouseEvent(int eventType, Point mousePoint) {
        // Use the mouse position to work out menu option to select.
        MenuOption newMenuOption = MenuOption.getMenuOption(mousePoint, lastMenuOption);
        if (newMenuOption != null) {
            menuOption = newMenuOption;
        }

        // Redraw the menu.
        drawMenuBar();

        if (eventType == MouseEvent.MOUSE_PRESSED) {
            if (!editStatus.isMenuActive()) {
                // If the menu isn't yet active then this is the mouse click
                // that should activate the menu system.
                editStatus.setMenuActive(true);
            } else {
                // Close menu system before responding to selection.
                closeMenuSystem();

                // If an option was clicked on then process the menu selection.
                if ((newMenuOption != null) && newMenuOption.getItemBox().contains(mousePoint)) {
                    processMenuSelection(newMenuOption);
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
        processMenuSelection(MenuOption.getMenuOption(event.getActionCommand()));
    }

    /**
     * Processes the selection of a menu item.
     * 
     * @param menuOption the selected MenuOption to process.
     */
    private void processMenuSelection(MenuOption menuOption) {
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
                }
                break;

            case BANDS:
                editStatus.setBandsOn(!editStatus.isBandsOn());
                break;

            case DUAL_MODE:
                editStatus.setDualModeEnabled(!editStatus.isDualModeEnabled());
                break;
        }
    }
}
