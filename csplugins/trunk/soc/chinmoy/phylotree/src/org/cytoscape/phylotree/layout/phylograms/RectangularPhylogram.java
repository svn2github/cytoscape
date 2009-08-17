package org.cytoscape.phylotree.layout.phylograms;

import cytoscape.layout.LayoutProperties;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.Tunable;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;
import org.cytoscape.phylotree.layout.CommonFunctions;

import javax.swing.JPanel;
import giny.model.Node;
import giny.model.Edge;


public class RectangularPhylogram extends AbstractLayout{
	
	static double LEAF_X = 500.0; // X Position where all leaves should be 
	static double INTERNODE_DISTANCE = 100.0;


	private int numLeavesVisited = 0; //
	private LayoutProperties layoutProperties;
	private double scalingFactor = 0.0;
	
	private CommonFunctions commonFunctions; 
	
	public RectangularPhylogram()
	{
		
		super();

		layoutProperties = new LayoutProperties(getName());
		initialize_properties();

	}
	protected void initialize_properties()
	{	
		layoutProperties.add(new Tunable("edge_scaling", "Edge scaling",
                Tunable.DOUBLE, new Double(scalingFactor = 500.0)));
		
//		layoutProperties.add(new Tunable("edge_scaling", "Edge scaling", Tunable.STRING, 
//				new Double(scalingFactor = 500.0), new Double(100.0),
//				new Double(1000.0), Tunable.USESLIDER));
		
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

		Tunable t = layoutProperties.get("edge_scaling");
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
		return "rectangular_phylogram";
	}

	/**
	 * toString is used to get the user-visible name
	 * 1of the layout
	 */
	public  String toString(){
		return "Phylogram - Rectangular";
	}

	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); 
		
		// Intialize the common functions
		commonFunctions = new CommonFunctions();
		
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
		
		// Get post Order Traversal of nodes in tree
		List<Node> postOrderNodes = commonFunctions.postOrderTraverse(network, root);
		Iterator<Node> postOrderIterator = postOrderNodes.iterator();
		
		// Set each node's Y co-ordinate starting from the root
		while(postOrderIterator.hasNext())
		{
			Node node = postOrderIterator.next();
			if(network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true).length == 0)
				positionLeafY(node);
			else
				positionInternalNodeY(node);
			
		}
		
		
		//Traverse and set each node's X co-ordinate starting form the root
		
		List<Node> preOrderNodes = commonFunctions.preOrderTraverse(network, root);
		Iterator<Node> preOrderIterator = preOrderNodes.iterator();
		
		// Set each node's Y co-ordinate starting from the root
		while(preOrderIterator.hasNext())
		{
			Node node = preOrderIterator.next();
			positionNodeX(node);
			
		}
		
				
		// Bend each edge to make it look rectangular

		 allEdges = network.edgesList();
		 edgesIterator = allEdges.iterator();

		while(edgesIterator.hasNext())
		{

			Edge edge = edgesIterator.next();
			networkView.getEdgeView(edge).clearBends();
			commonFunctions.addRectangularBends(network, networkView, edge);
		}

	}
	else
		System.out.println("The "+getName()+" layout can only be applied to trees.");

		

	}
	
	
	
	/**
	 * positionLeaf(Node)
	 * Positions the leaves
	 * @param node - the leaf to be positioned
	 */
	private void positionLeafY(Node node)
	{
		numLeavesVisited++;
		networkView.getNodeView(node).setYPosition(INTERNODE_DISTANCE * numLeavesVisited, true);
				
	}

	/**
	 * positionInternalNode(Node)
	 * Positions the internal nodes
	 * @param node - the internal node to be positioned
	 */
	private void positionInternalNodeY(Node node)
	{
		// Find the child horizontally nearest
		// Get the leaves of the subtree rooted at node
		List<Node> subtreeLeaves = commonFunctions.getLeaves(network, node);

		// Find the vertical midpoint of the subtree rooted at node
		double midpointY = commonFunctions.findSubtreeYMidPoint(networkView, subtreeLeaves);

		// Set the positions
		networkView.getNodeView(node).setYPosition(midpointY,true);
		
	}
	
	
	
	/**
	 * Sets the X co-ordinates for a the nodes in the tree
	 * @param node - the node to be positioned
	 */
	private void positionNodeX(Node node)
	{
		if(network.getInDegree(node,false) == 0)
			networkView.getNodeView(node).setXPosition(0.0, false);
		
		int [] incomingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);
		if(incomingEdgesArray.length>0)
		{
		Edge edge = network.getEdge(incomingEdgesArray[0]);
		double parentX = networkView.getNodeView(edge.getSource()).getXPosition();

		// get the branchLength
		double length = commonFunctions.getBranchLength(network, node);

//
		if(length == 0.0)
			length = networkView.getNodeView(edge.getSource()).getWidth();
		else
			// Scale the branch length by incorporating the scalingFactor
			length = length*scalingFactor;
			
		
		double nodeX = parentX + (length);
		
		networkView.getNodeView(node).setXPosition(nodeX, true);
		
		}
		
	}

}
