package org.cytoscape.phylotree.layout.cladograms;

import org.cytoscape.phylotree.layout.CommonFunctions;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.AbstractLayout;


import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import giny.model.Edge;
import giny.model.Node;

public class SlantedCladogram extends AbstractLayout{
	
	static double LEAF_X = 500.0; // X Position where all leaves should be placed
	static double INTERNODE_DISTANCE = 100.0;
	
	private LayoutProperties layoutProperties;
	
	CommonFunctions commonFunctions = new CommonFunctions();
	
	private int numLeavesVisited = 0; //
	
	public SlantedCladogram()
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();		
	}
	
	protected void initialize_properties()
	{	
		layoutProperties.initializeProperties();

		updateSettings(true);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateSettings() {
		updateSettings(false);
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();
	}

	// UI for getting settings
	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}


	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}



	/**
	 * getName is used to construct property strings
	 * for this layout.
	 */
	public  String getName() {
		return "slanted_cladogram";
	}

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public  String toString(){
		return "Cladogram - Slanted";
	}
	public void construct(double distance)
	{
		INTERNODE_DISTANCE += distance;
		construct();
	}

	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); 

		if(commonFunctions.hasLeaf(network)&&commonFunctions.isTree(network))
		{
		// Find the root of the tree
		Node root = commonFunctions.getTreeRoot(network);
		// Remove bends

		List<Edge> allEdges = network.edgesList();
		Iterator<Edge> edgesIterator = allEdges.iterator();

		while(edgesIterator.hasNext())
		{

			Edge edge = edgesIterator.next();
			networkView.getEdgeView(edge).clearBends();
		}
		numLeavesVisited = 0;

		// Obtain post order traversal of nodes starting from the root
		List<Node> postOrderNodes = commonFunctions.postOrderTraverse(network, root);

		// Position each node
		Iterator<Node> it = postOrderNodes.iterator();
		while(it.hasNext())
		{
			Node node = it.next();
			
			// If leaf position it accordingly
			if(network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true).length == 0)
				positionLeaf(node);
			else
				positionInternalNode(node);
				
				
			
		}
	}
	else
		System.out.println("The "+getName()+" layout can only be applied to trees.");

	}
	
	
	/**
	 * positionLeaf(Node)
	 * Positions the leaves
	 */
	private void positionLeaf(Node node)
	{
		numLeavesVisited++;
		networkView.getNodeView(node).setXPosition(LEAF_X,true);
		networkView.getNodeView(node).setYPosition(INTERNODE_DISTANCE*numLeavesVisited, true);
		
	}
	
	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 */
	private void positionInternalNode(Node node)
	{
		// Find the child horizontally nearest
		// Get the leaves of the subtree rooted at node
		List<Node> subtreeLeaves = commonFunctions.getLeaves(network, node);

		// Find the vertical midpoint of the subtree rooted at node
		double midpointY = commonFunctions.findSubtreeYMidPoint(networkView, subtreeLeaves);

		// Set the positions
		networkView.getNodeView(node).setYPosition(midpointY,true);
		networkView.getNodeView(node).setXPosition(LEAF_X-((subtreeLeaves.size()-1))*INTERNODE_DISTANCE, true);


	}

	

}
