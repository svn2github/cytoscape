/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis;

import infovis.graph.event.GraphChangedListener;
import infovis.utils.RowIterator;


/**
 * A Graph in an interface for
 * a table of vertices and a table of edges, along with a set of
 * methods to manage these tables as a graph.
 * 
 * <p>Graph manage vertices and edges.  Contrary to other graph
 * packages, InfoVis does not implement a Vertex and Edge class.  A
 * vertex is simply an index in the table of vertices and an edge is
 * an index in the table of edges.  Apart from that rather unusual
 * lack of object-orientedness, the provided interface is very
 * similar to other graph packages.  Simply, each time you think
 * of the pseudo type <code>Vertex</code> or <code>Edge</code>,
 * replace it by <code>int</code>.  For example:</p>
 * 
 * <pre>
 * int vertex1 = graph.addVertex();
 * int vertex2 = graph.addVertex();
 * int edge1 = graph.addEdge(vertex1, vertex2);
 * </pre> 
 * 
 * <p>Any number of attrbutes can be added to vertices and edges,
 * simply by adding columns to the vertex table and edge table:</p>
 * 
 * <pre>
 *  // Create a name for vertices
 * StringColumn name = new StringColumn("name");
 * graph.getVertexTable().addColumn(name);
 * 
 * // Create a length for edges
 * IntColumn length = new IntColumn("length");
 * graph.getEdgeTable().addColumn(length);
 *
 * // Create 10 vertices linked together
 * // and named vertex0, vertex1, ... 
 * int prev = graph.addVertex(); 
 * name.setExtend(v, "vertex0");
 * for (int i = 1; i < 10; i++) {
 *     int v = graph.addVertex();
 *     name.setExtend(v, "vertex"+i);
 *     // Edge with previous vertex
 *     int e = graph.addEdge(prev, v);
 *     // Associate it a length of i
 *     length.setExtend(e, i);
 *     prev = v;
 * }
 * </pre>
 * 
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.37 $
 */
public interface Graph extends Metadata {
    /** Metadata key for the graph type. */
    String GRAPH_TYPE = "GRAPH_TYPE";
    /** Metadata value for a directed graph. */
    String GRAPH_TYPE_DIRECTED = "directed";
    /** Metadata value for an undirected graph. */
    String GRAPH_TYPE_UNDIRECTED = "undirected";
    /**
     * NIL value for a vertex or edge.
     */
    int NIL = -1;

    /**
     * Returns the Graph name.
     * @return the Graph name.
     */
    String getName();

    /**
     * Sets the Graph name.
     * @param name The Graph name to set
     */
    void setName(String name);

    /**
     * Clears the Graph.
     * 
     * <p>After this method, the graph tables are almost in the same state as if 
     * it had been created afresh except it contains the same columns as before 
     * but they are all cleared.
     * 
     */
    void clear();
    
    /**
     * Returns true if the graph is directed.
     * 
     * @return true if the graph is directed.
     */
    boolean isDirected();
    
    /**
     * Sets the graph to directed or undirected.
     * 
     * @param directed boolean specifying whether the graph is directed or not.
     */
    void setDirected(boolean directed);

    /**
     * Returns the number of vertices in the graph.
     *
     * @return        The number of vertices in the graph.
     */
    int getVerticesCount();

    /**
     * Adds a new "in" vertex to the graph.
     *
     * @return the "in" vertex number.
     */
    int addVertex();

    /**
     * Removes the specified vertex from the graph.
     * 
     * @param vertex the vertex to remove
     */
    void removeVertex(int vertex);

    /**
     * Returns the number of edges in the graph.
     *
     * @return the number of edges in the graph.
     */
    int getEdgesCount();

    /**
     * Adds a new edge between two vertices.
     *
     * @param v1 the first vertex.
     * @param v2 the second vertex.
     *
     * @return the new edge index.
     */
    int addEdge(int v1, int v2);
    
    /**
     * Removes the specified edge.
     * 
     * @param edge the edge to remove
     */
    void removeEdge(int edge);

    /**
     * Returns the first vertex of the specified edge.  When
     * the graph is directed, this is the source vertex.
     *
     * @param edge the edge.
     *
     * @return the first vertex of an edge or NIL.
     */
    int getFirstVertex(int edge);
    
    /**
     * Given a specified edge and a vertex on one side of this edge,
     * returns the vertex on the other side.  If the specified vertex
     * is the first vertex, it returns the second and vice versa.
     * 
     * @param edge the edge
     * @param vertex the vertex
     * @return the other vertex or NIL if the vertex is not on any
     * side of the edge.
     */
    int getOtherVertex(int edge, int vertex);

    /**
     * Returns the second vertex of an edge.  When the graph
     * is directed, this is the destination/target vertex.
     *
     * @param edge the edge.
     *
     * @return the second vertex of the edge.
     */
    int getSecondVertex(int edge);

    /**
     * Returns the outgoing edge of a specified vertex at a specified index.
     *  
     * @param vertex the in vertex of the requested edge
     * @param index the index of the edge in the edge list of the vertex
     * @return the outgoing edge of a specified vertex at a specified index or NIL.
     */
    int getOutEdgeAt(int vertex, int index);
    
    /**
     * Returns the incoming edge of a specified vertrex at a specified index.
     *  
     * @param vertex the out vertex of the requested edge
     * @param index the index of the edge in the edge list of the vertex
     * @return the incoming edge of a specified vertex at a specified index or NIL.
     */
    int getInEdgeAt(int vertex, int index);

    /**
     * Returns an edge between two specified vertices.
     *
     * @param v1 the first vertex.
     * @param v2 the second vertex.
     *
     * @return an edge between two specified vertices
     * 	or NIL if none exists.
     */
    int getEdge(int v1, int v2);

    /**
     * Returns an edge between two specified vertices.
     *
     * @param v1 the first vertex.
     * @param v2 the second vertex.
     *
     * @return an edge between two specified vertices
     * 	creating one if none exists.
     */
    int findEdge(int v1, int v2);

    /**
     * Returns the out degree of the vertex, which is simply the number 
     * of outgoing edges of the vertex.
     *
     * @param vertex the vertex.
     * @return  The out degree of the vertex.
     */
    int getOutDegree(int vertex);
    
    /**
     * Returns an iterator over the outgoing edges of a specified vertex.
     *
     * @param vertex the vertex.
     *
     * @return the iterator over the outgoing edges of the vertex.
     */
    RowIterator outEdgeIterator(int vertex);

    /**
     * Returns the in degree of the vertex, which is simply the number of 
     * incoming edges at the vertex.
     *
     * @param vertex the vertex.
     * @return  The number of incoming edges at this vertex.
     */
    int getInDegree(int vertex);

    /**
     * Returns an iterator over the incoming edges of specified vertex.
     * 
     * @param vertex the vertex
     * @return an iterator over the incoming edges of the specified vertex
     */
    RowIterator inEdgeIterator(int vertex);

    /**
     * Returns the degree of the vertex, which is inDegree + outDegree.
     *
     * @param vertex the vertex.
     * @return  The degree of the vertex.
     */
    int getDegree(int vertex);
    
    /**
     * Returns an iterator over all the edges (incoming and outgoing)
     * of a specified vertex.
     *
     * @param vertex the vertex.
     *
     * @return the iterator over all the edges of the vertex.
     */
    RowIterator edgeIterator(int vertex);
    
    /**
     * Returns an iterator over the vertices of the graph.
     * 
     * @return an iterator over the vertices of the graph
     */
    RowIterator vertexIterator();
    
    /**
     * Returns an iterator over the edges of the graph.
     * 
     * @return an iterator over the edges of the graph
     */
    RowIterator edgeIterator();

    /**
     * Returns the edgeTable.
     * @return DefaultTable
     */
    DynamicTable getEdgeTable();
    
    /**
     * Returns the vertex Table.
     * @return the vertex Table 
     */
    DynamicTable getVertexTable();

    /**
     * Attaches a GraphChangedListener to the graph.
     *  
     * @param l the GraphChangedListener to notify when the graph structure changes.
     */
    void addGraphChangedListener(GraphChangedListener l);
    
    /**
     * Removes a GraphChangedListener from the graph.
     *  
     * @param l the GraphChangedListener to remove
     */
    void removeGraphChangedListener(GraphChangedListener l);
}
