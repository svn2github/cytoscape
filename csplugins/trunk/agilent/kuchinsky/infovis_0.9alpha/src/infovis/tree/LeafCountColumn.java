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
import infovis.metadata.AggregationConstants;
import infovis.utils.RowIterator;

/**
 * Class SumDegreeColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class LeafCountColumn extends TreeIntColumn {
    /**
     * Name of the Column referencing the number of leaves descendant
     * of a node
     */
    public static final String LEAF_COUNT_COLUMN = "[leafCount]";

    public LeafCountColumn(Tree tree) {
        super(LEAF_COUNT_COLUMN, tree);
        getMetadata().put(
                AggregationConstants.AGGREGATION_TYPE,
                AggregationConstants.AGGREGATION_TYPE_ADDITIVE);
    }

    public static LeafCountColumn getColumn(Tree tree) {
        Column c = tree.getColumn(LEAF_COUNT_COLUMN);
        if (c instanceof LeafCountColumn) {
            return (LeafCountColumn)c;
        }
        return null;
    }
    
    public static LeafCountColumn findColumn(Tree tree) {
        LeafCountColumn dc = getColumn(tree);
        if (dc == null) {
            dc = new LeafCountColumn(tree);
            tree.addColumn(dc);
        }
        return dc;
    }
    
    public void update() {
        try {
            disableNotify();
            clear();
            DepthFirst.visit(tree, new DepthFirst.Visitor() {
                public boolean preorder(int node) {
                    return true;
                }
                public void inorder(int node) {
                }
                public void postorder(int node) {
                    if (tree.isLeaf(node)) {
                        setExtend(node, 1);
                    } else {
                        int sum = 0;
                        for (RowIterator iter = tree
                                .childrenIterator(node); iter.hasNext();) {
                            sum += get(iter.nextRow());
                        }
                        setExtend(node, sum);
                    }
                }
            });
        } finally {
            enableNotify();
        }
    }
}