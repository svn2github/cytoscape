package org.mike.impl; 

import org.mike.CyEdge;

class EdgePointer {

	final CyEdge cyEdge;
	final int index;

	EdgePointer nextOutEdge;
	EdgePointer prevOutEdge;

	EdgePointer nextInEdge;
	EdgePointer prevInEdge;

	boolean directed;

	NodePointer source;
	NodePointer target;

	EdgePointer(final NodePointer s, final NodePointer t, boolean dir, final int ind) {
		index = ind; 
		source = s;
		target = t;
		directed = dir;
		cyEdge = new CyEdgeImpl(source.cyNode, target.cyNode, directed, index);

        if (directed) {
            source.outDegree++;
            target.inDegree++;
        } else {
            source.undDegree++;
            target.undDegree++;
        }

		// Self-edge
        if (source == target) { 
            if (directed) {
                source.selfEdges++;
            } else {
                source.undDegree--;
            }
        }
	}
}
