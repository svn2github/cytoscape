/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.Column;
import infovis.Tree;
import infovis.utils.RowIterator;

/**
 * Computes and maintains the degree of a tree in a column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class DegreeColumn extends TreeIntColumn {
    /**
     * Name of the optional IntColumn referencing the degree of a node.
     */
    public static final String DEGREE_COLUMN = "[degree]";
    
    protected DegreeColumn(Tree tree) {
        super(DEGREE_COLUMN, tree);
    }

    /**
     * Returns the DegreeColumn of this tree if it exists or null.
     * @param tree the tree
     * @return the DegreeColumn of this tree if it exists or null.
     */
    public static DegreeColumn getColumn(Tree tree) {
        Column c = tree.getColumn(DEGREE_COLUMN);
        if (c instanceof DegreeColumn) {
            return (DegreeColumn)c;
        }
        return null;
    }
    
    /**
     * Returns the DegreeColumn of this tree if it exists and
     * creates it otherwise.
     * @param tree the tree
     * @return the DegreeColumn of this tree.
     */
    public static DegreeColumn findColumn(Tree tree) {
        DegreeColumn dc = getColumn(tree);
        if (dc == null) {
            dc = new DegreeColumn(tree);
            tree.addColumn(dc);
        }
        return dc;
        
    }

//    public void treeNodesInserted(TreeModelEvent e) {
//        int parent = RowObject.getRow(tree, e.getTreePath().getLastPathComponent());
//        int[] indices = e.getChildIndices();
//        setExtend(parent, tree.getChildCount(parent));
//        for (int i = 0; i < indices.length; i++) {
//            int node = tree.getChild(parent, indices[i]);
//            setExtend(node, tree.getChildCount(node));
//        }
//    }
//
//    public void treeNodesRemoved(TreeModelEvent e) {
//        int parent = RowObject.getRow(tree, e.getTreePath().getLastPathComponent());
//        int[] indices = e.getChildIndices();
//        setExtend(parent, tree.getChildCount(parent));
//        for (int i = 0; i < indices.length; i++) {
//            int node = tree.getChild(parent, indices[i]);
//            setExtend(node, tree.getChildCount(node));
//        }
//    }

    protected void update() {
        try {
            disableNotify();
            clear();
            for (RowIterator iter = tree.iterator(); iter.hasNext(); ) {
                int node = iter.nextRow();
                setExtend(node, tree.getChildCount(node));
            }
        }
        finally {
            enableNotify();
        }
    }
}
