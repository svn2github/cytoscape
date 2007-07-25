/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import javax.swing.event.ChangeListener;

import infovis.Column;
import infovis.utils.*;

/**
 * <code>AbstractColumn</code> is the base class for each concrete column.
 * 
 * <p>
 * <code>AbstractColumn</code> implements the notification mechanism and its
 * inhibition.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.24 $
 */
public abstract class AbstractColumn extends ChangeManager implements Column {
    /**
     * The last modified row or -1 if this has no sense.
     */
    private transient int                 lastModifiedRow = MODIFIED_NONE;
    /** Value of lastModifiedRow when more than one row has been modified. */
    public static final int               MODIFIED_ALL    = -2;
    /** Value of lastModifiedRow when no row has been modified. */
    public static final int               MODIFIED_NONE   = -1;

    /**
     * Return the last modified row.
     * 
     * @return the last modified row
     */
    public int getLastModifiedRow() {
        return lastModifiedRow;
    }

    /**
     * Compare two objects that can be null.
     * 
     * @param o1 first object
     * @param o2 second object
     * @return true if both object have the same value or
     * are equal.
     */
    public static boolean equalObj(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if (o1 != null) {
            if (o2 != null) {
                return o1.equals(o2);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        // Necessary if this class is used in non-homogenous structures,
        // i.e., EventListenerLists
        if (!(obj instanceof Column))
            return false;

        Column c = (Column) obj;
        if (!equalObj(getName(), c.getName())) {
            return false;
        }
        if (size() != c.size()) {
            return false;
        }
        if (!isInternal() == c.isInternal()) {
            return false;
        }
        if (!equalObj(getFormat(), c.getFormat())) {
            return false;
        }
        if (!getValueClass().equals(c.getValueClass())) {
            return false;
        }
        if (!equalObj(getMetadata(), c.getMetadata())) {
            return false;
        }
        return compareValues(c);
    }

    /**
     * Compare the values of this column and the specified column.
     * 
     * @param c
     *            the column to compare values from
     * @return <code>true</code> if the values match, <code>false</code>
     *         otherwise
     */
    public boolean compareValues(Column c) {
        for (int i = 0; i < size(); i++) {
            if (isValueUndefined(i)) {
                if (!c.isValueUndefined(i)) {
                    return false;
                }
            }
            else {
                if (!equalObj(getValueAt(i), c.getValueAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Fire the notification.
     */
    protected void fireChanged() {
        try {
            super.fireChanged();
        } finally {
            lastModifiedRow = MODIFIED_NONE;
        }
    }
    
    /**
     * Adds a <code>ChangeListener</code> to call when this
     * column is modified.
     * 
     * @param listener the listener to add
     */
    public void addChangeListener(ChangeListener listener) {
        lastModifiedRow = MODIFIED_ALL;
        super.addChangeListener(listener);
    }

    protected boolean modified(int row) {
        if (lastModifiedRow == MODIFIED_NONE) {
            lastModifiedRow = row;
        }
        else if (lastModifiedRow != row
                && lastModifiedRow != MODIFIED_ALL) {
            lastModifiedRow = MODIFIED_ALL;
        }
        return modified();
    }
    
    protected void readonly(String msg) throws ReadOnlyColumnException {
        throw new ReadOnlyColumnException(msg);
    }

    protected void readonly() throws ReadOnlyColumnException {
        readonly("Trying to change a read-only column");
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * Returns the list of uniq elements of this columns associated with
     * their count.
     * 
     * <p>The list contains tuples: the index of a representative of the value
     * and the number of repetition of this value.
     * 
     * @param comp a comparator used to order the value or <code>null</code> if
     * it should appear in the order specified by the column.
     * 
     * @return a <code>IntIntSortedMap</code> that can be read using an iterator.
     */
    public IntIntSortedMap computeValueMap(RowComparator comp) {
        IntIntSortedMap map = new IntIntSortedMap(comp == null ? this : comp);
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int v = iter.nextRow();
            if (comp != null && comp.isValueUndefined(v)) {
                continue;
            }
            if (map.containsKey(v)) {
                map.put(v, map.get(v) + 1);
            }
            else {
                map.put(v, 1);
            }
        }

        return map;
    }

    /**
     * Returns the list of uniq elements of this columns associated with
     * their count.
     * 
     * <p>The list contains tuples: the index of a representative of the value
     * and the number of repetition of this value.
     * 
     * @return a <code>IntIntSortedMap</code> that can be read using an iterator.
     */
    public IntIntSortedMap computeValueMap() {
        return computeValueMap(null);
    }
}
