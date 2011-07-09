/**************************************************************************************
Copyright (C) Gerardo Huck, 2011


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

**************************************************************************************/

package cytoscape.plugins.igraph.layout;

import cytoscape.plugins.igraph.*;
import cytoscape.logger.CyLogger;
import cytoscape.layout.LayoutProperties;
import csplugins.layout.LayoutPartition;

import java.util.*;

public class CircleLayout extends AbstractIgraphLayout {

    public CircleLayout() {
	super();
	logger = CyLogger.getLogger(CircleLayout.class);
	
	layoutProperties = new LayoutProperties(getName());
	initialize_properties();
    }

    /**
     * Do the layout on a graph alrealy loaded into igraph
     */
    public int layout(double[] x, 
		      double[] y, 
		      LayoutPartition part, 
		      HashMap<Integer,Integer> mapping, 
		      double[] weights) {
	// Simplify graph
	IgraphInterface.simplify();
	
 	// Make native method call
	IgraphInterface.layoutCircle(x, y);

	return 1;
    }

    /**
     * getName is used to construct property strings
     * for this layout.
     */
    public String getName() {
	return "Igraph Circle Layout";
    }
    
    /**
     * toString is used to get the user-visible name
     * of the layout
     */
    public String toString() {
	return "Circle Layout";
    }

}