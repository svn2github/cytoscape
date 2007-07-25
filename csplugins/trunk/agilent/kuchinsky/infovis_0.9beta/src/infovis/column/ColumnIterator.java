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
 * Iterator over a column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class ColumnIterator extends TableIterator {
    protected Column column;
    protected int cur;

    /**
     * Creates a column iterator given a first and last index and 
     * a direction. 
     * @param c the column
     * @param first the first index (inclusive)
     * @param end the end index (exclusive)
     * @param up the direction: true for up and false for down.
     */
    public ColumnIterator(Column c, int first, int end, boolean up) {
        super(first, end, up);
        this.column = c;
        cur = -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int nextRow() {
        cur = row;
        do {
            row += up ? 1 : -1;
        } while (row != end && column.isValueUndefined(row));

        return cur;
    }

    /**
     * {@inheritDoc}
     */
    public int peekRow() {
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove() {
        column.setValueUndefined(cur, true);
    }
}