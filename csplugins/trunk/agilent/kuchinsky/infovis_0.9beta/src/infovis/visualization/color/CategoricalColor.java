/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import infovis.Column;
import infovis.column.IntColumn;
import infovis.visualization.ColorVisualization;

import java.awt.Color;

import javax.swing.event.ChangeEvent;


/**
 * Color BasicVisualization for Categorical Colors.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class CategoricalColor extends ColorVisualization {
    private IntColumn column;
    private int[]     category;
    private float     startHue;
    private float     startSaturation;
    private float     startValue;
    private float     hueSeparation = 0;

    /**
     * Creates a new CategoricalColor object.
     *
     * @param column the Column,
     * @param startHue Starting Hue
     * @param startSaturation Starting Saturation.
     * @param startValue Starting value.
     */
    public CategoricalColor(IntColumn column, float startHue,
                            float startSaturation, float startValue) {
        super(column);
        this.category = null;
        this.startSaturation = startSaturation;
        this.startHue = startHue;
        this.startValue = startValue;
    }

    /**
     * Creates a new CategoricalColor object.
     *
     * @param column the column.
     * @param hsbStart a table of three floats for the hue, saturatio and brightness.
     */
    public CategoricalColor(IntColumn column, float[] hsbStart) {
        this(column, hsbStart[0], hsbStart[1], hsbStart[2]);
    }

    /**
     * Creates a new CategoricalColor object.
     *
     * @param column the column.
     * @param start the starting color in the hsb cirle.
     */
    public CategoricalColor(IntColumn column, Color start) {
        this(column,
             Color.RGBtoHSB(start.getRed(), start.getGreen(),
                            start.getBlue(), null));
    }

    /**
     * Creates a new CategoricalColor object.
     *
     * @param column the color.
     */
    public CategoricalColor(IntColumn column) {
        this(column, 0, 1, 1);
    }

    protected void computeColors() {
        if (category != null)
            return;
        if (column.getMaxIndex() == -1)
            return;
        int cat = 
            column.get(column.getMaxIndex()) 
            - column.get(column.getMinIndex()) 
            + 1;
        category = new int[cat];
        if (cat == 1) {
            category[0] = Color.WHITE.getRGB();
        }
        else {
            int   i = 0;
            float dh = 1.0f / cat;
            for (float hue = startHue; i < cat;
                     hue += dh + hueSeparation) {
                category[i++] = Color.HSBtoRGB(hue, startSaturation, startValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        category = null;
    }

    /**
     * {@inheritDoc}
     */
    public int getColorValue(int row) {
        computeColors();
        if (column.isValueUndefined(row) || category == null)
            return 0;
        return category[column.get(row)];
    }
    
    /**
     * Returns a color given a category number.
     * @param val the category
     * @return a color given a category number
     */
    public Color getColorForValue(int val) {
        computeColors();
        if (category == null || val < 0 || val >= category.length) 
            return null;
        return new Color(category[val]);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Sets the managed column.
     * @param c the column.
     */
    public void setColumn(Column c) {
        IntColumn column = (IntColumn)c;
        if (this.column == column)
            return;
        this.column = column;
        super.setColumn(c);
    }
}
