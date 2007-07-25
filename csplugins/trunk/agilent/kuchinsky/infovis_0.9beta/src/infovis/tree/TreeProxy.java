/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.Tree;
import infovis.table.DynamicTableProxy;
import infovis.utils.RowIterator;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;


/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class TreeProxy extends DynamicTableProxy implements Tree {
    /** The real tree */
    protected Tree tree;

    public TreeProxy(Tree tree) {
        super(tree);
        this.tree = tree;
    }
    
    public Tree getTree() {
        return tree;
    }

    public int addNode(int par) {
        return tree.addNode(par);
    }

    public RowIterator childrenIterator(int node) {
        return tree.childrenIterator(node);
    }

    public int getChild(int node, int index) {
        return tree.getChild(node, index);
    }

    public int getChildCount(int node) {
        return tree.getChildCount(node);
    }

    public int getDepth(int node) {
        return tree.getDepth(node);
    }

    public int getParent(int node) {
        return tree.getParent(node);
    }

    public boolean isAncestor(int node, int par) {
        return tree.isAncestor(node, par);
    }

    public boolean isLeaf(int node) {
        return tree.isLeaf(node);
    }

    public int nextNode() {
        return tree.nextNode();
    }

    public void reparent(int node, int newparent) {
        tree.reparent(node, newparent);
    }
    public boolean removeNode(int node) {
        return tree.removeNode(node);
    }

    public int getNodeCount() {
        return tree.getNodeCount();
    }
    
    public void addTreeModelListener(TreeModelListener l) {
        tree.addTreeModelListener(l);
    }
    public Object getChild(Object parent, int index) {
        return tree.getChild(parent, index);
    }
    public int getChildCount(Object parent) {
        return tree.getChildCount(parent);
    }
    public int getIndexOfChild(Object parent, Object child) {
        return tree.getIndexOfChild(parent, child);
    }
    public Object getRoot() {
        return tree.getRoot();
    }
    public boolean isLeaf(Object node) {
        return tree.isLeaf(node);
    }
    public void removeTreeModelListener(TreeModelListener l) {
        tree.removeTreeModelListener(l);
    }
    public void valueForPathChanged(TreePath path, Object newValue) {
        tree.valueForPathChanged(path, newValue);
    }
}
