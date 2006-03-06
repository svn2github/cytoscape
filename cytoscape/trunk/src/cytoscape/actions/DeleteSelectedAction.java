
/*
  File: DeleteSelectedAction.java 
  
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

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.*;

import giny.model.GraphPerspective;
import giny.view.GraphView;

import cytoscape.CyNetwork;
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
/**
 * Giny version of the original class. Note that the original version was
 * only available when editing mode was enabled, and caused the selected
 * nodes to be permanently removed from the graph (and, necessarily, the view).
 * This version hides the selected nodes from both the graph and the view,
 * as there are currently no methods to remove a node view from the graph view
 * in Giny. The semantics of this and related classes for modifying the
 * graph and view should be clarified.
 */
public class DeleteSelectedAction extends AbstractAction {
    NetworkView networkView;
    
    public DeleteSelectedAction(NetworkView networkView) {
        super("Delete Selected Nodes and Edges");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "DeleteSelectedAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        
        GraphView view = networkView.getView();
        GraphPerspective perspective = view.getGraphPerspective();
        // get the Selected node and edge indices
        int[] node_indicies = view.getSelectedNodeIndices();
        int[] edge_indicies = view.getSelectedEdgeIndices();
        //and the node/edge vew objects
        List selected_nodeViews = view.getSelectedNodes();
        List selected_edgeViews = view.getSelectedEdges();

        // Hide the viewable things and the perspective refs
        view.hideGraphObjects( selected_nodeViews );
        view.hideGraphObjects( selected_edgeViews );
        perspective.hideEdges( edge_indicies );
        perspective.hideNodes( node_indicies );
        
        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
    } // actionPerformed
} // inner class DeleteSelectedAction
