/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *    
 *                                                                           * 
 * This file is extracted from JUNG http://jung.sourceforge.net              *
 *****************************************************************************/
package infovis.utils;

import java.util.*;

/**
 * An array-based binary heap implementation of a priority queue, 
 * which also provides
 * an efficient <code>update()</code> operation.
 * (A previous version of this class had implemented the commons-collections
 * PriorityQueue interface, which has since been deprecated.)
 * It contains extra infrastructure (a hash table) to keep track of the 
 * position of each element in the array; thus, if the key value of an element
 * changes, it may be "resubmitted" to the heap via <code>update</code>
 * so that the heap can reposition it efficiently, as necessary.  
 * 
 * @author Joshua O'Madadhain
 */
public class Heap {
    private ArrayList   heap;          // holds the heap as an implicit binary tree
    private HashMap     objectIndices; // maps each object in the heap to its index in the heap
    private Comparator  comp;
    private final static int TOP = 0;   // the index of the top of the heap

    /**
     * Creates a <code>MapBinaryHeap</code> whose heap ordering
     * is based on the ordering of the elements specified by <code>c</code>.
     */
    public Heap(Comparator c) {
        this.comp = c;
        objectIndices = new HashMap();
        heap = new ArrayList();
    }
    
    /**
     * Creates a <code>MapBinaryHeap</code> whose heap ordering
     * will be based on the <i>natural ordering</i> of the elements,
     * which must be <code>Comparable</code>.
     */
    public Heap() {
        this(new ComparableComparator());
    }

    public void clear() {
        objectIndices = new HashMap();
        heap = new ArrayList();
    }

    public void insert(Object o) {
        int i = heap.size();  // index 1 past the end of the heap
        heap.add(null);
        percolateUp(i, o);
    }
    
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public Object peek() throws NoSuchElementException {
        return heap.get(TOP);
    }

    public Object pop() throws NoSuchElementException {
        Object top = heap.get(TOP);
        if (top == null)
            return top;
        
        Object bottomElt = heap.get(heap.size()-1);
        heap.set(TOP, bottomElt);
        setIndex(bottomElt, TOP);
        
        heap.remove(heap.size() - 1);  // remove the last element
        if (heap.size() > 1)
            percolateDown(TOP);

        objectIndices.remove(top);
        return top;
    }
    
    public int size() {
        return heap.size();
    }
       
    /**
     * Informs the heap that this object's internal key value has been
     * updated, and that its place in the heap may need to be shifted
     * (up or down).
     * @param o
     */
    public void update(Object o) {
        // Since we don't know whether the key value increased or 
        // decreased, we just percolate up followed by percolating down;
        // one of the two will have no effect.
        
        int cur = getIndex(o);
        int newIdx = percolateUp(cur, o);
        percolateDown(newIdx);
    }
    

    
    private int getIndex(Object o) {
        return ((Integer)objectIndices.get(o)).intValue();
    }
    
    private void setIndex(Object o, int i) {
        objectIndices.put(o, new Integer(i));
    }

    /**
     * Moves the element at position <code>cur</code> closer to 
     * the bottom of the heap, or returns if no further motion is
     * necessary.  Calls itself recursively if further motion is 
     * possible.
     */
    private void percolateDown(int cur) {
        int left = lChild(cur);
        int right = rChild(cur);
        int smallest;

        if ((left < heap.size()) 
                && (comp.compare(heap.get(left), heap.get(cur)) < 0))
            smallest = left;
        else
            smallest = cur;

        if ((right < heap.size()) 
                && (comp.compare(heap.get(right), heap.get(smallest)) < 0))
            smallest = right;

        if (cur != smallest) {
            swap(cur, smallest);
            percolateDown(smallest);
        }
    }

    /**
     * Moves the element <code>o</code> at position <code>cur</code> 
     * as high as it can go in the heap.  Returns the new position of the 
     * element in the heap.
     */
    private int percolateUp(int cur, Object o) {
        int i = cur;
        
        while ((i > TOP) 
                && (comp.compare(heap.get(parent(i)), o) > 0)) {
            Object parentElt = heap.get(parent(i));
            heap.set(i, parentElt);
            setIndex(parentElt, i);
            i = parent(i);
        }
        
        // place object in heap at appropriate place
        heap.set(i, o);
        setIndex(o, i);

        return i;
    }
    
    /**
     * Returns the index of the left child of the element at 
     * index <code>i</code> of the heap.
     * @param i
     * @return
     */
    private static int lChild(int i) {
        return (i<<1) + 1;
    }
    
    /**
     * Returns the index of the right child of the element at 
     * index <code>i</code> of the heap.
     * @param i
     * @return
     */
    private static int rChild(int i) {
        return (i<<1) + 2;
    }
    
    /**
     * Returns the index of the parent of the element at 
     * index <code>i</code> of the heap.
     * @param i
     * @return
     */
    private static int parent(int i) {
        return (i-1)>>1;
    }
    
    /**
     * Swaps the positions of the elements at indices <code>i</code>
     * and <code>j</code> of the heap.
     * @param i
     * @param j
     */
    private void swap(int i, int j) {
        Object iElt = heap.get(i);
        Object jElt = heap.get(j);

        heap.set(i, jElt);
        setIndex(jElt, i);

        heap.set(j, iElt);
        setIndex(iElt, j);
    }
    
    /**
     * Comparator used if none is specified in the constructor.
     * @author Joshua O'Madadhain
     */
    private static class ComparableComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            if (!(arg0 instanceof Comparable) 
                    || !(arg1 instanceof Comparable))
                throw new IllegalArgumentException("Arguments must be Comparable");
            Comparable i1 = (Comparable)arg0;
            Comparable i2 = (Comparable)arg1;
            
            return i1.compareTo(i2);
        }
    }

}