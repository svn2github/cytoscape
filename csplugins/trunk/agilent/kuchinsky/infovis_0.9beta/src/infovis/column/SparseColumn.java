/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.column;

import infovis.utils.IntSortedMap;

import java.util.Comparator;

/**
 * Base class for sparse columns, i&dot;e&dot; columns containing few elements.
 * A sparse column is implemented with an {@link infovis.utils.IntSortedMap}.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public abstract class SparseColumn extends BasicColumn
    implements Comparator {
    IntSortedMap map;
    protected transient boolean min_max_updated;
	
    /**
     * Constructor for SparseColumn.
     * @param name the colum name.
     */
    public SparseColumn(String name) {
	super(name);
	map = new IntSortedMap();
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
	return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
	map.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
	return map.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int i) {
	return map.containsKey(i);
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
    }

    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
	if (undef) {
	    map.remove(i);
	}
    }
}
