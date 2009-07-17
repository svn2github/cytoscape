package org.cytoscape.phylotree.layout;

import cytoscape.Cytoscape;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.Tunable;
import cytoscape.data.CyAttributes;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import giny.model.Node;
import giny.model.Edge;
public class PhylogramLayout extends AbstractLayout {


	static double LEAF_X = 500.0; // X Position where all leaves should be 
	static double INTERNODE_DISTANCE = 100.0;


	private LayoutProperties layoutProperties;

	private boolean constantBranches; // Whether or not branch lengths are reflected
	private boolean branchFlag = false;
	private int numLeavesVisited = 0;
	private int layerNum = 0;
	public PhylogramLayout()
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();		
	}

	protected void initialize_properties()
	{	
		layoutProperties.add(new Tunable("constant_branches",
				"Ignore Branch Lengths",
				Tunable.BOOLEAN, new Boolean(false)));

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

		Tunable t = layoutProperties.get("constant_branches");
		if ((t != null) && (t.valueChanged() || force))
			constantBranches = ((Boolean) t.getValue()).booleanValue();


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
		return "Phylogram Layout";
	}

	/**
	 * toString is used to get the user-visible name
	 * 1of the layout
	 */
	public  String toString(){
		return "Phylogram";
	}
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); 
		// Find the root of the tree
		Node root = getTreeRoot();

		
		if(!constantBranches)
			{
				branchFlag = true;
				constantBranches = true;
			}
		// Traverse and position each node starting from the root
		traverse(root);
		
		
		
		// If branch lengths are provided, update the network view
		if(branchFlag)
		{
			constantBranches = false;
			branchFlag = false;
			traversePreOrder(root);
			
		}

	}

	// Methods used by construct //

	/**
	 * getTreeRoot()
	 * Finds the root of the tree and returns it
	 */
	private Node getTreeRoot()
	{
		// Get all nodes
		List<Node> nodesList = network.nodesList();
		Iterator <Node> nodesListIterator = nodesList.iterator();

		while(nodesListIterator.hasNext())
		{
			Node node = nodesListIterator.next();

			// Get all directed edges incident on the node 
			int[] incomingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);

			// If no incoming edges, it probably is the root of a tree
			if(incomingEdgesArray.length == 0)
				return node;
		}

		return null;

	}

	/**
	 * traverse(Node)
	 * Recursively performs a post-order traversal of tree from node arg
	 * Invokes positioning methods for nodes
	 */

	private void traverse(Node node)
	{

		// Find all outgoing edges
		int[] outgoingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);


		if(outgoingEdgesArray.length!=0)
		{
			// Traverse every child
			for(int i = 0; i<outgoingEdgesArray.length; i++)	
			{
				Edge edge = network.getEdge(outgoingEdgesArray[i]);

				traverse(edge.getTarget());
			}
			// Traverse the parent last
			positionInternalNode(node, constantBranches);
		}
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
			positionLeaf(node, constantBranches);
		}


	}
	
	/**
	 * traversePreOrder(Node)
	 * Recursively performs a post-order traversal of tree from node arg
	 * Invokes positioning methods for nodes
	 */

	private void traversePreOrder(Node node)
	{

		// Find all outgoing edges
		int[] outgoingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);


		if(outgoingEdgesArray.length!=0)
		{

			positionInternalNode(node, constantBranches);
			// Traverse every child
			for(int i = 0; i<outgoingEdgesArray.length; i++)	
			{
				Edge edge = network.getEdge(outgoingEdgesArray[i]);

				traversePreOrder(edge.getTarget());
			}
			// Traverse the parent last
		}
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
			positionLeaf(node, constantBranches);

		}


	}

	/**
	 * positionLeaf(Node)
	 * Positions the leaves
	 */
	private void positionLeaf(Node node, boolean constantBranch)
	{
		numLeavesVisited++;
		if(constantBranch)
			{
			networkView.getNodeView(node).setXPosition(LEAF_X,true);
			networkView.getNodeView(node).setYPosition(INTERNODE_DISTANCE*numLeavesVisited, true);
			}
			// Adjust the branchLengths
			if(!constantBranch && network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false).length!=0)
			{	double branchLength = getBranchLength(node);
				
				applyBranchLengths(node,branchLength);
				
			}
	}

	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 */
	private void positionInternalNode(Node node, boolean constantBranch)
	{
		layerNum = 1;
		// Find the child horizontally nearest
		// Get the leaves of the subtree rooted at node
		List<Node> subtreeLeaves = new LinkedList<Node>();
		getLeaves(node, subtreeLeaves);

		// Find the vertical midpoint of the subtree rooted at node
		double midpointY = findSubtreeYMidPoint(subtreeLeaves);

		// Set the positions
		if(constantBranch)
		{networkView.getNodeView(node).setYPosition(midpointY,true);
		networkView.getNodeView(node).setXPosition(LEAF_X-((subtreeLeaves.size()-1))*INTERNODE_DISTANCE, true);
		
		}
		// Adjust the branchLengths
		if(!constantBranch && network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false).length!=0)
			{	double branchLength = getBranchLength(node);
			
			applyBranchLengths(node,branchLength);
			
		}
	}


	/**
	 * getLeaves(Node, List<Node>)
	 * Recursively populates List with leaves on a path from Node
	 */
	private void getLeaves(Node node, List<Node> list)
	{
		// Get all children
		int[] edges = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);
		for(int i = 0; i<edges.length; i++)
		{
			Edge e = network.getEdge(edges[i]);
			Node child = e.getTarget();
			if(network.getAdjacentEdgeIndicesArray(child.getRootGraphIndex(), false, false, true).length == 0)
			{
				// If child is a leaf, add to list
				list.add(child);	
			}
			else
			{	
				// Otherwise, probe the subtree rooted at the child
				layerNum++;
				getLeaves(child,list);
			}

		}
	}



	/**
	 * Find the vertical midpoint of a list of leaves
	 */
	private double findSubtreeYMidPoint(List<Node> leaves)
	{

		Iterator<Node> it = leaves.iterator();
		Node firstLeaf = it.next();
		double highestY,lowestY;
		highestY = lowestY = networkView.getNodeView(firstLeaf).getYPosition();
		
		

		while(it.hasNext())
		{
			Node leaf = it.next();
			double leafY = networkView.getNodeView(leaf).getYPosition();
			if(leafY<lowestY)
			{
				lowestY = leafY;

			}
			if(leafY>highestY)
			{
				highestY = leafY;

			}	
		}
		return (highestY+lowestY)/2.0;
	}

	private double getBranchLength(Node node)
	{

		// Find all outgoing edges
		int[] incomingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);

		Double length = 0.0;
		if(incomingEdgesArray.length!=0)
		{
			Edge edge = network.getEdge(incomingEdgesArray[0]);

			CyAttributes att = Cytoscape.getEdgeAttributes();
			//Get length
			length = att.getDoubleAttribute(edge.getIdentifier(), "branchLength");


		}
		return length;
	}
	
	private void applyBranchLengths(Node node, double length)
	{
		double nodeXposition = networkView.getNodeView(node).getXPosition();
		double nodeYposition = networkView.getNodeView(node).getYPosition();
		int[] incomingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);

		// Scale the edge to find a new position for the node
		if(incomingEdgesArray.length!=0)
		{

			Edge edge = network.getEdge(incomingEdgesArray[0]);
			Node parent = edge.getSource();
			
			double parentXposition = networkView.getNodeView(parent).getXPosition();
			double parentYposition = networkView.getNodeView(parent).getYPosition();
			
			double slope = -1*(parentYposition - nodeYposition) /(parentXposition - nodeXposition);
			
			length = length *INTERNODE_DISTANCE*3.0;
			double scalingFactor = Math.sqrt((length*length)/((slope*slope)+1));
		

			networkView.getNodeView(node).setXPosition(parentXposition+(scalingFactor));
			networkView.getNodeView(node).setYPosition(parentYposition-(scalingFactor*slope*2.0));
		}
	}
}