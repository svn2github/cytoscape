/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.metadata;

import cern.colt.function.IntProcedure;
import infovis.Column;
import infovis.Tree;
import infovis.tree.DepthFirst;

/**
 * A <code>Column</code> in a Tree is considerer aggregated at the
 * leaf level it only its leave nodes have defined values.
 * 
 * Such columns can the be used using most aggregation types (additive,
 * min, max, etc.) by filling the inner nodes with the desired aggregation
 * function without breaking the column's visible semantic. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * 
 * @infovis.factory AggregationFactory AtLeaf
 */
public class AtLeafAggregation implements Aggregation {
    private static final AtLeafAggregation instance = new AtLeafAggregation();
    
    public static AtLeafAggregation getInstance() {
        return instance;
    }
    
    public boolean isAggregating(final Column col, final Tree tree) {
        String aggr = (String)col.getMetadata().getAttribute(AGGREGATION_TYPE);
        
        if (aggr != null ) {
            return aggr.equals(AGGREGATION_TYPE_ATLEAF);
        }
        try {
            DepthFirst.visitPreorder(tree,
                    new IntProcedure() {
                void no() {
                    throw new RuntimeException("NO");
                }
                public boolean apply(int node) {
                    if (tree.isLeaf(node)) {
                        if (col.isValueUndefined(node)) {
                            no();
                        }
                    }
                    else {
                        if (! col.isValueUndefined(node)) {
                            no();
                        }
                    }
                    return true;
                }
            });
        }
        catch(RuntimeException e) {
            return false;
        }
        col.getMetadata().addAttribute(AGGREGATION_TYPE, AGGREGATION_TYPE_ATLEAF);
        return true;
    }

    public Column aggregate(Column src, Tree tree, Column dst) {
        return null; // don't compute anything here
    }

    public String getType() {
        return AGGREGATION_TYPE_ATLEAF;
    }

    public static final boolean isAtLeaf(Column col, Tree tree) {
        return instance.isAggregating(col, tree);
    }
}
