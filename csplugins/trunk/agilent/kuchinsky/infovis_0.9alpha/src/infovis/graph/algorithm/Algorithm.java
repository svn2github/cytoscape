/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;

/**
 * Class Algorithm
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public abstract class Algorithm {
    protected Graph graph;
    public static final int WHITE = 0;
    public static final int GREY  = 1;
    public static final int BLACK = 2;
    
    public Algorithm(Graph graph) {
        this.graph = graph;
    }
}
