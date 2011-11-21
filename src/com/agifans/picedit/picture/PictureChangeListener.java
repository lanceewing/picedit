package com.agifans.picedit.picture;

/**
 * Classes that want to be notified of changes to the Picture can implement this
 * interface and then register themselves with the Picture.
 * 
 * @author Lance Ewing
 */
public interface PictureChangeListener {

    /**
     * Invoked when one or more PictureCodes are added to the Picture.
     * 
     * @param fromIndex The index at which the codes started to be added.
     * @param toIndex The index at which the codes finished being added.
     */
    void pictureCodesAdded(int fromIndex, int toIndex);
    
    /**
     * Invoked when one or more Picturecodes are removed from the Picture.
     * 
     * @param fromIndex The index at which the codes started to be removed.
     * @param toIndex The index at which the codes finished being removed.
     */
    void pictureCodesRemoved(int fromIndex, int toIndex);
}
