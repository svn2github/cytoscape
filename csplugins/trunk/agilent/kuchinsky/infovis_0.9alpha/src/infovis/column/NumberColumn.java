/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.column;

import infovis.Column;


/**
 * Abstract class for all columns containing numeric values.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.25 $
 */
public interface NumberColumn extends Column {
    /**
     * Returns the value as an int
     *
     * @param row the row
     *
     * @return the value as an int
     */
    public abstract int getIntAt(int row);

    /**
     * Returns the value as a float
     *
     * @param row the row
     *
     * @return the value as a float
     */
    public float getFloatAt(int row);

    /**
     * Returns the value as a long
     *
     * @param row the row
     *
     * @return the value as a long
     */
    public long getLongAt(int row);

    /**
     * Returns the value as a double
     *
     * @param row the row
     *
     * @return the value as a double
     */
    public double getDoubleAt(int row);

    /**
     * Sets the value as an int
     *
     * @param row the row
     * @param v the value as an int
     */
    public void setIntAt(int row, int v);

    /**
     * Sets the value as a float
     *
     * @param row the row
     * @param v the value as a float
     */
    public void setFloatAt(int row, float v);

    /**
     * Sets the value as a long
     *
     * @param row the row
     * @param v the value as a long
     */
    public void setLongAt(int row, long v);

    /**
     * Sets the value as a double
     *
     * @param row the row
     * @param v the value as a double
     */
    public abstract void setDoubleAt(int row, double v);
    
    public abstract double getDoubleMin();
    public abstract double getDoubleMax();
    
    public double round(double value) ;
    
    public String format(double value);
}
