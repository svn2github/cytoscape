/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

/**
 * Class GraphException
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class GraphException extends RuntimeException {
    protected int vertex;
    protected int edge;
    public GraphException(String msg, int vertex, int edge) {
        super(msg);
        this.vertex = vertex;
        this.edge = edge;
    }


    public int getEdge() {
        return edge;
    }

    public int getVertex() {
        return vertex;
    }

}
