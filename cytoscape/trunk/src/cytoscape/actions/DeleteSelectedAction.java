//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.base.*;
import y.view.Graph2D;

import giny.view.*;
import java.util.*;
import giny.model.GraphPerspective;

import cytoscape.data.CyNetwork;
import cytoscape.view.NetworkView;
import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
public class DeleteSelectedAction extends AbstractAction {
    NetworkView networkView;
    
    public DeleteSelectedAction(NetworkView networkView) {
        super("Delete Selected Nodes and Edges");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
      // Y-Files check
      if ( networkView.getCytoscapeObj().getConfiguration().isYFiles() ) {
        String callerID = "DeleteSelectedAction.actionPerformed";
        
        CyNetwork network = networkView.getNetwork();
        network.beginActivity(callerID); //also fires a graph pre-event
        Graph2D graph = network.getGraph();
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
             
        NodeCursor nc = graph.selectedNodes(); 
        EdgeCursor ec = graph.selectedEdges();
        if (nc.size() == 0 && ec.size() == 0) {//nothing to do
            networkView.getNetwork().endActivity(callerID);
            return;
        }

        while (nc.ok()) {
            Node node = nc.node();
            graph.removeNode(node);
            nodeAttributes.removeObjectMapping(node);
            nc.next();
        } // while
        while (ec.ok()) {
            Edge edge = ec.edge();
            graph.removeEdge(edge);
            ec.next();
            edgeAttributes.removeObjectMapping(edge);
        }
        
        networkView.redrawGraph(false, false);
        network.endActivity(callerID);
      } else {
        //GINY
       
        // get the GraphView
        GraphView view = networkView.getView();
        // get the GraphPerspective
        GraphPerspective perspective = view.getGraphPerspective();
        // get the Selected Nodes
        List selected_nodes = view.getSelectedNodes();
        Iterator selected_nodes_iterator = selected_nodes.iterator();
        // get the Indecies of the Selected Nodes
        int[] node_indicies = new int[ selected_nodes.size() ];
        for ( int i = 0; selected_nodes_iterator.hasNext(); i++ ) {
          node_indicies[i] = ( ( NodeView )selected_nodes_iterator.next() ).getGraphPerspectiveIndex();
        }
        // get the Conencting Edge indecies
        int[] edge_indicies =  perspective.getConnectingEdgeIndicesArray( node_indicies );

        // get the corresponding EdgeViews
        List edge_views = new ArrayList( edge_indicies.length );
        for ( int i = 0; i < edge_indicies.length; i++ ) {
          edge_views.add( view.getEdgeView( edge_indicies[i] ) );
        }

        // Hide the viewable things and the perspective refs
        view.hideGraphObjects( selected_nodes );
        view.hideGraphObjects( edge_views );
        perspective.hideEdges( edge_indicies );
        perspective.hideNodes( node_indicies );
      
      }
    } // actionPerformed
} // inner class DeleteSelectedAction
