/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.*;
import infovis.Graph;
import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.visualization.layout.GraphVizLayout;
import infovis.visualization.*;
import infovis.visualization.magicLens.Fisheye;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Node-Link Visualization for graphs. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.31 $
 * 
 * @infovis.factory VisualizationFactory "Graph Node Link" infovis.Graph
 */
public class NodeLinkGraphVisualization extends GraphVisualization
    implements NodeAccessor, LinkShaper {
    protected NodeLinkGraphLayout layout;
    protected boolean paintingLinks = true;
    protected boolean debugging = true;
    protected transient Dimension preferredSize;
    protected ShapeColumn linkShapes;
    protected LinkVisualization linkVisualization;

    public NodeLinkGraphVisualization(Graph graph) {
        super(graph, graph.getVertexTable());
        linkShapes = new ShapeColumn("#linkShapes");
        linkVisualization = new LinkVisualization(graph.getEdgeTable(), this, this);
        linkVisualization.setOrientation(ORIENTATION_INVALID);
        linkVisualization.setLinkShaper(this);
        clearRulers();
    }
    
    public NodeLinkGraphVisualization(Table table) {
        this(DefaultGraph.getGraph(table));
    }
    
    public void init(Visualization vis, ShapeColumn shapes) {
    }
    
    // NodeAccessor interface    
    public int getStartNode(int link) {
        return getGraph().getInVertex(link);
    }

    public int getEndNode(int link) {
        return getGraph().getOutVertex(link);
    }
    
    // LinkShaped interface
    public Shape computeLinkShape(
        int link,
        NodeAccessor accessor,
        Shape s) {
        if (s != null && !(s instanceof Line2D.Float))
            return s;
        Line2D.Float l = new Line2D.Float();
        s = l;
        linkShapes.setExtend(link, s);
        int v1 = accessor.getStartNode(link);
        int v2 = accessor.getEndNode(link);
        Rectangle2D r1 = getShapeAt(v1).getBounds2D();
        Rectangle2D r2 = getShapeAt(v2).getBounds2D();
        l.x1 = (float)r1.getCenterX();
        l.y1 = (float)r1.getCenterY();
        l.x2 = (float)r2.getCenterX();
        l.y2 = (float)r2.getCenterY();
        return s;
    }

    
    public void paintItems(Graphics2D graphics, Rectangle2D bounds) {
        if (isPaintingLinks())
            linkVisualization.paint(graphics, bounds);
        super.paintItems(graphics, bounds);
    }
    
    public void dispose() {
        super.dispose();
        linkVisualization.dispose();
    }
    
    public void invalidate() {
        super.invalidate();
        //linkVisualization.invalidate();
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
        if (! layout.isFinished()) {
            layout.incrementLayout(bounds, this);
            repaint();
        }
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
        return linkShapes;
    }
    /**
     * @return Returns the layout.
     */
    public Layout getLayout() {
        if (layout == null) {
            setLayout(new GraphVizLayout());
        }
        return layout;
    }
    /**
     * @param layout The layout to set.
     */
    public void setLayout(NodeLinkGraphLayout layout) {
	if (this.layout == layout) return;
	firePropertyChange(PROPERTY_LAYOUT, this.layout, layout);
        this.layout = layout;
        invalidate();
    }
    
    public Dimension getPreferredSize() {
        if (preferredSize == null)
            preferredSize = super.getPreferredSize();
        return preferredSize;
    }
}
