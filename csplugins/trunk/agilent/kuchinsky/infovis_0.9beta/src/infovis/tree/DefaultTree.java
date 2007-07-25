/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.Tree;
import infovis.column.IntColumn;
import infovis.table.DefaultDynamicTable;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import cern.colt.Sorting;


/**
 * Default implementation of the Tree interface.
 * 
 * <p>A <code>DefaultTree</code> is a <code>Table</code> proxy, not a
 * <code>Table</code> itself (it references a table).
 * 
 * <p>Topology is build using four internal <code>IntColumn</code>s:
 * <ol>
 * <li><code>#child</code> contains the index of the first child of a node 
 * or NIL if the node is a leaf
 * <li><code>#next</code> contains the index of the next sibling of a node
 * <li><code>#last</code> contains the index of the last child of a node
 * <li><cide>#parent</code> contains the index of the parent of a node
 * </ol>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 */
public class DefaultTree extends DefaultDynamicTable implements Tree {
    /**
     * Name of the IntColumn referencing the first child of a node.
     */
    public static final String CHILD_COLUMN = "#child";

    /**
     * Name of the IntColumn referencing the next sibling of a node.
     */
    public static final String NEXT_COLUMN = "#next";

    /**
     * Name of the IntColumn referencing the last child of a node.
     */
    public static final String LAST_COLUMN = "#last";

    /**
     * Name of the IntColumn referencing the parent of a node.
     */
    public static final String PARENT_COLUMN = "#parent";
    
    protected IntColumn    child;
    protected IntColumn    next;
    protected IntColumn    last;
    protected IntColumn    parent;
    
    protected int firing = 0;
    
    protected static final int[] NULL_INT = new int[0];
    
    /**
     * Creates a new DefaultTree object.
     */
    public DefaultTree() {
        child = IntColumn.findColumn(this, CHILD_COLUMN);
        next = IntColumn.findColumn(this, NEXT_COLUMN);
        last = IntColumn.findColumn(this, LAST_COLUMN);
        parent = IntColumn.findColumn(this, PARENT_COLUMN);

        clear();
    }
    
    // Overriden from the Table interface
    /**
     * @see infovis.Table#clear()
     */
    public void clear() {
        try {
            disableNotify();
            super.clear();
            int root = super.addRow();
            assert(root == ROOT);
            child.setExtend(ROOT, NIL);
            next.setExtend(ROOT, NIL);
            last.setExtend(ROOT, NIL);
            parent.setExtend(ROOT, NIL);
        }
        finally {
            enableNotify();
        }
    }
    
    public int addRow() {
        return addNode(Tree.ROOT);
    }
    
    public void removeRow(int row) {
        removeNode(row);
    }
    
    // Tree interface
    
    /**
     * @see infovis.Tree#getChild(int, int)
     */
    public int getChild(int node, int index) {
        if (! isRowValid(node))
            return NIL;
        for (int c = getFirstChild(node); c != Tree.NIL; c = getNextSibling(c)) {
            if (index-- == 0)
                return c;
        }
        return NIL;
    }

    /**
     * Returns the parent of a node.
     *
     * @param node the node
     *
     * @return the parent or the NIL value if node is the top node
     */
    public int getParent(int node) {
        if (! isRowValid(node))
            return NIL;
        return parent.get(node);
    }

    /**
     * Returns the iterator over the children of a node.
     *
     * @param node the node
     *
     * @return the iterator over the children of the node
     */
    public RowIterator childrenIterator(int node) {
        if (! isRowValid(node))
            return null;
        return new ChildrenIterator(child.get(node));
    }

    public LeafIterator leafIterator(){
    	return new LeafIterator(this);
    }
    
    /**
     * Adds a node to the tree.
     *
     * @param par the parent of the node.
     *
     * @return the created node.
     *
     * @throws ArrayIndexOutOfBoundsException DOCUMENT ME!
     */
    public int addNode(int par) {
        if (par == NIL) {
            throw new ArrayIndexOutOfBoundsException(" NIL is an invalid parent except for the root" +
                                                     par);
        }
        int node = super.addRow();
        
        try {
            disableNotify();
            child.setExtend(node, NIL);
            last.setExtend(node, NIL);
            next.setExtend(node, NIL);
            addChild(node, par);
        }
        finally {
            enableNotify();
        }
        return node;
    }


    protected void addChild(int node, int par) {
        parent.setExtend(node, par);

        if (last.get(par) == NIL) {
            child.set(par, node);
            next.setExtend(node, NIL);
        } else {
            next.setExtend(last.get(par), node);
        }

        last.set(par, node);
        fireTreeNodesInserted(node);
    }


    protected void removeChild(int node) {
        int par;
        int index = -1;

        par = parent.get(node);
        parent.set(node, NIL);
        if (child.get(par) == node) {
            index = 0;
            child.set(par, next.get(node));
            if (last.get(par) == node) {
                last.set(par, NIL);
            }

            // If we are first, no previous to update
            // If we are also last, nothing else to do
        }
        else {
            // Not first, chase the previous to change its next pointer.
            int n;
            index = 1;
            for (n = child.get(par); n != NIL; n = next.get(n)) {
                if (next.get(n) == node) {
                    // got it
                    next.set(n, next.get(node));
                    break;
                }
                index++;
            }
            if (last.get(par) == node) {
                assert (next.get(node) == NIL);
                last.set(par, n);
            }
        }
        next.set(node, NIL);
        fireTreeNodesRemoved(par, index);
        
    }
    public int getNodeCount() {
        return getRowCount();
    }

    public boolean removeNode(int node) {
        if (!isRowValid(node))
            return false;
        if (node == ROOT) {
            clear();
        }
        else {
            try {
                disableNotify();
                // Free all the children nodes
                visit(new DepthFirst.Visitor() {
                    public boolean preorder(int node) {
                        return true;
                    }
                    public void postorder(int node) {
                        DefaultTree.super.removeRow(node);
                        // Leave topology dirty for now
                        // reallocating a node will clean it up
                        // if needed.
                        //parent.setValueUndefined(node, true);
                        //child.setValueUndefined(node, true);
                        //last.setValueUndefined(node, true);
                    }
                },
                node);
                // don't touch the columns
                removeChild(node);
                //super.removeRow(node);
                parent.setSize(getRowCount());
                child.setSize(getRowCount());
                last.setSize(getRowCount());
                next.setSize(getRowCount());
            }
            finally {
                enableNotify();
            }
        }
        return true;
    }

    /**
     * Returns the next node to be returned by addNode.
     *
     * @return the next node to be returned by addNode.
     */
    public int nextNode() {
        int next = super.addRow();
        super.removeRow(next);
        return next;
    }

    /**
     * Change the parent of a specified node, changing the structure.
     *
     * @param node the node.
     * @param newparent the new parent.
     *
     * @throws RuntimeException if the node is an ancestor of the parent.
     */
    public void reparent(int node, int newparent) {
        if (isAncestor(newparent, node))
            throw new RuntimeException("cannot reparent into a child");
        int par = getParent(node);
        if (par == newparent)
            return;
        try {
            disableNotify();
            firing++;
            removeChild(node);
            addChild(node, newparent);
        }
        finally {
            firing--;
            enableNotify();
            fireTreeNodesChanged(node);
        }
    }
    

    /**
     * Returns true if the first node has the second node as ancestor.
     *
     * @param node the node.
     * @param par the tested ancestor.
     *
     * @return true if the first node has the second node as ancestor.
     */
    public boolean isAncestor(int node, int par) {
        while (node != NIL) {
            if (node == par) {
                return true;
            }

            node = getParent(node);
        }

        return false;
    }
    
    public int getIndexOfChild(int node) {
        if (! isRowValid(node)) {
            return -1;
        }
        if (node == ROOT) {
            return 0;
        }
        int parent = getParent(node);
        int index = 0;
        for (int c = getFirstChild(parent); c != NIL; c = getNextSibling(c)) {
            if (c == node) {
                return index;
            }
            index++;
        }
        assert(false); // should not happen
        return -1;
    }
    
    /**
     * Returns the depth of a node using either a depth column if it has been computed or
     * the computeDepth method.
     *
     * @param node the node.
     *
     * @return the depth of the node.
     */
    public int getDepth(int node) {
        return computeDepth(node);
    }

    /**
     * Returns the degree of a node using either a degree column if it has been computed or
     * the computeDegree method.
     *
     * @param node the node.
     *
     * @return the depth of the node.
     */
    public int getChildCount(int node) {
        return computeDegree(node);
    }

    /**
     * Returns the first child of a node.
     *
     * @param node the node
     *
     * @return the first child or NIL value if node has no child
     */
    public int getFirstChild(int node) {
        if (! isRowValid(node))
            return NIL;
        return child.get(node);
    }

    /**
     * Returns the next sibling of a node.
     *
     * @param node the node
     *
     * @return the next sibling or the NIL value if node is the last sibling
     */
    public int getNextSibling(int node) {
        if (! isRowValid(node))
            return NIL;
        return next.get(node);
    }

    /**
     * Returns the last child of a node.
     *
     * @param node the node
     *
     * @return the last child or the NIL value if node has no child.
     */
    public int getLastChild(int node) {
        if (! isRowValid(node))
            return NIL;
        return last.get(node);
    }
    
    /**
     * Computes the depth of the node in the tree and return it.
     *
     * @param node the node.
     *
     * @return the depth of the node in the tree.
     */
    public int computeDepth(int node) {
        int depth = 0;
        while (node != ROOT) {
            node = getParent(node);
            depth++;
        }
        return depth;
    }

    /**
     * Returns the degree of a node, i&dot;e&dot; the number of children.
     *
     * @param node the node
     *
     * @return the degree of the node
     */
    public int computeDegree(int node) {
        int degree = 0;

        for (int n = child.get(node); n != NIL; n = next.get(n)) {
            degree++;
        }

        return degree;
    }

    /**
     * Returns true if the node is a leaf_node. Use this method rather than
     * degree(node)==0
     *
     * @param node the node
     *
     * @return true if the node is a leaf_node.
     */
    public boolean isLeaf(int node) {
        if (!isRowValid(node))
            return false;
        return getFirstChild(node) == NIL;
    }

    /**
     * Sorts the node children according to a <code>RowComparator</code>
     *
     * @param node the node.
     * @param comp the comparator.
     */
    protected void sortChildren(int node, RowComparator comp) {
        int[] c = children(node);

        if (c == null || c.length < 2) {
            return;
        }

        Sorting.mergeSort(c, 0, c.length, comp);
    }

    /**
     * Returns a table of children of this node.
     *
     * @param node the node.
     *
     * @return a table of childen of the given node
     * or null is the node has no child.
     */
    public int[] children(int node) {
        int   n = getChildCount(node);
        int[] c;
        if (n == 0)
            c = NULL_INT;
        else {
            c = new int[n];
            int i = 0;

            for (int child = getFirstChild(node); child != NIL;
                     child = getNextSibling(child))
                c[i++] = child;
        }
        return c;
    }

    /**
     * Traverse the tree with a depth first traversal, calling the visitor at
     * each node.
     *
     * @param visitor the <code>DepthFirstVisitor</code>.
     */
    public void visit(DepthFirst.Visitor visitor) {
        DepthFirst.visit(this, visitor, ROOT);
    }

    /**
     * Traverse the tree with a depth first traversal, calling the visitor at
     * each node from a specified root.
     *
     * @param visitor the <code>DepthFirstVisitor</code>.
     * @param root the starting node.
     */
    public void visit(DepthFirst.Visitor visitor, int root) {
        DepthFirst.visit(this, visitor, root);
    }
    
    /**
     * Traverse the tree with a depth first traversal, calling the visitor at
     * each node.
     *
     * @param visitor the <code>DepthFirstVisitor</code>.
     */
    public void visit(BreadthFirst.Visitor visitor) {
        BreadthFirst.visit(this, visitor, ROOT);
    }

    class ChildrenIterator implements RowIterator {
        int node;

        public ChildrenIterator(int node) {
            this.node = node;
        }

        public boolean hasNext() {
            return node != NIL;
        }

        public Object next() {
            return new Integer(nextRow());
        }

        public void remove() {
        }

        public int nextRow() {
            int n = node;
            node = next.get(node);

            return n;
        }

        public int peekRow() {
            return node;
        }
        
        /**
         * @see infovis.utils.RowIterator#copy()
         */
        public RowIterator copy() {
            return new ChildrenIterator(node);
        }

    }
    
    class LeafIterator implements RowIterator {
        int node;
        int next_node;
        RowIterator iter;
        DefaultTree tree;
        
        public LeafIterator(DefaultTree tree) {
        	this.tree = tree;
        	iter = tree.iterator();
        	node = ROOT;
        	next_node = ROOT;
        	while (!isLeaf(next_node) && hasNext()) {
        		next_node = iter.nextRow();
            }
        }

        public boolean hasNext() {
        	return next_node != NIL;
        }

        public Object next() {
            return new Integer(nextRow());
        }

        public void remove() {
        }

        public int nextRow() {
            node = next_node;
            if (iter.hasNext()) next_node = iter.nextRow();
            else next_node = NIL;
            while (!isLeaf(next_node) && iter.hasNext()) {
            	next_node = iter.nextRow();
            }
            return node;
        }

        public int peekRow() {
            return node;
        }
        
        /**
         * @see infovis.utils.RowIterator#copy()
         */
        public RowIterator copy() {
            return new LeafIterator(tree);
        }


    }
    
    // TreeModel implementation
    public Object getRoot() {
        return getObjectFromRow(ROOT);
    }
    
    public Object getChild(Object parent, int index) {
        int par = getRowFromObject(parent);
        return getObjectFromRow(getChild(par, index));
    }
    
    public int getChildCount(Object parent) {
        return getChildCount(getRowFromObject(parent));
    }
    
    public boolean isLeaf(Object node) {
        return isLeaf(getRowFromObject(node));
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {
        fireTreeNodesChanged(getRowFromObject(path.getLastPathComponent()));
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        return getIndexOfChild(getRowFromObject(child));
    }
    
    public void addTreeModelListener(TreeModelListener l) {
        getListenerList().add(TreeModelListener.class, l);
    }
    
    public void removeTreeModelListener(TreeModelListener l) {
        if (! shouldFire()) return;
        listenerList.remove(TreeModelListener.class, l);
    }
    
    public Object[] buildPath(int node) {
        int depth = getDepth(node);
        Object[] path = new Object[depth];
        for (int i = depth-1; i >= 0; i--) {
            path[i] = getObjectFromRow(node);
            node = getParent(node);
        }
        
        return path;
    }
    
    protected boolean shouldFire() {
        return firing == 0
            && listenerList != null 
            && listenerList.getListenerCount(TreeModelListener.class) != 0;
    }
    
    protected void fireTreeNodesChanged(int node) {
        if  (! shouldFire()) {
            return;
        }
        fireTreeNodesChanged(new TreeModelEvent(this, buildPath(node)));
    }
    
    protected void fireTreeNodesChanged(TreeModelEvent e) {
        Object[] ll = listenerList.getListeners(TreeModelListener.class);
        for (int i = 0; i < ll.length; i++) {
            TreeModelListener l = (TreeModelListener)ll[i];
            l.treeNodesChanged(e);
        }
    }
    
    protected void fireTreeNodesInserted(int node) {
        if (! shouldFire()) {
            return;
        }
        int parent = getParent(node);
        int[] childIndices = new int[1];
        childIndices[0] = getIndexOfChild(node);
        Object[] children = new Object[1];
        children[0] = getObjectFromRow(parent);
        fireTreeNodesInserted(
                new TreeModelEvent(
                        this,
                        buildPath(parent),
                        childIndices,
                        children));
    }
    
    protected void fireTreeNodesInserted(TreeModelEvent e) {
        Object[] ll = getListenerList().getListeners(TreeModelListener.class);
        for (int i = 0; i < ll.length; i++) {
            TreeModelListener l = (TreeModelListener)ll[i];
            l.treeNodesInserted(e);
        }
    }
    
    protected void fireTreeNodesRemoved(int parent, int index) {
        if (! shouldFire()) {
            return;
        }
        int[] childIndices = new int[1];
        childIndices[0] = index;
        Object[] children = new Object[1];
        children[0] = getObjectFromRow(parent);
        fireTreeNodesRemoved(
                new TreeModelEvent(
                        this,
                        buildPath(parent),
                        childIndices,
                        children));
    }
    
    
    protected void fireTreeNodesRemoved(TreeModelEvent e) {
        Object[] ll = getListenerList().getListeners(TreeModelListener.class);
        for (int i = 0; i < ll.length; i++) {
            TreeModelListener l = (TreeModelListener)ll[i];
            l.treeNodesRemoved(e);
        }
    }
}
