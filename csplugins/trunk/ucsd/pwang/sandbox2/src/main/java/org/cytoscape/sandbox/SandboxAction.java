package org.cytoscape.sandbox;

import cytoscape.CyNode;
import cytoscape.CyEdge;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import giny.model.Node;

import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.visual.VisualStyle;

/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class SandboxAction extends CytoscapeAction {
		
		public SandboxAction() {
			// Give your action a name here
			super("Sandbox");

			// Set the menu you'd like here.  Plugins don't need
			// to live in the Plugins menu, so choose whatever
			// is appropriate!
	        setPreferredMenu("Plugins");
		}
		
		public void actionPerformed(ActionEvent e) {

			System.out.println( "\nMenuItem sandbox is clicked!\n ");
			
			CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();			
			CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();			

			CyNetwork network = Cytoscape.getCurrentNetwork();
			
			HashSet<Node> selectedNodeSet = new HashSet<Node>();
									
			Iterator<CyNode> it = network.nodesIterator();			
			while (it.hasNext()){
				CyNode node = it.next();

				String name = nodeAttrs.getAttribute(node.getIdentifier(), "Name").toString();
				
				// start of a case
				if (name.equalsIgnoreCase("Diabetes Type 2")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=10){ //13/27
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case

				// start of a case
				if (name.equalsIgnoreCase("Obesity")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=6){ //45/65
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case

				// start of a case
				if (name.equalsIgnoreCase("Hypertension")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=5){//20/37
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case
				
				// start of a case
				if (name.equalsIgnoreCase("Coronary Heart Disease")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=8){//17/52
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case
				
				// start of a case
				if (name.equalsIgnoreCase("Alzheimer's disease")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=5){//15/21
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case
				
				// start of a case
				if (name.equalsIgnoreCase("Dyslipidemia")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=4){//16/21
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case

				// start of a case
				if (name.equalsIgnoreCase("Heart Attack")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=4){//18/24
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case
				// start of a case
				if (name.equalsIgnoreCase("Stroke")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=2){ //7/10
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case

				
				//
				// start of a case
				if (name.equalsIgnoreCase("Metabolic syndrome x") ){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=4) { // 4/7
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // end of a case
				
				
				// start of a case
				if (name.equalsIgnoreCase("Osteoporosis")){
					// get the edges for this disease node					
					List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
					
					//System.out.println( "\tedgelist.size() = "+ edgeList.size());
					// compare edge attribute -- asssociationCount and make selection
					Iterator<CyEdge> edgeIt = edgeList.iterator();
					while (edgeIt.hasNext()){
						CyEdge edge = edgeIt.next();						
						int assCount  = edgeAttrs.getIntegerAttribute(edge.getIdentifier(), "AssociationCount");						
						if (assCount >=3){ //7/10
							selectedNodeSet.add(edge.getSource());
							selectedNodeSet.add(edge.getTarget());							
						}
					}					
				} // End of a case

			}

			network.setSelectedNodeState(selectedNodeSet, true);
			Cytoscape.getNetworkView(network.getIdentifier()).updateView();
		}
}
