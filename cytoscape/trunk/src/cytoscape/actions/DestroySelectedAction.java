
/*
  File: DestroySelectedAction.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

// -------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;

import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

//-------------------------------------------------------------------------
public class DestroySelectedAction extends CytoscapeAction {

	public DestroySelectedAction() {
		// AJK: 10/24/05 change name to "Delete" rather than "Destroy"
//		super("Destroy Selected Nodes/Edges");
		super("Delete Selected Nodes/Edges");
		setPreferredMenu("Edit");
	}

	public DestroySelectedAction(boolean label) {
		super();
	}

	   public void actionPerformed(ActionEvent e) {
	        String callerID = "DeleteSelectedAction.actionPerformed";
	        final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
	        networkView.getNetwork().beginActivity(callerID);

			java.util.List edgeViews = networkView.getSelectedEdges();
			java.util.List nodeViews = networkView.getSelectedNodes();
			final CyNetwork cyNet = networkView.getNetwork();

			final int[] nodes = new int[nodeViews.size() + 1];
			int[] allEdges = new int[0];
			
			// cache the coordinate positions so that they can be restored upon a redo
			final HashMap coords = new HashMap();
			final Node[] cyNodes = new Node[nodeViews.size()];

			// first collect the selected nodes and their adjacent edges
			for (int i = 0; i < nodeViews.size(); i++) {
				NodeView nview = (NodeView) nodeViews.get(i);
				CyNode cyNode = (CyNode) nview.getNode();
				
				coords.put(cyNode.getIdentifier(), nview.getOffset());
				cyNodes[i] = cyNode;
				
				int nodeIdx = cyNode.getRootGraphIndex();
				nodes[i] = nodeIdx;
				int[] edgesList = cyNet.getAdjacentEdgeIndicesArray(nodeIdx, true,
						true, true);
				int[] bigEdges = new int[allEdges.length + edgesList.length];
				for (int m = 0; m < allEdges.length; m++) {
					bigEdges[m] = allEdges[m];
				}
				for (int p = 0; p < edgesList.length; p++) {
					bigEdges[allEdges.length + p] = edgesList[p];
				}
				allEdges = bigEdges;
			}

			// then collect and add the selected edges
			for (int j = 0; j < edgeViews.size(); j++) {
				EdgeView eview = (EdgeView) edgeViews.get(j); // n.b.
				CyEdge cyEdge = (CyEdge) eview.getEdge();
				int edgeIdx = cyEdge.getRootGraphIndex();
				int[] bigEdges = new int[allEdges.length + 1];
				for (int m = 0; m < allEdges.length; m++) {
					bigEdges[m] = allEdges[m];
				}
				bigEdges[allEdges.length] = edgeIdx;
				allEdges = bigEdges;
			}


			// now do the deletions
			final int[] edges = allEdges;

//			cyNet.hideNodes(nodes);
//			cyNet.hideEdges(edges);
			
			// now do the deletions
			
			for (int i = 0; i < edges.length; i++)
			{
				cyNet.removeEdge(edges[i], false);
			}
			
			for (int j = 0; j < nodes.length; j++)
			{
				cyNet.removeNode(nodes[j], false);
			}
			

	        networkView.redrawGraph(false, false);
	        networkView.getNetwork().endActivity(callerID);
	        
	        // AJK: 06/10/06 BEGIN
	        //     make this action undo-able
	        
	        System.out.println ("adding undoableEdit to undoManager: " + 
	        		Cytoscape.getDesktop().undo);
	 
			Cytoscape.getDesktop().undo.addEdit(new AbstractUndoableEdit() {

				final String network_id = cyNet.getIdentifier();

				public String getPresentationName() {
					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
//					return "Delete";
					return  "Remove";
				}

				public String getRedoPresentationName() {
					if (edges.length == 0)
						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Redo: Removed Nodes";
//						return " ";
					else
						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Redo: Removed Nodes and Edges";
//						return " ";
			}

				public String getUndoPresentationName() {

					if (edges.length == 0)
						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Undo: Removed Nodes";
//						return null;
					else
						// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
						return "Undo: Removed Nodes and Edges";
//						return null;
				}

				public void redo() {
					super.redo();
					// removes the removed nodes and edges from the network
					CyNetwork network = Cytoscape.getNetwork(network_id);
					for (int i = 0; i < edges.length; i++)
					{
						network.removeEdge(edges[i], false);
					}
					
					for (int j = 0; j < nodes.length; j++)
					{
						network.removeNode(nodes[j], false);
					}

				}

				public void undo() {
					super.undo();
					CyNetwork network = Cytoscape.getNetwork(network_id);
					if (network != null) {
						network.restoreNodes(nodes);
						network.restoreEdges(edges);
						GinyUtils.unHideAll( cytoscape.Cytoscape.getCurrentNetworkView());

						// restore positions of nodes
						for (int i = 0; i < cyNodes.length; i++)
						{
							Node n = cyNodes[i];
							Point2D pt = (Point2D) coords.get(n.getIdentifier());
					        NodeView nv = 
					        	networkView.getNodeView(n);
					        nv.setOffset(pt.getX(), pt.getY());
						}
					}
				}

			});

			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNet);
	       
	        // AJK: 06/10/06
	    } // actionPerformed
}

