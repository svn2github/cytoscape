/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.metadata;

import infovis.Column;
import infovis.Tree;

/**
 * Interface <code>Aggregation</code> is used to check whether
 * a column is aggregating following a specified rule in a tree.
 * Aggregation can be additive, mean, min, max, etc. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 */
public interface Aggregation extends AggregationConstants {
    /**
     * Checks whether a column is aggregating following the
     * class aggregation scheme.
     * 
     * @param col the column
     * @param tree the tree
     * @return <code>true</code> if the column follows the aggregation
     * scheme, <code>false</code> otherwise.
     */
    boolean isAggregating(Column col, Tree tree);
    
    /**
     * Computes the aggregation values of the specified source column into 
     * the destination column.  Source and destination should be allocated
     * and of the right type.
     * 
     * @param src the source column
     * @param tree the tree
     * @param dst the destination column
     * @return the dst column.
     */
    Column aggregate(Column src, Tree tree, Column dst);
    
    /**
     * Returns the type name of the aggregation.
     * @return the type name of the aggregation.
     */
    String getType();
}
