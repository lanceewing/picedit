package com.agifans.picedit.gui.handler;

import com.agifans.picedit.PicEdit;
import com.agifans.picedit.types.ToolType;

/**
 * Handles processing that is common to both mouse and keyboard events.
 * 
 * @author Lance Ewing
 */
public abstract class CommonHandler {

    /**
     * The PICEDIT application component.
     */
    protected PicEdit application;
     
    /**
     * Constructor for CommonHandler.
     * 
     * @param application the PICEDIT application component.
     */
    public CommonHandler(PicEdit application) {
        this.application = application;
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
}
