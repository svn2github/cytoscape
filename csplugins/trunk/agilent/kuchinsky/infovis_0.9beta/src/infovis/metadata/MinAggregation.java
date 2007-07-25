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
import infovis.column.ColumnProxy;
import infovis.column.NumberColumn;
import infovis.tree.DepthFirst;
import infovis.utils.IntIntSortedMap;
import infovis.utils.RowIterator;

/**
 * Aggregate by computing the min value
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * @infovis.factory AggregationFactory Min
 */
public class MinAggregation implements Aggregation {
    private static MinAggregation instance = new MinAggregation();

    public static MinAggregation getInstance() {
        return instance;
    }

    public MinAggregation() {
    }

    public boolean isAggregating(final Column col, final Tree tree) {
        String aggr = (String) col.getMetadata().getAttribute(AGGREGATION_TYPE);

        if (aggr != null) {
            return aggr.equals(AGGREGATION_TYPE_MIN);
        }
        try {
            DepthFirst.visit(tree, new DepthFirst.Visitor() {
                void no() {
                    throw new RuntimeException("NO");
                }

                public boolean preorder(int node) {
                    return true;
                }

                public void postorder(int node) {
                    if (tree.isLeaf(node)) {
                        if (col.isValueUndefined(node)) {
                            no();
                        }
                    } else {
                        if (col.isValueUndefined(node)) {
                            no();
                        }
                        // Min starts with first value
                        RowIterator iter = tree.childrenIterator(node);
                        int minIndex = iter.nextRow();

                        while (iter.hasNext()) {
                            int c = iter.nextRow();
                            if (col.compare(minIndex, c) > 0) {
                                minIndex = c;
                            }
                        }
                        if (col.compare(minIndex, node) != 0) {
                            no();
                        }
                    }
                }
            });
        } catch (Exception e) {
            return false;
        }
        col.getMetadata().addAttribute(AGGREGATION_TYPE, AGGREGATION_TYPE_MIN);
        return true;
    }

    public Column aggregate(Column src, final Tree tree, Column dst) {
        if (dst == null 
                || !(dst instanceof NumberColumn)
                || !(src instanceof NumberColumn)) {
            return null;
        }
        dst.clear();
        dst.ensureCapacity(src.size());

        return aggregate(tree, (NumberColumn) dst, (NumberColumn) src);
    }

    private NumberColumn aggregate(final Tree tree, final NumberColumn to, final NumberColumn col) {
        DepthFirst.visit(tree, new DepthFirst.Visitor() {
            public boolean preorder(int node) {
                return true;
            }
            public void postorder(int node) {
                if (tree.isLeaf(node)) {
                    to.setDoubleAt(node, col.getDoubleAt(node));
                } else {
                    // Min starts with first value
                    RowIterator iter = tree.childrenIterator(node);
                    int minIndex = iter.nextRow();

                    while (iter.hasNext()) {
                        int c = iter.nextRow();
                        if (col.compare(minIndex, c) > 0) {
                            minIndex = c;
                        }
                    }
                    to.setDoubleAt(node, col.getDoubleAt(minIndex));
                }
            }
        });
        return to;
    }

    public String getType() {
        return AGGREGATION_TYPE_MIN;
    }
    
    public static ColumnProxy build(Column src, Tree tree) {
        return new DenseColumn(src, tree);
    }

    public static class DenseColumn extends ColumnProxy 
        implements DepthFirst.Visitor {
        protected IntIntSortedMap missing;
        protected Tree tree;
        protected Column src;
        
        public DenseColumn(Column src, Tree tree) {
            super(src);
            this.src = src;
            this.tree = tree;
            this.missing = new IntIntSortedMap();
            update();
        }
        
        public void update() {
            missing.clear();
            // Undefined leaves will have the maxIndex
            if (getMaxIndex()== -1) {
                // No need to do anything
                return;
            }
            DepthFirst.visit(tree, this);
        }
        public boolean preorder(int node) {
            return true;
        }
        public void postorder(int node) {
            if (! super.isValueUndefined(node)) {
                return;
            }
            int minIndex = getMaxIndex();
            if (! tree.isLeaf(node)) {
                // precondition: all leaves have defined/comparable values now
                for (RowIterator iter = tree.childrenIterator(node); iter.hasNext(); ) {
                    int c = iter.nextRow();
                    if (super.compare(minIndex, c) > 0) {
                        minIndex = c;
                    }
                }
            }
            missing.put(node, minIndex);
        }
        
        public boolean isValueUndefined(int row) {
            return false;
        }
        
        public int getIndex(int row) {
            if (missing.containsKey(row)) {
                return missing.get(row);
            }
            else {
                return row;
            }
        }
        
        public String getValueAt(int row) {
            return super.getValueAt(getIndex(row));
        }
        
        public int compare(int a, int b) {
            return super.compare(getIndex(a), getIndex(b));
        }
    }
}