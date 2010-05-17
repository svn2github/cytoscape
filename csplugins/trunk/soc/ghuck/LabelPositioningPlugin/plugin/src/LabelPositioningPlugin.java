package LabelPositioningPlugin;

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
import cytoscape.ding.DingNetworkView;
import cytoscape.layout.CyLayouts;

import giny.view.NodeView;
import giny.view.Label;
import cytoscape.view.CyNodeView;

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.LabelPosition;

import java.awt.Color;

import cytoscape.data.Semantics;
/** ---------------------------AutomaticLabelLayout-----------------------------
 * Takes the current network and reorganizes it so that the new network is more
 * readable.  This will be done through the repositioning of network labels,
 * and subtle repositioning of nodes.
 * @author Victoria Mui
 *
 */
public class LabelPositioningPlugin extends CytoscapePlugin {
	
    /**
     * Constructor which adds this layout to Cytoscape Layouts.  This in turn
     * adds it to the Cytoscape menus as well.
     */
	public LabelPositioningPlugin() {
		// Add this layout to the Layout menu under Cytoscape Layouts.
		CyLayouts.addLayout(new LabelForceDirectedLayout(), 
				"Cytoscape Layouts");
	}
	
	
	/** ------------------------AutomaticLabelAlgorithm------------------------
	 * */
	public class AutomaticLabelAlgorithm extends AbstractLayout {
		
		LayoutProperties layoutProperties = null;
		
		// The attributes of all nodes in the network
		CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
		
		DingNetworkView dingNetView = new DingNetworkView(
				Cytoscape.getCurrentNetwork(), 
				Cytoscape.getCurrentNetwork().getTitle());
		
		// The List of static nodes
		List<CyNode> staticNodes;
		
		// The List of static edges
		List<CyNode> staticEdges;
		
		// The List of dynamic nodes
		List<CyNode> dynamicNodes = new ArrayList<CyNode>();
		
		// The List of dynamic edges
		List<CyEdge> dynamicEdges = new ArrayList<CyEdge>();
	    
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
	    	
	    	// Get the set of CyEdges --> static edges
	    	staticEdges = Cytoscape.getCyEdgesList();
	    	
	    	// Print the set of all static nodes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<REMOVE
	    	for(CyNode n : staticNodes) {
	    		System.out.println("StaticNode: " + n.getIdentifier());
	    	}
	    	
	    	// Create label nodes for each of the node's labels --> dynamic nodes
	    	createLabelNodes(staticNodes);

	    	// Print the set of all dynamic nodes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<REMOVE
	    	for (CyNode m : dynamicNodes) {
	    		System.out.println("DynamicNode: " + m.getIdentifier());
	    	}
	    	
	    	// Apply Force-directed algorithm to dynamic nodes while locking static nodes
	    	CyNode[] staticNodeArr = new CyNode[staticNodes.size()];
	    	CyEdge[] staticEdgeArr = new CyEdge[staticEdges.size()];
	    	dingNetView.applyLockedLayout(CyLayouts.getLayout("force-directed"),
	    			staticNodes.toArray(staticNodeArr), 
	    			staticEdges.toArray(staticEdgeArr));
	        	    	
	    	// Remove the label nodes and reposition the node labels so that
	    	// they are located at where the node's corresponding label node
	    	// is currently located.
	    	removeLabelNodes();
	    	
	    	// Print the set of all static nodes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<REMOVE
	    	for(CyNode n : staticNodes) {
	    		System.out.println("FINAL StaticNode: " + n.getIdentifier());
	    	}

	    	// Print the set of all dynamic nodes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<REMOVE
	    	for (CyNode m : dynamicNodes) {
	    		System.out.println("FINAL DynamicNode: " + m.getIdentifier());
	    	}
	    	
	    	System.out.println("FINAL NUM NODES: " + network.getNodeCount());
	    	
	    	// Clean up the lists of static and dynamic nodes and edges
	    	clear();
	    	
	    } //end construct()
	    
	    /**
	     * Creates label nodes and edges, which connect nodes with their
	     * corresponding label nodes.
	     * @param nodes
	     */
	    public void createLabelNodes(List<CyNode> nodes) {
	    	
	    	Iterator<CyNode> nodeIt = nodes.iterator();
	    	
	    	CyNode labelNode = null;
	    	
	    	while (nodeIt.hasNext()) {
	    		CyNode current = nodeIt.next();
    			
    			// Create a new CyNode for the label
    			labelNode = Cytoscape.getCyNode("child_" + current.getIdentifier(), 
    					true);
	    		
	    		// Add the label node to the current network
	    		network.addNode(labelNode);
	    		
	    		// Create a new edge between the node and its label node
	    		CyEdge edge = Cytoscape.getCyEdge(current.getIdentifier(), 
	    				"", labelNode.getIdentifier(), 
	    				Semantics.INTERACTION);
	    		network.addEdge(edge); // Add new edge to the current network
	    		
	    		// Add the newly created label node and edge to the list
	    		// of dynamicNodes and dynamicEdges respectively.
	    		dynamicNodes.add(labelNode);
	    		dynamicEdges.add(edge);
	    	}
	    	networkView.updateView();
	    	networkView.redrawGraph(true, true);
	    }
	    
	    /**
	     * Removes all label nodes and edges, and repositions their parent 
	     * node's label at where the label node currently is
	     */
	    public void removeLabelNodes() {
	    	
    		nodeAtts = Cytoscape.getNodeAttributes();
	    	LabelPosition lp;    	
	    	int count = 0;
	    	
	    	// - Remove all label nodes
	    	// - Relocate all node's labels to corresponding label node's
	    	// current position
	    	for(CyNode n : dynamicNodes) {
	    		CyNode parentNode = staticNodes.get(count);
	    		
	    		NodeView parentView = networkView.getNodeView(
	    				parentNode.getRootGraphIndex());
	    		
	    		NodeView nView = networkView.getNodeView(n.getRootGraphIndex());
	    		
	    		lp = new LabelPosition();
	    		
	    		lp.setOffsetX(nView.getXPosition() - parentView.getXPosition());
	    		lp.setOffsetY(nView.getYPosition() - parentView.getYPosition());
	    		
	    		nodeAtts.setAttribute(parentNode.getIdentifier(), 
	    				"node.labelPosition", lp.shortString());
	    		
	    		// Remove the label node
	    		network.removeNode(n.getRootGraphIndex(), true);
	    		count++;
	    	}
	    	
	    	// Remove all label edges
	    	for(CyEdge e : dynamicEdges) {
	    		network.removeEdge(e.getRootGraphIndex(), true);
	    	}

	    	networkView.updateView();
	    	networkView.redrawGraph(true, true);
	    }
	    
	    
	    /**
	     * Clears the dynamic and static lists of nodes and edges
	     */
	    public void clear() {
	    	staticNodes.clear();
	    	staticEdges.clear();
	    	dynamicNodes.clear();
	    	dynamicEdges.clear();
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