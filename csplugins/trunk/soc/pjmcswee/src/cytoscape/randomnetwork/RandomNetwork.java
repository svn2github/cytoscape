/* File: RandomNetwork.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.randomnetwork;

import cytoscape.graph.dynamic.*;
import cytoscape.graph.dynamic.util.*;
import cytoscape.visual.*;
import java.util.Iterator;
import javax.swing.*;
import java.util.ListIterator;
import java.util.LinkedList;
import cytoscape.data.*;
import cytoscape.*;
import cytoscape.layout.*;
import cytoscape.layout.algorithms.*;
import javax.swing.SwingConstants;
import cytoscape.view.*;
import giny.model.*;
import giny.view.*;
import ding.view.*;
import java.awt.geom.Point2D;
import java.awt.Dimension;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

/******************************************************************
 *  This class wraps a DynamicGraph with the node and edge labels
 *  from a CyNetwork.  You can create a RandomNetwork object using
 *  a CyNetwork, so strictly speaking every RandomNetwork is not really
 *  random, however, every really random network is a RandomNetwork
 *  object.
 *
 *	@author Patrick J. McSweeney
 *	@version 1.0
 ******************************************************************/
public class RandomNetwork implements DynamicGraph
{

	/**
	 * The topology of the graph
	 */
	private DynamicGraph mGraph;

	/**
	 * The name of the network
	 */
	private String mNetworkName;
	
	
	/**
 	 * Whether or not this network is directed
	 */
	private boolean mDirected;
	
	
	/**
	 * The number of nodes in the random graph
	 */
	private int mNumNodes;
	
	
	/**
	 * The number of edges in the random graph
	 */
	private int mNumEdges;
	
	/**
	 * A static count of the number of nodes created
	 */
	private static int sNumNets = 1; 
	
	/**
	 *
	 */
	private int mNetId;


	/**
	 * This boolean indicates that the network is from a
	 * CyNetwork and has data associated with and is not just
	 * a topology.
	 */
	private boolean mHasData;

////////////////////////////////////////////////////////////////
// All variables below are only instantiated if mHasData is true

	/**
	 * The node labels for the network
	 */
	private String[] mNodeLabels;

	/**
	 * The edge labels
	 */
	private String[] mEdgeLabels;
	
	/**
	 * Node Positions
	 */
	private double mNodePositions[][];
	
	/**
	 * Which visual style the original CyNetwork uses
	 */
	private VisualStyle mVisualStyle;
	
	/**
	 *
	 */
	private double mCenterX;
	
	/**
	 *
	 */
	private double mCenterY;
	
	/**
	 *
	 */
	private double mWidth;
	
	/**
	 *
	 */
	private double mHeight;
	
	/**
	*
	*/
	private double mZoom;
		
	/**-----------------------------------------------------------------
	 *  Default Constructor.  Create an empty graph with no data.
	 *
	 *-----------------------------------------------------------------*/
	public RandomNetwork(boolean pDirected)
	{
		
		//Create the topology graph
		mGraph = DynamicGraphFactory.instantiateDynamicGraph();
		
		//Mark the direction of edges
		mDirected = pDirected;
		
		//Nodes and edges are empty
		mNumNodes = 0;
		mNumEdges = 0;
		
		//Created with the null model
		mHasData = false;		
		mNodeLabels = null;
		mEdgeLabels = null;
		mVisualStyle = null;
		mNodePositions = null;
		mZoom = 0;
	}
	
	/**-----------------------------------------------------------------
	 *  Create an empty graph
	 *
	 *-----------------------------------------------------------------*/
	public RandomNetwork(RandomNetwork pNetwork, boolean pDirected)
	{
		
		mDirected = pNetwork.getDirected();
		
		//Create the graph
		mGraph = DynamicGraphFactory.instantiateDynamicGraph();

		//How many edges are there
		int N = pNetwork.getNumNodes();
		
		mNetworkName = pNetwork.getTitle();																														
		
		//whether the incoming network has data
		mHasData = pNetwork.hasData();
		//create the data containers
		if(mHasData)
		{
			mNodeLabels = new String[N];
			mEdgeLabels = new String[pNetwork.getNumEdges()];
			mNodePositions = new double[N][2];
			mVisualStyle = pNetwork.getVisualStyle();
			mZoom = pNetwork.getZoom();
			mCenterX = pNetwork.getCenterX();
			mCenterY = pNetwork.getCenterY();
			mWidth = pNetwork.getWidth();
			mHeight = pNetwork.getHeight();
		}
		
		//For each node
		for(int i = 0; i < N; i++)
		{
			if(mHasData)
			{
				mNodeLabels[i] = pNetwork.getNodeLabels()[i];
				mNodePositions[i][0] = pNetwork.getNodePositions()[i][0];
				mNodePositions[i][1] = pNetwork.getNodePositions()[i][1];
			}
			
			nodeCreate();
		}
		
		//iterate over all of the edges
		int edgeCount = 0;
		IntEnumerator edgeIter = pNetwork.edges();
		while(edgeIter.numRemaining() > 0)
		{
			//get the next edge
			int edge = edgeIter.nextInt();

			if(mHasData)
			{
				mEdgeLabels[edgeCount] = pNetwork.getEdgeLabels()[edge];
			}
			
			int source = pNetwork.edgeSource(edge);
			int target = pNetwork.edgeTarget(edge);			
			edgeCreate(source,target, mDirected);
			edgeCount++;
		}
	
	}
	
		
	/**-----------------------------------------------------------------
	 *  Create an graph from a CyNetwork.
	 *		(1) Transfer the topology into a RandomNetwork
	 *		(2) Save the node labels.
	 *		(3) Save the edge labels.
	 *		(4) Save the node positions.
	 *		(5) Save the Visual Style.
	 *		(6) Record the number of edges and nodes.
	 * 
	 *
	 * @param pCyNetwork The CyNetwork to convert into a RandomNetwork.
	 * @param pDirected How to treat the CyNetwork's edges.
	 *-----------------------------------------------------------------*/
	public RandomNetwork(CyNetwork pCyNetwork, boolean pDirected)
	{	
		
		
		//Create an empty dynamic graph representation 
		mGraph = DynamicGraphFactory.instantiateDynamicGraph();

		//determine the number of nodes
		int N = pCyNetwork.getNodeCount();
		
		mDirected = pDirected;		
								
		//Create the array to hold the node labels
		mNodeLabels = new String[N];
		
		//Create the array to hold edge labels
		mEdgeLabels = new String[N];
				
		//Get the view from the original network
		CyNetworkView orgView = Cytoscape.createNetworkView(pCyNetwork);
		
		//Get the visual style from the view
		mVisualStyle = orgView.getVisualStyle();
				
		
		//Get the name of network
		mNetworkName = pCyNetwork.getTitle();

		
		//Save the Node Positions
		mNodePositions = new double[N][2];
		
		//Save the node indicies
		int nodeIndicies[] = new int[N];
		
		//Save the cyNodes
		Node cynodes[] = new Node[N];

		//set this value = 0
		mNumNodes = 0;

		//determine the number of edges
		mNumEdges = 0;
		
		//created from a CyNetwork
		mHasData = true;

		Point2D pt = ((DGraphView)orgView).getCenter();
		mCenterX = pt.getX();
		mCenterY =  pt.getY();
	
		mWidth = ((DGraphView)orgView).getComponent().getWidth();
		mHeight = ((DGraphView)orgView).getComponent().getHeight(); 
		mZoom = orgView.getZoom();

		//Create the appropriate number of nodes
		int i = 0;
		
		//Get the iterator for the CyNodes
		Iterator netIter = pCyNetwork.nodesIterator();
		
		//For each Node
		while(netIter.hasNext())
		{
			//Get the next node
			Node current = (Node)netIter.next();
			
			//Get the next iter
			NodeView nodeView = orgView.getNodeView(current.getRootGraphIndex());

			//Save this node
			cynodes[i] = current;
			
			//Create a node in the dynamic graph for this CyNode
			nodeIndicies[i] = nodeCreate();


			//Save the node positions
			mNodePositions[nodeIndicies[i]][0] = nodeView.getXPosition();
			mNodePositions[nodeIndicies[i]][1] = nodeView.getYPosition();
		
			
			//Save the CyNodes identifier
			mNodeLabels[nodeIndicies[i]] = current.getIdentifier();
			
			i++;
		}
		
		LinkedList<String> edgeList = new LinkedList<String>();
		
		//Iterate over all of the nodes in the network
		for(i = 0; i < mNumNodes; i++)
		{
			//Get the next node id
			int nodeIndex = nodeIndicies[i];
	
			//Get the next node
			Node current = cynodes[i];
			
			//Iterate through the neighborhood
			int edges[] = pCyNetwork.getAdjacentEdgeIndicesArray(current.getRootGraphIndex(), true, false, true);
			for(int k = 0; k < edges.length; k++)
			{
				//Get the edges
				Edge adjEdge = pCyNetwork.getEdge(edges[k]);
			
				//Get the target of this edge
				Node next = adjEdge.getTarget();
				
				//Switch to Source (undirected)
				if(next == current)
				{
					next = adjEdge.getSource();
				}
			
				//Find the index of this node
				int nextIndex = -1;
			
				//Find the CyNode in our array of CyNodes
				for(int j = 0; j < mNumNodes; j++)
				{			
					//If we have found it
					if(cynodes[j] == next)
					{
						//save the index
						nextIndex = nodeIndicies[j];
						
						break;
					}
				}
				
				
				
				//If the network is undirected only create one instance of the edge
				//This may be aproblem for multi-edge situations!!!
				if(!pDirected)
				{
					
					boolean exists = false;
					
					//Iterate through all of the existing undirected edges that touch nodeIndex
					IntEnumerator edgeIter = edgesAdjacent(nodeIndex,false,false,true);
					while(edgeIter.numRemaining() > 0)
					{
						//Get the next edge
						int checkEdge = edgeIter.nextInt();
						
						//Check to see if nodeIndex is already connected to nextIndex
						if((edgeSource(checkEdge) == nextIndex) || (edgeTarget(checkEdge) == nextIndex))
						{
							exists = true;
						}
					}
					
					//If this edge already exists
					if(exists)
					{
						//do not create it again
						continue;
					}
					
					//If this edge does not exist, then create it
					int edgeIndex  = edgeCreate(nodeIndex, nextIndex, pDirected);
					edgeList.add(adjEdge.getIdentifier());
				}
				
				//If the network is directed
				else 
				{
					//Create the edge
					int edgeIndex = edgeCreate(nodeIndex, nextIndex, pDirected);
					edgeList.add(adjEdge.getIdentifier());
				}
			}
		}
		
		ListIterator<String> iter = edgeList.listIterator();
		mEdgeLabels = new String[edgeList.size()];
		int edgeCount = 0;
		while(iter.hasNext())
		{
			String label = iter.next();
			mEdgeLabels[edgeCount] = label;
			edgeCount++;
		}	
	}


	


	/**-----------------------------------------------------------------
	 *  Exports the RandomNetwork as a CyNetwork
	 *
	 *
	 *  @return Returns the RandomNetwork 
	 *-----------------------------------------------------------------*/
	public CyNetwork toCyNetwork()
	{
		//Create a new CyNetwork
		String title = new String(mNetworkName);
		
		 if(mHasData)
			title += "'";
			
		title += " : " + sNumNets++;
		
		
		CyNetwork cynetwork =  Cytoscape.createNetwork(title, false);

	
		//Create the nodes indices array
		int nodeIndicies[]  = new int[mNumNodes];
		
		//Create an array of CyNode objects 
		CyNode nodes[] = new CyNode[mNumNodes];
	
		//The number of CyNodes that have been created.
		int nodeCount = 0;
		
		//Iterate through all of thie nodes in this network
		IntEnumerator nodeIterator = nodes();		
		
		//Iterate through all of our nodes
		while(nodeIterator.numRemaining() > 0)
		{
			//Save the nodeIndex for this node
			nodeIndicies[nodeCount] = nodeIterator.nextInt();
			
			//Get the Cytoscape node using the NodeIds (string labels)
			CyNode node = null;
			if(mHasData)
			{
				//Get the CyNode
				node = Cytoscape.getCyNode(mNodeLabels[nodeIndicies[nodeCount]], false);
			}
			else
			{
				//Create the CyNode if labels do not exist
				node = Cytoscape.getCyNode(""+ nodeIndicies[nodeCount], true);
			}
			
			//Add this node to our  network
			cynetwork.addNode(node);

			// Save node in array
			nodes[nodeCount] = node;
			
			//increment the number of nodes we have processed
			nodeCount++;
		}
		
		//Keep track of the number of edges we process
		int edgeCount = 0;
	
		//iterate through all of the edges
		IntEnumerator edgeIterator = edges();
		while(edgeIterator.numRemaining() > 0)
		{
			//Get the next edge
			int edgeIndex = edgeIterator.nextInt();
			
			//Get the source and target of the edge
			int source = edgeSource(edgeIndex);
			int target = edgeTarget(edgeIndex);
			
			//Get the type of this edge
			byte type = edgeType(edgeIndex);
			
			//Determine if this edge is directed or undirected
			boolean directed = false;
			if(type ==  DynamicGraph.DIRECTED_EDGE)
			{
				directed = true;
			}
			
			
			/*
			This code makes sure that indicies are linear... if nodes have been
			deleted (not sure why we would do that), then they might not be linear.
			I'm pretty sure we can remove this code pjm 8/6/08
			*/
			if(nodeIndicies[source] != source)
			{
				for(int i = 0; i < mNumNodes; i++)
				{
					if(nodeIndicies[i] == source)
					{
						source = i;
						break;
					}
				}
			}
			
			
			/*
			This code makes sure that indicies are linear... if nodes have been
			deleted (not sure why we would do that), then they might not be linear.
			I'm pretty sure we can remove this code pjm 8/6/08
			*/
			if(nodeIndicies[target] != target)
			{
				for(int i = 0; i < mNumNodes; i++)
				{
					if(nodeIndicies[i] == target)
					{
						target = i;
						break;
					}
				}
							
			
			}

			//A string that acts a flag to ensure that multi-edges are not lost
			String duplicate = "";
			
			//iterate through the existing edges in the network
			int existsCount = 0;

			int edges[] = cynetwork.getAdjacentEdgeIndicesArray(nodes[source].getRootGraphIndex(), !directed, false, directed);

			for(int e = 0; e < edges.length; e++)
			{
				//get the next edge
				Edge next = cynetwork.getEdge(edges[e]);
				
				//If this edge already exists
				if(next.getTarget() == nodes[target])
				{
					duplicate = "dup :";
					//Keep track of how many times this edge exists
					existsCount++;
				}
			}
			
			//Change the name if there are more than one other instances of this edge
			if(existsCount > 0)
			{
				duplicate += "("  + existsCount +")";
			}
			
			
			//Create the edge between these two nodes
			CyEdge edge = Cytoscape.getCyEdge(nodes[source], nodes[target],
					Semantics.INTERACTION, new String(duplicate + "("
							+ Math.min(source, target) + ","
							+ Math.max(source, target) + ")"), true,
						directed);

			// Add this edge to the network
			cynetwork.addEdge(edge);
			
			//increment the number of edges we have processed
			edgeCount++;
		}
		
		
		
			
		//Now that the network is complete, create a view for it
		CyNetworkView view = Cytoscape.createNetworkView(cynetwork);

		//Iterate through all of our nodes
		for(int i = 0; i < nodeCount; i++)
		{
			//Get the next node
			Node node = nodes[i];
			NodeView nodeView = view.addNodeView(node.getRootGraphIndex());

			//If the node has an x,y position
			if(mHasData)
			{				
				nodeView.setXPosition(mNodePositions[nodeIndicies[i]][0]);
				nodeView.setYPosition(mNodePositions[nodeIndicies[i]][1]);
			}
		}
		
		
		
		//Apply the style
		if(mHasData)
		{
			//If there is data, apply the zoom, visual style and center
			view.applyVizmapper(mVisualStyle);
			view.setZoom(mZoom);
			((DGraphView)view).setCenter(mCenterX, mCenterY);


		
			((DGraphView)view).getComponent().setSize((int)mWidth,(int)mHeight);

			InnerCanvas ic = (InnerCanvas)((DGraphView)view).getComponent();
			//BirdsEyeView bev = (BirdsEyeView)Cytoscape.getDesktop().getBirdsEyeViewHandler().getBirdsEyeView();
			ic.setBounds(0,0,(int)mWidth, (int)mHeight);
			((DGraphView)view).updateView();
			//ic.setMinimumSize(new Dimension((int)mWidth, (int)mHeight));
			//ic.setPreferredSize(new Dimension((int)mWidth, (int)mHeight));

			//((JLayeredPane)((DGraphView)view).getComponent().getParent()).setSize(mWidth,mHeight);

		}
		else
		{
		
			//If there was no data, apply the grid node layout and 
			//FIt the graph to the screen.
			view.applyLayout(new GridNodeLayout());		
			view.fitContent();
		}
		
		//Change the view back to the main Network Panel
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(0);
			
		//Return the result
		return cynetwork;
	
	}
	


	/**-----------------------------------------------------------------
	 * Returns an enumeration of all nodes currently in this graph.
	 * Every node in this graph is a unique non-negative integer.  A given
	 * node and a given edge in one graph may be the same integer.<p>
	 * The returned enumeration becomes invalid as soon as any
	 * graph-topology-modifying method on this graph is called.  Calling
	 * methods on an invalid enumeration will result in undefined behavior
	 * of that enumeration.  Enumerating through a graph's nodes will
	 * never have any effect on the graph.
	 *
	 * @return an enumeration over all nodes currently in this graph; null
	 *   is never returned.
	 *-----------------------------------------------------------------*/
	public IntEnumerator nodes()
	{
		return mGraph.nodes();
	}

	/**-----------------------------------------------------------------
	 * Returns an enumeration of all edges currently in this graph.
	 * Every edge in this graph is a unique non-negative integer.  A given
	 * node and a given edge in one graph may be the same integer.<p>
	 * The returned enumeration becomes invalid as soon as any
	 * graph-topology-modifying method on this graph is called.  Calling
	 * methods on an invalid enumeration will result in undefined behavior
	 * of that enumeration.  Enumerating through a graph's edges will
	 * never have any effect on the graph.
	 *
	 * @return an enumeration over all edges currently in this graph; null
	 *   is never returned.
	 *-----------------------------------------------------------------*/
	public IntEnumerator edges()
	{
		return mGraph.edges();
	}

	/**
	 * Creates a new node in this graph.  Returns the new node.  Nodes are
	 * always non-negative.<p>
	 * Implementations may create nodes with arbitrarily large values.
	 * Even if implementations initially create nodes with small values,
	 * nodes may take ever-increasing values when nodes are continually being
	 * removed and created.  Or, implementations may choose to re-use node values
	 * as nodes are removed and added again.
	 *
	 * @return the newly created node.
	 *-----------------------------------------------------------------*/
	public int nodeCreate()
	{
		mNumNodes++;
		return mGraph.nodeCreate();
	}
	

	/**-----------------------------------------------------------------
	 * Removes the specified node from this graph.  Returns true if and only
	 * if the specified node was in this graph at the time this method was
	 * called.  A return value of true implies that the specified node has
	 * been successfully removed from this graph.<p>
	 * Note that removal of a node necessitates the removal of any edge
	 * touching that node.
	 *
	 * @param node the node that is to be removed from this graph.
	 * @return true if and only if the specified node existed in this graph
	 *   at the time this operation was started.
	 *-----------------------------------------------------------------*/
	public boolean nodeRemove(int node)
	{
		mNumNodes--;
		return mGraph.nodeRemove(node);
	}

	/**-----------------------------------------------------------------
	 * Creates a new edge in this graph, having source node, target node,
	 * and directedness specified.  Returns the new edge, or -1 if either the
	 * source or target node does not exist in this graph.  Edges are always
	 * non-negative.<p>
	 * Implementations may create edges with arbitrarily large values.
	 * Even if implementations initially create edges with small values,
	 * edges may take ever-increasing values when edges are continually being
	 * removed and created.  Or, implementations may choose to re-use edge values
	 * as edges are removed and added again.
	 *
	 * @param sourceNode the source node that the new edge is to have.
	 * @param targetNode the target node that the new edge is to have.
	 * @param directed the new edge will be directed if and only if this value
	 *   is true.
	 * @return the newly created edge or -1 if either the source or target node
	 *   specified does not exist in this graph.
	 *-----------------------------------------------------------------*/
	public int edgeCreate(int sourceNode, int targetNode, boolean directed)
	{
		mNumEdges++;
		return mGraph.edgeCreate(sourceNode, targetNode, directed);
	}

	/**-----------------------------------------------------------------
	 * Removes the specified edge from this graph.  Returns true if and only
	 * if the specified edge was in this graph at the time this method was
	 * called.  A return value of true implies that the specified edge has
	 * been successfully removed from this graph.<p>
	 * Note that removing an edge does not cause that edge's endpoint nodes
	 * to be removed from this graph.
	 *
	 * @param edge the edge that is to be removed from this graph.
	 * @return true if and only if the specified edge existed in this graph
	 *   at the time this operation was started.
	 *-----------------------------------------------------------------*/
	public boolean edgeRemove(int edge)
	{
		mNumEdges--;
		return mGraph.edgeRemove(edge);
	}

	/**-----------------------------------------------------------------
	 * Determines whether or not a node exists in this graph.
	 * Returns true if and only if the node specified exists.<p>
	 * Note that this method is superfluous in this interface (that is,
	 * it could be removed without losing any functionality), because
	 * edgesAdjacent(int, boolean, boolean, boolean) can be used to test
	 * the presence of a node.  However, because nodeExists(int) does not
	 * return a complicated object, its performance may be better
	 * than that of edgesAdjacent().
	 *
	 * @param node the [potentially existing] node in this graph whose existence
	 *   we're querying.
	 * @return the existence of specified node in this graph.
	 *-----------------------------------------------------------------*/
	public boolean nodeExists(int node)
	{
		return mGraph.nodeExists(node);
	}

	/**-----------------------------------------------------------------
	 * Determines the existence and directedness of an edge.
	 * Returns -1 if specified edge does not exist in this graph,
	 * otherwise returns DIRECTED_EDGE or UNDIRECTED_EDGE.
	 *
	 * @param edge the edge in this graph whose existence and/or
	 *   directedness we're seeking.
	 * @return DIRECTED_EDGE if specified edge is directed, UNDIRECTED_EDGE
	 *   if specified edge is undirected, and -1 if specified edge does not
	 *   exist in this graph.
	 *-----------------------------------------------------------------*/
	public byte edgeType(int edge)
	{
		return mGraph.edgeType(edge);
	}

	/**-----------------------------------------------------------------
	 * Determines the source node of an edge.
	 * Returns the source node of specified edge or -1 if specified edge does
	 * not exist in this graph.
	 *
	 * @param edge the edge in this graph whose source node we're seeking.
	 * @return the source node of specified edge or -1 if specified edge does
	 *   not exist in this graph.
	 *-----------------------------------------------------------------*/
	public int edgeSource(int edge)
	{
		return mGraph.edgeSource(edge);
	}

	/**-----------------------------------------------------------------
	 * Determines the target node of an edge.
	 * Returns the target node of specified edge or -1 if specified edge does
	 * not exist in this graph.
	 *
	 * @param edge the edge in this graph whose target node we're seeking.
	 * @return the target node of specified edge or -1 if specified edge does
	 *   not exist in this graph.
	 *-----------------------------------------------------------------*/
	public int edgeTarget(int edge)
	{
		return mGraph.edgeTarget(edge);
	}

	/**-----------------------------------------------------------------
	 * Returns a non-repeating enumeration of edges adjacent to a node.
	 * The three boolean input parameters define what is meant by "adjacent
	 * edge".  If all three boolean input parameters are false, the returned
	 * enumeration will have zero elements.<p>
	 * The returned enumeration becomes invalid as soon as any
	 * graph-topology-modifying method on this graph is called.  Calling
	 * methods on an invalid enumeration will result in undefined behavior
	 * of that enumeration.<p>
	 * This method returns null if and only if the specified node does not
	 * exist in this graph.  Therefore, this method can be used to test
	 * the existence of a node in this graph.
	 *
	 * @param node the node in this graph whose adjacent edges we're seeking.
	 * @param outgoing all directed edges whose source is the node specified
	 *   are included in the returned enumeration if this value is true;
	 *   otherwise, not a single such edge is included in the returned
	 *   enumeration.
	 * @param incoming all directed edges whose target is the node specified
	 *   are included in the returned enumeration if this value is true;
	 *   otherwise, not a single such edge is included in the returned
	 *   enumeration.
	 * @param undirected all undirected edges touching the specified node
	 *   are included in the returned enumeration if this value is true;
	 *   otherwise, not a single such edge is included in the returned
	 *   enumeration.
	 * @return an enumeration of edges adjacent to the node specified
	 *   or null if specified node does not exist in this graph.
	 *-----------------------------------------------------------------*/
	public IntEnumerator edgesAdjacent(int node, boolean outgoing, boolean incoming,
	                                   boolean undirected)
	{
		return mGraph. edgesAdjacent(node, outgoing, incoming, undirected);
	}

	/**-----------------------------------------------------------------
	 * Returns a non-repeating iteration of edges connecting two nodes.
	 * The three boolean input parameters define what is meant by "connecting
	 * edge".  If all three boolean input parameters are false, the returned
	 * iteration will have no elements.<p>
	 * The returned iteration becomes invalid as soon as any
	 * graph-topology-modifying method on this graph is called.  Calling
	 * methods on an invalid iteration will result in undefined behavior
	 * of that iteration.<p>
	 * I'd like to discuss the motivation behind this interface method.
	 * I assume that most implementations of this interface will implement
	 * this method in terms of edgesAdjacent().  Why, then, is this method
	 * necessary?  Because some implementations may choose to optimize the
	 * implementation of this method by using a search tree or a
	 * hashtable, for example.  This method is a hook to provide such
	 * optimization.<p>
	 * This method returns an IntIterator as opposed to an IntEnumerator
	 * so that non-optimized implementations would not be required to
	 * pre-compute the number of edges being returned.
	 *
	 * @param node0 one of the nodes in this graph whose connecting edges
	 *   we're seeking.
	 * @param node1 one of the nodes in this graph whose connecting edges
	 *   we're seeking.
	 * @param outgoing all directed edges whose source is node0 and whose
	 *   target is node1 are included in the returned iteration if this value
	 *   is true; otherwise, not a single such edge is included in the returned
	 *   iteration.
	 * @param incoming all directed edges whose source is node1 and whose
	 *   target is node0 are included in the returned iteration if this value
	 *   is true; otherwise, not a single such edge is included in the returned
	 *   iteration.
	 * @param undirected all undirected edges E such that E's endpoints
	 *   are node0 and node1 are included in the returned iteration if this
	 *   value is true; otherwise, not a single such edge is incuded in the
	 *   returned iteration.
	 * @return an iteration of edges connecting node0 with node1 in a fashion
	 *   specified by boolean input parameters or null if either of node0 or
	 *   node1 does not exist in this graph.
	 *-----------------------------------------------------------------*/
	public IntIterator edgesConnecting(int node0, int node1, boolean outgoing, boolean incoming,
	                                   boolean undirected)
	{
		return mGraph.edgesConnecting(node0, node1, outgoing, incoming, undirected);
	}
	
	
	
	
	/**
	 *  Areturn The width of the canvas
	 */
	public double getWidth()
	{
		return mWidth;
	}
	
	
	/**
	 * @return The height of the canvas
	 */
	public double getHeight()
	{
		return mHeight;
	}
	
	/**
	 * @return the X center of the viewport
	 */
	public double getCenterX()
	{
		return mCenterX;
	}
	
	
	/**
	* @return the Y center of the viewport
	*/
	public double getCenterY()
	{
		return mCenterY;
	}
	
	
	/**
	 * @return the zoom level
	 */
	public double getZoom()
	{
		return mZoom;
	}
	
	
	/**
	 * @return the direction
	 */
	public boolean getDirected()
	{
		return mDirected;
	}
	
	/**
	 * @return the node positions
	 */
	public double[][] getNodePositions()
	{
		return mNodePositions;
	}
	
	/**
 	 * @return the visual style used
	 */
	public VisualStyle getVisualStyle()
	{
		return mVisualStyle;
	}
	
	/**
	 * @return True - this network came from a CyNetwork.
	 *	       False - this network was generated from sratch.
	 */
	public boolean hasData()
	{
		return mHasData;
	}
	
	/**
	 * @return The node labels
	 */
	public String[] getNodeLabels()
	{
		return mNodeLabels;
	}

	/**
	 * @return The edge labels
	 */
	public String[] getEdgeLabels()
	{
		return mEdgeLabels;
	}
	
	
	/**
	 * @param pNetworkName The title of this network
	 */
	public void setTitle(String pNetworkName)
	{
		mNetworkName = pNetworkName;
	}
	
	/**
	 * @return The title of this network
	 */
	public String getTitle()
	{
		return mNetworkName;
	}
	
	/**
	 * @return The number of nodes in the network
	 */
	public int getNumNodes()
	{
		return mNumNodes;
	}
	
	/**
	 * @return The number of edges in this network
	 */
	public int getNumEdges()
	{
		return mNumEdges;
	}	

	
}


