/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.Table;
import infovis.column.*;
import infovis.utils.Heap;
import infovis.utils.RowIterator;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Class DijkstraShortestPath
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class DijkstraShortestPath extends Algorithm {
    protected OpenIntObjectHashMap vertexMaps;
    protected NumberColumn edgeWeights;
    
    public DijkstraShortestPath(Graph graph, NumberColumn edgeWeights, boolean cached) {
        super(graph);
        this.edgeWeights = edgeWeights;
        if (cached) {
            vertexMaps = new OpenIntObjectHashMap();
        }
    }
    
    public static DoubleMatrix2D allShortestPaths(
            Graph graph, 
            NumberColumn edgeWeights,
            DoubleMatrix2D matrix) {
        assert(!graph.isDirected());
        Table vertices = graph.getVertexTable();
        int size = vertices.getLastRow()+1;
        if (matrix == null
                || matrix.columns() < size 
                || matrix.rows() < size) {
            matrix = DoubleFactory2D.dense.make(size, size, Double.POSITIVE_INFINITY);
        }
        else {
            matrix.assign(Double.POSITIVE_INFINITY);
        }
        if (edgeWeights == null) {
            edgeWeights = ColumnOne.instance;
        }
                
        for (RowIterator viter = graph.vertexIterator(); viter.hasNext(); ) {
            int from = viter.nextRow();
            Predecessor[] S = new Predecessor[size];
            Predecessor[] queued = new Predecessor[size];
            Heap queue = new Heap();
            
            Predecessor p = new Predecessor(from, 0);
            queue.insert(p);
            queued[from] = p;
//            for (int j = 0; j < size; j++) {
//                double d = matrix.getQuick(j, from);
//                if (d!=Double.POSITIVE_INFINITY) {
//                    queued[j] = new Predecessor(j, d);
//                }
//            }
            while(! queue.isEmpty()) {
                p = (Predecessor)queue.pop();
                int v = p.vertex;
                S[v] = p;
                for (RowIterator iter = graph.edgeIterator(v); iter.hasNext(); ) {
                    int edge = iter.nextRow();
                    double d = p.weight+ edgeWeights.getDoubleAt(edge);
                    int v2 = graph.getOtherVertex(edge, v);
                    assert(v2 != Graph.NIL);
                    Predecessor D = queued[v2];
                    if (D == null) {
                        Predecessor p2 = new Predecessor(v2, d);
                        queue.insert(p2);
                        queued[v2] = p2;
                    }
                    else if (D.weight > d) {
                        D.weight = d;
                        D.pred = v;
                        queue.update(D);
                    }
                }
            }
            matrix.setQuick(from, from, 0);
            for (int j = 0; j < S.length; j++) {
                p = S[j];
                if (p != null) {
                    assert(p.vertex==j);
                    matrix.setQuick(from, j, p.weight);
                }
            }
        }
        return matrix;
    }
    
    public DijkstraShortestPath(Graph graph, boolean cached) {
        this(graph, null, cached);
    }
    
    public DijkstraShortestPath(Graph graph) {
        this(graph, true);
    }
    
    public boolean isCached() {
        return vertexMaps != null;
    }
    
    public void setCached(boolean cached) {
        if (cached) {
            if (vertexMaps == null) {
                vertexMaps = new OpenIntObjectHashMap();
            }
        }
        else {
            vertexMaps = null;
        }
    }
    
    public double getEdgeWeight(int edge) {
        if (edgeWeights != null && !edgeWeights.isValueUndefined(edge)) {
            double w = edgeWeights.getDoubleAt(edge);
            assert(w>=0);
            return w;
        }
        else {
            return 1;
        }
    }
    
    public Predecessor shortestPath(int from, int to) {
        OpenIntObjectHashMap map = allShortestPaths(from);
        if (map == null) return null;
        Predecessor p = (Predecessor)map.get(to);
        return p;
    }

    public OpenIntObjectHashMap allShortestPaths(int from) {
        if (vertexMaps != null && vertexMaps.containsKey(from)) {
            return (OpenIntObjectHashMap)vertexMaps.get(from);
        }
        OpenIntObjectHashMap S = new OpenIntObjectHashMap();
        Heap queue = new Heap();
        OpenIntObjectHashMap queued = new OpenIntObjectHashMap();
        Predecessor p = new Predecessor(from, 0);
        queue.insert(p);
        queued.put(from, p);
        
        while(! queue.isEmpty()) {
            p = (Predecessor)queue.pop();
            int v = p.vertex;
            S.put(v, p);
            for (RowIterator iter = graph.edgeIterator(v); iter.hasNext(); ) {
                int edge = iter.nextRow();
                double d = p.weight+ getEdgeWeight(edge);
                int v2 = graph.getOtherVertex(edge, v);
                assert(v2 != Graph.NIL);
                Predecessor D = (Predecessor)queued.get(v2);
                if (D == null) {
                    Predecessor p2 = new Predecessor(v, v2, d);
                    queue.insert(p2);
                    queued.put(v2, p2);
                }
                else if (D.weight > d) {
                    D.weight = d;
                    D.pred = v;
                    queue.update(D);
                }
            }
        }
        if (vertexMaps != null) {
            vertexMaps.put(from, S);
        }
        return S;
    }
    
    public static double getWeight(Object o) {
        return ((Predecessor)o).getWeight();
    }
    
    public static int getVertex(Object o) {
        return ((Predecessor)o).getVertex();
    }
    
    public static int getPred(Object o) {
        return ((Predecessor)o).getPred();
    }
    
    public Predecessor getPredecessor(int from, Predecessor p) {
        return shortestPath(from, p.getPred());
    }

    public static class Predecessor implements Comparable {
        protected int vertex;
        protected double weight;
        protected int pred;
        Predecessor(int pred, int vertex, double weight) {
            this.pred = pred;
            this.vertex = vertex;
            this.weight = weight;
        }
        
        Predecessor(int vertex, double weight) {
            this.pred = -1;
            this.vertex = vertex;
            this.weight = weight;
        }
        
        public int compareTo(Object o) {
            double cmp = (weight - ((Predecessor)o).weight);
            if (cmp < 0) return -1;
            else if (cmp > 0) return 1;
            else return 0;
        }
        
        public int getVertex() {
            return vertex;
        }
        
        public double getWeight() {
            return weight;
        }
        
        public int getPred() {
            return pred;
        }
    }
}
