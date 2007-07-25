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
 * Column containing columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ColumnColumn extends BasicObjectColumn {
    private static final long serialVersionUID = 2746171414424196856L;

    /**
     * Creates a new <code>ColumnColumn</code> object.
     * 
     * @param name
     *            the column name.
     */
    public ColumnColumn(String name) {
        super(name);
    }

    /**
     * Creates a new <code>ColumnColumn</code> object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public ColumnColumn(String name, int reserve) {
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
    public Column get(int index) {
        return (Column) getObjectAt(index);
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
    public void set(int index, Column element) {
        setObjectAt(index, element);
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
    public void setExtend(int index, Column element) {
        super.setExtend(index, element);
    }

    /**
     * Adds an element to the column.
     * 
     * @param element
     *            the element.
     */
    public void add(Column element) {
        super.add(element);
    }

    /**
     * Fills the column with the specified value.
     * 
     * @param val
     *            the value
     */
    public void fill(Column val) {
        super.fill(val);
    }

    /**
     * Comparator for elements of this column.
     * 
     * @param c1
     *            first element.
     * @param c2
     *            second element.
     * 
     * @return the comparison of the elements.
     */
    public final int compare(Column c1, Column c2) {
        return c1.getName().compareToIgnoreCase(c2.getName());
    }

    /**
     * Returns a column as a <code>ColumnColumn</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>ColumnColumn</code> or null if no such column exists or
     *         the column is not a <code>ColumnColumn</code>.
     */
    public static ColumnColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof ColumnColumn) {
            return (ColumnColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>ColumnColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>ColumnColumn</code> or null if no such column exists or
     *         the column is not a <code>ColumnColumn</code>.
     */
    public static ColumnColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof ColumnColumn) {
            return (ColumnColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>ColumnColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>ColumnColumn</code> from a table,
     */
    public static ColumnColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c == null) {
            c = new ColumnColumn(name);
            t.addColumn(c);
        }

        return (ColumnColumn) c;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Column.class;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {
        return compare(get(row1), get(row2));
    }

    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return this;
    }

}
