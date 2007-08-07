//
// RotateLayoutHelper.java
//
// $Revision$
// $Date$
// $Author$
//



package cytoscape.layout;

import java.io.*;
import java.util.*;

import y.base.*;
import y.layout.*;


/**
 * A utility class to rotate a node selection around its centroid.
 */
public class RotateLayoutHelper  {

    /**
     * Public static rotation routine.  Rotates the given selection of
     * nodes about its centroid by the specified angle (in radians)
     */
    public static void rotate(LayoutGraph graph, NodeCursor nc, double angle) {
	double cX = 0;
	double cY = 0;
	
	// step 1: calculate centroid
	for (nc.toFirst(); nc.ok(); nc.next()) {
	    cX += graph.getX(nc.node());
	    cY += graph.getY(nc.node());
	}

	cX /= nc.size();
	cY /= nc.size();


	// step 2: rotate
	for (nc.toFirst(); nc.ok(); nc.next()) {
	    double x = graph.getX(nc.node()) - cX;
	    double y = graph.getY(nc.node()) - cY;
	    double r = Math.sqrt(x*x + y*y);
	    double t = 0;

	    if (x == 0)
		t = (y < 0 ? Math.PI + Math.PI / 2 : Math.PI / 2);
	    else {
		t = Math.atan(y/x);
		if (x < 0)
		    t += Math.PI;
	    }

	    graph.setLocation(nc.node(),
			      cX + r * Math.cos(t + angle),
			      cY + r * Math.sin(t + angle));
	}
    }
}
