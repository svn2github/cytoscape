/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;


/**
 * Class FilteredRowComparator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class FilteredRowComparator implements RowComparator {
    protected RowComparator comparator;
    protected RowFilter filter;

    public FilteredRowComparator(RowComparator comparator, RowFilter filter) {
        this.comparator = comparator;
        this.filter = filter;
    }
    
    public FilteredRowComparator(RowFilter filter) {
        this(IdRowComparator.getInstance(), filter);
    }
    
    public boolean isValueUndefined(int row) {
        return filter.isFiltered(row) ||
            comparator.isValueUndefined(row);
    }

    public int compare(int a, int b) {
        if (isValueUndefined(a)) {
            if (isValueUndefined(b)) {
                return a-b;
            }
            // undefined values always larger than
            // defined ones
            return 1;
        }
        else if (isValueUndefined(b)) {
            return -1;
        }
        return comparator.compare(a, b);
    }

}
