//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
//-------------------------------------------------------------------------
public class DeSelectAllNodesAction extends CytoscapeAction  {

    public DeSelectAllNodesAction () {
        super ("Deselect all nodes");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
      //GinyUtils.deselectAllNodes( Cytoscape.getCurrentNetworkView() );
      Cytoscape.getCurrentNetwork().unFlagAllNodes();
    }
}

