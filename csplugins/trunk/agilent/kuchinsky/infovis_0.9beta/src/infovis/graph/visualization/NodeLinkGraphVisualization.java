/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.*;
import infovis.column.ShapeColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.visualization.layout.GraphVizLayout;
import infovis.visualization.*;
import infovis.visualization.magicLens.Fisheye;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Node-Link Visualization for graphs.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.33 $
 * 
 * @infovis.factory VisualizationFactory "Graph Node Link" infovis.Graph
 */
public class NodeLinkGraphVisualization extends GraphVisualization 
    implements NodeAccessor {
    protected NodeLinkGraphLayout layout;
    protected boolean             paintingLinks = true;
    protected boolean             debugging     = true;
    protected LinkVisualization   linkVisualization;
    protected transient Dimension preferredSize;

    public NodeLinkGraphVisualization(Graph graph) {
        super(graph, graph.getVertexTable());
        linkVisualization = new LinkVisualization(
                graph.getEdgeTable(),
                this,
                this);
        linkVisualization.setOrientation(ORIENTATION_INVALID);
    }

    public NodeLinkGraphVisualization(Table table) {
        this(DefaultGraph.getGraph(table));
    }

    public void paintItems(Graphics2D graphics, Rectangle2D bounds) {
        if (isPaintingLinks())
            linkVisualization.paint(graphics, bounds);
        super.paintItems(graphics, bounds);
    }

    public void invalidate() {
        super.invalidate();
        // linkVisualization.invalidate();
        preferredSize = null;
    }

    public void setFisheye(Fisheye fisheye) {
        super.setFisheye(fisheye);
        linkVisualization.setFisheye(fisheye);
    }

    public Visualization getVisualization(int index) {
        if (index == 0)
            return linkVisualization;
        return null;
    }

    public void validateShapes(Rectangle2D bounds) {
        super.validateShapes(bounds);
        if (!layout.isFinished()) {
            layout.incrementLayout(bounds, this);
            repaint();
        }
    }
    
    public Layout getLayout() {
        if (layout == null) {
            setLayout(new GraphVizLayout());
        }
        return layout;
    }

    public void setLayout(NodeLinkGraphLayout layout) {
        if (this.layout == layout)
            return;
        firePropertyChange(PROPERTY_LAYOUT, this.layout, layout);
        this.layout = layout;
        invalidate();
    }

    public Dimension getPreferredSize() {
        if (preferredSize == null)
            preferredSize = super.getPreferredSize();
        return preferredSize;
    }

    public boolean isPaintingLinks() {
        return paintingLinks;
    }

    public void setPaintingLinks(boolean b) {
        if (paintingLinks != b) {
            paintingLinks = b;
            repaint();
        }
    }

    /**
     * @return Returns the linkShapes.
     */
    public ShapeColumn getLinkShapes() {
        return linkVisualization.getShapes();
    }

    // NodeAccessor interface
    public int getStartNode(int link) {
        return getGraph().getFirstVertex(link);
    }

    public int getEndNode(int link) {
        return getGraph().getSecondVertex(link);
    }
}
