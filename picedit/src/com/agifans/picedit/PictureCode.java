package com.agifans.picedit;

/**
 * This class holds a single picture code, which is a single byte within the 
 * picture code buffer. A picture code can be either a data code or a action
 * code. A data code is a data byte related to the previous action code.
 * 
 * @author Lance Ewing
 */
public class PictureCode {

    /**
     * The raw code value as stored in the AGI picture.
     */
    private int code;

    /**
     * Constructor for PictureCode.
     * 
     * @param code the raw code value.
     */
    public PictureCode(int code) {
        this.code = code;
    }

    /**
     * Gets the raw code value.
     * 
     * @return the raw code value.
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the raw code value.
     * 
     * @param code the raw code value.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Returns true if this is an action code; otherwise false.
     * 
     * @return true if this is an action code; otherwise false.
     */
    public boolean isActionCode() {
        return ((code >= 0xF0) && (code != 0xFF));
    }

    /**
     * Returns true if this is a data code; otherwise false.
     * 
     * @return true if this is a data code; otherwise false.
     */
    public boolean isDataCode() {
        return (code < 0xF0);
    }
}
