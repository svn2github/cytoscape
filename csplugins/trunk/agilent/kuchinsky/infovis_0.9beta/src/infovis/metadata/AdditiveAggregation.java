/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.metadata;

import cern.colt.function.DoubleFunction;
import infovis.*;
import infovis.Tree;
import infovis.column.*;
import infovis.column.NumberColumn;
import infovis.tree.DepthFirst;
import infovis.utils.*;
import infovis.utils.RowDoubleValueGenerator;
import infovis.utils.RowIterator;

/**
 * Manage number columns that are additive from the leaves to the root, as
 * required by treemaps.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory AggregationFactory Additive
 */
public class AdditiveAggregation implements Aggregation {
    /** The instance. */
    public static final AdditiveAggregation instance = new AdditiveAggregation();

    /**
     * Returns the instance of AdditiveAggregation.
     * @return the instance of AdditiveAggregation
     */
    public static AdditiveAggregation getInstance() {
        return instance;
    }

    /**
     * Name of the column containing the Additive degree of a tree.
     */
    public static final String ADDEDDEGREE_COLUMN = "#addedDegree";

    /**
     * {@inheritDoc}
     */
    public boolean isAggregating(Column col, Tree tree) {
        String aggr = (String)col.getMetadata().getAttribute(AGGREGATION_TYPE);
        
        if (aggr != null ) {
            return aggr.equals(AGGREGATION_TYPE_ADDITIVE);
        }
        
        if (! (col instanceof NumberColumn)) {
            return false;
        }
        NumberColumn column = (NumberColumn) col;
        short ret = analyzeAdditiveWeight(column, tree);
        if (ret == AGGREGATE_YES) {
            col.getMetadata().addAttribute(AGGREGATION_TYPE,
                    AGGREGATION_TYPE_ADDITIVE);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Column aggregate(Column src, Tree tree, Column dst) {
        if (dst == null 
                || !(dst instanceof NumberColumn)
                || !(src instanceof NumberColumn)) {
            return null;
        }
        buildAdditiveWeight(
                (NumberColumn) src, 
                tree,
                null,
                new RowIsLeafGenerator(tree),
                (NumberColumn) dst);
        dst.getMetadata().addAttribute(AGGREGATION_TYPE, getType());
        return dst;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return AGGREGATION_TYPE_ADDITIVE;
    }

    /**
     * Returns whether a NumberColumn is a valid sizeColumn.
     * 
     * @param col
     *            the NumberColumn
     * @param tree
     *            the Tree.
     * 
     * @return <code>AGGREGATE_YES</code> if the NumberColumn is a valid
     *         sizeColumn, <code>AGGREGATE_NO</code> if the column is not a
     *         valid sizeColumn and cannot be turned into one, and
     *         <code>AGGREGATE_COMPATIBLE</code> otherwise.
     */
    public static short isAdditive(NumberColumn col, Tree tree) {
        String aggregationType =
            AggregationFactory.getAggregationType(col, tree);
        if (aggregationType == null) {
            return AGGREGATE_NO;
        }
        if (aggregationType.equals(AGGREGATION_TYPE_ADDITIVE))
            return AGGREGATE_YES;
        if (col instanceof NumberColumn
                && aggregationType.equals(AGGREGATION_TYPE_ATLEAF))
            return AGGREGATE_COMPATIBLE;
        return AGGREGATE_NO;
    }

    /**
     * Checks whether a NumberColumn is a valid sizeColumn.
     * 
     * @param col
     *            the NumberColumn
     * @param tree
     *            the Tree.
     * 
     * @return <code>ADDITIVE_YES</code> if the NumberColumn is a valid
     *         sizeColumn, <code>ADDITIVE_NO</code> if the column is not a
     *         valid sizeColumn and cannot be turned into one, and
     *         <code>ADDITIVE_COMPATIBLE</code> otherwise.
     */
    public static short analyzeAdditiveWeight(NumberColumn col,
            Tree tree) {
        AnalyzeCompatibility analyzer = new AnalyzeCompatibility(tree,
                col);
        try {
            DepthFirst.visit(tree, analyzer);
        } catch (RuntimeException e) {
            ; // ignore
        }

        return analyzer.valid;
    }

    static class AnalyzeCompatibility implements DepthFirst.Visitor {
        public short valid = AGGREGATE_YES;
        Tree tree;
        NumberColumn col;

        AnalyzeCompatibility(Tree tree, NumberColumn col) {
            this.tree = tree;
            this.col = col;
        }
        
        void no() {
            valid = AGGREGATE_NO;
            throw new RuntimeException("NO");
        }

        public boolean preorder(int node) {
            return true;
        }

        public void postorder(int node) {
            if (tree.isLeaf(node)
                    && col.isValueUndefined(node)) {
                no();
            }
            else if (col.isValueUndefined(node)) {
                valid = AGGREGATE_COMPATIBLE;
            }
            else if (valid == AGGREGATE_COMPATIBLE) {
                no();
            }
            else {
                double sum = 0;
    
                for (RowIterator iter = tree.childrenIterator(node); 
                    iter.hasNext();) {
                    int c = iter.nextRow();
                    if (! col.isValueUndefined(c)) {
                        sum += col.getDoubleAt(c);
                    }
                }
    
                if (col.getDoubleAt(node) != sum) {
                    no();
                }
            }
        }
    }

    /**
     * Creates a NumberColumn to be a valid sizeColumn by computing the sum of
     * the leave and undefining non leaf nodes. The initial values are provided
     * by a <code>WeightBuilder</code> and transformed through a function.
     * 
     * @param src
     *            the source NumberColumn.
     * @param tree
     *            the tree.
     * @param fn the transforming function.
     * @param gen the generator for undefined values
     * @param dst the destination column
     * @return a NumberColumn containing the sum of the leaves of the
     * specified NumberColumn of the specified tree.
     */
    public static NumberColumn buildAdditiveWeight(
            final NumberColumn src,
            final Tree tree,
            final DoubleFunction fn,
            final RowDoubleValueGenerator gen,
            NumberColumn dst) {
        final NumberColumn col = 
            dst == null ? new DoubleColumn(src.getName()) : dst;

        DepthFirst.visit(tree, new DepthFirst.Visitor() {
            public boolean preorder(int node) {
                return true;
            }
            
            void setValue(int node, double v) {
                if (fn != null) {
                    v = fn.apply(v);
                }
                if (v > 0) {
                    col.setDoubleAt(node, v);
                }
            }

            public void postorder(int node) {
                if (tree.isLeaf(node)) {
                    if (src == null || src.isValueUndefined(node)) {
                        if (gen == null) {
                            col.setValueUndefined(node, true);
                        }
                        else {
                            setValue(node, gen.generate(node));
                        }
                    } else {
                        setValue(node, src.getDoubleAt(node));
                    }
                }
                else {
                    double sum = 0;

                    for (RowIterator iter = tree.childrenIterator(node); iter
                            .hasNext();) {
                        int c = iter.nextRow();
                        if (! col.isValueUndefined(c)) {
                            sum += col.getDoubleAt(c);
                        }
                    }
                    col.setDoubleAt(node, sum);
                    // node value is marked as undefined to
                    // get the correct min/max values
                    // without the parents.
                    //col.setValueUndefined(node, true);
                }
            }
        });
        col.getMetadata().addAttribute(AGGREGATION_TYPE,
                AGGREGATION_TYPE_ADDITIVE);
        return col;
    }
//
//    /**
//     * Transform a NumberColumn to be a valid sizeColumn by computing the sum of
//     * the leaves and undefining non leaf nodes.
//     * 
//     * @param col
//     *            the NumberColumn.
//     * @param tree
//     *            the Tree.
//     */
//    public static void buildAdditiveWeight(NumberColumn col, Tree tree) {
//        buildAdditiveWeight(col, tree, col);
//    }
//
//    /**
//     * Creates a NumberColumn to be a valid sizeColumn by computing the sum of
//     * the leave and undefining non leaf nodes. The initial values are provided
//     * by a <code>WeightBuilder</code>.
//     * 
//     * @param col
//     *            the NumberColumn.
//     * @param tree
//     *            the tree.
//     * @param builder
//     *            the <code>RowDoubleValueGenerator</code>.
//     */
//    public static NumberColumn buildAdditiveWeight(
//            final NumberColumn col,
//            final Tree tree,
//            final RowDoubleValueGenerator builder) {
//        return buildAdditiveWeight(col, tree, null, builder, col);
//    }
//
//    /**
//     * Creates a NumberColumn to be a valid sizeColumn by computing the sum of
//     * the leave and undefining non leaf nodes. The initial values are provided
//     * by a <code>WeightBuilder</code>.
//     * 
//     * @param col
//     *            the NumberColumn.
//     * @param tree
//     *            the tree.
//     * @param builder
//     *            the <code>WeightBuilder</code>.
//     */
//    public static void buildAdditiveWeight(
//            final NumberColumn col,
//            final Tree tree,
//            final NumberColumn weight) {
//        DepthFirst.visit(tree, new DepthFirst.Visitor() {
//            public boolean preorder(int node) {
//                return true;
//            }
//
//            public void postorder(int node) {
//                if (tree.isLeaf(node)) {
//                    if (weight.isValueUndefined(node)) {
//                        col.setValueUndefined(node, true);
//                    } else {
//                        double v = weight.getDoubleAt(node);
//                        if (v == 0) {
//                            col.setValueUndefined(node, true);
//                        } else {
//                            col.setDoubleAt(node, v);
//                        }
//                    }
//
//                    return;
//                }
//
//                double sum = 0;
//
//                for (RowIterator iter = tree.childrenIterator(node); iter
//                        .hasNext();) {
//                    int c = iter.nextRow();
//                    if (! col.isValueUndefined(c))
//                        sum += col.getDoubleAt(c);
//                }
//                if (sum == 0) {
//                    col.setValueUndefined(node, true);
//                }
//                else {
//                    col.setDoubleAt(node, sum);
//                }
//            }
//
//            /**
//             * @see infovis.Tree.DepthFirstVisitor#inorder(int)
//             */
//            public void inorder(int node) {
//            }
//        });
//        col.getMetadata().put(AGGREGATION_TYPE,
//                AGGREGATION_TYPE_ADDITIVE);
//    }
//
//    /**
//     * Creates an IntColumn containing the additive weight
//     * of node degrees, i.e. the degree of a leaf node is 1 and the
//     * degree of an internal node is the sum of its children.
//     * 
//     * @param tree the Tree
//     * @return an IntColumn containing the additive weight of 
//     * node degrees.
//     */
//    public static IntColumn buildDegreeAdditiveWeight(final Tree tree) {
//        final IntColumn degree = new IntColumn(ADDEDDEGREE_COLUMN);
//        DepthFirst.visit(
//                tree, 
//                new DepthFirst.Visitor() {
//                public boolean preorder(int node) {
//                    return true;
//                }
//                public void inorder(int node) {
//                }
//                public void postorder(int node) {
//                    if (tree.isLeaf(node)) {
//                        degree.setExtend(node, 1);
//                    }
//                    else {
//                       int sum = 0;
//                        for (RowIterator iter = tree.childrenIterator(node);
//                            iter.hasNext(); ) {
//                            sum += degree.get(iter.nextRow());    
//                        }
//                        degree.setExtend(node, sum);
//                    }
//                }
//            }); 
//
//        tree.addColumn(degree);
//        return degree;
//    }
//
//    public static NumberColumn buildDegreeAdditiveWeight(
//            final NumberColumn degree, final Tree tree) {
//        return buildAdditiveWeight(
//                degree,
//                tree,
//                null,
//                new RowIsLeafGenerator(tree),
//                degree);
//    }
//
//    /**
//     * Returns a degree column in the Tree or create one if it
//     * does not exist.
//     * 
//     * @param tree the tree
//     * 
//     * @return a degree column
//     */
//    public static IntColumn findDegreeColumn(Tree tree) {
//        IntColumn degree = IntColumn.getColumn(tree, ADDEDDEGREE_COLUMN);
//        if (degree != null)
//            return degree;
//        return buildDegreeAdditiveWeight(tree);
//    }

    /**
     * ColumnFilter to filter out columns that are non additive. 
     */
    public static class NonAdditiveFilter implements ColumnFilter {
        Tree tree;

        /**
         * Constructor.
         * @param tree the tree.
         */
        public NonAdditiveFilter(Tree tree) {
            this.tree = tree;
        }

        /**
         * {@inheritDoc}
         */
        public boolean filter(Column column) {
            if (column instanceof NumberColumn) {
                NumberColumn col = (NumberColumn) column;
                return (isAdditive(col, tree) == AGGREGATE_NO);
            }
            return false;
        }
    }
}