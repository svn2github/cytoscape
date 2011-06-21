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
along with this program.  If not, see <http://www.gnu.org/licenses/>.

**************************************************************************************/

package cytoscape.plugins.igraph;

import cytoscape.Cytoscape;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.CyNode;
import cytoscape.logger.CyLogger;

import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.EdgeWeighter;
import csplugins.layout.algorithms.graphPartition.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;

public class CircleLayout extends AbstractGraphPartition
{
    private String message;
    private LayoutProperties layoutProperties;


    /**
     * Creates a new layout object.
     */
    public CircleLayout() {
	super();

	logger = CyLogger.getLogger(CircleLayout.class);
	// logger.setDebug(true);

	layoutProperties = new LayoutProperties(getName());
	initialize_properties();
    }

    /**
     * This plugin supports laying out only selected nodes
     */
    public boolean supportsSelectedOnly() {
		return true;
    }
    
    /**
     * Adds tunable objects for adjusting plugin parameters
     * Initializes default values for those parameters
     */
    protected void initialize_properties() {
	
	// Add new properties to layout 
	
	// Initialize layout properties
	layoutProperties.initializeProperties();
	
	// Force the settings update
	updateSettings(true);
    }
    
    /**
     * getName is used to construct property strings
     * for this layout.
     */
    public  String getName() {
	return "Igraph Circle Layout";
    }
    
    /**
     * toString is used to get the user-visible name
     * of the layout
     */
    public  String toString(){
	return "Circle Layout";
    }
    
    /**
     * Overload updateSettings for using it without arguments
     */
    public void updateSettings() {
	updateSettings(false);
    }
    
    /**
     * Get new values from tunables and update parameters
     */
    public void updateSettings(boolean force) {
	layoutProperties.updateValues();	
    }
    
    /**
     * Get the settings panel for this layout
     */
    public JPanel getSettingsPanel() {
	JPanel panel = new JPanel(new GridLayout(1, 1));
	panel.add(layoutProperties.getTunablePanel());
	
	return panel;
    }
    
    /**
     * Revert previous settings
     */
    public void revertSettings() {
	layoutProperties.revertProperties();
    }    
    
    /**
     * Get layout properties
     */
    public LayoutProperties getSettings() {
	return layoutProperties;
    }
	
    
    /**
     * 
     */
    public void layoutPartition (LayoutPartition part) {

	// Check whether it has been canceled by the user
	if (canceled)
	    return;

	// Show message on the task monitor
	taskMonitor.setStatus("Initializing: Partition: " + part.getPartitionNumber());
	
	// The completed percentage is indeterminable
	taskMonitor.setPercentCompleted(-1);

	// Figure out our starting point if we are only laying out selected nodes
	Dimension initialLocation = null;
	if (selectedOnly) {
	    initialLocation = part.getAverageLocation();
	}

	// Get the number of edges and nodes
	int numNodes = part.nodeCount();

	// Allocate memory for storing graph edges information (to be used as arguments for JNI call)
	double[] x = new double[numNodes];
	double[] y = new double[numNodes];	

	// Load graph into native library
	HashMap<Integer,Integer> mapping = loadGraphPartition(part);

	// Simplify graph
	IgraphInterface.simplify();

	// Check whether it has been canceled by the user
	if (canceled)
	    return;

	// Show message on the task monitor
	taskMonitor.setStatus("Calling native code: Partition: " + part.getPartitionNumber());
	
 	// Make native method call
	IgraphInterface.layoutCircle(x, y);

	// Check whether it has been canceled by the user
	if (canceled)
	    return;
	// Show message on the task monitor
	taskMonitor.setStatus("Updating display");	
	



	// Find which ratio is required to 'scale up' the node positions so that nodes don't overlap
	double upRatio = 0.0;
	double actualRatio = 0.0;

	for (LayoutEdge edge: part.getEdgeList()) {
	    LayoutNode n1 = edge.getSource();
	    LayoutNode n2 = edge.getTarget();

	    double n1Size = Math.max(n1.getHeight(), n1.getWidth());
	    double n2Size = Math.max(n2.getHeight(), n2.getWidth());

	    double edgeSize = Math.sqrt( Math.pow(x[mapping.get(n1.getIndex())] - x[mapping.get(n2.getIndex())], 2)
				       + Math.pow(y[mapping.get(n1.getIndex())] - y[mapping.get(n2.getIndex())], 2)
				       );
	    // Check if edgeSize is not zero
	    if (edgeSize > 0.0){
	    
		// actualRatio = minimun_desired_length / actual_length
		actualRatio = (n1Size + n2Size) / edgeSize;
		
		// Update upRatio if necessary
		if (actualRatio > upRatio)
		    upRatio = actualRatio;
	    }

	}
	
	//if (upRatio > 1000) 
	//    upRatio =  Math.log(upRatio) + 1000;
	//upRatio = Math.min(upRatio, 100.0);

	// Check whether the ratio is not zero
	if (upRatio < 1.0){   
	    upRatio = 1.0;
	}

	double oldUpRatio = upRatio;

	//	logger.info("upRatio = " + upRatio);		    
	//message = "upRatio = " + upRatio;
	//JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);


	// Move nodes to their position
	boolean success = false;
	while (!success){
	    
	    try{
		// We will keep track of the offset of the whole part so that we can eliminate it
		double minX = x[0];
		double minY = y[0];
		
		// Get the 'offset' of the whole partition, so that we can eliminate it
		for (int i = 1; i < numNodes; i++) {
		    
		    if (x[i] < minX)
			minX = x[i];

		    if (y[i] < minY)
			minY = y[i];
		}

		minX = upRatio * minX;
		minY = upRatio * minY;
		
		// Reset the nodes so we get the new average location
		part.resetNodes(); 

		// Iterate over all nodes
		int currentNode = 0;
		
		// Create an iterator for processing the nodes
		Iterator<LayoutNode> iterator2 = part.getNodeList().iterator();
				
		while (iterator2.hasNext()){
		    
		    // Get next node
		    LayoutNode node = (LayoutNode) iterator2.next();
		
		    // Set node's X and Y positions
		    node.setX(upRatio * x[mapping.get(node.getIndex())] - minX);
		    node.setY(upRatio * y[mapping.get(node.getIndex())] - minY);

		    //logger.debug("moving node " + currentNode + "to:\nX = " + (upRatio * node_positions[currentNode][0]) + "\nY = " + (upRatio * node_positions[currentNode][1]));
		    
		    // Move node to desired location
		    part.moveNodeToLocation(node);
		    
		    currentNode++;
		}
	
	
		// Not quite done, yet.  If we're only laying out selected nodes, we need
		// to migrate the selected nodes back to their starting position
		if (selectedOnly) {
		    double xDelta = 0.0;
		    double yDelta = 0.0;
		    Dimension finalLocation = part.getAverageLocation();
		    xDelta = finalLocation.getWidth()  - initialLocation.getWidth();
		    yDelta = finalLocation.getHeight() - initialLocation.getHeight();
		    for (LayoutNode v: part.getNodeList()) {
			if (!v.isLocked()) {
			    v.decrement(xDelta, yDelta);
			    part.moveNodeToLocation(v);
			}
		    }
		}
		
		success = true;

	    }
	    catch(Exception excep){

		upRatio = upRatio / 10.0;
		if (upRatio <= 0.0){
		    message = "Sorry, cannot produce layout";
		    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
		    return;
		}
		success = false;
	    }

	}//while(!success)







	// Actually move the pieces around
	// Note that we reset our min/max values before we start this
	// so we can get an accurate min/max for paritioning
// 	part.resetNodes();

// 	for (LayoutNode v: part.getNodeList()) {
// 	    // Set node's X and Y positions
// 	    v.setX(x[mapping.get(v.getIndex())]);
// 	    v.setY(y[mapping.get(v.getIndex())]);

// 	    // Actually move the node
// 	    part.moveNodeToLocation(v);
// 	}

// 	// Not quite done, yet.  If we're only laying out selected nodes, we need
// 	// to migrate the selected nodes back to their starting position
// 	if (selectedOnly) {
// 	    double xDelta = 0.0;
// 	    double yDelta = 0.0;
// 	    Dimension finalLocation = part.getAverageLocation();
// 	    xDelta = finalLocation.getWidth() - initialLocation.getWidth();
// 	    yDelta = finalLocation.getHeight() - initialLocation.getHeight();

// 	    for (LayoutNode v: part.getNodeList()) {
// 		if (!v.isLocked()) {
// 		    v.decrement(xDelta, yDelta);
// 		    part.moveNodeToLocation(v);
// 		}
// 	    }
// 	}


    }// layoutPartion(LayoutPartition part)


    /**
     * This function loads a partition into igraph
     *
     */    
    public static HashMap<Integer,Integer> loadGraphPartition(LayoutPartition part){

	CyLogger logger = CyLogger.getLogger(CircleLayout.class);	    
	
	// Create a reverse mapping
	int nodeCount = part.nodeCount();

	HashMap<Integer, Integer> nodeIdMapping = new HashMap<Integer, Integer>(nodeCount);
	int j = 0;

	Iterator<LayoutNode> nodeIt = part.getNodeList().iterator();	
	while(nodeIt.hasNext()){            
	    LayoutNode node = (LayoutNode) nodeIt.next();
	    nodeIdMapping.put(node.getIndex(), j);
	    j++;
	}
		
	// Write edges (as pairs of consecutive nodes) in edgeArray
	int[] edgeArray = new int[part.edgeCount() * 2];
	int i = 0;
	
	Iterator<LayoutEdge> it = part.getEdgeList().iterator();

	while (it.hasNext()) {
	    LayoutEdge e = (LayoutEdge) it.next();

	    LayoutNode source = e.getSource();
	    LayoutNode target = e.getTarget();

	    edgeArray[i]     = nodeIdMapping.get(source.getIndex());
	    edgeArray[i + 1] = nodeIdMapping.get(target.getIndex());
	    i += 2;
	    
	}

	IgraphInterface.createGraph(edgeArray, i);
	//	IgraphInterface.simplify();

	return nodeIdMapping;
    } // loadGraphPartition()


}