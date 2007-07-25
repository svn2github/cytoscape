/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import infovis.Column;

/**
 * DenseColumnComparator is used to wrap a column and use it as
 * a RowComparator. Undefined values are turned into
 * the maximum value.  
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DenseColumnComparator implements RowComparator {
    protected Column column;
    protected int maxIndex;
    
    public DenseColumnComparator(Column column) {
        this.column = column;
        this.maxIndex = column.getMaxIndex();
    }

    public boolean isValueUndefined(int row) {
        return false;
    }

    public int compare(int a, int b) {
        if (a == b) return 0;
        if (column.isValueUndefined(a)) {
            a = maxIndex;
        }
        if (column.isValueUndefined(b)) {
            b = maxIndex;
        }
        if (a == b) return 0;
        return column.compare(a, b);
    }
}
