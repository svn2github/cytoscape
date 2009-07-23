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
import org.cytoscape.phylotree.layout.CommonFunctions;

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
	
	private CommonFunctions commonFunctions; 

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
                Tunable.DOUBLE, new Double(scalingFactor = 100.0)));
		
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
		
		// Intialize the common functions
		commonFunctions = new CommonFunctions();
		
		
		// Find the root of the tree
		Node root = commonFunctions.getTreeRoot(network);
		
		
		// Traverse and set each node's Y co-ordinate starting from the root
		//List<Node> l = commonFunctions.postOrderTraverse(network, root);
		
		
		//Traverse and set each node's X co-ordinate starting form the root
		
		//List<Node> p = commonFunctions.preOrderTraverse(network, root);
		
		// If rectangular format is preferred, edit the edge shape by adding corner nodes
		if(rectangular || circular)
		{

//			addRectangularBends();

		}
		

	}

	// Methods used by construct //

	



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
		List<Node> subtreeLeaves = commonFunctions.getLeaves(network, node);

		// Find the vertical midpoint of the subtree rooted at node
		double midpointY = commonFunctions.findSubtreeYMidPoint(networkView, subtreeLeaves);

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
		
		double nodeX = parentX + (scalingFactor*commonFunctions.getBranchLength(network, node));
		
		networkView.getNodeView(node).setXPosition(nodeX, true);
		
		}
		
	}
	
	private void setRadius(Node node)
	{
		
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
			if(network.getInDegree(target.getRootGraphIndex(), false) <= 1 && source.getRootGraphIndex()!=target.getRootGraphIndex())
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
					double radius = (INTERNODE_DISTANCE/2.0)*(commonFunctions.getLevel(network, commonFunctions.getTreeRoot(network)) - commonFunctions.getLevel(network, source));
					
					// And the angle of the target
					double angle = Math.atan2(networkView.getNodeView(target).getYPosition(), networkView.getNodeView(target).getXPosition());
					
					//Bend the edge
					Bend circularBend = networkView.getEdgeView(edge).getBend();
					
					circularBend.addHandle(new Point2D.Double(radius*Math.cos(angle),radius*Math.sin(angle)));
				}
			}	



		}
	}
	

}