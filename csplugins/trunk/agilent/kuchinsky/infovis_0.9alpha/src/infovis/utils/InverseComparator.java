/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class InverseComparator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class InverseComparator implements RowComparator {
    protected RowComparator comparator;
    
    public InverseComparator(RowComparator comparator) {
        this.comparator = comparator;
    }
    
    public int compare(int row1, int row2) {
        if (comparator == null) {
            return row2 - row1;
        }
        return comparator.compare(row2, row1);
    }
    
    public boolean isValueUndefined(int row) {
        if (comparator == null)
            return false;
        return comparator.isValueUndefined(row);
    }
    
    public RowComparator getComparator() {
        return comparator;
    }

}
