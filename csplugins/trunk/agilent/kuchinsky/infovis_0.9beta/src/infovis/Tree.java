/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis;

import infovis.utils.RowIterator;

import javax.swing.tree.TreeModel;


/**
 * Container for a rooted tree structure (hierarchy).
 *
 * <p>A tree is a table that maintains its topological
 * information stored as four columns.  An index represents a node
 * number, with all associated values stored in the columns.  The four
 * internal columns are: 
 * <ul>
 * <li>child: the index of the first child for a given node
 * <li>next: the next sibling node
 * <li>last: the index of the last child for a given node
 * <li>parent: the index of the parent for a given node. 
 * </ul></p>
 * 
 * <p>The root of a Tree is always at index 0 so a tree is
 * never empty, it always contain at least a root.</p>
 *
 * @version $Revision: 1.38 $
 * @author Jean-Daniel Fekete
 */
public interface Tree extends DynamicTable, TreeModel {
    /** Metadata to get the tree from the table. */
    String TREE_METADATA = "tree";

    /** Value of the tree's ROOT. */
    int ROOT = 0;

    /**
     * Returns the nth child of a node.
     *
     * @param node the node
     * @param index the index if the requested child
     *
     * @return the requested child or NIL if node has not so many children.
     */
    int getChild(int node, int index);

    /**
     * Returns the parent of a node.
     *
     * @param node the node
     *
     * @return the parent or the NIL value if node is the top node
     */
    int getParent(int node);

    /**
     * Returns the iterator over the children of a node.
     *
     * @param node the node
     *
     * @return the iterator over the children of the node
     */
    RowIterator childrenIterator(int node);

    /**
     * Adds a node to the tree.
     *
     * @param par the parent of the node.
     *
     * @return the created node.
     */
    int addNode(int par);
    
    /**
     * Removes a node from the tree.
     * 
     * @param node the node to remove
     * @return true if the node has been removed.
     */
    boolean removeNode(int node);
    
    /**
     * Returns the number of proper nodes in the Tree.
     * Can be different than the value returned by getRowCount if
     * nodes have been removed.
     * 
     * @return the number of proper nodes in the Tree.
     */
    int getNodeCount();

    /**
     * Returns the next node to be returned by addNode.
     *
     * @return the next node to be returned by addNode.
     */
    int nextNode();

    /**
     * Change the parent of a specified node, changing the structure.
     *
     * @param node the node.
     * @param newparent the new parent.
     */
    void reparent(int node, int newparent);
    
    /**
     * Returns true if the first node has the second node as ancestor.
     *
     * @param node the node.
     * @param par the tested ancestor.
     *
     * @return true if the first node has the second node as ancestor.
     */
    boolean isAncestor(int node, int par);

    /**
     * Returns the depth of a node using either a depth column if it has been computed or
     * the computeDepth method.
     *
     * @param node the node.
     *
     * @return the depth of the node.
     */
    int getDepth(int node);

    /**
     * Returns the number of children of a <code>node</code>.
     *
     * @param node the node.
     *
     * @return the number of children of a <code>node</code>.
     */
    int getChildCount(int node);

    /**
     * Returns true if the node is a leaf_node. Use this method rather than
     * degree(node)==0
     *
     * @param node the node
     *
     * @return true if the node is a leaf_node.
     */
    boolean isLeaf(int node);

}
