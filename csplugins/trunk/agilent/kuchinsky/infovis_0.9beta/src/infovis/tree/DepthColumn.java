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
import infovis.utils.RowObject;

import javax.swing.event.TreeModelEvent;

/**
 * Class DepthColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class DepthColumn extends TreeIntColumn {
  /**
   * Name of the optional IntColumn referencing the depth of a node.
   */
    public static final String DEPTH_COLUMN = "[depth]";
  
    protected DepthColumn(Tree tree) {
        super(DEPTH_COLUMN, tree);
    }
    
    public static DepthColumn getColumn(Tree tree) {
        Column c = tree.getColumn(DEPTH_COLUMN);
        if (c instanceof DepthColumn) {
            return (DepthColumn)c;
        }
        return null;
    }
    
    public static DepthColumn findColumn(Tree tree) {
        DepthColumn dc = getColumn(tree);
        if (dc == null) {
            dc = new DepthColumn(tree);
            tree.addColumn(dc);
        }
        return dc;
    }

//    public void treeNodesInserted(TreeModelEvent e) {
//        int parent = RowObject.getRow(tree, e.getTreePath().getLastPathComponent());
//        int[] indices = e.getChildIndices();
//        for (int i = 0; i < indices.length; i++) {
//            int node = tree.getChild(parent, indices[i]);
//            setExtend(node, tree.getDepth(node));
//        }
//    }
//
//    public void treeNodesRemoved(TreeModelEvent e) {
//        int parent = RowObject.getRow(tree, e.getTreePath().getLastPathComponent());
//        int[] indices = e.getChildIndices();
//        for (int i = 0; i < indices.length; i++) {
//            int node = tree.getChild(parent, indices[i]);
//            setValueUndefined(node, true);
//        }
//    }
    
    protected void update() {
        try {
            disableNotify();
            clear();
            DepthFirst.visit(tree, 
                    new DepthFirst.Visitor() {
                int depth = -1;
                public boolean preorder(int node) {
                    depth++;
                    setExtend(node, depth);
                    return true;
                }
                public void postorder(int node) {
                    depth--;
                }
            });
        }
        finally {
            enableNotify();
        }
    }

}
