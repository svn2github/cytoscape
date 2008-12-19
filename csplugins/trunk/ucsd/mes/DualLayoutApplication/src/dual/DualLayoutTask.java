package dual;

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
import cytoscape.plugin.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.Semantics;
import cytoscape.view.CyWindow;
import cytoscape.view.GraphViewController;
import cytoscape.data.readers.GMLTree;

public class DualLayoutTask extends Thread{
	private static String TITLE1 = "Split Graph";
	private static String SPLIT_STRING = "\\|";
	private double GAP = 200;
	private double OFFSET = 500;
	private CyNetwork sifNetwork;
	HashMap node2Species;
	NodePairSet homologyPairSet;
	int k;
	private String title;

	/**
	 * @param sifNetwork a subgraph of the compatability graph that is a conserved complex
	 */
	public DualLayoutTask(CyNetwork sifNetwork) {
		this.sifNetwork = sifNetwork;
	}

	public void layoutNetwork(PGraphView view) {
		((PGraphView)view).getCanvas().paintImmediately();
		SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter(view,node2Species,homologyPairSet);
		layouter.doLayout();
		((PGraphView)view).getCanvas().paintImmediately();
		
		//this array holds the min x position for each species
		double [] min_x = new double[k];		
		//this array holds the max x position for each species
		double [] max_x = new double[k];
		{
			Iterator nodeViewIt = view.getNodeViewsIterator();
			while ( nodeViewIt.hasNext()) {
				NodeView nodeView = (NodeView)nodeViewIt.next();
				int species = ((Integer)node2Species.get(nodeView.getNode())).intValue();
				min_x[species] = Math.min(min_x[species],nodeView.getXPosition());
				max_x[species] = Math.max(max_x[species],nodeView.getXPosition());
			} 
		}
		//hold the offset for each species
		double [] offset = new double[k];
		offset[0] = 0;
		for ( int idx = 1;idx<k;idx++) {
			offset[idx] = offset[idx-1]+GAP+max_x[idx-1]-min_x[idx-1];
		} 
		

		{
			//move all the nodes over an amount proportional to their species number
			Iterator nodeViewIt = view.getNodeViewsIterator();
			while (nodeViewIt.hasNext()) {
				NodeView nodeView = (NodeView)nodeViewIt.next();
				int species = ((Integer)node2Species.get(nodeView.getNode())).intValue();
				System.out.println("species int: " + species);
				nodeView.setXPosition(nodeView.getXPosition()+offset[species]);
			} 
		}
		
		//make sure all the nodes have their position updated
		{
			Iterator nodeViewIt = view.getNodeViewsIterator();
			while(nodeViewIt.hasNext()) {
				((NodeView)nodeViewIt.next()).setNodePosition(true);
			}
		}
		((PGraphView)view).getCanvas().paintImmediately();
	}
	
	public void splitNetwork(CyNetwork splitNetwork) {
		//try to guess how many species we are tyring to align
		k = 0;
		{
			List nodes = sifNetwork.nodesList();
			if (nodes == null || nodes.size() <= 0) {
				throw new IllegalArgumentException("No nodes in this graph");
			} 
			CyNode firstNode = (CyNode)nodes.get(0);
			String firstName = sifNetwork.getNodeAttributes().getCanonicalName(firstNode);
			String [] splat = firstName.split("\\|");
			k = splat.length;
			if (k <= 1) {
				throw new IllegalArgumentException("Must align at least 2 species");
			} 
		} 
		
		Vector name2Node_Vector = new Vector();
		for (int	idx= 0;	idx<k ; idx++) {
			name2Node_Vector.add(new HashMap());
		} 
	 
		GraphObjAttributes nodeAttributes = sifNetwork.getNodeAttributes();
		Iterator compatNodeIt = sifNetwork.nodesList().iterator();
		homologyPairSet = new NodePairSet();
		//this maps from a node to the species that node belongs
		//to
		node2Species = new HashMap();
		while(compatNodeIt.hasNext()) {
			CyNode current = (CyNode)compatNodeIt.next();
			//String name = current.getIdentifier();
			String name = nodeAttributes.getCanonicalName(current);
			String [] names = name.split(SPLIT_STRING);
			System.out.println("names: " + names[0] + "  " + names[1]);
			if (names.length != k) {
				//awww, shit
				throw new IllegalArgumentException("Incorrect value of k");
			} 
			
			Vector nodes = new Vector(k);
			for (int idx = 0; idx < k ; idx++) {
				HashMap name2Node = (HashMap)name2Node_Vector.get(idx);
				CyNode idxNode = (CyNode)name2Node.get(names[idx]);
				if (idxNode == null) {
					idxNode = splitNetwork.addNode(Cytoscape.getCyNode(names[idx],true));
					if (idxNode == null) {
									idxNode = Cytoscape.getCyNode(names[idx]);
					}
					name2Node.put(names[idx],idxNode);
					node2Species.put(idxNode,new Integer(idx));
					System.out.println("node name: " + names[idx] + " species " + idx);
				} 
				nodes.add(idxNode);
				splitNetwork.addNode(idxNode);
			} 
			
			for (int idx = 0;idx<k ;idx++) {
				for (int idy = idx+1;idy<k;idy++) {
					homologyPairSet.add((CyNode)nodes.get(idx),(CyNode)nodes.get(idy));
				} 
			} 
		}

		
		//for each edge in the compatability graph, split it into two edges
		//and add each of these edges to the new root graph 
		Iterator compatEdgeIt = sifNetwork.edgesList().iterator();
		GraphObjAttributes compatEdgeAttributes = sifNetwork.getEdgeAttributes();
		while(compatEdgeIt.hasNext()) {
			CyEdge current = (CyEdge)compatEdgeIt.next();
			//figure out the names of the four end points for the two edges
			String [] sourceSplat = nodeAttributes.getCanonicalName(current.getSource()).split(SPLIT_STRING);
			String [] targetSplat = nodeAttributes.getCanonicalName(current.getTarget()).split(SPLIT_STRING);


			String compatInteraction = (String)compatEdgeAttributes.get("interaction",compatEdgeAttributes.getCanonicalName(current));
			System.out.println("compatInter: " + compatInteraction);
			//this is a vector of interaction types which stores the equivalent interaction for each of the species
			Vector interactionTypes = new Vector(k);
			if(compatInteraction.length() == k) {
				for (int idx = 0; idx < k; idx++) {
					System.out.println("subst: " + compatInteraction.substring(idx,idx+1) + " idx " + idx);
					interactionTypes.add(compatInteraction.substring(idx,idx+1)+idx);
				} 
			} else {
				for (int idx = 0; idx < k; k++) {
					interactionTypes.add("?"+idx);
				} 
			}

			for ( int q = 0; q < interactionTypes.size(); q++ )  
				System.out.println("intType q: " + q + " " +interactionTypes.get(q));

			// add homology edges
			splitNetwork.addEdge(Cytoscape.getCyEdge(sourceSplat[0],sourceSplat[0]+ " (hm) " + sourceSplat[1], sourceSplat[1],"hm")); 
			splitNetwork.addEdge(Cytoscape.getCyEdge(targetSplat[0],targetSplat[0]+ " (hm) " + targetSplat[1], targetSplat[1],"hm")); 

			for (int idx = 0;idx	< k; idx++) {
				//creat the new nedge
				//don't make self edges
				//need to make a check here to see iff this edge
				//has already been added into the graph
				if (!sourceSplat[idx].equals(targetSplat[idx])) {
					String idxName = sourceSplat[idx]+" ("+interactionTypes.get(idx)+") "+targetSplat[idx];
					//if (!newEdgeAttributes.getObjectMap().keySet().contains(idxName)) {
					HashMap name2Node = (HashMap)name2Node_Vector.get(idx);
					CyNode sourceNode = (CyNode)name2Node.get(sourceSplat[idx]);
					CyNode targetNode = (CyNode)name2Node.get(targetSplat[idx]);
					if (!splitNetwork.isNeighbor(sourceNode,targetNode)) {
						splitNetwork.addEdge(Cytoscape.getCyEdge(sourceSplat[idx],idxName,targetSplat[idx],(String)interactionTypes.get(idx)));
					} 
				} 
			} 
		}
	}
}
