/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.graph.algorithm;

import infovis.Graph;
import infovis.utils.IntLinkedList;
import infovis.utils.RowIterator;
import cern.colt.map.OpenIntIntHashMap;

/**
 * Breadth first search algorithm for Graphs.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class BreadthFirst extends Algorithm {
    /**
     * Constructor for BreadthFirst.
     */
    public BreadthFirst(Graph graph) {
        super(graph);
    }

    public interface Visitor {
        /**
         * Invoked on every vertex before the start of the search. 
         * @param vertex the Vertex
         */
        public void initializeVertex(int vertex);
        /**
         * Invoked on every vertex before the start of the search.
         * 
         * @param vertex
         *            the Vertex
         */
        public void startVertex(int vertex);

        /**
         * Invoked the first time the algorithm encounters vertex <i>u </i>. All
         * vertices closer to the source vertex have been discovered, and
         * vertices further from the source have not yet been discovered.
         * 
         * @param vertex
         *            the Vertex
         */
        public void discoverVertex(int vertex);

        /**
         * Invoked in each vertex as it is removed from the queue
         * 
         * @param vertex
         *            the Vertex
         */
        public void examineVertex(int vertex);

        /**
         * Invoked on every out-edge of each vertex immediately after the vertex
         * is removed from the queue.
         * 
         * @param edge
         *            the Edge
         */
        public void examineEdge(int edge);

        /**
         * Invoked (in addition to <tt>examine_edge()</tt>) if the edge is a
         * tree edge. The target vertex of edge <tt>e</tt> is discovered at
         * this time.
         * 
         * @param edge
         *            the Edge
         */
        public void treeEdge(int edge);

        /**
         * Invoked (in addition to <tt>examine_edge()</tt>) if the edge is
         * not a tree edge.
         * 
         * @param edge
         *            the Edge
         */
        public void nonTreeEdge(int edge);

        /**
         * Invoked (in addition to <tt>non_tree_edge()</tt>) if the target
         * vertex is colored grey at the time of examination. The color grey
         * indicates that the vertex is currently in the queue.
         * 
         * @param edge
         *            the Edge
         */
        public void greyTarget(int edge);

        /**
         * Invoked (in addition to <tt>non_tree_edge()</tt>) if the target
         * vertex is colored black at the time of examination. The color black
         * indicates that the vertex is no longer in the queue.
         * 
         * @param edge
         *            the Edge
         */
        public void blackTarget(int edge);

        /**
         * Invoked after all of the out edges of <i>u </i> have been examined
         * and all of the adjacent vertices have been discovered.
         * 
         * @param vertex
         *            the Vertex
         */
        public void finishVertex(int vertex);
    }

    protected void visit(Visitor vis, int start, OpenIntIntHashMap color) {
        IntLinkedList Q = new IntLinkedList();

        color.put(start, GREY);
        Q.addLast(start);
        while (!Q.isEmpty()) {
            int u = Q.getFirst();
            Q.removeFirst();
            vis.examineVertex(u);
            for (RowIterator iter = graph.isDirected() 
                    ? graph.outEdgeIterator(u) : graph.edgeIterator(u);
                iter.hasNext();) {
                int e = iter.nextRow();
                int v = graph.getOtherVertex(e, u);
                vis.examineEdge(e);
                int v_color = color.get(v);
                if (v_color == WHITE) {
                    vis.treeEdge(e);
                    color.put(v, GREY);
                    vis.discoverVertex(v);
                    Q.addLast(v);
                } else {
                    vis.nonTreeEdge(e);
                    if (v_color == GREY)
                        vis.greyTarget(e);
                    else
                        vis.blackTarget(e);
                }
            } // end for
            color.put(u, BLACK);
            vis.finishVertex(u);
        } // end while
    }
    
    /**
     * Performs a breadth-first traversal of a graph.   
     * @param vis the Visitor
     * @param start the starting vertex
     */
    
    public void visit(Visitor vis, int start) {
        OpenIntIntHashMap color = new OpenIntIntHashMap();
        for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
            int v = iter.nextRow();
            color.put(v, WHITE);
            vis.initializeVertex(v);
        }
        visit(vis, start, color);
    }
}