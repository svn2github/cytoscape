/**************************************************************************************
Copyright (C) Gerardo Huck, 2009


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
package GpuLayout;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.CyNode;
import cytoscape.logger.CyLogger;

import giny.model.GraphPerspective;
import giny.model.Node;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.lang.Math;
import java.util.*;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;


import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.EdgeWeighter;
import csplugins.layout.algorithms.graphPartition.*;


public class ForceDirected extends AbstractGraphPartition
{
    private double H_SIZE = 1000.0;
    private double V_SIZE = 1000.0;
    private String GPU_LIBRARY = "GpuLayout";
     
    // Default values for algorithm parameters	
    private int coarseGraphSize         = 50;
    private int interpolationIterations = 50;
    private int levelConvergence        = 2;
    private int edgeLen                 = 5;
    private int initialNoIterations     = 300;

    private String CUDA_PATH = "/usr/local/cuda/lib";

    private LayoutProperties layoutProperties;




    /**
     * Creates a new layout object.
     */
    public ForceDirected() {
	super();

	logger = CyLogger.getLogger(ForceDirected.class);
	// logger.setDebug(true);

	layoutProperties = new LayoutProperties(getName());
	initialize_properties();
    }

    /**
     * This plugin supports laying out only selected nodes
     */
    public boolean supportsSelectedOnly() {
		return false;
    }

    
    /**
     * Adds tunable objects for adjusting plugin parameters
     * Initializes default values for those parameters
     */
    protected void initialize_properties() {
	
	// Add new properties to layout 
	layoutProperties.add(new Tunable("coarseGraphSize",         "Coarse Graph Size"           , Tunable.INTEGER, new Integer(50) ));
	layoutProperties.add(new Tunable("interpolationIterations", "Interpolation Iterations"    , Tunable.INTEGER, new Integer(50) ));
	layoutProperties.add(new Tunable("levelConvergence",        "Level Convergence"           , Tunable.INTEGER, new Integer(2)  ));
	layoutProperties.add(new Tunable("edgeLen",                 "Ideal Edge Length"           , Tunable.INTEGER, new Integer(5)  ));
	layoutProperties.add(new Tunable("initialNoIterations",     "Initial Number of Iterations", Tunable.INTEGER, new Integer(300)));

	layoutProperties.add(new Tunable("CUDA_PATH", "CUDA instalation folder", Tunable.STRING , new String("/usr/local/cuda")));
	
	
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
	return "Force Directed GPU Layout";
    }
    
    /**
     * toString is used to get the user-visible name
     * of the layout
     */
    public  String toString(){
	return "Force Directed Layout";
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
	
	// Get coarseGraphSize
	Tunable t1 = layoutProperties.get("coarseGraphSize");
	if ((t1 != null) && (t1.valueChanged() || force))
	    coarseGraphSize = ((Integer) t1.getValue()).intValue();
	
	// Get interpolationIterations
	Tunable t2 = layoutProperties.get("interpolationIterations");
	if ((t2 != null) && (t2.valueChanged() || force))
	    interpolationIterations = ((Integer) t2.getValue()).intValue();
	
	// Get levelConvergence
	Tunable t3 = layoutProperties.get("levelConvergence");
	if ((t3 != null) && (t3.valueChanged() || force))
	    levelConvergence = ((Integer) t3.getValue()).intValue();
	
	// Get edgeLen
	Tunable t4 = layoutProperties.get("edgeLen");
	if ((t4 != null) && (t4.valueChanged() || force))
	    edgeLen = ((Integer) t4.getValue()).intValue();
	
	// Get initialNoIterations
	Tunable t5 = layoutProperties.get("initialNoIterations");
	if ((t5 != null) && (t5.valueChanged() || force))
	    initialNoIterations = ((Integer) t5.getValue()).intValue();

	// Get initialNoIterations
	Tunable t6 = layoutProperties.get("CUDA_PATH");
	if ((t6 != null) && (t6.valueChanged() || force))
	    CUDA_PATH =  t6.getValue().toString();


	
	// Show message on screen    
	/*
	  String message = "Preferences updated\n" 
	  +  coarseGraphSize + "\n" 
	  + interpolationIterations + "\n"  
	  + levelConvergence + "\n"         
	  + edgeLen + "\n"                  
	  + initialNoIterations + "\n"
	  + CUDA_PATH;
	  
	  JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	*/
    }
    
    /**
     * Get the settings panel for this layout
     */
    public JPanel getSettingsPanel() {
	JPanel panel = new JPanel(new GridLayout(0, 5));
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
     * This function does the "heavy work", calling the native code
     */
    public void layoutPartion (LayoutPartition part) {

	// Show message on the task monitor
	taskMonitor.setStatus("Initializing: Partition: " + part.getPartitionNumber());
	
	// The completed percentage is indeterminable
	taskMonitor.setPercentCompleted(-1);
	
	// Calls initialize_local	      
	initialize(); 
		
	// Get the number of edges and nodes
	int numNodes = part.nodeCount();
	int numEdges = part.edgeCount();
	
	// Get node's list
	List<LayoutNode> nodeList = part.getNodeList();

	// Allocate memory for storing graph edges information (to be used as arguments for JNI call)
	int[] AdjMatIndex = new int[numNodes + 1];
	int[] AdjMatVals  = new int[2 * numEdges];	
	
	// Create an iterator for processing the nodes
	Iterator<LayoutNode> it = nodeList.iterator();
	
	// Auxiliary variables
	int position = 0;
	int currentNodePosition = 0;

	// Iterate over all nodes
	while (it.hasNext()){
	    
	    // Check whether it has been canceled by the user
	    if (canceled)
		return;

	    // Get next node
	    LayoutNode node = (LayoutNode) it.next();

	    // Get index of node
	    int currentNodeIndex = network.getIndex(node.getNode());

	    // Set AdjMatIndex[current_node_index] to point to start of neighbors list of this node
	    AdjMatIndex[currentNodePosition] = position;
	    
	    // Get neighbors of node
	    List<LayoutNode> neighbors = node.getNeighbors();
	    
	    // Process neighbors of node, adding them in AdjMatVals
	    Iterator<LayoutNode> neighborsIterator = neighbors.iterator();
	    while (neighborsIterator.hasNext()){

		// Get the next neighbor
		LayoutNode currentNeighbor = (LayoutNode) neighborsIterator.next();

		// Get the position in nodeList of neighbor
		int currentNeighborPosition = nodeList.indexOf(currentNeighbor);
			    
		//logger.debug("Adding node " + currentNeighborPosition + " as neighbor of node " + currentNodePosition + " in position " + position);

		// Add alias of current_neighbor to AjdMatVals
		AdjMatVals[position] = currentNeighborPosition;
		    
		// Increment position
		position++;				       
	    }
	    
	    // Increment currentNodePosition
	    currentNodePosition++;	    
	}
	
	// Mark end of AdjMatIndex, so that you can now where ends AdjMatVals  
	AdjMatIndex[currentNodePosition] = position;
	
	// Check whether it has been canceled by the user
	if (canceled)
	    return;

	// Reset the "sys_paths" field of the ClassLoader to null.
	Class clazz = ClassLoader.class;
	Field field;
	try {
	    field = clazz.getDeclaredField("sys_paths");
	    boolean accessible = field.isAccessible();
	    if (!accessible)
		field.setAccessible(true);
	    Object original = field.get(clazz);
	    // Get original PATH
	    String orig_path = System.getProperty("java.library.path");
	    
	    // Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value
	    field.set(clazz, null);
	    try {

		Map<String, String> env = System.getenv();


		// Change the value and load the library.
		System.setProperty("java.library.path", "./plugins" + ":" + CUDA_PATH + "/lib" + ":" + orig_path);
		System.loadLibrary("cudart");
		System.loadLibrary(GPU_LIBRARY);
	    }
	    catch (UnsatisfiedLinkError error){
		String message = "Problem detected while loading Static Library with Native Code\nCannot Produce Layout\n"		    
		    + error.getMessage() 
		    + "\nPlease check that CUDA instalation folder is correctly set in the menu \"Layouts->Settings->GpuLayout(ForceDirected)\"";
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
		
		// Revert back the changes
		field.set(clazz, original);
		field.setAccessible(accessible);
		
		// Exit
		return;
	    }		
	    finally {
		// Revert back the changes
		field.set(clazz, original);
		field.setAccessible(accessible);   
	    }
	}
	catch (Exception exception){
	    /*String message4 = "Problem detected while loading Static Library\nCannot Produce Layout\n"
		    + exception.getMessage();
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message4);
	    */
	}

	// Check whether it has been canceled by the user
	if (canceled)
	    return;      

	// Show message on the task monitor
	taskMonitor.setStatus("Calling native code: Partition: " + part.getPartitionNumber());
	
	// Make native method call
	float[][]node_positions = ComputeGpuLayout( AdjMatIndex, 
						    AdjMatVals, 
						    coarseGraphSize, 
						    interpolationIterations, 
						    levelConvergence, 
						    edgeLen, 
						    initialNoIterations,
						    H_SIZE,
						    V_SIZE 
						    );
	
	// Check whether it has been canceled by the user
	if (canceled)
	    return;
	
	// Update Node position
	//part.resetNodes(); // reset the nodes so we get the new average location

	// Iterate over all nodes
	int currentNode = 0;

	// Create an iterator for processing the nodes
	Iterator<LayoutNode> iterator2 = nodeList.iterator();

	while (iterator2.hasNext()){
	    
	    // Get next node
	    LayoutNode node = (LayoutNode) iterator2.next();
		
	    // Set node's X and Y positions
	    node.setX(node_positions[currentNode][0]);
	    node.setY(node_positions[currentNode][1]);

	    // Move node to desired location
	    part.moveNodeToLocation(node);

	    currentNode++;
	}
	
	//return;
    }// layoutPartion(LayoutPartition part)
    
    
    
    /**
     * Native method that computes the layout and returns position of nodes
     */
    private native 
	float[][] ComputeGpuLayout( int[] AdjMatIndexJ, 
				    int[] AdjMatValsJ, 
				    int coarseGraphSizeJ, 
				    int interpolationIterationsJ, 
				    int levelConvergenceJ, 
				    int edgeLenJ, 
				    int initialNoIterationsJ,
				    double hSizeJ,
				    double vsizeJ
				    );   
   

}
