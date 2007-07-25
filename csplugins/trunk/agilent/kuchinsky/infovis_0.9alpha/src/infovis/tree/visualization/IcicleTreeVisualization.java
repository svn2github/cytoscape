/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Tree;
import infovis.column.*;
import infovis.metadata.AdditiveAggregation;
import infovis.tree.Algorithms;
import infovis.tree.DepthFirst;
import infovis.utils.IntStack;
import infovis.visualization.ItemRenderer;
import infovis.visualization.ruler.*;
import infovis.visualization.ruler.DiscreteRulersBuilder;
import infovis.visualization.ruler.LinearRulersBuilder;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.39 $
 * 
 * @infovis.factory VisualizationFactory "Tree Icicle" infovis.Tree
 */
public class IcicleTreeVisualization extends TreeVisualization {
    protected int maxDepth;
    protected RainbowColumn rainbowColumn;
    protected transient IcicleTreeVisitor visitor = new IcicleTreeVisitor();
    protected float maxRootHeight = 20;
    protected float rootHeight;
    protected int preferredItemSize;

    /**
     * Constructor for IcicleTreeVisualization.
     * 
     * @param tree
     */
    public IcicleTreeVisualization(Tree tree) {
        this(tree, null);
        rulers = new RulerTable();
    }

    /**
     * Constructor for IcicleTreeVisualization.
     * 
     * @param tree
     * @param ir
     */
    public IcicleTreeVisualization(Tree tree, ItemRenderer ir) {
        super(tree, ir);
        rainbowColumn = RainbowColumn.findColumn(tree);
        setVisualColumn(VISUAL_COLOR, rainbowColumn);
        maxDepth = Algorithms.treeDepth(tree);
        ColumnFilter filter = new AdditiveAggregation.NonAdditiveFilter(tree);
        getVisualColumnDescriptor(VISUAL_SIZE).setFilter(filter);
    }

    public void setVisibleRoot(int root) {
        int oldroot = this.visibleRoot;
        super.setVisibleRoot(root);
        if (oldroot != this.visibleRoot) {
            getShapes().clear();
            maxDepth = Algorithms.treeDepth(tree, this.visibleRoot);
        }
    }

    /**
     * @see infovis.visualization.DefaultVisualization#computeShapes(Rectangle2D)
     */
    public void computeShapes(Rectangle2D bounds) {
        float scaleX = 1;
        float scaleY = 1;
        getShapes().clear();
        NumberColumn sizeColumn = getWeightColumn();

        if (maxDepth == 1) {
            getShapes().findRect(ROOT).setRect(bounds);
            return;
        }
        clearRulers();
        double step;

        switch (orientation) {
        case ORIENTATION_NORTH:
        case ORIENTATION_SOUTH:
            rootHeight = Math.min(
                    (float) (bounds.getHeight()) / maxDepth, 
                    maxRootHeight);
            scaleX = (float) ((bounds.getWidth()) / sizeColumn
                    .getDoubleAt(visibleRoot));
            scaleY = (float) ((bounds.getHeight()- rootHeight) / (maxDepth - 1));

            for (int i = 0; i < maxDepth; i++) {
                DiscreteRulersBuilder.createHorizontalRuler(
                        bounds, 
                        Integer.toString(i),
                        rootHeight + i * scaleY,
                        getRulerTable());
            }

            step = LinearRulersBuilder.computeStep(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(),
                    scaleX);

            LinearRulersBuilder.createVerticalRulers(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(), 
                    bounds, 
                    step,
                    scaleX,
                    getRulerTable());
            break;

        case ORIENTATION_EAST:
        case ORIENTATION_WEST:
            rootHeight = Math.min(
                    (float) (bounds.getWidth()) / maxDepth, 
                    maxRootHeight);
            scaleX = (float) ((bounds.getHeight()) 
                    / sizeColumn.getDoubleAt(visibleRoot));
            scaleY = (float) ((bounds.getWidth() - rootHeight) / (maxDepth - 1));

            for (int i = 0; i < maxDepth; i++) {
                DiscreteRulersBuilder.createVerticalRuler(
                        bounds, 
                        Integer.toString(i),
                        rootHeight + i * scaleX,
                        getRulerTable());
            }

            step = LinearRulersBuilder.computeStep(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(), 
                    scaleY);
            LinearRulersBuilder.createHorizontalRulers(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(), 
                    bounds, 
                    step,
                    scaleY,
                    getRulerTable());
            break;
        }

        visitor.start(scaleX, scaleY);
        DepthFirst.visit(tree, visitor, visibleRoot);
    }

    public Dimension getPreferredSize() {
        NumberColumn sizeColumn = getWeightColumn();
        if (sizeColumn.getMinIndex() == -1) {
            return null;
        }
        double min = sizeColumn.getDoubleAt(sizeColumn.getMinIndex());
        switch (orientation) {
        case ORIENTATION_NORTH:
        case ORIENTATION_SOUTH:
            return new Dimension(
                    (int) (sizeColumn.getDoubleAt(Tree.ROOT) / min * 5),
                    maxDepth * 10);
        case ORIENTATION_EAST:
        case ORIENTATION_WEST:
            return new Dimension(maxDepth * 10, (int) (sizeColumn
                    .getDoubleAt(Tree.ROOT)
                    / min * 5));
        }
        return null;
    }

    public Dimension getMinimumSize() {
        NumberColumn sizeColumn = getWeightColumn();
        if (sizeColumn.getMinIndex() == -1) {
            return null;
        }
        double min = sizeColumn.getDoubleAt(sizeColumn.getMinIndex());
        switch (orientation) {
        case ORIENTATION_NORTH:
        case ORIENTATION_SOUTH:
            return new Dimension(
                    (int) (sizeColumn.getDoubleAt(Tree.ROOT) / min * 2),
                    maxDepth * 5);
        case ORIENTATION_EAST:
        case ORIENTATION_WEST:
            return new Dimension(maxDepth * 5, (int) (sizeColumn
                    .getDoubleAt(Tree.ROOT)
                    / min * 2));
        }
        return null;
    }

    float rectStartPos(int depth, float len) {
        if (depth == 0) {
            return 0;
        } else {
            return rootHeight + (depth - 1) * len;
        }
    }

    float rectLength(int depth, float len) {
        if (depth == 0) {
            return rootHeight;
        } else {
            return len;
        }
    }

    public class IcicleTreeVisitor implements DepthFirst.Visitor {
        protected float width;

        protected float height;

        protected int depth = 0;

        protected IntStack positions = new IntStack();

        protected NumberColumn sizeColumn;

        private DoubleColumn rulersPositions;

        public IcicleTreeVisitor() {
        }

        /**
         *  
         */
        public DoubleColumn getRulersPositionsColumn() {
            return rulersPositions;

        }

        /**
         * @param rulersPositions
         */
        public void setRulersPositionsColumn(DoubleColumn rulersPositions) {
            this.rulersPositions = rulersPositions;

        }

        public void start(float width, float height) {
            this.width = width;
            this.height = height;
            positions.clear();
            positions.ensureCapacity(maxDepth);
            positions.push(0);
            depth = -1;
            sizeColumn = getWeightColumn();
        }

        public int getPosition() {
            return positions.top();
        }

        protected void push() {
            positions.push(getPosition());
            depth++;
        }

        protected void pop() {
            positions.pop();
            depth--;
        }

        protected void setPosition(int pos) {
            positions.setTop(pos);
        }

        public boolean preorder(int node) {
            push();
            int position = getPosition();
            Rectangle2D bounds = getBounds();
            Rectangle2D.Float rect = findRectAt(node);
            int dp = sizeColumn.getIntAt(node);
            switch (orientation) {
            case ORIENTATION_NORTH:
                rect.x = position * width;
                rect.width = dp * width;
                rect.height = rectLength(depth, height);
                rect.y = (float) bounds.getHeight()
                        - rectStartPos(depth, height) - rect.height;
                break;
            case ORIENTATION_SOUTH:
                rect.x = position * width;
                rect.y = rectStartPos(depth, height);
                rect.width = dp * width;
                rect.height = rectLength(depth, height);
                break;
            case ORIENTATION_WEST:
                rect.y = position * width;
                rect.height = dp * width;
                rect.width = rectLength(depth, height);
                rect.x = (float) bounds.getWidth()
                        - rectStartPos(depth, height) - rect.width;
                break;
            case ORIENTATION_EAST:
                rect.y = position * width;
                rect.x = rectStartPos(depth, height);
                rect.height = dp * width;
                rect.width = rectLength(depth, height);
                break;
            }
            setShapeAt(node, rect);
            return true;
        }

        /**
         * @see infovis.tree.DepthFirst.Visitor#inorder(int)
         */
        public void inorder(int node) {
        }

        /**
         * @see infovis.tree.DepthFirst.Visitor#postorder(int)
         */
        public void postorder(int node) {
            pop();
            int position = getPosition();
            position += sizeColumn.getIntAt(node);
            setPosition(position);
        }
    }

}