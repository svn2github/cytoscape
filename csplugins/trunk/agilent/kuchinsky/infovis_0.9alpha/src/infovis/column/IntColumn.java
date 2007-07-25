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
import infovis.utils.RowComparator;
import infovis.utils.Sort;

import java.text.ParseException;

import cern.colt.list.IntArrayList;

/**
 * A Column of integer values.
 * 
 * @version $Revision: 1.48 $
 * @author fekete
 * @infovis.factory ColumnFactory "integer" DENSE
 * @infovis.factory ColumnFactory "int" DENSE
 */
public class IntColumn extends LiteralColumn {
    protected IntArrayList value;

    /**
     * Creates a new IntColumn object.
     * 
     * @param name
     *            the column name.
     */
    public IntColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new IntColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial allocated size.
     */
    public IntColumn(String name, int reserve) {
        super(name);

        value = new IntArrayList(reserve);
    }

    public IntColumn(String name, IntArrayList value) {
        super(name);
        this.value = value;
    }
    
    public void clear() {
        value.clear();
        super.clear();
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public int get(int index) {
        assert ((index >= 0) && (index < size()));

        return value.getQuick(index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     *  
     */
    public void set(int index, int element) {
        assert ((index >= 0) && (index < size()));

        value.setQuick(index, element);
        set(index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element, growing the column if necessary.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     *  
     */
    public void setExtend(int index, int element) {
        assert (index >= 0);
        if (index >= size()) {
            setSize(index + 1);
        }
        set(index, element);
    }
    
    public int addExtend(int index, int element) {
        assert(index >= 0);
        int ret;
        if (index >= size()) {
            setSize(index + 1);
            set(index, ret=element);
        }
        else {
            set(index, ret=get(index)+element);
        }
        return ret;
    }
    
    public int getMin() {
        return get(getMinIndex());
    }
    
    public int getMax() {
        return get(getMaxIndex());
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
     * Fills the column with the specified value.
     * 
     * @param val
     *            the value
     */
    public void fill(int val) {
        undefined = null;
        value.fillFromToWith(0, size() - 1, val);
        super.fill();
        modified();
    }

    public void copyFrom(Column from) {
        if (from instanceof IntColumn) {
            IntColumn new_from = (IntColumn) from;
            setSize(new_from.size());
            value.replaceFromToWithFrom(0, size() - 1, new_from.value,
                    0);
            copyUndefinedFrom(new_from);
            modified();
        } else {
            super.copyFrom(from);
        }
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
        if (v == null) {
            throw new ParseException("null string as in int", 0);
            //return 0;
        }
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
        if (v == null) {
            setValueUndefined(i, true);
        }
        else {
            setExtend(i, parse(v));
        }
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
    public static IntColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof IntColumn) {
            return (IntColumn) c;
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
    public static IntColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof IntColumn) {
            return (IntColumn) c;
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
    public static IntColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new IntColumn(name);
            t.addColumn(c);
        }
        return (IntColumn) c;
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
        System.arraycopy(value.elements(), 0, a, 0, size());
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
        return value.elements();
    }

    public void sort(RowComparator comp) {
        if (comp == null)
            return;
        Sort.sort(value.elements(), 0, size(), comp);
        modified();
    }

    public int capacity() {
        return value.elements().length;
    }

    public void ensureCapacity(int minCapacity) {
        value.ensureCapacity(minCapacity);
    }

    public int size() {
        return value.size();
    }

    public void setSize(int newSize) {
        try {
            disableNotify();
            super.setSize(newSize);
            value.setSize(newSize);
        }
        finally {
            enableNotify();
        }
    }
    
    public IntArrayList getValueReference() {
        return value;
    }
    
}