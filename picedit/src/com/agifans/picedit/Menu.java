package com.agifans.picedit;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * This class manages the menu system.
 * 
 * @author Lance Ewing
 */
public class Menu extends CommonHandler {

    /**
     * The currently highlighted menu option.
     */
    private MenuOption menuOption;

    /**
     * The previously highlighted menu option.
     */
    private MenuOption lastMenuOption;

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
            // Show background image (for overdrawing).
            if (barOption != oldBarOption) {
                picGraphics.drawFilledBox(120, 8, 223, 40, 15);
                picGraphics.drawBox(122, 9, 221, 39, 0);
                picGraphics.drawBox(123, 9, 220, 39, 0);
            }
            picGraphics.drawString("Background", 136, 16, 0, 15);
            if (editStatus.isBackgroundEnabled()) {
                picGraphics.drawChar((char) 7, 126, 16, 4, 15);
            }
            picGraphics.drawString("Bands", 136, 24, 0, 15);
            if (editStatus.isBandsOn()) {
                picGraphics.drawChar((char) 7, 126, 24, 4, 15);
            }
            switch (itemOption) {
            case 0:
                picGraphics.drawString("Background", 136, 16, 15, 0);
                break;
            case 1:
                picGraphics.drawString("Bands", 136, 24, 15, 0);
                break;
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
     * Processes the selection of a menu item.
     * 
     * @param menuOption the selected MenuOption to process.
     */
    private void processMenuSelection(MenuOption menuOption) {
        if (menuOption.equals(MenuOption.NEWPIC)) {
            Object[] options = { "New", "Cancel" };
            int answer = JOptionPane.showOptionDialog(application,
                    "Are you sure you want to create a new picture?", "",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            if (answer == JOptionPane.YES_OPTION) {
                editStatus.clear();
                picture.drawPicture();
                picture.updateScreen();
                picGraphics.setBackgroundImage(null);
            }
        }
        if (menuOption.equals(MenuOption.LOADPIC)) {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                if (selectedFile != null) {
                    loadPicture(selectedFile);
                }
            }
        }
        if (menuOption.equals(MenuOption.SAVEPIC)) {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                if (selectedFile != null) {
                    savePicture(selectedFile);
                }
            }
        }
        if (menuOption.equals(MenuOption.QUIT)) {
            Object[] options = { "Quit", "Cancel" };
            int answer = JOptionPane.showOptionDialog(application,
                    "Are you sure you want to Quit?", "",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            if (answer == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
        if (menuOption.equals(MenuOption.ABOUT)) {
            showAboutMessage();
        }
        if (menuOption.equals(MenuOption.HELP)) {
            showHelp();
        }
        if (menuOption.equals(MenuOption.VIEWDATA)) {
            showHexData();
        }
        if (menuOption.equals(MenuOption.ZOOMX2)) {
            application.resizeScreen(2);
        }
        if (menuOption.equals(MenuOption.ZOOMX3)) {
            application.resizeScreen(3);
        }
        if (menuOption.equals(MenuOption.ZOOMX4)) {
            application.resizeScreen(4);
        }
        if (menuOption.equals(MenuOption.ZOOMX5)) {
            application.resizeScreen(5);
        }
        if (menuOption.equals(MenuOption.BACKGROUND)) {
            if (editStatus.isBackgroundEnabled()) {
                processToggleBackground();
                picGraphics.setBackgroundImage(null);
            } else {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fc.getSelectedFile();
                    if (selectedFile != null) {
                        loadBackgroundImage(selectedFile);
                    }
                }
            }
        }
        if (menuOption.equals(MenuOption.BANDS)) {
            editStatus.setBandsOn(!editStatus.isBandsOn());
        }
    }
}
