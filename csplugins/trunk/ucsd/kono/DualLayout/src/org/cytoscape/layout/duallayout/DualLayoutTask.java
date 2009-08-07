package org.cytoscape.layout.duallayout;
import giny.view.NodeView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;

public class DualLayoutTask extends Thread {
	
	// Delimiter
	private static final String SPLIT_STRING = "\\|";
	
	private double GAP = 200;
	private double OFFSET = 500;
	
	private CyNetwork sifNetwork;
	private Map node2Species;
	NodePairSet homologyPairSet;
	int k;
	private String title;
	private DualLayoutCommandLineParser parser;
	/**
	 * @param sifNetwork
	 *            a subgraph of the compatability graph that is a conserved
	 *            complex
	 */
	public DualLayoutTask(CyNetwork sifNetwork,DualLayoutCommandLineParser parser) {
		this.sifNetwork = sifNetwork;
		this.parser = parser;
	}

	public void layoutNetwork(CyNetworkView view) {
//	((PGraphView) view).getCanvas().paintImmediately();
		SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter(view,
				node2Species, homologyPairSet,parser.isRandom());
		layouter.doLayout();
	//	((PGraphView) view).getCanvas().paintImmediately();

		// this array holds the min x position for each species
		double[] min_x = new double[k];
		// this array holds the max x position for each species
		double[] max_x = new double[k];
		{
			Iterator nodeViewIt = view.getNodeViewsIterator();
			while (nodeViewIt.hasNext()) {
				NodeView nodeView = (NodeView) nodeViewIt.next();
				int species = ((Integer) node2Species.get(nodeView.getNode()))
						.intValue();
				min_x[species] = Math.min(min_x[species], nodeView
						.getXPosition());
				max_x[species] = Math.max(max_x[species], nodeView
						.getXPosition());
			} // end of while ()
		}
		// hold the offset for each species
		double[] offset = new double[k];
		offset[0] = 0;
		for (int idx = 1; idx < k; idx++) {
			offset[idx] = offset[idx - 1] + GAP + max_x[idx - 1]
					- min_x[idx - 1];
		} // end of for ()

		{
			// move all the nodes over an amount proportional to their species
			// number
			Iterator nodeViewIt = view.getNodeViewsIterator();
			while (nodeViewIt.hasNext()) {
				NodeView nodeView = (NodeView) nodeViewIt.next();
				int species = ((Integer) node2Species.get(nodeView.getNode()))
						.intValue();
				nodeView
						.setXPosition(nodeView.getXPosition() + offset[species]);
			} // end of while ()
		}

		if (parser.normalizeY()) {
			Map outerMap = homologyPairSet.getOuterMap();
			Collection leftNodes = outerMap.keySet();
			for (Iterator lit = leftNodes.iterator(); lit.hasNext();) {
				CyNode lnode = (CyNode)lit.next();
				NodeView lnodeView = view.getNodeView(lnode);  
				double ly = lnodeView.getYPosition();
				Collection rightNodes = (Collection)outerMap.get(lnode);
				for (Iterator rit = rightNodes.iterator(); rit.hasNext();) {
					CyNode rnode = (CyNode)rit.next();
					NodeView rnodeView = view.getNodeView(rnode);  
					double ry = rnodeView.getYPosition();

					lnodeView.setYPosition( (ly + ry) / 2 );
					rnodeView.setYPosition( (ly + ry) / 2 );
				}
			}
		}

				

		// make sure all the nodes have their position updated
		{
			Iterator nodeViewIt = view.getNodeViewsIterator();
			while (nodeViewIt.hasNext()) {
				((NodeView) nodeViewIt.next()).setNodePosition(true);
			}
		}



		//((PGraphView) view).getCanvas().paintImmediately();
	}

	public void addHomologyEdges(CyNetwork splitNetwork) {
		HashMap outerMap = homologyPairSet.getOuterMap();
		for (Iterator outerSetIt = outerMap.keySet().iterator(); outerSetIt
				.hasNext();) {
			CyNode outerNode = (CyNode) outerSetIt.next();
			for (Iterator innerSetIt = ((Set) outerMap.get(outerNode))
					.iterator(); innerSetIt.hasNext();) {
				CyNode innerNode = (CyNode) innerSetIt.next();
				splitNetwork.addEdge(Cytoscape.getCyEdge(outerNode, innerNode,
						Semantics.INTERACTION, "hm", true));
			}
		}
	}

	public void splitNetwork(CyNetwork splitNetwork) {
		// tryy to guess how many species we are tyring to align
		k = 0;
		{
			// List nodes = sifNetwork.nodesList();
			if (sifNetwork == null || sifNetwork.getNodeCount() <= 0) {
				throw new IllegalArgumentException("No nodes in this graph");
			} // end of if ()
			CyNode firstNode = (CyNode) sifNetwork.nodesIterator().next();
			String firstName = firstNode.getIdentifier();
			//String firstName = sifNetwork.getNodeAttributes().getCanonicalName(firstNode);
			String[] splat = firstName.split("\\|");
			k = splat.length;
			if (k <= 1) {
				throw new IllegalArgumentException(
						"Must align at least 2 species");
			} // end of if ()
		}

		Vector name2Node_Vector = new Vector();
		for (int idx = 0; idx < k; idx++) {
			name2Node_Vector.add(new HashMap());
		} // end of for (int = 0; < ; ++)

		//GraphObjAttributes nodeAttributes = sifNetwork.getNodeAttributes();
		//CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Iterator compatNodeIt = sifNetwork.nodesIterator();
		homologyPairSet = new NodePairSet();
		// this maps from a node to the species that node belongs
		// to
		node2Species = new HashMap();
		while (compatNodeIt.hasNext()) {
			CyNode current = (CyNode) compatNodeIt.next();
			String name = current.getIdentifier();
			//String name = nodeAttributes.getCanonicalName(current);
			String[] names = name.split(SPLIT_STRING);
			if (names.length != k) {
				// awww, shit
				throw new IllegalArgumentException("Incorrect value of k");
			} // end of if ()

			Vector nodes = new Vector(k);
			for (int idx = 0; idx < k; idx++) {
				HashMap name2Node = (HashMap) name2Node_Vector.get(idx);
				CyNode idxNode = (CyNode) name2Node.get(names[idx]);
				if (idxNode == null) {
					idxNode = splitNetwork.addNode(Cytoscape.getCyNode(
							names[idx], true));
					name2Node.put(names[idx], idxNode);
					node2Species.put(idxNode, new Integer(idx));
				} // end of if ()
				nodes.add(idxNode);
				splitNetwork.addNode(idxNode);
			} // end of for (int = 0; < ; ++)

			for (int idx = 0; idx < k; idx++) {
				for (int idy = idx + 1; idy < k; idy++) {
					homologyPairSet.add((CyNode) nodes.get(idx), (CyNode) nodes
							.get(idy));
				} // end of for (int = 0; < ; ++)
			} // end of for (int = 0; < ; ++)
		}

		// for each edge in the compatability graph, split it into two edges
		// and add each of these edges to the new root graph
		Iterator compatEdgeIt = sifNetwork.edgesIterator();
		CyAttributes compatEdgeAttributes = Cytoscape.getEdgeAttributes();
		while (compatEdgeIt.hasNext()) {
			CyEdge current = (CyEdge) compatEdgeIt.next();
			// figure out the names of the four end points for the two edges
			String[] sourceSplat = current.getSource().getIdentifier().split(SPLIT_STRING);
			String[] targetSplat = current.getTarget().getIdentifier().split(SPLIT_STRING);

			String compatInteraction = compatEdgeAttributes.getStringAttribute(current.getIdentifier(),Semantics.INTERACTION);
			// this is a vector of interaction types which stores the equivalent
			// interaction for each of the species
			Vector interactionTypes = new Vector(k);
			if (compatInteraction.length() == k) {
				for (int idx = 0; idx < k; idx++) {
					interactionTypes.add(compatInteraction.substring(idx,
							idx + 1)
							+ idx);
				} // end of for (int = 0; < ; ++)
			} else {
				for (int idx = 0; idx < k; k++) {
					interactionTypes.add("?" + idx);
				} // end of for (int = 0; < ; ++)

			}

			for (int idx = 0; idx < k; idx++) {
				// creat the new nedge
				// don't make self edges
				// need to make a check here to see iff this edge
				// has already been added into the graph
				if (!sourceSplat[idx].equals(targetSplat[idx])) {
					String idxName = sourceSplat[idx] + " ("
							+ interactionTypes.get(idx) + ") "
							+ targetSplat[idx];
					// if
					// (!newEdgeAttributes.getObjectMap().keySet().contains(idxName))
					// {
					HashMap name2Node = (HashMap) name2Node_Vector.get(idx);
					CyNode sourceNode = (CyNode) name2Node
							.get(sourceSplat[idx]);
					CyNode targetNode = (CyNode) name2Node
							.get(targetSplat[idx]);
					if (!splitNetwork.isNeighbor(sourceNode, targetNode)) {
						splitNetwork.addEdge(Cytoscape.getCyEdge(
								sourceSplat[idx], idxName, targetSplat[idx],
								(String) interactionTypes.get(idx)));
					} // end of if ()
				} // end of if ()
			} // end of for (int this = 0; < ; ++)
		}
	}
}
