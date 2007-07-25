/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import infovis.Column;
import infovis.Table;
import infovis.column.ColumnFilter;
import infovis.column.ColumnNameComparator;
import infovis.column.filter.InternalFilter;

import java.util.*;

import javax.swing.event.ChangeEvent;

/**
 * Proxy Table filtering Columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class FilteredTable extends TableProxy implements ColumnFilter {
    protected Column[]     columns;
    protected ColumnFilter filter;
    protected Comparator   comparator;

    /**
     * Create a Filtered Table from a reference table, a column filter
     * and a comparator that specifies the order of columns in the table.
     * 
     * @param table the table
     * @param filter the filter
     * @param comp the comparator for the order of the columns.
     */
    public FilteredTable(Table table, ColumnFilter filter, Comparator comp) {
        super(table);
        this.filter = filter;
        this.comparator = comp;
        update();
    }

    /**
     * Create a Filtered Table from a reference table and a column filter.
     * @param table the table
     * @param filter the filter
     */
    public FilteredTable(Table table, ColumnFilter filter) {
        this(table, filter, ColumnNameComparator.sharedInstance());
    }

    /**
     * Create a Filtered Table from a reference table.
     * @param table the table
     */
    public FilteredTable(Table table) {
        this(table, InternalFilter.sharedInstance());
    }

    protected void update() {
        int count = super.getColumnCount();
        ArrayList cols = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            Column c = super.getColumnAt(i);
            if (!filter(c)) {
                cols.add(c);
            }
        }
        columns = new Column[cols.size()];
        cols.toArray(columns);
        if (comparator != null)
            Arrays.sort(columns, comparator);
    }

    /**
     * {@inheritDoc}
     */
    public boolean filter(Column c) {
        return filter != null && filter.filter(c);
    }

    /**
     * @see infovis.Table#getColumn(String)
     */
    public Column getColumn(String name) {
        // pass internal name queries.
        if (name.length() > 0 && name.charAt(0) == INTERNAL_PREFIX)
            return super.getColumn(name);
        int index = indexOf(name);
        if (index == -1)
            return null;
        return getColumnAt(index);
    }

    /**
     * @see infovis.Table#getColumnCount()
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Sorts the column according the the specified <code>Comparator</code>.
     * 
     * @param comp
     *            the Comparator.
     */
    public void sortColumns(Comparator comp) {
        comparator = comp;
        update();
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumnAt(int index) {
        if (index < 0 || index >= columns.length)
            return null;
        return columns[index];
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(String name) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getName().equals(name))
                return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(Column column) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i] == column)
                return i;
        }
        return -1;
    }

    /**
     * Returns the comparator.
     * 
     * @return Comparator
     */
    public Comparator getComparator() {
        return comparator;
    }

    /**
     * Returns the filter.
     * 
     * @return ColumnFilter
     */
    public ColumnFilter getFilter() {
        return filter;
    }

    /**
     * Sets the comparator.
     * 
     * @param comparator
     *            The comparator to set
     */
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
        update();
    }

    /**
     * Sets the filter.
     * 
     * @param filter
     *            The filter to set
     */
    public void setFilter(ColumnFilter filter) {
        this.filter = filter;
        update();
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        update();
        super.stateChanged(e);
    }
}
