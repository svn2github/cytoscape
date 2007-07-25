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
 * @version $Revision: 1.27 $
 */
public interface NumberColumn extends Column {
    /**
     * Returns the value as an int.
     *
     * @param row the row
     *
     * @return the value as an int
     */
    int getIntAt(int row);

    /**
     * Returns the value as a float.
     *
     * @param row the row
     *
     * @return the value as a float
     */
    float getFloatAt(int row);

    /**
     * Returns the value as a long.
     *
     * @param row the row
     *
     * @return the value as a long
     */
    long getLongAt(int row);

    /**
     * Returns the value as a double.
     *
     * @param row the row
     *
     * @return the value as a double
     */
    double getDoubleAt(int row);

    /**
     * Sets the value as an int.
     *
     * @param row the row
     * @param v the value as an int
     */
    void setIntAt(int row, int v);

    /**
     * Sets the value as a float.
     *
     * @param row the row
     * @param v the value as a float
     */
    void setFloatAt(int row, float v);

    /**
     * Sets the value as a long.
     *
     * @param row the row
     * @param v the value as a long
     */
    void setLongAt(int row, long v);

    /**
     * Sets the value as a double.
     *
     * @param row the row
     * @param v the value as a double
     */
    void setDoubleAt(int row, double v);
    
    /**
     * Returns the smallest value as a double.
     * @return the smallest value as a double.
     */
    double getDoubleMin();
    
    /**
     * Returns the largset value as a double.
     * @return the largset value as a double.
     */
    double getDoubleMax();

    /**
     * Returns the rounded value according to the semantics
     * of this number value.
     * @param value the value to round
     * @return the rounded value according to the semantics
     * of this number value.
     */
    double round(double value) ;
    
    /**
     * Returns the specified value formatted according to
     * this column's format.
     * @param value the value to format
     * @return the string formatted according to
     * this column's format.
     */
    String format(double value);
}
