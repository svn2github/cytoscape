/** Copyright (c) 2004 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Robert Sheridan
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 ** Date: January 19.2004
 ** Description: Hierarcical layout plugin, based on techniques by Sugiyama
 ** et al. described in chapter 9 of "graph drawing", Di Battista et al,1999
 **
 ** Based on the csplugins.tutorial written by Ethan Cerami and GINY plugin
 ** written by Andrew Markiel
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.layout.algorithms.hierarchicalLayout;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import cytoscape.task.TaskMonitor;

import cytoscape.view.CyNetworkView;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.GridLayout;
import java.awt.geom.Point2D;

import java.util.*;

import javax.swing.JPanel;


class HierarchyFlowLayoutOrderNode implements Comparable {
	/**
	 * 
	 */
	public NodeView nodeView;

	/**
	 * 
	 */
	public int componentNumber;

	/**
	 * 
	 */
	public int componentSize;

	/**
	 * 
	 */
	public int layer;

	/**
	 * 
	 */
	public int horizontalPosition;

	/**
	 * 
	 */
	public int xPos;

	/**
	 * 
	 */
	public int yPos;
	
	/**
	 * *
	 */
	public int graphIndex;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getXPos() {
		return xPos;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getYPos() {
		return yPos;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param a_xPos DOCUMENT ME!
	 */
	public void setXPos(int a_xPos) {
		xPos = a_xPos;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param a_yPos DOCUMENT ME!
	 */
	public void setYPos(int a_yPos) {
		yPos = a_yPos;
	}

	/**
	 * Creates a new HierarchyFlowLayoutOrderNode object.
	 *
	 * @param a_nodeView  DOCUMENT ME!
	 * @param a_componentNumber  DOCUMENT ME!
	 * @param a_componentSize  DOCUMENT ME!
	 * @param a_layer  DOCUMENT ME!
	 * @param a_horizontalPosition  DOCUMENT ME!
	 */
	public HierarchyFlowLayoutOrderNode(NodeView a_nodeView, int a_componentNumber,
	                                    int a_componentSize, int a_layer, int a_horizontalPosition, int a_graphIndex) {
		nodeView = a_nodeView;
		componentNumber = a_componentNumber;
		componentSize = a_componentSize;
		layer = a_layer;
		horizontalPosition = a_horizontalPosition;
		graphIndex = a_graphIndex;		
	}

	public HierarchyFlowLayoutOrderNode(HierarchyFlowLayoutOrderNode a) {
		nodeView = a.nodeView;
		componentNumber = a.componentNumber;
		componentSize = a.componentSize;
		layer = a.layer;
		horizontalPosition = a.horizontalPosition;
		graphIndex = a.graphIndex;
		yPos = a.yPos;
		xPos = a.xPos;
		
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param o DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		HierarchyFlowLayoutOrderNode y = (HierarchyFlowLayoutOrderNode) o;
		int diff = y.componentSize - componentSize;

		if (diff != 0)
			return diff;

		diff = componentNumber - y.componentNumber;

		if (diff != 0)
			return diff;

		diff = layer - y.layer; //y.layer - layer;

		if (diff != 0)
			return diff;

		return horizontalPosition - y.horizontalPosition;
	}
}
;


/**
 * Lays out graph in tree-like pattern.
 * The layout will approximate the optimal orientation
 * for nodes which have a tree-like relationship. <strong> This
 * assumed relationship is based on directed edges. This class does
 * not currently distinguish or gracefully treat undirected edges.
 * Also, duplicate edges are ignored for the purpose of positioning
 * nodes in the layout.
 * </strong>
 * <br>The major steps in this algorithm are:
 * <ol>
 * <li>Choose the set of nodes to be layed out based on which are selected</li>
 * <li>Partition this set into connected components</li>
 * <li>Detect and eliminate (temporarily) graph cycles</li>
 * <li>Eliminate (temporarily) transitive edges</li>
 * <li>Assign nodes to layers (parents always in layer above any child's layer)</li>
 * <li>Choose a within-layer ordering which reduces edge crossings between layers</li>
 * <li>Select horizontal positions for nodes within a layer to minimize edge length</li>
 * <li>Assemble layed out components and any unselected nodes into a composite layout</li>
 * </ol>
 * Steps 2 through 6 are performed by calls to methods in the class
 * {@link csplugins.hierarchicallayout.Graph}
*/
public class HierarchicalLayoutAlgorithm extends AbstractLayout {
	private int nodeHorizontalSpacing = 64;
	private int nodeVerticalSpacing = 32;
	private int componentSpacing = 64;
	private int bandGap = 64;
	private int leftEdge = 32;
	private int topEdge = 32;
	private int rightMargin = 7000;
	private boolean selected_only = false;
	private LayoutProperties layoutProperties;
	
	private HashMap<Integer, HierarchyFlowLayoutOrderNode> nodes2HFLON = new HashMap<Integer, HierarchyFlowLayoutOrderNode>();

	/**
	 * Creates a new HierarchicalLayoutAlgorithm object.
	 */
	public HierarchicalLayoutAlgorithm() {
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}

	// We do support selected only
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	/**
	 * Lays out the graph. See this class' description for an outline
	 * of the method used. <br>
	 * For the last step, assembly of the layed out components, the method
	 * implemented is similar to the FlowLayout layout manager from the AWT.
	 * Space is allocated in horizontal bands, with a new band begun beneath
	 * the higher bands. This happens on two scales: on the component scale,
	 * and on the intra-component scale. Within each component, the layers
	 * are placed horizontally, each within its own band. Globally, each
	 * component appears in a band which is filled until a right margin is
	 * hit. After that a new band is started beneath the higher band of
	 * layed out components. Components are never split between these global
	 * bands. Each component is finished, regardless of its horizontal
	 * extent. <br>
	 * Also, a post placement pass is done on each component to move each
	 * layer horizontally in order to line up the centers of the layers with
	 * the center of the component.
	 * @param event Menu Selection Event.
	 */

	/**
	 * Main entry point for AbstractLayout classes
	 */
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); // Calls initialize_local
		layout();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void layout() {
		taskMonitor.setPercentCompleted(0);
		taskMonitor.setStatus("Capturing snapshot of network and selected nodes");

		if (canceled)
			return;

		/* construct node list with selected nodes first */
		List selectedNodes = networkView.getSelectedNodes();
		int numSelectedNodes = selectedNodes.size();

		if (!selectedOnly)
			numSelectedNodes = 0;

		if (numSelectedNodes == 1) {
			// We were asked to do a hierchical layout of a single node -- done!
			return;
		}

		final int numNodes = networkView.getNodeViewCount();
		final int numLayoutNodes = (numSelectedNodes < 1) ? numNodes : numSelectedNodes;
		NodeView[] nodeView = new NodeView[numNodes];
		int nextNode = 0;
		HashMap<Integer, Integer> ginyIndex2Index = new HashMap<Integer, Integer>(numNodes * 2);

		if (numSelectedNodes > 1) {
			Iterator iter = selectedNodes.iterator();

			while (iter.hasNext() && !canceled) {
				nodeView[nextNode] = (NodeView) (iter.next());
				ginyIndex2Index.put(new Integer(nodeView[nextNode].getNode().getRootGraphIndex()),
				                    new Integer(nextNode));
				nextNode++;
			}
		} else {
			Iterator iter = networkView.getNodeViewsIterator(); /* all nodes */

			while (iter.hasNext() && !canceled) {
				NodeView nv = (NodeView) (iter.next());
				Integer nodeIndexKey = new Integer(nv.getNode().getRootGraphIndex());

				if (!ginyIndex2Index.containsKey(nodeIndexKey)) {
					nodeView[nextNode] = nv;
					ginyIndex2Index.put(nodeIndexKey, new Integer(nextNode));
					nextNode++;
				}
			}
		}

		if (canceled)
			return;

		/* create edge list from edges between selected nodes */
		LinkedList<Edge> edges = new LinkedList();
		Iterator iter = networkView.getEdgeViewsIterator();

		while (iter.hasNext()) {
			EdgeView ev = (EdgeView) (iter.next());
			Integer edgeFrom = (Integer) ginyIndex2Index.get(new Integer(ev.getEdge().getSource()
			                                                               .getRootGraphIndex()));
			Integer edgeTo = (Integer) ginyIndex2Index.get(new Integer(ev.getEdge().getTarget()
			                                                             .getRootGraphIndex()));

			if ((edgeFrom == null) || (edgeTo == null)) {
				// Must be from an unselected node
				continue;
			}

			if (canceled)
				return;

			if ((numSelectedNodes <= 1)
			    || ((edgeFrom.intValue() < numSelectedNodes)
			       && (edgeTo.intValue() < numSelectedNodes))) {
				/* add edge to graph */
				Edge theEdge = new Edge(edgeFrom.intValue(), edgeTo.intValue());				
				edges.add(theEdge);								
			}
		}
				

		/* find horizontal and vertical coordinates of each node */
		Edge[] edge = new Edge[edges.size()];
		edges.toArray(edge);

		Graph graph = new Graph(numLayoutNodes, edge);

		/*
		int edgeIndex;
		for (edgeIndex = 0; edgeIndex<edge.length; edgeIndex++) {
		     System.out.println("Edge: " + edge[edgeIndex].getFrom() + " - " + edge[edgeIndex].getTo());
		}
		*/
		int[] cI = graph.componentIndex();
		int x;
		/*
		System.out.println("Node index:\n");
		for (x=0; x<graph.getNodecount(); x++) {
		    System.out.println(cI[x]);
		}
		System.out.println("Partitioning into components:\n");
		*/
		taskMonitor.setPercentCompleted(10);
		taskMonitor.setStatus("Finding connected components");

		if (canceled)
			return;

		int[] renumber = new int[cI.length];
		Graph[] component = graph.partition(cI, renumber);
		final int numComponents = component.length;
		int[][] layer = new int[numComponents][];
		int[][] horizontalPosition = new int[numComponents][];
		Graph[] reduced = new Graph[component.length];
		Graph[] reducedTmp = new Graph[component.length];
		HashMap<Integer, Edge>[] dummy2Edge = new HashMap[component.length];
		int[] dummyStartForComp = new int[component.length];
		HashMap<Edge, EdgeView>[] myEdges2EdgeViews= new HashMap[component.length];

		for (x = 0; x < component.length; x++) {
			/*
			System.out.println("plain component:\n");
			System.out.println(component[x]);
			System.out.println("filtered component:\n");
			System.out.println(component[x].getGraphWithoutOneOrTwoCycles());
			System.out.println("nonmulti component:\n");
			System.out.println(component[x].getGraphWithoutMultipleEdges());
			int cycleEliminationPriority[] = component[x].getCycleEliminationVertexPriority();
			System.out.println("acyclic component:\n");
			System.out.println(component[x].getGraphWithoutCycles(cycleEliminationPriority));
			System.out.println("reduced component:\n");
			System.out.println(component[x].getReducedGraph());
			System.out.println("layer assignment:\n");
			*/
			taskMonitor.setPercentCompleted(20 + ((40 * (x * 3)) / numComponents / 3));
			taskMonitor.setStatus("making acyclic transitive reduction");
			Thread.yield();

			if (canceled)
				return;

			reducedTmp[x] = component[x].getReducedGraph();
			taskMonitor.setPercentCompleted(20 + ((40 * ((x * 3) + 1)) / numComponents / 3));
			taskMonitor.setStatus("layering nodes vertically");
			Thread.yield();
					
			if (canceled)
				return;

			layer[x] = reducedTmp[x].getVertexLayers();
			LinkedList<Integer> layerWithDummy = new LinkedList<Integer>();
			for (int i = 0; i < layer[x].length; i++)
				layerWithDummy.add(new Integer(layer[x][i]));
			/*
			int y;
			for (y=0;y<layer[x].length;y++) {
			    System.out.println("" + y + " : " + layer[x][y]);
			}
			System.out.println("horizontal position:\n");
			*/
			
			/* Insertion of the dummy nodes in the graph */
			Edge[] allEdges = component[x].GetEdges(); 
			LinkedList<Edge> edgesWithAdd = new LinkedList<Edge>();
			int dummyStart = component[x].getNodecount();
			dummyStartForComp[x] = dummyStart;
			dummy2Edge[x] = new HashMap<Integer, Edge>();
			
			System.out.println(allEdges.length);
			for (int i = 0; i < allEdges.length; i++)
			{
				int from = allEdges[i].getFrom();
				int to = allEdges[i].getTo();
				
				if (layer[x][from] == layer[x][to] + 1)
				{
					edgesWithAdd.add(allEdges[i]);					
				}
				else 
				{
					if (layer[x][from] < layer[x][to])
					{
						int tmp = from;
						from = to;
						to = tmp;
					}
					layerWithDummy.add(new Integer(layer[x][to] + 1));
					dummy2Edge[x].put(new Integer(layerWithDummy.size() - 1), allEdges[i]);
					edgesWithAdd.add(new Edge(layerWithDummy.size() - 1, to));
					for (int j = layer[x][to] + 2; j < layer[x][from]; j++)
					{
						layerWithDummy.add(new Integer(j));
						dummy2Edge[x].put(new Integer(layerWithDummy.size() - 1), allEdges[i]);									
						edgesWithAdd.add(new Edge(layerWithDummy.size() - 1, layerWithDummy.size() - 2));						
					}
					edgesWithAdd.add(new Edge(from, layerWithDummy.size() - 1));
				}				
			}
			
			allEdges = new Edge[edgesWithAdd.size()];
			edgesWithAdd.toArray(allEdges);
			
			reduced[x] = new Graph(layerWithDummy.size(), allEdges);
			reduced[x].setDummyNodesStart(dummyStart);
			reduced[x].setReduced(true);
			
			int[] layerNew = new int[layerWithDummy.size()];
			iter = layerWithDummy.iterator();
			for (int i = 0; i < layerNew.length; i++)
				layerNew[i] = ((Integer) iter.next()).intValue();
			layer[x] = layerNew;			
						
			taskMonitor.setPercentCompleted(20 + ((40 * ((x * 3) + 2)) / numComponents / 3));
			taskMonitor.setStatus("positioning nodes within layer");
			Thread.yield();

			if (canceled) 
				return;

			horizontalPosition[x] = reduced[x].getHorizontalPositionReverse(layer[x]);

			/*
			for (y=0;y<horizontalPosition[x].length;y++) {
			    System.out.println("" + y + " : " + horizontalPosition[x][y]);
			}
			*/
		}
		
		int resize = renumber.length;
		for (int i = 0; i < component.length; i++)
			resize += layer[i].length - dummyStartForComp[i];
		int[] newRenumber = new int[resize];
		int[] newcI = new int[resize];
		for (int i = 0; i < renumber.length; i++)
		{
			newRenumber[i] = renumber[i];
			newcI[i] = cI[i];
		}
		int t = renumber.length;
		for (int i = 0; i < reduced.length; i++)
		{
			for (int j = reduced[i].getDummyNodesStart(); j < reduced[i].getNodecount(); j++)
			{
				newRenumber[t] = j;
				newcI[t] = i;
				t++;
			}
		}
		renumber = newRenumber;
		cI = newcI;
		
		edges = new LinkedList<Edge>();
		for (int i = 0; i < reduced.length; i++)
		{
			edge =  reduced[i].GetEdges();
			for (int j = 0; j < edge.length; j++)
			{// uzasna budzevina!!!!!!
				int from = -1, to = -1;
				for (int k = 0; k < cI.length; k++)
				{
					if (cI[k] == i && renumber[k] == edge[j].getFrom())
						from = k;
					if (cI[k] == i && renumber[k] == edge[j].getTo())
						to = k;
					if (from != -1 && to != -1)
						break;
				}			
				
				edges.add(new Edge(from, to));//edges.add(new Edge(to, from));
			}
		}
		edge = new Edge[edges.size()];
		edges.toArray(edge);
		graph = new Graph (resize, edge);
		

		taskMonitor.setPercentCompleted(60);
		taskMonitor.setStatus("Repositioning nodes in view");
		Thread.yield();

		if (canceled)
			return;

		/* order nodeviews by layout order */
		HierarchyFlowLayoutOrderNode[] flowLayoutOrder = new HierarchyFlowLayoutOrderNode[resize];

		for (x = 0; x < resize; x++) {
			if (x < numLayoutNodes)
				flowLayoutOrder[x] = new HierarchyFlowLayoutOrderNode(nodeView[x], cI[x],
			                                                      reduced[cI[x]].getNodecount(),
			                                                      layer[cI[x]][renumber[x]],
			                                                      horizontalPosition[cI[x]][renumber[x]], x);
			else flowLayoutOrder[x] = new HierarchyFlowLayoutOrderNode(null, cI[x],
                    reduced[cI[x]].getNodecount(),
                    layer[cI[x]][renumber[x]],
                    horizontalPosition[cI[x]][renumber[x]], x);
				
			nodes2HFLON.put(x, flowLayoutOrder[x]);
		}

		Arrays.sort(flowLayoutOrder);
		
		int lastComponent = -1;
		int lastLayer = -1;
		int startBandY = topEdge;
		int cleanBandY = topEdge;
		int startComponentX = leftEdge;
		int cleanComponentX = leftEdge;
		int startLayerY = topEdge;
		int cleanLayerY = topEdge;
		int cleanLayerX = leftEdge;
		int[] layerStart = new int[numLayoutNodes + 1];
			
		/* layout nodes which are selected */
		int nodeIndex, lastComponentEnd = -1;
		for (nodeIndex = 0; nodeIndex < resize; nodeIndex++) {
			HierarchyFlowLayoutOrderNode node = flowLayoutOrder[nodeIndex];
			int currentComponent = node.componentNumber;
			int currentLayer = node.layer;
			NodeView currentView = node.nodeView;

			taskMonitor.setPercentCompleted(60 + ((40 * (nodeIndex + 1)) / resize));
			taskMonitor.setStatus("layering nodes vertically");
			Thread.yield();
			
			if (canceled)
				return;
			
			if (lastComponent == -1) {
				/* this is the first component */
				lastComponent = currentComponent;
				lastLayer = currentLayer;
				layerStart[currentLayer] = -1;				
			}

			if (lastComponent != currentComponent) 
			{
				/* new component */
				// first call function for Horizontal Positioning of nodes in lastComponent
				int[] minXArray = new int[1];
				int maxX = HorizontalNodePositioning(nodeIndex - flowLayoutOrder[nodeIndex - 1].componentSize, nodeIndex - 1, flowLayoutOrder, graph,
													renumber, cI, dummyStartForComp, minXArray);
				int minX = minXArray[0];
				lastComponentEnd = nodeIndex - 1;
								
				for (int i = nodeIndex - flowLayoutOrder[nodeIndex - 1].componentSize; i <= nodeIndex - 1; i++)
					flowLayoutOrder[i].xPos -= (minX - startComponentX);
				maxX -= (minX - startComponentX);
								
				layerStart[lastLayer] = startComponentX;

				/* initialize for new component */
				startComponentX = cleanComponentX + componentSpacing;
				if (maxX > startComponentX)
					startComponentX = maxX + componentSpacing;
				
				if (startComponentX > rightMargin) {
					/* new band */
					startBandY = cleanBandY + bandGap;
					cleanBandY = startBandY; 
					startComponentX = leftEdge;
					cleanComponentX = leftEdge;
				}

				startLayerY = startBandY;
				cleanLayerY = startLayerY;
				cleanLayerX = startComponentX;
				layerStart[currentLayer] = -1;
				
			} else if (lastLayer != currentLayer) 
				{
				/* new layer */
				layerStart[lastLayer] = startComponentX;

				startLayerY = cleanLayerY + nodeVerticalSpacing;
				cleanLayerY = startLayerY;
				cleanLayerX = startComponentX;
				layerStart[currentLayer] = -1;
			}

			node.setXPos(cleanLayerX);
			node.setYPos(startLayerY);
			cleanLayerX += nodeHorizontalSpacing;

			int currentBottom;
			int currentRight; 
			if (currentView != null)
			{
				currentBottom = startLayerY + (int) (currentView.getHeight());
				currentRight = cleanLayerX + (int) (currentView.getWidth());
			}
			else 
			{
				currentBottom = startLayerY;
				currentRight = cleanLayerX;
			}

			if (currentBottom > cleanBandY)
				cleanBandY = currentBottom;

			if (currentRight > cleanComponentX)
				cleanComponentX = currentRight;

			if (currentBottom > cleanLayerY)
				cleanLayerY = currentBottom;

			if (currentRight > cleanLayerX)
				cleanLayerX = currentRight;

			lastComponent = currentComponent;
			lastLayer = currentLayer;

		}	
		
		if (canceled)
			return;
		
		/* Set horizontal positions of last component */ 
		int[] minXArray = new int[1];
		HorizontalNodePositioning(lastComponentEnd + 1, resize - 1, flowLayoutOrder, graph,
											renumber, cI, dummyStartForComp, minXArray);
		int minX = minXArray[0];
		for (int i = lastComponentEnd + 1; i < resize; i++)
			flowLayoutOrder[i].xPos -= (minX - startComponentX);

		
		/* Map edges to edge views in order to map dummy nodes to edge bends properly */
		iter = networkView.getEdgeViewsIterator();
		while (iter.hasNext()) {
			EdgeView ev = (EdgeView) (iter.next());
			Integer edgeFrom = (Integer) ginyIndex2Index.get(new Integer(ev.getEdge().getSource()
			                                                               .getRootGraphIndex()));
			Integer edgeTo = (Integer) ginyIndex2Index.get(new Integer(ev.getEdge().getTarget()
			                                                             .getRootGraphIndex()));
			if ((edgeFrom == null) || (edgeTo == null)) {
				// Must be from an unselected node
				continue;
			}
			if ((numSelectedNodes <= 1)
			    || ((edgeFrom.intValue() < numSelectedNodes)
			       && (edgeTo.intValue() < numSelectedNodes))) {
				/* add edge to graph */
				Edge theEdge = component[cI[edgeFrom.intValue()]].GetTheEdge(renumber[edgeFrom.intValue()], renumber[edgeTo.intValue()]);
				
				if (myEdges2EdgeViews[cI[edgeFrom.intValue()]] == null)
					myEdges2EdgeViews[cI[edgeFrom.intValue()]] = new HashMap<Edge, EdgeView>();
				myEdges2EdgeViews[cI[edgeFrom.intValue()]].put(theEdge, ev);
			}
		}
		
		/* Delete edge anchors */
		iter = networkView.getEdgeViewsIterator();
		while (iter.hasNext()) {
			((EdgeView) iter.next()).getBend().removeAllHandles();
		} /* Done removing edge anchors */
		
		iter = networkView.getEdgeViewsIterator();;
		for (nodeIndex = 0; nodeIndex < resize; nodeIndex++) {
			HierarchyFlowLayoutOrderNode node = flowLayoutOrder[nodeIndex];
			if (node.nodeView != null)
			{
				NodeView currentView = node.nodeView;
				currentView.setOffset(node.getXPos(), node.getYPos());
			}
		}		 
		for (nodeIndex = 0; nodeIndex < resize; nodeIndex++) {
			HierarchyFlowLayoutOrderNode node = flowLayoutOrder[nodeIndex];
			if (node.nodeView == null)
			{			
				Edge theEdge = (Edge) dummy2Edge[cI[node.graphIndex]].get(new Integer(renumber[node.graphIndex]));								
				EdgeView ev = myEdges2EdgeViews[cI[node.graphIndex]].get(theEdge);				
				if (ev != null)
				{
					int source = ginyIndex2Index.get(ev.getEdge().getSource().getRootGraphIndex());
					int target = ginyIndex2Index.get(ev.getEdge().getTarget().getRootGraphIndex());
					double k = (nodeView[target].getYPosition() - nodeView[source].getYPosition()) / 
						(nodeView[target].getXPosition() - nodeView[source].getXPosition());					
					
					double xPos = nodeView[source].getXPosition();
					if (k != 0) 
						xPos += (node.yPos - nodeView[source].getYPosition())/ k;
					
					Point2D p2d = new Point2D.Double();
					p2d.setLocation(xPos, node.yPos);
					ev.getBend().addHandle(p2d);
				}
			}
		}
		for (nodeIndex = 0; nodeIndex < resize; nodeIndex++) {
			HierarchyFlowLayoutOrderNode node = flowLayoutOrder[nodeIndex];
			if (node.nodeView == null)
			{			
				Edge theEdge = dummy2Edge[cI[node.graphIndex]].get(new Integer(renumber[node.graphIndex]));
				EdgeView ev = myEdges2EdgeViews[cI[node.graphIndex]].get(theEdge);
				
				Point2D[] bends = ev.getBend().getDrawPoints();
				for (int i = 0; i < bends.length; i++)
					if (bends[i].getY() == node.yPos)
					{
						Point2D p2d = new Point2D.Double();
						p2d.setLocation(node.xPos, node.yPos);
						ev.getBend().moveHandle(i, p2d);
						break;
					}				
			}
		}
		
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("hierarchical layout complete");
	}

	/**
	 * Sum length of edges between 2 consecutive layers. This is used for getting as compact 
	 * layout as possible, we want to minimize this sum by horizontal coordinate assignment
	 * @param nodes
	 * @param edgesFrom
	 * @param edgesTo
	 * @param x
	 * @param direct
	 * @param startInd
	 * @param endInd
	 * @return
	 */
	private double EdgeLength2Layers (HierarchyFlowLayoutOrderNode[] nodes, LinkedList<Integer>[] edgesFrom, LinkedList<Integer>[] edgesTo,
									int x, int direct, int startInd, int endInd)
	{
		double layerMin = 0;
		
		HashMap<Integer, HierarchyFlowLayoutOrderNode> nodesBak2HFLON = new HashMap<Integer, HierarchyFlowLayoutOrderNode>();
		for (int i = startInd; i <= endInd; i++)
			nodesBak2HFLON.put(new Integer(nodes[i].graphIndex), nodes[i]);
		
		if (direct == -1)
		{
			int xHlp = x;
			while (xHlp < nodes.length && nodes[xHlp].layer == nodes[x].layer)
			{
				Iterator iterToHlp = edgesTo[nodes[xHlp].graphIndex].iterator();
				double curPos = nodes[xHlp].xPos;
				while (iterToHlp.hasNext())
				{
					Integer neigh = (Integer) iterToHlp.next();
					layerMin += Math.abs(nodesBak2HFLON.get(neigh).xPos - curPos) / ((double)nodeHorizontalSpacing);
					// mozda ako je ivica izmedju 2 dummy cvora da duplira daljinu
				}
				xHlp++;
			}
			xHlp = x - 1;
			while (xHlp >= 0 && nodes[xHlp].layer == nodes[x].layer)
			{
				Iterator iterToHlp = edgesTo[nodes[xHlp].graphIndex].iterator();
				double curPos = nodes[xHlp].xPos;
				while (iterToHlp.hasNext())
				{
					Integer neigh = (Integer) iterToHlp.next();
					layerMin += Math.abs(nodesBak2HFLON.get(neigh).xPos - curPos) / ((double)nodeHorizontalSpacing);
				}
				xHlp--;
			}			
		}
		else
		{
			int xHlp = x;
			while (xHlp < nodes.length && nodes[xHlp].layer == nodes[x].layer)
			{
				Iterator iterFromHlp = edgesFrom[nodes[xHlp].graphIndex].iterator();
				double curPos = nodes[xHlp].xPos;
				while (iterFromHlp.hasNext())
				{
					Integer neigh = (Integer) iterFromHlp.next();
					layerMin += Math.abs(nodesBak2HFLON.get(neigh).xPos - curPos) / ((double)nodeHorizontalSpacing);
				}
				xHlp++;
			}
			xHlp = x - 1;
			while (xHlp >= 0 && nodes[xHlp].layer == nodes[x].layer)
			{
				Iterator iterFromHlp = edgesFrom[nodes[xHlp].graphIndex].iterator();
				double curPos = nodes[xHlp].xPos;
				while (iterFromHlp.hasNext())
				{
					Integer neigh = (Integer) iterFromHlp.next();
					layerMin += Math.abs(nodesBak2HFLON.get(neigh).xPos - curPos) / ((double)nodeHorizontalSpacing);
				}
				xHlp--;
			}			
		}
		
		return layerMin;
	}
	
	/**
	 * Function which does actual horizontal coordinate assignment of nodes
	 * @param startInd - in nodes array
	 * @param endInd - in nodes array 
	 * @param nodes
	 * @param theGraph
	 * @param renumber
	 * @param cI
	 * @param dummyStarts - dummy nodes are always in the end, so we always remember just 
	 * 						the index of the first dummy in a graph component
	 * @param minX2Return
	 * @return
	 */
	private int HorizontalNodePositioning(int startInd, int endInd, HierarchyFlowLayoutOrderNode[] nodes, Graph theGraph,
											int[] renumber, int[] cI, int[] dummyStarts, int[] minX2Return)
	{
		/* sort nodes in layer in order of coordinate assignment - first dummy nodes, then sorted by fan-in + fan-out */
		LinkedList<Integer>[] edgesFrom = theGraph.GetEdgesFrom();
		LinkedList<Integer>[] edgesTo = theGraph.GetEdgesTo();
		
		LayerOrderNode[] lon = new LayerOrderNode[endInd - startInd + 1]; 
		HashMap<Integer, LayerOrderNode> ind2Lon = new HashMap<Integer, LayerOrderNode>();
		for (int i = 0; i <= endInd - startInd; i++)
		{
			boolean dum = false;	
			if (renumber[nodes[startInd + i].graphIndex] >= dummyStarts[cI[nodes[startInd + i].graphIndex]])
				dum = true;
			lon[i] = new LayerOrderNode (startInd + i, dum, edgesFrom[nodes[startInd + i].graphIndex].size() 
						+ edgesTo[nodes[startInd + i].graphIndex].size(), nodes[startInd + i].layer);		
			ind2Lon.put(new Integer(startInd + i), lon[i]);
		}
		Arrays.sort(lon); 
			
		int cur = 0, x = lon[0].GetIndex(), direct = 1, 
			noOfSteps = 10 * (endInd - startInd + 1) - (10 - 1) / 2;//, curLayer = nodes[x].layer;
		double layerMin = Integer.MAX_VALUE; 
		boolean newLayer = true, dirFirst = true;
		for (int dx = 0; dx < noOfSteps; dx++)
		{
			if (newLayer) 
			{
				layerMin = EdgeLength2Layers(nodes, edgesFrom, edgesTo, x, direct, startInd, endInd);
				newLayer = false;
			}
			
			int idealPosXUp = 0, idealPosXDown = 0, neighsCountUp = 0, neighsCountDown = 0;
			Iterator iterFrom = edgesFrom[nodes[x].graphIndex].iterator();
			Iterator iterTo = edgesTo[nodes[x].graphIndex].iterator();
			
			while (iterFrom.hasNext() && (direct == 1 || (edgesTo[nodes[x].graphIndex].isEmpty() && direct == -1)))  
			{
				Integer neigh = (Integer) iterFrom.next();
				if (nodes2HFLON.get(neigh).layer == nodes[x].layer - 1)
				{
					idealPosXUp += nodes2HFLON.get(neigh).xPos;
					neighsCountUp++;
					// enforcing the impact of dummy nodes, it straightens the lines connected to dummy nodes
					if (renumber[nodes2HFLON.get(neigh).graphIndex] >= dummyStarts[cI[nodes2HFLON.get(neigh).graphIndex]]
					   && ind2Lon.get(new Integer(x)).GetIsDummy())
					{
						idealPosXUp += 4 * nodes2HFLON.get(neigh).xPos;
						neighsCountUp += 4;
					} 
				}
			}		
			
			while (iterTo.hasNext() && (direct == -1 || (edgesFrom[nodes[x].graphIndex].isEmpty() && direct == 1))) 
			{
				Integer neigh = (Integer) iterTo.next();
				if (nodes2HFLON.get(neigh).layer == nodes[x].layer + 1)
				{
					idealPosXDown += nodes2HFLON.get(neigh).xPos;
					neighsCountDown++;
					if (renumber[nodes2HFLON.get(neigh).graphIndex] >= dummyStarts[cI[nodes2HFLON.get(neigh).graphIndex]]
						&& ind2Lon.get(new Integer(x)).GetIsDummy())
					{
						idealPosXDown += 4 * nodes2HFLON.get(neigh).xPos;
						neighsCountDown += 4;
					}				
				}
			}
			
			if (neighsCountUp > 0 || neighsCountDown > 0)
			{
				int idealPosX;
				if (neighsCountUp == 0) 
					idealPosX = idealPosXDown / neighsCountDown;
				else if (neighsCountDown == 0)
					idealPosX = idealPosXUp / neighsCountUp;
				else 
					idealPosX = (idealPosXUp / neighsCountUp + idealPosXDown / neighsCountDown) / 2;
				
				if (idealPosX % nodeHorizontalSpacing != 0)
					if (idealPosX - nodes[x].xPos < nodeHorizontalSpacing)
						idealPosX = ((int)(idealPosX / nodeHorizontalSpacing))* nodeHorizontalSpacing;
					else if (nodes[x].xPos - idealPosX < nodeHorizontalSpacing)
						idealPosX = ((int)(idealPosX / nodeHorizontalSpacing) + 1)* nodeHorizontalSpacing;
					else
						idealPosX = ((int)((idealPosX / nodeHorizontalSpacing) + 0.5))* nodeHorizontalSpacing;
								
							
				int oldXPos = nodes[x].xPos;
				nodes[x].xPos = idealPosX;
				HierarchyFlowLayoutOrderNode[] nodesBak = new HierarchyFlowLayoutOrderNode[nodes.length];
				for (int i = 0; i < nodes.length; i++)
					nodesBak[i] = new HierarchyFlowLayoutOrderNode (nodes[i]);
				if ((idealPosX > oldXPos) && (x < endInd) && (nodes[x + 1].layer == nodes[x].layer))						
				{
					boolean q = false;
					for (int i = x + 1; i <= endInd && !q; i++)
					{
					//	System.out.print(nodesBak[i].xPos + " " + nodesBak[i+1].xPos + " " + x + " " + i + " ");
						if (nodesBak[i].layer == nodesBak[x].layer && nodesBak[i].xPos < nodesBak[i-1].xPos + nodeHorizontalSpacing)
						{
							nodesBak[i].xPos = nodesBak[i-1].xPos + nodeHorizontalSpacing;
							/*if (((LayerOrderNode)ind2Lon.get(new Integer(x))).GetPriority() < ((LayerOrderNode)ind2Lon.get(new Integer(i))).GetPriority())									
								q = true;*/
						}
						else break;
					}
					double w = EdgeLength2Layers(nodesBak, edgesFrom, edgesTo, x, direct, startInd, endInd);
					if (!q && w <= layerMin)
					{
						layerMin = w;
						for (int i = x + 1; i <= endInd; i++)
							if (nodes[i].layer == nodes[x].layer && nodes[i].xPos < nodes[i-1].xPos + nodeHorizontalSpacing)
							{
								nodes[i].xPos = nodes[i-1].xPos + nodeHorizontalSpacing;							
							}
							else 
							{							
								break;
							}
					}
					else 
					{
						if (nodes[x+1].layer == nodes[x].layer)
							nodes[x].xPos = nodes[x+1].xPos - nodeHorizontalSpacing; 
						else nodes[x].xPos = oldXPos;						
					}
				}
				else if ((idealPosX < oldXPos) && (x > 0) && (nodes[x - 1].layer == nodes[x].layer))						
				{
					boolean q = false;
					for (int i = x - 1; i >= 0 && !q; i--)
					{
						//System.out.print(nodesBak[i].xPos + " " + nodesBak[i+1].xPos + " " + x + " " + i + " ");
						if (nodesBak[i].layer == nodesBak[x].layer && nodesBak[i].xPos > nodesBak[i+1].xPos - nodeHorizontalSpacing)
						{
							nodesBak[i].xPos = nodesBak[i+1].xPos - nodeHorizontalSpacing;
							/*if (((LayerOrderNode)ind2Lon.get(new Integer(x))).GetPriority() < ((LayerOrderNode)ind2Lon.get(new Integer(i))).GetPriority())
							{
								q = true;								
							}*/
						}
						else break;
					}
					double w = EdgeLength2Layers(nodesBak, edgesFrom, edgesTo, x, direct, startInd, endInd);
					if (!q && w <= layerMin)
					{
						layerMin = w;
						for (int i = x - 1; i >= 0; i--)
							if (nodes[i].layer == nodes[x].layer && nodes[i].xPos > nodes[i+1].xPos - nodeHorizontalSpacing)
							{
								nodes[i].xPos = nodes[i+1].xPos - nodeHorizontalSpacing;							
							}
							else 
							{								
								break; 
							}
					}
					else 
					{
						if (nodes[x-1].layer == nodes[x].layer)
							nodes[x].xPos = nodes[x-1].xPos + nodeHorizontalSpacing; 
						else nodes[x].xPos = oldXPos;						
					}
				}				
			}
			
			if (dx % (startInd - endInd) == 0 && dx != 0)
				if (!dirFirst)
				{
					direct *= -1;
					dirFirst = true;
				}
				else dirFirst = false;
			
			cur = (cur + direct + lon.length) % lon.length; //(cur + 1) % lon.length;
			if (nodes[x].layer != nodes[lon[cur].GetIndex()].layer)
			{
				newLayer = true;				
			}
			x = lon[cur].GetIndex();
		}		
		
		int maxX = Integer.MIN_VALUE, minX = Integer.MAX_VALUE; 
		for (int i = startInd; i <= endInd; i++)
		{
			if (nodes[i].getXPos() > maxX)
				maxX = nodes[i].getXPos();
			if (nodes[i].getXPos() < minX)
				minX = nodes[i].getXPos();
		}
		
		minX2Return[0] = minX;
		return maxX;
	}
	
	
	/**
	* Non-blocking call to interrupt the task.
	*/
	public void halt() {
		canceled = true;
	}

	/**
	 * Overrides for LayoutAlgorithm support
	 */
	public String getName() {
		return "hierarchical";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Hierarchical Layout";
	}

	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}

	protected void initialize_properties() {
		layoutProperties.add(new Tunable("nodeHorizontalSpacing",
		                                 "Horizontal spacing between nodes", Tunable.INTEGER,
		                                 new Integer(64)));
		layoutProperties.add(new Tunable("nodeVerticalSpacing", "Vertical spacing between nodes",
		                                 Tunable.INTEGER, new Integer(32)));
		layoutProperties.add(new Tunable("componentSpacing", "Component spacing", Tunable.INTEGER,
		                                 new Integer(64)));
		layoutProperties.add(new Tunable("bandGap", "Band gap", Tunable.INTEGER, new Integer(64)));
		layoutProperties.add(new Tunable("leftEdge", "Left edge margin", Tunable.INTEGER,
		                                 new Integer(32)));
		layoutProperties.add(new Tunable("topEdge", "Top edge margin", Tunable.INTEGER,
		                                 new Integer(32)));
		layoutProperties.add(new Tunable("rightMargin", "Right edge margin", Tunable.INTEGER,
		                                 new Integer(7000)));
		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));
		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
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

		Tunable t = layoutProperties.get("nodeHorizontalSpacing");

		if ((t != null) && (t.valueChanged() || force))
			nodeVerticalSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("nodeVerticalSpacing");

		if ((t != null) && (t.valueChanged() || force))
			nodeVerticalSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("componentSpacing");

		if ((t != null) && (t.valueChanged() || force))
			componentSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("bandGap");

		if ((t != null) && (t.valueChanged() || force))
			bandGap = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("leftEdge");

		if ((t != null) && (t.valueChanged() || force))
			leftEdge = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("topEdge");

		if ((t != null) && (t.valueChanged() || force))
			topEdge = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("rightMargin");

		if ((t != null) && (t.valueChanged() || force))
			rightMargin = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("selected_only");

		if ((t != null) && (t.valueChanged() || force))
			selected_only = ((Boolean) t.getValue()).booleanValue();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	/**
	* Sets the Task Monitor.
	*
	* @param taskMonitor TaskMonitor Object.
	*/
	public void setTaskMonitor(TaskMonitor tm) {
		taskMonitor = tm;
	}

	/**
	* Gets the Task Title.
	*
	* @return human readable task title.
	*/
	public String getTitle() {
		return new String("Hierarchical Layout");
	}
}

/**
 * Class which we use to determine the order by which we traverse nodes when 
 * we calculate their horizontal coordinate. We traverse it layer by layer, inside 
 * the layer we first place dummy nodes and then sorted by degree of the node. * 
 * @author Aleksandar Nikolic
 *
 */
class LayerOrderNode implements Comparable
{
	private int index;
	private boolean isDummy;
	private int degree;
	private int priority;
	private int layer;

	public LayerOrderNode(int index, boolean isDummy, int degree, int layer)
	{
		this.index = index;
		this.isDummy = isDummy;
		this.degree = degree;
		this.layer = layer;
		
		if (isDummy)
			priority = 20;
		else if (degree < 5)
			priority = 5;
		else if (degree < 10)
			priority = 10;
		else priority = 15; //priority = degree;
	}
	
	public int compareTo(Object arg0) 
	{
		LayerOrderNode second = (LayerOrderNode) arg0;
		
		if (layer != second.layer)
			return (layer - second.layer);//(second.layer - layer); 
		
		if (isDummy && !second.isDummy)
			return -1;
		
		if (!isDummy && second.isDummy)
			return 1; 
		
		return (second.degree - degree);		
	}
	
	public int GetIndex()
	{
		return index;
	}	
	
	public int GetPriority()
	{
		return priority;
	}
	
	public boolean GetIsDummy()
	{
		return isDummy;
	}
}

