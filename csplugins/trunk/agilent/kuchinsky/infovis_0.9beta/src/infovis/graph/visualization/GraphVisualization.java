/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.*;
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.utils.RowIterator;
import infovis.visualization.DefaultVisualization;

/**
 * Abstract base class for Graph Visualizations.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.32 $
 */
public abstract class GraphVisualization
    extends DefaultVisualization
    implements Graph, GraphChangedListener {
    protected Graph graph;

    /**
     * Creates a new GraphVisualization object.
     *
     * @param table the Table.to pass down.
     * @param graph the Graph
     */
    public GraphVisualization(Graph graph, Table table) {
        super(table);
        this.graph = graph;
        this.graph.addGraphChangedListener(this);
    }
    
    public GraphVisualization(Graph graph) {
        this(graph, graph.getEdgeTable());
    }

    /**
     * Returns the graph.
     * @return Graph
     */
    public Graph getGraph() {
        return graph;
    }

    public int addEdge(int v1, int v2) {
        return graph.addEdge(v1, v2);
    }

    public int addVertex() {
        return graph.addVertex();
    }
    
    public RowIterator edgeIterator(int vertex) {
        return graph.edgeIterator(vertex);
    }

    public RowIterator outEdgeIterator(int vertex) {
        return graph.outEdgeIterator(vertex);
    }

    public int findEdge(int v1, int v2) {
        return graph.findEdge(v1, v2);
    }

    public int getOutDegree(int vertex) {
        return graph.getOutDegree(vertex);
    }
    
    public int getDegree(int vertex) {
        return graph.getDegree(vertex);
    }

    public int getEdge(int v1, int v2) {
        return graph.getEdge(v1, v2);
    }

    public int getEdgesCount() {
        return graph.getEdgesCount();
    }

    public DynamicTable getEdgeTable() {
        return graph.getEdgeTable();
    }

    public int getFirstVertex(int edge) {
        return graph.getFirstVertex(edge);
    }

    public int getSecondVertex(int edge) {
        return graph.getSecondVertex(edge);
    }
    
    public int getOtherVertex(int edge, int vertex) {
        return graph.getOtherVertex(edge, vertex);
    }

    public int getVerticesCount() {
        return graph.getVerticesCount();
    }

    public boolean isDirected() {
        return graph.isDirected();
    }

    public void setDirected(boolean directed) {
        graph.setDirected(directed);
    }

    public DynamicTable getVertexTable() {
        return graph.getVertexTable();
    }

    public RowIterator vertexIterator() {
        return graph.vertexIterator();
    }

    public RowIterator edgeIterator() {
        return graph.edgeIterator();
    }

    public int getOutEdgeAt(int vertex, int index) {
        return graph.getOutEdgeAt(vertex, index);
    }

    public int getInEdgeAt(int vertex, int index) {
        return graph.getInEdgeAt(vertex, index);
    }

    public void removeEdge(int edge) {
        graph.removeEdge(edge);
    }

    public void removeGraphChangedListener(GraphChangedListener l) {
        graph.removeGraphChangedListener(l);
    }

    public void addGraphChangedListener(GraphChangedListener l) {
        graph.addGraphChangedListener(l);
    }

    public int getInDegree(int vertex) {
        return graph.getInDegree(vertex);
    }

    public RowIterator inEdgeIterator(int vertex) {
        return graph.inEdgeIterator(vertex);
    }

    public void removeVertex(int vertex) {
        graph.removeVertex(vertex);
    }
    
    public void graphChanged(GraphChangedEvent e) {
        invalidate();
    }

}
