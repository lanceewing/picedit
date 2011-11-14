package com.agifans.picedit.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
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
        this.setFocusable(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addListSelectionListener(this);
        
        FontMetrics metrics = this.getFontMetrics(this.getFont());
        this.setCellRenderer(new TextCellRenderer(metrics, 130));
        
        // These two settings make the JList SIGNIFICANTLY faster when adding new picture
        // codes within the middle of the list.
        this.setPrototypeCellValue("Start");
        this.setFixedCellWidth(130);
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
            PictureCode pictureCode = null;
            PictureCode previousPictureCode = null;
            try {
                pictureCode = pictureCodes.get(index - 1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            
            String displayText = null;
            if (pictureCode.isActionCode()) {
                PictureCodeType actionCodeType = pictureCode.getType();
                StringBuilder displayTextBuf = new StringBuilder("  ");
                displayTextBuf.append(actionCodeType.getDisplayableText());
                displayText = displayTextBuf.toString();
            } else {
                int code = pictureCode.getCode();
                switch (pictureCode.getType()) {
                    case FILL_POINT_DATA:
                        displayText = String.format("    Fill %d %d", (code & 0xFF00) >> 8, code & 0x00FF);
                        break;
                    case BRUSH_POINT_DATA:
                        displayText = String.format("    Plot %d %d", (code & 0xFF00) >> 8, code & 0x00FF);
                        break;
                    case ABSOLUTE_POINT_DATA:
                        previousPictureCode = pictureCodes.get(index  - 2);
                        if (previousPictureCode.isActionCode()) {
                            displayText = String.format("    MoveTo %d %d", (code & 0xFF00) >> 8, code & 0x00FF);
                        } else {
                            displayText = String.format("    LineTo %d %d", (code & 0xFF00) >> 8, code & 0x00FF);
                        }
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
                        displayText = String.format("    LineTo %d +0", code);
                        break;
                    case Y_POSITION_DATA:
                        displayText = String.format("    LineTo +0 %d", code);
                        break;
                    case BRUSH_PATTERN_DATA:
                        displayText = String.format("    SetPattern %d", pictureCode.getCode());
                        break;
                    case BRUSH_TYPE_DATA:
                        displayText = "    " + BrushType.getBrushTypeForBrushCode(pictureCode.getCode()).getDisplayName();
                        break;
                    case COLOR_DATA:
                        displayText = "    " +  EgaPalette.COLOR_NAMES[pictureCode.getCode()];
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
     * Invoked when the position slider value changes. Keeps the picture code list selected index in sync.
     */
    public void stateChanged(ChangeEvent e) {
        int pictureIndex = picture.getPicturePosition() + 1;
        if (pictureIndex != getSelectedIndex()) {
            setSelectedIndex(pictureIndex);
        }
    }

    /**
     * Invoked when the user selects something on the picture code JList.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !pictureCodesAreAdjusting) {
            int value = this.getSelectedIndex();
            value = (value > 0? value - 1 : 0);
            
            LinkedList<PictureCode> pictureCodes = picture.getPictureCodes();
            if (value < (pictureCodes.size() - 1)) {
                // Find the closest picture action to the entered position.
                while (pictureCodes.get(value).isDataCode()) {
                    value = value - 1;
                }
            }
            
            // This check is so that we don't redraw picture if picture is already at the position.
            if (value != picture.getPicturePosition()) {
                picture.setPicturePosition(value);
                picture.drawPicture();
            }
        }
    }
    
    class PictureCodeListItemValue {
        String text;
        Color color;
        
        PictureCodeListItemValue(String text, Color color) {
            this.text = text;
            this.color = color;
        }
    }
    
    /** 
     * A CellRenderer that eliminates any of the overhead that the
     * DefaultListCellRenderer (a JLabel) adds.  Only left justified
     * strings are displayed, and cells have a fixed preferred
     * height and width.   
     */
    class TextCellRenderer extends JPanel implements ListCellRenderer {
        String text;
        final int borderWidth = 2;
        final int baseline;
        final int width;
        final int height;

        TextCellRenderer(FontMetrics metrics, int width) {
            super();
            baseline = metrics.getAscent() + borderWidth;
            this.height = metrics.getHeight() + (2 * borderWidth);
            this.width = width;
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


        /* This is is the ListCellRenderer method.  It just sets
         * the foreground and background properties and updates the
         * local text field.
         */
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            
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
}
