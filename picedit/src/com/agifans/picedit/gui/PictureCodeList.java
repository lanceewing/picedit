package com.agifans.picedit.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.agifans.picedit.picture.Picture;
import com.agifans.picedit.picture.PictureCodeType;
import com.agifans.picedit.picture.PictureChangeListener;
import com.agifans.picedit.picture.PictureCode;
import com.agifans.picedit.types.BrushType;
import com.agifans.picedit.utils.EgaPalette;

/**
 * The JList that holds the human readable list of picture codes for the currently 
 * selected Picture.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class PictureCodeList extends JList implements PictureChangeListener, ChangeListener, ListSelectionListener {

    /**
     * The Picture whose picture codes will be displayed in this JList.
     */
    private Picture picture;
    
    /**
     * Provides direct access to the model.
     */
    private PictureCodeListModel pictureCodeListModel;
    
    /**
     * Is true if the picture codes are being added to or removed from; otherwise false.
     */
    private boolean pictureCodesAreAdjusting;
    
    /**
     * The popup menu to display when someone right clicks on a PictureCodeList item.
     */
    private PictureCodeListPopupMenu popupMenu;
    
    /**
     * Constructor for PictureCodeList.
     * 
     * @param picture The Picture whose picture codes will be displayed in this JList.
     */
    public PictureCodeList(Picture picture) {
        this.picture = picture;
        this.pictureCodeListModel = new PictureCodeListModel();
        this.setModel(pictureCodeListModel);
        this.setFont(new Font("Courier New", Font.BOLD, 10));
        this.setForeground(Color.BLACK);
        this.setFocusable(false);
        this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.addListSelectionListener(this);
        
        FontMetrics metrics = this.getFontMetrics(this.getFont());
        this.setCellRenderer(new TextCellRenderer(metrics, 130));
        
        // These two settings make the JList SIGNIFICANTLY faster when adding new picture
        // codes within the middle of the list.
        this.setPrototypeCellValue("Start");
        this.setFixedCellWidth(130);
        
        // Set up the popup menu.
        popupMenu = new PictureCodeListPopupMenu();
        this.addMouseListener(new PictureCodeListMouseListener());
    }
    
    /**
     * Popup menu that appears over the picture code list when the right mouse button is clicked.
     */
    class PictureCodeListPopupMenu extends JPopupMenu {
        
        private JMenuItem deleteMenuItem;
        
        /**
         * Constructor for PictureCodeListPopupMenu.
         */
        PictureCodeListPopupMenu() {
            deleteMenuItem = new JMenuItem("Delete");
            deleteMenuItem.addActionListener(new PictureCodeListPopupMenuActionListener());
            add(deleteMenuItem);
        }
        
        /**
         * Refreshes the state of the menu items.
         */
        public void refreshState() {
            boolean deleteEnabledStatus = !picture.getCurrentPictureCode().getType().equals(PictureCodeType.COLOR_DATA);
            // TODO: Add more checks for delete enabled status, e.g. cannot delete if it would create a corrupt picture.
            deleteMenuItem.setEnabled(deleteEnabledStatus);
        }
    }
    
    /**
     * ListModel for the PictureCodeList JList component that holds the human readable picture
     * codes for the currently selected Picture.
     */
    class PictureCodeListModel extends AbstractListModel implements PictureChangeListener {

        /**
         * Gets a human readable form for the picture code item at the given index.
         * 
         * @param index The index at which to retrieve the PictureCode.
         * 
         * @return The human readable form of the requested PictureCode.
         */
        public Object getElementAt(int index) {
            if (index == 0) {
                return "Start";
            }
            
            LinkedList<PictureCode> pictureCodes = picture.getPictureCodes();
            PictureCode pictureCode = pictureCodes.get(index - 1);
            PictureCode previousPictureCode = null;
            
            String displayText = null;
            if (pictureCode.isActionCode()) {
                PictureCodeType actionCodeType = pictureCode.getType();
                StringBuilder displayTextBuf = new StringBuilder("  ");
                displayTextBuf.append(actionCodeType.getDisplayableText());
                displayText = displayTextBuf.toString();
            } else {
                StringBuilder displayTextBuf = null;
                int code = pictureCode.getCode();
                switch (pictureCode.getType()) {
                    case FILL_POINT_DATA:
                        displayTextBuf = new StringBuilder("    Fill ");
                        displayTextBuf.append((code & 0xFF00) >> 8);
                        displayTextBuf.append(" ");
                        displayTextBuf.append(code & 0x00FF);
                        displayText = displayTextBuf.toString();
                        break;
                    case BRUSH_POINT_DATA:
                        displayTextBuf = new StringBuilder("    Plot ");
                        displayTextBuf.append((code & 0xFF00) >> 8);
                        displayTextBuf.append(" ");
                        displayTextBuf.append(code & 0x00FF);
                        displayText = displayTextBuf.toString();
                        break;
                    case ABSOLUTE_POINT_DATA:
                        previousPictureCode = pictureCodes.get(index  - 2);
                        if (previousPictureCode.isActionCode()) {
                            displayTextBuf = new StringBuilder("    MoveTo ");
                        } else {
                            displayTextBuf = new StringBuilder("    LineTo ");
                        }
                        displayTextBuf.append((code & 0xFF00) >> 8);
                        displayTextBuf.append(" ");
                        displayTextBuf.append(code & 0x00FF);
                        displayText = displayTextBuf.toString();
                        break;
                    case RELATIVE_POINT_DATA:
                        int dx = ((code & 0xF0) >> 4) & 0x0F;
                        int dy = (code & 0x0F);
                        if ((dx & 0x08) > 0) {
                            dx = (-1) * (dx & 0x07);
                        }
                        if ((dy & 0x08) > 0) {
                            dy = (-1) * (dy & 0x07);
                        }
                        StringBuilder displayTextBuilder = new StringBuilder();
                        displayTextBuilder.append("    LineTo ");
                        if (dx >= 0) {
                          displayTextBuilder.append("+");
                        }
                        displayTextBuilder.append(dx);
                        displayTextBuilder.append(" ");
                        if (dy >= 0) {
                          displayTextBuilder.append("+");
                        }
                        displayTextBuilder.append(dy);
                        displayText = displayTextBuilder.toString();
                        break;
                    case X_POSITION_DATA:
                        displayTextBuf = new StringBuilder("    LineTo ");
                        displayTextBuf.append(code);
                        displayTextBuf.append(" +0");
                        displayText = displayTextBuf.toString();
                        break;
                    case Y_POSITION_DATA:
                        displayTextBuf = new StringBuilder("    LineTo +0 ");
                        displayTextBuf.append(code);
                        displayText = displayTextBuf.toString();
                        break;
                    case BRUSH_PATTERN_DATA:
                        displayTextBuf = new StringBuilder("    SetPattern ");
                        displayTextBuf.append(code);
                        displayText = displayTextBuf.toString();
                        break;
                    case BRUSH_TYPE_DATA:
                        displayTextBuf = new StringBuilder("    ");
                        displayTextBuf.append(BrushType.getBrushTypeForBrushCode(pictureCode.getCode()).getDisplayName());
                        displayText = displayTextBuf.toString();
                        break;
                    case COLOR_DATA:
                        displayTextBuf = new StringBuilder("    ");
                        displayTextBuf.append(EgaPalette.COLOR_NAMES[pictureCode.getCode()]);
                        displayText = displayTextBuf.toString();
                        break;
                    case END:
                        displayText = "End";
                        break;
                }
            }
            
            return displayText;
        }

        /**
         * Gets the number of items in the picture code list.
         * 
         * @return The number of items in the picture code list.
         */
        public int getSize() {
            int listSize = picture.getPictureCodes().size() + 1;
            return listSize;
        }
        
        /**
         * Completely refreshes the JList content by firing an event to say that every item has 
         * changed. This will cause the value of every item to be re-read. 
         */
        public void refreshList() {
            fireContentsChanged(this, 0, getSize());
        }

        /**
         * Fires an interval added event to update the JList content to include the newly added items.
         * 
         * @param fromIndex The index from which the new items were added.
         * @param toIndex The index to which the new items were added.
         */
        public void pictureCodesAdded(int fromIndex, int toIndex) {
            fireIntervalAdded(this, fromIndex, toIndex);
        }

        /**
         * Fires an interval removed event to update the JList content to exclude the removed items.
         * 
         * @param fromIndex The index from which the items were removed.
         * @param toIndex The index to which the items were removed.
         */
        public void pictureCodesRemoved(int fromIndex, int toIndex) {
            fireIntervalRemoved(this, fromIndex, toIndex);
        }

        /**
         * The List Model is not interested in this event.
         */
        public void selectionIntervalCollapsed() {
        }
    }

    /**
     * Completely refreshes the JList content.
     */
    public void refreshList() {
        pictureCodeListModel.refreshList();
    }
    
    /**
     * Invoked when picture codes are added to the Picture. Delegates to the PictureCodeListModel.
     */
    public void pictureCodesAdded(int fromIndex, int toIndex) {
        pictureCodesAreAdjusting = true;
        pictureCodeListModel.pictureCodesAdded(fromIndex, toIndex);
        pictureCodesAreAdjusting = false;
    }

    /**
     * Invoked when picture codes are removed from the Picture. Delegates to the PictureCodeListModel.
     */
    public void pictureCodesRemoved(int fromIndex, int toIndex) {
        pictureCodesAreAdjusting = true;
        pictureCodeListModel.pictureCodesRemoved(fromIndex, toIndex);
        pictureCodesAreAdjusting = false;
    }

    /**
     * Invoked when the picture has forced a collapse of the selection interval.
     */
    public void selectionIntervalCollapsed() {
        setSelectedIndex(getMaxSelectionIndex());
    }

    /**
     * Invoked when the position slider value changes. Keeps the picture code list selected index in sync.
     */
    public void stateChanged(ChangeEvent e) {
        int pictureIndex = picture.getPicturePosition() + 1;
        if (pictureIndex != getMaxSelectionIndex()) {
            setSelectedIndex(pictureIndex);
        }
    }

    /**
     * Invoked when the user selects something on the picture code JList.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !pictureCodesAreAdjusting) {
            int selectedIndex = this.getMinSelectionIndex();
            if (selectedIndex > 0) {
                int selectedPicturePosition = selectedIndex - 1;
                
                // If an action code is selected in isolation then auto-select the associated data codes.
                List<PictureCode> pictureCodes = picture.getPictureCodes();
                PictureCode pictureCode = pictureCodes.get(selectedPicturePosition);
                if (pictureCode.isActionCode() && (getMinSelectionIndex() == getMaxSelectionIndex())) {
                    // Find the end of the data codes.
                    int position = selectedPicturePosition + 1;
                    do {
                        pictureCode = pictureCodes.get(position++);
                    } while ((pictureCode != null) && pictureCode.isDataCode());
                    
                    // Only if there is at least one data code do we auto-select them.
                    int dataCodeCount = ((position - selectedIndex) - 1); 
                    if (dataCodeCount > 0) {
                        setSelectionInterval(selectedIndex, position - 1);
                        return;
                    }
                }
                
                // This check is so that we don't redraw picture if picture is already at the position.
                if (selectedPicturePosition != picture.getPicturePosition()) {
                    picture.setPicturePosition(selectedPicturePosition);
                    picture.drawPicture();
                }
                
                // Auto-scroll the JList to show the selected picture code if it isn't visible.
                int minSelectionIndex = getMinSelectionIndex();
                if (minSelectionIndex < getFirstVisibleIndex() || minSelectionIndex > getLastVisibleIndex()) {
                    int numOfVisibleItems = (getLastVisibleIndex() - getFirstVisibleIndex()) - 1;
                    int topIndex = minSelectionIndex;
                    int bottomIndex = Math.min(minSelectionIndex + numOfVisibleItems, picture.getPictureCodes().size() + 1);
                    scrollRectToVisible(getCellBounds(topIndex, bottomIndex));
                }
                
            } else {
                // This takes care of moving the selection off the Start item. We don't want that
                // to be selectable.
                setSelectedIndex(1);
                return;
            }
        }
        
        // Keeps Picture in sync with currently selected items.
        picture.setSelectionInterval(getMinSelectionIndex() - 1, getMaxSelectionIndex() - 1);
    }
    
    /** 
     * A CellRenderer that eliminates any of the overhead that the DefaultListCellRenderer (a JLabel) 
     * adds.  Only left justified strings are displayed, and cells have a fixed preferred height and 
     * width.   
     */
    class TextCellRenderer extends JPanel implements ListCellRenderer {
        
        String text;
        final int borderWidth = 2;
        final int baseline;
        final int width;
        final int height;

        /**
         * Constructor for TextCellRenderer.
         * 
         * @param metrics FontMetrics from which we get info for drawing the text.
         * @param width The width of the renderer component.
         */
        TextCellRenderer(FontMetrics metrics, int width) {
            super();
            baseline = metrics.getAscent() + borderWidth;
            this.height = metrics.getHeight() + (2 * borderWidth);
            this.width = width;
            this.setFocusable(false);
        }

        /** 
         * Return the renderers fixed size here.  
         */
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }

        /**
         * Completely bypass all of the standard JComponent painting machinery.
         * This is a special case: the renderer is guaranteed to be opaque,
         * it has no children, and it's only a child of the JList while
         * it's being used to rubber stamp cells.
         * <p>
         * Clear the background and then draw the text.
         */
        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(getForeground());
            g.drawString(text, borderWidth, baseline);
        }


        /**
         * This is is the ListCellRenderer method.  It just sets the foreground and background 
         * properties and updates the local text field.
         */
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            
            // TODO: The index gives us an opportunity to get hold of the PictureCode to get extra info.
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
            }
            else {
                setBackground(list.getBackground());
            }
            
            text = value.toString();
            
            if (text.startsWith("    ")) {
                if (isSelected) {
                    setForeground(Color.WHITE);
                    setBackground(Color.LIGHT_GRAY);
                } else {
                    setForeground(Color.GRAY);
                }
            }
            else {
                if (isSelected) {
                    setForeground(Color.WHITE);
                } else {
                    setForeground(Color.BLACK);
                }
            }
    
            return this;
        }
    }

    /**
     * Mouse Listener for the PictureCodeList JList. Handles the popup menu for items in the list.
     */
    class PictureCodeListMouseListener extends MouseAdapter {
      
        /**
         * Invoked when a mouse pressed event occurs on the PictureCodeList JList.
         * 
         * @param e The MouseEvent.
         */
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }
  
        /**
         * Invoked when a mouse released event occurs on the PictureCodeList JList.
         * 
         * @param e The MouseEvent.
         */
        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }
        
        /**
         * Checks if the mouse event is the popup event. This is the platform 
         * independent way of implementing this.
         *  
         * @param e The MouseEvent to check.
         */
        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // Convert the mouse event in to the corresponding picture position.
                int itemIndex = locationToIndex(e.getPoint());
                
                // Only show the popup menu if the item is selected and it isn't the end code.
                if ((itemIndex >= getMinSelectionIndex()) && (itemIndex <= getMaxSelectionIndex()) && !picture.getCurrentPictureCode().isEndCode()) {
                    popupMenu.refreshState();
                    popupMenu.show(PictureCodeList.this, e.getX(), e.getY());
                }
            }
        }
    }
    
    /**
     * Enum representing the options in the popup menu.
     */
    private enum PopupMenuAction { 
        DELETE,
        COPY
    };
    
    /**
     * ActionListener for the popup menu that is activated for a selected item in the JList.
     */
    class PictureCodeListPopupMenuActionListener implements ActionListener {

        /**
         * Processes the given ActionEvent for the popup menu.
         * 
         * @param e The ActionEvent to process.
         */
        public void actionPerformed(ActionEvent e) {
            PopupMenuAction action = PopupMenuAction.valueOf(e.getActionCommand().toUpperCase());
            switch (action) {
                case DELETE:
                    // Store some details about what is being deleted so we can decide what to do after it has been deleted.
                    int minSelectionIndex = getMinSelectionIndex();
                    PictureCode codeToDelete = picture.getCurrentPictureCode();

                    // Allow any code other than the end code to be deleted. There must always be an end code.
                    if (!codeToDelete.isEndCode()) {
                        // Remember, picture position is always one less than the JList index.
                        picture.deletePictureCodes(getMinSelectionIndex() - 1, getMaxSelectionIndex() - 1);
                    }
                    
                    // Reselect the current picture position after delete since JList will have lowered index by 1. 
                    setSelectedIndex(minSelectionIndex);
                    break;
            }
        }
    }
}
