/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import infovis.DynamicTable;
import infovis.Graph;
import infovis.graph.event.GraphChangedListener;
import infovis.utils.RowIterator;

import javax.swing.text.MutableAttributeSet;

/**
 * Class GraphProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class GraphProxy implements Graph {
    protected Graph graph;
    
    public GraphProxy(Graph graph) {
        setGraph(graph);
    }
    
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    
    public Graph getGraph() {
        return graph;
    }
    
    public String getName() {
        return graph.getName();
    }
    public void setName(String name) {
        graph.setName(name);
    }
    public void clear() {
        graph.clear();
    }
    public MutableAttributeSet getClientProperty() {
        return graph.getClientProperty();
    }
    
    public MutableAttributeSet getMetadata() {
        return graph.getMetadata();
    }

    public int addEdge(int v1, int v2) {
        return graph.addEdge(v1, v2);
    }

    public void addGraphChangedListener(GraphChangedListener l) {
        graph.addGraphChangedListener(l);
    }
    public int addVertex() {
        return graph.addVertex();
    }

    public RowIterator edgeIterator() {
        return graph.edgeIterator();
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

    public int getOutEdgeAt(int vertex, int index) {
        return graph.getOutEdgeAt(vertex, index);
    }

    public int getEdgesCount() {
        return graph.getEdgesCount();
    }

    public DynamicTable getEdgeTable() {
        return graph.getEdgeTable();
    }

    public int getInDegree(int vertex) {
        return graph.getInDegree(vertex);
    }

    public int getInEdgeAt(int vertex, int index) {
        return graph.getInEdgeAt(vertex, index);
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

    public DynamicTable getVertexTable() {
        return graph.getVertexTable();
    }

    public int getVerticesCount() {
        return graph.getVerticesCount();
    }

    public RowIterator inEdgeIterator(int vertex) {
        return graph.inEdgeIterator(vertex);
    }

    public boolean isDirected() {
        return graph.isDirected();
    }

    public void removeEdge(int edge) {
        graph.removeEdge(edge);
    }

    public void removeGraphChangedListener(GraphChangedListener l) {
        graph.removeGraphChangedListener(l);
    }

    public void removeVertex(int vertex) {
        graph.removeVertex(vertex);
    }

    public void setDirected(boolean directed) {
        graph.setDirected(directed);
    }

    public RowIterator vertexIterator() {
        return graph.vertexIterator();
    }

}
