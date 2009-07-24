package org.cytoscape.phylotree.layout;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import giny.model.Edge;
import giny.model.Node;
import giny.view.Bend;

public class CommonFunctions {
	
	/**
	 * getTreeRoot(network)
	 * Finds the root of the tree and returns it
	 * @param - The network to find the root of
	 * @return - The root node of the network   
	 */
	public Node getTreeRoot(CyNetwork network)
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
	 * traverse(network, node)
	 * Recursively performs a post-order traversal of tree from node arg
	 * Invokes positioning methods for nodes
	 * @param network - the network to traverse
	 * @param node - the node to start traversal from
	 * @return list - the list containing nodes in post order
	 */
	public List<Node> postOrderTraverse(CyNetwork network, Node node)
	{

		List<Node> list = new LinkedList<Node>();
		
		// Find all outgoing edges
		int[] outgoingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);


		if(outgoingEdgesArray.length!=0)
		{
			// Traverse every child
			for(int i = 0; i<outgoingEdgesArray.length; i++)	
			{
				Edge edge = network.getEdge(outgoingEdgesArray[i]);
				//networkView.getEdgeView(edge).clearBends();
				
				list.addAll(postOrderTraverse(network, edge.getTarget()));
			}
			// Traverse the parent last
			list.add(node);
			return list;
			
		}
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
			list.add(node);
			return list;
		}

		return null;
	}

	
	
	
	/**
	 * preOrderTraverse(network, Node)
	 * Recursively performs a post-order traversal of tree from node arg
	 * Invokes positioning methods for nodes
	 * @param network - the network to traverse
	 * @param node - the node to start traversal from
	 * @return list - the list containing nodes in pre order
	 */

	public List<Node> preOrderTraverse(CyNetwork network, Node node)
	{

		List<Node> list = new LinkedList<Node>();
		// Find all outgoing edges
		int[] outgoingEdgesArray = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);


		if(outgoingEdgesArray.length!=0)
		{
			list.add(node);
			
			
			// Traverse every child
			for(int i = 0; i<outgoingEdgesArray.length; i++)	
			{
				Edge edge = network.getEdge(outgoingEdgesArray[i]);

				list.addAll(preOrderTraverse(network, edge.getTarget()));
			}
			return list;
		}
		// Base case: if node is a leaf
		else if(outgoingEdgesArray.length == 0)
		{
		
			list.add(node);
			return list;
		}

		return null;
	}
	
	
	/**
	 * getLeaves(network, Node)
	 * Recursively populates List with leaves on a path from Node
	 * @param network - the network in which the node is located
	 * @param node - the node which is the LSA of the leaves returned
	 * @return list - list of leaves of which node is the LSA
	 */
	public List<Node> getLeaves(CyNetwork network, Node node)
	{
		List<Node> list = new LinkedList<Node>();
		
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
				
				list.addAll(getLeaves(network, child));
				
			}
			

		}

		return list;
	}
	
	
	public List<Node> getAncestors(CyNetwork network, Node node)
	{
		List<Node> list = new LinkedList<Node>();
		
		while(network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false).length>0)
		{
			int[] incomingEdges= network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);
		
		if(incomingEdges.length>0)
		{
			Node ancestor = network.getEdge(incomingEdges[0]).getSource();
			list.add(ancestor);
			node = ancestor;
		}
		
		}
		return list;
	}
	
	/**
	 * Find the vertical midpoint of a list of leaves
	 * @param networkView - the networkView containing the leaves
	 * @param leaves - the list of leaves to be probed
	 * @return - the midpoint on the Y-axis of the all the leaves' positions  
	 */
	public double findSubtreeYMidPoint(CyNetworkView networkView, List<Node> leaves)
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
	 * Obtain the length of the edge incident onto the node
	 * @param network - the network in which the node is situated
	 * @param node - the node on which the edge of interest is incident
	 * @return length of the branch
	 */
	public double getBranchLength(CyNetwork network, Node node)
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
	 * Find the level of the node (Leaves are level 0)
	 * @param node - the node whose level is to be found
	 * @return the level of the node
	 */
	public int getLevel(CyNetwork network, Node node)
	{
		if(network.getOutDegree(node, false) == 0)
			return 0;
		else
		{
			int max = 0;
			int [] outGoingEdges = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true);
			for (int i = 0; i < outGoingEdges.length; i++)
			{
				int level = getLevel(network, network.getEdge(outGoingEdges[i]).getTarget()); 
				if(level > max)
					max = level;

			}

			return max+1;
		}
	}

	/**
	 * Calculate the number of leaves in the tree
	 * @return - the number of leaves in the tree
	 */
	public int getNumberOfLeaves(CyNetwork network)
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
	
	public double getScalingFactor(CyNetwork network)
	{
	
	
		double factor = 1.0;
		// Find the smallest edge
		List<Edge> allEdges = network.edgesList();
		Iterator<Edge> edgesIterator = allEdges.iterator();
		
		
		double smallestLength = Double.MAX_VALUE;
		while(edgesIterator.hasNext())
		{

			Edge edge = edgesIterator.next();
			double length = getBranchLength(network, edge.getTarget());
			if(length<smallestLength)
				smallestLength = length;
		}
		
		// Calculate the scaling factor
		
		while(smallestLength * factor <= 50.0)
			factor *= 10.0;
		
		
		return factor;		
	}
	
	/**
	 * Adds the bends to make the edges look rectangular
	 */
	public void addRectangularBends(CyNetwork network, CyNetworkView networkView, Edge edge)
	{
		
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


					// Bend the edge
					Bend rectangularBend = networkView.getEdgeView(edge).getBend();
					
					rectangularBend.addHandle(new Point2D.Double(cornerX, cornerY));
			
			}
	}
	
	/**
	 * Adds the bends to make the edges look circular
	 */
	public void addCircularBends(CyNetwork network, CyNetworkView networkView, Edge edge)
	{
		
			Node source = edge.getSource();
			Node target = edge.getTarget();
			
			// Check if the target is a reticulate node (indegree>1)
			// If yes, don't bend the edge
			if(network.getInDegree(target.getRootGraphIndex(), false) <= 1 && source.getRootGraphIndex()!=target.getRootGraphIndex())
			{

				
				double sourceX = networkView.getNodeView(source).getXPosition();
				double sourceY = networkView.getNodeView(source).getYPosition();
				// Get the radius of the source
				double radius = Math.sqrt(sourceX*sourceX + sourceY*sourceY);
				
				
				// And the angle of the target
				double angle = Math.atan2(networkView.getNodeView(target).getYPosition(), networkView.getNodeView(target).getXPosition());
				
				//Bend the edge
				Bend circularBend = networkView.getEdgeView(edge).getBend();
				
				circularBend.addHandle(new Point2D.Double(radius*Math.cos(angle),radius*Math.sin(angle)));
				
				
				
			}
	}
	
	
}
