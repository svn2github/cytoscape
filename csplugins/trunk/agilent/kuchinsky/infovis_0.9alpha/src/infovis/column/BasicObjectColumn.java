/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Abstract base class for columns containing objects
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public abstract class BasicObjectColumn extends BasicColumn {
    private ArrayList value;
    private Comparator order;

    /**
     * Creates a new StringColumn object.
     * 
     * @param name
     *            the column name.
     */
    public BasicObjectColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new ObjectColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public BasicObjectColumn(String name, int reserve) {
        super(name);

        value = new ArrayList(reserve);
    }

    public void clear() {
        value.clear();
        super.clear();
    }

    /**
     * Returns the element at the specified position or <code>null</code> if
     * it is undefined or invalid.
     * 
     * @param index
     *            the index.
     * @return the element at the specified position or <code>null</code> if
     *         it is undefined or invalid.
     */
    public Object getObjectAt(int index) {
        if ((index >= 0) && (index < size()))
            return value.get(index);
        return null;
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
    public void setObjectAt(int index, Object element) {
        assert ((index >= 0) && (index < size()));
        value.set(index, element);
        min_max_updated = false;
        modified();
    }

    public void setValueUndefined(int index, boolean undef) {
        if (index < 0) return;
        if (index > size()) {
            setSize(index+1);
            if (! undef) {
                setObjectAt(index, definedValue());
                return;
            }
        }
        setExtend(index, null);
    }
    
    /**
     * Returns the instance of a value defined with the right type
     * for this column.
     * 
     * @return the instance of a value defined with the right type
     * for this column.
     */
    public abstract Object definedValue();

    public boolean isValueUndefined(int index) {
        if (index < 0 || index >= size())
            return true;
        return value.get(index) == null;
    }
    
    public boolean hasUndefinedValue() {
        return value.indexOf(null) != -1;
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
    public final void setExtend(int index, Object element) {
        assert (index >= 0);
        if (index >= size()) {
            setSize(index + 1);
        }
        setObjectAt(index, element);
    }

    /**
     * Adds a new element in the column,
     * 
     * @param element
     *            the element.
     */
    public void add(Object element) {
        setExtend(size(), element);
    }
    
    public void remove(int index) {
        assert ((index >= 0) && (index < size()));
        value.remove(index);
        min_max_updated = false;
        modified();
    }
    
    public int indexOf(Object o) {
        return value.indexOf(o);
    }
    
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    /**
     * Fills the column with the specified value.
     * 
     * @param val
     *            the value
     */
    public void fill(Object val) {
        for (int i = 0; i < size(); i++) {
            value.set(i, val);
        }
        min_max_updated = true;
        if (size() == 0) {
            minIndex = -1;
            maxIndex = -1;            
        }
        else {
            minIndex = 0;
            maxIndex = 0;
        }
        modified();
    }

    public void copyFrom(Column from) {
        if (from instanceof BasicObjectColumn) {
            BasicObjectColumn new_from = (BasicObjectColumn) from;
            value.clear();
            value.addAll(new_from.value);
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
    public Object parse(String v) throws ParseException {
        try {
            if (getFormat() != null) {
                return (Object) getFormat().parseObject(v);
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return v;
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
    public String format(Object v) {
        if (getFormat() != null) {
            return getFormat().format(v);
        }

        return v.toString();
    }

    public String getValueAt(int i) {
        if (i < 0 || i >= size() || isValueUndefined(i))
            return "";
        return format(getObjectAt(i));
    }

    public void setValueAt(int i, String v) throws ParseException {
        if (v == null) {
            setValueUndefined(i, true);
        }
        else {
            setExtend(i, parse(v));
        }
    }

    public final int compare(Object s1, Object s2) {
        if (order != null)
            return order.compare(s1, s2);
        return s1.hashCode() - s2.hashCode();
    }

    /**
     * Returns the order used to find the min and max for the strings.
     * 
     * @return RowComparator
     */
    public Comparator getOrder() {
        return order;
    }

    /**
     * Sets the order used to find the min and max for the strings.
     * 
     * @param order
     *            The order to set
     */
    public void setOrder(Comparator order) {
        this.order = order;
    }

    public Class getValueClass() {
        return Object.class;
    }

    public int compare(int row1, int row2) {
        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
        Object o1 = getObjectAt(row1);
        Object o2 = getObjectAt(row2);
        return compare(o1, o2);
    }

    public void setSize(int newSize) {
        try {
            disableNotify();
            while (size() < newSize) {
                value.add(null);
            }
        }
        finally {
            enableNotify();
        }
    }

    public int capacity() {
        return value.size();
    }

    public void ensureCapacity(int minCapacity) {
        value.ensureCapacity(minCapacity);
    }

    public int size() {
        return value.size();
    }
    
    public ArrayList getValueReference() {
        return value;
    }
}