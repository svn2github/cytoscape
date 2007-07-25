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
 * Column containing the number of incident edges of each vertex of the Graph.
 *
 * <p>This column is automatically maintained by the <code>ColumnLink</code>
 * mechanism.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class InDegree implements GraphChangedListener {
    /** Name of the optional Column containing the number of incident edges. */
    public static final String INDEGREE_COLUMN = "[inDegree]";
    Graph graph;
    IntColumn inDegree;

    protected InDegree(Graph graph, IntColumn inDegree) {
        this.graph = graph;
        this.inDegree = inDegree;
        inDegree.getMetadata().addAttribute(IO.IO_TRANSIENT, Boolean.TRUE);
    }
    
    public void graphChanged(GraphChangedEvent e) {
        switch(e.getType()) {
            case GraphChangedEvent.GRAPH_VERTEX_ADDED:
                inDegree.setExtend(e.getDetail(), 0);
                break;
            case GraphChangedEvent.GRAPH_VERTEX_REMOVED:
                inDegree.setValueUndefined(e.getDetail(), true);
                break;
            case GraphChangedEvent.GRAPH_EDGE_ADDED: {
                int v = graph.getFirstVertex(e.getDetail());
                inDegree.set(v, inDegree.get(v)+1);
            }
                break;
            case GraphChangedEvent.GRAPH_EDGE_REMOVED: {
                int v = graph.getFirstVertex(e.getDetail());
                inDegree.set(v, inDegree.get(v)-1);
            }
                break;
        }
    }

    public void update() {
        try {
            inDegree.disableNotify();
            inDegree.clear();
    
            for (RowIterator iter = graph.vertexIterator();
                iter.hasNext();
                ) {
                int vertex = iter.nextRow();
                inDegree.setExtend(vertex, graph.getInDegree(vertex));
            }
        }
        finally {
            inDegree.enableNotify();
        }
    }

    /**
     * Returns the in degree column associated with a graph, creating it
     * if required.
     *
     * @param graph the graph.
     *
     * @return the in degree column associated with the graph.
     */
    public static IntColumn getColumn(Graph graph) {
        IntColumn inDegree =
            IntColumn.getColumn(graph.getVertexTable(), INDEGREE_COLUMN);
        if (inDegree == null) {
            inDegree = IntColumn.findColumn(graph.getVertexTable(), INDEGREE_COLUMN);            
            InDegree degree =
                new InDegree(
                    graph,
                    inDegree);
            degree.update();
        }
        return inDegree;
    }

}
