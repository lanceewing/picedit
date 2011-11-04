package com.agifans.picedit.gui;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.picture.PictureChangeListener;
import com.agifans.picedit.picture.PictureCode;

/**
 * The JList that holds the human readable list of picture codes for the currently 
 * selected Picture.
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class PictureCodeList extends JList implements PictureChangeListener {

    /**
     * The PICEDIT application.
     */
    private PicEdit application;
    
    /**
     * Constructor for PictureCodeList.
     * 
     * @param application The PICEDIT application.
     */
    public PictureCodeList(PicEdit application) {
        this.application = application;
        this.setModel(new PictureCodeListModel());
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
            PictureCode pictureCode = null;
            try {
                pictureCode = application.getPicture().getPictureCodes().get(index);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            return String.format("0x%02X", pictureCode.getCode());
        }

        /**
         * Gets the number of items in the picture code list.
         * 
         * @return The number of items in the picture code list.
         */
        public int getSize() {
            int listSize = application.getPicture().getPictureCodes().size();
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
        ((PictureCodeListModel)(this.getModel())).refreshList();
    }
    
    /**
     * Invoked when picture codes are added to the Picture. Delegates to the PictureCodeListModel.
     */
    public void pictureCodesAdded(int fromIndex, int toIndex) {
        ((PictureCodeListModel)this.getModel()).pictureCodesAdded(fromIndex, toIndex);
    }

    /**
     * Invoked when picture codes are removed from the Picture. Delegates to the PictureCodeListModel.
     */
    public void pictureCodesRemoved(int fromIndex, int toIndex) {
        ((PictureCodeListModel)this.getModel()).pictureCodesRemoved(fromIndex, toIndex);
    }
}
