
//============================================================================
// 
//  file: DualLayoutTask.java
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



package nct.visualization.cytoscape.dual;

import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.io.*;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import phoebe.PGraphView;

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
//import cytoscape.plugin.AbstractPlugin;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import cytoscape.view.GraphViewController;
import cytoscape.data.readers.GMLTree;


/**
 * The Thread that does the actual DualLayout calculations.
 */
public class DualLayoutTask extends Thread{
    private static String TITLE1 = "Split Graph";
    private static String SPLIT_STRING = "\\|";
    private double GAP = 100;
    private CyNetwork sifNetwork;
    HashMap<CyNode,Integer> node2Species;
    NodePairSet homologyPairSet;
    int k;
    private String title;

	private CyAttributes nodeAttrs;
	private CyAttributes edgeAttrs;

	/**
	 *
	 */
	private String getCNodeName( CyNode node ) {
		return nodeAttrs.getStringAttribute( node.getIdentifier(),"name" );
	}

    /**
     * @param sifNetwork A subgraph of the compatability 
     * graph that is a conserved complex.
     */
    public DualLayoutTask(CyNetwork sifNetwork) {
		this.sifNetwork = sifNetwork;

		this.nodeAttrs = Cytoscape.getNodeAttributes();
		this.edgeAttrs = Cytoscape.getEdgeAttributes();
    }

    /**
     * Creates the layout of the network using the
     * {@link SpringEmbeddedLayouter}.
     * @param view The graph view used for creating the layout.
     */
    public void layoutNetwork(PGraphView view) {
		view.getCanvas().paintImmediately();
		SpringEmbeddedLayouter layouter =
			new SpringEmbeddedLayouter(view,node2Species,homologyPairSet);
		layouter.doLayout();
		view.getCanvas().paintImmediately();
		
		// This array holds the min & max x positions for the species
		double [] min_x = new double[k];		
		double [] max_x = new double[k];
		for( int i = 0; i < k; ++i ) {
			min_x[i] = 1e70;
			max_x[i] = 0.0;
		}
		Iterator nodeViewIt = view.getNodeViewsIterator();
		while ( nodeViewIt.hasNext()) {
			NodeView nodeView = (NodeView)nodeViewIt.next();
			int species = node2Species.get(nodeView.getNode()).intValue();
			min_x[species] = Math.min(min_x[species],nodeView.getXPosition());
			max_x[species] = Math.max(max_x[species],nodeView.getXPosition());
		} 

		// Calculate the offset for each of species:
		double [] offset = new double[k];
		offset[0] = 0;
		for ( int idx = 1;idx<k;idx++) {
			offset[idx] = offset[idx-1] + GAP + max_x[idx-1] - min_x[idx];
			System.out.println( "DBG: Shifting sub-clusters: " + GAP +
								" " + max_x[idx-1] +
								" " + min_x[idx] + 
								"= " + offset[idx] );
		}
		
		// Shift nodes proportionally to their species number
		nodeViewIt = view.getNodeViewsIterator();
		while (nodeViewIt.hasNext()) {
			NodeView nodeView = (NodeView)nodeViewIt.next();
			int species = node2Species.get(nodeView.getNode()).intValue();
			nodeView.setXPosition(nodeView.getXPosition()+offset[species]);
		} 
		
		// Make sure all the nodes have their position updated
		nodeViewIt = view.getNodeViewsIterator();
		while(nodeViewIt.hasNext()) {
			((NodeView)nodeViewIt.next()).setNodePosition(true);
		}
		view.getCanvas().paintImmediately();
    }

    /**
     * Separates the network nodes into their constituent parts and creates the
     * homology edges between them.
     * @param splitNetwork The network to be separated.
     */
    public void splitNetwork(CyNetwork splitNetwork) {
		// Find out how many species we are tyring to align
		Iterator nodeIt = sifNetwork.nodesIterator();
		if (nodeIt == null || !nodeIt.hasNext() ) {
			throw new IllegalArgumentException("No nodes in this graph");
		}
		CyNode firstNode = (CyNode)nodeIt.next();
		String firstName = getCNodeName( firstNode );
		//System.out.println("got firstname " + firstName);
		String [] splat = firstName.split("\\|");
		k = splat.length;
		//System.out.println("splat len " + k);
		if ( k < 1 || k > 2 ) {
			throw new IllegalArgumentException(
					  "ERROR: wrong input graph, 1 or 2 species supported" );
		}

		Vector<HashMap<String,CyNode>> name2Node_Vector =
			new Vector<HashMap<String,CyNode>>();
		for ( int idx = 0; idx < k; idx++ ) {
			name2Node_Vector.add( new HashMap<String,CyNode>() );
		}

		Iterator compatNodeIt = sifNetwork.nodesIterator();
		homologyPairSet = new NodePairSet();
		// This maps from a node to the species that node belongs to
		node2Species = new HashMap<CyNode,Integer>();
		while(compatNodeIt.hasNext()) {
			CyNode current = (CyNode)compatNodeIt.next();
			String name = getCNodeName( current );
			String [] names = name.split(SPLIT_STRING);
			if (names.length != k) {
				//awww, shit
				throw new IllegalArgumentException("Incorrect value of k");
			} 
			//System.out.println("split string " + names[0] + " " + names[1]);
			
			Vector<CyNode> nodes = new Vector<CyNode>(k);
			for (int idx = 0; idx < k ; idx++) {
				HashMap<String,CyNode> name2Node = name2Node_Vector.get(idx);
				CyNode idxNode = name2Node.get(names[idx]);
				if (idxNode == null) {
					idxNode = splitNetwork.addNode(Cytoscape.getCyNode(names[idx],true));
					if (idxNode == null) {
						idxNode = Cytoscape.getCyNode(names[idx]);
					}
					name2Node.put(names[idx],idxNode);
					//System.out.println("node name : " + names[idx] + " species: " + idx);
					node2Species.put(idxNode,new Integer(idx));
					nodeAttrs.setAttribute(idxNode.getIdentifier(),"species",Integer.toString(idx));
				}
				nodes.add(idxNode);
				splitNetwork.addNode(idxNode);
			}
			
			for (int idx = 0;idx<k ;idx++) {
				for (int idy = idx+1;idy<k;idy++) {
					homologyPairSet.add(nodes.get(idx),nodes.get(idy));
				}
			}
		}
		
		//System.out.println("got here 1");
		
		//for each edge in the compatability graph, split it into two edges
		//and add each of these edges to the new root graph 
		//Iterator compatEdgeIt = sifNetwork.edgesList().iterator();
		Iterator compatEdgeIt = sifNetwork.edgesIterator();
		CyAttributes compatEdgeAttributes = Cytoscape.getEdgeAttributes();
		while(compatEdgeIt.hasNext()) {
			//System.out.println("handling edge " + k);
			//Figure out the names of the four end points for the two edges:
			CyEdge current = (CyEdge)compatEdgeIt.next();
			String [] sourceSplat = 
				nodeAttrs.getStringAttribute(current.getSource().getIdentifier(),"name").split(SPLIT_STRING);
			String [] targetSplat = 
				nodeAttrs.getStringAttribute(current.getTarget().getIdentifier(),"name").split(SPLIT_STRING);


			//Build a vector of interaction types which stores the equivalent 
			// interaction for each of the species:
			String compatInteraction = edgeAttrs.getStringAttribute( current.getIdentifier(),"name" );
			Vector<String> interactionTypes = new Vector<String>(k);
			if(compatInteraction.length() == k) {
				for (int idx = 0; idx < k; idx++) {
					interactionTypes.add(compatInteraction.substring(idx,idx+1)+idx);
				} 
			} else {
				System.out.println("WARNING!!: Illegal edge description, using defalt\n" );
				for (int idx = 0; idx < k; idx++) {
					interactionTypes.add("?"+idx);
				} 
			}
			//System.out.println("Edge '" + compatInteraction + "' -> " + interactionTypes );

			if( k > 1 ) {
				// Actually, this only works for k == 2
				// Add homology edges
				//System.out.println( "DBG: Adding hom edge " + sourceSplat[0] + " " + sourceSplat[1] );
				splitNetwork.addEdge( Cytoscape.getCyEdge(sourceSplat[0],sourceSplat[0] + " (hm) " + sourceSplat[1],
														  sourceSplat[1],"hm") );
				splitNetwork.addEdge( Cytoscape.getCyEdge(targetSplat[0],targetSplat[0] + " (hm) " + targetSplat[1],
														  targetSplat[1],"hm") );
			}

			// Add in-specie weighted edges
			for ( int idx = 0; idx < k; idx++ ) {
				//System.out.println("index " + idx);
				//creat the new nedge
				//don't make self edges
				//need to make a check here to see iff this edge
				//has already been added into the graph
				if (!sourceSplat[idx].equals(targetSplat[idx])) {
					String idxName = sourceSplat[idx]+" ("+interactionTypes.get(idx)+") "+targetSplat[idx];
					HashMap name2Node = (HashMap)name2Node_Vector.get(idx);
					CyNode sourceNode = (CyNode)name2Node.get(sourceSplat[idx]);
					CyNode targetNode = (CyNode)name2Node.get(targetSplat[idx]);
					//if (!splitNetwork.isNeighbor(sourceNode,targetNode)) {
					int[] possibleEdge = {sourceNode.getRootGraphIndex(),targetNode.getRootGraphIndex()};
					int[] neighborEdges =  splitNetwork.getConnectingEdgeIndicesArray(possibleEdge); 
					if (null == neighborEdges || neighborEdges.length == 0) {
						splitNetwork.addEdge(
											 Cytoscape.getCyEdge( sourceSplat[idx],idxName,targetSplat[idx],
																  interactionTypes.get(idx) ) );
						//System.out.println( "DBG: Added spec edge " + idxName );
					}
				} 
			} 
		}

		System.out.println( "Finished splitting a network!" );
    }
}
