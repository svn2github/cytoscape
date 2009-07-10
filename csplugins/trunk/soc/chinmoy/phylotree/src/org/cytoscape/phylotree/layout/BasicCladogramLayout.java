package org.cytoscape.phylotree.layout;

import cytoscape.layout.AbstractLayout;
import java.util.Iterator;
import java.util.List;
import giny.model.Node;
import giny.model.Edge;

public class BasicCladogramLayout extends AbstractLayout {


	private int numLeavesVisited = 0;
	static double LEAF_Y_DISTANCE = 50.0;
	static double LEAF_X = 500.0;
	static double INTERNAL_X_DISTANCE = 100.0;
	
	public BasicCladogramLayout(){

		super();
		
	}

	/**
	 * getName is used to construct property strings
	 * for this layout.
	 */
	public  String getName() {
		return "Basic Cladogram Layout";
	}

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public  String toString(){
		return "Basic Cladogram";
	}
	public void construct() {

		//Find the root of the tree
		Node root = getTreeRoot();

		//Traverse and position each node starting from the root
		traverse(root);

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

		// Base case: if node is a leaf
		if(outgoingEdgesArray.length == 0)
		{
			positionLeaf(node);
		}
			
		else
		{
			// Traverse every child
			for(int i = 0; i<outgoingEdgesArray.length; i++)	
			{
				Edge edge = network.getEdge(outgoingEdgesArray[i]);
				traverse(edge.getTarget());
			}
			// Traverse the parent last
			positionInternalNode(node);
		}
	}
	
	/**
	 * positionLeaf(Node)
	 * Positions the leaves
	 */
	private void positionLeaf(Node node)
	{
		numLeavesVisited++;
		networkView.getNodeView(node).setXPosition(LEAF_X);
		networkView.getNodeView(node).setYPosition(LEAF_Y_DISTANCE*numLeavesVisited);
	
	}
	
	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 */
	private void positionInternalNode(Node node)
	{
		double childYSum = 0.0;
		double numChildren = 0.0;
		double childXNearest = Double.MAX_VALUE;
		double childX = 0.0;
		// Find all outgoing edges
		int[] outgoingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);

		// For each child, add up the Y positions to calculate the mean
		for(int i = 0; i<outgoingEdgesArray.length; i++)	
		{
			Edge edge = network.getEdge(outgoingEdgesArray[i]);
			Node child = edge.getTarget();
			numChildren++;
			childYSum = childYSum+networkView.getNodeView(child).getYPosition();
			
			// Also find smallest child X position, to find the nearest child
			childX = networkView.getNodeView(child).getXPosition();
			if(childX<childXNearest)
				childXNearest = childX;
		}
		// Set the Y position as the mean of Y positions of the children
		// Set the X position as a constant from the nearest child
		networkView.getNodeView(node).setYPosition(childYSum/numChildren);
		networkView.getNodeView(node).setXPosition(childXNearest-INTERNAL_X_DISTANCE);
	}

}