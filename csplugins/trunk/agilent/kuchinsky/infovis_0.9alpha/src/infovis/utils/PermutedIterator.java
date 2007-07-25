/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Iterator over permuted rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class PermutedIterator extends AbstractRowIterator {
    protected Permutation permutation;
    protected int index;
    protected int direction;
    
    public PermutedIterator(int index, Permutation permutation, boolean reverse) {
        this.permutation = permutation;
        this.index = index;
        this.direction = reverse ? -1 : 1;
    }
    
    public PermutedIterator(int index, Permutation permutation) {
        this(index, permutation, false);
    }

    public PermutedIterator(Permutation permutation) {
        this(0, permutation, false);
    }
    
    public boolean hasNext() {
        if (direction == 1)
            return index < permutation.size();
        else
            return direction >= 0;
    }

    public void remove() {
    }

    public int nextRow() {
        int tmp = index;
        index += direction;
        return permutation.getDirect(tmp);
    }

    public int peekRow() {
        return permutation.getDirect(index);
    }
    
    /**
     * @see infovis.utils.RowIterator#copy()
     */
    public RowIterator copy() {
        return new PermutedIterator(index, permutation, direction==-1);
    }
}