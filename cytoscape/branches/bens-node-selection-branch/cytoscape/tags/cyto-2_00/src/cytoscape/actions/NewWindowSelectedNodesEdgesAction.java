//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;

//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends CytoscapeAction {

    public NewWindowSelectedNodesEdgesAction () {
        super("Selected nodes, Selected edges");
        setPreferredMenu( "Select.To New Network" );
        setAcceleratorCombo(  java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK  );
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

