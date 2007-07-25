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
 * @version $Revision: 1.5 $
 */
public class ColumnId extends ColumnProxy implements NumberColumn {
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
    
    public ColumnId(Column col) {
        super(col);
    }
    
    public int get(int row) {
        return row;
    }
    
    public double getDoubleAt(int row) {
        return get(row);
    }

    public double getDoubleMax() {
        return size();
    }

    public double getDoubleMin() {
        return 0;
    }

    public float getFloatAt(int row) {
        return get(row);
    }

    public float getFloatMax() {
        return size();
    }

    public float getFloatMin() {
        return 0;
    }

    public int getIntAt(int row) {
        return get(row);
    }

    public int getIntMax() {
        return size();
    }

    public int getIntMin() {
        return 0;
    }

    public long getLongAt(int row) {
        return get(row);
    }

    public long getLongMax() {
        return size();
    }

    public long getLongMin() {
        return 0;
    }

    public double round(double value) {
        return Math.round(value);
    }

    public void setDoubleAt(int row, double v) {
        readonly();
    }

    public void setFloatAt(int row, float v) {
        readonly();
    }

    public void setIntAt(int row, int v) {
        readonly();
    }

    public void setLongAt(int row, long v) {
        readonly();
    }
    public String format(double value) {
        return ""+(int)value;
    }

}
