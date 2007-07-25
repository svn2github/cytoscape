/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.column.IntColumn;
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.metadata.IO;
import infovis.utils.Heap;
import infovis.utils.RowIterator;
import cern.colt.function.IntIntFunction;
import cern.colt.map.OpenIntObjectHashMap;

/**
 * Computes the "coreness" of graph vertices.
 * The "k-core" of a graph is the (unique) largest subgraph all of whose vertices have degree at least k.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class KCoreDecomposition implements GraphChangedListener {
    public static final String CORENESS_COLUMN = "[Coreness]";
    Graph                      graph;
    IntColumn                  corenessColumn;

    public KCoreDecomposition(Graph graph, IntColumn coreness) {
        this.graph = graph;
        this.corenessColumn = coreness;
        coreness.getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
        update();
    }

    public void graphChanged(GraphChangedEvent e) {
        update();
    }

    public void update() {
        try {
            corenessColumn.disableNotify();
            corenessColumn = computeCoreness(graph, corenessColumn);
        } finally {
            corenessColumn.enableNotify();
        }
    }

    static class Coreness implements Comparable {
        int vertex;
        int coreness;

        public Coreness(int vertex, int coreness) {
            this.vertex = vertex;
            this.coreness = coreness;
        }

        public int compareTo(Object o) {
            int cmp = (coreness - ((Coreness) o).coreness);
            if (cmp < 0)
                return -1;
            else if (cmp > 0)
                return 1;
            else
                return 0;
        }

        public int getVertex() {
            return vertex;
        }

        public int getCoreness() {
            return coreness;
        }

    }

    public static IntColumn computeCoreness(
            Graph g, 
            IntColumn coreness) {
        if (coreness == null) {
            coreness = new IntColumn(CORENESS_COLUMN);
        }
        else {
            coreness.clear();
        }
        Heap heap = new Heap();
        OpenIntObjectHashMap queued = new OpenIntObjectHashMap();
        for (RowIterator iter = g.vertexIterator(); iter.hasNext();) {
            int v = iter.nextRow();
            Coreness c = new Coreness(v, g.getDegree(v));
            heap.insert(c);
            queued.put(v, c);
        }

        while (!heap.isEmpty()) {
            Coreness c = (Coreness) heap.pop();
            int v = c.getVertex();
            int cor = c.getCoreness();
            queued.removeKey(v);
            coreness.setExtend(v, cor);
            for (RowIterator neighbors = g.edgeIterator(v); neighbors.hasNext();) {
                int e = neighbors.nextRow();
                int v2 = g.getOtherVertex(e, v);
                Coreness c2 = (Coreness) queued.get(v2);
                if (c2 != null) {
                    if (c2.coreness > cor) {
                        c2.coreness--;
                        heap.update(c2);
                    }
                }
            }
        }

        return coreness;
    }
    
    public static IntColumn computeEdgeCoreness(
            Graph g, 
            IntColumn coreness,
            IntColumn edgeCoreness,
            IntIntFunction fn) {
        if (edgeCoreness == null) {
            edgeCoreness = new IntColumn("[edgeCoreness]");
        }
        else {
            edgeCoreness.clear();
        }
        assert(coreness != null);
        for (RowIterator iter = g.edgeIterator(); iter.hasNext(); ) {
            int e = iter.nextRow();
            int value = fn.apply(
                    coreness.get(g.getFirstVertex(e)),
                    coreness.get(g.getSecondVertex(e)));
            edgeCoreness.setExtend(e, value);
        }
        return edgeCoreness;
    }
}
