//Updated

package org.cytoscape.phylotree.layout;

import cytoscape.layout.LayoutProperties;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.Tunable;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

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
	
	private int numLeavesVisited = 0; //
	
	
	private String[] cladogramTypes = {"Rectangular", "Slanted", "Radial"};
	
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
				Tunable.LIST, new Integer(0), (Object)cladogramTypes,(Object) null, 0));

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
		    if (((Integer) t.getValue()).intValue() == 0)
		      {
		    	rectangular = true;
		    	slanted = false;
		    	radial = false;
		      }
		    else if (((Integer) t.getValue()).intValue() == 1)
		    {
		    	rectangular = false;
		    	slanted = true;
		    	radial = false;
		      }
		    else if (((Integer) t.getValue()).intValue() == 2)
		    {
		    	rectangular = false;
		    	slanted = false;
		    	radial = true;
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
		taskMonitor.setStatus("Initializing");
		initialize(); 
		// Find the root of the tree
		Node root = getTreeRoot();

		
		
		// Traverse and position each node starting from the root
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


		if(outgoingEdgesArray.length!=0)
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
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
			positionLeaf(node);
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
}