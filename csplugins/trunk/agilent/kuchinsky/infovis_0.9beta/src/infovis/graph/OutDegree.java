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
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.metadata.IO;
import infovis.utils.RowIterator;

/**
 * Column containing the number of outgoing edges from each vertex of the Graph.
 *
 * <p>This column is automatically maintained by the <code>ColumnLink</code>
 * mechanism.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class OutDegree implements GraphChangedListener {
    /** Name of the optional Column containing the number of outgoing edges. */
    public static final String OUTDEGREE_COLUMN = "[outDegree]";
    Graph graph;
    IntColumn outDegree;

    protected OutDegree(Graph graph, IntColumn outDegree) {
        this.graph = graph;
        this.outDegree = outDegree;
        outDegree.getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
    }

    public void graphChanged(GraphChangedEvent e) {
        switch (e.getType()) {
            case GraphChangedEvent.GRAPH_VERTEX_ADDED :
                outDegree.setExtend(e.getDetail(), 0);
                break;
            case GraphChangedEvent.GRAPH_VERTEX_REMOVED :
                outDegree.setValueUndefined(e.getDetail(), true);
                break;
            case GraphChangedEvent.GRAPH_EDGE_ADDED :
                {
                    int v = graph.getSecondVertex(e.getDetail());
                    outDegree.set(v, outDegree.get(v) + 1);
                }
                break;
            case GraphChangedEvent.GRAPH_EDGE_REMOVED :
                {
                    int v = graph.getSecondVertex(e.getDetail());
                    outDegree.set(v, outDegree.get(v) - 1);
                }
                break;
        }
    }

    public void update() {
        try {
            outDegree.disableNotify();
            outDegree.clear();

            for (RowIterator iter = graph.getVertexTable().iterator();
                iter.hasNext();
                ) {
                int vertex = iter.nextRow();
                outDegree.setExtend(vertex, graph.getOutDegree(vertex));
            }
        }
        finally {
            outDegree.enableNotify();
        }
    }

    /**
     * Returns the out degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     *
     * @return the out degree column associated with the graph.
     */
    public static IntColumn getColumn(Graph graph) {
        IntColumn outDegree =
            IntColumn.getColumn(
                graph.getVertexTable(),
                OUTDEGREE_COLUMN);
        if (outDegree == null) {
            outDegree =
                IntColumn.findColumn(
                    graph.getVertexTable(),
                    OUTDEGREE_COLUMN);
            OutDegree degree = new OutDegree(graph, outDegree);
            degree.update();
        }
        return outDegree;
    }
}
