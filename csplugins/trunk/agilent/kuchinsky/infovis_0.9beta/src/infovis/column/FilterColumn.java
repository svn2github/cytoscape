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
import infovis.panel.DynamicQuery;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Column managing <code>BitSet</code> s meant for filtering columns with
 * dynamic queries.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.32 $
 */
public class FilterColumn extends BasicObjectColumn implements RowFilter {
    private transient ArrayList dynamicQueries = new ArrayList();

    /**
     * Constructor for FilterColumn.
     * 
     * @param name the name.
     */
    public FilterColumn(String name) {
        super(name);
    }

    /**
     * Constructor for FilterColumn.
     * 
     * @param name the name
     * @param reserve the reserved size
     */
    public FilterColumn(String name, int reserve) {
        super(name, reserve);
    }

    /**
     * Returns the bitIndex for the specified dynamic query or -1 if it is not
     * registered.
     * 
     * @param query
     *            the dynamic query
     * 
     * @return the bitIndex for the specified dynamic query or -1 if it is not
     *         registered.
     */
    public int getDynamicQueryIndex(DynamicQuery query) {
        return dynamicQueries.indexOf(query);
    }

    /**
     * Returns the dynamic query at the specified index or null.
     * @param index the index
     * @return the dynamic query at the specified index or null.
     */
    public DynamicQuery getDynamicQueryAt(int index) {
        return (DynamicQuery) dynamicQueries.get(index);
    }

    /**
     * Returns the number of dynamic queries managed by the
     * filter column.
     * @return the number of dynamic queries managed by the
     * filter column.
     */
    public int getDynamicQueryCount() {
        return dynamicQueries.size();
    }

    /**
     * Returns the bitIndex for the specified dynamic query creating it and
     * adding the dynamic query if is not registered.
     * 
     * @param query
     *            the dynamic query
     * 
     * @return the bitIndex for the specified dynamic query creating it and
     *         adding the dynamic query if is not registered.
     */
    public int findDynamicQueryIndex(DynamicQuery query) {
        int ret = getDynamicQueryIndex(query);
        if (ret != -1)
            return ret;
        return addDynamicQuery(query);
    }

    /**
     * Adds a dynamic query to the list of registered queries.
     * 
     * @param query
     *            the dynamic query
     * 
     * @return the bitIndex for the created query.
     */
    public int addDynamicQuery(DynamicQuery query) {
        int i = getDynamicQueryIndex(query); // check whether it already exist
                                             // first
        if (i == -1) // if not
            i = dynamicQueries.indexOf(null); // try to reuse an already
                                              // allocated index
        if (i == -1) { // if no index is available, create a new one
            dynamicQueries.add(query);
            return dynamicQueries.size() - 1;
        }
        dynamicQueries.set(i, query);
        return i;
    }

    /**
     * Removes a dynamic query from the list of registered queries, also
     * clearing the associated bit from the BitSets.
     * 
     * @param query
     *            the dynamic query
     * 
     * @return the bitIndex for the specified dynamic query or -1 if it wasn't
     *         registered.
     */
    public int removeDynamicQuery(DynamicQuery query) {
        int i = getDynamicQueryIndex(query);
        if (i == -1)
            return i;
        dynamicQueries.set(i, null);
        clearBit(i);
        query.setFilterColumn(null);
        return i;
    }

    /**
     * Applies a <code>DynamicQuery</code> to all the values of a
     * specified <code>RowIterator</code>.
     * 
     * @param query the dynamic query
     * @param iter the RowIterator
     */
    public void applyDynamicQuery(DynamicQuery query, RowIterator iter) {
        int bitIndex = findDynamicQueryIndex(query);

        try {
            disableNotify();
            modified();
            clearBit(bitIndex);
            while (iter.hasNext()) {
                int row = iter.nextRow();
                if (query.isFiltered(row)) {
                    setBit(row, bitIndex);
                }
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public BitSet get(int index) {
        return (BitSet) super.getObjectAt(index);
    }

    /**
     * Returns true if the specified index is filtered.
     * 
     * @param index
     *            the index.
     * 
     * @return true if the specified index is filtered.
     */
    public boolean isFiltered(int index) {
        if ((index < 0) || (index >= size())) {
            return false;
        }
        BitSet bs = get(index);
        if (bs == null)
            return false;
        return !bs.isEmpty();
    }

    /**
     * Clears a specified bit from <code>BitSet</code> at specified index.
     * 
     * @param index
     *            the index.
     * @param bitIndex
     *            the bit.
     */
    public void clearBit(int index, int bitIndex) {
        if (index < 0 || index >= size())
            return;

        BitSet bs = get(index);
        if (bs == null)
            return;
        bs.clear(bitIndex);
        modified(index);
    }

    /**
     * Clears a specified bit from all the <code>BitSet</code>s.
     * 
     * @param bitIndex
     *            the bit.
     */
    public final void clearBit(int bitIndex) {
        for (int index = 0; index < size(); index++) {
            BitSet bs = get(index);
            if (bs != null) {
                bs.clear(bitIndex);
            }
        }
        modified(bitIndex);
    }

    /**
     * Sets a specified bit from <code>BitSet</code> at a specified index,
     * creating the <code>BitSet</code> if required.
     * 
     * @param index the row
     * @param bitIndex the bit index
     */
    public final void setBit(int index, int bitIndex) {
        assert (index >= 0);
        BitSet bs = get(index);
        if (bs == null) {
            bs = new BitSet(bitIndex);
            setExtend(index, bs);
        }
        bs.set(bitIndex);
        modified(index);
    }


    /**
     * Returns a column as a <code>FilterColumn</code> from an
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>FilterColumn</code> or null if no such column exists or
     *         the column is not a <code>FilterColumn</code>.
     */
    public static FilterColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof FilterColumn) {
            return (FilterColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>FilterColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>FilterColumn</code> or null if no such column exists or
     *         the column is not a <code>FilterColumn</code>.
     */
    public static FilterColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof FilterColumn) {
            return (FilterColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>FilterColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>FilterColumn</code> from a table,
     */
    public static FilterColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new FilterColumn(name);
            t.addColumn(c);
        }
        return (FilterColumn) c;
    }
    
    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return BitSet.class;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {

        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
        return compare(get(row1), get(row2));
    }
    
    /**
     * {@inheritDoc}
     */
    public Object definedValue() {
        return new BitSet();
    }
    
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        dynamicQueries = new ArrayList();
     }
}