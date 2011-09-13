package com.agifans.picedit;

import java.util.LinkedList;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * A BoundedRangeModel implementation whose behaviour and state is determined
 * by an EditStatus instance.
 * 
 * @author Lance Ewing
 */
public class PositionSliderModel implements BoundedRangeModel {

    /**
     * The picture whose position is being adjusted.
     */
    private Picture picture;

    /**
     * The length of the inner range that begins at the model's value.
     */
    private int extent = 0;

    /**
     * true if the upcoming changes to the value property are part of a series.
     */
    private boolean isAdjusting = false;

    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    
    /**
     * The list of the currently registered EventListeners.
     */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * The value used in the most recent setValue method call.
     */
    private int lastSetValue;
    
    /**
     * Constructor for EditStatus.
     * 
     * @param picture The Picture whose position is being adjusted.
     */
    public PositionSliderModel(Picture picture) {
        this.picture = picture;
    }

    /**
     * Returns the minimum acceptable value.
     *
     * @return the value of the minimum property
     * @see #setMinimum
     */
    public int getMinimum() {
        // Minimum position is always 0, i.e. the start of the picture.
        return 0;
    }

    /**
     * Sets the minimum value (ignored in this case since minimum is always 0).
     * 
     * @param newMinimum The new minimum value.
     */
    public void setMinimum(int newMinimum) {
        setRangeProperties(getValue(), getExtent(), getMinimum(), getMaximum(), isAdjusting);
    }

    /**
     * Returns the model's maximum.  Note that the upper
     * limit on the model's value is (maximum - extent).
     *
     * @return the value of the maximum property.
     * @see #setMaximum
     * @see #setExtent
     */
    public int getMaximum() {
        return picture.getPictureCodes().size() - 1;
    }

    /**
     * Sets the model's maximum to <I>newMaximum</I>. The other 
     * three properties may be changed as well, to ensure that
     * <pre>
     * minimum &lt;= value &lt;= value+extent &lt;= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newMaximum the model's new maximum
     * @see #getMaximum
     * @see #addChangeListener
     */
    public void setMaximum(int newMaximum) {
        setRangeProperties(getValue(), getExtent(), getMinimum(), newMaximum, isAdjusting);
    }

    /**
     * Returns the model's current value.  Note that the upper
     * limit on the model's value is <code>maximum - extent</code> 
     * and the lower limit is <code>minimum</code>.
     *
     * @return  the model's value
     * @see     #setValue
     */
    public int getValue() {
        return picture.getPicturePosition();
    }

    /**
     * Sets the model's current value to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * minimum &lt;= value &lt;= value+extent &lt;= maximum
     * </pre>
     * Otherwise, if <code>newValue</code> is less than <code>minimum</code> 
     * it's set to <code>minimum</code>, if its greater than 
     * <code>maximum</code> then it's set to <code>maximum</code>, and 
     * if it's greater than <code>value+extent</code> then it's set to 
     * <code>value+extent</code>.
     * <p>
     * When a BoundedRange model is used with a scrollbar the value
     * specifies the origin of the scrollbar knob (aka the "thumb" or
     * "elevator").  The value usually represents the origin of the 
     * visible part of the object being scrolled.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newValue the model's new value
     * @see #getValue
     */
    public void setValue(int newValue) {
        if (newValue != lastSetValue) {
            newValue = Math.min(newValue, Integer.MAX_VALUE - extent);
            newValue = Math.max(newValue, getMinimum());
            if (newValue + extent > getMaximum()) {
                newValue = getMaximum() - extent;
            }
            setRangeProperties(newValue, extent, getMinimum(), getMaximum(), isAdjusting);
            lastSetValue = newValue;
        }
    }

    /**
     * This attribute indicates that any upcoming changes to the value
     * of the model should be considered a single event. This attribute
     * will be set to true at the start of a series of changes to the value,
     * and will be set to false when the value has finished changing.  Normally
     * this allows a listener to only take action when the final value change in
     * committed, instead of having to do updates for all intermediate values.
     * <p>
     * Sliders and scrollbars use this property when a drag is underway.
     * 
     * @param b true if the upcoming changes to the value property are part of a series
     */
    public void setValueIsAdjusting(boolean isAdjusting) {
        this.isAdjusting = isAdjusting;
    }

    /**
     * Returns true if the current changes to the value property are part 
     * of a series of changes.
     * 
     * @return the valueIsAdjustingProperty.  
     * @see #setValueIsAdjusting
     */
    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }

    /**
     * Returns the model's extent, the length of the inner range that
     * begins at the model's value.  
     *
     * @return  the value of the model's extent property
     * @see     #setExtent
     * @see     #setValue
     */
    public int getExtent() {
        return extent;
    }

    /**
     * Sets the model's extent.  The <I>newExtent</I> is forced to 
     * be greater than or equal to zero and less than or equal to
     * maximum - value.   
     * <p>
     * When a BoundedRange model is used with a scrollbar the extent
     * defines the length of the scrollbar knob (aka the "thumb" or
     * "elevator").  The extent usually represents how much of the 
     * object being scrolled is visible. When used with a slider,
     * the extent determines how much the value can "jump", for
     * example when the user presses PgUp or PgDn.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param  newExtent the model's new extent
     * @see #getExtent
     * @see #setValue
     */
    public void setExtent(int newExtent) {
        setRangeProperties(getValue(), newExtent, getMinimum(), getMaximum(), isAdjusting);
    }

    /**
     * This method sets all of the model's data with a single method call.
     * The method results in a single change event being generated. This is
     * convenient when you need to adjust all the model data simultaneously and
     * do not want individual change events to occur.
     *
     * @param value  an int giving the current value 
     * @param extent an int giving the amount by which the value can "jump"
     * @param min    an int giving the minimum value
     * @param max    an int giving the maximum value
     * @param adjusting a boolean, true if a series of changes are in
     *                    progress
     * 
     * @see #setValue
     * @see #setExtent
     * @see #setMinimum
     * @see #setMaximum
     * @see #setValueIsAdjusting
     */
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        // Since the state is determined by the EditStatus, we ignore everything 
        // except for the value.
        LinkedList<PictureCode> pictureCodes = picture.getPictureCodes();
        if (value < (pictureCodes.size() - 1)) {
            // Find the closest picture action to the entered position.
            while (pictureCodes.get(value).getCode() < 0xF0) {
                value = value - 1;
            }
        }
        // This first check is important for the slider to get updated for external position changes.
        if (value != lastSetValue) {
            // This second check is so that we don't redraw picture if picture is already at the position.
            if (value != picture.getPicturePosition()) {
                picture.setPicturePosition(value);
                picture.drawPicture();
                picture.updateScreen();
            }
            fireStateChanged();
        }
    }

    /**
     * Adds a ChangeListener to the model's listener list.
     *
     * @param listener the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    /**
     * Removes a ChangeListener from the model's listener list.
     *
     * @param listener the ChangeListener to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * Sends a {@link ChangeEvent} to any registered {@link
     * ChangeListener}s.
     *
     * @see #addChangeListener(ChangeListener)
     * @see #removeChangeListener(ChangeListener)
     */
    protected void fireStateChanged() {
        Object[] listeners;

        listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
}
