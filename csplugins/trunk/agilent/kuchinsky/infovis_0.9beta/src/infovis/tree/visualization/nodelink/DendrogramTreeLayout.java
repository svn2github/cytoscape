/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.nodelink;

import infovis.Visualization;
import infovis.column.DoubleColumn;
import infovis.column.NumberColumn;
import infovis.tree.Algorithms;
import infovis.tree.DepthFirst;
import infovis.tree.visualization.NodeLinkTreeLayout;
import infovis.tree.visualization.NodeLinkTreeVisualization;
import infovis.utils.RowIterator;
import infovis.visualization.Orientation;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * Layout for Dendrograms.
 * 
 * <p>May use a length column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 * @infovis.factory TreeLayoutFactory "Dendrogram"
 */
public class DendrogramTreeLayout extends NodeLinkTreeLayout {
    protected transient float      margin;
    protected double               scale;
    protected double               origin;
    private transient DoubleColumn posColumn         = new DoubleColumn("pos");

    /**
     * Create a DendrogramTreeLayout.
     *
     */
    public DendrogramTreeLayout() {
    }

    protected DoubleColumn createPosColumn(final NodeLinkTreeVisualization tree) {
        posColumn.clear();
        DepthFirst.visit(tree, new DepthFirst.Visitor() {
            int depth = 0;
            public boolean preorder(int node) {
                posColumn.setExtend(node, depth);
                depth++;
                return true;
            }

            public void postorder(int node) {
                depth--;
            }
        }, tree.getVisibleRoot());
        return posColumn;
    }

    protected DoubleColumn createPosColumn(
            final NodeLinkTreeVisualization tree,
            final NumberColumn lengthColumn) {
        posColumn.clear();
        DepthFirst.visit(tree, new DepthFirst.Visitor() {
            double pos = 0;

            public boolean preorder(int node) {
                if (!lengthColumn.isValueUndefined(node)) {
                    pos += lengthColumn.getDoubleAt(node);
                }
                posColumn.setExtend(node, pos);
                return true;
            }

            public void postorder(int node) {
                if (!lengthColumn.isValueUndefined(node)) {
                    pos -= lengthColumn.getDoubleAt(node);
                }
            }
        }, tree.getVisibleRoot());
        return posColumn;
    }

    /**
     * Returns the depth of a specified node.
     * @param node the node
     * @return the depth of a specified node.
     */
    public double nodeDepth(int node) {
        if (posColumn.isValueUndefined(node)) {
            return 0;
        }
        return scale * (posColumn.getDoubleAt(node) - origin);
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, NodeLinkTreeVisualization vis) {
        shapeColumn.clear();
        int visibleRoot = vis.getVisibleRoot();
        margin = (float) vs.getMaxSize() / 2;
        //levelSeparation = (float) vs.getMaxSize();
        NumberColumn lengthColumn = (NumberColumn) vis.getLinkVisualization()
                .getVisualColumn("length");
        if (lengthColumn == null) {
            createPosColumn(vis);
        }
        else {
            createPosColumn(vis, lengthColumn);
        }
        if (bounds == null || bounds.getWidth() == 0 || bounds.getHeight() == 0
                || posColumn.isEmpty()
                || posColumn.getMinIndex() == posColumn.getMaxIndex()) {
            scale = 1;
            origin = 0;
        }
        else {
            short o = getOrientation();
            double len = Orientation.isHorizontal(o)
                    ? bounds.getWidth()
                    : bounds.getHeight();
            len -= margin;
            if (o == ORIENTATION_SOUTH || o == ORIENTATION_EAST) {
                origin = posColumn.getDoubleMin();
                scale = len / (posColumn.getDoubleMax() - origin);
            }
            else {
                origin = posColumn.getDoubleMin();
                scale = len / (posColumn.getDoubleMax() - origin);
            }
        }
        computeSizes();
        computeShapes(vis, visibleRoot, 0);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Dendrogram";
    }
    protected float computeShapes(NodeLinkTreeVisualization vis, int node, float start) {
        float pos = start;
        if (!vis.isLeaf(node)) {
            Rectangle2D.Float first = null;
            int c = -1;
            for (RowIterator iter = childrenIterator(node); iter.hasNext();) {
                c = iter.nextRow();
                start = computeShapes(vis, c, start);
                start += siblingSeparation;
                if (first == null) {
                    first = getRectAt(c);
                }
            }
            start -= siblingSeparation;
            Rectangle2D.Float last = getRectAt(c);
            if (Orientation.isHorizontal(getOrientation())) {
                pos = (first.y + last.y) / 2;
            }
            else {
                pos = (first.x + last.x) / 2;
            }
        }
        Rectangle2D.Float rect = getRectAt(node);
        switch (getOrientation()) {
        case ORIENTATION_NORTH:
        case ORIENTATION_SOUTH:
            rect.x = pos;
            pos += rect.width;
            rect.y = (float) nodeDepth(node);
            break;
        case ORIENTATION_EAST:
        case ORIENTATION_WEST:
            rect.y = pos;
            pos += rect.height;
            rect.x = (float) nodeDepth(node);
            break;
        }
        setShapeAt(node, rect);
        start = Math.max(start, pos);
        return start;
    }

    protected float getLength(int node) {
        Rectangle2D.Float rect = getRectAt(node);
        if (rect == null) {
            return 0;
        }
        if (Orientation.isHorizontal(getOrientation())) {
            return rect.height;
        }
        else {
            return rect.width;
        }
    }

    protected float computeLength(NodeLinkTreeVisualization vis, int node) {
        if (isLeaf(node)) {
            return getLength(node);
        }
        float ret = 0;
        for (RowIterator iter = childrenIterator(node); iter.hasNext();) {
            int c = iter.nextRow();
            ret += computeLength(vis, c);
            ret += siblingSeparation;
        }
        ret -= siblingSeparation;
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (bounds != null) {
            return new Dimension((int) bounds.getWidth(), (int) bounds
                    .getHeight());
        }
        try {
            setVisualization(vis);
            computeSizes();
            float len = computeLength(visualization, visualization
                    .getVisibleRoot());
            int depth = Algorithms.treeDepth(visualization, visualization
                    .getVisibleRoot());
            Dimension d;
            if (Orientation.isHorizontal(getOrientation())) {
                d = new Dimension((int) (depth * vs.getMaxSize()), (int) len);
            }
            else {
                d = new Dimension((int) len, (int) (depth * vs.getMaxSize()));
            }
            bounds = new Rectangle2D.Float(0, 0, d.width, d.height);
            return d;
        } finally {
            unsetVisualization();
        }
    }


}
