//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.data.GraphObjAttributes;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;

//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends CytoscapeAction {

    public NewWindowSelectedNodesEdgesAction () {
        super("Selected nodes, Selected edges");
        setPreferredMenu( "Select.To New Window" );
    }

    public void actionPerformed(ActionEvent e) {
        //save the vizmapper catalog

      CyNetworkView current_network_view = Cytoscape.getCurrentNetworkView();
      CyNetwork current_network = current_network_view.getNetwork();

      int[] nodes = current_network_view.getSelectedNodeIndices();
      int[] edges = current_network_view.getSelectedEdgeIndices();

      //CyNetwork new_network = Cytoscape.createNetwork( nodes, edges );
      CyNetwork new_network = Cytoscape.createNetwork( nodes, edges , current_network.getTitle()+"->child", current_network );
      new_network.setExpressionData( current_network.getExpressionData() );

      String title = " selection";
      Cytoscape.createNetworkView( new_network, title );
     
    }
}

