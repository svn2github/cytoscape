
//============================================================================
// 
//  file: CytoscapeConverter.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.visualization.cytoscape;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.giny.CytoscapeRootGraph;

import giny.view.NodeView;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import nct.graph.Graph;
import nct.graph.basic.BasicGraph;
import nct.graph.Edge;

/**
 * A utility class for converting an NCT graph into a CyNetwork.
 */
public class CytoscapeConverter {

	private static CytoscapeRootGraph rootGraph; 
	private static CyAttributes nodeAttrs; 
	private static CyAttributes edgeAttrs; 

	static {
		rootGraph = Cytoscape.getRootGraph();
		nodeAttrs = Cytoscape.getNodeAttributes();
		edgeAttrs = Cytoscape.getEdgeAttributes();
	}

	public static Graph<String,Double> convert(CyNetwork network) {

		Graph<String,Double> graph = new BasicGraph<String,Double>();

		if ( network == null )
			return graph; 

		for ( Iterator it = network.nodesIterator(); it.hasNext(); ) {
			CyNode node = (CyNode) it.next();
			graph.addNode(node.getIdentifier());
		}

		for ( Iterator it = network.edgesIterator(); it.hasNext(); ) {
			CyEdge edge = (CyEdge) it.next();

			Double weight;
			try {

			String weightString = edgeAttrs.getStringAttribute( edge.getIdentifier(), "interaction" );
			weight = Double.parseDouble(weightString);

			} catch (Exception e) { 
				weight = new Double(1.0);
			}

			graph.addEdge( edge.getSource().getIdentifier(), edge.getTarget().getIdentifier(), weight);

		}
		return graph;
	}

	/**
	 * Converts an NCT graph into a CyNetwork.
	 * @param graph The NCT graph to be converted.
	 * @return A new CyNetwork based on the input graph. 
	 */
	public static CyNetwork convert(Graph<String,Double> nctGraph, String title)
	{
	  if (nctGraph == null) return Cytoscape.createNetwork(title);

	  int i;

	  Set<String> nctNodes = nctGraph.getNodes();
	  int[] ginyNodes = new int[nctNodes.size()];
	  Map<String,Integer> nctToGinyNodeMap = new HashMap<String,Integer>(nctNodes.size());

	  i = 0;
	  for (String nctNode : nctNodes)
	  {
	    if (!nctToGinyNodeMap.containsKey(nctNode))
	    {
	      int ginyNode = rootGraph.createNode();
	      rootGraph.getNode(ginyNode).setIdentifier(nctNode);
	      nctToGinyNodeMap.put(nctNode, new Integer(ginyNode));

	      ginyNodes[i++] = ginyNode;
	    }
	  }

	  Set<Edge<String,Double>> nctEdges = nctGraph.getEdges();
	  int[] ginyEdges = new int[nctEdges.size()];

	  i = 0;
	  for (Edge<String,Double> nctEdge : nctEdges)
	  {
	    String nctSourceNode = nctEdge.getSourceNode();
	    String nctTargetNode = nctEdge.getTargetNode();
	    
	    int ginySourceNode = nctToGinyNodeMap.get(nctSourceNode).intValue();
	    int ginyTargetNode = nctToGinyNodeMap.get(nctTargetNode).intValue();

            int ginyEdge = rootGraph.createEdge(ginySourceNode, ginyTargetNode, false);
	    edgeAttrs.setAttribute(Integer.toString(ginyEdge), "interaction", nctEdge.getWeight().toString());
	    edgeAttrs.setAttribute(Integer.toString(ginyEdge), "description", nctEdge.getDescription());

	    ginyEdges[i++] = ginyEdge;
	  }

	  return Cytoscape.createNetwork(ginyNodes, ginyEdges, title);
	}
}
