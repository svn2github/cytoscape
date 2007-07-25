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
import java.util.*;

import javax.swing.event.ChangeEvent;

import cern.colt.Sorting;
import cern.colt.function.IntComparator;
import cern.colt.list.IntArrayList;

/**
 * Maintain a permutation of indices.
 * 
 * <p>
 * Maintain two tables called <code>direct</code> and <code>inverse</code>.
 * <p>
 * The <code>direct</code> table usually is the result of a indirect sort over
 * a column. It contains the index of the rows sorted in a specific order. For
 * example, if a column containing {6, 7, 5} is sorted in ascending order, the
 * direct table will contain {2, 0, 1}.
 * <p>
 * The <code>inverse</code> table contains the index of a given row. With the
 * previous example, it contains {1, 2, 0} meaning that the index or rows
 * {0,1,2} are {1,2,0}.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.23 $
 */
public class Permutation extends ChangeManager implements RowComparator, Serializable {
    protected IntArrayList        direct;
    protected transient IntArrayList inverse;
    protected transient int       minIndex;
    protected transient int       maxIndex;
    protected ArrayList           listeners;
    private transient ChangeEvent changeEvent;

    public Permutation(int size) {
        direct = new IntArrayList(size);
        inverse = new IntArrayList(size);
        fillPermutation(size);
    }
    
    public Permutation(RowIterator iter) {
        this();
        fillPermutation(iter);
    }

    public Permutation() {
        direct = new IntArrayList();
        inverse = new IntArrayList();
        minIndex = -1;
        maxIndex = -1;
    }    
    
    protected ChangeEvent createChangeEvent() {
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        return changeEvent;
    }

    public void clear() {
        if (direct.isEmpty() && inverse.isEmpty()) return;
        minIndex = -1;
        maxIndex = -1;
        direct.clear();
        inverse.clear();
        modified();
    }

    public void fillPermutation(int size) {
        disableNotify();
        clear();
        minIndex = 0;
        maxIndex = size-1;
        for (int i = 0; i < size; i++) {
            direct.add(i);
            inverse.add(i);
        }
        enableNotify();
    }
    
    public void fillPermutation(RowIterator iter) {
        disableNotify();
        clear();
        try {
            while (iter.hasNext()) {
                int i = iter.nextRow();
                direct.add(i);
            }
            recomputeInverse();
        }
        finally {
            enableNotify();
        }
    }

    public void fillPermutation(int size, RowComparator comp) {
        disableNotify();
        clear();
        for (int i = 0; i < size; i++) {
            if (!comp.isValueUndefined(i)) {
                direct.add(i);
            }
        }
        recomputeInverse();
        enableNotify();
    }

    public void fillPermutation(int size, RowFilter filter) {
        disableNotify();
        clear();
        for (int i = 0; i < size; i++) {
            if (!filter.isFiltered(i)) {
                direct.add(i);
            }
        }
        recomputeInverse();
        enableNotify();
    }

    public void shuffle(int seed) {
        cern.jet.random.Uniform gen = new cern.jet.random.Uniform(new cern.jet.random.engine.DRand(seed));
        int to = direct.size()-1;
        for (int i=0; i<to; i++) { 
            int random = gen.nextIntFromTo(i, to);

            //swap(i, random)
            int tmpElement = direct.getQuick(random);
            direct.setQuick(random,direct.getQuick(i)); 
            direct.setQuick(i,tmpElement); 
        }  
        //direct.shuffle();
        recomputeInverse();
    }
    
    public void shuffle() {
        shuffle((int)System.currentTimeMillis());
    }
    
    public void inverse() {
        for(int min = 0, max = size()-1;
            min < max; min++, max--) {
            int tmp = direct.getQuick(min);
            direct.setQuick(min, direct.getQuick(max));
            direct.setQuick(max, tmp);
        }
        recomputeInverse();
    }
    

    /**
     * Returns the row at a specified index.
     * 
     * @param i
     *            the index
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
     * @param i
     *            the row
     * 
     * @return the index of a specified row.
     */
    public int getInverse(int i) {
        if (i < 0 || i >= inverse.size())
            return -1;
        return inverse.get(i);
    }

    public void recomputeInverse() {
        int size = 0;
        for (int i = 0; i < direct.size(); i++) {
            int v = direct.getQuick(i);
            size = Math.max(size, v);
        }
        
        inverse.setSize(size+1);
        inverse.fillFromToWith(0, size, -1);
        minIndex = -1;
        maxIndex = -1;
        for (int i = 0; i < direct.size(); i++) {
            int index = direct.get(i);
            if (minIndex == -1) {
                minIndex = index;
                maxIndex = index;
            }
            else if (minIndex > index) {
                minIndex = index;
            }
            else if (maxIndex < index) {
                maxIndex = index;
            }
            assert(inverse.getQuick(index)==-1);
            inverse.setQuick(index, i);
        }
        modified();
    }

    public void sort(RowComparator comp) {
        Sorting.mergeSort(direct.elements(), 0, size(), comp);
        recomputeInverse();
    }
    
//    
//    public void permute(int[] perm) {
//        assert(perm.length==direct.size() 
//                && checkPermutation(perm, perm.length));
//        int[] copy = new int[direct.size()];
//        direct.elements(copy);
//        for (int i  =0; i < copy.length; i++) {
//            direct.setQuick(i, copy[perm[i]]);
//        }
//        recomputeInverse();
//    }
//    
//    public void permute(IntArrayList perm) {
//        assert(perm.size()==direct.size() 
//                && checkPermutation(perm.elements(), perm.size()));
//        int[] copy = new int[direct.size()];
//        direct.elements(copy);
//        for (int i  =0; i < copy.length; i++) {
//            direct.setQuick(i, copy[perm.getQuick(i)]);
//        }
//        recomputeInverse();
//    }
//    
//    public void permute(IntColumn perm) {
//        assert(perm.size()==direct.size() 
//                && checkPermutation(perm.toArray(), perm.size()));
//        int[] copy = new int[direct.size()];
//        direct.elements(copy);
//        for (int i  =0; i < copy.length; i++) {
//            direct.setQuick(i, copy[perm.get(i)]);
//        }
//        recomputeInverse();
//    }
//    
//    public void permute(Permutation perm) {
//        assert(perm.size()==direct.size()
//    }

    public void filter(RowComparator comp) {
        for (int i = 0; i < direct.size(); i++) {
            if (comp.isValueUndefined(direct.get(i))) {
                direct.remove(i);
                i--;
            }
        }
        recomputeInverse();
    }

    public void filter(RowFilter filter) {
        for (int i = 0; i < direct.size(); i++) {
            if (filter.isFiltered(direct.get(i))) {
                direct.remove(i);
                i--;
            }
        }
        recomputeInverse();
    }
    
    public void filter(int row) {
        //TODO test
        int i = getInverse(row);
        if (i < 0) return;
        direct.remove(i);
        inverse.setQuick(row, -1);
        for (row++; row < inverse.size(); row++) {
            i = inverse.getQuick(row);
            if (i != -1) {
                inverse.setQuick(row, i-1);
            }
        }
        modified();
    }
    
    public void filter(RowIterator iter) {
        //TODO test
        IntIntSortedMap map = new IntIntSortedMap(new IntComparator() {
            public int compare(int arg0, int arg1) {
                return arg1 - arg0;
            }
        });
        for (; iter.hasNext(); ) {
            int row = iter.nextRow();
            int i = getInverse(row);
            if (i >= 0) {
                map.put(i, row);
            }
        }
        if (map.isEmpty()) return;
        int shift = map.size();
        int last = inverse.size();
        for (Iterator eiter = map.nodeIterator(); eiter.hasNext(); ) {
           IntIntSortedMap.RBNode node = (IntIntSortedMap.RBNode)eiter.next();
           direct.remove(node.getKey());
           inverse.setQuick(node.getValue(), -1);
           for (int row = node.getValue()+1; row < last; row++) {
               int v = inverse.getQuick(row);
               if (v >= 0) {
                   inverse.setQuick(row, v-shift);
               }
           }
           shift--;
        }
        modified();
    }

    /**
     * Returns the size of the direct table.
     * 
     * @return the size of the direct table.
     */
    public int size() {
        return direct.size();
    }
    
    public int getInverseSize() {
        return inverse.size();
    }

    public RowIterator iterator() {
        return new TableIterator(0, direct.size()) {
            public int nextRow() {
                return direct.get(super.nextRow());
            }

            public int peekRow() {
                return direct.get(super.peekRow());
            }
        };
    }
    
    public RowIterator reverseIterator() {
        return new TableIterator(direct.size()-1, -1, false) {
            public int nextRow() {
                return direct.get(super.nextRow());
            }

            public int peekRow() {
                return direct.get(super.peekRow());
            }
        };
    }

    // RowComparator Interface
    public int compare(int row1, int row2) {
        int a = getInverse(row1);
        int b = getInverse(row2);
        if (a == -1) {
            if (b == -1) {
                return a - b;
            }
            else
                return 1;
        }
        else if (b == -1) {
            return -1;
        }
        return a - b;
    }

    public boolean isValueUndefined(int row) {
        return getInverse(row) == -1;
    }
    
    public int getMinIndex() {
        return minIndex;
    }
    
    public int getMaxIndex() {
        return maxIndex;
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        recomputeInverse();
    }
}
