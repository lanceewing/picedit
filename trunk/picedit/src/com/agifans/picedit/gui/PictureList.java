package com.agifans.picedit.gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.agifans.picedit.PicEdit;

/**
 * 
 * 
 * @author Lance Ewing
 */
@SuppressWarnings("serial")
public class PictureList extends JList {

    /**
     * The PICEDIT application.
     */
    private PicEdit application;
    
    /**
     * Constructor for PictureList.
     * 
     * @param application The PICEDIT application.
     */
    public PictureList(PicEdit application) {
        this.application = application;
        
        DefaultListModel pictureListModel = new DefaultListModel();
        pictureListModel.addElement("One");
        this.setModel(pictureListModel);
    }
}
