/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.io.Serializable;
import java.util.*;

import cern.colt.function.*;
import cern.colt.function.IntComparator;
import cern.colt.function.IntProcedure;
import cern.colt.map.AbstractIntIntMap;

/**
 * Red-Black Tree
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class RBTree extends AbstractIntIntMap implements Cloneable, Serializable {
    public static final boolean RED   = false;
    public static final boolean BLACK = true;
    
    // NIL is a special node
    public static final RBNode NIL = new RBNode(BLACK, -1, -1, null);
    static {
        NIL.parent = NIL;
        NIL.left = NIL;
        NIL.right = NIL;
    }
    
    protected RBNode root;
    protected IntComparator comparator;
    protected int size;
    protected transient RBNode memoized = null;
    private transient int modCount = 0;


    private void incrementSize()   { modCount++; size++; }
    private void decrementSize()   { modCount++; size--; }
    
    public RBTree() {
        this((IntComparator)null);
    }
    
    public RBTree(RBTree other) {
        if (other != null) {
            this.root = new RBNode(other.root, null);
            this.size = other.size();
            this.comparator = other.comparator;
        }
        else {
            root = NIL;
        }
    }

    public RBTree(IntComparator comp) {
        root = NIL;
        comparator = comp;
    }
    
    public Object clone() {
        return new RBTree(this);
    }
    
    public boolean forEachKey(IntProcedure proc) {
        for (RowIterator iter = keyIterator(); iter.hasNext(); ) {
            if (! proc.apply(iter.nextRow())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean forEachPair(IntIntProcedure proc) {
        for (RBNodeIterator iter = new RBNodeIterator(); iter.hasNext(); ) {
            RBNode node = iter.nextNode();
            if (!proc.apply(node.key, node.value))
                return false;
        }
        return true;
    }
    
    public boolean removeKey(int key) {
        if( isEmpty() ) return false;
        
        RBNode z = getNode(key);
        if (isNull(z)) return false;
        deleteNode(z);
        return true;
    }
    
    public int compare(int key1, int key2) {
        if (comparator != null) {
            return comparator.compare(key1, key2);
        }
        else {
            return key1 - key2;
        }
    }

    /**
     * Report the number of elements in the tree.
     * @return the number of elements
     */
    public int size() {
        return size;
    }
 
    /**
     * Report whether this tree has no elements.
     * @return true if size() == 0
     */
    public boolean isEmpty() {
        return size == 0;
    }

    protected RBNode getNode(int key) {
        if (memoized != null && compare(memoized.key, key)==0) {
            return memoized;
        }
        for (RBNode current = root; ! isNull(current); ) {
            int cmp = compare(key, current.key); 
            if (cmp == 0) {
                memoized = current;
                return current;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        // Don't invalidate the memoized value
        return NIL;
    }
    
    public boolean containsKey(int key) {
        return ! isNull(getNode(key));
    }
    
    public int get(int key) {
        RBNode p = getNode(key);
        if (isNull(p))
            throw new NoSuchElementException();
        return p.value;
    }

    public boolean put(int key, int value) {
        RBNode current = root;
        RBNode parent = NIL;
        int cmp = 0;
        
        if (memoized != null && compare(memoized.key, key)==0) {
            memoized.value = value;
            return true;
        }
        memoized = null;

        // find where node belongs
        while (! isNull(current)) {
            cmp = compare(key, current.key); 
            if (cmp == 0) {
                memoized = current;
                memoized.value = value;
                return true;
            }
            parent = current;
            current = cmp < 0 ? current.left : current.right;
        }
        RBNode x = new RBNode(RED, key, value, parent);
        memoized = x;

        if (isNull(parent)) {
            root = x;
        }
        else {
            if (cmp < 0)
                parent.left = x;
            else
                parent.right = x;
        }
        insertFixup(x);
        incrementSize();
        return false;
    }

    public void insert(int key, int value) {
        int cmp = -1;
        RBNode current = root;
        RBNode parent = NIL;
//        if (memoized != null && compare(memoized.key, key) == 0) {
//            current = memoized;
//            parent = current.parent;
//        }
        while (! isNull(current) && cmp != 0) {
            cmp = compare(key, current.key);
            parent = current;
            current = cmp < 0 ? current.left : current.right;
        }
        RBNode x = new RBNode(RED, key, value, parent);
        memoized = x;
        if (isNull(parent)) {
            root = x;
        }
        else {
            if (cmp < 0)
                parent.left = x;
            else
                parent.right = x;
        }
        insertFixup(x);
        incrementSize();
    }
    
    public int remove(int key) {
        if( isEmpty() ) return Integer.MIN_VALUE;
        
        RBNode z = getNode(key);
        return deleteNode(z);
    }
    
    protected int deleteNode(RBNode z) {
        if (isNull(z))
            return Integer.MIN_VALUE; 
        
        if (memoized  == z) memoized = null;
        int ret = z.value;
        
        RBNode y;
        if (isNull(z.left) || isNull(z.right)) {
            y = z;
        }
        else {
            y = z.right;
            while (! isNull(y.left)) {
                y = y.left;
            }
        }
        
        RBNode x = (! isNull(y.left)) ? y.left : y.right;

        // remove y from the parent chain
        x.parent = y.parent;
        if (isNull(y.parent)) 
            root = x;
        else if (y == y.parent.left)
            y.parent.left = x;
        else
            y.parent.right = x;

        if (y != z) {
            z.key = y.key;
            z.value = y.value;
        }

        if (y.color == BLACK) {
            deleteFixup(x);
        }
        decrementSize();
        return ret;
    }

    public void clear() {
        modCount++;
        size = 0;
        root = NIL;
        memoized = null;
    }
    
    protected RBNode firstNode() {
        RBNode x = root;
        while (! isNull(x.left)) {
            x = x.left;
        }
        return x;
    }
    
    public int firstKey() {
        return firstNode().key;
    }
    
    public int lastKey() {
        return lastNode().key;
    }
    
    protected RBNode lastNode() {
        RBNode x = root;
        while(! isNull(x.right)) {
            x = x.right;
        }
        return x;
    }
    
    protected RBNode nextNode(RBNode n) {
        if (isNull(n)) return NIL;
        if (! isNull(n.right)) {
            RBNode nn = n.right;
            while (! isNull(nn.left)) nn = nn.left;
            return nn;
        }
        RBNode nn = n.parent;
        RBNode t = n;
        while ((! isNull(nn)) && t == nn.right) {
            t = nn;
            nn = nn.parent;
        }
        return nn;
    }
    
    public class RBNodeIterator implements Iterator {
        private int eModCount;
        protected RBNode last = null;
        protected RBNode next;
        
        public RBNodeIterator() {
            this(firstNode());
        }
        
        public RBNodeIterator(RBNode node) {
            next = node;
            eModCount = modCount;
        }
        
        public boolean hasNext() {
            return ! isNull(next);
        }
        
        public RBNode nextNode() {
            if (isNull(next)) 
                throw new NoSuchElementException();
            if (modCount != eModCount)
                throw new ConcurrentModificationException();
            last = next;
            next = RBTree.this.nextNode(next);
            return last;
        }
        
        public Object next() {
            return nextNode();
        }
        
        public void remove() {
            if (last == null)
                throw new IllegalStateException();
            if (modCount != eModCount)
                throw new ConcurrentModificationException();
            if ((! isNull(last.left)) && (! isNull(last.right)))
                next = last;
            deleteNode(last);
            eModCount++;
            last = null;
        }
    }
    
    public class KeyIterator extends RBNodeIterator 
        implements RowIterator {
        public KeyIterator() {
            super();
        }
        
        public KeyIterator(RBNode node) {
            super(node);
        }
        
        public int peekRow() {
            return next.key;
        }
        
        public Object next() {
            return new Integer(nextRow());
        }
        
        public int nextRow() {
            return nextNode().key;
        }
        
        public RowIterator copy() {
            return new KeyIterator(next);
        }
    }
    
    private class ValueIterator extends KeyIterator {
        public int peekRow() {
            return next.value;
        }
        
        public int nextRow() {
            return nextNode().value;
        }
    }
    

    public RowIterator keyIterator() {
        return new KeyIterator();
    }
    
    public RowIterator keyIterator(int key) {
        RBNode node = getNode(key);
        if (node == null) return null;
        return new KeyIterator(node);
    }
    
    public RowIterator valueIterator() {
        return new ValueIterator();
    }
    
    public IntComparator getComparator() {
        return comparator;
    }

    static public class RBNode {
        RBNode left = NIL;
        RBNode right = NIL;
        RBNode parent;
        boolean color;

        int key;
        int value;
        
        public RBNode(int key, int value, RBNode parent) {
            this(false, key, value, parent);
        }
        
        public RBNode(boolean color, int key, int value, RBNode parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
        
        public RBNode(RBNode other, RBNode parent) {
            this.key = other.key;
            this.value = other.value;
            this.parent = parent;
            if (! isNull(other.left))
                this.left = new RBNode(other.left, this);
            else
                this.left = NIL;
            if (! isNull(other.right))
                this.right = new RBNode(other.right, this);
            else
                this.right = NIL;
        }
    }


    
    public static boolean isNull(RBNode node) {
        assert(node != null);
        return node == NIL;
    }
    /**
     * Rotate node x to left
     *
     * @param   x   The pivot node
     */
    protected void rotateLeft(RBNode x) {
        RBNode y = x.right;
        x.right = y.left;
        if (! isNull(y.left)) y.left.parent = x;
        y.parent = x.parent;
        if (isNull(x.parent))
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
           x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    /**
     * Rotate node x to right
     *
     * @param   x   The pivot node
     */
    protected void rotateRight(RBNode x) {
        RBNode y = x.left;
        x.left = y.right;
        if (! isNull(y.right)) y.right.parent = x;
        y.parent = x.parent;
        if (isNull(x.parent))
            root = y;
        else if (x == x.parent.right)
            x.parent.right = y;
        else
            x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    /**
     * Maintain Red-Black Tree balance after inserting node x
     *
     * @param   x   The node on which fixup starts
     */
    protected void insertFixup(RBNode x) {
        // check red-black tree properties
        while ( x != root && x.parent.color == RED) {
            // we have a violation
            if (x.parent == x.parent.parent.left) {
                RBNode y = x.parent.parent.right;
                if (y.color == RED) {
                    // uncle is RED
                    x.parent.color = BLACK;
                    y.color = BLACK;
                    x.parent.parent.color = RED;
                    x = x.parent.parent;
                }
                else {
                    // uncle is BLACK
                    if (x == x.parent.right) {
                        // make x a left child
                        x = x.parent;
                        rotateLeft(x);
                    }
                    // recolor and rotate right
                    x.parent.color = BLACK;
                    x.parent.parent.color = RED;
                    rotateRight(x.parent.parent);
                }
            }
            else {
                // mirror image of above code
                RBNode y = x.parent.parent.left;
                if (y.color == RED) {
                    // uncle is RED
                    x.parent.color = BLACK;
                    y.color = BLACK;
                    x.parent.parent.color =RED;
                    x = x.parent.parent;
                }
                else {
                    // uncle is BLACK
                    if (x == x.parent.left) {
                        x = x.parent;
                        rotateRight(x);
                    }
                    // recolor and rotate left
                    x.parent.color = BLACK;
                    x.parent.parent.color = RED;
                    rotateLeft(x.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    /**
     * Maintain Red-Black tree balance after deleting node x
     *
     * @param   x   The node on which fixup starts
     */
    protected void deleteFixup(RBNode x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                RBNode w = x.parent.right;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                }
                else {
                    if (w.right.color == BLACK) {
                        w.left.color = BLACK;
                        w.color = RED;
                        rotateRight(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            }
            else {
                RBNode w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                }
                else {
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        rotateLeft(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }
}
