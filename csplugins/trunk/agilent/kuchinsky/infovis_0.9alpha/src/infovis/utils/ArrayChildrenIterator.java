/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class ArrayChildrenIterator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */

public class ArrayChildrenIterator implements RowIterator {
    int index;
    int[] children;

    public ArrayChildrenIterator(int index, int[] children) {
        this.index = index;
        this.children = children;
    }

    public ArrayChildrenIterator(int[] children) {
        this(0, children);
    }

    public boolean hasNext() {
        return index < children.length;
    }

    public Object next() {
        return new Integer(nextRow());
    }

    public void remove() {
    }

    public int nextRow() {
        if (index >= children.length)
            return -1;
        return children[index++];
    }

    public int peekRow() {
        if (index >= children.length)
            return -1;
        return children[index];
    }
    /**
     * @see infovis.utils.RowIterator#copy()
     */
    public RowIterator copy() {
        return new ArrayChildrenIterator(index, children);
    }

}