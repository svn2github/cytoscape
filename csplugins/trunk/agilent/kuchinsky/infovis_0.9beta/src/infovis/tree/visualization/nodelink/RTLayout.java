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
import infovis.column.FloatColumn;
import infovis.column.IntColumn;
import infovis.tree.visualization.NodeLinkTreeLayout;
import infovis.tree.visualization.NodeLinkTreeVisualization;
import infovis.utils.RectPool;
import infovis.utils.RowIterator;
import infovis.visualization.Orientable;
import infovis.visualization.render.VisualSize;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Implements the Reingold and Tilford Tree Layout.
 * 
 *  E.M. Reingold and J.S. Tilford,
 *  "Tidier Drawings of Trees",
 *  "IEEE Transactions on Software Engineering"
 *  pages "223--228"
 *  1981
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * 
 * @infovis.factory TreeLayoutFactory "Reingold&Tilford"
 */
public class RTLayout extends NodeLinkTreeLayout implements Orientable {
    protected FloatColumn prelim;
    protected FloatColumn modifier;
    protected IntColumn leftNeighbor;
    protected IntColumn prev;
    protected IntColumn next;
    private int[] previousNodeAtLevel;
    private float[] maxNodeHightAtLevel;
    private float[] maxNodeWidthAtLevel;
    protected int maxDepth = Integer.MAX_VALUE;
    private float topXAdjustment = 0;
    private float topYAdjustment = 0;
//    private Line2D.Float tmpLine = new Line2D.Float();
//    private Point2D.Float tmpPoint = new Point2D.Float();
    protected Rectangle2D.Float bbox = null;
    protected VisualSize vs;

    /**
     * Constructor.
     *
     */
    public RTLayout() {
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Reingold&Tilford";
    }
    
    protected void setVisualization(Visualization vis) {
        super.setVisualization(vis);

        vs = VisualSize.get(vis);
        levelSeparation = (float) vs.getMaxSize();        
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
        RectPool.freeRect(bbox);
        bbox = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (bbox == null) {
            computeShapes(null, vis);
        }
        if (! bbox.isEmpty()) {
            return new Dimension((int)bbox.width, (int)bbox.height);
        }
        else {
            return super.getPreferredSize(vis);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, NodeLinkTreeVisualization vis) {
        if (bbox != null) {
            return;
        }
        shapeColumn.clear();
        prelim = new FloatColumn("#prelim");
        modifier = new FloatColumn("#modifier");
        leftNeighbor = new IntColumn("#leftNeighbor");
        prev = new IntColumn("#prev");
        next = new IntColumn("#next");
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            Rectangle2D.Float rect = findRectAt(row);
            vs.setRectSizeAt(row, rect);
            setShapeAt(row, rect); // force notification
        }
        positionTree(visualization.getVisibleRoot());
        bbox = shapeColumn.getBounds();
        centerTree();
        prelim = null;
        modifier = null;
        leftNeighbor = null;
        prev = null;
        next = null;
    }

    protected void centerTree() {
        Rectangle2D.Float bbox = shapeColumn.getBounds();
        if (bbox.x == 0 && bbox.y == 0) return;
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            Rectangle2D.Float rect = getRectAt(row);
            if (rect == null) {
                continue;
            }
            rect.x = (rect.x - bbox.x);
            rect.y = (rect.y - bbox.y);
            setShapeAt(row, rect);
        }
    }
    
    protected void setLeftNeighbor(int node, int left) {
        leftNeighbor.setExtend(node, left);
    }

    protected int getLeftNeighbor(int node) {
        return leftNeighbor.get(node);
    }
    
    protected boolean hasLeftSibling(int node) {
        return prev.get(node) != Tree.NIL;
    }
    
    protected int getLeftSibling(int node) {
        return prev.get(node);
    }
    
    protected void setLeftSibling(int node, int left) {
        prev.setExtend(node, left);
    }
    
    protected int getRightSibling(int node) {
        return next.get(node);
    }
    
    protected void setRightSibling(int node, int right) {
        next.setExtend(node, right);
    }
    
    protected float getXCoord(int node) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect == null)
            return 0;
        else
            return rect.x;
    }
    
    protected void setXCoord(int node, float x) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect != null)
            rect.x = x;
    }
    
    protected float getYCoord(int node) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect == null)
            return 0;
        else
            return rect.y;
    }
    
    protected void setYCoord(int node, float y) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect != null)
            rect.y = y;
    }
    
    protected float getLayoutSize(int node) {
        switch (getOrientation()) {
            case ORIENTATION_NORTH :
            case ORIENTATION_SOUTH :
                return getWidth(node);
            case ORIENTATION_EAST :
            case ORIENTATION_WEST :
                return getHeight(node);
        }
        return 0;
    }
    
    protected float getOppositeLayoutSize(int node) {
        switch (getOrientation()) {
            case ORIENTATION_NORTH :
            case ORIENTATION_SOUTH :
                return getHeight(node);
            case ORIENTATION_EAST :
            case ORIENTATION_WEST :
                return getWidth(node);
        }
        return 0;
    }
    
    protected float getMeanNodeSize(int leftNode, int rightNode) {
        float nodeSize = 0;
        if (leftNode != Tree.NIL)
            nodeSize += getLayoutSize(leftNode) / 2;
        if (rightNode != Tree.NIL)
            nodeSize += getLayoutSize(rightNode) / 2;
        return nodeSize;
    }

    protected float getHeight(int node) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect != null)
            return rect.height;
        return (float) vs.getDefaultSize();
    }
    
    protected float getWidth(int node) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect != null)
            return rect.width;
        return (float) vs.getDefaultSize();
    }
    
    protected float getPrelim(int node) {
        return prelim.get(node);
    }

    protected void setPrelim(int node, float p) {
        prelim.setExtend(node, p);
    }

    protected float getModifier(int node) {
        return modifier.get(node);
    }

    protected void setModifier(int node, float mod) {
        modifier.setExtend(node, mod);
    }

    protected void positionTree(int node) {
        if (node != Tree.NIL) {
            initPrevNodeList();
            setLeftSibling(node, Tree.NIL);
            setRightSibling(node, Tree.NIL);
            firstWalk(node, 0);
            topXAdjustment = (float) vs.getMaxSize() / 2;
            topYAdjustment = 0;
            //bbox.setRect(0, 0, vs.getMaxSize(), vs.getMaxSize());
            secondWalk(node, 0, 0);
            resetPrevNodeList();
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void resetPrevNodeList() {
        previousNodeAtLevel = null;
        maxNodeHightAtLevel = null;
        maxNodeWidthAtLevel = null;
    }

    /**
     * DOCUMENT ME!
     */
    protected void initPrevNodeList() {
        previousNodeAtLevel = new int[100];
        Arrays.fill(previousNodeAtLevel, 0, 100, Tree.NIL);
        maxNodeHightAtLevel = new float[100];
        maxNodeWidthAtLevel = new float[100];
    }

    /**
     * Computes the preliminary position and modifier.
     *
     * @param node appex node.
     * @param level depth level.
     */
    protected void firstWalk(int node, int level) {
        setModifier(node, 0);
        setPrelim(node, 0);

        updateLevelHeight(node, level);
        updateLevelWidth(node, level);
        updateLeftAndRightNeighborsAtLevel(node, level);

        if (isLeaf(node) || level == maxDepth) {
            if (hasLeftSibling(node)) {
                int left = getLeftSibling(node);
                setPrelim(
                    node,
                    getPrelim(left)
                        + siblingSeparation
                        + getMeanNodeSize(left, node));
                //getLayoutSize(left));
            }
            else {
                setPrelim(node, 0);
            }
        }
        else {
            int rightmost = Tree.NIL;
            for (RowIterator iter = childrenIterator(node);
                iter.hasNext();
                ) {
                int child = iter.nextRow();
                // update the left/right sibling tables here
                setLeftSibling(child, rightmost);
                if (rightmost != Tree.NIL) {
                    setRightSibling(rightmost, child);
                }
                firstWalk(child, level + 1);
                rightmost = child;
            }
            setRightSibling(rightmost, Tree.NIL);

            int leftmost = getFirstChild(node);
            float midpoint =
                (getPrelim(leftmost) + getPrelim(rightmost)) / 2;

            if (hasLeftSibling(node)) {
                int left = getLeftSibling(node);
                setPrelim(node, getPrelim(left) + siblingSeparation +
                //getLayoutSize(left));
                getMeanNodeSize(left, node));
                setModifier(node, getPrelim(node) - midpoint);
                apportion(node, level);
            }
            else {
                setPrelim(node, midpoint);
            }
        }
    }
    
    protected void apportion(int node, int level) {
        int leftmost = getFirstChild(node);
        int neighbor = getLeftNeighbor(leftmost);
        // neighbor is on the left of leftmost
        int depthToStop = maxDepth - level;

        for (int compareDepth = 1;
            leftmost != Tree.NIL
                && neighbor != Tree.NIL
                && compareDepth <= depthToStop;
            ) {
            float leftModSum = 0;
            float rightModSum = 0;
            int ancestorLeftmost = leftmost;
            int ancestorNeighbor = neighbor;

            for (int i = 0; i < compareDepth; i++) {
                ancestorLeftmost = getParent(ancestorLeftmost);
                ancestorNeighbor = getParent(ancestorNeighbor);
                rightModSum += getModifier(ancestorLeftmost);
                leftModSum += getModifier(ancestorNeighbor);
            }

            float moveDistance =
                (getPrelim(neighbor)
                    + leftModSum
                    + subtreeSeparation
                    + getMeanNodeSize(leftmost, neighbor))
                    - (getPrelim(leftmost) + rightModSum);

            if (moveDistance > 0) {
                int tmp = node;
                int leftSiblings = 0;
                while (tmp != Tree.NIL && tmp != ancestorNeighbor) {
                    leftSiblings++;
                    tmp = getLeftSibling(tmp);
                }

                if (tmp != Tree.NIL) {
                    float portion = moveDistance / leftSiblings;
                    tmp = node;

                    while (tmp != ancestorNeighbor) {
                        setPrelim(tmp, getPrelim(tmp) + moveDistance);
                        setModifier(
                            tmp,
                            getModifier(tmp) + moveDistance);
                        moveDistance -= portion;
                        tmp = getLeftSibling(tmp);
                    }
                }
            }
            //            else {
            //                return;
            //            }
            compareDepth++;
            if (isLeaf(leftmost)) {
                leftmost = getLeftMost(node, 0, compareDepth);
            }
            else {
                leftmost = getFirstChild(leftmost);
            }
            if (leftmost != Tree.NIL)
                neighbor = getLeftNeighbor(leftmost);
        }
    }
    
    protected int getLeftMost(int node, int level, int depth) {
        if (level >= depth)
            return node;
        if (isLeaf(node))
            return Tree.NIL;
        for (RowIterator iter = childrenIterator(node);
            iter.hasNext();
            ) {
            int child = iter.nextRow();
            int leftMost = getLeftMost(child, level + 1, depth);
            if (leftMost != Tree.NIL)
                return leftMost;
        }
        return Tree.NIL;
    }

    protected void updateLevelHeight(int node, int level) {
        if (maxNodeHightAtLevel[level] < getHeight(node)) {
            maxNodeHightAtLevel[level] = getHeight(node);
        }
    }

    protected void updateLevelWidth(int node, int level) {
        if (maxNodeWidthAtLevel[level] < getWidth(node)) {
            maxNodeWidthAtLevel[level] = getWidth(node);
        }
    }

    protected void updateLeftAndRightNeighborsAtLevel(
        int node,
        int level) {
        int prev = previousNodeAtLevel[level];
        setLeftNeighbor(node, prev);
        previousNodeAtLevel[level] = node;
    }

    /**
     * Second pass to propagate the modifiers to actual positions.
     *
     * @param node the appex node.
     * @param level the depth level.
     * @param xModifierSum the accumulated modifiers for X.
     * @param yModifierSum the accumulated modifiers for Y.
     */
    protected void secondWalk(int node, int level, float modifierSum) {
        while (level < maxDepth) {
            float x = 0;
            float y = 0;

            switch (getOrientation()) {
                case ORIENTATION_NORTH :
                    x =
                        topXAdjustment
                            + getPrelim(node)
                            + modifierSum
                            - getLayoutSize(node) / 2;
                    y =
                        topYAdjustment
                            - level
                                * ((float) vs.getMaxSize()
                                    + levelSeparation);
                    break;
                case ORIENTATION_SOUTH :
                    x =
                        topXAdjustment
                            + getPrelim(node)
                            + modifierSum
                            - getLayoutSize(node) / 2;
                    y =
                        topYAdjustment
                            + level
                                * ((float) vs.getMaxSize()
                                    + levelSeparation);
                    break;
                case ORIENTATION_EAST :
                    y =
                        topXAdjustment
                            + getPrelim(node)
                            + modifierSum
                            - getLayoutSize(node) / 2;
                    x =
                        topYAdjustment
                            + level
                                * ((float) vs.getMaxSize()
                                    + levelSeparation);
                    break;
                case ORIENTATION_WEST :
                    y =
                        topXAdjustment
                            + getPrelim(node)
                            + modifierSum
                            - getLayoutSize(node) / 2;
                    x =
                        topYAdjustment
                            - level
                                * ((float) vs.getMaxSize()
                                    + levelSeparation);
                    break;
            }

            setXCoord(node, x);
            setYCoord(node, y);
            // Handle terminal recursion by trying to unroll the final call
            // to avoid java stack overflow.
            int right = getRightSibling(node);
            if (right == Tree.NIL) {
                if (isLeaf(node)) return;
                modifierSum += getModifier(node);
                node = getFirstChild(node);
                level++;
            }
            else {
                if (! isLeaf(node)) {
                    secondWalk(
                            getFirstChild(node),
                            level + 1,
                            modifierSum + getModifier(node));
                }
                node = right;
            }
        }
    }

}
