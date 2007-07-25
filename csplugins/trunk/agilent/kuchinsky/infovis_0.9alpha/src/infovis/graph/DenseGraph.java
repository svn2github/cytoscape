/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

import infovis.DynamicTable;
import infovis.Graph;
import infovis.table.DefaultDynamicTable;
import infovis.utils.*;
import infovis.utils.RowIterator;
import infovis.utils.TableIterator;

/**
 * Class DenseGraph
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DenseGraph implements Graph {
    protected Map       metadata;
    protected Map       clientPropery;
    protected String    name;

    protected DynamicTable edgeTable;
    protected DynamicTable vertexTable;
    protected EventListenerList listeners;
    protected boolean directed;
    protected int size;

    public DenseGraph(int size) {
        vertexTable = new DefaultDynamicTable(size);
        edgeTable = new DefaultDynamicTable(size*size);
        this.size = size;
    }
    // interface Metadata
    /**
     * Returns the clientPropery.
     * @return Map the clientPropery map.
     */
    public Map getClientPropery() {
        if (clientPropery == null) {
            clientPropery = new HashMap();
        }
        return clientPropery;
    }

    /**
     * Returns the metadata.
     * @return Map the metadata map.
     */
    public Map getMetadata() {
        if (metadata == null) {
            metadata = new HashMap();
        }
        return metadata;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name; 
    }

    public void clear() {
        //getEdgeTable().clear();
        //vertexTable.clear();
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getVerticesCount() {
        return size;
    }

    public int addVertex() {
        throw new UnsupportedOperationException("Dense Graph cannot grow");
    }

    public void removeVertex(int vertex) {
        throw new UnsupportedOperationException("Dense Graph cannot shrink");
    }

    public int getEdgesCount() {
        return size*size;
    }

    public int addEdge(int v1, int v2) {
        return v1*size + v2;
    }

    public void removeEdge(int edge) {
        throw new UnsupportedOperationException("Dense Graph cannot shrink");
    }

    public int getInVertex(int edge) {
        return edge / size;
    }
    
    public int getOtherVertex(int edge, int vertex) {
        int in = getInVertex(edge);
        int out = getOutVertex(edge);
        if (getInVertex(edge)==vertex) {
            return out;
        }
        if (out == vertex) {
            return in;
        }
        return NIL;
    }

    public int getOutVertex(int edge) {
        return edge % size;
    }
    
    public int getOutEdgeAt(int vertex, int index) {
        return vertex * size + index;
    }

    public int getInEdgeAt(int vertex, int index) {
        return index * size + vertex;
    }

    public int getEdge(int v1, int v2) {
        return v1*size + v2;
    }

    public int findEdge(int v1, int v2) {
        return getEdge(v1, v2);
    }

    public int getOutDegree(int vertex) {
        return size;
    }

    public int getDegree(int vertex) {
        return getInDegree(vertex) + getOutDegree(vertex);
    }


    public RowIterator outEdgeIterator(int vertex) {
        return new TableIterator(vertex*size, (vertex+1)*size);
    }

    public int getInDegree(int vertex) {
        return size;
    }

    public RowIterator inEdgeIterator(int vertex) {
        return new TableIterator(vertex, size*(size-1)+vertex+1) {
            public int nextRow() {
                int ret = row;
                if (up){
                    row += size;
                } else {
                    row += size;
                }
                return ret;
            }
        };
    }
    
    public RowIterator edgeIterator(int vertex) {
        return new AppendRowIterator(
                outEdgeIterator(vertex),
                inEdgeIterator(vertex));
    }

    public RowIterator vertexIterator() {
        return vertexTable.iterator();
    }

    public RowIterator edgeIterator() {
        return edgeTable.iterator();
    }

    public DynamicTable getEdgeTable() {
        return edgeTable;
    }

    public DynamicTable getVertexTable() {
        return vertexTable;
    }

    public void addGraphChangedListener(GraphChangedListener l) {
    }

    public void removeGraphChangedListener(GraphChangedListener l) {
    }
}
