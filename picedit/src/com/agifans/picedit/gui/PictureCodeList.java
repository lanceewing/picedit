package com.agifans.picedit.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JList;

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
public class PictureCodeList extends JList implements PictureChangeListener {

    /**
     * The Picture whose picture codes will be displayed in this JList.
     */
    private Picture picture;
    
    /**
     * Provides direct access to the model.
     */
    private PictureCodeListModel pictureCodeListModel;
    
    /**
     * Constructor for PictureCodeList.
     * 
     * @param picture The Picture whose picture codes will be displayed in this JList.
     */
    public PictureCodeList(Picture picture) {
        this.picture = picture;
        this.pictureCodeListModel = new PictureCodeListModel();
        this.setModel(pictureCodeListModel);
        this.setFont(new Font("Courier New", Font.PLAIN, 10));
        this.setForeground(Color.BLACK);
        this.setFocusable(true);
    }
    
    /**
     * ListModel for the PictureCodeList JList component that holds the human readable picture
     * codes for the currently selected Picture.
     */
    class PictureCodeListModel extends AbstractListModel implements PictureChangeListener {

        /**
         * The last rendered picture position.
         */
        private int lastPicturePosition = -1;
        
        /**
         * Gets a human readable form for the picture code item at the given index.
         * 
         * @param index The index at which to retrieve the PictureCode.
         * 
         * @return The human readable form of the requested PictureCode.
         */
        public Object getElementAt(int index) {
            if (index == 0) {
                return "<html><b>Start</b></html>";
            }
            
            boolean isCurrentPosition = (index == picture.getPicturePosition() + 1);
            
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
                StringBuilder displayTextBuf = new StringBuilder("<html><b");
                if (isCurrentPosition) {
                    displayTextBuf.append(" style='color: #000077; background-color: #BBBBDD; width: 170px;'");
                }
                displayTextBuf.append(">&nbsp;&nbsp;");
                displayTextBuf.append(actionCodeType.getDisplayableText());
                displayTextBuf.append("</b></html>");
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
                        displayText = "<html><b>End</b></html>";
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
        
        public void updatePicturePosition() {
            int currentPicturePosition = picture.getPicturePosition();
            if (currentPicturePosition != lastPicturePosition) {
                if (lastPicturePosition != -1) {
                    //fireContentsChanged(this, lastPicturePosition, lastPicturePosition);
                }
                //fireContentsChanged(this, currentPicturePosition, currentPicturePosition);
                this.lastPicturePosition = currentPicturePosition;
            }
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
        pictureCodeListModel.pictureCodesAdded(fromIndex, toIndex);
    }

    /**
     * Invoked when picture codes are removed from the Picture. Delegates to the PictureCodeListModel.
     */
    public void pictureCodesRemoved(int fromIndex, int toIndex) {
        pictureCodeListModel.pictureCodesRemoved(fromIndex, toIndex);
    }
    
    /**
     * Updates the JList to show the current picture position (if required).
     */
    public void updatePicturePosition() {
        pictureCodeListModel.updatePicturePosition();
    }
}
