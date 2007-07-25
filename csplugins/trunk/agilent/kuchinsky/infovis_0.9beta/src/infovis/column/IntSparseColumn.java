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
import infovis.utils.*;
import infovis.utils.RBTree.RBNode;
import infovis.utils.RBTree.RBNodeIterator;

import java.text.ParseException;

/**
 * Class IntSparseColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 * @infovis.factory ColumnFactory "integer" SPARSE
 */
public class IntSparseColumn extends BasicColumn implements NumberColumn {
    protected IntIntSortedMap value;
    protected int size;

    public IntSparseColumn(String name) {
        super(name);
        value = new IntIntSortedMap();
        size = 0;
    }

    public void setSize(int newSize) {
        size = newSize;
        for (RowIterator iter = value.keyIterator(newSize); iter.hasNext(); ) {
            iter.remove();
        }
    }

    public boolean isValueUndefined(int i) {
        return i < 0 || i >= size || !value.containsKey(i);
    }

    public void setValueUndefined(int i, boolean undef) {
        if (undef == isValueUndefined(i)) return;
        if (undef) {
            value.remove(i);
        } else {
            value.put(i, 0);
        }
        min_max_updated = false;
        modified(i);
    }

    public int size() {
        return size;
    }
    
    public boolean hasUndefinedValue() {
        return value.size() != size();
    }

    public void clear() {
        value.clear();
        size = 0;
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

    public int capacity() {
        return Integer.MAX_VALUE;
    }

    public int get(int index) {
        assert(!isValueUndefined(index));
        return value.get(index);
    }

    public void set(int index, int element) {
        assert(index >= 0 && index < size);
        value.put(index, element);
        min_max_updated = false;
        modified(index);
    }

    public void setExtend(int index, int element) {
        size = Math.max(index+1, size);
        set(index, element);
    }

    /**
     * Adds a new element in the column,
     * 
     * @param element
     *            the element.
     */
    public void add(int element) {
        setExtend(size(), element);
    }
    
    /**
     * Parse a string and return the value for the column.
     * 
     * @param v
     *            the string representation of the value
     * 
     * @return the value
     * 
     * @throws ParseException
     *             if the value cannot be parsed
     */
    public int parse(String v) throws ParseException {
        if (v == null)
            return 0;
        try {
            if (getFormat() != null) {
                return ((Number) getFormat().parseObject(v)).intValue();
            }

            return Integer.parseInt(v);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /**
     * Returns the string representation of a value according to the current
     * format.
     * 
     * @param v
     *            the value
     * 
     * @return the string representation.
     */
    public String format(int v) {
        if (getFormat() != null) {
            return getFormat().format(new Integer(v));
        }

        return Integer.toString(v);
    }

    /**
     * Returns a column as a <code>IntColumn</code> from an <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>IntColumn</code> or null if no such column exists or
     *         the column is not a <code>IntColumn</code>.
     */
    public static IntSparseColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof IntSparseColumn) {
            return (IntSparseColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as an <code>IntColumn</code> from a <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>IntColumn</code> or null if no such column exists or
     *         the column is not a <code>IntColumn</code>.
     */
    public static IntSparseColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof IntSparseColumn) {
            return (IntSparseColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>IntColumn</code> from a table, creating it
     * if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>IntColumn</code> from a table,
     */
    public static IntSparseColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new IntSparseColumn(name);
            t.addColumn(c);
        }
        return (IntSparseColumn) c;
    }

    /**
     * @see infovis.Column#getValueClass()
     */
    public Class getValueClass() {
        return Integer.class;
    }

    /**
     * @see infovis.column.NumberColumn#getDoubleAt(int)
     */
    public double getDoubleAt(int row) {
        return get(row);
    }

    /**
     * @see infovis.column.NumberColumn#getFloatAt(int)
     */
    public float getFloatAt(int row) {
        return get(row);
    }

    /**
     * @see infovis.column.NumberColumn#getIntAt(int)
     */
    public int getIntAt(int row) {
        return get(row);
    }

    /**
     * @see infovis.column.NumberColumn#getLongAt(int)
     */
    public long getLongAt(int row) {
        return get(row);
    }

    /**
     * @see infovis.column.NumberColumn#setDoubleAt(int, double)
     */
    public void setDoubleAt(int row, double v) {
        setExtend(row, (int) Math.round(v));
    }

    /**
     * @see infovis.column.NumberColumn#setIntAt(int, int)
     */
    public void setIntAt(int row, int v) {
        setExtend(row, v);
    }

    /**
     * @see infovis.column.NumberColumn#setLongAt(int, long)
     */
    public void setLongAt(int row, long v) {
        setExtend(row, (int) v);
    }

    /**
     * @see infovis.column.NumberColumn#round(double)
     */
    public double round(double value) {
        return Math.round(value);
    }

    public String format(double value) {
        return format((int) value);
    }


    public void ensureCapacity(int minCapacity) {
    }


    /**
     * @see infovis.Column#getValueAt(int)
     */
    public String getValueAt(int i) {
        if (i >= size() || isValueUndefined(i))
            return null;
        return format(get(i));
    }

    /**
     * @see infovis.Column#setValueAt(int, String)
     */
    public void setValueAt(int i, String v) throws ParseException {
        setExtend(i, parse(v));
    }
    public IntIntSortedMap getValueReference() {
        return value;
    }

    public void setFloatAt(int row, float v) {
        set(row, (int)v);
    }
    
    public int getMin() {
        return get(getMinIndex());
    }

    public int getMax() {
        return get(getMaxIndex());
    }
    

    public double getDoubleMin() {
        return getDoubleAt(getMinIndex());
    }

    public double getDoubleMax() {
        return getDoubleAt(getMaxIndex());
    }
    /**
     * Returns the array of integer values copied from the column.
     * 
     * @param a
     *            an array of int with at least the column size or
     *            <code>null</code>.
     * 
     * @return the array of integer values copied from the column.
     */
    public int[] toArray(int[] a) {
        if (a == null) {
            a = new int[size()];
        }
        for (RBNodeIterator iter = value.nodeIterator(); iter.hasNext(); ) {
            RBNode node = (RBNode)iter.next();
            a[node.getKey()] = node.getValue();
        }
        return a;
    }

    /**
     * Returns the array of integer values used by the column. BEWARE! no copy
     * is done for performance reasons and the array should only be read. It may
     * become out of sync with the column if the column is resized.
     * 
     * @return the array of integer values used by the column.
     */
    public int[] toArray() {
        return toArray(null);
    }
    
    public RowIterator iterator() {
        return value.keyIterator();
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
        double d = (get(row1) - get(row2));
        if (d == 0)
            return 0;
        else if (d < 0)
            return -1;
        else
            return 1;
    }   
}