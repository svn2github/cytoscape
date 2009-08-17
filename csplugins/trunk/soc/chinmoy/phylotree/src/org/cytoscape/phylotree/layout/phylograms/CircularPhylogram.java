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


public class CircularPhylogram extends AbstractLayout{

	static double BASE_RADIUS = 10.0;

	private int numLeavesVisited = 0; //
	private LayoutProperties layoutProperties;
	private double scalingFactor = 0.0;
	
	private CommonFunctions commonFunctions; 
	
	public CircularPhylogram()
	{
		
		super();

		layoutProperties = new LayoutProperties(getName());
		initialize_properties();

	}
	protected void initialize_properties()
	{	
		layoutProperties.add(new Tunable("edge_scaling", "Edge scaling",
                Tunable.DOUBLE, new Double(scalingFactor = 35.0)));
//		
//		layoutProperties.add(new Tunable("edge_scaling", "Edge scaling", Tunable.STRING, 
//				new Double(scalingFactor = 35.0), new Double(10.0),
//				new Double(100.0), Tunable.USESLIDER));
		
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
		return "circular_phylogram";
	}

	/**
	 * toString is used to get the user-visible name
	 * 1of the layout
	 */
	public  String toString(){
		return "Phylogram - Circular";
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
				setAngleLeaf(node);
			else
				setAngleInternalNode(node);
			
		}
		
		
		//Traverse and set each node's X co-ordinate starting form the root
		
		List<Node> preOrderNodes = commonFunctions.preOrderTraverse(network, root);
		Iterator<Node> preOrderIterator = preOrderNodes.iterator();
		
		// Set each node's Y co-ordinate starting from the root
		while(preOrderIterator.hasNext())
		{
			Node node = preOrderIterator.next();
			setRadius(node);
			
		}
				
		// Bend each edge to make it look circular

		allEdges = network.edgesList();
		edgesIterator = allEdges.iterator();

		while(edgesIterator.hasNext())
		{

			Edge edge = edgesIterator.next();
			networkView.getEdgeView(edge).clearBends();
			commonFunctions.addCircularBends(network, networkView, edge);
		}

		}
		else
			System.out.println("The "+getName()+" layout can only be applied to trees.");

		

	}
	
	
	private void setRadius(Node node)
	{
		// Get the angle
		double angle = Math.atan2(networkView.getNodeView(node).getYPosition(), networkView.getNodeView(node).getXPosition());
		
		// Calculate new radius
		List<Node> ancestors = commonFunctions.getAncestors(network, node);
		Iterator<Node> it = ancestors.iterator();
		
		// Find total distance from root to node
		double totalRadius = 0.0;
		while(it.hasNext())
		{
			Node ancestor = it.next();
			totalRadius = totalRadius + commonFunctions.getBranchLength(network, ancestor);
		}
		
		totalRadius = totalRadius+commonFunctions.getBranchLength(network, node);
		
		totalRadius = totalRadius*scalingFactor;
		// Reposition node
		networkView.getNodeView(node).setXPosition(totalRadius*BASE_RADIUS*Math.cos(angle),true);
		networkView.getNodeView(node).setYPosition(totalRadius*BASE_RADIUS*Math.sin(angle),true);
		
	}
	private void setAngleInternalNode(Node node)
	{
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

		// Position
		networkView.getNodeView(node).setXPosition(BASE_RADIUS*Math.cos(meanAngle),true);
		networkView.getNodeView(node).setYPosition(BASE_RADIUS*Math.sin(meanAngle),true);
		
	}

	private void setAngleLeaf(Node node)
	{
		numLeavesVisited++;
		double numLeaves = commonFunctions.getNumberOfLeaves(network);
		
		// Calculate the angle
		double angle = 2*Math.PI*((double)numLeavesVisited/(double)numLeaves);
		
		// Position the leaf's angle
		networkView.getNodeView(node).setXPosition(BASE_RADIUS*Math.cos(angle),true);
		networkView.getNodeView(node).setYPosition(BASE_RADIUS*Math.sin(angle),true);
	}

}
