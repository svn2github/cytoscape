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

import java.text.ParseException;

import cern.colt.list.FloatArrayList;

/**
 * A Column of float values, implemented by Colt
 * FloatArrayList.
 * 
 * @version $Revision: 1.43 $
 * @author fekete
 * @infovis.factory ColumnFactory "float" DENSE
 */
public class FloatColumn extends LiteralColumn {
    protected FloatArrayList value;

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     */
    public FloatColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public FloatColumn(String name, int reserve) {
        super(name);

        value = new FloatArrayList(reserve);
    }

    /**
     * Creates a new FloatColumn object backed
     * on a Colt FloatArrayList.
     *
     * @param name the column name.
     * @param value the value list which will be used, not copied.
     */
    public FloatColumn(String name, FloatArrayList value) {
        super(name);
        this.value = value;
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public float get(int index) {
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
     */
    public void set(int index, float element) {
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
     */
    public void setExtend(int index, float element) {
        assert (index >= 0);
        if (index >= size()) {
            if (index == size()) {
                value.setSize(index+1);
            }
            else {
                setSize(index + 1);
            }
        }
        set(index, element);
    }
    
    /**
     * Returns the minum value of this column.
     * @return the minum value of this column.
     */
    public float getMin() {
        return get(getMinIndex());
    }
    
    /**
     * Returns the maximum value of this column.
     * @return the maximum value of this column.
     */
    public float getMax() {
        return get(getMaxIndex());
    }

    /**
     * Adds a new element in the column.
     * 
     * @param element
     *            the element.
     */
    public final void add(float element) {
        setExtend(size(), element);
    }

    /**
     * Fills the column with the specified value.
     * 
     * @param val
     *            the value
     */
    public void fill(float val) {
        undefined = null;
        value.fillFromToWith(0, size() - 1, val);
        super.fill();
        modified();
    }

//    public void copyFrom(Column from) {
//        if (from instanceof FloatColumn) {
//            FloatColumn new_from = (FloatColumn) from;
//            setSize(from.size());
//            value.replaceFromToWithFrom(0, size() - 1, new_from.value,
//                    0);
//            copyUndefinedFrom(new_from);
//            modified();
//        } else {
//            super.copyFrom(from);
//        }
//    }

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
    public float parse(String v) throws ParseException {
        if (v == null)
            throw new ParseException("null specified as an float", 0);
        try {
            if (getFormat() != null) {
                return ((Number) getFormat().parseObject(v))
                        .floatValue();
            }

            return Float.parseFloat(v);
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
    public String format(float v) {
        if (getFormat() != null) {
            return getFormat().format(new Float(v));
        }

        return Float.toString(v);
    }

    /**
     * @see infovis.Column#getValueAt(int)
     */
    public String getValueAt(int i) {
        if (i < 0 || i >= size() || isValueUndefined(i))
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
     * Returns a column as a <code>FloatColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>FloatColumn</code> or null if no such column exists or
     *         the column is not a <code>FloatColumn</code>.
     */
    public static FloatColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof FloatColumn) {
            return (FloatColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>FloatColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>FloatColumn</code> or null if no such column exists or
     *         the column is not a <code>FloatColumn</code>.
     */
    public static FloatColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof FloatColumn) {
            return (FloatColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>FloatColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>FloatColumn</code> from a table,
     */
    public static FloatColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new FloatColumn(name);
            t.addColumn(c);
        }
        return (FloatColumn) c;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Float.class;
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
    public float getFloatAt(int row) {
        return get(row);
    }

    /**
     * {@inheritDoc}
     */
    public int getIntAt(int row) {
        return (int) get(row);
    }

    /**
     * {@inheritDoc}
     */
    public long getLongAt(int row) {
        return (long) get(row);
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        setExtend(row, (float) v);
    }

    /**
     * {@inheritDoc}
     */
    public void setFloatAt(int row, float v) {
        setExtend(row, v);
    }
    
    /**
     * {@inheritDoc}
     */
    public double round(double value) {
        return (float) value;
    }

    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        return format((float) value);
    }

    /**
     * Returns the array of double values copied from the column.
     * 
     * @param a
     *            an array of double with at least the column size or
     *            <code>null</code>.
     * 
     * @return the array of doubles values copied from the column.
     */
    public float[] toArray(float[] a) {
        if (a == null) {
            a = new float[size()];
        }
        System.arraycopy(value, 0, a, 0, size());
        return a;
    }

    /**
     * Returns the array of double values used by the column. BEWARE! no copy is
     * done for performance reasons and the array should only be read. It may
     * become out of sync with the column if the column is resized.
     * 
     * @return the array of double values used by the column.
     */
    public float[] toArray() {
        return value.elements();
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return value.elements().length;
    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
        value.ensureCapacity(minCapacity);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return value.size();
    }

    /**
     * {@inheritDoc}
     */
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
    /**
     * Returns the FloatArrayList backing the
     * implementation of the column (USE WITH CARE).
     * @return the FloatArrayList backing the
     * implementation of the column.
     */
    public FloatArrayList getValueReference() {
        return value;
    }

}