/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.graph.algorithm;

import infovis.Graph;
import infovis.utils.RowIterator;
import cern.colt.map.OpenIntIntHashMap;

/**
 * Depth first search algorithm for Graphs.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DepthFirst extends Algorithm {
    /**
     * Constructor for DepthFirst.
     */
    public DepthFirst(Graph graph) {
        super(graph);
    }

    public interface Visitor {
        /**
         * invoked on every vertex of the graph before
         * the start of the graph search.
         * @param vertex the Vertex
         */
        public void initializeVertex(int vertex);
        /**
         * invoked on the source vertex once before 
         * the start of the search.
         * @param vertex the Vertex
         */
        public void startVertex(int vertex);
        /**
         * Invoked when a vertex is encountered for the first time.
         * @param vertex the Vertex
         */
        public void discoverVertex(int vertex);
        /**
         * Invoked on every out-edge of each vertex after
         * it is discovered.
         * @param edge the Edge
         */
        public void examineEdge(int edge);
        /**
         * Invoked on each edge as it becomes a member of the
         * edges that form the search tree. If you wish to record
         * predecessors, do so at this event point.
         * @param edge the Edge
         */
        public void treeEdge(int edge);
        /**
         * Invoked on the back edges in the graph.
         * @param edge the Edge
         */
        public void backEdge(int edge);
        /**
         * Invoked on forward or cross edges in the graph.
         * In an undirected graph this method is never called.
         * @param edge the Edge
         */
        public void forwardOrCrossEdge(int edge);
        /**
         * Invoked on a vertex after all of its out edges have
         * been added to the search tree and all of the adjacent
         * vertices have been discovered (but before their out-edges
         * have been examined).
         * @param vertex the Vertex
         */
        public void finishVertex(int vertex);
    }

    public void visit(Visitor visitor, int start) {
        visit(visitor, start, new OpenIntIntHashMap());
    }

    public void visit(Visitor vis, int start, OpenIntIntHashMap color) {
        for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
            int v = iter.nextRow();
            color.put(v, WHITE);
            vis.initializeVertex(v);
        }
        
        RowIterator iter = graph.vertexIterator();
        if (start != iter.peekRow()) {
            vis.startVertex(start);
            visitDfs(vis, start, color);
        }
        while (iter.hasNext()) {
            int v = iter.nextRow();
            if (color.get(v) == WHITE) {
                vis.startVertex(v);
                visitDfs(vis, v, color);
            }
        }
    }

    void visitDfs(Visitor visitor, int vertex, OpenIntIntHashMap color) {
        color.put(vertex, GREY);
        visitor.discoverVertex(vertex);
        for (RowIterator iter = graph.isDirected() 
                ? graph.outEdgeIterator(vertex) : graph.edgeIterator(vertex);
                iter.hasNext();) {
            int edge = iter.nextRow();
            visitor.examineEdge(edge);
            int v2 = graph.getOtherVertex(edge, vertex);
            
            int c = color.get(v2);
            if (c == WHITE) {
                visitor.treeEdge(edge);
                visitDfs(visitor, v2, color);
            } else if (c == GREY) {
                visitor.backEdge(edge);
            } else { // color == BLACK
                visitor.forwardOrCrossEdge(edge);
            }
        }
        color.put(vertex, BLACK);
        visitor.finishVertex(vertex);
    }
}