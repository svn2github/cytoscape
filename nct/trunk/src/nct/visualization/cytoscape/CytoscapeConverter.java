
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
import cytoscape.CytoscapeInit;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

import phoebe.PGraphView;
import giny.view.NodeView;

import java.util.Iterator;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

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
	private static Map<String,Integer> nodeIdMap; 
	private static Map<String,Map<String,Integer>> edgeIdMap; 

	static {
		rootGraph = Cytoscape.getRootGraph();
		nodeIdMap = new HashMap<String,Integer>(); 
		edgeIdMap = new HashMap<String,Map<String,Integer>>(); 
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
	public static CyNetwork convert(Graph<String,Double> graph) {

		if ( graph == null )
			return rootGraph.createNetwork(new int[] {}, new int[] {});

		Set<String> nodes = graph.getNodes();
		Set<Edge<String,Double>> edges = graph.getEdges();
		
		int[] nodeIds = new int[nodes.size()];
		int i = 0;
		for (String node : nodes) {
			if ( nodeIdMap.containsKey(node) ) {
				nodeIds[i] = nodeIdMap.get(node).intValue();
			} else {
				int nodeId = rootGraph.createNode();
				//nodeAttrs.setAttribute(Integer.toString(nodeId),"name",node);
				rootGraph.getNode(nodeId).setIdentifier(node);
				nodeIdMap.put(node,nodeId);
				nodeIds[i] = nodeId;
			}
			i++;
		}

		int[] edgeIds = new int[edges.size()];
		i = 0;
		for (Edge<String,Double> edge: edges) {
			Map<String,Integer> m = null;
			Integer I = null;
			if ( edgeIdMap.containsKey(edge.getSourceNode()) ) {
				m = edgeIdMap.get(edge.getSourceNode());
				I = m.get(edge.getTargetNode());
			} else if ( edgeIdMap.containsKey(edge.getTargetNode()) ) {
				m = edgeIdMap.get(edge.getTargetNode());
				I = m.get(edge.getSourceNode());
			}


			// edge already exists
			if ( I != null ) {
				edgeIds[i] = I.intValue();

			// create an edge
			} else {
				String source = edge.getSourceNode();
				String target = edge.getTargetNode();
				int sourceId = nodeIdMap.get(source).intValue();
				int targetId = nodeIdMap.get(target).intValue();
				int edgeId = rootGraph.createEdge(sourceId,targetId,false);
				// TODO make sure edge contains the actual description.
				//edgeAttrs.setAttribute(Integer.toString(edgeId),"name",graph.getEdgeDescription(source,target));
				rootGraph.getEdge(edgeId).setIdentifier(graph.getEdgeDescription(source, target));
				if ( !edgeIdMap.containsKey(source))
					edgeIdMap.put(source,new HashMap<String,Integer>());
				edgeIdMap.get(source).put(target,edgeId);
				edgeIds[i] = edgeId;
			}
			i++;
		}

		return rootGraph.createNetwork(nodeIds,edgeIds);
	}
}
