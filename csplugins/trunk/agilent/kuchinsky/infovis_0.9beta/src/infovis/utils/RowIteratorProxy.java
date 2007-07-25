/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class RowIteratorProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class RowIteratorProxy implements RowIterator {
    protected RowIterator iterator;

    public RowIteratorProxy(RowIterator iterator) {
        this.iterator = iterator;
    }

    public RowIterator copy() {
        return new RowIteratorProxy(iterator.copy());
    }
    
    public boolean hasNext() {
        return iterator.hasNext();
    }
    public Object next() {
        return iterator.next();
    }
    public int nextRow() {
        return iterator.nextRow();
    }
    public int peekRow() {
        return iterator.peekRow();
    }
    public void remove() {
        iterator.remove();
    }
    public String toString() {
        return iterator.toString();
    }
}
