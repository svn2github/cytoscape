/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package csplugins.layout.algorithms.bioLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Comparator;
import java.util.HashMap;

import cytoscape.*;
import cytoscape.view.*;
import giny.view.*;

import csplugins.layout.algorithms.bioLayout.LayoutNode;
import csplugins.layout.algorithms.bioLayout.LayoutEdge;
import csplugins.layout.algorithms.bioLayout.Profile;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;


/**
 * 
 *
 * @author <a href="mailto:scooter@cgl.ucsf.edu">Scooter Morris</a>
 * @version 0.9
 */

public class LayoutPartition {
	private ArrayList<LayoutNode> nodeList;
	private ArrayList<LayoutEdge> edgeList;
	private static HashMap<CyNode,LayoutNode> nodeToLayoutNode = null;
	private int nodeIndex = 0;
	// Keep track of the node min and max values
	private double maxX = 0;
	private double maxY = 0;
	private double minX = 0;
	private double minY = 0;
	private double width = 0;
	private double height = 0;

  // private constants
  private static final int m_NODE_HAS_NOT_BEEN_SEEN = 0;
  private static final int m_NODE_HAS_BEEN_SEEN     = 1;

	public LayoutPartition() {
		nodeList = new ArrayList();
		edgeList = new ArrayList();
		LayoutNode.reset();
		if (nodeToLayoutNode == null)
			nodeToLayoutNode = new HashMap();
	}

	public void addEdge(LayoutEdge edge) {
		edgeList.add(edge);
	}

	public void addEdge(CyEdge edge) {
		LayoutEdge newEdge = new LayoutEdge(edge);
		edgeList.add(newEdge);
	}

	public void addEdge(CyEdge edge, String edgeAttribute) {
		LayoutEdge newEdge = new LayoutEdge(edge);
		newEdge.setWeight(edgeAttribute);
		edgeList.add(newEdge);
	}

	public double getMaxX() { return maxX; }
	public double getMaxY() { return maxY; }
	public double getMinX() { return minX; }
	public double getMinY() { return minY; }
	public double getWidth() { return width; }
	public double getHeight() { return height; }

	public void fixEdges() {
		Iterator edgeIter = edgeList.iterator();
		while (edgeIter.hasNext()) {
			// Get the "layout edge"
			LayoutEdge lEdge = (LayoutEdge)edgeIter.next();
			// Get the underlying edge
			CyEdge edge = lEdge.getEdge();
			CyNode target = (CyNode)edge.getTarget();
			CyNode source = (CyNode)edge.getSource();
			if (nodeToLayoutNode.containsKey(source) && 
					nodeToLayoutNode.containsKey(target)) {
				// Add the connecting nodes
				lEdge.addNodes((LayoutNode)nodeToLayoutNode.get(source),
				               (LayoutNode)nodeToLayoutNode.get(target));
			}
		}
	}

	public void addNode(LayoutNode node) {
		nodeList.add(node);
	}

	public void addNode(NodeView nv) {
		CyNode node = (CyNode)nv.getNode();
		LayoutNode v = new LayoutNode(nv, nodeIndex++, true);
		nodeList.add(v);
		nodeToLayoutNode.put(node,v);
		this.minX = v.getMinX();
		this.minY = v.getMinY();
		this.maxX = v.getMaxX();
		this.maxY = v.getMaxY();
		this.width = v.getTotalWidth();
		this.height = v.getTotalHeight();
	}

	public void trimToSize() {
		nodeList.trimToSize();
		edgeList.trimToSize();
	}

	public int size() { return nodeList.size(); }

	public List<LayoutNode> getNodeList() { return nodeList; }
	public List<LayoutEdge> getEdgeList() { return edgeList; }

	// Static routines

	/**
	 * Partition the graph -- this builds the LayoutEdge and LayoutNode
	 * arrays as a byproduct.  The algorithm for this was taken from
	 * algorithms/graphPartition/SGraphPartition.java.
	 */

	public static List partition(CyNetwork network, CyNetworkView networkView, 
	                             boolean selectedOnly, String edgeAttribute) {
		ArrayList partitions = new ArrayList();
		
		Iterator nodeViewIter = networkView.getNodeViewsIterator();

		OpenIntIntHashMap nodesSeenMap = new OpenIntIntHashMap(network.getNodeCount());
		OpenIntObjectHashMap nodesToViews = new 
						OpenIntObjectHashMap(network.getNodeCount());

		// Initialize the map
		while (nodeViewIter.hasNext()) {
			NodeView nv = (NodeView)nodeViewIter.next();
			int node = nv.getNode().getRootGraphIndex();
			nodesSeenMap.put(node, m_NODE_HAS_NOT_BEEN_SEEN);
			nodesToViews.put(node, nv);
		}

		// OK, now get new iterators and traverse the graph
		Iterator nodeIter = null;
		if (selectedOnly) {
			nodeIter = ((CyNetwork)network).getSelectedNodes().iterator();
		} else {
			nodeIter = network.nodesIterator();
		}

		while (nodeIter.hasNext()) {
			CyNode node = (CyNode)nodeIter.next();
			int nodeIndex = node.getRootGraphIndex();

			// Have we seen this already?
			if (nodesSeenMap.get(nodeIndex) == m_NODE_HAS_BEEN_SEEN) continue;

			// Nope, first time
			LayoutPartition part = new LayoutPartition();

			nodesSeenMap.put(nodeIndex, m_NODE_HAS_BEEN_SEEN);

			// Traverse through all connected nodes
			traverse(network, networkView, nodesSeenMap, 
			         nodesToViews, node, part, edgeAttribute);

			// Done -- finalize the parition
			part.trimToSize();

			// Finally, now that we're sure we've touched all of our
			// nodes.  Fix up our edgeLayout list to have all of our
			// layoutNodes
			part.fixEdges();
		
			partitions.add(part);
		}

		// Now sort the partitions based on the partition's node count
		Object parts[] = partitions.toArray();
		Arrays.sort(parts, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					LayoutPartition p1 = (LayoutPartition)o1;
					LayoutPartition p2 = (LayoutPartition)o2;
					return (p2.size() - p1.size());
				}

				public boolean equals(Object obj) { return false; }
			});
		
		return Arrays.asList(parts);
	}

  /**
    * This method traverses nodes connected to the specified node.
    * @param network				The CyNetwork we are laying out
    * @param networkView		The CyNetworkView we are laying out
    * @param nodesSeenMap		A map that specifies which nodes have been seen.
    * @param nodesToViews		A map that maps between nodes and views
    * @param node						The node to search for connected nodes.
    * @param partition			The partition that holds all of the nodes and edges.
    * @param edgeAttribute	A String that is the name of the attribute to use
		*                     	for weights
    */
  private static void traverse(CyNetwork network,
	                             CyNetworkView networkView,
	                             OpenIntIntHashMap nodesSeenMap,
                               OpenIntObjectHashMap nodesToViews,
                               CyNode node, LayoutPartition partition,
	                             String edgeAttribute)
  {
		int nodeIndex = node.getRootGraphIndex();

		// Get the nodeView
		NodeView nv = (NodeView)nodesToViews.get(nodeIndex);

		// Add this node to the partition
		partition.addNode(nv);

		// Get the list of edges connected to this node
		int incidentEdges[] = network.getAdjacentEdgeIndicesArray(nodeIndex,
                                     true, true, true);

    // Iterate through each connected edge
    for (int i = 0; i < incidentEdges.length; i++)
    {
			// Get the actual edge
			CyEdge incidentEdge = (CyEdge)network.getEdge(incidentEdges[i]);
			// Make sure we clean up after any previous layouts
			EdgeView ev = networkView.getEdgeView(incidentEdge);
			ev.clearBends();

			// Add the edge to the partition
			partition.addEdge(incidentEdge);

			// Determine the node's index that is on the other side of the edge
			CyNode otherNode;
			if (incidentEdge.getSource() == node) {
				otherNode = (CyNode)incidentEdge.getTarget();
			} else {
				otherNode = (CyNode)incidentEdge.getSource();
			}

			int incidentNodeIndex = otherNode.getRootGraphIndex();

			// Have we seen the connecting node yet?
			if (nodesSeenMap.containsKey(incidentNodeIndex) &&
					nodesSeenMap.get(incidentNodeIndex) == m_NODE_HAS_NOT_BEEN_SEEN) {
				// Mak it as having been seen
				nodesSeenMap.put(incidentNodeIndex, m_NODE_HAS_BEEN_SEEN);

				// Traverse through this one
				traverse(network, networkView, nodesSeenMap, 
				         nodesToViews, otherNode, partition, edgeAttribute);
			}
		}
	}

}
