/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.clustering;

import java.io.Serializable;

import infovis.Graph;
import infovis.utils.RowComparator;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class VertexOrderComparator implements RowComparator, Serializable {
    Graph graph;
    
    public VertexOrderComparator(Graph graph) {
        this.graph = graph;
    }

    /* (non-Javadoc)
     * @see infovis.utils.RowComparator#compare(int, int)
     */
    public int compare(int edge1, int edge2) {
        return graph.getFirstVertex(edge1) - graph.getFirstVertex(edge2);
    }
    
    public boolean isValueUndefined(int row) {
        return false;
    }



}
