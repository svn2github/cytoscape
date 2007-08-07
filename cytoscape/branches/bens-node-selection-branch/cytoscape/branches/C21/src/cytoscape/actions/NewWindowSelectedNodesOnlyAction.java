//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


import cytoscape.data.GraphObjAttributes;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;

public class NewWindowSelectedNodesOnlyAction extends CytoscapeAction {

    public NewWindowSelectedNodesOnlyAction () {
        super("Selected nodes, All edges");
        setPreferredMenu( "Select.To New Window" );
        setAcceleratorCombo(java.awt.event. KeyEvent.VK_W, ActionEvent.SHIFT_MASK|ActionEvent.CTRL_MASK );
    }

    public void actionPerformed(ActionEvent e) {
        //save the vizmapper catalog

      CyNetworkView current_network_view = Cytoscape.getCurrentNetworkView();
      CyNetwork current_network = current_network_view.getNetwork();

      int [] nodes = current_network_view.getSelectedNodeIndices();
      
      CyNetwork new_network = Cytoscape.createNetwork( nodes, current_network.getConnectingEdgeIndicesArray( nodes ) , current_network.getTitle()+"->child", current_network );
      new_network.setExpressionData( current_network.getExpressionData() );

      //String title = " selection";
      //Cytoscape.createNetworkView( new_network, title );
     
    }
}

