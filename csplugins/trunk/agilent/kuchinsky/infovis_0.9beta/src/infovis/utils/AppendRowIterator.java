/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Appends two row iterators
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class AppendRowIterator extends RowIteratorProxy {
    protected RowIterator i2;
    public AppendRowIterator(RowIterator i1, RowIterator i2) {
        super(i1);
        this.i2 = i2;
    }
    
    public boolean hasNext() {
        if (iterator.hasNext()) {
            return true;
        }
        if (i2 == null) {
            return false;
        }
        iterator = i2;
        i2 = null;
        return hasNext();//recurse
    }

}
