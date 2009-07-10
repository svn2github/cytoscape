/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
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

import giny.model.GraphPerspective;
import giny.model.Node;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Iterator;
import java.lang.reflect.Field;





/**
 * This plugin provides a GPU assited graph layout utility by calling CUDA C++ code
 */
public class GpuLayout extends CytoscapePlugin {

    // Default values for algorithm parameters	
    private int coarseGraphSize         = 50;
    private int interpolationIterations = 50;
    private int levelConvergence        = 2;
    private int edgeLen                 = 5;
    private int initialNoIterations     = 300;

    private LayoutProperties layoutProperties;

	
    /**
     * 
     */
    public GpuLayout() {	
	// Add Layout to menu
	CyLayouts.addLayout(new ForceDirected(), "GPU Assisted Layout");
    }
    


    class ForceDirected extends AbstractLayout{

	/**
	 * Creates a new layout object.
	 */
	public ForceDirected() {
	    super();
	    layoutProperties = new LayoutProperties(getName());
	    initialize_properties();
	}

	/**
	 * Adds tunable objects for adjusting plugin parameters
	 * Initializes default values for those parameters
	 */
	protected void initialize_properties() {
	    
	    // Add new properties to layout 
	    layoutProperties.add(new Tunable("coarseGraphSize", "Coarse Graph Size "               , Tunable.INTEGER, new Integer(50) ));
	    layoutProperties.add(new Tunable("interpolationIterations", "Interpolation Iterations ", Tunable.INTEGER, new Integer(50) ));
	    layoutProperties.add(new Tunable("levelConvergence", "Level Convergence "              , Tunable.INTEGER, new Integer(2)  ));
	    layoutProperties.add(new Tunable("edgeLen", "Ideal Edge Length "                       , Tunable.INTEGER, new Integer(5)  ));
	    layoutProperties.add(new Tunable("initialNoIterations", "Initial Number of Iterations ", Tunable.INTEGER, new Integer(300)));
	    
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
	    
	    //  Show message on screen    
	    //  String message = "Preferences updated\n" 
	    //	+  coarseGraphSize + "\n" 
	    //  + interpolationIterations + "\n"  
	    //  + levelConvergence + "\n"         
	    //  + edgeLen + "\n"                  
	    //  + initialNoIterations; 

	    // Use the CytoscapeDesktop as parent for a Swing dialog
	    // JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	    
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
	public void construct() {

	    // Show message on the task monitor
	    taskMonitor.setStatus("Initializing");

	    // The completed percentage is indeterminable
	    taskMonitor.setPercentCompleted(-1);

	    // Calls initialize_local	      
	    initialize(); 
	    

	    
	    // Pack the arguments needed for calling native code
	    
	    // Get the number of edges and nodes
	    int numNodes = network.getNodeCount();
	    int numEdges = network.getEdgeCount();

	    // Allocate memory for storing graph edges
	    int[] AdjMatIndex = new int[numNodes + 1];
	    int[] AdjMatVals  = new int[2 * numEdges];



	    // Initialize mapping beetwen aliases and node's ID
	    int[] node_map = new int[numNodes];

	    // Create an iterator for processing the nodes
	    Iterator<Node> it = network.nodesIterator();

	    // Auxiliary variable to keep track of nodes aliases
	    int alias = 0;

	    // Iterate over the nodes
	    while (it.hasNext()) {

		// Get next node
		Node node = (Node) it.next();

		// Add alias and node ID to node_map
		node_map[alias] = network.getIndex(node);

		// Increment alias
		alias++;
	    }

	    // Check whether it has been canceled by the user
	    if (canceled)
		return;

	    // Auxiliary variable used to keep track of the position in AdjMatVals
	    int position = 0;
	    	    
	    // Iterate over all nodes
	    for (alias = 0; alias < numNodes; alias++){

		// Set AdjMatIndex[alias] to point to start of neighbors list of this node
		AdjMatIndex[alias] = position;

		// Get current node's index
		int current_node_index = node_Alias2Index(node_map, alias);

		// Get neighbors of node
		int[] neighbors = network.neighborsArray(current_node_index);

		// Process neighbors of node, adding them in AdjMatVals
		for (int i = 0; i < neighbors.length; i++){
		    int current_neighbor_index = neighbors[i];

		    // Take into account both directed (in both directions) and undirected edges
		    int multiplicity = network.getEdgeCount(current_node_index, current_neighbor_index, true) 
			             + network.getEdgeCount(current_neighbor_index, current_node_index, false);

		    // Add current_neighbor to AdjMatVals "multiplicity" times
		    for (int j = 0; j < multiplicity; j++){

			    // Add alias of current_neighbor to AjdMatVals
			    AdjMatVals[position] = node_Index2Alias(node_map, current_neighbor_index);

			    // Increment position
			    position++;
		    }
		    
		}
		  			     			     			    			     
	    }

	    // Mark end of AdjMatIndex, so that you can now where ends AdjMatVals  
	    AdjMatIndex[alias] = position;
		

	    //  Show message on screen with AdjMatIndex and AdjMatVals   
	    /*String message2 = "AdjMatIndex\n"; 
	    for (int i = 0; i < AdjMatIndex.length; i++)
		message2 = message2 + " " +AdjMatIndex[i];
	    message2 = message2 + "\nAdhMatVals\n";
	    for (int i = 0; i < AdjMatVals.length; i++)
		message2 = message2 + " " +AdjMatVals[i];	    
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message2);
	    */

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
		// Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value
		field.set(clazz, null);
		try {
		    // Change the value and load the library.
		    System.setProperty("java.library.path", CY_PLUGIN_PATH);
		    System.loadLibrary(GPU_LIBRARY);
		}
		catch (UnsatisfiedLinkError error){
		    String message = "Problem detected while loading Static Library with Native Code\n Cannot Produce Layout\n"
			           + error.getMessage();
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
	    catch (Exception E){}

	    // Check whether it has been canceled by the user
	    if (canceled)
		return;

	    // Show message on the task monitor
	    taskMonitor.setStatus("Calling native code...");

	    // Make native method call
	    int[][]node_positions = ComputeGpuLayout( AdjMatIndex, 
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

	    // Iterate over all nodes
	    for (alias = 0; alias < numNodes; alias++){

		// Get current node's index
		int current_node_index = node_Alias2Index(node_map, alias);

		// Get current node
		Node node = network.getNode(current_node_index);
		
		// Set node's X and Y positions
		networkView.getNodeView(node).setXPosition(node_positions[alias][0]);
		networkView.getNodeView(node).setYPosition(node_positions[alias][1]);	     

	    }

	    // (UPDATE NODES?????) I don't know if that will be required..

	    return;
	}
	
	private double H_SIZE = 1000.0;
	private double V_SIZE = 1000.0;
	private String GPU_LIBRARY = "GpuLayout";
	private String CY_PLUGIN_PATH = "/home/gerardo/Cytoscape_v2.6.2/plugins";
    }


    /**
     * Get the index of a node in node_map, given the alias
     */
    public int node_Alias2Index(int[] map, int alias){
	    return (map[alias]);
    }


    /**
     * Get the alias (ordinal value) of a node in a node_map
     */
    public int node_Index2Alias(int[] map, int index){
	for (int i = 0; i < map.length; i++)
	    if (map[i] == index)
		return i;
	return -1;
    }

    
    /**
     * Native method that computes the layout and returns position of nodes
     */
    private native 
	int[][] ComputeGpuLayout( int[] AdjMatIndexJ, 
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








