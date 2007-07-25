/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.utils.*;

/**
 * Various algorithms on columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class Algorithms {
    
    /**
     * Computes the index of the minimum and maximum elements contained
     * in a specified column taking into account a <code>Permutation</code>.
     * 
     * @param c the column
     * @param perm the permutation or <code>null</code>
     * @param p a pair of integers that will be returned or <code>null</code> if
     * a fresh one is wanted.
     * @return a pair of indexes for the minimum and maximum values contained
     * in the column taking into accound the permutation.
     */
    public static IntPair computeMinMax(Column c, Permutation perm, IntPair p) {
        if (perm == null || c.getMinIndex() == -1) {
            p.first = c.getMinIndex();
            p.second = c.getMaxIndex();
            return p;
        }
        else {
            return computeMinMax(c, perm.iterator(), p);
        }
    }
    
    /**
     * Computes the index of the minimum and maximum elements contained
     * in a specified column taking into account a <code>Permutation</code>.
     * 
     * @param c the column
     * @param perm the permutation or <code>null</code>
     * @return a pair of indexes for the minimum and maximum values contained
     * in the column taking into accound the permutation.
     */
    public static IntPair computeMinMax(Column c, Permutation perm) {
        return computeMinMax(c, perm, new IntPair());
    }

    /**
     * Computes the index of the minimum and maximum elements contained
     * in a specified column taking into account a <code>RowIterator</code>.
     * 
     * @param c the column
     * @param iter the row iterator
     * @param p a pair of integers that will be returned or <code>null</code> if
     * a fresh one is wanted.
     * @return a pair of indexes for the minimum and maximum values contained
     * in the column taking into accound the row iterator.
     */
    public static IntPair computeMinMax(Column c, RowIterator iter, IntPair p) {
        int row;
        if (iter.hasNext()) {
            row = iter.nextRow();
            p.first = row;
            p.second = row;
        }
        else {
            p.first = -1;
            p.second = -1;
            return p;
        }
        while (iter.hasNext()) {
            row = iter.nextRow();
            if (c.isValueUndefined(row)) continue;
            if (c.compare(row, p.first) < 0) {
                p.first = row;
            }
            if (c.compare(row, p.second) > 0) {
                p.second = row;
            }
        }
        return p;
    }
    


    /**
     * Computes the index of the minimum and maximum elements contained
     * in a specified column taking into account a <code>RowIterator</code>.
     * 
     * @param c the column
     * @param iter the row iterator
     * @return a pair of indexes for the minimum and maximum values contained
     * in the column taking into accound the row iterator.
     */
    public static IntPair computeMinMax(Column c, RowIterator iter) {
        return computeMinMax(c, iter, new IntPair());
    }

    /**
     * Increments a NumberColumn at a specified row by a specified amount,
     * setting it if it is undefined.
     *  
     * @param col the number column
     * @param row the row
     * @param v the value to add
     * @return the computed sum
     */
    public static double incrColumn(NumberColumn col, int row, double v) {
        if (! col.isValueUndefined(row)) {
            v += col.getDoubleAt(row);
        }
        col.setDoubleAt(row, v);
        return v;
    }
    
    /**
     * Increments a NumberColumn at a specified row by one,
     * setting it if it is undefined.
     *  
     * @param col the number column
     * @param row the row
     * @return the computed sum
     */
    public static double incrColumn(NumberColumn col, int row) {
        return incrColumn(col, row, 1);
    }
}
