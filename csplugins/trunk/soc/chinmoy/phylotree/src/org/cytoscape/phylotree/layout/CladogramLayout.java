//Updated to adjust reticulate edges

package org.cytoscape.phylotree.layout;

import cytoscape.layout.LayoutProperties;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.Tunable;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import java.awt.geom.Point2D;
import giny.view.Bend;
import giny.model.Node;
import giny.model.Edge;

public class CladogramLayout extends AbstractLayout {


	static double LEAF_X = 500.0; // X Position where all leaves should be placed
	static double INTERNODE_DISTANCE = 100.0;


	private LayoutProperties layoutProperties;


	// Indicators of the type of cladogram preferred
	private boolean rectangular = false;
	private boolean slanted = false;
	private boolean radial = false;
	private boolean circular = false;

	private int numLeavesVisited = 0; //


	private String[] cladogramTypes = {"Rectangular", "Slanted", "Radial","Circular"};

	public CladogramLayout()
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();		
	}

	protected void initialize_properties()
	{	
		layoutProperties.add(new Tunable("cladogram_type",
				"Type of Cladogram",
				Tunable.LIST, new Integer(2), (Object)cladogramTypes,(Object) null, 0));

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

		Tunable t = layoutProperties.get("cladogram_type");
		if ((t != null) && (t.valueChanged() || force)) {
			if (((Integer) t.getValue()).intValue() == 0 && rectangular == false)
			{
				rectangular = true;
				slanted = false;
				radial = false;
				circular = false;
			}
			else if (((Integer) t.getValue()).intValue() == 1 && slanted == false)
			{
				rectangular = false;
				slanted = true;
				radial = false;
				circular = false;
			}
			else if (((Integer) t.getValue()).intValue() == 2 && radial == false)
			{
				rectangular = false;
				slanted = false;
				radial = true;
				circular = false;
			}
			else if (((Integer) t.getValue()).intValue() == 3 && circular == false)
			{
				rectangular = false;
				slanted = false;
				radial = false;
				circular = true;
			}
		}


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
		return "cladogram";
	}

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public  String toString(){
		return "Cladogram Layout";
	}
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); 


		// Find the root of the tree
		Node root = getTreeRoot();

		numLeavesVisited = 0;
		// Traverse and position each node starting from the root

		traverse(root);


		// If rectangular format is preferred, edit the edge shape by adding corner nodes
		if(rectangular || circular)
		{

			addRectangularBends();

		}




		// Order subtrees to prevent crossings
		traversePreOrder(root);


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

			// If no incoming edges, it probably is the root of a tree
			if(network.getInDegree(node, false)== 0)
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


				// Reset the bends 
				networkView.getEdgeView(edge).clearBends();

				traverse(edge.getTarget());
			}
			// Traverse the parent last
			if(rectangular || slanted)
				positionInternalNode(node);
			else if(radial||circular)
				positionInternalNodeRadial(node);
		}
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
			if(rectangular || slanted)
				positionLeaf(node);
			else if(radial||circular)
				positionLeafRadial(node);
		}


	}


	private void traversePreOrder(Node node)
	{

		// Find all outgoing edges
		int[] outgoingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);


		if(outgoingEdgesArray.length!=0)
		{
			if(network.getInDegree(node,false)>1)
				orderNode(node);

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
			if(network.getInDegree(node,false)>1)
				orderNode(node);


		}


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
	 * Position leaves of the circle based cladograms
	 * @param node - the node to be positioned
	 */
	private void positionLeafRadial(Node node)
	{
		double angle = 2.0 * Math.PI * ((double)numLeavesVisited)/((double)getNumberOfLeaves());

		double radius = 50.0 * (double)getLevel(getTreeRoot());

		// Get the planar co-ordinates from the polar co-ordinates
		double nodeX = radius * Math.cos(angle);
		double nodeY = radius * Math.sin(angle); 

		networkView.getNodeView(node).setXPosition(nodeX,true);
		networkView.getNodeView(node).setYPosition(nodeY, true);	

		numLeavesVisited++;

	}

	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 */
	private void positionInternalNode(Node node)
	{
		// Find the child horizontally nearest
		// Get the leaves of the subtree rooted at node
		List<Node> subtreeLeaves = new LinkedList<Node>();
		getLeaves(node, subtreeLeaves);

		// Find the vertical midpoint of the subtree rooted at node
		double midpointY = findSubtreeYMidPoint(subtreeLeaves);

		// Set the positions
		networkView.getNodeView(node).setYPosition(midpointY,true);
		networkView.getNodeView(node).setXPosition(LEAF_X-((subtreeLeaves.size()-1))*INTERNODE_DISTANCE, true);


	}


	/**
	 * Position internal Nodes of the circle based cladograms
	 * @param node - the node to be positioned
	 */
	private void positionInternalNodeRadial(Node node)
	{
		double radius = 50*((double)(getLevel(getTreeRoot()) - getLevel(node)));
		double meanSine = 0.0;
		double meanCosine = 0.0;

		int numChildren = 0;

		List<Node> subtreeLeaves = new LinkedList<Node>();
		getLeaves(node, subtreeLeaves);
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

		double nodeX = radius * Math.cos(meanAngle); 
		double nodeY = radius * Math.sin(meanAngle); 

		networkView.getNodeView(node).setXPosition(nodeX,true);
		networkView.getNodeView(node).setYPosition(nodeY, true);



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
			if(network.getOutDegree(child, false) == 0)
			{
				// If child is a leaf, add to list
				list.add(child);	
			}
			else
			{	
				// Otherwise, probe the subtree rooted at the child

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




	/**
	 * Adds the bends to make the edges look rectangular
	 */
	private void addRectangularBends()
	{
		// Get all edges

		List<Edge> allEdges = network.edgesList();
		Iterator<Edge> edgesIterator = allEdges.iterator();

		while(edgesIterator.hasNext())
		{

			Edge edge = edgesIterator.next();

			Node source = edge.getSource();
			Node target = edge.getTarget();
			// Check if the target is a reticulate node (indegree>1)
			// If yes, don't bend the edge
			if(network.getInDegree(target.getRootGraphIndex(), false) <= 1)
			{
				// For each edge, get the source node's X position
				double cornerX = networkView.getNodeView(source).getXPosition(); 

				// For each edge, get the target node's Y position
				double cornerY = networkView.getNodeView(target).getYPosition();


				if(rectangular)
				{	
					// Bend the edge
					Bend rectangularBend = networkView.getEdgeView(edge).getBend();
					rectangularBend.addHandle(new Point2D.Double(cornerX, cornerY));
				}
				else if(circular)
				{
					// Get the radius of the source
					double radius = (INTERNODE_DISTANCE/2.0)*(getLevel(getTreeRoot()) - getLevel(source));
					
					// And the angle of the target
					double angle = Math.atan2(networkView.getNodeView(target).getYPosition(), networkView.getNodeView(target).getXPosition());
					
					//Bend the edge
					Bend circularBend = networkView.getEdgeView(edge).getBend();
					
					circularBend.addHandle(new Point2D.Double(radius*Math.cos(angle),radius*Math.sin(angle)));
				}
			}	



		}
	}

	/**
	 * Creates a topological ordering of the subtrees that prevents edge crossing
	 * @param node - A reticulate node
	 */
	private void orderNode(Node node)
	{


	}

	/**
	 * Calculate the number of leaves in the tree
	 * @return - the number of leaves in the tree
	 */
	private int getNumberOfLeaves()
	{
		int numLeaves = 0;
		List<Node> allNodes = network.nodesList();
		Iterator<Node> nodesIterator = allNodes.iterator();

		while (nodesIterator.hasNext())
		{
			Node node = nodesIterator.next();
			if(network.getOutDegree(node, false) == 0)
			{
				numLeaves++;
			}
		}

		return numLeaves;
	}

	/**
	 * Find the level of the node (Leaves are level 0)
	 * @param node - the node whose level is to be found
	 * @return the level of the node
	 */
	private int getLevel(Node node)
	{
		if(network.getOutDegree(node, false) == 0)
			return 0;
		else
		{
			int max = 0;
			int [] outGoingEdges = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);
			for (int i = 0; i < outGoingEdges.length; i++)
			{
				int level = getLevel(network.getEdge(outGoingEdges[i]).getTarget()); 
				if(level > max)
					max = level;

			}

			return max+1;
		}
	}


}