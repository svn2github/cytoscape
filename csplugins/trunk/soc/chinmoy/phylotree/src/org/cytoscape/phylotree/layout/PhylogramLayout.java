package org.cytoscape.phylotree.layout;

import cytoscape.Cytoscape;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.Tunable;
import cytoscape.data.CyAttributes;

import java.awt.GridLayout;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSlider;
import giny.model.Node;
import giny.model.Edge;
import giny.view.Bend;
public class PhylogramLayout extends AbstractLayout {


	static double LEAF_X = 500.0; // X Position where all leaves should be 
	static double INTERNODE_DISTANCE = 100.0;


	private LayoutProperties layoutProperties;

	// Indicators of the type of cladogram preferred
	private boolean rectangular = false;
	private boolean slanted = false;
	private boolean radial = false;
	private boolean circular = false;

	private int numLeavesVisited = 0; //

	private Object lower = "0";
	private Object upper = "100.0";
	private String[] phylogramTypes = {"Rectangular", "Slanted", "Radial","Circular"};
	
	private double scalingFactor;

	public PhylogramLayout()
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();		
	}

	protected void initialize_properties()
	{	
		layoutProperties.add(new Tunable("phylogram_type",
				"Type of Phylogram",
				Tunable.LIST, new Integer(0), (Object)phylogramTypes,(Object) null, 0));

//		layoutProperties.add(new Tunable("edge_scaling",
//				"Edge Length Scaling Factor", Tunable.DOUBLE, new Double(scalingFactor), 
//				(Object)10.0, (Object)200.0, Tunable.USESLIDER));
		
		layoutProperties.add(new Tunable("edge_scaling", "Edge scaling",
                Tunable.DOUBLE, new Double(scalingFactor)));
		
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

		Tunable t = layoutProperties.get("phylogram_type");
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
		
		 t = layoutProperties.get("edge_scaling");
		  if ((t != null) && (t.valueChanged() || force))
		    scalingFactor = ((Double) t.getValue()).doubleValue();

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
		return "phylogram";
	}

	/**
	 * toString is used to get the user-visible name
	 * 1of the layout
	 */
	public  String toString(){
		return "Phylogram Layout";
	}
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); 
		// Find the root of the tree
		Node root = getTreeRoot();

		
		
		// Traverse and set each node's Y co-ordinate starting from the root
		traverse(root);
		
		//Traverse and set each node's X co-ordinate starting form the root
		
		traversePreOrder(root);
		
		// If rectangular format is preferred, edit the edge shape by adding corner nodes
		if(rectangular || circular)
		{

			addRectangularBends();

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
				networkView.getEdgeView(edge).clearBends();
				
				traverse(edge.getTarget());
			}
			// Traverse the parent last
			if(rectangular || slanted)
				positionYInternalNode(node);
			else if(radial||circular)
				setAngleInternalNode(node);
			
			
			
		}
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
			if(rectangular || slanted)
				positionYLeaf(node);
			else if(radial||circular)
				setAngleLeaf(node);
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
			if(rectangular || slanted)
				positionX(node);
			else if(radial||circular)
				setRadius(node);

			
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
			if(rectangular || slanted)
				positionX(node);
			else if(radial||circular)
				setRadius(node);

		}


	}

	/**
	 * positionLeaf(Node)
	 * Positions the leaves
	 */
	private void positionYLeaf(Node node)
	{
		numLeavesVisited++;
		networkView.getNodeView(node).setYPosition(INTERNODE_DISTANCE * numLeavesVisited, true);
				
	}

	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 */
	private void positionYInternalNode(Node node)
	{
		// Find the child horizontally nearest
		// Get the leaves of the subtree rooted at node
		List<Node> subtreeLeaves = new LinkedList<Node>();
		getLeaves(node, subtreeLeaves);

		// Find the vertical midpoint of the subtree rooted at node
		double midpointY = findSubtreeYMidPoint(subtreeLeaves);

		// Set the positions
		networkView.getNodeView(node).setYPosition(midpointY,true);
		
	}
	
	private void setAngleInternalNode(Node node)
	{
		
	}

	private void setAngleLeaf(Node node)
	{
		
	}

	private void positionX(Node node)
	{
		if(network.getInDegree(node,false) == 0)
			networkView.getNodeView(node).setXPosition(0.0, false);
		
		int [] incomingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);
		if(incomingEdgesArray.length>0)
		{
		Edge edge = network.getEdge(incomingEdgesArray[0]);
		double parentX = networkView.getNodeView(edge.getSource()).getXPosition();
		
		double nodeX = parentX + (scalingFactor*getBranchLength(node));
		
		networkView.getNodeView(node).setXPosition(nodeX, false);
		
		}
		
	}
	
	private void setRadius(Node node)
	{
		
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