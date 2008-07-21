import cytoscape.plugin.CytoscapePlugin;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;

import cytoscape.view.CyNetworkView;

import giny.view.NodeView;
import giny.view.Label;

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;
import java.awt.Color;

import cytoscape.data.Semantics;
/** ---------------------------AutomaticLabelLayout-----------------------------
 * Takes the current network and reorganizes it so that the new network is more
 * readable.  This will be done through the repositioning of network labels,
 * and subtle repositioning of nodes.
 * @author Victoria Mui
 *
 */
public class AutomaticLabelLayout extends CytoscapePlugin {
	
    /**
     * Constructor which adds this layout to Cytoscape Layouts.  This in turn
     * adds it to the Cytoscape menus as well.
     */
	public AutomaticLabelLayout() {
		// Add this layout to the Layout menu under Cytoscape Layouts.
		CyLayouts.addLayout(new AutomaticLabelAlgorithm(), 
				"Cytoscape Layouts");
	}
	
	
	/** ------------------------AutomaticLabelAlgorithm------------------------
	 * */
	public class AutomaticLabelAlgorithm extends AbstractLayout {
		
		LayoutProperties layoutProperties = null;
		
		// The attributes of all nodes in the network
		CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
		
		// The List of static nodes
		List<CyNode> staticNodes;
		
		// The List of dynamic nodes
		List<CyNode> dynamicNodes = new ArrayList<CyNode>();
	    
	    /** 
	     * Constructs a new Spring Embedded Algorithm object and initializes
	     * the layout properties.
	     */
	    public AutomaticLabelAlgorithm() {
			super();    
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.initializeProperties();
	    }
	    
	    /**
	     * Constructs a new network that is more readable:
	     * - Adjusts the labels appropriately
	     */
	    public void construct() {

	    	// Get the set of CyNodes --> static nodes
	    	staticNodes = Cytoscape.getCyNodesList();
	    	
	    	// Create label nodes for each of the node's labels --> dynamic nodes
	    	createLabelNodes(staticNodes);
	    	
	    	// Apply Force-directed algorithm to dynamic nodes
	    	System.out.println(network.getNodeCount());
	    	
	        // Remove label nodes and edges
	    	
	    } //end construct()
	    
	    
	    public void createLabelNodes(List<CyNode> nodes) {
	    	
	    	Iterator<CyNode> nodeIt = nodes.iterator();
	    	
	    	CyNode labelNode = null;
	    	
	    	while (nodeIt.hasNext()) {
	    		CyNode current = nodeIt.next();

	    		Appearance app = new Appearance();
	    		
	    		// Create a new label node
	    		labelNode = Cytoscape.getCyNode(current.toString() + "a", true);
	    		network.addNode(labelNode);
	    		
	    		// Replace the node's label with an empty string
	    		//current.setIdentifier(""); ---------------------------------------UNCOMMENT
	    		
	    		// Create a new edge between the node and its label node
	    		CyEdge edge = Cytoscape.getCyEdge(current.getIdentifier(), 
	    				"", labelNode.getIdentifier(), 
	    				Semantics.INTERACTION);
	    		network.addEdge(edge);
	    		dynamicNodes.add(labelNode);
	    		
	    		
	    		// Make label node's background and border transparent
	    		// ================== SAND-BOX =================
	    		CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
	    		nodeAtts.setAttribute(labelNode.getIdentifier(),
	    				"node.opacity","0");
	    		nodeAtts.setAttribute(labelNode.getIdentifier(),
	    				"node.borderOpacity","0");
	    		
	    		// Make the edge connecting the node and its node label transparent
	    		CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
	    		edgeAtts.setAttribute(edge.getIdentifier(), "edge.opacity", "0");
	    		
	    		// ================== NODE LABEL SAND-BOX =====================
	    		System.out.println("node ID: " + current.getIdentifier());
	    		System.out.println("label node ID: " + labelNode.getIdentifier());
	    	}
	    	System.out.println("createLabelNodes: " + network.getNodeCount());
	    	networkView.updateView();
	    	networkView.redrawGraph(true, true);
	    }
	    
	    
	    /** 
	     * Returns the name to construct property strings 
	     * for this layout with */
	    public String getName() {
			return "auto-label-layout";
		}
	    
		/** Return the user-visible name of this layout */
		public String toString() {
			return "Automatic Label Layout";
		}
		
	}
}