/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.column.IntColumn;
import infovis.column.StringColumn;
import infovis.utils.*;
import cern.colt.Sorting;
import cern.colt.list.IntArrayList;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.OpenIntIntHashMap;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class Algorithms {

    public static int[] sortEdges(Graph graph, int vertex,
            RowComparator comp) {
        int size = graph.getOutDegree(vertex);
        int[] sorted = new int[size];
        int i = 0;

        for (RowIterator iter = graph.outEdgeIterator(vertex); iter
                .hasNext();) {
            int edge = iter.nextRow();
            sorted[i++] = edge;
        }
        if (sorted.length > 1)
            Sorting.mergeSort(sorted, 0, sorted.length, comp);

        return sorted;
    }

    /**
     * Copy one graph into another.
     * 
     * @param fromGraph
     *            the source graph
     * @param toGraph
     *            the destination graph
     * @return the mapping from the vertices in the source graph to the vertices
     *         in the destination graph.
     */
    public static OpenIntIntHashMap copy(Graph fromGraph, Graph toGraph) {
        OpenIntIntHashMap map = new OpenIntIntHashMap();
        for (RowIterator edge = fromGraph.edgeIterator(); edge
                .hasNext();) {
            int e = edge.nextRow();
            int v1 = fromGraph.getFirstVertex(e);
            if (map.containsKey(v1)) {
                v1 = map.get(v1);
            } else {
                int v = toGraph.addVertex();
                map.put(v1, v);
                v1 = v;
            }
            int v2 = fromGraph.getSecondVertex(e);
            if (map.containsKey(v2)) {
                v2 = map.get(v2);
            } else {
                int v = toGraph.addVertex();
                map.put(v2, v);
                v2 = v;
            }
            toGraph.addEdge(v1, v2);
        }
        return map;
    }

    /**
     * Returns a bigger, undirected test graph with a just one component. This
     * graph consists of a clique of ten edges, a partial clique (randomly
     * generated, with edges of 0.6 probability), and one series of edges
     * running from the first node to the last.
     * 
     * Adapted from JUNG (jung.sourceforge.net)
     * 
     * @return the testgraph
     */
    public static Graph getOneCompnentGraph() {
        DefaultGraph g = new DefaultGraph();
        g.setDirected(false);
        StringColumn sl = StringColumn.findColumn(g.getVertexTable(),
                "label");
        IntColumn el = IntColumn.findColumn(g.getEdgeTable(), "weight");

        for (int i = 0; i < 20; i++) {
            int v = g.addVertex();
            assert (v == i);
            sl.setExtend(v, "" + v+1);
        }
        // let's throw in a clique, too
        for (int i = 1; i <= 10; i++) {
            for (int j = i + 1; j <= 10; j++) {
                int edge = g.addEdge(i - 1, j - 1);
                el.setExtend(edge, i + j);
            }
        }

        // and, last, a partial clique
        for (int i = 11; i <= 20; i++) {
            for (int j = i + 1; j <= 20; j++) {
                if (Math.random() > 0.6)
                    continue;
                int edge = g.addEdge(i - 1, j - 1);
                el.setExtend(edge, i + j);
            }
        }

        // and one edge to connect them all
        //JDF: JUNG is unpredictable in its order whereas InfoVis is
        // totaly predictable so random pemute the generated path
//        for (int i = 0; i < g.getVerticesCount() - 1; i++) {
//            g.findEdge(i, i+1);
//        }
        IntArrayList list = new IntArrayList(20);
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        list.shuffle();
        for (int i = 0; i < list.size()-1; i++) {
            g.findEdge(list.get(i), list.get(i+1));
        }
        

        return g;
    }
    
    public static Graph getGridGraph(int width, int height) {
        DefaultGraph g = new DefaultGraph();
        g.setDirected(false);
        int x, y;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                int v = g.addVertex();
                if (x != 0) {
                    g.addEdge(v-1, v);
                }
                if (y != 0) {
                    g.addEdge(v-width, v);
                }
            }
        }
        
        return g;
    }
    
    public static void findComponent(Graph g, int vertex, AbstractIntIntMap map) {
        if (map.containsKey(vertex)) return;
        map.put(vertex, map.size()+1);
        for (RowIterator iter = g.edgeIterator(vertex); iter.hasNext(); ) {
            int e = iter.nextRow();
            int v2 = g.getOtherVertex(e, vertex);
            findComponent(g, v2, map);
        }
    }
    
    public static int labelConnectedComponents(Graph graph, IntColumn labels, IntColumn sizes) {
        int comp = 0;
        IntIntSortedMap map = new IntIntSortedMap();
        final IntColumn l = (labels == null) ? new IntColumn("components") : labels;
        for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
            int vertex = iter.nextRow();
            if (l.isValueUndefined(vertex)) {
                findComponent(graph, vertex, map);
                for (RowIterator kiter = map.keyIterator(); kiter.hasNext(); ) {
                    int i = kiter.nextRow();
                    l.setExtend(i, comp);
                }
                if (sizes != null) {
                    sizes.setExtend(comp, map.size());
                }
                map.clear();
                comp++;
            }
        }
        return comp;
    }
}