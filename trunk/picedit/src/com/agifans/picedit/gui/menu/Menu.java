package com.agifans.picedit.gui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.gui.frame.PictureFrame;
import com.agifans.picedit.gui.frame.PicturePanel;
import com.agifans.picedit.picture.EditStatus;
import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.picture.PictureCode;
import com.agifans.picedit.types.BrushShape;
import com.agifans.picedit.types.BrushTexture;
import com.agifans.picedit.types.ToolType;

/**
 * This class manages the menu system.
 * 
 * @author Lance Ewing
 */
public class Menu implements ActionListener, MenuListener {

    /**
     * The PICEDIT application component.
     */
    protected PicEdit application;
    
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
     * The menu item used for toggling dual mode.
     */
    private JCheckBoxMenuItem dualModeMenuItem;
    
    /**
     * The menu item used for toggling priority bands display.
     */
    private JCheckBoxMenuItem bandsMenuItem;
    
    /**
     * The menu item used for toggling ego test mode.
     */
    private JCheckBoxMenuItem egoTestMenuItem;
    
    /**
     * The Open Recent sub-menu item.
     */
    private JMenu openRecentMenu;
    
    /**
     * The View menu.
     */
    private JMenu viewMenu;
    
    /**
     * The help window.
     */
    private JFrame helpFrame;
    
    /**
     * Constructor for Menu.
     * 
     * @param application the PICEDIT application component (used for opening dialogs)
     */
    public Menu(PicEdit application) {
        this.application = application;
        
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
        bandsMenuItem = new JCheckBoxMenuItem(MenuOption.BANDS.getDisplayValue());
        bandsMenuItem.setMnemonic(KeyEvent.VK_B);
        bandsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, acceleratorKey));
        bandsMenuItem.setSelected(editStatus.isBandsOn());
        dualModeMenuItem = new JCheckBoxMenuItem(MenuOption.DUAL_MODE.getDisplayValue());
        dualModeMenuItem.setMnemonic(KeyEvent.VK_D);
        dualModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, acceleratorKey));
        dualModeMenuItem.setSelected(editStatus.isDualModeEnabled());
        egoTestMenuItem = new JCheckBoxMenuItem(MenuOption.EGO_TEST.getDisplayValue());
        egoTestMenuItem.setMnemonic(KeyEvent.VK_E);
        egoTestMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, acceleratorKey));
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
        egoTestMenuItem.addActionListener(this);
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
        viewMenu.add(egoTestMenuItem);
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
                            if (application.getPicture().getPictureCodes().size() > 1) {
                                // If the picture is not empty then launch a new picture frame.
                                newPicture();
                            } else {
                                // Otherwise reuse the old frame. We need to clear off the background image
                                // since the EditStatus clear call doesn't handle this.
                                application.getPicturePanel().setBackgroundImage(null);
                            }
                            application.getPicture().loadPicture(pictureFile);
                            application.updateRecentPictures(pictureFile);
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
        
        // If a menu option is selected, we'll always clear the temporary line.
        application.getPicturePanel().clearTemporaryLine();
        
        switch (menuOption) {
            case NEW:
                newPicture();
                break;

            case OPEN:
                if (fileChooser.showOpenDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        if (application.getPicture().getPictureCodes().size() > 1) {
                            // If the picture is not empty then launch a new picture frame.
                            newPicture();
                        } else {
                            // Otherwise reuse the old frame. We need to clear off the background image
                            // since the EditStatus clear call doesn't handle this.
                            application.getPicturePanel().setBackgroundImage(null);
                        }
                        application.getPicture().loadPicture(selectedFile);
                        application.updateRecentPictures(selectedFile);
                    }
                }
                break;

            case SAVE_AS:
            case SAVE:
                if ((editStatus.getPictureFile() == null) || MenuOption.SAVE_AS.equals(menuOption)) {
                    if (fileChooser.showSaveDialog(this.application) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile != null) {
                            application.getPicture().savePicture(selectedFile);
                            application.updateRecentPictures(selectedFile);
                        }
                    }
                } else {
                    application.getPicture().savePicture(editStatus.getPictureFile());
                    application.updateRecentPictures(editStatus.getPictureFile());
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
                
            case EGO_TEST:
                editStatus.setEgoTestEnabled(!editStatus.isEgoTestEnabled());
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
     * Processes the selection of a new tool.
     * 
     * @param tool the tool to process the selection of.
     */
    protected void processToolSelect(ToolType tool) {
        application.getPicturePanel().clearTemporaryLine();
        application.getPictureFrame().getPositionSlider().setEnabled(true);
        application.getEditStatus().setTool(tool);
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
                    application.getPicturePanel().clearTemporaryLine();
                    picture.setPicturePosition(newPosition);
                    picture.drawPicture();
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
        application.getPicturePanel().clearTemporaryLine();
        application.getPictureFrame().getPositionSlider().setEnabled(true);
        EditStatus editStatus = application.getEditStatus();
        editStatus.toggleScreen();
        editStatus.setTool(ToolType.NONE);
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
        PicturePanel picturePanel = application.getPicturePanel();
        
        try {
            Image image = ImageIO.read(imageFile);
            if (image != null) {
                picturePanel.setBackgroundImage(image);
                editStatus.setBackgroundEnabled(true);
            } else {
                picturePanel.setBackgroundImage(null);
                editStatus.setBackgroundEnabled(false);
            }
        } catch (IOException e) {
            picturePanel.setBackgroundImage(null);
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
                if (frameTitle.contains("Untitled")) {
                    int untitledFrameNum = 1;
                    if (!frameTitle.endsWith("Untitled")) {
                        untitledFrameNum = Integer.parseInt(frameTitle.substring(frameTitle.indexOf("Untitled") + 9));
                        if (untitledFrameNum > maximumUntitledFrameNum) {
                            maximumUntitledFrameNum = untitledFrameNum;
                        }
                    }
                    if (untitledFrameNum > maximumUntitledFrameNum) {
                        maximumUntitledFrameNum = untitledFrameNum;
                    }
                }
            }
        }
        StringBuilder defaultPictureName = new StringBuilder("Untitled");
        if (maximumUntitledFrameNum > 0) {
            defaultPictureName.append(" ");
            defaultPictureName.append(maximumUntitledFrameNum + 1);
        }
        
        // Now create the new PictureFrame.
        PictureFrame newPictureFrame = new PictureFrame(application, application.getEditStatus().getZoomFactor(), defaultPictureName.toString());
        int initialFrameIndent = 20 + (desktop.getAllFrames().length * 25);
        newPictureFrame.setLocation(initialFrameIndent, initialFrameIndent);
        
        // Add to the desktop, start up the mouse motion timer and then autoselect.
        desktop.add(newPictureFrame);
        try {
            newPictureFrame.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    /**
     * Updates the menu items in the View menu to reflect the active picture's edit 
     * status. This would usually be called when a new picture frame is selected so 
     * that the state of the menu reflects the active pictures status.
     */
    public void updateViewMenuItems() {
        // Start with the visual/priority screen selection. Reuse the method we're using for toggle.
        recreateScreenMenuItems();
        
        // Now update the various mode check boxes to reflect the appropriate state.
        backgroundMenuItem.setSelected(application.getEditStatus().isBackgroundEnabled());
        dualModeMenuItem.setSelected(application.getEditStatus().isDualModeEnabled());
        bandsMenuItem.setSelected(application.getEditStatus().isBandsOn());
        egoTestMenuItem.setSelected(application.getEditStatus().isEgoTestEnabled());
        
        // Make sure that the menu is redrawn, just in case.
        viewMenu.revalidate();
        viewMenu.repaint();
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
