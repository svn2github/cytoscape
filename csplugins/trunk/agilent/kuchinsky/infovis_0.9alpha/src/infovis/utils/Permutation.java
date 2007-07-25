/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.io.IOException;
import java.io.Serializable;

import infovis.column.IntColumn;


/**
 * Maintain a permutation of indices.
 * 
 * <p>Maintain two tables called <code>direct</code> and <code>inverse</code>.
 * <p>The <code>direct</code> table usually is the result of a indirect sort
 * over a column.  It contains the index of the rows sorted in a specific
 * order.  For example, if a table containing {6, 7, 5} is sorted in
 * increasing order, the direct table will contain {2, 0, 1}.
 * <p>The <code>inverse</code> table contains the index
 * of a given row.  With the previous example, it contains 
 * {1, 2, 0} meaning that the index or rows {0,1,2} are {1,2,0}.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public class Permutation implements RowComparator, Serializable {
    protected IntColumn direct;
    protected transient IntColumn inverse;
    protected int inverseCount;

    public Permutation(IntColumn direct, IntColumn inverse) {
        this.direct = direct;
        this.inverse = inverse;
    }
    /**
     * Constructor for Permutation.
     * 
     * @param direct the index of the permuted rows which is not copied
     * but stored directly.
     */
    public Permutation(IntColumn direct) {
        this(direct, new IntColumn("#inverse"));
        recomputeInverse();
    }
    
    /**
     * Default constructor.
     */
    public Permutation() {
        this(new IntColumn("#direct"), new IntColumn("#inverse"));
    }
    
    public void reserve(int size) {
        direct.ensureCapacity(size);
        inverse.ensureCapacity(size);
    }

    public void clear() {
        direct.clear();
        inverse.clear();
        inverseCount = 0;
    }
    
    
    
    public void fillPermutation(int size, RowComparator comp) {
        direct.clear();
        for (int i = 0; i < size; i++) {
            if (comp.isValueUndefined(i)) {
//                inverse.setExtend(i, -1);
            }
            else {
                direct.add(i);
            }
        }
        recomputeInverse();
    }
    
    public void fillPermutation(int size, RowComparator comp, RowFilter filter) {
       clear();
       for (int i = 0; i < size; i++) {
           if ((comp != null && comp.isValueUndefined(i)) || filter.isFiltered(i)) {
//               inverse.setExtend(i, -1);
           }
           else {
               direct.add(i);
           }
       }
       recomputeInverse();
   }

    /**
     * Returns the row at a specified index.
     *
     * @param i the index
     *
     * @return the row column at a specified index.
     */
    public int getDirect(int i) {
        return direct.get(i);
    }
    
    public int getDirectCount() {
        return direct.size();
    }

    /**
     * Return the index of a specified row.
     *
     * @param i the row
     *
     * @return the index of a specified row.
     */
    public int getInverse(int i) {
        if (inverse.isValueUndefined(i))
            return -1;
        return inverse.get(i);
    }
    
    public int getInverseCount() {
        return inverseCount;
    }

    /**
     * Swaps two indices in the permutation table.
     *
     * @param i1 first index
     * @param i2 second index
     */
    public void swap(int i1, int i2) {
        int tmp = direct.get(i1);
        direct.set(i1, direct.get(i2));
        direct.set(i2, tmp);
    }

    public void recomputeInverse() {
        inverse.clear();
        inverseCount = 0;
        for (int i = 0; i < direct.size(); i++) {
            if (! direct.isValueUndefined(i)) {
                int index = direct.get(i);
                inverse.setExtend(index, i);
                inverseCount++;
            }
        }
    }

    public void sort(int size, RowComparator comp) {
        if (comp instanceof Permutation) {
            Permutation perm = (Permutation) comp;
            if (perm.getDirect() == direct && perm.getInverse() == inverse)
                return;
        }
        fillPermutation(size, comp);
        direct.sort(comp);
        recomputeInverse();
    }
    
    public void sort(int size, RowComparator comp, RowFilter filter) {
        fillPermutation(size, comp, filter);
        direct.sort(comp);
        recomputeInverse();
    }
    
    public void setPermutation(IntColumn perm) {
        if (perm == direct)
            return;
        clear();
        int size = perm.size();
        for (int i = 0; i < size; i++) {
            if (! perm.isValueUndefined(i)) {
                direct.add(perm.get(i));
            }
        }
        recomputeInverse();
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int size() {
        return direct.size();
    }

    /**
     * Returns the direct.
     * @return IntColumn
     */
    public IntColumn getDirect() {
        return direct;
    }

    /**
     * Returns the inverse.
     * @return IntColumn
     */
    public IntColumn getInverse() {
        return inverse;
    }

    //  RowComparator Interface
     public int compare(int row1, int row2) {
         return getInverse(row1) - getInverse(row2);
     }
    
     public boolean isValueUndefined(int row) {
         return getInverse(row) == -1;
     }
     
     private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         recomputeInverse();
     }
    
}
