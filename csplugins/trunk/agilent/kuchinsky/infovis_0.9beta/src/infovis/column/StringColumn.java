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

/**
 * A Column of Strings.
 * 
 * @version $Revision: 1.42 $
 * @author fekete
 * @infovis.factory ColumnFactory "string" DENSE
 */
public class StringColumn extends BasicObjectColumn {
    /**
     * Creates a new StringColumn object.
     * 
     * @param name
     *            the column name.
     */
    public StringColumn(String name) {
        this(name, 10);
        setOrder(CaseInsensitiveComparator.getInstance());
    }

    /**
     * Creates a new StringColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public StringColumn(String name, int reserve) {
        super(name, reserve);
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public String get(int index) {
        return (String) getObjectAt(index);
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
    public void set(int index, String element) {
        setObjectAt(index, element);
    }

    /**
     * Returns a column as a <code>StringColumn</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>StringColumn</code> or null if no such column exists or
     *         the column is not a <code>StringColumn</code>.
     */
    public static StringColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof StringColumn) {
            return (StringColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>StringColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>StringColumn</code> or null if no such column exists or
     *         the column is not a <code>StringColumn</code>.
     */
    public static StringColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof StringColumn) {
            return (StringColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>StringColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>StringColumn</code> from a table,
     */
    public static StringColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c == null) {
            c = new StringColumn(name);
            t.addColumn(c);
        }

        return (StringColumn) c;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return "";
    }

}
