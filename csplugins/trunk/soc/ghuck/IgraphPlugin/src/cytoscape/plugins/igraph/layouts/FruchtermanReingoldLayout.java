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
import cytoscape.layout.Tunable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;

import java.util.*;

public class FruchtermanReingoldLayout extends AbstractIgraphLayout {

    private int     iterNumber            = 500;   // Number of iterations
    private double  maxDeltaCoefficient   = 1.0;   // Maximum distance to move a node in each iteration
    private double  areaCoefficient       = 1.0;   // Area parameter
    private double  coolExp               = 1.5;   // The cooling exponent of the simulated annealing
    private double  repulseRadCoefficient = 1.0;   // Determines the radius at which vertex-vertex repulsion cancels out attraction of adjacent vertices
    private boolean randomize             = true;  // Randomize node position before layout


    public FruchtermanReingoldLayout(boolean supportEdgeWeights) {
	super();
	logger = CyLogger.getLogger(FruchtermanReingoldLayout.class);
	
	supportWeights = supportEdgeWeights;

	layoutProperties = new LayoutProperties(getName());
	this.initialize_properties();
    }

    /**
     * Adds tunable objects for adjusting plugin parameters
     * Initializes default values for those parameters
     */
    protected void initialize_properties() {
	super.initialize_properties();

	// Add new properties to layout 
	layoutProperties.add(new Tunable("iterNumber", 
					 "Number of Iterations", 
					 Tunable.INTEGER, 
					 new Integer(iterNumber)));	

	layoutProperties.add(new Tunable("maxDeltaCoefficient", 
					 "Distance to move nodes in each iteration", 
					 Tunable.DOUBLE, 
					 new Double(maxDeltaCoefficient)));

	layoutProperties.add(new Tunable("areaCoefficient", 
					 "Area to use in layout", 
					 Tunable.DOUBLE, 
					 new Double(areaCoefficient)));

	layoutProperties.add(new Tunable("coolExp", 
					 "Cooling exponent of the simulated annealing", 
					 Tunable.DOUBLE, 
					 new Double(coolExp)));

	layoutProperties.add(new Tunable("repulseRadCoefficient", 
					 "Radius at which vertex-vertex repulsion cancels out attraction of adjacent vertices", 
					 Tunable.DOUBLE, 
					 new Double(repulseRadCoefficient)));

	layoutProperties.add(new Tunable("randomize",
					 "Randomize node locations before laying out nodes",
					 Tunable.BOOLEAN, 
					 new Boolean(randomize)));

	// Initialize layout properties
	layoutProperties.initializeProperties();
	
	// Force the settings update
	updateSettings(true);
    }

    /**
     * Get new values from tunables and update parameters
     */
    public void updateSettings(boolean force) {
	super.updateSettings(force);

	// Get initialNoIterations
	Tunable t1 = layoutProperties.get("iterNumber");
	if ((t1 != null) && (t1.valueChanged() || force))
	    iterNumber = ((Integer) t1.getValue()).intValue();

	// Get maxDeltaCoefficient
	Tunable t2 = layoutProperties.get("maxDeltaCoefficient");
	if ((t2 != null) && (t2.valueChanged() || force))
	    maxDeltaCoefficient = ((Double) t2.getValue()).doubleValue();

	// Get areaCoefficient
	Tunable t3 = layoutProperties.get("areaCoefficient");
	if ((t3 != null) && (t3.valueChanged() || force))
	    areaCoefficient = ((Double) t3.getValue()).doubleValue();

	// Get coolExp
	Tunable t4 = layoutProperties.get("coolExp");
	if ((t4 != null) && (t4.valueChanged() || force))
	    coolExp = ((Double) t4.getValue()).doubleValue();

	// Get repulseRadCoefficient
	Tunable t5 = layoutProperties.get("repulseRadCoefficient");
	if ((t5 != null) && (t5.valueChanged() || force))
	    repulseRadCoefficient = ((Double) t5.getValue()).doubleValue();

	// Get randomize
	Tunable t6 = layoutProperties.get("randomize");
	if ((t6 != null) && (t6.valueChanged() || force))
	    randomize = ((Boolean) t6.getValue()).booleanValue();

	updateSettings(layoutProperties, force);
    }


    /**
     * Do the layout on a graph alrealy loaded into igraph
     */
    public int layout(double[] x, 
		      double[] y, 
		      LayoutPartition part, 
		      HashMap<Integer,Integer> mapping, 		      
		      double[] weights) {

	
	int numNodes = mapping.size();

	double maxDelta   = maxDeltaCoefficient * numNodes;
	double area       = areaCoefficient * numNodes * numNodes;
	double repulseRad = repulseRadCoefficient * area * numNodes;

	// Store current node positions if necessary
	if (!randomize) {
	    loadPositions(part, mapping, x, y);
	}
	
 	// Make native method call
	IgraphInterface.layoutFruchterman(x, 
					  y, 
					  iterNumber, 
					  maxDelta, 
					  area, 
					  coolExp, 
					  repulseRad, 
					  !randomize,
					  supportWeights,
					  weights);
	
	return 1;
    }

    /**
     * getName is used to construct property strings
     * for this layout.
     */
    public String getName() {
	if (supportWeights)
	    return "Igraph Edge-Weighted Fruchterman-Reingold Layout";
	else
	    return "Igraph Fruchterman-Reingold Layout";
    }
    
    /**
     * toString is used to get the user-visible name
     * of the layout
     */
    public String toString() {
	if (supportWeights)
	    return "Edge-Weighted Fruchterman-Reingold Layout";
	else
	    return "Fruchterman-Reingold Layout";
    }

}



