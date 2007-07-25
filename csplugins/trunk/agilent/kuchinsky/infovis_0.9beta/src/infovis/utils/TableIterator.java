/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;


/**
 * Iterator over table rows.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */
public class TableIterator extends AbstractRowIterator {
    protected int row;
    protected int end;
    protected boolean up;

    /**
     * Creates an iterator over a table given its first row, its end
     * (1 + last) and its direction.
     * 
     * @param first the first row
     * @param end the end of the iterator
     * @param up true if the iterator goes from lower value to upper values,
     *  false otherwise
     */
    public TableIterator(int first, int end, boolean up) {
        this.row = first;
        this.end= end;
        this.up = up;
    }
    
    /**
     * Creates an iterator over a table given its first row, its end
     * (1 + last). The direction is guessed.
     * 
     * @param first the first row
     * @param end the end of the iterator
     */
    public TableIterator(int first, int end) {
        this(first, end, first < end);
    }
    

    public boolean hasNext() {
        if (up)
            return row < end;
        else
            return row > end;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int nextRow() {
        int ret = row;
        if (up){
        	row++;
        } else {
        	row--;
        }
        return ret;
    }

    public int peekRow() {
        return row;
    }
    /**
     * @see infovis.utils.RowIterator#copy()
     */
    public RowIterator copy() {
        return new TableIterator(row, end, up);
    }
}
