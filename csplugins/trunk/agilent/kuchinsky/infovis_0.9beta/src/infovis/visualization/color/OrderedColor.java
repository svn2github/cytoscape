/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import infovis.Column;
import infovis.column.NumberColumn;
import infovis.visualization.ColorVisualization;

import java.awt.Color;

import javax.swing.event.ChangeEvent;

/**
 * Color BasicVisualization for Ordered values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.20 $
 */
public class OrderedColor extends ColorVisualization {
    static protected Color[]  defaultRamp = { Color.WHITE, Color.BLACK };
    protected NumberColumn    column;
    protected Color[]         ramp;
    protected double          scale;
    protected double          origin;
    private transient Color[] cache;
    private int               cacheSize = 256;
    private boolean           fixedRange;

    public OrderedColor(NumberColumn column, Color[] ramp) {
        super(column);
        setRamp(ramp);
        setColumn(column);
    }

    /**
     * Constructor for OrderedColor.
     */
    public OrderedColor(NumberColumn column, Color start, Color end) {
        super(column);
        setStart(start);
        setEnd(end);
    }

    public static void setDefaults(Color[] ramp) {
        defaultRamp = ramp;
    }

    /**
     * Creates a new OrderedColor object.
     * 
     * @param column
     *            the managed column.
     */
    public OrderedColor(NumberColumn column) {
        this(column, defaultRamp);
    }

    /**
     * Sets the managed column.
     * 
     * @param c
     *            the column.
     */
    public void setColumn(Column c) {
        column = (NumberColumn)c;
        super.setColumn(c);
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        update();
    }

    public Color[] getCache() {
        if (cache == null) {
            cache = new Color[cacheSize];
            double dt = 1.0 / cacheSize;

            cache[0] = ramp[0];
            for (int i = 1; i < (cacheSize-1); i++) {
                double t = i * dt;
                cache[i] = new Color(getColorAtParameter(t));
            }
            cache[cacheSize - 1] = ramp[ramp.length - 1];
        }
        return cache;
    }

    public void update() {
        if (column == null || fixedRange)
            return;
        int minIndex = column.getMinIndex();
        if (minIndex == -1) {
            scale = 0;
            return;
        }
        origin = column.getDoubleAt(minIndex);
        double range = column.getDoubleAt(column.getMaxIndex()) - origin;
        scale = range == 0 ? 1 : 1.0 / range;
    }

    /**
     * @see infovis.visualization.ColorVisualization#getColumn()
     */
    public Column getColumn() {
        return column;
    }

    public double getColorParameterFor(double value) {
        double t = (value - origin) * scale;
        return t;
    }

    public int getColorAtParameter(double t) {
        if (t <= 0) {
            return ramp[0].getRGB();
        }
        else if (t >= 1) {
            return ramp[ramp.length - 1].getRGB();
        }
        t *= ramp.length - 1;
        int i = (int) t;
        t -= i;
        Color start = ramp[i];
        Color end = ramp[i + 1];
        double red = (1 - t) * start.getRed() + t * end.getRed();
        double green = (1 - t) * start.getGreen() + t * end.getGreen();
        double blue = (1 - t) * start.getBlue() + t * end.getBlue();
        return computeColor(red / 255, green / 255, blue / 255, 1);
    }

    public int getColorFor(double value) {
        return getColorAtParameter(getColorParameterFor(value));
    }

    /**
     * @see infovis.visualization.ColorVisualization#getColor(int)
     */
    public int getColorValue(int row) {
        if (column.isValueUndefined(row))
            return 0;
        return getColorFor(column.getDoubleAt(row));
    }

    public Color getColor(int row) {
        if (column.isValueUndefined(row))
            return null;
        double t = (column.getDoubleAt(row) - origin) * scale;
        int i = (int)Math.round(t * (cacheSize-1));
        if (i >= cacheSize) {
            i = cacheSize-1;
        }
        else if (i < 0) {
            i = 0;
        }
        return getCache()[i];
    }

    public Color[] getRampReference() {
        return ramp;
    }

    public void setRamp(Color[] ramp) {
        this.ramp = (Color[]) ramp.clone();
        cache = null;
    }

    /**
     * Returns the start Color.
     * 
     * @return the start Color.
     */
    public Color getStart() {
        return ramp[0];
    }

    /**
     * Sets the start color.
     * 
     * @param start
     *            the start Color.
     */
    public void setStart(Color start) {
        if (ramp == null) {
            ramp = new Color[2];
            ramp[1] = start;
        }
        ramp[0] = start;
        cache = null;
    }

    /**
     * Returns the end Color.
     * 
     * @return the end Color.
     */
    public Color getEnd() {
        return ramp[ramp.length - 1];
    }

    /**
     * Sets the end color.
     * 
     * @param end
     *            the end Color.
     */
    public void setEnd(Color end) {
        if (ramp == null) {
            ramp = new Color[2];
            ramp[0] = end;
        }
        ramp[1] = end;
        cache = null;
    }

    public static Color[] getDefaultRamp() {
        return defaultRamp;
    }

    public static void setDefaultRamp(Color[] ramp) {
        defaultRamp = ramp;
    }

    public int getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        cache = null;
    }
    
    public void setRange(double min, double max) {
        if (min == max) {
            fixedRange = false;
        }
        else {
            fixedRange = true;
            origin = min;
            scale = 1.0 / (max - min);  
        }
    }
}
