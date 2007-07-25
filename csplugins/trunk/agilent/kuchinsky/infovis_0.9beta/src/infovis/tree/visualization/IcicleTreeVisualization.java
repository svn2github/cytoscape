/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import infovis.Tree;
import infovis.Visualization;
import infovis.column.*;
import infovis.metadata.AdditiveAggregation;
import infovis.tree.Algorithms;
import infovis.tree.DepthFirst;
import infovis.utils.IntStack;
import infovis.visualization.ItemRenderer;
import infovis.visualization.Layout;
import infovis.visualization.ruler.*;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.41 $
 * 
 * @infovis.factory VisualizationFactory "Tree Icicle" infovis.Tree
 */
public class IcicleTreeVisualization extends TreeVisualization 
    implements Layout {
    protected RainbowColumn rainbowColumn;
    protected int                  maxDepth;
    protected float                maxRootHeight = 20;
    protected float                rootHeight;
    protected int                  preferredItemSize;
    protected float                width;
    protected float                height;
    private transient int          depth         = 0;
    private transient IntStack     positions     = new IntStack();

    /**
     * Constructor for IcicleTreeVisualization.
     * 
     * @param tree the tree
     */
    public IcicleTreeVisualization(Tree tree) {
        this(tree, null);
        rulers = new RulerTable();
    }

    /**
     * Constructor for IcicleTreeVisualization.
     * 
     * @param tree the tree
     * @param ir the root ItemRederer
     */
    public IcicleTreeVisualization(Tree tree, ItemRenderer ir) {
        super(tree, ir);
        rainbowColumn = RainbowColumn.findColumn(tree);
        setVisualColumn(VISUAL_COLOR, rainbowColumn);
        ColumnFilter filter = new AdditiveAggregation.NonAdditiveFilter(tree);
        getVisualColumnDescriptor(VISUAL_SIZE).setFilter(filter);
    }

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Icicle Tree";
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }
    
    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        float scaleX = 1;
        float scaleY = 1;
        ShapeColumn shapes = getShapes();
        shapes.clear();
        clearRulers();

        maxDepth = Algorithms.treeDepth(this);
        NumberColumn sizeColumn = getWeightColumn();

        if (maxDepth == 1) {
            getShapes().findRect(Tree.ROOT).setRect(bounds);
            return;
        }
        double step;

        switch (getOrientation()) {
        case ORIENTATION_SOUTH:
            rootHeight = Math.min(
                    (float) (bounds.getHeight()) / maxDepth,
                    maxRootHeight);
            scaleX = (float) ((bounds.getWidth()) / sizeColumn
                    .getDoubleAt(getVisibleRoot()));
            scaleY = (float) ((bounds.getHeight() - rootHeight) / (maxDepth - 1));

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
            RulerTable.setAxisLabel(getRulerTable(), "Depth", true);
            RulerTable.setAxisLabel(getRulerTable(), sizeColumn.getName(), false);
            
            break;
        case ORIENTATION_NORTH:
            rootHeight = Math.min(
                    (float) (bounds.getHeight()) / maxDepth,
                    maxRootHeight);
            scaleX = (float) ((bounds.getWidth()) / sizeColumn
                    .getDoubleAt(getVisibleRoot()));
            scaleY = (float) ((bounds.getHeight() - rootHeight) / (maxDepth - 1));

            for (int i = 0; i < maxDepth; i++) {
                DiscreteRulersBuilder.createHorizontalRuler(
                        bounds, 
                        Integer.toString(i), 
                        bounds.getHeight() - rootHeight - i * scaleY,
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
            RulerTable.setAxisLabel(getRulerTable(), "Depth", true);
            RulerTable.setAxisLabel(getRulerTable(), sizeColumn.getName(), false);

            break;

        case ORIENTATION_EAST:
            rootHeight = Math.min(
                    (float) (bounds.getWidth()) / maxDepth,
                    maxRootHeight);
            scaleX = (float) ((bounds.getHeight()) / sizeColumn
                    .getDoubleAt(getVisibleRoot()));
            scaleY = (float) ((bounds.getWidth() - rootHeight) / (maxDepth - 1));

            for (int i = 0; i < maxDepth; i++) {
                DiscreteRulersBuilder.createVerticalRuler(
                        bounds, 
                        Integer.toString(i), 
                        rootHeight + i * scaleY, 
                        getRulerTable());
            }

            step = LinearRulersBuilder.computeStep(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(),
                    scaleX);

            LinearRulersBuilder.createHorizontalRulers(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(),
                    bounds,
                    step,
                    scaleX,
                    getRulerTable(),
                    false);
            RulerTable.setAxisLabel(getRulerTable(), "Depth", false);
            RulerTable.setAxisLabel(getRulerTable(), sizeColumn.getName(), true);
            break;
        case ORIENTATION_WEST:
            rootHeight = Math.min(
                    (float) (bounds.getWidth()) / maxDepth,
                    maxRootHeight);
            scaleX = (float) ((bounds.getHeight()) / sizeColumn
                    .getDoubleAt(getVisibleRoot()));
            scaleY = (float) ((bounds.getWidth() - rootHeight) / (maxDepth - 1));

            for (int i = 0; i < maxDepth; i++) {
                DiscreteRulersBuilder.createVerticalRuler(
                        bounds, 
                        Integer.toString(i), 
                        bounds.getWidth() - rootHeight - i * scaleY, 
                        getRulerTable());
            }

            step = LinearRulersBuilder.computeStep(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(),
                    scaleX);

            LinearRulersBuilder.createHorizontalRulers(
                    sizeColumn.getDoubleMin(),
                    sizeColumn.getDoubleMax(),
                    bounds,
                    step,
                    scaleX,
                    getRulerTable(),
                    false);
            RulerTable.setAxisLabel(getRulerTable(), "Depth", false);
            RulerTable.setAxisLabel(getRulerTable(), sizeColumn.getName(), true);
        }

        start(scaleX, scaleY);
        DepthFirst.visit(
                this, 
                new DepthFirst.Visitor() {
                    NumberColumn sizeColumn = getWeightColumn();
                    /**
                     * {@inheritDoc}
                     */
                    public boolean preorder(int node) {
                        push();
                        int position = getPosition();
                        Rectangle2D bounds = getBounds();
                        Rectangle2D.Float rect = findRectAt(node);
                        int dp = sizeColumn.getIntAt(node);
                        switch (getOrientation()) {
                        case ORIENTATION_NORTH:
                            rect.x = position * width;
                            rect.width = dp * width;
                            rect.height = rectLength(depth, height);
                            rect.y = (float) bounds.getHeight() - rectStartPos(depth, height)
                                    - rect.height;
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
                            rect.x = (float) bounds.getWidth() - rectStartPos(depth, height)
                                    - rect.width;
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
                     * {@inheritDoc}
                     */
                    public void postorder(int node) {
                        pop();
                        int position = getPosition();
                        position += sizeColumn.getIntAt(node);
                        setPosition(position);
                    }
                },
                getVisibleRoot());
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        TreeVisualization visualization = (TreeVisualization) vis;
        NumberColumn sizeColumn = visualization.getWeightColumn();
        if (sizeColumn.getMinIndex() == -1) {
            return null;
        }
        double min = sizeColumn.getDoubleMin();
        if (min == 0) {
            min = 1;
        }
        switch (getOrientation()) {
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

    protected float rectStartPos(int depth, float len) {
        if (depth == 0) {
            return 0;
        }
        else {
            return rootHeight + (depth - 1) * len;
        }
    }

    protected float rectLength(int depth, float len) {
        if (depth == 0) {
            return rootHeight;
        }
        else {
            return len;
        }
    }

    protected void start(float width, float height) {
        this.width = width;
        this.height = height;
        positions.clear();
        positions.ensureCapacity(maxDepth);
        positions.push(0);
        depth = -1;
    }

    protected int getPosition() {
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
}