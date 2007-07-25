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


/**
 * A Column of Strings.
 *
 * @version $Revision: 1.37 $
 * @author fekete
 * @infovis.factory ColumnFactory "object" DENSE
 */
public class ObjectColumn extends BasicObjectColumn {
    /**
     * Creates a new StringColumn object.
     *
     * @param name the column name.
     */
    public ObjectColumn(String name) {
	this(name, 10);
    }

    /**
     * Creates a new ObjectColumn object.
     *
     * @param name the column name.
     * @param reserve the initial capacity.
     */
    public ObjectColumn(String name, int reserve) {
	super(name, reserve);
    }
    
    /**
     * Returns the Object at the specified index or null.
     * @param index the index
     * @return the Object at the specified index or
     * null.
     */
    public Object get(int index) {
        return super.getObjectAt(index);
    }
    
    /**
     * Replaces the element at the specified position in this column
     * with the specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public final void set(int index, Object element) {
        super.setObjectAt(index, element);
    }

    /**
     * Returns a column as a <code>ObjectColumn</code> from an
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param index index in the <code>Table</code>
     *
     * @return a <code>ObjectColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>ObjectColumn</code>.
     */
    public static ObjectColumn getColumn(Table t, int index) {
	Column c = t.getColumnAt(index);

	if (c instanceof ObjectColumn) {
	    return (ObjectColumn)c;
	} else {
	    return null;
	}
    }
	
    /**
     * Returns a column as a <code>ObjectColumn</code> from a
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a <code>ObjectColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>ObjectColumn</code>.
     */
    public static ObjectColumn getColumn(Table t, String name) {
	Column c = t.getColumn(name);

	if (c instanceof ObjectColumn) {
	    return (ObjectColumn)c;
	} else {
	    return null;
	}
    }
	
    /**
     * Returns a column as a <code>ObjectColumn</code> from a table,
     * creating it if needed.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a column as a <code>ObjectColumn</code> from a table,
     */
    public static ObjectColumn findColumn(Table t, String name) {
	Column c = t.getColumn(name);
	if (c == null) {
	    c = new ObjectColumn(name);
	    t.addColumn(c);
	}
	return (ObjectColumn)c;
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return new Object();
    }
}
