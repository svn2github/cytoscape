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

import cern.colt.list.LongArrayList;


/**
 * A Column of longs.
 * 
 * @version $Revision: 1.40 $
 * @author fekete
 * @infovis.factory ColumnFactory "long" DENSE
 */
public class LongColumn extends LiteralColumn {
    protected LongArrayList value;

    /**
     * Creates a new LongColumn object.
     * 
     * @param name the column name.
     */
    public LongColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new LongColumn object.
     * 
     * @param name the column name.
     * @param reserve the initial capacity.
     */
    public LongColumn(String name, int reserve) {
        super(name);
        
        value = new LongArrayList(reserve);
    }
    
    public void clear() {
        value.clear();
        super.clear();
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public long get(int index) {
	assert((index >= 0) && (index < size()));
        return value.getQuick(index);
    }

    /**
     * Replaces the element at the specified position in this column
     * with the specified element.
     * 
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(int index, long element) {
	assert((index >= 0) && (index < size()));
        value.setQuick(index, element);
        set(index);
    }

    /**
     * Replaces the element at the specified position in this column
     * with the specified element, growing the column if necessary.
     * 
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
  	
    public void setExtend(int index, long element) {
	assert(index >= 0);
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
    
    public long getMin() {
        return get(getMinIndex());
    }
    
    public long getMax() {
        return get(getMaxIndex());
    }
	
    /**
     * Adds a new element in the column.
     * 
     * @param element the element.
     */
    public final void add(long element) {
        setExtend(size(), element);
    }

    /**
     * Fills the column with the specified value
     * 
     * @param val the value
     */
    public void fill(long val) {
        undefined = null;
        value.fillFromToWith(0, size() - 1, val);
        super.fill();
        modified();
    }
    
//    public void copyFrom(Column from) {
//        if (from instanceof LongColumn) {
//            LongColumn new_from = (LongColumn) from;
//            setSize(from.size());
//            value.replaceFromToWithFrom(0, size() - 1, new_from.value,
//                    0);
//            copyUndefinedFrom(new_from);
//            modified();
//        }
//        else {
//            super.copyFrom(from);
//        }
//    }

    /**
     * Parse a string and return the value for the column.
     *
     * @param v the string representation of the value
     *
     * @return the value
     *
     * @throws ParseException if the value cannot be parsed
     */
    public long parse(String v) throws ParseException {
    	if (v == null) {
            throw new ParseException("null value as a long", 0);
        }
    	try {
	    if (getFormat() != null) {
		return ((Number)getFormat().parseObject(v)).longValue();
	    }

	    return Long.parseLong(v);
        }
    	catch(Exception e) {
	    throw new ParseException(e.getMessage(), 0);
    	}
    }

    /**
     * Returns the string representation of a value according to the current format.
     *
     * @param v the value
     *
     * @return the string representation.
     */
    public String format(long v) {
        if (getFormat() != null) {
            return getFormat().format(new Long(v));
        }

        return Long.toString(v);
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
     * Returns a column as a <code>LongColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t the <code>Table</code>
     * @param index index in the <code>Table</code>
     * 
     * @return a <code>LongColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>LongColumn</code>.
     */
    public static LongColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof LongColumn) {
            return (LongColumn)c;
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns a column as a <code>LongColumn</code> from a
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a <code>LongColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>LongColumn</code>.
     */
    public static LongColumn getColumn(Table t, String name) {
	Column c = t.getColumn(name);

	if (c instanceof LongColumn) {
	    return (LongColumn)c;
	} else {
	    return null;
	}
    }
	
    /**
     * Returns a column as a <code>LongColumn</code> from a table,
     * creating it if needed.
     * 
     * @param t the <code>Table</code>
     * @param name the column name.
     * 
     * @return a column as a <code>LongColumn</code> from a table,
     */
    public static LongColumn findColumn(Table t, String name) {
    	Column c = t.getColumn(name);
    	if (c == null) {
	    c = new LongColumn(name);
	    t.addColumn(c);
    	}
    	return (LongColumn)c;
    }
    
    /**
     * @see infovis.Column#getValueClass()
     */
    public Class getValueClass() {
	return Long.class;
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
	return (int)get(row);
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
	setExtend(row, (long)Math.round(v));
    }

    /**
     * @see infovis.column.NumberColumn#setLongAt(int, long)
     */
    public void setLongAt(int row, long v) {
	setExtend(row, v);
    }
    
    /**
     * @see infovis.column.NumberColumn#round(double)
     */
    public double round(double value) {
        return Math.round(value);
    }
    
    public String format(double value) {
        return format((long)value);
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
    public LongArrayList getValueReference() {
        return value;
    }
}
