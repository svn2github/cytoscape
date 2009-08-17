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
	 * @param network - the network to be probed
	 * @return true if network has a leaf (node with outDegree = 0)
	 */
	public boolean hasLeaf(CyNetwork network)
	{
		// Get all nodes
		List<Node> nodesList = network.nodesList();
		Iterator <Node> nodesListIterator = nodesList.iterator();
		while(nodesListIterator.hasNext())
		{
			Node node = nodesListIterator.next();
			if(network.getOutDegree(node)==0)
				return true;

		}
		return false;
	}

	/**
	 * @param network - the network to be probed
	 * @return true if network is a tree (all nodes have inDegree = 1) 
	 */
	public boolean isTree(CyNetwork network)
	{
		// Get all nodes
		List<Node> nodesList = network.nodesList();
		Iterator <Node> nodesListIterator = nodesList.iterator();
		while(nodesListIterator.hasNext())
		{
			Node node = nodesListIterator.next();
			if(network.getInDegree(node)==0||network.getInDegree(node)==1)
				continue;
			else
				return false;
		}
		return true;
	}
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
	 * Find the depth of the node (Root is depth 0)
	 * @param node - the node whose depth is to be found
	 * @return the depth of the node
	 */
	public int getDepth(CyNetwork network, Node node)
	{
		if(network.getInDegree(node, false) == 0)
		{
			return 0;
		}
		else
		{
			int [] incomingEdges = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, true, false);
			

			int depth = getDepth(network, network.getEdge(incomingEdges[0]).getSource()); 
			
			return depth+1;
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


//	/**
//	 * Calculates the optimum factor by which edges must be scaled to obtain an optimum view
//	 * @param network - the network from which the scaling factor is to be calculated
//	 * @return - the factor to be multiplied to the branchLengths to obtain the optimum view
//	 */
//	public double getScalingFactor(CyNetwork network)
//	{
//
//
//		double factor = 1.0;
//		// Find the smallest edge
//		List<Edge> allEdges = network.edgesList();
//		Iterator<Edge> edgesIterator = allEdges.iterator();
//
//
//		double smallestLength = Double.MAX_VALUE;
//		while(edgesIterator.hasNext())
//		{
//
//			Edge edge = edgesIterator.next();
//			double length = getBranchLength(network, edge.getTarget());
//			if(length<smallestLength && length>0.0)
//				smallestLength = length;
//		}
//
//		// Calculate the scaling factor
//
//		while(smallestLength * factor <= 50.0) //50
//			factor *= 10.0;                     //10
//
//
//		return factor;		
//	}

	/**
	 * Adds the bends to make the edges look rectangular
	 * @param network - the network on which the bends are to be added
	 * @param networkView - the networkView on which bends are to be added
	 * @param edge - the edge onto which bends are to be added
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
	 * @param network - the network on which the bends are to be added
	 * @param networkView - the networkView on which bends are to be added
	 * @param edge - the edge onto which bends are to be added
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

			// And the angle of the source
			double sourceAngle = Math.atan2(sourceY, sourceX);


			// And the angle of the target
			double angle = Math.atan2(networkView.getNodeView(target).getYPosition(), networkView.getNodeView(target).getXPosition());

			//Bend the edge
			Bend circularBend = networkView.getEdgeView(edge).getBend();
			Point2D handlePoint = new Point2D.Double(radius*Math.cos(angle),radius*Math.sin(angle));
			Point2D sourcePoint = new Point2D.Double(sourceX, sourceY);
			circularBend.addHandle(handlePoint);

			// Algorithm to draw arcs:
			// Find the length of the chord from the source to the first handle in the bend
			// Find the length of the segment that would produce an arc of the desired handleInterval
			// Divide the length of the chord and the subSegment to calculate how many subSegments are required
			// Number of subSegments on chord = number of handles to produce the arc from the bend

			double chordLength = handlePoint.distance(sourcePoint);

			double handleInterval = Math.PI/18.0;
			double subSegmentLength = 2*radius*Math.sin(handleInterval/2.0);

			int iterations = (int)(chordLength/subSegmentLength);

			if(compareAngles(angle,sourceAngle)==0)

			{
				for(int i = 0;i<iterations; i ++)
				{
					angle = angle+(handleInterval);
					circularBend.addHandle(new Point2D.Double(radius*Math.cos(angle),radius*Math.sin(angle)));

				}
			}
			else if(compareAngles(angle,sourceAngle)==1)
			{
				for(int i =0; i<iterations; i++)
				{
					angle = angle-(handleInterval);
					circularBend.addHandle(new Point2D.Double(radius*Math.cos(angle),radius*Math.sin(angle)));

				}
			}

		}
	}


	/**
	 * Important for the functioning of addCircularBends
	 * Given the angle of the initial handle in the bend(arc) and the angle of the source node
	 * calculates which one is smaller
	 * @param angle1 - the angle of the initial handle
	 * @param angle2 - the angle of the source node
	 * @return 1 if angle1 is smaller, 0 if angle2 is smaller, 2 if error
	 */
	private int compareAngles(double angle1, double angle2)
	{
		int result = 2;
		if((angle1>0 && angle2>0)|| (angle1<0 && angle2<0))
		{
			if(angle1>=angle2)
				result = 1;
			else if(angle1<angle2)
				result = 0;
		}
		else if(angle1>=0 && angle2<0)
		{
			result = 0;
		}
		else if(angle1<0 && angle2>=0)
		{
			result = 1;
		}

		return result;


	}

}
