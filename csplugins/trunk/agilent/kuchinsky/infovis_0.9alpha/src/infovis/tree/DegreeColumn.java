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
import infovis.utils.RowObject;

import javax.swing.event.TreeModelEvent;

/**
 * Class DegreeColun
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DegreeColumn extends TreeIntColumn {
    /**
     * Name of the optional IntColumn referencing the degree of a node.
     */
    public static final String DEGREE_COLUMN = "[degree]";
    
    protected DegreeColumn(Tree tree) {
        super(DEGREE_COLUMN, tree);
    }

    public static DegreeColumn getColumn(Tree tree) {
        Column c = tree.getColumn(DEGREE_COLUMN);
        if (c instanceof DegreeColumn) {
            return (DegreeColumn)c;
        }
        return null;
    }
    
    public static DegreeColumn findColumn(Tree tree) {
        DegreeColumn dc = getColumn(tree);
        if (dc == null) {
            dc = new DegreeColumn(tree);
            tree.addColumn(dc);
        }
        return dc;
        
    }

    public void treeNodesInserted(TreeModelEvent e) {
        int parent = RowObject.getRow(tree, e.getTreePath().getLastPathComponent());
        int[] indices = e.getChildIndices();
        setExtend(parent, tree.getChildCount(parent));
        for (int i = 0; i < indices.length; i++) {
            int node = tree.getChild(parent, indices[i]);
            setExtend(node, tree.getChildCount(node));
        }
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        int parent = RowObject.getRow(tree, e.getTreePath().getLastPathComponent());
        int[] indices = e.getChildIndices();
        setExtend(parent, tree.getChildCount(parent));
        for (int i = 0; i < indices.length; i++) {
            int node = tree.getChild(parent, indices[i]);
            setExtend(node, tree.getChildCount(node));
        }
    }

    public void update() {
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
