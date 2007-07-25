/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import cern.colt.list.IntArrayList;

import infovis.Tree;
import infovis.column.IntColumn;
import infovis.metadata.IO;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;

/**
 * Base class for columns computing topological values on a tree.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public abstract class TreeIntColumn extends IntColumn 
    implements TreeModelListener {
    protected Tree    tree;
    protected boolean invalid = true;

    /**
     * Constructor.
     * 
     * @param name
     *            column name
     * @param tree
     *            associated tree
     */
    public TreeIntColumn(String name, Tree tree) {
        super(name, tree.getRowCount());
        this.tree = tree;
        tree.addTreeModelListener(this);
        getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
        //update();
    }

    /**
     * Releases listeners.
     */
    public void dispose() {
        tree.removeTreeModelListener(this);
        clear();
        tree = null;
    }

    /**
     * Method called when the tree is changed.
     * 
     */
    protected abstract void update();

    /**
     * {@inheritDoc}
     */
    public int get(int index) {
        validate();
        return super.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        validate();
        return super.size();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        validate();
        return super.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int[] toArray() {
        validate();
        return super.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public int[] toArray(int[] a) {
        validate();
        return super.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    public void sort(RowComparator comp) {
        validate();
        super.sort(comp);
    }

    /**
     * {@inheritDoc}
     */
    public void stableSort(RowComparator comp) {
        validate();
        super.stableSort(comp);
    }

    /**
     * {@inheritDoc}
     */
    public IntArrayList getValueReference() {
        validate();
        return super.getValueReference();
    }

    /**
     * Validate the contents of this column if needed.
     * 
     */
    public void validate() {
        if (invalid) {
            invalid = false;
            update();
        }
    }

    /**
     * Sets the column to invalid.
     * 
     */
    public void invalidate() {
        invalid = true;
    }

    /**
     * @return Returns the invalid.
     */
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesChanged(TreeModelEvent e) {
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesInserted(TreeModelEvent e) {
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesRemoved(TreeModelEvent e) {
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void treeStructureChanged(TreeModelEvent e) {
        invalidate();
    }

}
