/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.event;

import infovis.Graph;

/**
 * Class GraphChangedEvent
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class GraphChangedEvent {
    protected Graph graph;
    protected int detail;
    protected short type;
    
    public static final short GRAPH_VERTEX_ADDED = 0;
    public static final short GRAPH_VERTEX_REMOVED = 1;
    public static final short GRAPH_EDGE_ADDED = 2;
    public static final short GRAPH_EDGE_REMOVED = 3;
    
    public GraphChangedEvent(Graph graph, int detail, short type) {
        this.graph = graph;
        this.detail = detail;
        this.type = type;
    }
    
    public int getDetail() {
        return detail;
    }

    public Graph getGraph() {
        return graph;
    }

    public short getType() {
        return type;
    }

}
