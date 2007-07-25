/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.function.IntComparator;



/**
 * Interface for sorting according to row values.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public interface RowComparator extends IntComparator {
    /**
     * Returns <code>true</code> if the value at the specified row is undefined.
     * 
     * @param row the row.
     * 
     * @return <code>true</code> if the value at the specified row is undefined.
     */
    public boolean isValueUndefined(int row);
}
