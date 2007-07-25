/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.panel.DefaultDoubleBoundedRangeModel;
import infovis.panel.DoubleBoundedRangeModel;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

/**
 * Base class for range sliders.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public abstract class BasicRangeSlider
    extends JComponent
    implements MouseListener, MouseMotionListener, ChangeListener {
//    private boolean enabled;
//    private static int PICK_WIDTH = 10;
    protected int direction;
    protected DoubleBoundedRangeModel model;
    /**
     * Main direction length, the slider will move in that direction
     */
    protected float length;
    
    /**
     * Secondary direction, the slider will not move in that direction
     */
    protected float width;


    /**
     * Constructs a new range slider.
     *
     * @param minimum - the minimum value of the range.
     * @param maximum - the maximum value of the range.
     * @param lowValue - the current low value shown by the range
     *        slider's bar.
     * @param highValue - the current high value shown by the range
     *        slider's bar.
     */
    public BasicRangeSlider(
        double minimum,
        double maximum,
        double lowValue,
        double highValue) {
        this(
            new DefaultDoubleBoundedRangeModel(
                lowValue,
                highValue - lowValue,
                minimum,
                maximum));
    }

    /**
     * Creates a new RangeSlider object.
     *
     * @param model the DoubleBoundedRangeModel
     */
    public BasicRangeSlider(DoubleBoundedRangeModel model) {
        this.model = model;
        model.addChangeListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Returns the current "low" value shown by the range slider's
     * bar. The low value meets the constraint minimum  &lt;=
     * lowValue  &lt;= highValue  &lt;= maximum.
     *
     * @return the current "low" value shown by the range slider's bar.
     */
    public double getLowValue() {
        return model.getValue();
    }

    /**
     * Returns the current "high" value shown by the range slider's
     * bar. The high value meets the constraint minimum  &lt;=
     * lowValue  &lt;= highValue  &lt;= maximum.
     *
     * @return the current "high" value shown by the range slider's
     * bar.
     */
    public double getHighValue() {
        return model.getValue() + model.getExtent();
    }

    /**
     * Returns the minimum possible value for either the low value or
     * the high value.
     *
     * @return the minimum possible value for either the low value or
     *         the high value.
     */
    public double getMinimum() {
        return model.getMinimum();
    }

    /**
     * Returns the maximum possible value for either the low value or
     * the high value.
     *
     * @return the maximum possible value for either the low value or
     *         the high value.
     */
    public double getMaximum() {
        return model.getMaximum();
    }

    /**
     * Returns true if the specified value is within the range
     * indicated by this range slider, i&dot;e&dot; lowValue 1 &lt;= v &lt;=
     * highValue.
     *
     * @param v value
     *
     * @return true if the specified value is within the range
     *         indicated by this range slider.
     */
    public boolean contains(double v) {
        return (v >= getLowValue() && v <= getHighValue());
    }

    /**
     * Sets the low value shown by this range slider. This causes the
     * range slider to be repainted and a RangeEvent to be fired.
     *
     * @param lowValue the low value shown by this range slider
     */
    public void setLowValue(double lowValue) {
        if ((lowValue + model.getExtent()) > getMaximum()) {
            model.setExtent(getMaximum() - lowValue);
            model.setValue(lowValue);
        }
        else {
            double high = getHighValue();
            model.setValue(lowValue);
            setHighValue(high);
        }
    }

    /**
     * Sets the high value shown by this range slider. This causes
     * the range slider to be repainted and a RangeEvent to be
     * fired.
     *
     * @param highValue the high value shown by this range slider
     */
    public void setHighValue(double highValue) {
        model.setExtent(highValue - getLowValue());
    }

    /**
     * Sets the minimum value of the doubleBoundedRangeModel.
     *
     * @param min the minimum value.
     */
    public void setMinimum(double min) {
        model.setMinimum(min);
    }

    /**
     * Sets the maximum value of the doubleBoundedRangeModel.
     *
     * @param max the maximum value.
     */
    public void setMaximum(double max) {
        model.setMaximum(max);
    }

    protected void updateBounds() {

    }

    Rectangle getInBounds() {
        Dimension sz = getSize();
        Insets    insets = getInsets();
        return new Rectangle(insets.left, insets.top,
                     sz.width - insets.left - insets.right,
                     sz.height - insets.top - insets.bottom);
    }
    
    // Converts from screen coordinates to a range value.
//    private double toLocalX(double x) {
//        Dimension sz = getSize();
//        double    xScale = (sz.width - 3) / (getMaximum() - getMinimum());
//    
//        return (x / xScale) + getMinimum();
//    }
//
//    // Converts from a range value to screen coordinates.
//    private int toScreenX(double x) {
//        Dimension sz = getSize();
//        double    xScale = (sz.width - 3) / (getMaximum() - getMinimum());
//    
//        return (int)((x - getMinimum()) * xScale);
//    }
}
