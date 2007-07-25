/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.util.Iterator;

import infovis.Column;
import infovis.metadata.DependencyMetadata;

/**
 * Class ColumnId wraps any column to make it a NumberColumn.
 * As value, it simply returns the index of the element.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class ColumnId extends ColumnProxy implements NumberColumn {
    /**
     * Search for an IdColumn associated with a specified column and returns it
     * or creates it if none exists.
     * 
     * @param col the column
     * @return an IdColumn associated with the specified column
     */
    public static ColumnId findIdColumn(Column col) {
        for (Iterator iter = DependencyMetadata.dependentIterator(col);
            iter.hasNext();) {
            Object o = iter.next();
            if (o instanceof ColumnId) {
                return (ColumnId)o;
            }
        }
        return new ColumnId(col);
    }
    
    protected ColumnId(Column col) {
        super(col);
    }
    
    /**
     * Returns the row.
     * @param row the row
     * @return the row
     */
    public int get(int row) {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getDoubleAt(int row) {
        return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleMax() {
        return size();
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleMin() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public float getFloatAt(int row) {
        return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public int getIntAt(int row) {
        return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public long getLongAt(int row) {
        return get(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public double round(double value) {
        return Math.round(value);
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setFloatAt(int row, float v) {
        readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setIntAt(int row, int v) {
        readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setLongAt(int row, long v) {
        readonly();
    }
    
    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        return ""+(int)value;
    }

}
