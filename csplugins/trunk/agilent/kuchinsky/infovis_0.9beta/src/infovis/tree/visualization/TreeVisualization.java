/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Column;
import infovis.Tree;
import infovis.column.*;
import infovis.metadata.AdditiveAggregation;
import infovis.tree.*;
import infovis.utils.*;
import infovis.visualization.DefaultVisualization;
import infovis.visualization.ItemRenderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import cern.colt.Sorting;
import cern.colt.function.DoubleFunction;
import cern.colt.function.IntProcedure;
import cern.colt.list.IntArrayList;

/**
 * Abstract base class for tree visualizations.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.41 $
 */
public class TreeVisualization extends DefaultVisualization 
    implements Tree, IntProcedure {
    /** Property name for the visible root field. */
    public static final String     PROPERTY_ROOT         = "visibleRoot";
    /** Name of the column containing the sorted children of the tree. */
    public static final String     SORTEDCHILDREN_COLUMN = "#sortedChildren";
    /** Name of the column where the additive weights are computed. */
    public static final String     WEIGHT_COLUMN         = "#weight";

    protected Tree                 tree;
    protected int                  visibleRoot;
    protected transient Graphics2D graphics;
    protected ObjectColumn         sortedChildren;
    protected DepthColumn          depthColumn;
    protected DegreeColumn         degreeColumn;
//    private NumberColumn           denseColumn;

    /** The additive weight is computed and stored in this column */
    private DoubleColumn           weightColumn;
    private Column                 sizeColumn;
    protected LeafCountColumn      leafCount;
    protected DoubleFunction       transformFunction;

    /**
     * Constructor for TreeVisualization.
     * 
     * @param tree
     *            the Tree.
     */
    public TreeVisualization(Tree tree) {
        this(tree, null);
    }

    /**
     * Constructor for TreeVisualization.
     * 
     * @param tree
     *            the tree
     * @param ir
     *            the ItemRenderer root
     */
    public TreeVisualization(Tree tree, ItemRenderer ir) {
        super(tree, ir);
        this.tree = tree;
        depthColumn = DepthColumn.findColumn(tree);
        degreeColumn = DegreeColumn.findColumn(tree);
        this.visibleRoot = Tree.ROOT;
        this.sortedChildren = ObjectColumn.findColumn(
                tree,
                SORTEDCHILDREN_COLUMN);
    }

    /**
     * Returns the tree.
     * 
     * @return Tree
     */
    public Tree getTree() {
        return tree;
    }

    /**
     * {@inheritDoc}
     */
    public void paintItems(final Graphics2D graphics, Rectangle2D bounds) {
        this.graphics = graphics;
        DepthFirst.visitPreorder(this, this, visibleRoot);
    }

    /**
     * {@inheritDoc}
     */
    public boolean apply(int node) {
        paintItem(graphics, node);
        return true;
    }

    /**
     * Returns the visible root.
     * 
     * @return int the visible root.
     */
    public int getVisibleRoot() {
        return visibleRoot;
    }

    /**
     * Sets the visible root.
     * 
     * @param root
     *            The visible root to set
     */
    public void setVisibleRoot(int root) {
        if (this.visibleRoot != root) {
            if (!tree.isRowValid(root))
                return;
            if (root != Tree.ROOT && tree.isLeaf(root)) {
                root = tree.getParent(root);
            }
            int old = this.visibleRoot;
            this.visibleRoot = root;
            invalidate();
            firePropertyChange(PROPERTY_ROOT, old, visibleRoot);
        }
    }

//    /**
//     * Checks whether the specified column is defined for all tree nodes.
//     * 
//     * @param column
//     *            the column
//     * @return true if the column has defined values for all nodes, false
//     *         otherwise.
//     */
//    public boolean isDense(Column column) {
//        String type = AggregationFactory.getAggregationType(column, tree);
//        if (type != null) {
//            return !type.equals(Aggregation.AGGREGATION_TYPE_ATLEAF);
//        }
//        for (RowIterator iter = tree.iterator(); iter.hasNext();) {
//            if (column.isValueUndefined(iter.nextRow())) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * Returns an additive weighted column from the specified size column.
     * 
     * @return an additive weighted column from the specified size
     */
    public NumberColumn getWeightColumn() {
        Column column = getVisualColumn(VISUAL_SIZE);
        if (column == null) {
            if (leafCount == null) {
                leafCount = LeafCountColumn.findColumn(tree);
            }
            column = leafCount;
            return leafCount;
        }
        if (column != sizeColumn) {
            sizeColumn = column;
            if (weightColumn == null) {
                weightColumn = new DoubleColumn(WEIGHT_COLUMN);
            }
            else {
                weightColumn.clear();
            }
            weightColumn.disableNotify();
            weightColumn.setName(sizeColumn.getName());
            AdditiveAggregation.buildAdditiveWeight(
                    (NumberColumn) sizeColumn,
                    this,
                    transformFunction,
                    ZeroValueGenerator.instance,
                    weightColumn);
            weightColumn.enableNotify();
        }
        return weightColumn;
    }

    /**
     * Returns the transform function applied to weights or null.
     * 
     * @return the transform function applied to weights or null.
     */
    public DoubleFunction getTransformFunction() {
        return transformFunction;
    }

    /**
     * Sets the transform function applied to weights or null.
     * 
     * @param transformFunction
     *            the transform function.
     */
    public void setTransformFunction(DoubleFunction transformFunction) {
        if (this.transformFunction == transformFunction)
            return;
        this.transformFunction = transformFunction;
        sizeColumn = null;
        invalidate();
    }

//    /**
//     * Creates a dense columnm, filling the undefined values with something
//     * meaningful.
//     * 
//     * @param column
//     *            the column
//     * @return a column defined for all the valid nodes.
//     */
//    public RowComparator densifyColumn(Column column) {
//        if (isDense(column)) {
//            return column;
//        }
//
//        Aggregation aggr = null;
//        if (AtLeafAggregation.isAtLeaf(column, tree)) {
//            // choose additive aggregation as default, doesn't make sense for
//            // dates
//            aggr = AdditiveAggregation.getInstance();
//            // } else {
//            // AggregationFactory factory = AggregationFactory
//            // .getInstance();
//            // aggr = factory.get(column, tree);
//        }
//        // TODO improve!!!
//        if (aggr != null) {
//            if (denseColumn == null) {
//                denseColumn = new DoubleColumn("#dense", getRowCount());
//            }
//            Column col = aggr.aggregate(column, this, denseColumn);
//            if (col != null) {
//                column = col;
//            }
//        }
//
//        return column;
//    }

    /**
     * {@inheritDoc}
     */
    public void setPermutation(final Permutation permutation) {
        if (permutation == this.permutation)
            return;
        tree.disableNotify();
        try {
            if (permutation == null) {
                sortedChildren.clear();
            }
            else {
                IntProcedure visitor = new IntProcedure() {
                    public boolean apply(int node) {
                        // Visitor visitor = new Visitor() {
                        // public boolean preorder(int node) {
                        if (permutation.getInverse(node) == -1) {
                            sortedChildren.setValueUndefined(node, true);
                            return false;
                        }
                        IntArrayList children = new IntArrayList(tree
                                .getChildCount(node));
                        for (RowIterator citer = tree.childrenIterator(node); citer
                                .hasNext();) {
                            int c = citer.nextRow();
                            if (permutation.getInverse(c) != -1)
                                children.add(c);
                        }
                        if (children.size() == 0) {
                            sortedChildren.setValueUndefined(node, true);
                        }
                        else {
                            children.trimToSize();
                            int[] cTable = children.elements();
                            if (cTable.length > 1)
                                Sorting.mergeSort(
                                        cTable,
                                        0,
                                        cTable.length,
                                        permutation);
                            sortedChildren.setExtend(node, cTable);
                        }
                        return true;
                    }
                };
                DepthFirst.visitPreorder(tree, visitor);
                sizeColumn = null;
            }
        } finally {
            super.setPermutation(permutation);
            tree.enableNotify();
        }
    }

    // Tree Interface

    /**
     * {@inheritDoc}
     */
    public RowIterator childrenIterator(int node) {
        if (permutation != null) {
            int[] children = (int[]) sortedChildren.getObjectAt(node);
            if (children == null)
                return NullRowIterator.sharedInstance();
            else
                return new ArrayChildrenIterator(0, children);
        }
        else {
            return tree.childrenIterator(node);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int addNode(int par) {
        return tree.addNode(par);
    }

    /**
     * {@inheritDoc}
     */
    public int addRow() {
        return tree.addRow();
    }

    /**
     * {@inheritDoc}
     */
    public void removeRow(int row) {
        tree.removeRow(row);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeNode(int node) {
        return tree.removeNode(node);
    }

    /**
     * {@inheritDoc}
     */
    public int getChild(int node, int index) {
        if (permutation != null) {
            int[] children = (int[]) sortedChildren.getObjectAt(node);
            if (children != null && children.length > index)
                return children[index];
            return Tree.NIL;
        }
        return tree.getChild(node, index);
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount(int node) {
        if (permutation != null) {
            int[] children = (int[]) sortedChildren.getObjectAt(node);
            if (children == null)
                return 0;
            return children.length;
        }
        else if (degreeColumn != null) {
            return degreeColumn.get(node);
        }
        else {
            return tree.getChildCount(node);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getDepth(int node) {
        if (depthColumn != null) {
            return depthColumn.get(node);
        }
        return tree.getDepth(node);
    }

    /**
     * Returns the first child of a specified node or NIL.
     * 
     * @param node
     *            the node
     * @return the first child of a specified node or NIL.
     */
    public int getFirstChild(int node) {
        return getChild(node, 0);
    }

    /**
     * {@inheritDoc}
     */
    public int getParent(int node) {
        return tree.getParent(node);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAncestor(int node, int par) {
        return tree.isAncestor(node, par);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(int node) {
        if (permutation != null) {
            int[] children = (int[]) sortedChildren.getObjectAt(node);
            return children == null;
        }
        return tree.isLeaf(node);
    }

    /**
     * {@inheritDoc}
     */
    public void reparent(int node, int newparent) {
        tree.reparent(node, newparent);
    }

    /**
     * {@inheritDoc}
     */
    public int nextNode() {
        return tree.nextNode();
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return tree.reverseIterator();
    }

    /**
     * {@inheritDoc}
     */
    public int getNodeCount() {
        return tree.getNodeCount();
    }

    /**
     * {@inheritDoc}
     */
    public void addTreeModelListener(TreeModelListener l) {
        tree.addTreeModelListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public Object getChild(Object parent, int index) {
        return tree.getChild(parent, index);
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount(Object parent) {
        return tree.getChildCount(parent);
    }

    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(Object parent, Object child) {
        return tree.getIndexOfChild(parent, child);
    }

    /**
     * {@inheritDoc}
     */
    public Object getRoot() {
        return tree.getRoot();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(Object node) {
        return tree.isLeaf(node);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTreeModelListener(TreeModelListener l) {
        tree.removeTreeModelListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        tree.valueForPathChanged(path, newValue);
    }
}