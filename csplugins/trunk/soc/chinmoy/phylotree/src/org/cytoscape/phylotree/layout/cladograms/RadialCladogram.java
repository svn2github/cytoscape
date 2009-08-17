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

public class RadialCladogram extends AbstractLayout{
	
	static double BASE_RADIUS = 100.0;
	
	private LayoutProperties layoutProperties;
	
	CommonFunctions commonFunctions = new CommonFunctions();
	
	private int numLeavesVisited = 0; //
	
	public RadialCladogram()
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
		return "radial_cladogram";
	}

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public  String toString(){
		return "Cladogram - Radial";
	}
	
	public void construct(double radius)
	{
		BASE_RADIUS = radius;
		construct();
	}

	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); 
		
		if(commonFunctions.hasLeaf(network)&&commonFunctions.isTree(network))
		{
		
		// Remove bends

		List<Edge> allEdges = network.edgesList();
		Iterator<Edge> edgesIterator = allEdges.iterator();

		while(edgesIterator.hasNext())
		{

			Edge edge = edgesIterator.next();
			networkView.getEdgeView(edge).clearBends();
		}

		// Find the root of the tree
		Node root = commonFunctions.getTreeRoot(network);

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

		double angle = 2.0 * Math.PI * ((double)numLeavesVisited)/((double)commonFunctions.getNumberOfLeaves(network));

		double radius = BASE_RADIUS * (double)commonFunctions.getLevel(network, commonFunctions.getTreeRoot(network));

		// Get the planar co-ordinates from the polar co-ordinates
		double nodeX = radius * Math.cos(angle);
		double nodeY = radius * Math.sin(angle); 
		
		
		networkView.getNodeView(node).setXPosition(nodeX,true);
		networkView.getNodeView(node).setYPosition(nodeY, true);	
		
	}
	
	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 */
	private void positionInternalNode(Node node)
	{
		int rootLevel = commonFunctions.getLevel(network, commonFunctions.getTreeRoot(network));
		int nodeLevel = commonFunctions.getLevel(network, node);
		double radius = BASE_RADIUS*(double)(rootLevel - nodeLevel);
		double meanSine = 0.0;
		double meanCosine = 0.0;

		int numChildren = 0;

		// Get all sub leaves
		List<Node> subtreeLeaves = commonFunctions.getLeaves(network, node);
		Iterator<Node> leafIterator = subtreeLeaves.iterator();

		// Find the mean of the angle of all the leaves of the subtree
		while(leafIterator.hasNext())
		{
			Node leaf = leafIterator.next();
			double childX = networkView.getNodeView(leaf).getXPosition();
			double childY = networkView.getNodeView(leaf).getYPosition();
			double angle = Math.atan2(childY, childX);


			// Mean angles require mean sine and cosines
			meanSine = meanSine + Math.sin(angle);
			meanCosine = meanCosine+Math.cos(angle);


			numChildren++;	
		}

		double meanAngle = 0.0;
		if(numChildren>0)
			meanAngle = Math.atan2(meanSine/(double)numChildren, meanCosine/(double)numChildren);

		// Translate polar to planar co-ordinates
		double nodeX = radius * Math.cos(meanAngle); 
		double nodeY = radius * Math.sin(meanAngle); 

		
		// Position
		networkView.getNodeView(node).setXPosition(nodeX,true);
		networkView.getNodeView(node).setYPosition(nodeY, true);


	}

	

}
