package com.agifans.picedit;

import java.awt.Dimension;

import javax.swing.JDialog;

@SuppressWarnings("serial")
public class ColourChooserDialog extends JDialog {

    public ColourChooserDialog() {
        // TODO: Set to undecorated once palette is drawn and selection code in place.
        this.setModal(true);
        this.setSize(new Dimension(128, 128));
        this.setResizable(false);
    }
    
}
