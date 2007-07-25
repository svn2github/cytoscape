/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.column;


import java.io.Serializable;

import infovis.Column;
import infovis.utils.RowComparator;


/**
 * Comparator for sorting column rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class ColumnComparator implements RowComparator, Serializable {
    private static final long serialVersionUID = 1681090574974854558L;
    protected Column column;
	
    public ColumnComparator(Column column) {
	this.column = column;
    }
	
    /**
     * @see infovis.utils.RowComparator#compare(int, int)
     */
    public int compare(int row1, int row2) {
	return column.compare(row1, row2);
    }
    
    public boolean isValueUndefined(int row) {
        return column.isValueUndefined(row);
    }

}
