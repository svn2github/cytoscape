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
import infovis.utils.CaseInsensitiveComparator;

import java.util.Comparator;


/**
 * A Column of Strings.
 *
 * @version $Revision: 1.39 $
 * @author fekete
 * @infovis.factory ColumnFactory "string" DENSE
 */
public class StringColumn extends BasicObjectColumn {
    private Comparator order;

    /**
     * Creates a new StringColumn object.
     *
     * @param name the column name.
     */
    public StringColumn(String name) {
	this(name, 10);
        order = CaseInsensitiveComparator.getSharedInstance();
    }
    
    /**
     * Creates a new StringColumn object.
     *
     * @param name the column name.
     * @param reserve the initial capacity.
     */
    public StringColumn(String name, int reserve) {
	super(name, reserve);
    }

    /**
     * Returns the element at the specified position in this column.
     *
     * @param index index of element to return.
     *
     * @return the element at the specified position in this column.
     */
    public String get(int index) {
	return (String)getObjectAt(index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(int index, String element) {
        setObjectAt(index, element);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element, growing the column if necessary.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void setExtend(int index, String element) {
        super.setExtend(index, element);
    }

    /**
     * Adds an element to the column.
     *
     * @param element the element.
     */
    public void add(String element) {
        super.add(element);
    }

    /**
     * Fills the column with the specified value
     * 
     * @param val the value
     */
    public void fill(String val) {
        super.fill(val);
    }

    /**
     * Comparator for elements of this column.
     *
     * @param s1 first element.
     * @param s2 second element.
     *
     * @return the comparison of the elements.
     */
    public final int compare(String s1, String s2) {
	if (order != null) {
	    return order.compare(s1, s2);
	}

	return s1.compareToIgnoreCase(s2);
    }
    /**
     * Returns a column as a <code>StringColumn</code> from an
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param index index in the <code>Table</code>
     *
     * @return a <code>StringColumn</code> or null if no such column exists
     *         or the column is not a <code>StringColumn</code>.
     */
    public static StringColumn getColumn(Table t, int index) {
	Column c = t.getColumnAt(index);

	if (c instanceof StringColumn) {
	    return (StringColumn)c;
	} else {
	    return null;
	}
    }
	
    /**
     * Returns a column as a <code>StringColumn</code> from a
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a <code>StringColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>StringColumn</code>.
     */
    public static StringColumn getColumn(Table t, String name) {
	Column c = t.getColumn(name);

	if (c instanceof StringColumn) {
	    return (StringColumn)c;
	} else {
	    return null;
	}
    }

    /**
     * Returns a column as a <code>StringColumn</code> from a table, creating
     * it if needed.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a column as a <code>StringColumn</code> from a table,
     */
    public static StringColumn findColumn(Table t, String name) {
	Column c = t.getColumn(name);

	if (c == null) {
	    c = new StringColumn(name);
	    t.addColumn(c);
	}

	return (StringColumn)c;
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
     * @param order The order to set
     */
    public void setOrder(Comparator order) {
	this.order = order;
    }

    public Class getValueClass() {
	return String.class;
    }

    public int compare(int row1, int row2) {
        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
	return compare(get(row1), get(row2));
    }
    
    public Object definedValue() {
        return "";
    }
    
}
