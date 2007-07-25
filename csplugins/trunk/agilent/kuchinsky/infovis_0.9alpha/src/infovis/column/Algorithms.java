/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;

/**
 * Various algorithms on columns
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class Algorithms {
    
    public static int[] computeMinMax(Column column, RowFilter filter) {
        int minIndex = -1;
        int maxIndex = -1;
        
        for (RowIterator iter = column.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            if (! filter.isFiltered(row)) {
                if (minIndex == -1) {
                    minIndex = row;
                    maxIndex = row;
                }
                else if (column.compare(minIndex, row) > 0) {
                    minIndex = row;
                }
                else if (column.compare(row, maxIndex) > 0) {
                    maxIndex = row;
                }
            }
        }
        if (minIndex == -1)
            return null;
        int[] ret = new int[2];
        ret[0] = minIndex;
        ret[1] = maxIndex;
        return ret;
    }
    
    public static double incrColumn(NumberColumn col, int row, double v) {
        if (! col.isValueUndefined(row)) {
            v += col.getDoubleAt(row);
        }
        col.setDoubleAt(row, v);
        return v;
    }
    
    public static double incrColumn(NumberColumn col, int row) {
        return incrColumn(col, row, 1);
    }
}
