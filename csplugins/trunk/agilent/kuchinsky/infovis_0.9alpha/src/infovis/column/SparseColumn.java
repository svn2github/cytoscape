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
 * @version $Revision: 1.17 $
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
     * @see infovis.Column#capacity()
     */
    public int capacity() {
	return Integer.MAX_VALUE;
    }

    /**
     * @see infovis.Column#clear()
     */
    public void clear() {
	map.clear();
    }

    /**
     * @see infovis.Column#ensureCapacity(int)
     */
    public void ensureCapacity(int minCapacity) {
    }

    /**
     * @see infovis.Column#size()
     */
    public int size() {
	return map.size();
    }

    /**
     * @see infovis.Column#isValueUndefined(int)
     */
    public boolean isValueUndefined(int i) {
	return map.containsKey(i);
    }

    /**
     * See BasicColumn#setSize(int)
     */
    public void setSize(int newSize) {
    }

    /**
     * @see infovis.Column#setValueUndefined(int, boolean)
     */
    public void setValueUndefined(int i, boolean undef) {
	if (undef) {
	    map.remove(i);
	}
    }
}
