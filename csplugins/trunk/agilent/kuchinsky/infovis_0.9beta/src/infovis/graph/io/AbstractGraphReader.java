/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.ShapeColumn;
import infovis.table.io.AbstractTableReader;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;

/**
 * Base class for Graph readers, except for formats based on XML.
 * 
 * Graph readers hold a graph and can also maintain
 * the shapes of the nodes and the shapes of the links.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public abstract class AbstractGraphReader extends AbstractTableReader {
    protected Graph graph;
    protected ShapeColumn nodeShapes;
    protected ShapeColumn linkShapes;    
 
     /**
      * Creates an AbstractGraphReader from a BufferedReader, a name and a Graph.
      * 
      * @param in the BufferedReader
      * @param name the file/input name
      * @param graph the Graph
      */   
    public AbstractGraphReader(
        InputStream in,
        String name,
        Graph graph) {
        super(in, name, graph.getEdgeTable());
        this.graph = graph;
    }

    /**
     * Returns the Graph
     * @return the Graph
     */    
    public Graph getGraph() {
        return graph;
    }
    
    
    public void reset() {
        nodeShapes = null; // don't clear
        linkShapes = null; // idem
    }
    /**
     * Returns the rectangle containing the layed-out graph
     * @return the rectangle containing the layed-out graph
     *  or null if it is not computed.
     */
    public abstract Rectangle2D.Float getBbox();
    
    public ShapeColumn findNodeShapes() {
        if (nodeShapes == null) {
            nodeShapes = new ShapeColumn("#nodeShapes");
        }
        return nodeShapes;
    }
    
    public ShapeColumn getNodeShapes() {
        return nodeShapes;
    }
    
    public Shape getNodeShape(int node) {
        return findNodeShapes().get(node); 
    }
    
    public void setNodeShape(int node, Shape s) {
        findNodeShapes().setExtend(node, s);
    }
    
     public Shape findNodeShape(int node) {
         return findNodeShapes().findRect(node);
    }
    
    public ShapeColumn findLinkShapes() {
        if (linkShapes == null) {
            linkShapes = new ShapeColumn("#linkShapes");
        }
        return linkShapes;
    }
    
    public ShapeColumn getLinkShapes() {
        return linkShapes;
    }
    
    public Shape getLinkShape(int edge) {
        return findLinkShapes().get(edge);
    }
    
    public void setLinkShape(int edge, Shape s) {
        findLinkShapes().setExtend(edge, s);
    }
    
    public Shape findLinkShape(int edge) {
        if (findLinkShapes().isValueUndefined(edge)) {
            GeneralPath p = new GeneralPath();
            setLinkShape(edge, p);
            return p;
        }
        return getLinkShape(edge);
    }
}
