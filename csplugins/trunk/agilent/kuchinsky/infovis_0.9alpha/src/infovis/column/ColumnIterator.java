/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.utils.TableIterator;

/**
 * Class ColumnIterator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class ColumnIterator extends TableIterator {
    protected Column column;
    protected int cur;

    public ColumnIterator(Column c, int first, int end, boolean up) {
        super(first, end, up);
        this.column = c;
        cur = -1;
    }
    
    public int nextRow() {
        cur = row;
        do {
            row += up ? 1 : -1;
        } while (row < end && column.isValueUndefined(row));

        return cur;
    }

    public int peekRow() {
        return row;
    }
    
    public void remove() {
        column.setValueUndefined(cur, true);
    }
}