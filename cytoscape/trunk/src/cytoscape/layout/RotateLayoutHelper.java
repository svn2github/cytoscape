//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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


