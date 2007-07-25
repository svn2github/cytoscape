/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.column;

import infovis.Column;
import infovis.Table;

import java.util.BitSet;

/**
 * Abstract class for all columns containing literal values.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */

public abstract class LiteralColumn extends BasicColumn implements
        NumberColumn {

    /**
     * Sets of indexes with undefined values, for sparse columns. null by
     * default
     */
    protected BitSet undefined;

    /**
     * Constructor for LiteralColumn.
     * 
     * @param name
     */
    public LiteralColumn(String name) {
        super(name);
    }

    /**
     * @see infovis.Column#clear()
     * 
     * Call clear after clearing the subclass contents since this method fires
     * the notifications.
     */
    public void clear() {
        undefined = null;
        super.clear();
    }
    
    public boolean compareValues(Column c) {
        if (! (c instanceof NumberColumn)) {
            return false;
        }
        NumberColumn other = (NumberColumn)c;
        for (int i = 0; i < size(); i++) {
            if (isValueUndefined(i)) {
                if (! c.isValueUndefined(i)) {
                    return false;
                }
            }
            else {
                if (getDoubleAt(i) != other.getDoubleAt(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see infovis.Column#isValueUndefined(int)
     */
    public boolean isValueUndefined(int i) {
        if (i < 0 || i >= size())
            return true;
        if (undefined == null)
            return false;
        return undefined.get(i);
    }
    /**
     * @see infovis.Column#setValueUndefined(int,boolean)
     */
    public void setValueUndefined(int i, boolean undef) {
        if (i < 0) return;
        
        if (i >= size()) {
            try {
                disableNotify();
                setSize(i+1);
                if (! undef) {
                    undefined.clear(i);
                }
            }
            finally {
                enableNotify();
            }
            return;
        }
        //if (isValueUndefined(i) == undef) return;
        if (undefined == null) {
            if (undef) {
                undefined = new BitSet();
            }
            else {
                // if value is not undefined, no need to
                // create the map
                return;
            }
        }
        if (undef) {
            undefined.set(i);
        } else {
            undefined.clear(i);
        }
        min_max_updated = false;
        modified(i);
    }
    
    protected void set(int index) {
        setValueUndefined(index, false);
    }

    /*
     * @see infovis.column.BasicColumn#setSize(int)
     * 
     * Call this method before resizing the data structure since it uses the
     * current size of the data structure.
     */
    public void setSize(int newSize) {
        if (newSize == size()) return;
        try {
            disableNotify();
            //modified();
            if (newSize > size()) {
                if (undefined == null) {
                    undefined = new BitSet();
                }
                undefined.set(size(), newSize);
//                for (int i = size(); i < newSize; i++) {
//                    undefined.set(i);
//                }
            } else {
                if (undefined == null || undefined.isEmpty()) {
                    return;
                }
                undefined.clear(newSize, undefined.length());
                min_max_updated = false; //TODO check 
                if (undefined.isEmpty()) {
                    undefined = null;
                }
//                for (int k = undefined.lastKey(); k > newSize; k = undefined
//                        .lastKey()) {
//                    undefined.remove(k);
//                    min_max_updated = false;
//                    if (undefined.isEmpty()) {
//                        undefined = null;
//                        break;
//                    }
//                }
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * Removes all the undefined values
     * at the end of the column
     *
     */
    public void trimUndefined() {
        int last;
        for (last = size()-1; last >= 0; last--) {
            if (! isValueUndefined(last)) {
                break;
            }
        }
        setSize(last+1);
    }

    protected void copyUndefinedFrom(LiteralColumn from) {
        min_max_updated = from.min_max_updated;
        if (min_max_updated) {
            minIndex = from.minIndex;
            maxIndex = from.maxIndex;
        }
        if (from.undefined == null) {
            undefined = null;
            return;
        }
        undefined = (BitSet)from.undefined.clone();
    }
    
    protected void fill() {
        min_max_updated = true;
        if (size() == 0) {
            minIndex = -1;
            maxIndex = -1;
        }
        else {
            minIndex = 0;
            maxIndex = 0;
        }
    }

    /**
     * Returns the value as an int
     * 
     * @param row
     *            the row
     * 
     * @return the value as an int
     */
    public int getIntAt(int row) {
        return (int) getDoubleAt(row);
    }

    /**
     * Returns the value as a float
     * 
     * @param row
     *            the row
     * 
     * @return the value as a float
     */
    public float getFloatAt(int row) {
        return (float) getDoubleAt(row);
    }

    /**
     * Returns the value as a long
     * 
     * @param row
     *            the row
     * 
     * @return the value as a long
     */
    public long getLongAt(int row) {
        return (long) getDoubleAt(row);
    }

    /**
     * Returns the value as a double
     * 
     * @param row
     *            the row
     * 
     * @return the value as a double
     */
    public abstract double getDoubleAt(int row);

    /**
     * Sets the value as an int
     * 
     * @param row
     *            the row
     * @param v
     *            the value as an int
     */
    public void setIntAt(int row, int v) {
        setLongAt(row, v);
    }

    /**
     * Sets the value as a float
     * 
     * @param row
     *            the row
     * @param v
     *            the value as a float
     */
    public void setFloatAt(int row, float v) {
        setDoubleAt(row, v);
    }

    /**
     * Sets the value as a long
     * 
     * @param row
     *            the row
     * @param v
     *            the value as a long
     */
    public void setLongAt(int row, long v) {
        setDoubleAt(row, v);
    }

    /**
     * Sets the value as a double
     * 
     * @param row
     *            the row
     * @param v
     *            the value as a double
     */
    public abstract void setDoubleAt(int row, double v);

    /**
     * Returns a column as a <code>NumberColumn</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>NumberColumn</code> or null if no such column exists or
     *         the column is not a <code>NumberColumn</code>.
     */
    public static NumberColumn getNumberColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof NumberColumn) {
            return (NumberColumn) c;
        } else {
            return null;
        }
    }

    /**
     * @see infovis.Column#compare(int,int)
     */
    public int compare(int row1, int row2) {
        if (row1 == row2)
            return 0;
        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
        double d = (getDoubleAt(row1) - getDoubleAt(row2));
        if (d == 0)
            return 0;
        else if (d < 0)
            return -1;
        else
            return 1;
    }

    public boolean hasUndefinedValue() {
        return undefined != null && undefined.size()==0; 
    }

    public double round(double value) {
        return value;
    }
    
    public double getDoubleMin() {
        return getDoubleAt(getMinIndex());
    }
    
    public double getDoubleMax() {
        return getDoubleAt(getMaxIndex());
    }
}