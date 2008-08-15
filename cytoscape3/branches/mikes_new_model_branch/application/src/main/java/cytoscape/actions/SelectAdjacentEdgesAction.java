/*
 File: SelectAdjacentEdgesAction.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

//-------------------------------------------------------------------------
// $Revision: 11118 $
// $Date: 2007-07-24 14:42:07 -0700 (Tue, 24 Jul 2007) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.Cytoscape;

import org.cytoscape.GraphPerspective;

import org.cytoscape.Node;

import org.cytoscape.Edge;

import cytoscape.util.CytoscapeAction;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import java.util.Set;

import java.util.HashMap;

import javax.swing.event.MenuEvent;

//-------------------------------------------------------------------------
/**
 *
 */
public class SelectAdjacentEdgesAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870522966L;
	/**
	 * Creates a new SelectAdjacentEdgesAction object.
	 */
	public SelectAdjacentEdgesAction() {
		super("Select adjacent edges");
		setPreferredMenu("Select.Edges");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_E, ActionEvent.ALT_MASK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// GinyUtils.selectAllEdges( Cytoscape.getCurrentNetworkView() );
		GraphPerspective network = Cytoscape.getCurrentNetwork();
		HashMap<Edge,Edge> edgeMap = new HashMap<Edge,Edge>();

		// Get the list of selected nodes
		for (Node node: (Set<Node>)network.getSelectedNodes()) {
			// Get the list of edges connected to this node
			int[] edgeIndices = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true);
			// For each node, select the appropriate edges
			if (edgeIndices == null)
				continue;

			for (int i = 0; i < edgeIndices.length; i++)  {
				Edge edge = (Edge)network.getEdge(edgeIndices[i]);
				edgeMap.put(edge,edge);
			}
		}
		network.setSelectedEdgeState(edgeMap.keySet(), true);

		if (Cytoscape.getCurrentNetworkView() != null) {
			Cytoscape.getCurrentNetworkView().updateView();
		}
	} // action performed

    public void menuSelected(MenuEvent e) {
        enableForNetwork();
    }
}
