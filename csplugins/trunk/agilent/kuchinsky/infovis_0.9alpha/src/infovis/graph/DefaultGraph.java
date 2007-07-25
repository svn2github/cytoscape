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

import infovis.*;
import infovis.column.IntColumn;
import infovis.table.DefaultDynamicTable;
import infovis.utils.*;
import infovis.utils.RowIterator;
import infovis.utils.TableIterator;

import javax.swing.event.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.27 $
 */
public class DefaultGraph implements Graph, TableModelListener {
    /** Name of the column containing the index of the first out edge. */
    public static final String FIRSTEDGE_COLUMN = "#FirstEdge";

    /** Name of the column containing the index of the last out edge. */
    public static final String LASTEDGE_COLUMN = "#LastEdge";

    /** Name of the column containing the "in" vertex in the edge table. */
    public static final String INVERTEX_COLUMN = "#InVertex";

    /** Name of the column containing the "out" vertex in the edge table. */
    public static final String OUTVERTEX_COLUMN = "#OutVertex";

    /** Name of the column containing the next edge in the edge table. */
    public static final String NEXTEDGE_COLUMN = "#NextEdge";

    /** Name of the column containing the previous edge in the edge table. */
    public static final String PREVEDGE_COLUMN = "#PrevEdge";

    //    /** The Metadata key of the vertex table in the underlying table. */
    //    public static final String VERTEX_TABLE_METADATA =
    // "VERTEX_TABLE_METADATA";
    /** The Metadata key of the Graph using the Table */
    public static final String GRAPH_METADATA = "GRAPH_METADATA";

    /** Name of the column containing the index of the first int edge. */
    public static final String OUT_FIRSTEDGE_COLUMN = "#outFirstEdge";

    /** Name of the column containing the index of the last out edge. */
    public static final String OUT_LASTEDGE_COLUMN = "#outLastEdge";

    /** Name of the column containing the next out edge in the edge table. */
    public static final String OUT_NEXTEDGE_COLUMN = "#outNextEdge";

    /** Name of the column containing the previous out edge in the edge table. */
    public static final String OUT_PREVEDGE_COLUMN = "#outPrevEdge";

    /** The Edge table */
    protected DynamicTable edgeTable;

    protected Map metadata;

    protected Map clientPropery;

    protected String name;

    /** The vertex table */
    protected DynamicTable vertexTable;

    /** The first edge of each "in" vertex stored in the vertexTable. */
    protected IntColumn vertexFirstEdge;

    /** The last edge of each "in" vertex stored in the vertexTable */
    protected IntColumn vertexLastEdge;

    /** The first edge of each "out" vertex stored in the vertexTable. */
    protected IntColumn outVertexFirstEdge;

    /** The last edge of each "out" vertex stored in the vertexTable */
    protected IntColumn outVertexLastEdge;

    /** The in vertex of each edge stored in the edgeTable */
    protected IntColumn edgeInVertex;

    /** The out vertex of each edge stored in the edgeTable */
    protected IntColumn edgeOutVertex;

    /** The next edge in the list of the "in" vertex, stored in the edgeTable */
    protected IntColumn nextEdge;

    /** The prev edge in the list of the "in" vertex, stored in the edgeTable */
    protected IntColumn prevEdge;

    /** The next edge in the list of the "out" vertex, stored in the edgeTable */
    protected IntColumn outNextEdge;

    /** The prev edge in the list of the "out" vertex, stored in the edgeTable */
    protected IntColumn outPrevEdge;

    protected EventListenerList listeners;

    protected boolean directed = true;

    protected DefaultGraph(DynamicTable edgeTable,
            DynamicTable vertexTable) {
        if (edgeTable == null) {
            edgeTable = new DefaultDynamicTable();
        }
        this.edgeTable = edgeTable;
        initializeEdgeTable(edgeTable);

        if (vertexTable == null) {
            vertexTable = new DefaultDynamicTable();
        }
        this.vertexTable = vertexTable;
        initializeVertexTable(vertexTable);

        setDirected(true);
    }

    /**
     * Creates a copy of the topology of the specified graph. The indexes are
     * identical so attribute columns can be shared among two copied graphs.
     * 
     * @param other
     *            the specified source graph.
     */
    public DefaultGraph(DefaultGraph other) {
        vertexTable = new DefaultDynamicTable();
        initializeVertexTable(vertexTable);
        vertexFirstEdge.copyFrom(other.vertexFirstEdge);
        vertexLastEdge.copyFrom(other.vertexLastEdge);
        outVertexFirstEdge.copyFrom(other.outVertexFirstEdge);
        outVertexLastEdge.copyFrom(other.outVertexLastEdge);

        edgeTable = new DefaultDynamicTable();
        initializeEdgeTable(edgeTable);
        //getMetadata().put(VERTEX_TABLE_METADATA, vertexTable);
        edgeInVertex.copyFrom(other.edgeInVertex);
        edgeOutVertex.copyFrom(other.edgeOutVertex);

        nextEdge.copyFrom(other.nextEdge);
        prevEdge.copyFrom(other.prevEdge);
        outNextEdge.copyFrom(other.outNextEdge);
        outPrevEdge.copyFrom(other.outPrevEdge);

        setDirected(other.isDirected());
    }

    /**
     * Constructor for Graph.
     */
    public DefaultGraph() {
        this(null, null);
    }

    public static Graph getGraph(Table table) {
        return (Graph) table.getMetadata().get(GRAPH_METADATA);
    }

    protected void initializeEdgeTable(DynamicTable edgeTable) {
        edgeInVertex = IntColumn.findColumn(edgeTable, INVERTEX_COLUMN);
        edgeOutVertex = IntColumn.findColumn(edgeTable,
                OUTVERTEX_COLUMN);

        nextEdge = IntColumn.findColumn(edgeTable, NEXTEDGE_COLUMN);
        prevEdge = IntColumn.findColumn(edgeTable, PREVEDGE_COLUMN);
        outNextEdge = IntColumn.findColumn(edgeTable,
                OUT_NEXTEDGE_COLUMN);
        outPrevEdge = IntColumn.findColumn(edgeTable,
                OUT_PREVEDGE_COLUMN);
        edgeTable.getMetadata().put(GRAPH_METADATA, this);
        edgeTable.addTableModelListener(this);
    }

    protected void initializeVertexTable(DynamicTable vertexTable) {
        vertexFirstEdge = IntColumn.findColumn(vertexTable,
                FIRSTEDGE_COLUMN);
        vertexLastEdge = IntColumn.findColumn(vertexTable,
                LASTEDGE_COLUMN);
        outVertexFirstEdge = IntColumn.findColumn(vertexTable,
                OUT_FIRSTEDGE_COLUMN);
        outVertexLastEdge = IntColumn.findColumn(vertexTable,
                OUT_LASTEDGE_COLUMN);
        vertexTable.getMetadata().put(GRAPH_METADATA, this);
        vertexTable.addTableModelListener(this);
    }

    // interface Metadata
    /**
     * Returns the clientPropery.
     * 
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
     * 
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
        getEdgeTable().setName(name);
        getEdgeTable().setName(name);
    }

    public void clear() {
        getEdgeTable().clear();
        vertexTable.clear();
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    /**
     * Returns the number of vertices in the graph
     * 
     * @return The number of vertices in the graph.
     */
    public int getVerticesCount() {
        return vertexTable.getRowCount();
    }

    /**
     * Adds a new vertex to the graph.
     * 
     * @return the vertex number.
     */
    public int addVertex() {
        int v = vertexTable.addRow(); // will trigger a notification
        return v;
    }

    protected void vertexInserted(int v) {
        try {
            disableNotify();
            vertexFirstEdge.setExtend(v, Graph.NIL);
            vertexLastEdge.setExtend(v, Graph.NIL);
            outVertexFirstEdge.setExtend(v, Graph.NIL);
            outVertexLastEdge.setExtend(v, Graph.NIL);
        } finally {
            enableNotify();
        }
        fireGraphChangedListeners(v,
                GraphChangedEvent.GRAPH_VERTEX_ADDED);
    }

    protected void verticesInserted(int v0, int v1) {
        while (v0 <= v1) {
            vertexInserted(v0);
            v0++;
        }
    }

    public void removeVertex(int vertex) {
        checkVertex(vertex);
        vertexTable.removeRow(vertex); // triggers notification
    }

    protected void vertexRemoved(int vertex) {
        assert (!vertexTable.isRowValid(vertex));
        try {
            disableNotify(); // prevent notification before the graph is
                             // coherent
            for (int e = getFirstEdge(vertex); e != NIL; e = getFirstEdge(vertex)) {
                removeEdge(e);
            }
            for (int e = getFirstOutEdge(vertex); e != NIL; e = getFirstOutEdge(vertex)) {
                removeEdge(e);
            }
            vertexFirstEdge.setValueUndefined(vertex, true);
            vertexLastEdge.setValueUndefined(vertex, true);
            outVertexFirstEdge.setValueUndefined(vertex, true);
            outVertexLastEdge.setValueUndefined(vertex, true);
        } finally {
            enableNotify();
        }
        fireGraphChangedListeners(vertex,
                GraphChangedEvent.GRAPH_VERTEX_REMOVED);
    }

    protected void verticesRemoved(int v0, int v1) {
        while (v0 <= v1) {
            vertexRemoved(v0);
            v0++;
        }
    }

    /**
     * Returns the number of edges in the graph.
     * 
     * @return the number of edges in the graph.
     */
    public int getEdgesCount() {
        return getEdgeTable().getRowCount();
    }

    /**
     * Adds a new edge between two vertices.
     * 
     * @param v1
     *            the first vertex.
     * @param v2
     *            the second vertex.
     * 
     * @return the new edge index.
     */
    public int addEdge(int v1, int v2) {
        checkVertex(v1);
        checkVertex(v2);
        int edge = getEdgeTable().addRow();
        try {
            disableNotify(); // prevents notification before the graph is
                             // coherent
            setInVertex(edge, v1);
            setOutVertex(edge, v2);

            addEdgeIn(edge, v1, vertexFirstEdge, vertexLastEdge,
                    nextEdge, prevEdge);
            addEdgeIn(edge, v2, outVertexFirstEdge, outVertexLastEdge,
                    outNextEdge, outPrevEdge);

        } finally {
            enableNotify();
        }
        fireGraphChangedListeners(edge,
                GraphChangedEvent.GRAPH_EDGE_ADDED);
        return edge;
    }

    protected void addEdgeIn(int edge, int v, IntColumn first,
            IntColumn last, IntColumn next, IntColumn prev) {
        assert (!first.isValueUndefined(v));
        assert (!last.isValueUndefined(v));
        int p = last.get(v);
        last.setExtend(v, edge);
        if (p == NIL) {
            first.set(v, edge);
        } else {
            next.setExtend(p, edge);
        }
        next.setExtend(edge, NIL);
        prev.setExtend(edge, p);
    }

    public void removeEdge(int edge) {
        getEdgeTable().removeRow(edge); // triggers notification
    }

    protected void edgeRemoved(int edge) {
        quickRemoveEdge(edge);
    }

    protected void edgesRemoved(int edge0, int edge1) {
        try {
            disableNotify();
            while (edge0 <= edge1) {
                edgeRemoved(edge0);
                edge0++;
            }
        } finally {
            enableNotify();
        }
    }

    protected void quickRemoveEdge(int edge) {
        int v1 = getInVertex(edge);
        int v2 = getOutVertex(edge);
        removeEdgeIn(edge, v1, vertexFirstEdge, vertexLastEdge,
                nextEdge, prevEdge);
        removeEdgeIn(edge, v2, outVertexFirstEdge, outVertexLastEdge,
                outNextEdge, outPrevEdge);
        setInVertex(edge, NIL);
        setOutVertex(edge, NIL);
        fireGraphChangedListeners(edge,
                GraphChangedEvent.GRAPH_EDGE_REMOVED);
    }

    protected void removeEdgeIn(int edge, int v, IntColumn first,
            IntColumn last, IntColumn next, IntColumn prev) {
        int n = next.get(edge);
        int p = prev.get(edge);
        next.set(edge, NIL);
        next.setValueUndefined(edge, true);
        prev.set(edge, NIL);
        prev.setValueUndefined(edge, true);
        if (n == NIL) { // last edge
            last.set(v, p);
        } else {
            prev.set(n, p);
        }
        if (p == NIL) { // first edge
            first.set(v, n);
        } else {
            next.set(p, n);
        }
    }

    /**
     * Returns the "in" vertex of an edge.
     * 
     * @param edge
     *            the edge.
     * 
     * @return the "in" vertex of an edge or NIL.
     */
    public int getInVertex(int edge) {
        return edgeInVertex.get(edge);
    }

    protected void setInVertex(int edge, int v) {
        edgeInVertex.setExtend(edge, v);
    }

    /**
     * Returns the "out" vertex of an edge.
     * 
     * @param edge
     *            the edge.
     * 
     * @return the "out" vertex of an edge.
     */
    public int getOutVertex(int edge) {
        return edgeOutVertex.get(edge);
    }

    protected void setOutVertex(int edge, int v) {
        edgeOutVertex.setExtend(edge, v);
    }

    public int getOutEdgeAt(int vertex, int index) {
        int e;

        for (e = getFirstEdge(vertex); e != NIL && index != 0; e = getNextEdge(e))
            ;

        return e;
    }

    public int getInEdgeAt(int vertex, int index) {
        int e;

        for (e = outVertexFirstEdge.get(vertex); e != NIL && index != 0; e = outNextEdge
                .get(e))
            ;

        return e;
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

    /**
     * Returns the first edge of a specified vertex.
     * 
     * @param vertex
     *            the vertex,
     * 
     * @return the first edge of the specified vertex or NIL if none exists.
     */
    protected int getFirstEdge(int vertex) {
        return vertexFirstEdge.get(vertex);
    }

    /**
     * Returns the last edge of a specified vertex.
     * 
     * @param vertex
     *            the vertex,
     * 
     * @return the last edge of the specified vertex or NIL if none exists.
     */
    protected int getLastEdge(int vertex) {
        return vertexLastEdge.get(vertex);
    }

    /**
     * Returns the edge following a specified edge starting at a vertex.
     * 
     * @param edge
     *            the edge.
     * 
     * @return the edge following a given edge starting at the vertex or NIL if
     *         the specified edge is the last of the "in" vertex.
     */
    protected int getNextEdge(int edge) {
        return nextEdge.get(edge);
    }

    /**
     * Returns an edge between two specified vertices.
     * 
     * @param v1
     *            the first vertex.
     * @param v2
     *            the second vertex.
     * 
     * @return an edge between two specified vertices or NIL if none exists.
     */
    public int getEdge(int v1, int v2) {
        if (!(vertexTable.isRowValid(v1) && vertexTable.isRowValid(v2))) {
            return Graph.NIL;
        }
        for (int e = getFirstEdge(v1); e != Graph.NIL; e = getNextEdge(e)) {
            if (getOutVertex(e) == v2) {
                return e;
            }
        }
        if (!isDirected()) {
            for (int e = getFirstEdge(v2); e != Graph.NIL; e = getNextEdge(e)) {
                if (getOutVertex(e) == v1) {
                    return e;
                }
            }
        }
        return NIL;
    }

    /**
     * Returns an edge between two specified vertices.
     * 
     * @param v1
     *            the first vertex.
     * @param v2
     *            the second vertex.
     * 
     * @return an edge between two specified vertices creating one if none
     *         exists.
     */
    public int findEdge(int v1, int v2) {
        if (!(vertexTable.isRowValid(v1) && vertexTable.isRowValid(v2))) {
            return Graph.NIL;
        }
        int e = getEdge(v1, v2);
        if (e == NIL)
            return addEdge(v1, v2);
        return e;
    }

    protected int getFirstOutEdge(int vertex) {
        return outVertexFirstEdge.get(vertex);
    }

    protected int getLastOutEdge(int vertex) {
        return outVertexLastEdge.get(vertex);
    }

    protected int getNextOutEdge(int edge) {
        return outNextEdge.get(edge);
    }

    /**
     * Returns the out degree of the vertex, which is simply the number of edges
     * starting from the vertex.
     * 
     * @param vertex
     *            the vertex.
     * @return The out degree of the vertex.
     */
    public int getOutDegree(int vertex) {
        int cnt = 0;
        for (int edge = vertexFirstEdge.get(vertex); edge != -1; edge = nextEdge
                .get(edge)) {
            cnt++;
        }
        return cnt;
    }

    public int getInDegree(int vertex) {
        int cnt = 0;
        for (int edge = outVertexFirstEdge.get(vertex); edge != -1; edge = outNextEdge
                .get(edge)) {
            cnt++;
        }
        return cnt;
    }

    public int getDegree(int vertex) {
        int in = getInDegree(vertex);
        int out = getOutDegree(vertex);
        return in + out;
    }

    public RowIterator edgeIterator(int vertex) {
        return new AppendRowIterator(outEdgeIterator(vertex),
                inEdgeIterator(vertex));
    }

    /**
     * Returns an iterator over the edges of a specified vertex.
     * 
     * @param vertex
     *            the vertex.
     * 
     * @return the iterator over the edges of the vertex.
     */
    public RowIterator outEdgeIterator(int vertex) {
        return new TableIterator(vertexFirstEdge.get(vertex),
                getEdgeTable().getRowCount(), true) {
            public int nextRow() {
                int ret = row;
                row = nextEdge.get(row);
                return ret;
            }

            /**
             * @see infovis.Table.Iterator#hasNext()
             */
            public boolean hasNext() {
                return row != NIL;
            }
        };
    }

    public RowIterator inEdgeIterator(int vertex) {
        return new TableIterator(outVertexFirstEdge.get(vertex),
                getEdgeTable().getRowCount(), true) {
            public int nextRow() {
                int ret = row;
                row = outNextEdge.get(row);
                return ret;
            }

            /**
             * @see infovis.Table.Iterator#hasNext()
             */
            public boolean hasNext() {
                return row != NIL;
            }
        };
    }

    public RowIterator vertexIterator() {
        return vertexTable.iterator();
    }

    public RowIterator edgeIterator() {
        return getEdgeTable().iterator();
    }

    /**
     * Returns the edgeTable.
     * 
     * @return DefaultTable
     */
    public DynamicTable getEdgeTable() {
        return edgeTable;
    }

    public DynamicTable getVertexTable() {
        return vertexTable;
    }

    public void addGraphChangedListener(GraphChangedListener l) {
        getListeners().add(GraphChangedListener.class, l);
    }

    public void removeGraphChangedListener(GraphChangedListener l) {
        getListeners().remove(GraphChangedListener.class, l);
    }

    protected void fireGraphChangedListeners(GraphChangedEvent e) {
        if (listeners == null)
            return;
        Object[] ll = listeners
                .getListeners(GraphChangedListener.class);
        for (int i = 0; i < ll.length; i++) {
            GraphChangedListener l = (GraphChangedListener) ll[i];
            l.graphChanged(e);
        }
    }

    protected void fireGraphChangedListeners(int detail, short type) {
        if (listeners == null)
            return;
        fireGraphChangedListeners(new GraphChangedEvent(this, detail,
                type));
    }

    protected EventListenerList getListeners() {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        return listeners;
    }

    protected void checkVertex(int v) {
        if (!vertexTable.isRowValid(v)) {
            throw new GraphException("invalid vertex", v, NIL);
        }
    }

    protected void checkEdge(int e) {
        if (!getEdgeTable().isRowValid(e)) {
            throw new GraphException("invalid edge " + e, NIL, e);
        }
    }

    protected void disableNotify() {
        vertexTable.disableNotify();
        edgeTable.disableNotify();
    }

    protected void enableNotify() {
        edgeTable.enableNotify();
        vertexTable.enableNotify();
    }

    public void tableChanged(TableModelEvent e) {
        if (e.getSource() == getVertexTable()) {
            switch (e.getType()) {
            case TableModelEvent.INSERT:
                verticesInserted(e.getFirstRow(), e.getLastRow());
                break;
            case TableModelEvent.DELETE:
                verticesRemoved(e.getFirstRow(), e.getLastRow());
                break;
            }
        } else if (e.getSource() == getEdgeTable()) {
            switch (e.getType()) {
            case TableModelEvent.DELETE:
                edgesRemoved(e.getFirstRow(), e.getLastRow());
                break;
            }
        }
    }

}