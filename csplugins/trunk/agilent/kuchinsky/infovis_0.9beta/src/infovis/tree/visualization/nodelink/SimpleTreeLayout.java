/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.nodelink;

import infovis.Tree;
import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.tree.Algorithms;
import infovis.tree.DepthFirst;
import infovis.tree.visualization.NodeLinkTreeLayout;
import infovis.tree.visualization.NodeLinkTreeVisualization;
import infovis.utils.IntStack;
import infovis.visualization.Orientable;
import infovis.visualization.Orientation;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * Simple algorithm for tree layout.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 *
 * @infovis.factory TreeLayoutFactory "Simple"
 */
public class SimpleTreeLayout extends NodeLinkTreeLayout 
    implements Orientable, DepthFirst.Visitor {
    protected float                  margin        = 0;
    protected float                  maxRootHeight = 20;
    protected float                  rootHeight;
    protected transient NumberColumn sizeColumn;
    protected transient Rectangle2D  bounds;
    private transient float          width;
    private transient float          height;
    private transient int            depth         = 0;
    private transient IntStack       positions     = new IntStack();

    /**
     * Constructor.
     */
    public SimpleTreeLayout() {
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(
            Rectangle2D bounds, 
            NodeLinkTreeVisualization vis) {
        this.bounds = vis.getBounds();
        float scaleX = 1;
        float scaleY = 1;
        shapeColumn.clear();
        sizeColumn = vis.getWeightColumn();
        int maxDepth = Algorithms.treeDepth(vis.getTree());
        int visibleRoot = vis.getVisibleRoot();
        margin = (float)vs.getMaxSize()/2;

        switch (getOrientation()) {
        case ORIENTATION_NORTH:
        case ORIENTATION_SOUTH:
            rootHeight = Math.min(
                    (float) (bounds.getHeight() - margin) / maxDepth, 
                    maxRootHeight);
            scaleX = (float) ((bounds.getWidth() - margin) 
                    / sizeColumn.getDoubleAt(visibleRoot));
            scaleY = (float) ((bounds.getHeight() - margin - rootHeight)
                    / (maxDepth - 1));
            break;

        case ORIENTATION_EAST:
        case ORIENTATION_WEST:
            rootHeight = Math.min(
                    (float) (bounds.getWidth() - margin) / maxDepth, 
                    maxRootHeight);
            scaleX = (float) ((bounds.getHeight() - margin) 
                    / sizeColumn.getDoubleAt(visibleRoot));
            scaleY = (float) ((bounds.getWidth() - rootHeight) 
                    / (maxDepth - 1));
            break;
        }
        computeSizes();
        start(scaleX, scaleY, maxDepth);
        DepthFirst.visit(vis.getTree(), this, visibleRoot);

    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Simple";
    }

    protected float rectStartPos(int depth, float len) {
        if (depth == 0) {
            return 0;
        }
        else {
            return rootHeight + (depth - 1) * len;
        }
    }

    float rectLength(int depth, float len) {
        if (depth == 0) {
            return rootHeight;
        }
        else {
            return len;
        }
    }

    protected void start(float width, float height, int maxDepth) {
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

    /**
     * {@inheritDoc}
     */
    public boolean preorder(int node) {
        push();
        int position = getPosition();
        Rectangle2D.Float rect = findRectAt(node);
        float dp = sizeColumn.getFloatAt(node);
        switch (getOrientation()) {
        case ORIENTATION_NORTH:
            rect.x = position * width + margin - rect.width/2 + dp*width/2;
            //rect.width = dp * width;
            //rect.height = rectLength(depth, height);
            rect.y = (float) bounds.getHeight() - margin
                    - rectStartPos(depth, height) + rect.height/2 
                    - rectLength(depth, height)/2;
            break;
        case ORIENTATION_SOUTH:
            rect.x = position * width + margin - rect.width/2 + dp*width/2;
            rect.y = rectStartPos(depth, height) - rect.height/2 
                + rectLength(depth, height)/2;
            //rect.width = dp * width;
            //rect.height = rectLength(depth, height);
            break;
        case ORIENTATION_WEST:
            rect.y = position * width + margin - rect.height/2 + dp*width/2;
            //rect.height = dp * width;
            //rect.width = rectLength(depth, height);
            rect.x = (float) bounds.getWidth() - margin
                    - rectStartPos(depth, height) - rect.width/2 
                    - rectLength(depth, height)/2;
            break;
        case ORIENTATION_EAST:
            rect.y = position * width + margin - rect.height/2 + dp*width/2;
            rect.x = rectStartPos(depth, height) - rect.width/2
            + rectLength(depth, height)/2;
            //rect.height = dp * width;
            //rect.width = rectLength(depth, height);
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

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        NodeLinkTreeVisualization visualization = (NodeLinkTreeVisualization)vis;
        int leafs = Algorithms.leafCount(visualization, Tree.ROOT);
        int depth = Algorithms.treeDepth(visualization);
        if (Orientation.isHorizontal(visualization.getOrientation())) {
            return new Dimension(leafs * 5, depth*5);
        }
        else {
            return new Dimension(depth*5, leafs * 5);
        }
    }    
}
